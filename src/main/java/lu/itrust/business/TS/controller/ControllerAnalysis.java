package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.ALLOWED_TICKETING;
import static lu.itrust.business.TS.constants.Constant.ANALYSIS_TASK_ID;
import static lu.itrust.business.TS.constants.Constant.CURRENT_CUSTOMER;
import static lu.itrust.business.TS.constants.Constant.FILTER_ANALYSIS_NAME;
import static lu.itrust.business.TS.constants.Constant.LAST_SELECTED_ANALYSIS_NAME;
import static lu.itrust.business.TS.constants.Constant.LAST_SELECTED_CUSTOMER_ID;
import static lu.itrust.business.TS.constants.Constant.OPEN_MODE;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_SINGLE_NAME;
import static lu.itrust.business.TS.constants.Constant.ROLE_MIN_CONSULTANT;
import static lu.itrust.business.TS.constants.Constant.ROLE_MIN_USER;
import static lu.itrust.business.TS.constants.Constant.SELECTED_ANALYSIS;
import static lu.itrust.business.TS.constants.Constant.SELECTED_ANALYSIS_LANGUAGE;
import static lu.itrust.business.TS.constants.Constant.SOA_THRESHOLD;
import static lu.itrust.business.TS.constants.Constant.TICKETING_NAME;
import static lu.itrust.business.TS.constants.Constant.TICKETING_URL;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerAnalysisImport;
import lu.itrust.business.TS.asynchronousWorkers.WorkerCreateAnalysisVersion;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportAnalysis;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportWordReport;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.CustomerManager;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceRole;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.AbstractWordExporter;
import lu.itrust.business.TS.exportation.ExportQualitativeReport;
import lu.itrust.business.TS.exportation.ExportQuantitativeReport;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.helper.ActionPlanComputation;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.helper.Estimation;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.general.TSSetting;
import lu.itrust.business.TS.model.general.TSSettingName;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.iteminformation.helper.ComparatorItemInformation;
import lu.itrust.business.TS.model.parameter.IProbabilityParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.helper.StandardComparator;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureManager;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.model.ticketing.TicketingProject;
import lu.itrust.business.TS.model.ticketing.builder.Client;
import lu.itrust.business.TS.model.ticketing.builder.ClientBuilder;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.validator.HistoryValidator;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;

/**
 * ControllerAnalysis.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 22, 2013
 */
