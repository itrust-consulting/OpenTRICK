/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerCreateAnalysisProfile;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceRole;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private ServiceRole serviceRole;

	@Autowired
	private MessageSource messageSource;

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	@RequestMapping("/Add/{analysisId}")
	public String createProfile(@PathVariable int analysisId, Model model, Principal principal) throws Exception {
		List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(analysisId);
		model.addAttribute("analysisStandards", analysisStandards);
		model.addAttribute("id", analysisId);
		return "analyses/all/forms/createProfile";
	}

	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody Map<String, String> saveProfile(@RequestBody String value, Principal principal, Locale locale) throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {

			PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);

			// retrieve analysis id to compute
			int analysisId = jsonNode.get("id").asInt();

			if (analysisId == -1 || permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.READ)
				|| serviceUser.hasRole(serviceUser.get(principal.getName()), serviceRole.getByName(RoleType.ROLE_CONSULTANT.name()))) {

				String name = jsonNode.get("description").asText();

				List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(analysisId);

				List<Integer> standards = new ArrayList<Integer>();

				for (AnalysisStandard standard : analysisStandards) {

					boolean addstandard = jsonNode.get("standard_" + standard.getStandard().getId()).asBoolean();
					if (addstandard)
						standards.add(standard.getStandard().getId());

				}

				if (StringUtils.isEmpty(name)) {
					errors.put("description", messageSource.getMessage("error.analysis_profile.empty_description", null, "Description cannot be empty", locale));
					return errors;
				}else if(serviceAnalysis.isProfileNameInUsed(name)){
					errors.put("description", messageSource.getMessage("error.analysis_profile.name_in_used", null, "Another analysis profile with the same description already exists", locale));
					return errors;
				}

				Worker worker = new WorkerCreateAnalysisProfile(serviceTaskFeedback, sessionFactory, workersPoolManager, analysisId, name, standards, principal.getName());
				if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {
					executor.execute(worker);
					errors.put("taskid", String.valueOf(worker.getId()));
				} else
					errors.put("analysisprofile", messageSource.getMessage("failed.analysis.duplication.start", null, "Error starting profile creation task!", locale));
				return errors;
			} else
				throw new AccessDeniedException(messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
		} catch (Exception e) {
			e.printStackTrace();
			errors.put("analysisprofile", e.getMessage());
		}

		return errors;

	}

	@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
	@RequestMapping("/Section")
	public String section(HttpServletRequest request, Principal principal, Model model) throws Exception {
		model.addAttribute("analyses", serviceAnalysis.getAllProfiles());
		model.addAttribute("login", principal.getName());
		return "knowledgebase/analysis/analyses";
	}
}
