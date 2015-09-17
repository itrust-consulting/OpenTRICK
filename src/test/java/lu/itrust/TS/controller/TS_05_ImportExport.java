/**
 * 
 */
package lu.itrust.TS.controller;

import java.util.ArrayList;
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
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;

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
		notNull(getInteger(ANALYSIS_KEY), "Analysis id cannot be found");
	}

	@Test(timeOut = 120000, dependsOnMethods = "test_01_CheckImportedAnalysis")
	public synchronized void test_02_ComputeActionPlan() throws Exception {
		Integer idAnalysis = getInteger(ANALYSIS_KEY);
		notNull(idAnalysis, "Analysis id cannot be found");
		this.mockMvc
				.perform(
						post("/Analysis/ActionPlan/Compute").with(csrf()).with(httpBasic(USERNAME, PASSWORD)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
								.content(String.format("{\"id\":%d}", idAnalysis))).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
		Worker worker = null;
		for (int i = 0; i < 30; i++) {
			List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
			notEmpty(tasks, "No background task found");
			for (String workerId : tasks) {
				Worker worker2 = workersPoolManager.get(workerId);
				if (worker2 != null && worker2.isMatch("class+analysis.id", WorkerComputeActionPlan.class, idAnalysis)) {
					worker = worker2;
					break;
				}
			}
			if (worker == null)
				wait(1000);
			else
				break;
		}
		notNull(worker, "Action plan worker cannot be found");
		while (worker.isWorking())
			wait(100);
		serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
		isNull(worker.getError(), "An error occured while compute action plan");
	}

	@Test(timeOut = 120000, dependsOnMethods = "test_01_CheckImportedAnalysis")
	public synchronized void test_03_ComputeRiskRegister() throws Exception {
		Integer idAnalysis = getInteger(ANALYSIS_KEY);
		this.mockMvc
				.perform(
						post("/Analysis/RiskRegister/Compute").with(csrf()).with(httpBasic(USERNAME, PASSWORD)).sessionAttr(Constant.SELECTED_ANALYSIS, idAnalysis)
								.contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
		Worker worker = null;
		for (int i = 0; i < 30; i++) {
			List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
			notEmpty(tasks, "No background task found");
			for (String workerId : tasks) {
				Worker worker2 = workersPoolManager.get(workerId);
				if (worker2 != null && worker2.isMatch("class+analysis.id", WorkerComputeRiskRegister.class, idAnalysis)) {
					worker = worker2;
					break;
				}
			}
			if (worker == null)
				wait(1000);
			else
				break;
		}
		notNull(worker, "Risk register worker cannot be found");
		while (worker.isWorking())
			wait(100);
		serviceTaskFeedback.unregisterTask(USERNAME, worker.getId());
		isNull(worker.getError(), "An error occured while compute risk register");
	}

	@Test(dependsOnMethods = "test_02_ComputeActionPlan")
	@Transactional(readOnly = true)
	public void test_04_CheckActionPlan() {

	}

	@Test(dependsOnMethods = "test_03_ComputeRiskRegister")
	@Transactional(readOnly = true)
	public void test_05_CheckRiskRegister() throws Exception {
		Analysis analysis = serviceAnalysis.get(getInteger(ANALYSIS_KEY));
		notNull(analysis, String.format("Analysis (identifier : %s and version: %s) cannot be found", identifier, version));
		List<Object[]> data = new ArrayList<Object[]>(6);
		data.add(new Object[] { "I2 - Fraudulent manipulation coming from internal", "Servers", 0.1, 10000d, 1000d, 0.1, 10000d, 1000d, 0.087, 8489.48, 742.49 });
		data.add(new Object[] { "I3 - Accidental manipulation", "Servers", 0.1, 10000d, 1000d, 0.1, 10000d, 1000d, 0.084, 8709.73, 735.66 });
		data.add(new Object[] { "I1 - External manipulation", "Servers", 0.1, 10000d, 1000d, 0.1, 10000d, 1000d, 0.082, 8778.35, 721.99 });
		data.add(new Object[] { "C3 - Accidental disclosure", "Customer documents", 0.1, 10000d, 1000d, 0.1, 10000d, 1000d, 0.073, 9838.32, 721.8 });
		data.add(new Object[] { "A_all - Complete loss, including backup", "Servers", 0.1, 10000d, 1000d, 0.1, 10000d, 1000d, 0.073, 9836.8, 719.58 });
		data.add(new Object[] { "A_1 - Partial loss or temporary", "Servers", 1d, 1000d, 1000d, 1d, 1000d, 1000d, 0.756, 951.84, 719.58 });
		List<RiskRegisterItem> registerItems = analysis.getRiskRegisters();
		for (int i = 0; i < registerItems.size(); i++)
			validate(registerItems.get(i), data.get(i));
	}

	private void validate(RiskRegisterItem riskRegisterItem, Object[] objects) {
		System.out.println();
		for (Object object : objects)
			System.out.print(object + " ");
		System.out.println();
		assertEquals("Bad scenario", objects[0], riskRegisterItem.getScenario().getName());
		assertEquals("Bad asset", objects[1], riskRegisterItem.getAsset().getName());
		assertEquals("Bad raw probability", (double) objects[2], riskRegisterItem.getRawEvaluation().getProbability(), 1E-3);
		assertEquals("Bad raw impact", (double) objects[3], riskRegisterItem.getRawEvaluation().getImpact(), 1E-2);
		assertEquals("Bad raw importance", (double) objects[4], riskRegisterItem.getRawEvaluation().getImportance(), 1E-2);
		assertEquals("Bad net probability", (double) objects[5], riskRegisterItem.getNetEvaluation().getProbability(), 1E-3);
		assertEquals("Bad net impact", (double) objects[6], riskRegisterItem.getNetEvaluation().getImpact(), 1E-2);
		assertEquals("Bad net importance", (double) objects[7], riskRegisterItem.getNetEvaluation().getImportance(), 1E-2);
		assertEquals("Bad expected probability", (double) objects[8], riskRegisterItem.getExpectedEvaluation().getProbability(), 1E-3);
		assertEquals("Bad expected impact", (double) objects[9], riskRegisterItem.getExpectedEvaluation().getImpact(), 1E-2);
		assertEquals("Bad expected importance", (double) objects[10], riskRegisterItem.getExpectedEvaluation().getImportance(), 1E-2);

	}
}
