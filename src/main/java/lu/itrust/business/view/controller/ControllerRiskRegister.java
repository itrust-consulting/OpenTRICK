package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.cssf.RiskRegisterItem;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.JsonMessage;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAnalysisNorm;
import lu.itrust.business.service.ServiceAsset;
import lu.itrust.business.service.ServiceRiskRegister;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserAnalysisRight;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.WorkerComputeRiskRegister;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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
 * ControllerRiskRegister.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Feb 17, 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/RiskRegister")
@Controller
public class ControllerRiskRegister {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceRiskRegister serviceRiskRegister;

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping
	public String showRiskRegister(HttpSession session, Map<String, Object> model, Principal principal) throws Exception {

		// retrieve analysis ID
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");

		// load all actionplans from the selected analysis
		List<RiskRegisterItem> riskregister = serviceRiskRegister.loadAllFromAnalysis(selected);

		// prepare model
		model.put("riskregister", riskregister);

		// return view
		return "analysis/components/riskregister";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Map<String, Object> model, HttpSession session, Principal principal) throws Exception {

		// retrieve analysis ID
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");

		// load all actionplans from the selected analysis
		List<RiskRegisterItem> riskregister = serviceRiskRegister.loadAllFromAnalysis(selected);

		// prepare model
		model.put("riskregister", riskregister);

		// return view
		return "analysis/components/riskregister";

	}

	/**
	 * retrieveSingle: <br>
	 * Description
	 * 
	 * @param entryID
	 *            : The actionplanentry id
	 * @param model
	 *            : model to be used inside view
	 * @param session
	 *            : user session containing the selectedAnalysis id
	 * @param principal
	 *            : user principal (user of the session)
	 * @return a html formated table line (tr < td) containing the single requested entry (with
	 *         javascript)
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping(value = "/RetrieveSingleEntry/{entryID}", method = RequestMethod.GET, headers = "Accept=application/json")
	public String retrieveSingle(@PathVariable("entryID") int entryID, Map<String, Object> model, HttpSession session, Principal principal) throws Exception {

		// retrieve actionplan entry from the given entryID
		RiskRegisterItem riskregisteritem = serviceRiskRegister.get(entryID);

		// prepare model
		model.put("riskregisteritem", riskregisteritem);

		// return view
		return "analysis/components/riskregisteritem";

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
	@RequestMapping(value = "/Compute", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String computeRiskRegister(HttpSession session, Principal principal, Locale locale, @RequestBody String value) throws Exception {

		// prepare permission verifier
		PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceUserAnalysisRight);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(value);

		// retrieve analysis id to compute
		int analysisId = jsonNode.get("id").asInt();

		// verify if user is authorized to compute the risk register
		if (permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.CALCULATE_RISK_REGISTER)) {
			
			boolean reloadSection = session.getAttribute("selectedAnalysis")!=null;
			
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