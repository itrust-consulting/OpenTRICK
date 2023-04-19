package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpSession;
import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.asynchronousWorkers.WorkerComputeActionPlan;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceActionPlan;
import lu.itrust.business.ts.database.service.ServiceAnalysisStandard;
import lu.itrust.business.ts.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.model.actionplan.ActionPlanEntry;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.actionplan.helper.ActionPlanManager;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.general.OpenMode;

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
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;


	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String showActionPlan(HttpSession session, Model model, Principal principal) throws Exception {
		section(model, session, principal);
		return "jsp/analyses/single/components/actionplan";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
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
		return "jsp/analyses/single/components/actionPlan/section";
	}

	@SuppressWarnings("unchecked")
	@GetMapping(value = "/Assets", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String loadAssets(@RequestParam(defaultValue = "APPN") String selectedApt, Model model, HttpSession session,
			Principal principal) throws Exception {
		try {
			section(model, session, principal);
			model.addAttribute("selectedApt", ActionPlanMode.valueOf(selectedApt));
			model.addAttribute("assets", ActionPlanManager
					.getAssetsByActionPlanType((List<ActionPlanEntry>) model.asMap().get("actionplans")));
			return "jsp/analyses/single/components/actionPlan/assets";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String computeActionPlanOptions(HttpSession session, Principal principal, Locale locale,
			Map<String, Object> model) throws Exception {
		Integer analysisID = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.put("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(analysisID));
		model.put("type", serviceAnalysis.getAnalysisTypeById(analysisID));
		model.put("id", analysisID);
		model.put("standards", serviceAnalysisStandard.getAllFromAnalysis(analysisID));
		return "jsp/analyses/single/components/actionPlan/form";
	}

	/**
	 * computeActionPlan: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param attributes
	 * @return
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 * @throws Exception
	 */
	@PostMapping(value = "/Compute", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String computeActionPlan(HttpSession session, Principal principal, Locale locale,
			@RequestBody List<Integer> standards) {

		// retrieve analysis id to compute
		final Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		// retrieve options selected by the user

		final boolean uncertainty = serviceAnalysis.isAnalysisUncertainty(analysisId);

		final boolean reloadSection = session.getAttribute(Constant.SELECTED_ANALYSIS) != null;

		final Worker worker = new WorkerComputeActionPlan(analysisId, standards, uncertainty, reloadSection);

		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", locale));
		// execute task
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.compute.actionplan", null,
				"Action plan computation was started successfully", locale));

	}

}