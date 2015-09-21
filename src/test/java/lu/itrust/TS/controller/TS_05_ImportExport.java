/**
 * 
 */
package lu.itrust.TS.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static lu.itrust.TS.controller.TS_02_InstallApplication.*;
import static lu.itrust.TS.helper.TestSharingData.getInteger;
import static lu.itrust.TS.helper.TestSharingData.put;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_PHASE_BEGIN_DATE;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_PHASE_END_DATE;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_PROFITABILITY_RISK_REDUCTION;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI_RELATIF;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INVESTMENT;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT;
import static lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.util.Assert.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerAnalysisImport;
import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeActionPlan;
import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeRiskRegister;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportAnalysis;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportWordReport;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

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

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
	public void test_04_CheckActionPlan() throws Exception {
		Analysis analysis = serviceAnalysis.get(getInteger(ANALYSIS_KEY));
		notNull(analysis, String.format("Analysis (identifier : %s and version: %s) cannot be found", identifier, version));
		List<Object[]> data = new ArrayList<Object[]>(7);
		data.add(new Object[] { "27002", "5.1.1", "Policies for information security", "Define sectorial policies.", 5426.5d, 573.5d, 3600d, -3026.5d, 1d, 1d, 1000d, 1 });
		data.add(new Object[] { "Custom", "1.1.1", "Custom security measure", " 	Custom security measure To do text", 5426.5d, 0d, 3600d, -3600d, 1d, 1d, 1000d, 1 });
		data.add(new Object[] { "Custom Asset", "1.1", "Subdomain name", "Subdomain name  custom asset todo", 5426.5d, 0d, 3600d, -3600d, 1d, 1d, 1000d, 1 });
		data.add(new Object[] { "Custom Asset", "1.2", "Subdomaine name 2", "Subdomain name 2  custom asset todo", 5426.5d, 0d, 3600d, -3600d, 1d, 1d, 1000d, 1 });
		data.add(new Object[] { "Custom non-computable", "1.1.1", "Non-comp domain name", "Non-comp domain name todo", 5426.5d, 0d, 3600d, -3600d, 1d, 1d, 1000d, 1 });
		data.add(new Object[] { "27001", "5.1.2", "Establishment of security policy & objectives", "", 5426.5d, 0d, 21000d, -21000d, 10d, 1d, 0d, 3 });
		data.add(new Object[] { "27002", "6.1.1", "Information security roles and responsibilities", "Define roles and responsibilities.", 5110.76d, 315.74d, 200d, 115.74d, 1d,
				0d, 0d, 4 });
		List<ActionPlanEntry> actionPlanEntries = analysis.getActionPlans();
		notEmpty(actionPlanEntries, "Action plan should not be empty");
		Language language = analysis.getLanguage();
		for (int i = 0; i < actionPlanEntries.size(); i++)
			validate(actionPlanEntries.get(i), data.get(i), language);
	}

	private void validate(ActionPlanEntry actionPlanEntry, Object[] objects, Language language) {
		System.out.println();
		for (Object object : objects)
			System.out.print(object + " ");
		System.out.println();
		Measure measure = actionPlanEntry.getMeasure();
		notNull(measure, "Action plan measure should not be null");
		MeasureDescription measureDescription = measure.getMeasureDescription();
		notNull(measureDescription, "Action plan measure description should not be null");
		assertEquals("Bad action plan standard", objects[0], measureDescription.getStandard().getLabel());
		assertEquals("Bad action plan reference", objects[1], measureDescription.getReference());
		MeasureDescriptionText measureDescriptionText = measureDescription.getMeasureDescriptionText(language);
		notNull(measureDescriptionText, "Action plan measure description text should not be null");
		assertEquals("Bad action plan measure domain", objects[2], measureDescriptionText.getDomain());
		assertEquals("Bad action plan measure todo", objects[3], measure.getToDo());
		assertEquals("Bad action plan ALE", (double) objects[4], actionPlanEntry.getTotalALE(), 1E-2);
		assertEquals("Bad action plan delta ALE", (double) objects[5], actionPlanEntry.getDeltaALE(), 1E-2);
		assertEquals("Bad action plan measure cost", (double) objects[6], measure.getCost(), 1E-2);
		assertEquals("Bad action plan ROI", (double) objects[7], actionPlanEntry.getROI(), 1E-2);
		assertEquals("Bad action plan measure external workload", (double) objects[8], measure.getInternalWL(), 1E-2);
		assertEquals("Bad action plan measure internal workload", (double) objects[9], measure.getExternalWL(), 1E-2);
		assertEquals("Bad action plan measure investment", (double) objects[10], measure.getInvestment(), 1E-2);
		assertEquals("Bad action plan measure phase", (int) objects[11], measure.getPhase().getNumber());
	}

	@Test(dependsOnMethods = "test_02_ComputeActionPlan")
	@Transactional(readOnly = true)
	public void test_04_CheckActionPlanSummary() throws Exception {
		Analysis analysis = serviceAnalysis.get(getInteger(ANALYSIS_KEY));
		notNull(analysis, String.format("Analysis (identifier : %s and version: %s) cannot be found", identifier, version));
		List<SummaryStage> summaryStages = analysis.getSummary(ActionPlanMode.APPN);
		Map<String, List<Object>> summaries = ActionPlanSummaryManager.buildRawData(summaryStages, analysis.getPhases());
		Map<String, Object[]> exceptedResults = new LinkedHashMap<String, Object[]>();

		exceptedResults.put(LABEL_PHASE_BEGIN_DATE, new Object[] { null, parseSQLDate("2015-07-13"), parseSQLDate("2016-07-13"), parseSQLDate("2017-07-13"),
				parseSQLDate("2018-07-13") });
		exceptedResults.put(LABEL_PHASE_END_DATE, new Object[] { null, parseSQLDate("2016-07-13"), parseSQLDate("2017-07-13"), parseSQLDate("2018-07-13"),
				parseSQLDate("2019-07-13") });
		exceptedResults.put(LABEL_CHARACTERISTIC_COMPLIANCE + "Custom Asset", new Object[] { 0, 100, 100, 100, 100 });
		exceptedResults.put(LABEL_CHARACTERISTIC_COMPLIANCE + "27001", new Object[] { 96, 96, 96, 100, 100 });
		exceptedResults.put(LABEL_CHARACTERISTIC_COMPLIANCE + "27002", new Object[] { 0, 50, 50, 50, 100 });
		exceptedResults.put(LABEL_CHARACTERISTIC_COMPLIANCE + "Custom", new Object[] { 50, 100, 100, 100, 100 });
		exceptedResults.put(LABEL_CHARACTERISTIC_COMPLIANCE + "Custom non-computable", new Object[] { 0, 100, 100, 100, 100 });
		exceptedResults.put(LABEL_CHARACTERISTIC_COMPLIANCE + "Maturity", new Object[] { 1, 1, 1, 1, 1 });
		exceptedResults.put(LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE, new Object[] { 0, 5, 0, 1, 1 });
		exceptedResults.put(LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED, new Object[] { 8, 13, 13, 14, 15 });
		exceptedResults.put(LABEL_PROFITABILITY_ALE_UNTIL_END, new Object[] { 6000.0, 5426.5, 5426.5, 5426.5, 5110.7615 });
		exceptedResults.put(LABEL_PROFITABILITY_RISK_REDUCTION, new Object[] { 0.0, 573.5, 0.0, 0.0, 315.73850000000004 });
		exceptedResults.put(LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE, new Object[] { 0.0, 18000.0, 0.0, 21000.0, 200.0 });
		exceptedResults.put(LABEL_PROFITABILITY_ROSI, new Object[] { 0.0, -17426.5, 0.0, -21000.0, 115.73850000000004 });
		exceptedResults.put(LABEL_PROFITABILITY_ROSI_RELATIF, new Object[] { 0.0, -0.9681388888888889, 0.0, -1.0, 0.5786925000000003 });
		exceptedResults.put(LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD, new Object[] { 0.0, 5.0, 0.0, 10.0, 1.0 });
		exceptedResults.put(LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD, new Object[] { 0.0, 5.0, 0.0, 1.0, 0.0 });
		exceptedResults.put(LABEL_RESOURCE_PLANNING_INVESTMENT, new Object[] { 0.0, 5000.0, 0.0, 0.0, 0.0 });
		exceptedResults.put(LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST, new Object[] { 0.0, 15000.0, 0.0, 11000.0, 1000.0 });
		exceptedResults.put(LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE, new Object[] { 0.0, 1.002053388090349, 5.9958932238193015, 5.9958932238193015, 15.989048596851472 });
		exceptedResults.put(LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE, new Object[] { 0.0, 1.002053388090349, 5.9958932238193015, 5.9958932238193015, 5.9958932238193015 });
		exceptedResults.put(LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT, new Object[] { 0.0, 2004.106776180698, 6995.208761122519, 6995.208761122519, 6995.208761122519 });
		exceptedResults.put(LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST, new Object[] { 0.0, 19008.213552361398, 18986.99520876112, 29986.99520876112, 29980.15058179329 });
		for (String key : exceptedResults.keySet()) {
			Object[] expectedData = exceptedResults.get(key);
			Object data = expectedData[expectedData.length - 1];
			if (data instanceof Double)
				validateDoubles(expectedData, summaries.get(key));
			else
				validate(expectedData, summaries.get(key));
		}

	}

	private Date parseSQLDate(String date) throws ParseException {
		return new Date(DATE_FORMAT.parse(date).getTime());
	}

	private void validate(Object[] expectedData, List<Object> actualData) {
		for (int i = 0; i < expectedData.length; i++)
			assertEquals(expectedData[i], actualData.get(i));
	}

	private void validateDoubles(Object[] expectedData, List<Object> actualData) {
		for (int i = 0; i < expectedData.length; i++)
			assertEquals((double) expectedData[i], (double) actualData.get(i), 1E-2);
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
		notEmpty(registerItems, "Risk register should be empty");
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

	@Test(dependsOnMethods = { "test_02_ComputeActionPlan", "test_03_ComputeRiskRegister" })
	public synchronized void test_04_ExportSQLite() throws Exception {
		Integer idAnalysis = getInteger(ANALYSIS_KEY);
		notNull(idAnalysis, "Analysis cannot be found");
		this.mockMvc.perform(get("/Analysis/Export/" + idAnalysis).with(csrf()).with(httpBasic(USERNAME, PASSWORD)).contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
		Worker worker = null;
		for (int i = 0; i < 30; i++) {
			List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
			notEmpty(tasks, "No background task found");
			for (String workerId : tasks) {
				Worker worker2 = workersPoolManager.get(workerId);
				if (worker2 != null && worker2.isMatch("class+analysis.id", WorkerExportAnalysis.class, idAnalysis)) {
					worker = worker2;
					break;
				}
			}
			if (worker == null)
				wait(1000);
			else
				break;
		}
		notNull(worker, "Export analysis worker cannot be found");
		while (worker.isWorking())
			wait(100);

		isNull(worker.getError(), "An error occured while export analysis");

		MessageHandler messageHandler = serviceTaskFeedback.recieveLast(worker.getId());

		notNull(messageHandler, "Last message cannot be found");

		this.mockMvc.perform(get("/Task/Status/" + worker.getId()).with(csrf()).with(httpBasic(USERNAME, PASSWORD)).contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.asyncCallback.args[0]").exists());

		assertFalse("Task should be not existed", serviceTaskFeedback.hasTask(USERNAME, worker.getId()));

		notNull(messageHandler.getAsyncCallback(), "AsyncCallback should not be null");

		notEmpty(messageHandler.getAsyncCallback().getArgs(), "AsyncCallback args should not be empty");

		put("key_sql_export", Integer.parseInt(messageHandler.getAsyncCallback().getArgs().get(0)));
	}

	@Test(dependsOnMethods = "test_04_ExportSQLite")
	public void test_05_DownloadSQLite() throws Exception {
		MvcResult result = this.mockMvc
				.perform(
						get(String.format("/Profile/Sqlite/%d/Download", getInteger("key_sql_export"))).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
								.contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andReturn();
		notNull(result, "No result");
		MockHttpServletResponse response = result.getResponse();
		assertEquals("Bad length", 486400, response.getContentLength());
		assertEquals("Bad content-disposition", "attachment; filename=\"ENG_2015_07_13_07_31_14.sqlite\"", response.getHeaderValue("Content-Disposition"));
		assertEquals("Bad contentType", "sqlite", response.getContentType());
	}

	@Test(dependsOnMethods = { "test_02_ComputeActionPlan", "test_03_ComputeRiskRegister" })
	public synchronized void test_06_ExportReport() throws Exception {
		Integer idAnalysis = getInteger(ANALYSIS_KEY);
		notNull(idAnalysis, "Analysis cannot be found");
		this.mockMvc.perform(get("/Analysis/Export/Report/" + idAnalysis).with(csrf()).with(httpBasic(USERNAME, PASSWORD)).contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
		Worker worker = null;
		for (int i = 0; i < 30; i++) {
			List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
			notEmpty(tasks, "No background task found");
			for (String workerId : tasks) {
				Worker worker2 = workersPoolManager.get(workerId);
				if (worker2 != null && worker2.isMatch("class+analysis.id", WorkerExportWordReport.class, idAnalysis)) {
					worker = worker2;
					break;
				}
			}
			if (worker == null)
				wait(1000);
			else
				break;
		}
		notNull(worker, "Export word report worker cannot be found");
		while (worker.isWorking())
			wait(100);

		isNull(worker.getError(), "An error occured while export word report");

		MessageHandler messageHandler = serviceTaskFeedback.recieveLast(worker.getId());

		notNull(messageHandler, "Last message cannot be found");

		this.mockMvc.perform(get("/Task/Status/" + worker.getId()).with(csrf()).with(httpBasic(USERNAME, PASSWORD)).contentType(APPLICATION_JSON_CHARSET_UTF_8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.asyncCallback.args[0]").exists());

		assertFalse("Task should be not existed", serviceTaskFeedback.hasTask(USERNAME, worker.getId()));

		notNull(messageHandler.getAsyncCallback(), "AsyncCallback should not be null");

		notEmpty(messageHandler.getAsyncCallback().getArgs(), "AsyncCallback args should not be empty");

		put("key_sql_export_word", Integer.parseInt(messageHandler.getAsyncCallback().getArgs().get(0)));
	}

	@Test(dependsOnMethods = "test_06_ExportReport")
	public void test_07_DownloadReport() throws Exception {
		MvcResult result = this.mockMvc
				.perform(
						get(String.format("/Profile/Report/%d/Download", getInteger("key_sql_export"))).with(csrf()).with(httpBasic(USERNAME, PASSWORD))
								.contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andReturn();
		notNull(result, "No result");
		MockHttpServletResponse response = result.getResponse();
		assertEquals("Bad length", 1457983, response.getContentLength());
		assertEquals("Bad content-disposition", "attachment; filename=\"STA_TS Validation Analysis_V0.2.docm\"", response.getHeaderValue("Content-Disposition"));
		assertEquals("Bad contentType", "docm", response.getContentType());
	}
}
