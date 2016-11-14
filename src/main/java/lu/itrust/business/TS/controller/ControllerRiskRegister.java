package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeRiskRegister;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportRiskRegister;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportRiskSheet;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.helper.Estimation;
import lu.itrust.business.TS.model.cssf.helper.CSSFExportForm;
import lu.itrust.business.TS.model.cssf.helper.CSSFFilter;
import lu.itrust.business.TS.model.general.helper.ExportType;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;

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
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceAssessment serviceAssessment;

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	@RequestMapping
	public String showRiskRegister(HttpSession session, Map<String, Object> model, Principal principal) throws Exception {
		// retrieve analysis ID
		Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		// load all actionplans from the selected analysis
		// prepare model
		ValueFactory valueFactory = new ValueFactory(analysis.getParameters());
		model.put("estimations", Estimation.GenerateEstimation(analysis, valueFactory, Estimation.IdComparator()));
		model.put("type", analysis.getType());
		model.put("riskregister", analysis.getRiskRegisters());
		model.put("valueFactory", valueFactory);
		model.put("language", analysis.getLanguage().getAlpha2());
		// return view
		return "analyses/single/components/riskRegister/home";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	@RequestMapping(value = "/Compute", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@Deprecated
	public @ResponseBody String computeRiskRegister(HttpSession session, Principal principal) throws Exception {
		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Locale analysisLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha2());
		WorkerComputeRiskRegister worker = new WorkerComputeRiskRegister(workersPoolManager, sessionFactory, serviceTaskFeedback, analysisId, true);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", analysisLocale));
		// execute task
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.compute.riskregister", null, "Risk Register computation was started successfully", analysisLocale));
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/RiskSheet/Form/Export", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String exportFrom(@RequestParam(value = "type", defaultValue = "REPORT") ExportType type, HttpSession session, Model model, HttpServletRequest request,
			Principal principal) {
		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<? extends IParameter> impacts = serviceImpactParameter.findByAnalysisId(analysisId), probabilities = serviceLikelihoodParameter.findByAnalysisId(analysisId);

		model.addAttribute("parameters", serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_CSSF_NAME, analysisId).stream()
				.collect(Collectors.toMap(IParameter::getDescription, Function.identity())));

		model.addAttribute("owners", serviceAssessment.getDistinctOwnerByIdAnalysis(analysisId));

		model.addAttribute("type", type);

		model.addAttribute("impacts", impacts);

		model.addAttribute("probabilities", probabilities);

		return "analyses/single/components/riskRegister/form";
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/RiskSheet/Export", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object export(@RequestBody CSSFExportForm cssfExportForm, HttpSession session, HttpServletRequest request, Principal principal, Locale locale) {
		Map<String, String> errors = new HashMap<>();
		if (cssfExportForm.getFilter() == null)
			errors.put("filter", messageSource.getMessage("error.invalid.filter", null, "Filter cannot be load", locale));
		else {
			CSSFFilter cssfFilter = cssfExportForm.getFilter();
			if (cssfFilter.getImpact() < 0 || cssfFilter.getImpact() > Constant.DOUBLE_MAX_VALUE)
				errors.put("filter.impact", messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getProbability() < 0 || cssfFilter.getProbability() > Constant.DOUBLE_MAX_VALUE)
				errors.put("filter.probability", messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getDirect() < -2)
				errors.put("filter.direct", messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getIndirect() < -2)
				errors.put("filter.indirect", messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
			if (cssfFilter.getCia() < -2)
				errors.put("filter.cia", messageSource.getMessage("error.invalid.value", null, "Invalid value", locale));
		}

		if (!errors.isEmpty())
			return errors;

		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Locale analysisLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha2());
		Worker worker = new WorkerExportRiskSheet(cssfExportForm, workersPoolManager, sessionFactory, serviceTaskFeedback, request.getServletContext().getRealPath("/WEB-INF"),
				analysisId, principal.getName(), messageSource);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", analysisLocale));
		// execute task
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.export.risk_sheet", null, "Start to export risk sheet", analysisLocale));
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Export", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object export(HttpSession session, HttpServletRequest request, Principal principal, Locale locale) {
		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Locale analysisLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha2());
		Worker worker = new WorkerExportRiskRegister(analysisId, principal.getName(), request.getServletContext().getRealPath("/WEB-INF"), sessionFactory, workersPoolManager,
				serviceTaskFeedback, messageSource);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", analysisLocale));
		// execute task
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.export.risk_register", null, "Start to export risk register", analysisLocale));
	}

	@Value("${app.settings.risk_sheet.french.template.name}")
	public void setRiskSheetFrTemplate(String template) {
		WorkerExportRiskSheet.FR_TEMPLATE = template;
	}

	@Value("${app.settings.risk_sheet.english.template.name}")
	public void setRiskSheetEnTemplate(String template) {
		WorkerExportRiskSheet.ENG_TEMPLATE = template;
	}

	@Value("${app.settings.risk_regsiter.french.template.name}")
	public void setRiskRegisterFrTemplate(String template) {
		WorkerExportRiskRegister.FR_TEMPLATE = template;
	}

	@Value("${app.settings.risk_regsiter.english.template.name}")
	public void setRiskRegisterEnTemplate(String template) {
		WorkerExportRiskRegister.ENG_TEMPLATE = template;
	}
}