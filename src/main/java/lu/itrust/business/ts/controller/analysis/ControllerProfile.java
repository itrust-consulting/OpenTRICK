/**
 * 
 */
package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.ts.constants.Constant.SELECTED_ANALYSIS;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.asynchronousWorkers.WorkerCreateAnalysisProfile;
import lu.itrust.business.ts.component.CustomDelete;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceAnalysisStandard;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.Standard;

/**
 * @author eomar
 * 
 */

@RequestMapping("/AnalysisProfile")
@Controller
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
public class ControllerProfile {

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;
	
	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping("/Add/{analysisId}")
	public String createProfile(@PathVariable int analysisId, Model model, Principal principal) throws Exception {
		List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(analysisId);
		model.addAttribute("analysisStandards", analysisStandards);
		model.addAttribute("id", analysisId);
		return "jsp/analyses/all/forms/createProfile";
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Analysis/{idAnalysis}/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> saveProfile(@PathVariable int idAnalysis, @RequestBody Map<Object, Object> data, Principal principal, Locale locale) throws Exception {

		final Map<String, String> errors = new LinkedHashMap<String, String>();
		try {
			final String name = (String) data.get("description");
			final List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);
			final List<Integer> standards = analysisStandards.stream().map(AnalysisStandard::getStandard).map(Standard::getId).filter(idStandard -> data.containsKey(idStandard+""))
					.collect(Collectors.toList());
			if (!StringUtils.hasText(name))
				errors.put("description", messageSource.getMessage("error.analysis_profile.empty_description", null, "Description cannot be empty", locale));
			else if (serviceAnalysis.isProfileNameInUsed(name))
				errors.put("description",
						messageSource.getMessage("error.analysis_profile.name_in_used", null, "Another analysis profile with the same description already exists", locale));
			
			if(standards.isEmpty())
				errors.put("standards",
						messageSource.getMessage("error.analysis_profile.collection.empty", null, "Please select at least one measures collection", locale));
			
			if(!errors.isEmpty())
				return errors;
			
			final Worker worker = new WorkerCreateAnalysisProfile( idAnalysis, name, standards, principal.getName());
			if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
				executor.execute(worker);
				errors.put("taskid", String.valueOf(worker.getId()));
			} else
				errors.put("analysisprofile", messageSource.getMessage("failed.analysis.duplication.start", null, "Error starting profile creation task!", locale));
			return errors;
		} catch (TrickException e) {
			TrickLogManager.persist(e);
			errors.put("analysisprofile", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (AccessDeniedException e) {
			TrickLogManager.persist(LogLevel.ERROR, LogType.ANALYSIS, "log.analysis_profile.access_deny", String.format("Analysis: %d", idAnalysis), principal.getName(),
					LogAction.DENY_ACCESS, idAnalysis + "");
			errors.put("analysisprofile", e.getMessage());
		} catch (Exception e) {
			TrickLogManager.persist(e);
			errors.put("analysisprofile", e.getMessage());
		}
		return errors;

	}

	// *****************************************************************
	// * set default profile
	// *****************************************************************
	@RequestMapping(value = "/SetDefaultProfile/{analysisId}", method = RequestMethod.POST)
	public @ResponseBody boolean setDefaultProfile(@PathVariable("analysisId") Integer analysisId, @RequestBody AnalysisType analysisType, Principal principal, HttpSession session)
			throws Exception {
		Analysis analysis = serviceAnalysis.get(analysisId);
		Analysis currentProfileanalysis = serviceAnalysis.getDefaultProfile(analysisType);
		if (analysis == null || !analysis.isProfile()) {
			System.out.println("Bad analysis for default profile");
			return false;
		}
		analysis.setDefaultProfile(true);
		serviceAnalysis.saveOrUpdate(analysis);
		if (currentProfileanalysis != null) {
			if (currentProfileanalysis.getId() != analysisId) {
				currentProfileanalysis.setDefaultProfile(false);
				serviceAnalysis.saveOrUpdate(currentProfileanalysis);
			}
		}
		return true;
	}

	@RequestMapping("/Section")
	public String section(HttpServletRequest request, Principal principal, Model model) throws Exception {
		model.addAttribute("analyses", serviceAnalysis.getAllProfiles());
		model.addAttribute("login", principal.getName());
		return "jsp/knowledgebase/analysis/analyses";
	}
	
	/**
	 * deleteAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Delete/{analysisId}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.hasDeletePermission(#analysisId, #principal, true)")
	public @ResponseBody String deleteAnalysis(@PathVariable("analysisId") int analysisId, Principal principal, HttpSession session, Locale locale)
			throws Exception {
		try {
			customDelete.deleteAnalysis(analysisId, principal.getName());
			Integer selectedAnalysis = (Integer) session.getAttribute(SELECTED_ANALYSIS);
			if (selectedAnalysis != null && selectedAnalysis == analysisId)
				session.removeAttribute(SELECTED_ANALYSIS);
			return JsonMessage.success(messageSource.getMessage("success.analysis.delete.successfully", null, "Analysis was deleted successfully", locale));
		} catch (Exception e) {
			// return error message
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage("failed.delete.analysis", null, "Analysis cannot be deleted!", locale));
		}
	}
}
