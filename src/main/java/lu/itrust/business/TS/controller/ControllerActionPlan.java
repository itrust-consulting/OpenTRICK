package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeActionPlan;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.data.actionplan.helper.ActionPlanManager;
import lu.itrust.business.TS.data.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.data.asset.Asset;
import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ControllerAdministration.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Dec 13, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("Analysis/ActionPlan")
@Controller
public class ControllerActionPlan {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	/**
	 * showActionPlan: <br>
	 * dispaly all actionplans of an analysis
	 * 
	 * @param session
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public String showActionPlan(HttpSession session, Map<String, Object> model, Principal principal) throws Exception {

		// retrieve analysis ID
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");

		// load all actionplans from the selected analysis
		List<ActionPlanEntry> actionplans = serviceActionPlan.getAllFromAnalysis(selected);

		// load all affected assets of the actionplans (unique assets used)
		List<Asset> assets = ActionPlanManager.getAssetsByActionPlanType(actionplans);

		// prepare model
		model.put("actionplans", actionplans);
		model.put("assets", assets);
		model.put("language", serviceAnalysis.getLanguageOfAnalysis(selected).getAlpha2());

		// return view
		return "analyses/singleAnalysis/components/actionplan";
	}

	/**
	 * section: <br>
	 * reload the section of a given actionplan type
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public String section(Map<String, Object> model, HttpSession session, Principal principal) throws Exception {

		// retrieve analysis ID
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");

		// load all actionplans from the selected analysis
		List<ActionPlanEntry> actionplans = serviceActionPlan.getAllFromAnalysis(selected);

		if (!actionplans.isEmpty()) {

			// load all affected assets of the actionplans (unique assets used)
			List<Asset> assets = ActionPlanManager.getAssetsByActionPlanType(actionplans);

			// Collections.reverse(actionplans);

			for (ActionPlanEntry ape : actionplans) {
				Hibernate.initialize(ape);
				Hibernate.initialize(ape.getMeasure());
				Hibernate.initialize(ape.getMeasure().getMeasureDescription().getMeasureDescriptionTexts());
			}
			// prepare model
			model.put("actionplans", actionplans);
			model.put("assets", assets);
			model.put("language", serviceAnalysis.getLanguageOfAnalysis(selected).getAlpha2());
		} else
			model.put("actionplans", actionplans);

		// return view
		return "analyses/singleAnalysis/components/actionplan";
	}

	/**
	 * computeActionPlan: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ComputeOptions", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).CALCULATE_ACTIONPLAN)")
	public String computeActionPlanOptions(HttpSession session, Principal principal, Locale locale, Map<String, Object> model) throws Exception {

		Integer analysisID = (Integer) session.getAttribute("selectedAnalysis");

		model.put("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(analysisID));
		model.put("show_cssf", serviceAnalysis.isAnalysisCssf(analysisID));

		model.put("id", analysisID);

		model.put("standards", serviceAnalysisStandard.getAllFromAnalysis(analysisID));

		return "analyses/singleAnalysis/components/forms/actionplanoptions";
	}

	/**
	 * computeActionPlan: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Compute", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String computeActionPlan(HttpSession session, Principal principal, Locale locale, @RequestBody String value) throws Exception {

		// prepare permission verifier
		PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(value);

		// retrieve analysis id to compute
		int analysisId = jsonNode.get("id").asInt();

		Locale analysisLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha2());

		// verify if user is authorized to compute the actionplan
		if (permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.CALCULATE_ACTIONPLAN)) {

			// retrieve options selected by the user

			boolean uncertainty = serviceAnalysis.isAnalysisUncertainty(analysisId);

			List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(analysisId);

			List<Integer> standards = new ArrayList<Integer>();

			for (AnalysisStandard analysisStandard : analysisStandards) {
				if (jsonNode.get("standard_" + analysisStandard.getId()) != null)
					if (jsonNode.get("standard_" + analysisStandard.getId()).asBoolean())
						standards.add(analysisStandard.getId());
			}

			boolean reloadSection = session.getAttribute("selectedAnalysis") != null;
			Worker worker = new WorkerComputeActionPlan(sessionFactory, serviceTaskFeedback, analysisId, standards, uncertainty, reloadSection, messageSource);
			worker.setPoolManager(workersPoolManager);
			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
				return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", analysisLocale));
			// execute task
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.start.compute.actionplan", null, "Action plan computation was started successfully", analysisLocale));
		} else {
			return JsonMessage.Success(messageSource.getMessage("error.permission_denied", null, "Permission denied!", analysisLocale));
		}
	}
}