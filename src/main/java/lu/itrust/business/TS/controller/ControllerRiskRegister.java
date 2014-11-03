package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.helper.JsonMessage;
import lu.itrust.business.TS.data.basic.AnalysisRight;
import lu.itrust.business.TS.data.cssf.RiskRegisterItem;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceRiskRegister;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.task.WorkerComputeRiskRegister;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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

/**
 * ControllerRiskRegister.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Feb 17, 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/RiskRegister")
@Controller
public class ControllerRiskRegister {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceRiskRegister serviceRiskRegister;

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
	 * showRiskRegister: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping
	public String showRiskRegister(HttpSession session, Map<String, Object> model, Principal principal) throws Exception {

		// retrieve analysis ID
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");

		// load all actionplans from the selected analysis
		List<RiskRegisterItem> riskregister = serviceRiskRegister.getAllFromAnalysis(selected);

		// prepare model
		model.put("riskregister", riskregister);

		// return view
		return "analyses/singleAnalysis/components/riskregister";
	}

	/**
	 * section: <br>
	 * reload the section of the risk register
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public String section(Map<String, Object> model, HttpSession session, Principal principal) throws Exception {

		return showRiskRegister(session, model, principal);

	}

	// *****************************************************************
	// * compute risk register
	// *****************************************************************

	/**
	 * computeRiskRegister: <br>
	 * Description
	 * 
	 * @param session
	 * @param principal
	 * @param locale
	 * @param value
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Compute", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String computeRiskRegister(HttpSession session, Principal principal, Locale locale, @RequestBody String value) throws Exception {

		// prepare permission verifier
		PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(value);

		// retrieve analysis id to compute
		int analysisId = jsonNode.get("id").asInt();

		// verify if user is authorized to compute the risk register
		if (permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.CALCULATE_RISK_REGISTER)) {

			boolean reloadSection = session.getAttribute("selectedAnalysis") != null;

			WorkerComputeRiskRegister worker = new WorkerComputeRiskRegister(workersPoolManager, sessionFactory, serviceTaskFeedback, analysisId, reloadSection);

			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
				return JsonMessage.Error(messageSource.getMessage("failed.start.compute.actionplan", null, "Risk Register computation was failed", locale));

			// execute task
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.start.compute.riskregister", null, "Risk Register computation was started successfully", locale));

		} else {
			return JsonMessage.Success(messageSource.getMessage("error.permissiondenied", null, "Permission denied!", locale));
		}
	}
}