/**
 * 
 */
package lu.itrust.TS.controller;

import static lu.itrust.TS.controller.TS_03_CreateAnAnlysis.ANALYSIS_ID;
import static lu.itrust.TS.helper.TestSharingData.getInteger;
import static lu.itrust.TS.helper.TestSharingData.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeActionPlan;
import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeRiskRegister;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceActionPlanSummary;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceRiskRegister;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.parameter.Parameter;

/**
 * @author eomar
 *
 */
@Test(groups = "Computation", dependsOnGroups = "CreateAnalysis")
public class TS_04_Computation extends SpringTestConfiguration {

	private static final String CSSF_PARAMETER_ANALYSIS = "cssf_parameter_analysis-";

	@Autowired
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceActionPlanSummary serviceActionPlanSummary;

	@Autowired
	private ServiceRiskRegister serviceRiskRegister;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Test(timeOut = 120000)
	public synchronized void test_00_ActionPlan() throws Exception {
		this.mockMvc.perform(post("/Analysis/ActionPlan/Compute").with(csrf()).with(httpBasic(USERNAME, PASSWORD)).contentType(APPLICATION_JSON_CHARSET_UTF_8)
				.content(String.format("{\"id\":%d}", ANALYSIS_ID))).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
		Worker worker = null;
		for (int i = 0; i < 30; i++) {
			List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
			notEmpty(tasks, "No background task found");
			for (String workerId : tasks) {
				Worker worker2 = workersPoolManager.get(workerId);
				if (worker2 != null && worker2.isMatch("class+analysis.id", WorkerComputeActionPlan.class, ANALYSIS_ID)) {
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

	@Test
	@Transactional(readOnly = true)
	public void loadCSSFParameter() {
		Analysis analysis = serviceAnalysis.get(ANALYSIS_ID);
		notNull(analysis, "Analysis cannot be found");
		Parameter parameter = analysis.findParameter(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, Constant.CSSF_CIA_SIZE);
		notNull(parameter, "Analysis cannot be found");
		put(CSSF_PARAMETER_ANALYSIS + ANALYSIS_ID, parameter.getId());
	}

	@Test(dependsOnMethods = "loadCSSFParameter")
	public void test_01_UpdateCSSFParameter() throws Exception {
		int id = getInteger(CSSF_PARAMETER_ANALYSIS + ANALYSIS_ID);
		this.mockMvc
				.perform(post("/Analysis/EditField/Parameter/" + id).with(csrf()).with(httpBasic(USERNAME, PASSWORD)).sessionAttr(Constant.OPEN_MODE, OpenMode.EDIT)
						.sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID).contentType(APPLICATION_JSON_CHARSET_UTF_8)
						.content(String.format("{\"id\":%d, \"fieldName\": \"%s\",\"type\": \"%s\", \"value\": %f}", id, "value", "double", -1D)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}

	@Test(timeOut = 120000, dependsOnMethods = { "test_01_UpdateCSSFParameter", "loadCSSFParameter" })
	public synchronized void test_01_RiskRegister() throws Exception {
		this.mockMvc.perform(post("/Analysis/RiskRegister/Compute").with(csrf()).with(httpBasic(USERNAME, PASSWORD)).sessionAttr(Constant.SELECTED_ANALYSIS, ANALYSIS_ID)
				.contentType(APPLICATION_JSON_CHARSET_UTF_8)).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
		Worker worker = null;
		for (int i = 0; i < 30; i++) {
			List<String> tasks = serviceTaskFeedback.tasks(USERNAME);
			notEmpty(tasks, "No background task found");
			for (String workerId : tasks) {
				Worker worker2 = workersPoolManager.get(workerId);
				if (worker2 != null && worker2.isMatch("class+analysis.id", WorkerComputeRiskRegister.class, ANALYSIS_ID)) {
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

	@Test(dependsOnMethods = "test_00_ActionPlan")
	@Transactional(readOnly = true)
	public void test_02_CheckActionPLan() throws Exception {
		List<ActionPlanEntry> actionPlans = serviceActionPlan.getAllFromAnalysis(ANALYSIS_ID);
		notEmpty(actionPlans, "Action plan is empty");
	}

	@Test(dependsOnMethods = "test_01_RiskRegister")
	@Transactional(readOnly = true)
	public void test_03_CheckRiskRegister() throws Exception {

		List<RiskRegisterItem> riskRegisterItems = serviceRiskRegister.getAllFromAnalysis(ANALYSIS_ID);
		notEmpty(riskRegisterItems, "Risk register is empty");
	}

	@Test(dependsOnMethods = "test_02_CheckActionPLan")
	@Transactional(readOnly = true)
	public void test_04_CheckActionPlanSummary() throws Exception {
		List<SummaryStage> summaryStages = serviceActionPlanSummary.getAllFromAnalysis(ANALYSIS_ID);
		notEmpty(summaryStages, "Action plan summary is empty");
	}
}
