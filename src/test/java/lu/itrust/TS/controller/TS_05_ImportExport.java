/**
 * 
 */
package lu.itrust.TS.controller;

import java.util.List;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerAnalysisImport;
import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeActionPlan;
import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeRiskRegister;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.analysis.Analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import static lu.itrust.TS.controller.TS_02_InstallApplication.*;
import static lu.itrust.TS.helper.TestSharingData.*;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.util.Assert.*;

/**
 * @author eomar
 *
 */
@Test(groups = "ImportExport", dependsOnGroups = "Installation")
public class TS_05_ImportExport extends SpringTestConfiguration {

	private static String ANALYSIS_KEY = null;

	@Autowired
	private ResourceLoader resourceLoader;

	@Value("${app.settings.test.validation.action.plan}")
	private String actionPlanValidation;

	@Value("${app.settings.test.validation.action.plan.analysis.identifier}")
	private String identifier;

	@Value("${app.settings.test.validation.action.plan.analysis.version}")
	private String version;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Test(timeOut = 120000)
	public synchronized void test_00_Import() throws Exception {

		Resource resource = resourceLoader.getResource(actionPlanValidation);

		isTrue(resource.exists(), "Resource cannot be found");

		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", resource.getInputStream());
		MvcResult mvcResult = this.mockMvc
				.perform(
						fileUpload("/Analysis/Import/Execute").file(mockMultipartFile).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
								.param("customerId", getInteger(ME_CUSTOMER).toString())).andExpect(status().isFound()).andExpect(redirectedUrl("/Analysis/Import")).andReturn();
		notNull(mvcResult, "Request should have result");

		assertFalse((String) mvcResult.getFlashMap().get("error"), mvcResult.getFlashMap().containsKey("error"));

		wait(1000);

		List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
		notEmpty(tasks, "No background task found");
		Worker worker = null;
		for (String workerId : tasks) {
			Worker worker2 = workersPoolManager.get(workerId);
			if (worker2.isMatch("class+customer.id", WorkerAnalysisImport.class, getInteger(ME_CUSTOMER))) {
				worker = worker2;
				break;
			}
		}
		notNull(worker, "Import analysis worker cannot be found");
		while (worker.isWorking())
			wait(100);
		serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
		isNull(worker.getError(), "An error occured while import analysis");
		ANALYSIS_KEY = String.format("Analysis-%s-%s", identifier, version);
	}

	@Test(dependsOnMethods = "test_00_Import")
	@Transactional(readOnly = true)
	public void test_01_CheckImportedAnalysis() {
		Analysis analysis = serviceAnalysis.getByIdentifierAndVersion(identifier, version);
		notNull(analysis, String.format("Analysis (identifier : %s and version: %s) cannot be found", identifier, version));
		put(ANALYSIS_KEY, analysis.getId());
		assertEquals("Bad analysis version", version, analysis.getVersion());
		assertEquals("Bad analysis identifier", identifier, analysis.getIdentifier());
		isTrue(analysis.getActionPlans().isEmpty(), "Action plan should be empty");
		isTrue(analysis.getSummaries().isEmpty(), "Action plan summary should be empty");
		isTrue(analysis.getRiskRegisters().isEmpty(), "Risk register should be empty");
	}
	
	@Test(timeOut = 120000, dependsOnMethods="test_01_CheckImportedAnalysis")
	public synchronized void test_02_ComputeActionPlan() throws Exception {
		Integer idAnalysis = getInteger(ANALYSIS_KEY);
		this.mockMvc
				.perform(
						post("/Analysis/ActionPlan/Compute").with(csrf()).with(httpBasic(USERNAME, PASSWORD)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
								.content(String.format("{\"id\":%d}", idAnalysis))).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
		wait(1000);
		List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
		notEmpty(tasks, "No background task found");
		Worker worker = null;
		for (String workerId : tasks) {
			Worker worker2 = workersPoolManager.get(workerId);
			if (worker2.isMatch("class+analysis.id", WorkerComputeActionPlan.class, idAnalysis)) {
				worker = worker2;
				break;
			}
		}
		notNull(worker, "Action plan worker cannot be found");
		while (worker.isWorking())
			wait(100);
		serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
		isNull(worker.getError(), "An error occured while compute action plan");
	}

	@Test(timeOut = 120000,dependsOnMethods="test_01_CheckImportedAnalysis")
	public synchronized void test_03_ComputeRiskRegister() throws Exception {
		Integer idAnalysis = getInteger(ANALYSIS_KEY);
		this.mockMvc
				.perform(
						post("/Analysis/RiskRegister/Compute").with(csrf()).with(httpBasic(USERNAME, PASSWORD)).sessionAttr(Constant.SELECTED_ANALYSIS, idAnalysis)
								.contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
		wait(1000);
		List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
		notEmpty(tasks, "No background task found");
		Worker worker = null;
		for (String workerId : tasks) {
			Worker worker2 = workersPoolManager.get(workerId);
			if (worker2.isMatch("class+analysis.id", WorkerComputeRiskRegister.class, idAnalysis)) {
				worker = worker2;
				break;
			}
		}
		notNull(worker, "Risk register worker cannot be found");
		while (worker.isWorking())
			wait(100);
		serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
		isNull(worker.getError(), "An error occured while compute risk register");
	}
}
