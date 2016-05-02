package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.ALLOWED_TICKETING;
import static lu.itrust.business.TS.constants.Constant.TICKETING_NAME;
import static lu.itrust.business.TS.constants.Constant.TICKETING_URL;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeActionPlan;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.helper.ActionPlanManager;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.general.TSSetting;
import lu.itrust.business.TS.model.general.TSSettingName;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;

/**
 * ControllerAdministration.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Dec 13, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/ActionPlan")
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
	private TaskExecutor executor;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceTSSetting serviceTSSetting;

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String showActionPlan(HttpSession session, Model model, Principal principal) throws Exception {
		// retrieve analysis ID
		Integer selected = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// load all actionplans from the selected analysis
		List<ActionPlanEntry> actionplans = serviceActionPlan.getAllFromAnalysis(selected);
		// load all affected assets of the actionplans (unique assets used)
		List<Asset> assets = ActionPlanManager.getAssetsByActionPlanType(actionplans);
		OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		// prepare model
		model.addAttribute("assets", assets);
		model.addAttribute("actionplans", actionplans);
		model.addAttribute("language", serviceAnalysis.getLanguageOfAnalysis(selected).getAlpha2());
		model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(selected) && loadUserSettings(principal, model, null));
		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode) && serviceUserAnalysisRight.isUserAuthorized(selected, principal.getName(), AnalysisRight.MODIFY));

		// return view
		return "analyses/single/components/actionplan";
	}

	private boolean loadUserSettings(Principal principal, @Nullable Model model, @Nullable User user) {
		boolean allowedTicketing = false;
		try {
			if (user == null)
				user = serviceUser.get(principal.getName());
			TSSetting name = serviceTSSetting.get(TSSettingName.TICKETING_SYSTEM_NAME), url = serviceTSSetting.get(TSSettingName.TICKETING_SYSTEM_URL);
			String username = user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME), password = user.getSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD);
			allowedTicketing = !(name == null || url == null || StringUtils.isEmpty(name.getValue()) || StringUtils.isEmpty(url.getValue()) || StringUtils.isEmpty(username)
					|| StringUtils.isEmpty(password)) && serviceTSSetting.isAllowed(TSSettingName.SETTING_ALLOWED_TICKETING_SYSTEM_LINK);
			if (model != null && allowedTicketing) {
				model.addAttribute(TICKETING_NAME, StringUtils.capitalize(name.getValue()));
				model.addAttribute(TICKETING_URL, url.getString());
			}
		} catch (Exception e) {
			TrickLogManager.Persist(e);

		} finally {
			if (model != null)
				model.addAttribute(ALLOWED_TICKETING, allowedTicketing);
		}
		return allowedTicketing;
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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {
		// retrieve analysis ID
		Integer selected = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// load all actionplans from the selected analysis
		List<ActionPlanEntry> actionplans = serviceActionPlan.getAllFromAnalysis(selected);
		if (!actionplans.isEmpty()) {
			// prepare model
			OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
			model.addAttribute("actionplans", actionplans);
			model.addAttribute("language", serviceAnalysis.getLanguageOfAnalysis(selected).getAlpha2());
			model.addAttribute("isLinkedToProject", serviceAnalysis.hasProject(selected) && loadUserSettings(principal, model, null));
			model.addAttribute("isEditable", !OpenMode.isReadOnly(mode) && serviceUserAnalysisRight.isUserAuthorized(selected, principal.getName(), AnalysisRight.MODIFY));
		} else
			model.addAttribute("actionplans", actionplans);

		// return view
		return "analyses/single/components/actionPlan/section";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/Assets", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadAssets(@RequestParam(defaultValue = "APPN") String selectedApt, Model model, HttpSession session, Principal principal) throws Exception {
		try {
			section(model, session, principal);
			model.addAttribute("selectedApt", ActionPlanMode.valueOf(selectedApt));
			model.addAttribute("assets", ActionPlanManager.getAssetsByActionPlanType((List<ActionPlanEntry>) model.asMap().get("actionplans")));
			return "analyses/single/components/actionPlan/assets";
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			throw e;
		}
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
	@RequestMapping(value = "/ComputeOptions", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String computeActionPlanOptions(HttpSession session, Principal principal, Locale locale, Map<String, Object> model) throws Exception {
		Integer analysisID = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.put("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(analysisID));
		model.put("show_cssf", serviceAnalysis.isAnalysisCssf(analysisID));
		model.put("id", analysisID);
		model.put("standards", serviceAnalysisStandard.getAllFromAnalysis(analysisID));
		return "analyses/single/components/actionPlan/form";
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
	@RequestMapping(value = "/Compute", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String computeActionPlan(HttpSession session, Principal principal, Locale locale, @RequestBody String value) throws Exception {

		// prepare permission verifier
		PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(value);

		// retrieve analysis id to compute
		int analysisId = jsonNode.get("id").asInt();

		// verify if user is authorized to compute the actionplan
		if (permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.READ)) {

			// retrieve options selected by the user

			boolean uncertainty = serviceAnalysis.isAnalysisUncertainty(analysisId);

			List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(analysisId);

			List<Integer> standards = new ArrayList<Integer>();

			for (AnalysisStandard analysisStandard : analysisStandards) {
				if (jsonNode.get("standard_" + analysisStandard.getId()) != null)
					if (jsonNode.get("standard_" + analysisStandard.getId()).asBoolean())
						standards.add(analysisStandard.getId());
			}

			boolean reloadSection = session.getAttribute(Constant.SELECTED_ANALYSIS) != null;
			Worker worker = new WorkerComputeActionPlan(workersPoolManager, sessionFactory, serviceTaskFeedback, analysisId, standards, uncertainty, reloadSection, messageSource);
			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
				return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
			// execute task
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.start.compute.actionplan", null, "Action plan computation was started successfully", locale));
		} else {
			return JsonMessage.Success(messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
		}
	}
}