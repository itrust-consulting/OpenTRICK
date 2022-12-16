package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeActionPlan;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.helper.ActionPlanManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
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
public class ControllerActionPlan extends AbstractController {

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String showActionPlan(HttpSession session, Model model, Principal principal) throws Exception {
		section(model, session, principal);
		return "analyses/single/components/actionplan";
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
		final Integer selected = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Analysis analysis = serviceAnalysis.get(selected);
		final List<ActionPlanEntry> actionplans = serviceActionPlan.getAllFromAnalysis(selected);
		final OpenMode mode = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		final boolean allowedTicketing = loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(),
				model,
				null);
		final boolean isNoClientTicketing = (boolean) model.asMap().getOrDefault("isNoClientTicketing", false);
		model.addAttribute("isLinkedToProject", allowedTicketing && (isNoClientTicketing || analysis.hasProject()));
		model.addAttribute("isEditable", !OpenMode.isReadOnly(mode)
				&& serviceUserAnalysisRight.isUserAuthorized(selected, principal.getName(), AnalysisRight.MODIFY));
		model.addAttribute("actionplans", actionplans);
		model.addAttribute("type", analysis.getType());
		model.addAttribute("analysisId", selected);
		return "analyses/single/components/actionPlan/section";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/Assets", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadAssets(@RequestParam(defaultValue = "APPN") String selectedApt, Model model, HttpSession session,
			Principal principal) throws Exception {
		try {
			section(model, session, principal);
			model.addAttribute("selectedApt", ActionPlanMode.valueOf(selectedApt));
			model.addAttribute("assets", ActionPlanManager
					.getAssetsByActionPlanType((List<ActionPlanEntry>) model.asMap().get("actionplans")));
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
	public String computeActionPlanOptions(HttpSession session, Principal principal, Locale locale,
			Map<String, Object> model) throws Exception {
		Integer analysisID = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.put("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(analysisID));
		model.put("type", serviceAnalysis.getAnalysisTypeById(analysisID));
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
	public @ResponseBody String computeActionPlan(HttpSession session, Principal principal, Locale locale,
			@RequestBody String value) throws Exception {

		// prepare permission verifier
		final PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis,
				serviceUserAnalysisRight);

		final ObjectMapper mapper = new ObjectMapper();

		final JsonNode jsonNode = mapper.readTree(value);

		// retrieve analysis id to compute
		final int analysisId = jsonNode.get("id").asInt();

		// verify if user is authorized to compute the actionplan
		if (permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.READ)) {

			// retrieve options selected by the user

			final boolean uncertainty = serviceAnalysis.isAnalysisUncertainty(analysisId);

			List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(analysisId);

			List<Integer> standards = new ArrayList<Integer>();

			for (AnalysisStandard analysisStandard : analysisStandards) {
				if (jsonNode.get("standard_" + analysisStandard.getId()) != null)
					if (jsonNode.get("standard_" + analysisStandard.getId()).asBoolean())
						standards.add(analysisStandard.getId());
			}

			final boolean reloadSection = session.getAttribute(Constant.SELECTED_ANALYSIS) != null;

			final Worker worker = new WorkerComputeActionPlan(analysisId, standards, uncertainty, reloadSection);

			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
				return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null,
						"Too many tasks running in background", locale));
			// execute task
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.start.compute.actionplan", null,
					"Action plan computation was started successfully", locale));
		} else {
			return JsonMessage
					.Success(messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
		}
	}
}