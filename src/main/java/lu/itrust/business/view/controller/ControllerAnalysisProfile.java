/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lu.itrust.business.TS.Standard;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.helper.AnalysisProfile;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceStandard;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;
import lu.itrust.business.task.WorkerCreateAnalysisProfile;
import lu.itrust.business.validator.AnalysisProfileValidator;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author eomar
 * 
 */

@RequestMapping("/AnalysisProfile")
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
public class ControllerAnalysisProfile {

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.replaceValidators(new AnalysisProfileValidator());
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping("/Add/{analysisId}")
	public String createProfile(@PathVariable int analysisId, Model model, Principal principal) throws Exception {
		List<Standard> standards = serviceStandard.getAllFromAnalysis(analysisId);
		AnalysisProfile analysisProfile = new AnalysisProfile(analysisId);
		model.addAttribute("standards", standards);
		model.addAttribute("analysisProfile", analysisProfile);
		return "analysis/forms/createProfile";
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisProfile.idAnalysis, #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping("/Save")
	public String saveProfile(@ModelAttribute @Valid AnalysisProfile analysisProfile, BindingResult result, Model model, Principal principal, Locale locale) throws Exception {

		if (result.hasErrors()) {
			model.addAttribute("standards", serviceStandard.getAllFromAnalysis(analysisProfile.getIdAnalysis()));
			return "analysis/forms/createProfile";
		}

		if (serviceAnalysis.isProfile(analysisProfile.getIdAnalysis())) {
			model.addAttribute("standards", serviceStandard.getAllFromAnalysis(analysisProfile.getIdAnalysis()));
			result.rejectValue("name", "error.analysis.profile.name.used", null, "Name is not available");
			return "analysis/forms/createProfile";
		}

		User user = serviceUser.get(principal.getName());

		Worker worker = new WorkerCreateAnalysisProfile(serviceTaskFeedback, sessionFactory, workersPoolManager, analysisProfile, user);
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {
			executor.execute(worker);
			return "redirect:/Task/Status/" + worker.getId();
		}
		result.reject("failed.analysis.duplication.start", "Profile cannot be create");
		return "analysis/forms/createProfile";
	}

	@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
	@RequestMapping("/Section")
	public String section(HttpServletRequest request, Principal principal, Model model) throws Exception {
		model.addAttribute("analyses", serviceAnalysis.getAllProfiles());
		model.addAttribute("login", principal.getName());
		return "knowledgebase/analysis/analyses";
	}
}
