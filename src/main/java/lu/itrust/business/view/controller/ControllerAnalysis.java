package lu.itrust.business.view.controller;

import java.io.File;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.AnalysisStandard;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.History;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.export.ExportAnalysisReport;
import lu.itrust.business.TS.export.UserSQLite;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.Duplicator;
import lu.itrust.business.component.GeneralComperator;
import lu.itrust.business.component.MeasureManager;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.exception.ResourceNotFoundException;
import lu.itrust.business.exception.TrickException;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;
import lu.itrust.business.service.ServiceActionPlan;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceActionPlanType;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAnalysisStandard;
import lu.itrust.business.service.ServiceAssessment;
import lu.itrust.business.service.ServiceAsset;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceHistory;
import lu.itrust.business.service.ServiceItemInformation;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServicePhase;
import lu.itrust.business.service.ServiceRiskInformation;
import lu.itrust.business.service.ServiceRiskRegister;
import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceScenario;
import lu.itrust.business.service.ServiceStandard;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserAnalysisRight;
import lu.itrust.business.service.ServiceUserSqLite;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;
import lu.itrust.business.task.WorkerAnalysisImport;
import lu.itrust.business.task.WorkerExportAnalysis;
import lu.itrust.business.validator.HistoryValidator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	private ServiceRiskRegister serviceRiskRegister;

	@Autowired
	private AssessmentManager assessmentManager;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceUserSqLite serviceUserSqLite;

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
	private ServiceMeasureDescription serviceMeasureDescription;

	@Autowired
	private ServiceRiskInformation serviceRiskInformation;

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
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private Duplicator duplicator;

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
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");

		// check if an analysis is selected
		if (selected != null) {

			Boolean hasPermission = false;

			// prepare permission evaluator
			PermissionEvaluatorImpl permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);

			Analysis analysis = serviceAnalysis.get(selected);

			if (analysis == null) {
				attributes.addFlashAttribute("errors", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
				throw new ResourceNotFoundException((String) attributes.getFlashAttributes().get("errors"));
			}

			User user = serviceUser.get(principal.getName());

			hasPermission =
				analysis.isProfile() ? user.hasRole(RoleType.ROLE_CONSULTANT) || user.hasRole(RoleType.ROLE_ADMIN) : permissionEvaluator.userIsAuthorized(selected, principal, AnalysisRight.READ);

			if (hasPermission) {

				// initialise analysis

				analysis.setAssets(serviceAsset.getAllFromAnalysis(selected));
				analysis.setScenarios(serviceScenario.getAllFromAnalysis(selected));
				analysis.setItemInformations(serviceItemInformation.getAllFromAnalysis(selected));
				analysis.setLanguage(serviceLanguage.getByAlpha3(analysis.getLanguage().getAlpha3()));
				analysis.setActionPlans(serviceActionPlan.getAllFromAnalysis(selected));
				analysis.setSummaries(serviceActionPlanSummary.getAllFromAnalysis(selected));
				model.addAttribute("login", user.getLogin());
				model.addAttribute("analysis", analysis);
				model.addAttribute("show_uncertainty", analysis.isUncertainty());
				model.addAttribute("show_cssf", analysis.isCssf());
				model.addAttribute("language", analysis.getLanguage().getAlpha3());

			} else {
				attributes.addFlashAttribute("errors", messageSource.getMessage("error.not_authorized", null, "Insufficient permissions!", locale));
				throw new AccessDeniedException((String) attributes.getFlashAttributes().get("errors"));
			}
		} else {

			// load only customers of this user
			List<Customer> customers = serviceCustomer.getAllNotProfileOfUser(principal.getName());

			// retrieve currently selected customer
			Integer customer = (Integer) session.getAttribute("currentCustomer");

			// check if the current customer is set -> no
			if (customer == null && !customers.isEmpty())

				// use first customer as selected customer
				session.setAttribute("currentCustomer", customer = customers.get(0).getId());
			if (customer != null)
				// load model with objects by the selected customer
				model.addAttribute("analyses", serviceAnalysis.getAllNotEmptyFromUserAndCustomer(principal.getName(), customer));

			model.addAttribute("customer", customer);
			model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
			model.addAttribute("login", principal.getName());
		}
		return "analyses/analysis";
	}

	// *****************************************************************
	// * reload customer section by pageindex
	// *****************************************************************

	@RequestMapping("/Section")
	public String section(HttpServletRequest request, Principal principal, Model model) throws Exception {
		Integer customer = (Integer) request.getSession().getAttribute("currentCustomer");
		List<Customer> customers = serviceCustomer.getAllNotProfileOfUser(principal.getName());
		if (customer == null) {
			if (!customers.isEmpty())
				request.getSession().setAttribute("currentCustomer", customer = customers.get(0).getId());
		}
		model.addAttribute("analyses", serviceAnalysis.getAllNotEmptyFromUserAndCustomer(principal.getName(), customer));
		model.addAttribute("customer", customer);
		model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		model.addAttribute("login", principal.getName());

		return "analyses/allAnalyses/analyses";
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
	@RequestMapping("/DisplayByCustomer/{customerSection}")
	public String section(@PathVariable Integer customerSection, HttpSession session, Principal principal, Model model) throws Exception {
		session.setAttribute("currentCustomer", customerSection);
		model.addAttribute("analyses", serviceAnalysis.getAllNotEmptyFromUserAndCustomer(principal.getName(), customerSection));
		model.addAttribute("customer", customerSection);
		model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		model.addAttribute("login", principal.getName());
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String update(HttpSession session, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

			if (analysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", customLocale != null ? customLocale : locale));
			assessmentManager.UpdateAssetALE(analysis);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.ale.update", null, "ALE was successfully updated", customLocale != null ? customLocale : locale));
		} catch (TrickException e) {
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
		} catch (Exception e) {
			e.printStackTrace();
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String selectAnalysis(Principal principal, @PathVariable("analysisId") Integer analysisId, HttpSession session) throws Exception {
		// select the analysis
		session.setAttribute("selectedAnalysis", analysisId);
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public @ResponseBody boolean selectOnly(Principal principal, @PathVariable("analysisId") Integer analysisId, HttpSession session) throws Exception {
		// select the analysis
		session.setAttribute("selectedAnalysis", analysisId);
		return session.getAttribute("selectedAnalysis") == analysisId;
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
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer != null) {
			session.removeAttribute("selectedAnalysis");
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	public @ResponseBody String deleteAnalysis(@PathVariable("analysisId") int analysisId, RedirectAttributes attributes, Locale locale, Principal principal, HttpSession session) throws Exception {
		try {

			Analysis analysis = serviceAnalysis.getDefaultProfile();

			if (analysis != null && analysis.getId() == analysisId)
				return JsonMessage.Error(messageSource.getMessage("error.profile.delete.failed", null, "Default profile cannot be deleted!", locale));

			// delete the analysis

			serviceActionPlan.deleteAllFromAnalysis(analysisId);

			serviceActionPlanSummary.deleteAllFromAnalysis(analysisId);

			serviceRiskRegister.deleteAllFromAnalysis(analysisId);

			serviceAnalysisStandard.deleteAllFromAnalysis(analysisId);

			serviceAnalysis.delete(analysisId);

			Integer selectedAnalysis = (Integer) session.getAttribute("selectedAnalysis");

			if (selectedAnalysis != null && selectedAnalysis == analysisId)
				session.removeAttribute("selectedAnalysis");

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> createNewVersion(@RequestBody String value, BindingResult result, @PathVariable int analysisId, Principal principal, Locale locale) throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		Analysis copy = null;

		try {

			// retrieve analysis object
			Analysis analysis = serviceAnalysis.get(analysisId);

			// check if object is not null
			if (analysis == null)
				errors.put("analysis", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", locale));

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
			String oldVersion = jsonNode.get("oldVersion").asText();

			error = validator.validate(history, "author", author);
			if (error != null)
				errors.put("author", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				history.setAuthor(author);

			error = validator.validate(history, "version", version);
			if (error != null)
				errors.put("version", serviceDataValidation.ParseError(error, messageSource, locale));
			else {

				if (GeneralComperator.VersionComparator(oldVersion, version) >= 0)
					errors.put("version", messageSource.getMessage("error.history.version.invalid", null, "Version has to be bigger than based on version", locale));
				else if (serviceAnalysis.exists(analysis.getIdentifier(), version))
					errors.put("version", messageSource.getMessage("error.history.version.exists", null, "Version already exists for the analysis", locale));
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

			copy = duplicator.duplicateAnalysis(analysis, null);

			copy.setBasedOnAnalysis(analysis);
			copy.addAHistory(history);
			copy.setVersion(history.getVersion());
			copy.setLabel(analysis.getLabel());
			copy.setCreationDate(new Timestamp(System.currentTimeMillis()));
			copy.setProfile(false);
			copy.setDefaultProfile(false);

			serviceAnalysis.saveOrUpdate(copy);

		} catch (CloneNotSupportedException e) {
			// return dubplicate error message
			e.printStackTrace();
			errors.put("analysis", messageSource.getMessage("error.analysis.duplicate", null, "Analysis cannot be duplicated!", locale));

			if (copy != null)
				removeStandardsOnError(copy.getAnalysisOnlyStandards());

		} catch (TrickException e) {
			e.printStackTrace();
			errors.put("analysis", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			e.printStackTrace();
			errors.put("analysis", messageSource.getMessage("error.analysis.duplicate.unknown", null, "An unknown error occurred during duplication!", locale));
		}finally {
			if(!errors.isEmpty() && copy !=null)
				removeStandardsOnError(copy.getAnalysisOnlyStandards());
		}
				
		return errors;
	}

	private void removeStandardsOnError(List<AnalysisStandard> standards) {
		// TODO remove standards in case of an error
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
	public Object importAnalysisSave(Principal principal, @RequestParam(value = "customerId") Integer customerId, HttpServletRequest request, @RequestParam(value = "file") MultipartFile file,
			final RedirectAttributes attributes, Locale locale) throws Exception {

		// retrieve the customer
		Customer customer = serviceCustomer.get(customerId);

		// if the customer or the file are not correct
		if (customer == null || file.isEmpty()) {
			attributes.addFlashAttribute("errors", messageSource.getMessage("error.customer_or_file.import.analysis", null, "Customer or file are not set or empty!", locale));
			return "analyses/importAnalysis";
		}

		// set selected customer, the selected customer of the analysis
		request.getSession().setAttribute("currentCustomer", customer.getId());

		// the file to import
		File importFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + "");

		// transfer form file to java file
		file.transferTo(importFile);

		// create worker
		Worker worker = new WorkerAnalysisImport(sessionFactory, serviceTaskFeedback, importFile, customer, serviceUser.get(principal.getName()));
		worker.setPoolManager(workersPoolManager);

		// register worker to tasklist
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
			// execute task
			executor.execute(worker);
		else
			// prepare error return message
			// add return message
			attributes.addFlashAttribute("errors", messageSource.getMessage("failed.start.import.analysis", null, "Analysis importation was failed", locale));
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).EXPORT)")
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
			return JsonMessage.Error(messageSource.getMessage("failed.start.export.analysis", null, "Analysis export was failed", locale));
	}

	/**
	 * download: <br>
	 * Description
	 * 
	 * @param idFile
	 * @param principal
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Download/{idFile}")
	public String download(@PathVariable Integer idFile, Principal principal, HttpServletResponse response) throws Exception {

		// get user file by given file id and username
		UserSQLite userSqLite = serviceUserSqLite.getByIdAndUser(idFile, principal.getName());

		// if file could not be found retrun 404 error
		if (userSqLite == null)
			return "errors/404";

		// set response contenttype to sqlite
		response.setContentType("sqlite");

		// retireve sqlite file name to set
		String identifierName = userSqLite.getAnalysisIdentifier();

		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\""
			+ (identifierName == null || identifierName.trim().isEmpty() ? "Analysis" : identifierName.trim().replaceAll(":|-|[ ]", "_")) + ".sqlite\"");

		// set sqlite file size as response size
		response.setContentLength((int) userSqLite.getSize());

		// return the sqlite file (as copy) to the response outputstream ( whihc
		// creates on the
		// client side the sqlite file)
		FileCopyUtils.copy(userSqLite.getSqLite(), response.getOutputStream());

		// return
		return null;
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
	@RequestMapping("/Export/Report/{analysisId}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).EXPORT)")
	public String exportReport(@PathVariable Integer analysisId, HttpServletResponse response, HttpServletRequest request, RedirectAttributes attributes, Principal principal, Locale locale)
			throws Exception {

		File file = null;

		try {

			ExportAnalysisReport exportAnalysisReport = new ExportAnalysisReport();

			switch (serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().toLowerCase()) {
				case "fra":
					locale = Locale.FRENCH;
					exportAnalysisReport.setReportName(frenchReportName);
					break;
				case "eng":
					locale = Locale.ENGLISH;
					exportAnalysisReport.setReportName(englishReportName);
					break;
				default: {
					locale = Locale.ENGLISH;
					exportAnalysisReport.setReportName(englishReportName);
				}
			}

			exportAnalysisReport.setMessageSource(messageSource);

			file = exportAnalysisReport.exportToWordDocument(analysisId, request.getServletContext(), serviceAnalysis, true);

			if (file != null) {

				response.setContentType("docx");
				response.setContentLength((int) file.length());
				response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
				FileCopyUtils.copy(FileCopyUtils.copyToByteArray(file), response.getOutputStream());
			}
			return null;
		} catch (Throwable t) {
			t.printStackTrace();
			attributes.addFlashAttribute("errors", messageSource.getMessage(t.getMessage(), null, t.getMessage(), locale));
			return "redirect:/Analysis";
		} finally {
			if (file != null && file.exists())
				file.delete();
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
			int id = jsonNode.get("id").asInt();

			if (id > 0)
				analysis = serviceAnalysis.get(id);

			if (analysis == null) {
				errors.put("analysis", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
				return false;
			}

			int idLanguage = jsonNode.has("analysislanguage") ? jsonNode.get("analysislanguage").asInt() : -1;
			Language language = serviceLanguage.get(idLanguage);

			String comment = jsonNode.has("comment") ? jsonNode.get("comment").asText() : "";

			int idCustomer = jsonNode.has("analysiscustomer") ? jsonNode.get("analysiscustomer").asInt() : -1;
			Customer customer = serviceCustomer.get(idCustomer);

			boolean uncertainty = jsonNode.has("uncertainty") ? !jsonNode.get("uncertainty").asText().isEmpty() : false;

			boolean cssf = jsonNode.has("cssf") ? !jsonNode.get("cssf").asText().isEmpty() : false;

			if (idCustomer < 1)
				errors.put("analysiscustomer", messageSource.getMessage("error.customer.null", null, "Customer cannot be empty", locale));
			else if (customer == null || !customer.isCanBeUsed())
				errors.put("analysiscustomer", messageSource.getMessage("error.customer.not_valid", null, "Customer is not valid", locale));
			if (idLanguage < 1)
				errors.put("analysislanguage", messageSource.getMessage("error.language.null", null, "Language cannot be empty", locale));
			else if (language == null)
				errors.put("analysislanguage", messageSource.getMessage("error.language.not_valid", null, "Language is not valid", locale));
			if (comment.trim().isEmpty())
				errors.put("comment", messageSource.getMessage("error.comment.null", null, "Comment cannot be empty", locale));

			if (!errors.isEmpty())
				return false;

			if (!analysis.isProfile())
				analysis.setCustomer(customer);

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
