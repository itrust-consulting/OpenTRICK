/**
 * 
 */
package lu.itrust.TS.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.notNull;

import java.io.UnsupportedEncodingException;
import java.util.List;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.scenario.Scenario;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author eomar
 *
 */

@Test(groups="CreateAnalysis", dependsOnGroups="Installation")
public class TS_03_CreateAnAnlysis extends SpringTestConfiguration {

	private static final String SIMPLE_ANALYSIS_VERSION = "0.0.1";

	private static final String SIMPLE_ANALYSIS_NAME = "simple-analysis";

	private static String SCENARIO_ASSET_TYPE_VALUE = "";

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceAssetType serviceAssetType;

	protected static String TASK_ID = null;

	protected static int LANGUAGE_ID = -1;

	protected static int CUSTOMER_ID = -1;

	protected static int ANALYSIS_ID = -1;

	@Test
	@Transactional(readOnly = true)
	public void test_00_loadData() throws Exception {
		Language language = serviceLanguage.getByAlpha3("FRA");
		notNull(language, "French language cannot be found");
		LANGUAGE_ID = language.getId();
		Customer customer = serviceCustomer.getFromContactPerson("me");
		notNull(customer, "'me' customer cannot be found");
		CUSTOMER_ID = customer.getId();
	}

	@Test
	public void test_01_CreateSimpleAnalysis() throws Exception {
		this.mockMvc
				.perform(
						post("/Analysis/Build/Save").with(csrf()).with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8).param("author", "Admin Admin")
								.param("name", SIMPLE_ANALYSIS_NAME).param("version", SIMPLE_ANALYSIS_VERSION).param("comment", "comment")
								.param("customer", String.valueOf(CUSTOMER_ID)).param("language", String.valueOf(LANGUAGE_ID))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").exists());

	}

	@Test
	@Transactional(readOnly = true)
	public void test_02_SelectAnalysis() throws Exception {
		Analysis analysis = serviceAnalysis.getByCustomerAndLabelAndVersion(CUSTOMER_ID, SIMPLE_ANALYSIS_NAME, SIMPLE_ANALYSIS_VERSION);
		notNull(analysis, "Analysis cannot be found");
		ANALYSIS_ID = analysis.getId();
		this.mockMvc.perform(get(String.format("/Analysis/%d/Select", ANALYSIS_ID)).with(csrf()).with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(forwardedUrl("analyses/single/home"));
	}

	@Test
	public void test_03_DisplayAll() throws Exception {
		this.mockMvc.perform(get(String.format("/Analysis")).with(csrf()).with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isFound())
				.andExpect(redirectedUrl("/Analysis/All"));
	}

	@Test
	@Transactional(readOnly = true)
	public void test_04_CreateCustom() throws Exception {
		Analysis analysis = serviceAnalysis.get(ANALYSIS_ID);
		notNull(analysis, "Analysis cannot be found");
	}

	@Test(timeOut = 30000)
	public synchronized void test_05_CreateVersion() throws Exception {
		TASK_ID = new ObjectMapper()
				.readTree(
						this.mockMvc
								.perform(
										post(String.format("/Analysis/Duplicate/%d", ANALYSIS_ID)).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
												.accept(APPLICATION_JSON_CHARSET_UTF_8)
												.content(String.format("{\"author\":\"%s\", \"version\":\"%s\", \"comment\":\"%s\"}", "Admin Admin", "0.0.2", "comment")))
								.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).findValue("analysis_task_id").asText("");

		Worker worker = workersPoolManager.get(TASK_ID);

		notNull(worker, "Worker cannot be found");

		while (worker.isWorking())
			wait(100);

		isNull(worker.getError(), "Error should be null");

		serviceTaskFeedback.unregisterTask(USERNAME, TASK_ID);
	}

	@Test
	@Transactional(readOnly = true)
	public void test_06_SelectAnalysis_Version_0_0_2() {
		Analysis analysis = serviceAnalysis.getByCustomerAndLabelAndVersion(CUSTOMER_ID, SIMPLE_ANALYSIS_NAME, "0.0.2");
		notNull(analysis, "Analysis cannot be found");
		ANALYSIS_ID = analysis.getId();
	}

	@Test
	public void test_07_AddAsset() throws UnsupportedEncodingException, Exception {
		this.mockMvc
				.perform(
						post("/Analysis/Asset/Save")
								.with(csrf())
								.with(httpBasic(USERNAME, PASSWORD))
								.accept(APPLICATION_JSON_CHARSET_UTF_8)
								.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID)
								.content(
										String.format(
												"{\"id\":\"-1\", \"name\":\"%s\" ,\"assetType\": {\"id\": \"%d\" }, \"value\": \"%s\", \"selected\":\"%s\", \"comment\":\"%s\", \"hiddenComment\":\"%s\"}",
												"Trick service", 1, "687,688", false, "comment", "hiddenComment"))).andExpect(status().isOk()).andExpect(content().string("{}"));
	}