@PreAuthorize(ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis")
public class ControllerAnalysis {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceRole serviceRole;

	@Autowired
	private CustomerManager customerManager;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private ServiceTSSetting serviceTSSetting;

	@Value("${app.settings.report.quantitative.french.template.name}")
	private String frenchQuantitativeReportName;

	@Value("${app.settings.report.quantitative.english.template.name}")
	private String englishQuantitativeReportName;

	@Value("${app.settings.report.qualitative.french.template.name}")
	private String frenchQualitativeReportName;

	@Value("${app.settings.report.qualitative.english.template.name}")
	private String englishQualitativeReportName;

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param principal
	 * @param model
	 * @param session
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String home(Principal principal, Model model, HttpSession session, Locale locale, HttpServletRequest request) throws Exception {
		// retrieve analysisId if an analysis was already selected
		Integer selected = (Integer) session.getAttribute(SELECTED_ANALYSIS);
		OpenMode openMode = OpenMode.parseOrDefault(session.getAttribute(OPEN_MODE));
		// check if an analysis is selected
		if (selected != null)
			return String.format("redirect:/Analysis/%d/Select?open=%s", selected, openMode.getValue());
		else
			return "redirect:/Analysis/All";
	}

	@RequestMapping("/All")
	public String AllAnalysis(Model model, Principal principal, HttpSession session) throws Exception {
		session.removeAttribute(OPEN_MODE);
		session.removeAttribute(SELECTED_ANALYSIS);
		session.removeAttribute(SELECTED_ANALYSIS_LANGUAGE);
		return LoadUserAnalyses(session, principal, model);
	}

	/**
	 * selectAnalysis: <br>
	 * selects or deselects an analysis
	 * 
	 * @param principal
	 * @param analysisId
	 * @param model
	 * @param session
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{analysisId}/Select")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String selectAnalysis(Model model, Principal principal, @PathVariable("analysisId") Integer analysisId, @RequestParam(value = "open", defaultValue = "edit") String open,
			HttpSession session, Locale locale) throws Exception {
		// select the analysis
		OpenMode mode = OpenMode.parseOrDefault(open);
		session.setAttribute(SELECTED_ANALYSIS, analysisId);
		session.setAttribute(OPEN_MODE, mode);
		PermissionEvaluatorImpl permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);
		Analysis analysis = serviceAnalysis.get(analysisId);
		if (analysis == null)
			throw new ResourceNotFoundException(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		User user = serviceUser.get(principal.getName());
		ValueFactory valueFactory = new ValueFactory(analysis.getParameters());
		Boolean readOnly = OpenMode.isReadOnly(mode);
		List<Standard> standards = null;
		boolean hasMaturity = false;
		boolean hasPermission = analysis.isProfile() ? user.isAutorised(RoleType.ROLE_CONSULTANT)
				: readOnly ? true : permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.MODIFY);
		if (hasPermission) {

			Collections.sort(analysis.getItemInformations(), new ComparatorItemInformation());
			standards = analysis.getAnalysisStandards().stream().map(analysisStandard -> analysisStandard.getStandard()).sorted(new StandardComparator())
					.collect(Collectors.toList());
			Map<String, List<Measure>> measuresByStandard = mapMeasures(analysis.getAnalysisStandards());
			hasMaturity = measuresByStandard.containsKey(Constant.STANDARD_MATURITY);
			model.addAttribute("soaThreshold", analysis.getParameter(PARAMETERTYPE_TYPE_SINGLE_NAME, SOA_THRESHOLD, 100.0));
			model.addAttribute("soas", analysis.getAnalysisStandards().stream().filter(AnalysisStandard::isSoaEnabled).collect(
					Collectors.toMap(analysisStandard -> analysisStandard.getStandard(), analysisStandard -> measuresByStandard.get(analysisStandard.getStandard().getLabel()))));
			model.addAttribute("measuresByStandard", measuresByStandard);
			model.addAttribute("show_uncertainty", analysis.isUncertainty());
			model.addAttribute("type", analysis.getType());
			model.addAttribute("standards", standards);
			model.addAttribute("hasMaturity", hasMaturity);
			if (analysis.getType() == AnalysisType.QUALITATIVE)
				model.addAttribute("estimations", Estimation.GenerateEstimation(analysis, valueFactory, Estimation.IdComparator()));
			if (hasMaturity)
				model.addAttribute("effectImpl27002",
						MeasureManager.ComputeMaturiyEfficiencyRate(measuresByStandard.get(Constant.STANDARD_27002), measuresByStandard.get(Constant.STANDARD_MATURITY),
								analysis.findByGroup(Constant.PARAMETER_CATEGORY_SIMPLE, Constant.PARAMETER_CATEGORY_MATURITY), true, valueFactory));

			model.addAttribute("standardChapters", spliteMeasureByChapter(measuresByStandard));
			model.addAttribute("valueFactory", valueFactory);
			model.addAttribute("open", mode);
			model.addAttribute("analysis", analysis);
			model.addAttribute("login", user.getLogin());
			loadUserSettings(principal, model, user);

			/**
			 * Log
			 */
			TrickLogManager.Persist(LogType.ANALYSIS, readOnly ? "log.open.analysis" : mode == OpenMode.EDIT ? "log.edit.analysis" : "log.edit.analysis.measure",
					String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), user.getLogin(), readOnly ? LogAction.OPEN : LogAction.EDIT,
					analysis.getIdentifier(), analysis.getVersion());
		} else {
			TrickLogManager.Persist(LogLevel.ERROR, LogType.ANALYSIS, "log.analysis.access_deny",
					String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), user.getLogin(), LogAction.DENY_ACCESS, analysis.getIdentifier(),
					analysis.getVersion());
			throw new AccessDeniedException(messageSource.getMessage("error.not_authorized", null, "Insufficient permissions!", locale));
		}
		return "analyses/single/home";
	}

	private Map<String, Map<String, List<Measure>>> spliteMeasureByChapter(Map<String, List<Measure>> measuresByStandard) {
		Map<String, Map<String, List<Measure>>> mapper = new LinkedHashMap<>();
		measuresByStandard.forEach((standard, measures) -> {
			Map<String, List<Measure>> chapters = new LinkedHashMap<>();
			measures.forEach(measure -> {
				String chapter = ActionPlanComputation.extractMainChapter(measure.getMeasureDescription().getReference());
				List<Measure> measuresByChpater = chapters.get(chapter);
				if (measuresByChpater == null)
					chapters.put(chapter, measuresByChpater = new LinkedList<Measure>());
				measuresByChpater.add(measure);
			});

			mapper.put(standard, chapters);
		});
		return mapper;
	}

	/**
	 * mapMeasures: <br>
	 * Description
	 * 
	 * @param standards
	 * @return
	 */
	private Map<String, List<Measure>> mapMeasures(List<AnalysisStandard> standards) {
		Comparator<Measure> comparator = new MeasureComparator();
		Map<String, List<Measure>> measuresmap = new LinkedHashMap<String, List<Measure>>();
		for (AnalysisStandard standard : standards) {
			Collections.sort(standard.getMeasures(), comparator);
			measuresmap.put(standard.getStandard().getLabel(), standard.getMeasures());
		}
		return measuresmap;
	}

	// *****************************************************************
	// * reload customer section by pageindex
	// *****************************************************************

	@RequestMapping("/Section")
	public String section(HttpServletRequest request, Principal principal, Model model) throws Exception {
		return LoadUserAnalyses(request.getSession(), principal, model);
	}

	private String LoadUserAnalyses(HttpSession session, Principal principal, Model model) throws Exception {
		List<String> names = null;
		Integer customer = (Integer) session.getAttribute(CURRENT_CUSTOMER);
		List<Customer> customers = serviceCustomer.getAllNotProfileOfUser(principal.getName());
		String nameFilter = (String) session.getAttribute(FILTER_ANALYSIS_NAME);
		User user = null;
		if (customer == null || nameFilter == null) {
			user = serviceUser.get(principal.getName());
			if (user == null)
				throw new AccessDeniedException("Access denied");
			if (customer == null) {
				customer = user.getInteger(LAST_SELECTED_CUSTOMER_ID);
				// check if the current customer is set -> no
				if (customer == null && !customers.isEmpty()) {
					// use first customer as selected customer
					user.setSetting(LAST_SELECTED_CUSTOMER_ID, customer = customers.get(0).getId());
					serviceUser.saveOrUpdate(user);
				}
				session.setAttribute(CURRENT_CUSTOMER, customer);
			}
			if (nameFilter == null) {
				nameFilter = user.getSetting(LAST_SELECTED_ANALYSIS_NAME);
				if (nameFilter == null && customer != null) {
					names = serviceAnalysis.getNamesByUserAndCustomerAndNotEmpty(principal.getName(), customer);
					if (!names.isEmpty()) {
						user.setSetting(LAST_SELECTED_ANALYSIS_NAME, nameFilter = names.get(0));
						serviceUser.saveOrUpdate(user);
					}
				}
			}
		}

		if (customer != null) {
			if (names == null)
				names = serviceAnalysis.getNamesByUserAndCustomerAndNotEmpty(principal.getName(), customer);
			// load model with objects by the selected customer
			if (nameFilter == null || nameFilter.equalsIgnoreCase("ALL") || !names.contains(nameFilter))
				model.addAttribute("analyses", serviceAnalysis.getAllNotEmptyFromUserAndCustomer(principal.getName(), customer));
			else
				model.addAttribute("analyses", serviceAnalysis.getAllByUserAndCustomerAndNameAndNotEmpty(principal.getName(), customer, nameFilter));
		}
		loadUserSettings(principal, model, user);
		model.addAttribute("names", names);
		model.addAttribute("analysisSelectedName", nameFilter);
		model.addAttribute("customer", customer);
		model.addAttribute("customers", customers);
		model.addAttribute("login", principal.getName());
		return "analyses/all/home";
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
	 * reload customer section by page index
	 * 
	 * @param customer
	 * @param pageIndex
	 * @param session
	 * @param principal
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/DisplayByCustomer/{idCustomer}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String section(@PathVariable("idCustomer") Integer idCustomer, @RequestBody String name, HttpSession session, Principal principal, Model model) throws Exception {
		if (StringUtils.isEmpty(name))
			name = "ALL";
		session.setAttribute(CURRENT_CUSTOMER, idCustomer);
		session.setAttribute(FILTER_ANALYSIS_NAME, name);
		List<String> names = serviceAnalysis.getNamesByUserAndCustomerAndNotEmpty(principal.getName(), idCustomer);
		if (name.equalsIgnoreCase("ALL") || !names.contains(name))
			model.addAttribute("analyses", serviceAnalysis.getAllNotEmptyFromUserAndCustomer(principal.getName(), idCustomer));
		else
			model.addAttribute("analyses", serviceAnalysis.getAllByUserAndCustomerAndNameAndNotEmpty(principal.getName(), idCustomer, name));
		model.addAttribute("analysisSelectedName", name);
		model.addAttribute("customer", idCustomer);
		model.addAttribute("names", names);
		model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		model.addAttribute("login", principal.getName());
		User user = serviceUser.get(principal.getName());
		if (user == null)
			return "redirect:/Logout";
		loadUserSettings(principal, model, user);
		user.setSetting(LAST_SELECTED_ANALYSIS_NAME, name);
		user.setSetting(LAST_SELECTED_CUSTOMER_ID, idCustomer);
		serviceUser.saveOrUpdate(user);

		return "analyses/all/home";
	}

	// *****************************************************************
	// * select or deselect analysis
	// *****************************************************************

	/**
	 * update: <br>
	 * Description
	 * 
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Update/ALE", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String update(HttpSession session, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			assessmentAndRiskProfileManager.updateAssetALE(analysis, null);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.ale.update", null, "ALE was successfully updated", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.analysis.ale.update", null, "ALE cannot be updated", locale));
		}
	}

	/**
	 * selectAnalysis: <br>
	 * selects or deselects an analysis
	 * 
	 * @param principal
	 * @param analysisId
	 * @param model
	 * @param session
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{analysisId}/SelectOnly", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody boolean selectOnly(Principal principal, @PathVariable("analysisId") Integer analysisId,
			@RequestParam(value = "open", defaultValue = "read-only") String open, HttpSession session) throws Exception {
		// select the analysis
		session.setAttribute(SELECTED_ANALYSIS, analysisId);
		session.setAttribute(OPEN_MODE, OpenMode.parseOrDefault(open));
		return session.getAttribute(SELECTED_ANALYSIS) == analysisId;
	}

	/**
	 * selectAnalysis: <br>
	 * selects or deselects an analysis
	 * 
	 * @param principal
	 * @param analysisId
	 * @param model
	 * @param session
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Deselect")
	public String DeselectAnalysis(HttpSession session) throws Exception {
		// retrieve selected analysis
		session.removeAttribute(OPEN_MODE);
		Integer integer = (Integer) session.getAttribute(SELECTED_ANALYSIS);
		if (integer != null) {
			session.removeAttribute(SELECTED_ANALYSIS);
			if (serviceAnalysis.isProfile(integer))
				return "redirect:/KnowledgeBase";
			else
				return "redirect:/Analysis";
		}
		return "redirect:/Home";

	}

	// *****************************************************************
	// * request create new analysis
	// *****************************************************************

	// *****************************************************************
	// * request edit analysis
	// *****************************************************************

	/**
	 * editAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Edit/{analysisId}")
	public String requestEditAnalysis(Principal principal, @PathVariable("analysisId") Integer analysisId, Map<String, Object> model, Locale locale) throws Exception {
		// retrieve analysis
		Analysis analysis = serviceAnalysis.get(analysisId);
		if (analysis == null)
			throw new ResourceNotFoundException(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", locale));
		// prepare permission evaluator
		PermissionEvaluatorImpl permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);

		if (permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.MODIFY)) {

			// add languages
			model.put("languages", serviceLanguage.getAll());

			// add customers of user
			model.put("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));

			model.put("types", AnalysisType.values());

			// add the analysis object
			model.put("analysis", analysis);

			return "analyses/all/forms/editAnalysis";
		}

		throw new AccessDeniedException(messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
	}

	// *****************************************************************
	// * save or update analysis object
	// *****************************************************************

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) {
		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {
			// prepare permission verifier
			PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);

			// retrieve analysis id to compute
			int analysisId = jsonNode.get("id").asInt();

			// check if it is a new analysis or the user is authorized to modify
			// the analysis
			if (analysisId == -1 || permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.MODIFY)
					|| serviceUser.hasRole(serviceUser.get(principal.getName()), serviceRole.getByName(RoleType.ROLE_CONSULTANT.name()))) {

				// create/update analysis object and set access rights
				buildAnalysis(errors, serviceUser.get(principal.getName()), value, locale, null);
			} else
				// throw error
				throw new AccessDeniedException(messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
		} catch (TrickException e) {
			errors.put("owner", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			errors.put("owner", messageSource.getMessage("error.user.not_found", null, "User cannot be found", locale));
			TrickLogManager.Persist(e);
		}
		return errors;
	}

	// *****************************************************************
	// * set default profile
	// *****************************************************************

	@RequestMapping(value = "/SetDefaultProfile/{analysisId}", method = RequestMethod.POST)
	@PreAuthorize(ROLE_MIN_CONSULTANT)
	public @ResponseBody boolean setDefaultProfile(Principal principal, @PathVariable("analysisId") Integer analysisId, HttpSession session) throws Exception {

		Analysis analysis = serviceAnalysis.get(analysisId);

		Analysis currentProfileanalysis = serviceAnalysis.getDefaultProfile();

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

	// *****************************************************************
	// * delete analysis
	// *****************************************************************

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String deleteAnalysis(@PathVariable("analysisId") int analysisId, RedirectAttributes attributes, Locale locale, Principal principal, HttpSession session)
			throws Exception {
		try {

			Analysis analysis = serviceAnalysis.getDefaultProfile();

			if (analysis != null && analysis.getId() == analysisId)
				return JsonMessage.Error(messageSource.getMessage("error.profile.delete.failed", null, "Default profile cannot be deleted!", locale));

			customDelete.deleteAnalysis(analysisId, principal.getName());

			Integer selectedAnalysis = (Integer) session.getAttribute(SELECTED_ANALYSIS);

			if (selectedAnalysis != null && selectedAnalysis == analysisId)
				session.removeAttribute(SELECTED_ANALYSIS);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.analysis.delete.successfully", null, "Analysis was deleted successfully", locale));
		} catch (Exception e) {
			// return error message
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("failed.delete.analysis", null, "Analysis cannot be deleted!", locale));
		}
	}

	// *****************************************************************
	// * create new version of analysis
	// *****************************************************************

	/**
	 * addHistory: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{analysisId}/NewVersion", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userOrOwnerIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String addHistory(@PathVariable("analysisId") Integer analysisId, Map<String, Object> model, Principal principal, HttpSession session) throws Exception {
		// retrieve user
		User user = serviceUser.get(principal.getName());
		// add data to model
		model.put("oldVersion", serviceAnalysis.getVersionOfAnalysis(analysisId));
		model.put("analysisId", analysisId);
		model.put("author", user.getFirstName() + " " + user.getLastName());
		return "analyses/all/forms/newVersion";
	}

	/**
	 * createNewVersion: <br>
	 * Description
	 * 
	 * @param history
	 * @param analysisId
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Duplicate/{analysisId}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userOrOwnerIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Map<String, String> createNewVersion(@RequestBody String value, BindingResult result, @PathVariable int analysisId, Principal principal, Locale locale)
			throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			// retrieve analysis object
			Analysis analysis = serviceAnalysis.get(analysisId);

			// check if object is not null
			if (analysis == null)
				errors.put("analysis", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", locale));

			String lastVersion = serviceAnalysis.getAllVersion(analysis.getIdentifier()).stream().sorted((v0, v1) -> {
				return NaturalOrderComparator.compareTo(v1, v0);
			}).findFirst().get();

			HistoryValidator validator = (HistoryValidator) serviceDataValidation.findByClass(History.class);

			if (validator == null)
				serviceDataValidation.register(validator = new HistoryValidator());

			History history = new History();

			JsonNode jsonNode = new ObjectMapper().readTree(value);
			String author = jsonNode.get("author").asText();
			Date date = new Date(System.currentTimeMillis());
			String version = jsonNode.get("version").asText();
			String comment = jsonNode.get("comment").asText();

			String error = validator.validate(history, "author", author);
			if (error != null)
				errors.put("author", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				history.setAuthor(author);

			error = validator.validate(history, "version", version);
			if (error != null)
				errors.put("version", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				if (NaturalOrderComparator.compareTo(lastVersion, version) >= 0)
					errors.put("version", messageSource.getMessage("error.history.version.invalid", new String[] { lastVersion },
							String.format("Version has to be bigger than last %s", lastVersion), locale));
				else
					history.setVersion(version);
			}

			error = validator.validate(history, "comment", comment);
			if (error != null)
				errors.put("comment", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				history.setComment(comment);

			// update date of history object
			history.setDate(date);

			if (!errors.isEmpty())
				// return error on failure
				return errors;

			Worker worker = new WorkerCreateAnalysisVersion(analysisId, history, principal.getName(), serviceTaskFeedback, sessionFactory, workersPoolManager);
			// register worker to tasklist
			if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {
				// execute task
				executor.execute(worker);
				errors.put(ANALYSIS_TASK_ID, String.valueOf(worker.getId()));
			} else
				errors.put("analysis", messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			errors.put("analysis", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			errors.put("analysis", messageSource.getMessage("error.analysis.duplicate.unknown", null, "An unknown error occurred during duplication!", locale));
		}

		return errors;
	}

	// *****************************************************************
	// * import form and import action
	// *****************************************************************

	/**
	 * importAnalysis: <br>
	 * Description
	 * 
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Import")
	public String importAnalysis(Principal principal, Map<String, Object> model) throws Exception {

		// add the customers of the user to the data model
		model.put("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		return "analyses/importAnalysis";
	}

	/**
	 * importAnalysisSave: <br>
	 * Description
	 * 
	 * @param principal
	 * @param customerId
	 * @param request
	 * @param file
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Import/Execute", method = RequestMethod.POST)
	public Object importAnalysisSave(Principal principal, @RequestParam(value = "customerId") Integer customerId, HttpServletRequest request,
			@RequestParam(value = "file") MultipartFile file, final RedirectAttributes attributes, Locale locale) throws Exception {

		User user = serviceUser.get(principal.getName());
		if (user == null)
			return "redirect:/Logout";

		// retrieve the customer
		Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customerId);

		if (customer == null) {
			attributes.addFlashAttribute("error", messageSource.getMessage("error.customer.not_found", null, "Customer cannot be found", locale));
			throw new ResourceNotFoundException((String) attributes.getFlashAttributes().get("error"));
		}

		// if the customer or the file are not correct
		if (customer == null || file.isEmpty()) {
			attributes.addFlashAttribute("errors", messageSource.getMessage("error.customer_or_file.import.analysis", null, "Customer or file are not set or empty!", locale));
			return "analyses/importAnalysis";
		}

		// set selected customer, the selected customer of the analysis
		request.getSession().setAttribute(CURRENT_CUSTOMER, customer.getId());

		if (customerId != user.getInteger(LAST_SELECTED_CUSTOMER_ID)) {
			user.setSetting(LAST_SELECTED_CUSTOMER_ID, String.valueOf(customerId));
			serviceUser.saveOrUpdate(user);
		}

		// the file to import
		File importFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime());

		// transfer form file to java file
		file.transferTo(importFile);

		// create worker
		Worker worker = new WorkerAnalysisImport(workersPoolManager, sessionFactory, serviceTaskFeedback, importFile, customer.getId(), principal.getName());

		// register worker to tasklist
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
			// execute task
			executor.execute(worker);
		else
			// prepare error return message
			// add return message
			attributes.addFlashAttribute("error", messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		return "redirect:/Analysis/Import";
	}

	// *****************************************************************
	// * export and download
	// *****************************************************************

	/**
	 * exportAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param principal
	 * @param request
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Export/{analysisId}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String exportAnalysis(@PathVariable int analysisId, Principal principal, HttpServletRequest request, Locale locale) throws Exception {

		// create worker
		Worker worker = new WorkerExportAnalysis(serviceTaskFeedback, sessionFactory, principal, request.getServletContext(), workersPoolManager, analysisId);

		// register worker
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {

			// execute task
			executor.execute(worker);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.start.export.analysis", null, "Analysis export was started successfully", locale));
		} else

			// return error message
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
	}

	/**
	 * computeRiskRegister: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Export/Report/{analysisId}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody String exportReport(@PathVariable Integer analysisId, HttpServletRequest request, Principal principal, Locale locale) {
		try {
			AnalysisType analysisType = serviceAnalysis.getAnalysisTypeById(analysisId);
			AbstractWordExporter exportAnalysisReport = analysisType == AnalysisType.QUANTITATIVE
					? new ExportQuantitativeReport(messageSource, serviceTaskFeedback, request.getServletContext().getRealPath(""))
					: new ExportQualitativeReport(messageSource, serviceTaskFeedback, request.getServletContext().getRealPath(""));
			switch (serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().toLowerCase()) {
			case "fra":
				locale = Locale.FRENCH;
				exportAnalysisReport.setReportName(analysisType == AnalysisType.QUANTITATIVE? frenchQuantitativeReportName : frenchQualitativeReportName );
				break;
			default:
				locale = Locale.ENGLISH;
				exportAnalysisReport.setReportName(analysisType == AnalysisType.QUANTITATIVE ? englishQuantitativeReportName : englishQualitativeReportName );
			}
			Worker worker = new WorkerExportWordReport(analysisId, principal.getName(), sessionFactory, exportAnalysisReport, workersPoolManager);
			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
				return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.report.exporting", null, "Exporting report", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
	}

	@RequestMapping(value = "/Export/Raw-Action-plan/{idAnalysis}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportRawActionPlan(@PathVariable Integer idAnalysis, Principal principal, HttpServletResponse response) throws Exception {
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		exportRawActionPlan(response, analysis, principal.getName(), new Locale(analysis.getLanguage().getAlpha2()));
	}

	@RequestMapping(value = "/Ticketing/UnLink", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String unlinkToProject(@RequestBody List<Integer> ids, Model model, Principal principal, Locale locale) {
		User user = serviceUser.get(principal.getName());
		if (!loadUserSettings(principal, model, user))
			throw new ResourceNotFoundException();
		String name = (String) model.asMap().get(TICKETING_NAME);
		ids.forEach(idAnalysis -> {
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis != null && analysis.hasProject() && analysis.isUserAuthorized(user, AnalysisRight.ALL)) {
				analysis.setProject(null);
				serviceAnalysis.saveOrUpdate(analysis);
			}
		});
		return JsonMessage.Success(ids.size() > 1
				? messageSource.getMessage("sucess.analyses.unlink.from.project", new String[] { name }, String.format("Analyses has been successfully unlinked from %s", name),
						locale)
				: messageSource.getMessage("sucess.analysis.unlink.from.project", new String[] { name }, String.format("Analysis has been successfully unlinked from %s", name),
						locale));
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).ALL)")
	@RequestMapping(value = "/{idAnalysis}/Ticketing/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String loadProject(@PathVariable Integer idAnalysis, Model model, Principal principal, RedirectAttributes attributes, Locale locale) {
		Client client = null;
		try {
			if (!loadUserSettings(principal, model, null))
				throw new ResourceNotFoundException();
			client = buildClient(principal.getName());
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			String idProject = serviceAnalysis.getProjectIdByIdentifier(analysis.getIdentifier());
			List<TicketingProject> projects = null;
			if (idProject != null) {
				TicketingProject project = client.findProjectById(idProject);
				if (project != null)
					(projects = new LinkedList<>()).add(project);
			} else {
				Map<String, Boolean> mapper = serviceAnalysis.getAllProjectIds().stream().collect(Collectors.toMap(Function.identity(), key -> true));
				(projects = client.findProjects()).removeIf(id -> mapper.containsKey(id.getId()));
			}
			model.addAttribute("projects", projects);
			model.addAttribute("analysis", analysis);
			return String.format("analyses/all/forms/ticketing_%s_link", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			return "redirect:/Error";
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			attributes.addAttribute("error", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			return "redirect:/Error";
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).ALL)")
	@RequestMapping(value = "/{idAnalysis}/Ticketing/Link", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String linkToProject(@PathVariable Integer idAnalysis, @RequestBody String idProject, Principal principal, Locale locale) {
		if (!loadUserSettings(principal, null, null))
			throw new ResourceNotFoundException();
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		if (StringUtils.isEmpty(idProject))
			return JsonMessage.Error(messageSource.getMessage("error.project.not_found", null, "Project cannot be found", locale));
		String OldProject = serviceAnalysis.getProjectIdByIdentifier(analysis.getIdentifier());
		if (!(OldProject == null || OldProject.equals(idProject)))
			return JsonMessage.Error(
					messageSource.getMessage("error.analysis.linked.to.another.project", null, "Another project is already linked to another version of this analysis", locale));
		analysis.setProject(idProject);
		serviceAnalysis.saveOrUpdate(analysis);
		return JsonMessage.Success(messageSource.getMessage("success.link.analysis.project", null, "Analysis has been successfully linked to project", locale));

	}

	private void exportRawActionPlan(HttpServletResponse response, Analysis analysis, String username, Locale locale) throws IOException {
		XSSFWorkbook workbook = null;
		try {
			List<IProbabilityParameter> expressionParameters = analysis.getExpressionParameters();
			int lineIndex = 0;
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("label.raw.action_plan", null, "Raw action plan", locale));
			XSSFRow row = sheet.getRow(0);
			if (row == null)
				row = sheet.createRow(0);

			int colCount = analysis.getType() == AnalysisType.QUANTITATIVE ? 21 : 19;

			for (int i = 0; i < colCount; i++) {
				if (row.getCell(i) == null)
					row.createCell(i, CellType.STRING);
			}
			addActionPLanHeader(row, analysis.getType(), locale);
			for (ActionPlanEntry actionPlanEntry : analysis.getActionPlan(ActionPlanMode.APPN)) {
				row = sheet.getRow(++lineIndex);
				if (row == null)
					row = sheet.createRow(lineIndex);
				writeActionPLanData(row, colCount, actionPlanEntry, analysis.getType(), expressionParameters, locale);
			}
			response.setContentType("xlsx");
			// set response header with location of the filename
			response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("STA_%s_V%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");

			workbook.write(response.getOutputStream());

			// Log
			TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.raw.action_plan",
					String.format("Analysis: %s, version: %s, type: Raw action plan", analysis.getIdentifier(), analysis.getVersion()), username, LogAction.EXPORT,
					analysis.getIdentifier(), analysis.getVersion());

		} finally {
			try {
				if (workbook != null)
					workbook.close();
			} catch (IOException e) {
				TrickLogManager.Persist(e);
				System.err.println("Close document: " + e.getMessage());
			}
		}
	}

	private void writeActionPLanData(XSSFRow row, int colCount, ActionPlanEntry actionPlanEntry, AnalysisType type, List<IProbabilityParameter> expressionParameters,
			Locale locale) {
		for (int i = 0; i < colCount; i++) {
			if (row.getCell(i) == null)
				row.createCell(i, i < 7 ? CellType.STRING : CellType.NUMERIC);
		}
		int colIndex = 0;
		Measure measure = actionPlanEntry.getMeasure();
		MeasureDescriptionText descriptionText = measure.getMeasureDescription().getMeasureDescriptionTextByAlpha3(locale.getISO3Language());
		row.getCell(colIndex).setCellValue(measure.getAnalysisStandard().getStandard().getLabel());
		row.getCell(++colIndex).setCellValue(measure.getMeasureDescription().getReference());
		row.getCell(++colIndex).setCellValue(descriptionText.getDomain());
		row.getCell(++colIndex).setCellValue(measure.getStatus());
		row.getCell(++colIndex).setCellValue(measure.getComment());
		row.getCell(++colIndex).setCellValue(measure.getToDo());
		row.getCell(++colIndex).setCellValue(measure.getResponsible());
		row.getCell(++colIndex).setCellValue(measure.getImplementationRateValue(expressionParameters));
		row.getCell(++colIndex).setCellValue(measure.getInternalWL());
		row.getCell(++colIndex).setCellValue(measure.getExternalWL());
		row.getCell(++colIndex).setCellValue(measure.getInvestment() * 0.001);
		row.getCell(++colIndex).setCellValue(measure.getLifetime());
		row.getCell(++colIndex).setCellValue(measure.getInternalMaintenance());
		row.getCell(++colIndex).setCellValue(measure.getExternalMaintenance());
		row.getCell(++colIndex).setCellValue(measure.getRecurrentInvestment() * 0.001);
		row.getCell(++colIndex).setCellValue(measure.getCost() * 0.001);
		row.getCell(++colIndex).setCellValue(measure.getPhase().getNumber());
		if (type == AnalysisType.QUALITATIVE)
			row.getCell(++colIndex).setCellValue(actionPlanEntry.getRiskCount());
		else {
			row.getCell(++colIndex).setCellValue(actionPlanEntry.getTotalALE() * 0.001);
			row.getCell(++colIndex).setCellValue(actionPlanEntry.getDeltaALE() * 0.001);
			row.getCell(++colIndex).setCellValue(actionPlanEntry.getROI() * 0.001);
		}
	}

	private void addActionPLanHeader(XSSFRow row, AnalysisType type, Locale locale) {
		int colIndex = 0;
		row.getCell(colIndex).setCellValue(messageSource.getMessage("report.action_plan.norm", null, "Stds", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.reference", null, "Ref.", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.domain", null, "Domain", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.status", null, "ST", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.comment", null, "Comment", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.to_do", null, "To Do", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.responsible", null, "Resp.", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.implementation_rate", null, "IR(%)", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.internal.workload", null, "IS(md)", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.external.workload", null, "ES(md)", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.investment", null, "INV(k€)", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.life_time", null, "LT(y)", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.internal.maintenance", null, "IM(md)", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.external.maintenance", null, "EM(md)", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.recurrent.investment", null, "RINV(k€)", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.measure.cost", null, "CS(k€)", locale));
		row.getCell(++colIndex).setCellValue(messageSource.getMessage("label.measure.phase", null, "Phase", locale));
		if (type == AnalysisType.QUANTITATIVE) {
			row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.action_plan.ale", null, "ALE", locale));
			row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.action_plan.delta_ale", null, "Δ ALE", locale));
			row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.action_plan.rosi", null, "ROSI", locale));
		} else
			row.getCell(++colIndex).setCellValue(messageSource.getMessage("report.action_plan.risk_count", null, "NR", locale));
	}

	// ******************************************************************************************************************
	// * Actions
	// ******************************************************************************************************************

	/**
	 * buildAnalysis: <br>
	 * Description
	 * 
	 * @param errors
	 * @param owner
	 * @param source
	 * @param locale
	 * @param session
	 * @return
	 */
	private boolean buildAnalysis(Map<String, String> errors, User owner, String source, Locale locale, HttpSession session) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			Analysis analysis = null;
			Customer customer = null;

			int id = jsonNode.get("id").asInt();
			int idCustomer = jsonNode.has("analysiscustomer") ? jsonNode.get("analysiscustomer").asInt() : -1;

			if (id > 0) {
				if (!serviceAnalysis.exists(id))
					errors.put("analysis", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
				else if (!serviceAnalysis.isProfile(id)) {
					customer = serviceCustomer.get(idCustomer);
					if (customer == null || !customer.isCanBeUsed() || !owner.containsCustomer(customer))
						errors.put("analysiscustomer", messageSource.getMessage("error.customer.not_valid", null, "Customer is not valid", locale));
					else {
						if (!serviceAnalysis.isAnalysisCustomer(id, idCustomer))
							customerManager.switchCustomer(serviceAnalysis.getIdentifierByIdAnalysis(id), idCustomer, owner.getLogin());
						analysis = serviceAnalysis.get(id);
						if (customer.getId() != analysis.getCustomer().getId())
							analysis.setCustomer(customer);
					}
				} else
					analysis = serviceAnalysis.get(id);
				/**
				 * Log
				 */
				if (analysis != null)
					TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.user.edit.analysis.information",
							String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), owner.getLogin(), LogAction.UPDATE,
							analysis.getIdentifier(), analysis.getVersion());
			} else {
				if (idCustomer < 1)
					errors.put("analysiscustomer", messageSource.getMessage("error.customer.null", null, "Customer cannot be empty", locale));
				else {
					customer = serviceCustomer.get(idCustomer);
					if (customer == null || !customer.isCanBeUsed() || !owner.containsCustomer(customer))
						errors.put("analysiscustomer", messageSource.getMessage("error.customer.not_valid", null, "Customer is not valid", locale));
				}
			}

			if (!errors.isEmpty())
				return false;

			int idLanguage = jsonNode.has("analysislanguage") ? jsonNode.get("analysislanguage").asInt() : -1;

			Language language = serviceLanguage.get(idLanguage);

			String comment = jsonNode.has("comment") ? jsonNode.get("comment").asText() : "";

			boolean uncertainty = jsonNode.has("uncertainty") ? !jsonNode.get("uncertainty").asText().isEmpty() : false;

			if (idLanguage < 1)
				errors.put("analysislanguage", messageSource.getMessage("error.language.null", null, "Language cannot be empty", locale));
			else if (language == null)
				errors.put("analysislanguage", messageSource.getMessage("error.language.not_valid", null, "Language is not valid", locale));
			if (comment.trim().isEmpty())
				errors.put("comment", messageSource.getMessage("error.comment.null", null, "Comment cannot be empty", locale));

			if (!errors.isEmpty())
				return false;
			boolean update = analysis.getId() > 0 && !analysis.isProfile();
			analysis.setLabel(comment);
			analysis.setLanguage(language);
			analysis.setUncertainty(analysis.getType() == AnalysisType.QUALITATIVE ? false : uncertainty);
			if (update)
				assessmentAndRiskProfileManager.updateRiskDendencies(analysis, null);
			serviceAnalysis.saveOrUpdate(analysis);
			return true;
		} catch (TrickException e) {
			errors.put("analysis", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			errors.put("analysis", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}
		return false;
	}

	private Client buildClient(String username) {
		User user = serviceUser.get(username);
		TSSetting urlSetting = serviceTSSetting.get(TSSettingName.TICKETING_SYSTEM_URL);
		TSSetting nameSetting = serviceTSSetting.get(TSSettingName.TICKETING_SYSTEM_NAME);
		if (urlSetting == null || nameSetting == null)
			throw new TrickException("error.load.setting", "Setting cannot be loaded");
		Map<String, Object> settings = new HashMap<>(3);
		settings.put("username", user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME));
		settings.put("password", user.getSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD));
		settings.put("url", urlSetting.getValue());
		Client client = null;
		boolean isConnected = false;
		try {
			client = ClientBuilder.Build(nameSetting.getString());
			isConnected = client.connect(settings);
		} catch (TrickException e) {
			throw e;
		} catch (Exception e) {
			throw new TrickException("error.ticket_system.connexion.failed", "Unable to connect to your ticketing system", e);
		} finally {
			if (!(client == null || isConnected)) {
				try {
					client.close();
				} catch (IOException e) {
					TrickLogManager.Persist(e);
				}
			}
		}
		return client;
	}
}
