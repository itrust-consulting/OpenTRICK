package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.AppSettingEntry;
import lu.itrust.business.component.ActionPlanManager;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;
import lu.itrust.business.service.ServiceActionPlan;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAnalysisNorm;
import lu.itrust.business.service.ServiceAppSettingEntry;
import lu.itrust.business.service.ServiceAsset;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserAnalysisRight;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;
import lu.itrust.business.task.WorkerComputeActionPlan;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerAdministration.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Dec 13, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/ActionPlan")
@Controller
public class ControllerActionPlan {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceAnalysisNorm serviceAnalysisNorm;

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

	@Autowired
	private ServiceAppSettingEntry serviceAppSettingEntry;

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
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

		// return view
		return "analysis/components/actionplan";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(Map<String, Object> model, HttpSession session, Principal principal) throws Exception {

		// retrieve analysis ID
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");

		// load all actionplans from the selected analysis
		List<ActionPlanEntry> actionplans = serviceActionPlan.getAllFromAnalysis(selected);

		if (!actionplans.isEmpty()) {

			// load all affected assets of the actionplans (unique assets used)
			List<Asset> assets = ActionPlanManager.getAssetsByActionPlanType(actionplans);

			Collections.reverse(actionplans);

			for (ActionPlanEntry ape : actionplans) {
				Hibernate.initialize(ape);
				Hibernate.initialize(ape.getMeasure());
				Hibernate.initialize(ape.getMeasure().getMeasureDescription().getMeasureDescriptionTexts());
			}
			// prepare model
			model.put("actionplans", actionplans);
			model.put("assets", assets);
		} else
			model.put("actionplans", actionplans);

		// return view
		return "analysis/components/actionplan";
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
	@RequestMapping(value = "/{analysisID}/ComputeOptions", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisID, #principal, T(lu.itrust.business.TS.AnalysisRight).CALCULATE_ACTIONPLAN)")
	public String computeActionPlanOptions(HttpSession session, Principal principal, Locale locale, Map<String, Object> model, @PathVariable("analysisID") Integer analysisID)
			throws Exception {
		AppSettingEntry settings = serviceAppSettingEntry.getByUsernameAndGroupAndName(principal.getName(), "analysis", analysisID.toString());
		if (settings != null) {
			model.put("show_uncertainty", settings.findByKey("show_uncertainty"));
			model.put("show_cssf", settings.findByKey("show_cssf"));
		}
		model.put("id", analysisID);

		model.put("norms", serviceAnalysisNorm.getAllFromAnalysis(analysisID));

		return "analysis/components/forms/actionplanoptions";
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

		// verify if user is authorized to compute the actionplan
		if (permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.CALCULATE_ACTIONPLAN)) {

			// retrieve options selected by the user

			boolean uncertainty = false;

			if (jsonNode.get("uncertainty") != null)
				uncertainty = jsonNode.get("uncertainty").asBoolean();

			List<AnalysisNorm> anorms = serviceAnalysisNorm.getAllFromAnalysis(analysisId);

			List<AnalysisNorm> norms = new ArrayList<AnalysisNorm>();

			for (AnalysisNorm anorm : anorms) {
				if (jsonNode.get("norm_" + anorm.getId()) != null)
					if (jsonNode.get("norm_" + anorm.getId()).asBoolean())
						norms.add(anorm);
			}

			// prepare asynchronous worker

			boolean reloadSection = session.getAttribute("selectedAnalysis") != null;

			Worker worker = new WorkerComputeActionPlan(sessionFactory, serviceTaskFeedback, analysisId, norms, uncertainty, reloadSection);
			worker.setPoolManager(workersPoolManager);

			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
				return JsonMessage.Error(messageSource.getMessage("failed.start.compute.actionplan", null, "Action plan computation was failed", locale));

			// execute task
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.start.compute.actionplan", null, "Action plan computation was started successfully", locale));
		} else {
			return JsonMessage.Success(messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
		}
	}
}