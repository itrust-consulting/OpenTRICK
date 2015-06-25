package lu.itrust.business.TS.controller;

import java.io.File;
import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerAnalysisImport;
import lu.itrust.business.TS.asynchronousWorkers.WorkerCreateAnalysisVersion;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportAnalysis;
import lu.itrust.business.TS.asynchronousWorkers.WorkerExportWordReport;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.CustomerManager;
import lu.itrust.business.TS.component.Duplicator;
import lu.itrust.business.TS.component.DynamicParameterComputer;
import lu.itrust.business.TS.component.GeneralComperator;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceActionPlanSummary;
import lu.itrust.business.TS.database.service.ServiceActionPlanType;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceHistory;
import lu.itrust.business.TS.database.service.ServiceItemInformation;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceRole;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.database.service.ServiceUserSqLite;
import lu.itrust.business.TS.database.service.ServiceWordReport;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.ExportAnalysisReport;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.ManageAnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.helper.AssessmentManager;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.iteminformation.helper.ComparatorItemInformation;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureManager;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.validator.HistoryValidator;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;

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

/**
 * ControllerAnalysis.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 22, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis")
public class ControllerAnalysis {

	private static final String FILTER_ANALYSIS_NAME = "filter_analysis_name";

	private static final String LAST_SELECTED_ANALYSIS_NAME = "last-selected-analysis-name";

	private static final String ANALYSIS_TASK_ID = "analysis_task_id";

	private static final String CURRENT_CUSTOMER = "currentCustomer";

	private static final String LAST_SELECTED_CUSTOMER_ID = "last-selected-customer-id";

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceActionPlanType serviceActionPlanType;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceActionPlanSummary serviceActionPlanSummary;

	@Autowired
	private AssessmentManager assessmentManager;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceUserSqLite serviceUserSqLite;

	@Autowired
	private ServiceWordReport serviceWordReport;

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
	private ServiceHistory serviceHistory;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceItemInformation serviceItemInformation;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceParameter serviceParameter;

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceRole serviceRole;
	
	@Autowired
	private DynamicParameterComputer dynamicParameterComputer;

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private Duplicator duplicator;

	@Autowired
	private ManageAnalysisRight manageAnalysisRight;

	@Autowired
	private CustomerManager customerManager;

	@Autowired
	private CustomDelete customDelete;

	@Value("${app.settings.report.french.template.name}")
	private String frenchReportName;

	@Value("${app.settings.report.english.template.name}")
	private String englishReportName;

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
	public String displayAll(Principal principal, Model model, HttpSession session, RedirectAttributes attributes, Locale locale, HttpServletRequest request) throws Exception {

		// retrieve analysisId if an analysis was already selected
		Integer selected = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Boolean isReadOnly = (Boolean) session.getAttribute(Constant.SELECTED_ANALYSIS_READ_ONLY);

		// check if an analysis is selected
		if (selected != null) {

			Boolean hasPermission = false;

			// prepare permission evaluator
			PermissionEvaluatorImpl permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);

			Analysis analysis = serviceAnalysis.get(selected);

			if (analysis == null) {
				attributes.addFlashAttribute("error", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
				throw new ResourceNotFoundException((String) attributes.getFlashAttributes().get("error"));
			}

			User user = serviceUser.get(principal.getName());

			if(isReadOnly == null)
				isReadOnly = false;
			
			hasPermission = analysis.isProfile() ? user.hasRole(RoleType.ROLE_CONSULTANT) || user.hasRole(RoleType.ROLE_ADMIN) : permissionEvaluator.userIsAuthorized(selected,
					principal, isReadOnly ?  AnalysisRight.READ : AnalysisRight.MODIFY);
			if (hasPermission) {
				// initialise analysis
				Collections.sort(analysis.getItemInformations(), new ComparatorItemInformation());
				Map<String, List<Measure>> measures = mapMeasures(analysis.getAnalysisStandards());
				Optional<Parameter> soaParameter = analysis.getParameters().stream().filter(parameter -> parameter.getDescription().equals(Constant.SOA_THRESHOLD)).findFirst();
				model.addAttribute("soaThreshold", soaParameter.isPresent() ? soaParameter.get().getValue() : 100.0);
				model.addAttribute("login", user.getLogin());
				model.addAttribute("analysis", analysis);
				model.addAttribute("standards", analysis.getStandards());
				model.addAttribute("measures", measures);
				model.addAttribute("soa", measures.get("27002"));
				model.addAttribute("show_uncertainty", analysis.isUncertainty());
				model.addAttribute("show_cssf", analysis.isCssf());
				model.addAttribute("isReadOnly", isReadOnly);
				model.addAttribute("language", analysis.getLanguage().getAlpha2());
				
				/**
				 * Log
				 */
				TrickLogManager.Persist(LogType.ANALYSIS, isReadOnly? "log.open.analysis" : "log.edit.analysis",
						String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), user.getLogin(), isReadOnly? LogAction.OPEN : LogAction.EDIT, analysis.getIdentifier(),
						analysis.getVersion());

			} else {
				TrickLogManager.Persist(LogLevel.ERROR, LogType.ANALYSIS, "log.analysis.access_deny",
						String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), user.getLogin(), LogAction.DENY_ACCESS,
						analysis.getIdentifier(), analysis.getVersion());
				attributes.addFlashAttribute("error", messageSource.getMessage("error.not_authorized", null, "Insufficient permissions!", locale));
				throw new AccessDeniedException((String) attributes.getFlashAttributes().get("error"));
			}
		} else {
			String view = LoadUserAnalyses(session, principal, model, null);
			if (view != null)
				return view;
		}
		return "analyses/analysis";
	}

	/**
	 * mapMeasures: <br>
	 * Description
	 * 
	 * @param standards
	 * @return
	 */
	private Map<String, List<Measure>> mapMeasures(List<AnalysisStandard> standards) {

		Map<String, List<Measure>> measuresmap = new LinkedHashMap<String, List<Measure>>();

		for (AnalysisStandard standard : standards) {
			List<Measure> measures = standard.getMeasures();
			Comparator<Measure> cmp = new Comparator<Measure>() {
				public int compare(Measure o1, Measure o2) {
					return Measure.compare(o1.getMeasureDescription().getReference(), o2.getMeasureDescription().getReference());
				}
			};
			Collections.sort(measures, cmp);
			measuresmap.put(standard.getStandard().getLabel(), measures);
		}

		return measuresmap;
	}

	// *****************************************************************
	// * reload customer section by pageindex
	// *****************************************************************

	@RequestMapping("/Section")
	public String section(HttpServletRequest request, Principal principal, Model model) throws Exception {
		return LoadUserAnalyses(request.getSession(), principal, model, "analyses/allAnalyses/analyses");
	}

	private String LoadUserAnalyses(HttpSession session, Principal principal, Model model, String view) throws Exception {
		List<String> names = null;
		Integer customer = (Integer) session.getAttribute(CURRENT_CUSTOMER);
		List<Customer> customers = serviceCustomer.getAllNotProfileOfUser(principal.getName());
		String nameFilter = (String) session.getAttribute(FILTER_ANALYSIS_NAME);
		if (customer == null || nameFilter == null) {
			User user = serviceUser.get(principal.getName());
			if (user == null)
				return "redirect:/Logout";
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
		model.addAttribute("names", names);
		model.addAttribute("analysisSelectedName", nameFilter);
		model.addAttribute("customer", customer);
		model.addAttribute("customers", customers);
		model.addAttribute("login", principal.getName());
		return view;
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
	@RequestMapping(value = "/DisplayByCustomer/{idCustomer}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
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
		user.setSetting(LAST_SELECTED_ANALYSIS_NAME, name);
		user.setSetting(LAST_SELECTED_CUSTOMER_ID, idCustomer);
		serviceUser.saveOrUpdate(user);
		return "analyses/allAnalyses/analyses";
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
	@RequestMapping(value = "/Update/ALE", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String update(HttpSession session, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

			if (analysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", customLocale != null ? customLocale : locale));
			assessmentManager.UpdateAssetALE(analysis);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.ale.update", null, "ALE was successfully updated", customLocale != null ? customLocale : locale));
		} catch (TrickException e) {
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
		} catch (Exception e) {
			e.printStackTrace();
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			return JsonMessage.Error(messageSource.getMessage("error.analysis.ale.update", null, "ALE cannot be updated", customLocale != null ? customLocale : locale));
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
	@RequestMapping("/{analysisId}/Select")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String selectAnalysis(Principal principal, @PathVariable("analysisId") Integer analysisId, @RequestParam(value = "readOnly", defaultValue = "false") boolean readOnly,
			HttpSession session) throws Exception {
		// select the analysis
		session.setAttribute(Constant.SELECTED_ANALYSIS, analysisId);
		session.setAttribute(Constant.SELECTED_ANALYSIS_READ_ONLY, readOnly);
		return "redirect:/Analysis";
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
	@RequestMapping(value = "/{analysisId}/SelectOnly", headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody boolean selectOnly(Principal principal, @PathVariable("analysisId") Integer analysisId,
			@RequestParam(value = "readOnly", defaultValue = "false") boolean readOnly, HttpSession session) throws Exception {
		// select the analysis
		session.setAttribute(Constant.SELECTED_ANALYSIS, analysisId);
		session.setAttribute(Constant.SELECTED_ANALYSIS_READ_ONLY, readOnly);
		return session.getAttribute(Constant.SELECTED_ANALYSIS) == analysisId;
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
		session.removeAttribute(Constant.SELECTED_ANALYSIS_READ_ONLY);
		Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (integer != null) {
			session.removeAttribute(Constant.SELECTED_ANALYSIS);
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

			// add the analysis object
			model.put("analysis", analysis);

			return "analyses/allAnalyses/forms/editAnalysis";
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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
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
		} catch (Exception e) {
			errors.put("owner", messageSource.getMessage("error.user.not_found", null, "User cannot be found", locale));
			e.printStackTrace();
		}
		return errors;
	}

	// *****************************************************************
	// * set default profile
	// *****************************************************************

	@RequestMapping("/SetDefaultProfile/{analysisId}")
	@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
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
	@RequestMapping(value = "/Delete/{analysisId}", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String deleteAnalysis(@PathVariable("analysisId") int analysisId, RedirectAttributes attributes, Locale locale, Principal principal, HttpSession session)
			throws Exception {
		try {

			Analysis analysis = serviceAnalysis.getDefaultProfile();

			if (analysis != null && analysis.getId() == analysisId)
				return JsonMessage.Error(messageSource.getMessage("error.profile.delete.failed", null, "Default profile cannot be deleted!", locale));

			customDelete.deleteAnalysis(analysisId, principal.getName());

			Integer selectedAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			if (selectedAnalysis != null && selectedAnalysis == analysisId)
				session.removeAttribute(Constant.SELECTED_ANALYSIS);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.analysis.delete.successfully", null, "Analysis was deleted successfully", locale));
		} catch (Exception e) {
			// return error message
			e.printStackTrace();
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
	@RequestMapping(value = "/{analysisId}/NewVersion", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.canCreateNewVersion(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String addHistory(@PathVariable("analysisId") Integer analysisId, Map<String, Object> model, Principal principal, HttpSession session) throws Exception {

		// retrieve user
		User user = serviceUser.get(principal.getName());

		// retrieve version
		String version = serviceAnalysis.getVersionOfAnalysis(analysisId);

		String author = user.getFirstName() + " " + user.getLastName();

		// add data to model
		model.put("oldVersion", version);
		model.put("analysisId", analysisId);
		model.put("author", author);

		return "analyses/allAnalyses/forms/newVersion";
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
	@RequestMapping(value = "/Duplicate/{analysisId}", headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.canCreateNewVersion(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Map<String, String> createNewVersion(@RequestBody String value, BindingResult result, @PathVariable int analysisId, Principal principal, Locale locale)
			throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			// retrieve analysis object
			Analysis analysis = serviceAnalysis.get(analysisId);

			// check if object is not null
			if (analysis == null)
				errors.put("analysis", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", locale));

			List<String> versions = serviceAnalysis.getAllNotEmptyVersion(analysis.getIdentifier());
			Comparator<String> comparator = new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return GeneralComperator.VersionComparator(o1, o2);
				}
			};
			Collections.sort(versions, Collections.reverseOrder(comparator));

			String lastVersion = versions.get(0);

			HistoryValidator validator = (HistoryValidator) serviceDataValidation.findByClass(History.class);

			if (validator == null)
				serviceDataValidation.register(validator = new HistoryValidator());

			History history = new History();

			String error = null;

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);

			String author = jsonNode.get("author").asText();
			Date date = new Date(System.currentTimeMillis());
			String version = jsonNode.get("version").asText();
			String comment = jsonNode.get("comment").asText();

			error = validator.validate(history, "author", author);
			if (error != null)
				errors.put("author", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				history.setAuthor(author);

			error = validator.validate(history, "version", version);
			if (error != null)
				errors.put("version", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				if (GeneralComperator.VersionComparator(lastVersion, version) >= 0)
					errors.put(
							"version",
							messageSource.getMessage("error.history.version.invalid", new String[] { lastVersion },
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

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			errors.put("analysis", messageSource.getMessage("error.analysis.duplicate", null, "Analysis cannot be duplicated!", locale));
		} catch (TrickException e) {
			e.printStackTrace();
			errors.put("analysis", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			e.printStackTrace();
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
	@RequestMapping("/Import/Execute")
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
		File importFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + "");

		// transfer form file to java file
		file.transferTo(importFile);

		// create worker
		Worker worker = new WorkerAnalysisImport(sessionFactory, serviceTaskFeedback, importFile, customer.getId(), principal.getName());
		worker.setPoolManager(workersPoolManager);

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
	@RequestMapping(value = "/Export/{analysisId}", method = RequestMethod.GET, headers = "Accept=application/json")
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
	@RequestMapping(value = "/Export/Report/{analysisId}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody String exportReport(@PathVariable Integer analysisId, HttpServletRequest request, Principal principal, Locale locale) {
		try {
			ExportAnalysisReport exportAnalysisReport = new ExportAnalysisReport(messageSource, serviceTaskFeedback, request.getServletContext().getRealPath(""));
			switch (serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().toLowerCase()) {
			case "fra":
				locale = Locale.FRENCH;
				exportAnalysisReport.setReportName(frenchReportName);
				break;
			default:
				locale = Locale.ENGLISH;
				exportAnalysisReport.setReportName(englishReportName);
			}
			Worker worker = new WorkerExportWordReport(analysisId, principal.getName(), sessionFactory, exportAnalysisReport, workersPoolManager);
			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
				return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.report.exporting", null, "Exporting report", locale));
		} catch (TrickException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
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

			boolean cssf = jsonNode.has("cssf") ? !jsonNode.get("cssf").asText().isEmpty() : false;

			if (idLanguage < 1)
				errors.put("analysislanguage", messageSource.getMessage("error.language.null", null, "Language cannot be empty", locale));
			else if (language == null)
				errors.put("analysislanguage", messageSource.getMessage("error.language.not_valid", null, "Language is not valid", locale));
			if (comment.trim().isEmpty())
				errors.put("comment", messageSource.getMessage("error.comment.null", null, "Comment cannot be empty", locale));

			if (!errors.isEmpty())
				return false;

			analysis.setLabel(comment);
			analysis.setLanguage(language);
			analysis.setUncertainty(uncertainty);
			analysis.setCssf(cssf);

			serviceAnalysis.saveOrUpdate(analysis);

			return true;
		} catch (Exception e) {
			errors.put("analysis", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		}
	}
}