	@Test
	@Transactional(readOnly = false)
	public void test_08_LoadAsset() throws Exception {
		Asset asset = serviceAsset.getByNameAndAnlysisId("Trick service", ANALYSIS_ID);
		notNull(asset, "Asset 'Trick service' cannot be found");
		assertEquals("Bad Asset name", asset.getName(), "Trick service");
		assertEquals("Bad Asset value", asset.getValue(), 687.688 * 1000, 1E-10);
		assertEquals("Bad Asset comment", asset.getValue(), 687.688 * 1000, 1E-10);
		assertEquals("Bad Asset hidden comment", asset.getValue(), 687.688 * 1000, 1E-10);
		AssetType assetType = serviceAssetType.get(1);
		notNull(assetType, "Asset type cannot be found");
		assertEquals("Bad Asset asset-type", asset.getAssetType(), assetType);
	}

	@Test
	@Transactional(readOnly = true)
	public void test_09_GenerateScenrioAssetTypeValue() throws Exception {
		List<AssetType> assetTypes = serviceAssetType.getAll();
		for (AssetType assetType : assetTypes)
			SCENARIO_ASSET_TYPE_VALUE += String.format(",\"%s\":%d", assetType.getType(), 1);
	}

	@Test
	public void test_10_AddScenario() throws Exception {
		this.mockMvc
				.perform(
						post("/Analysis/Scenario/Save")
								.with(csrf())
								.with(httpBasic(USERNAME, PASSWORD))
								.accept(APPLICATION_JSON_CHARSET_UTF_8)
								.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID)
								.content(
										String.format("{\"id\":\"-1\", \"name\":\"%s\", \"scenarioType\": {\"id\": %d},\"selected\":\"%s\", \"description\":\"%s\"%s}",
												"Scenario test", 1, false, "Test scenario", SCENARIO_ASSET_TYPE_VALUE))).andExpect(status().isOk())
				.andExpect(content().string("{}"));
	}

	@Test
	@Transactional(readOnly = true)
	public void test_11_LoadScenario() throws Exception {
		Scenario scenario = serviceScenario.getByNameAndAnalysisId("Scenario test", ANALYSIS_ID);
		notNull(scenario, "Scenario 'Scenario test' cannot be found");
		assertEquals("Bad scenario name", scenario.getName(), "Scenario test");
		assertEquals("Bad scenario description", scenario.getDescription(), "Test scenario");
		assertFalse("Scenario is selected", scenario.isSelected());
		serviceAssetType.getAll().forEach(assetType -> {
			AssetTypeValue assetTypeValue = scenario.retrieveAssetTypeValue(assetType);
			notNull(assetTypeValue, String.format("Scanrio Asset type value for '%s' cannot be found", assetType.getType()));
			assertEquals( String.format("Scanrio Asset type value for '%s' cannot be found", assetType.getType()),1, assetTypeValue.getValue());
		});

	}
}
