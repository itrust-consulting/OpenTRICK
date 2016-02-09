/**
 * 
 */
package lu.itrust.TS.controller;

import static lu.itrust.TS.controller.TS_02_InstallApplication.ME_CUSTOMER;
import static lu.itrust.TS.helper.TestSharingData.getInteger;
import static lu.itrust.TS.helper.TestSharingData.put;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerAnalysisImport;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * @author eomar
 *
 */
@Test(groups = "EditFieldFailure", dependsOnGroups = "Installation")
public class TS_09_EditFieldFailure extends SpringTestConfiguration {

	private static final String ASSET_FAILURE_ID = "asset-failure-id";

	private static final String ASSET_TEST_FAILURE = "Servers";

	private static final String SCNEARIO_TEST_FAILURE = "A_1 - Partial loss or temporary";

	public static String ANALYSIS_KEY;

	@Value("${app.settings.test.failure.analysis.filename}")
	private String testFileName;

	@Value("${app.settings.test.failure.analysis.identifier}")
	private String identifier;

	@Value("${app.settings.test.failure.analysis.version}")
	private String version;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ResourceLoader resourceLoader;

	@Test(timeOut = 120000)
	public synchronized void importAnalysis() throws Exception {
		Resource resource = resourceLoader.getResource(testFileName);

		isTrue(resource.exists(), "Resource cannot be found");

		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", resource.getInputStream());
		MvcResult mvcResult = this.mockMvc.perform(fileUpload("/Analysis/Import/Execute").file(mockMultipartFile).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
				.param("customerId", getInteger(ME_CUSTOMER).toString())).andExpect(status().isFound()).andExpect(redirectedUrl("/Analysis/Import")).andReturn();
		notNull(mvcResult, "Request should have result");
		assertFalse((String) mvcResult.getFlashMap().get("error"), mvcResult.getFlashMap().containsKey("error"));
		Worker worker = null;

		for (int i = 0; i < 30; i++) {
			List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
			notEmpty(tasks, "No background task found");
			for (String workerId : tasks) {
				Worker worker2 = workersPoolManager.get(workerId);
				if (worker2 != null && worker2.isMatch("class+customer.id", WorkerAnalysisImport.class, getInteger(ME_CUSTOMER))) {
					worker = worker2;
					break;
				}
			}
			if (worker == null)
				wait(1000);
			else
				break;
		}
		notNull(worker, "Import analysis worker cannot be found");
		while (worker.isWorking())
			wait(100);
		serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
		isNull(worker.getError(), "An error occured while import analysis");
		ANALYSIS_KEY = String.format("Analysis-%s-%s", identifier, version);
	}

	@Test(dependsOnMethods = "importAnalysis")
	@Transactional(readOnly = true)
	public void checkImportedAnalysis() {
		Analysis analysis = serviceAnalysis.getByIdentifierAndVersion(identifier, version);
		notNull(analysis, String.format("Analysis (identifier : %s and version: %s) cannot be found", identifier, version));
		put(ANALYSIS_KEY, analysis.getId());
		assertEquals("Bad analysis version", version, analysis.getVersion());
		assertEquals("Bad analysis identifier", identifier, analysis.getIdentifier());
		isTrue(analysis.getActionPlans().isEmpty(), "Action plan should be empty");
		isTrue(analysis.getSummaries().isEmpty(), "Action plan summary should be empty");
		isTrue(analysis.getRiskRegisters().isEmpty(), "Risk register should be empty");
		notNull(getInteger(ANALYSIS_KEY), "Analysis id cannot be found");
	}

	@Test(dependsOnMethods = "checkImportedAnalysis")
	@Transactional(readOnly = true)
	public void loadAsset() {
		Asset asset = serviceAsset.getByNameAndAnlysisId(ASSET_TEST_FAILURE, getInteger(ANALYSIS_KEY));
		notNull(asset, ASSET_TEST_FAILURE + " cannot be found");
		put(ASSET_FAILURE_ID, asset.getId());
	}

	@Test(dependsOnMethods = "checkImportedAnalysis")
	@Transactional(readOnly = true)
	public void loadScenario() {
		Scenario scenario = serviceScenario.getByNameAndAnalysisId(SCNEARIO_TEST_FAILURE, getInteger(ANALYSIS_KEY));
		notNull(scenario, SCNEARIO_TEST_FAILURE + " cannot be found");
		put("scenario-failure-id", scenario.getId());
	}

	@Test(dependsOnMethods = "loadAsset")
	public void editAssetName() throws Exception {
		Integer idAsset = getInteger(ASSET_FAILURE_ID);
		notNull(idAsset, "Asset id cannot be null");
		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": \"%s\"}", idAsset, "name", "String", "")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());
		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %d}", idAsset, "name", "Integer", 1568822)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());
		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": \"%s\"}", idAsset, "name", "String", "Customer documents")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());
	}

	@Test(dependsOnMethods = "loadAsset")
	public void editAssetValue() throws Exception {
		Integer idAsset = getInteger(ASSET_FAILURE_ID);
		notNull(idAsset, "Asset id cannot be null");
		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": \"%s\"}", idAsset, "value", "String", "")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": \"%s\"}", idAsset, "value", "Boolean", true)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %d }", idAsset, "value", "Integer", 85554)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());
	}

	@Test(dependsOnMethods = "loadAsset")
	public void editAssetType() throws Exception {
		Integer idAsset = getInteger(ASSET_FAILURE_ID);
		notNull(idAsset, "Asset id cannot be null");
		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": \"%s\"}", idAsset, "assetType", "String", "")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": \"%s\"}", idAsset, "assetType", "Boolean", true)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %d}", idAsset, "assetType", "Integer", -85554)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());
	}

	@Test(dependsOnMethods = "loadAsset")
	public void editAssetComment() throws Exception {
		Integer idAsset = getInteger(ASSET_FAILURE_ID);
		notNull(idAsset, "Asset id cannot be null");
		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\"}", idAsset, "comment", "String")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": \"%s\"}", idAsset, "comment", "Boolean", true)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %d}", idAsset, "comment", "Integer", -85554)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());
	}

	@Test(dependsOnMethods = "loadAsset")
	public void editAssetHiddenComment() throws Exception {
		Integer idAsset = getInteger(ASSET_FAILURE_ID);
		notNull(idAsset, "Asset id cannot be null");
		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\"}", idAsset, "hiddenComment", "String")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": \"%s\"}", idAsset, "hiddenComment", "Boolean", true)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());

		this.mockMvc
				.perform(post("/Analysis/EditField/Asset/" + idAsset).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
						.sessionAttr(Constant.SELECTED_ANALYSIS, getInteger(ANALYSIS_KEY)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %d}", idAsset, "hiddenComment", "Integer", -85554)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.error").exists());
	}

}
