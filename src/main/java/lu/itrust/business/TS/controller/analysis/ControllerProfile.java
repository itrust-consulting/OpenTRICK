/**
 * 
 */
package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.SELECTED_ANALYSIS;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
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

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerCreateAnalysisProfile;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;

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
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;
	
	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping("/Add/{analysisId}")
	public String createProfile(@PathVariable int analysisId, Model model, Principal principal) throws Exception {
		List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(analysisId);
		model.addAttribute("analysisStandards", analysisStandards);
		model.addAttribute("id", analysisId);
		return "analyses/all/forms/createProfile";
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Analysis/{idAnalysis}/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> saveProfile(@PathVariable int idAnalysis, @RequestBody Map<Object, Object> data, Principal principal, Locale locale) throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {
			String name = (String) data.get("description");
			List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);
			List<Integer> standards = analysisStandards.stream().map(AnalysisStandard::getStandard).map(Standard::getId).filter(idStandard -> data.containsKey(idStandard+""))
					.collect(Collectors.toList());
			if (StringUtils.isEmpty(name))
				errors.put("description", messageSource.getMessage("error.analysis_profile.empty_description", null, "Description cannot be empty", locale));
			else if (serviceAnalysis.isProfileNameInUsed(name))
				errors.put("description",
						messageSource.getMessage("error.analysis_profile.name_in_used", null, "Another analysis profile with the same description already exists", locale));
			
			if(standards.isEmpty())
				errors.put("standards",
						messageSource.getMessage("error.analysis_profile.collection.empty", null, "Please select at least one measures collection", locale));
			
			if(!errors.isEmpty())
				return errors;
			
			Worker worker = new WorkerCreateAnalysisProfile(serviceTaskFeedback, sessionFactory, workersPoolManager, idAnalysis, name, standards, principal.getName());
			if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
				executor.execute(worker);
				errors.put("taskid", String.valueOf(worker.getId()));
			} else
				errors.put("analysisprofile", messageSource.getMessage("failed.analysis.duplication.start", null, "Error starting profile creation task!", locale));
			return errors;
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			errors.put("analysisprofile", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (AccessDeniedException e) {
			TrickLogManager.Persist(LogLevel.ERROR, LogType.ANALYSIS, "log.analysis_profile.access_deny", String.format("Analysis: %d", idAnalysis), principal.getName(),
					LogAction.DENY_ACCESS, idAnalysis + "");
			errors.put("analysisprofile", e.getMessage());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
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
		return "knowledgebase/analysis/analyses";
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
			return JsonMessage.Success(messageSource.getMessage("success.analysis.delete.successfully", null, "Analysis was deleted successfully", locale));
		} catch (Exception e) {
			// return error message
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("failed.delete.analysis", null, "Analysis cannot be deleted!", locale));
		}
	}
}
