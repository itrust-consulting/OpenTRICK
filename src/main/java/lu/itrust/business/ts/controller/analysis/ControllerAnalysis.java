package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.ts.constants.Constant.ANALYSIS_TASK_ID;
import static lu.itrust.business.ts.constants.Constant.CURRENT_CUSTOMER;
import static lu.itrust.business.ts.constants.Constant.FILTER_ANALYSIS_NAME;
import static lu.itrust.business.ts.constants.Constant.LAST_SELECTED_ANALYSIS_NAME;
import static lu.itrust.business.ts.constants.Constant.LAST_SELECTED_CUSTOMER_ID;
import static lu.itrust.business.ts.constants.Constant.OPEN_MODE;
import static lu.itrust.business.ts.constants.Constant.PARAMETERTYPE_TYPE_SINGLE_NAME;
import static lu.itrust.business.ts.constants.Constant.ROLE_MIN_USER;
import static lu.itrust.business.ts.constants.Constant.SELECTED_ANALYSIS;
import static lu.itrust.business.ts.constants.Constant.SELECTED_ANALYSIS_LANGUAGE;
import static lu.itrust.business.ts.constants.Constant.SOA_THRESHOLD;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;
import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.asynchronousWorkers.WorkerCreateAnalysisVersion;
import lu.itrust.business.ts.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.ts.component.CustomDelete;
import lu.itrust.business.ts.component.CustomerManager;
import lu.itrust.business.ts.component.MeasureManager;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceCustomer;
import lu.itrust.business.ts.database.service.ServiceDataValidation;
import lu.itrust.business.ts.database.service.ServiceExternalNotification;
import lu.itrust.business.ts.database.service.ServiceIDS;
import lu.itrust.business.ts.database.service.ServiceLanguage;
import lu.itrust.business.ts.database.service.ServiceTicketingSystem;
import lu.itrust.business.ts.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.ts.exception.ResourceNotFoundException;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.Comparators;
import lu.itrust.business.ts.helper.DependencyGraphManager;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.actionplan.helper.ActionPlanComputation;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.assessment.helper.Estimation;
import lu.itrust.business.ts.model.externalnotification.helper.ExternalNotificationHelper;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.OpenMode;
import lu.itrust.business.ts.model.general.helper.PhaseManager;
import lu.itrust.business.ts.model.history.History;
import lu.itrust.business.ts.model.iteminformation.helper.ComparatorItemInformation;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;
import lu.itrust.business.ts.model.riskinformation.helper.RiskInformationManager;
import lu.itrust.business.ts.model.scale.ScaleType;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.ts.usermanagement.RoleType;
import lu.itrust.business.ts.usermanagement.User;
import lu.itrust.business.ts.validator.HistoryValidator;

/**
 * ControllerAnalysis is a controller class that handles requests related to
 * analysis operations.
 * It is responsible for managing analysis versions, archiving analysis,
 * creating new versions, deleting analysis, and selecting/deselecting analysis.
 * This class extends the AbstractController class and is annotated
 * with @Controller to indicate that it is a Spring MVC controller.
 * It also uses Spring Security's @PreAuthorize annotation to enforce
 * authorization rules for different methods.
 * 
 * @author itrust consulting s.à r.l https://www.itrust.lu
 * @since Oct 22, 2013
 */
@PreAuthorize(ROLE_MIN_USER)
@RequestMapping("/Analysis")
@Controller
public class ControllerAnalysis extends AbstractController {

	@Autowired
	private ServiceIDS serviceIDS;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private CustomerManager customerManager;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceTicketingSystem serviceTicketingSystem;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private PermissionEvaluator permissionEvaluator;

	@Autowired
	private ServiceExternalNotification serviceExternalNotification;

	/**
	 * Retrieves the path to the JSP file for creating a new version of an analysis.
	 *
	 * @param analysisId the ID of the analysis
	 * @param model      the map containing the model data
	 * @param principal  the principal object representing the currently
	 *                   authenticated user
	 * @param session    the HttpSession object
	 * @return the path to the JSP file for creating a new version of an analysis
	 * @throws Exception if an error occurs during the retrieval of user data
	 */
	@RequestMapping(value = "/{analysisId}/NewVersion", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.hasPermission(#analysisId, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public String addHistory(@PathVariable("analysisId") Integer analysisId, Map<String, Object> model,
			Principal principal, HttpSession session) throws Exception {
		// retrieve user
		User user = serviceUser.get(principal.getName());
		// add data to model
		model.put("oldVersion", serviceAnalysis.getVersionOfAnalysis(analysisId));
		model.put("analysisId", analysisId);
		model.put("author", user.getFirstName() + " " + user.getLastName());
		return "jsp/analyses/all/forms/newVersion";
	}

	/**
	 * Handles the request for "/All" and returns a String.
	 * 
	 * @param model     the model object for the view
	 * @param principal the principal object representing the currently
	 *                  authenticated user
	 * @param session   the HttpSession object for storing session attributes
	 * @return a String representing the view name
	 * @throws Exception if an error occurs during the analysis loading process
	 */
	@RequestMapping("/All")
	public String allAnalysis(Model model, Principal principal, HttpSession session) throws Exception {
		session.removeAttribute(OPEN_MODE);
		session.removeAttribute(SELECTED_ANALYSIS);
		session.removeAttribute(SELECTED_ANALYSIS_LANGUAGE);
		return loadUserAnalyses(session, principal, model, null);
	}

	/**
	 * Archives an analysis identified by the given analysisId.
	 *
	 * @param analysisId the ID of the analysis to be archived
	 * @param request    the HttpServletRequest object
	 * @param principal  the Principal object representing the currently
	 *                   authenticated user
	 * @param locale     the Locale object representing the desired language for
	 *                   error/success messages
	 * @return a JSON string representing the result of the archiving operation
	 */
	@RequestMapping(value = "/Archive/{analysisId}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.hasPermission(#analysisId, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).ALL)")
	public @ResponseBody String archiving(@PathVariable Integer analysisId, HttpServletRequest request,
			Principal principal, Locale locale) {
		try {
			Analysis analysis = serviceAnalysis.get(analysisId);
			if (analysis.isProfile())
				return JsonMessage.success(messageSource.getMessage("error.archive.profile", null,
						"An analysis profile cannot be archived", locale));
			else if (analysis.isArchived())
				return JsonMessage.success(messageSource.getMessage("error.archived.analysis", null,
						"An analysis is already archived", locale));
			analysis.setArchived(true);
			serviceAnalysis.saveOrUpdate(analysis);
			TrickLogManager.persist(LogType.ANALYSIS, "log.archive.analysis",
					String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()),
					principal.getName(), LogAction.ARCHIVE, analysis.getIdentifier(), analysis.getVersion());
			return JsonMessage.success(messageSource.getMessage("success.analysis.archived", null,
					"Analysis has been successfully archived", locale));
		} catch (TrickException e) {
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			return JsonMessage.error(
					messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
	}

	/**
	 * Creates a new version of an analysis.
	 *
	 * @param value      The request body containing the analysis data.
	 * @param result     The binding result for validation errors.
	 * @param analysisId The ID of the analysis.
	 * @param principal  The principal object representing the authenticated user.
	 * @param locale     The locale for error messages.
	 * @return A map containing any validation errors encountered during the
	 *         creation of the new version.
	 */
	@RequestMapping(value = "/Duplicate/{analysisId}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.hasPermission(#analysisId, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Map<String, String> createNewVersion(@RequestBody String value, BindingResult result,
			@PathVariable int analysisId, Principal principal, Locale locale) {
		final Map<String, String> errors = new LinkedHashMap<>();
		try {
			// retrieve analysis object
			final Analysis analysis = serviceAnalysis.get(analysisId);

			// check if object is not null
			if (analysis == null)
				errors.put("analysis", messageSource.getMessage("error.analysis.not_found", null,
						"Analysis cannot be found!", locale));
			else {

				final String lastVersion = serviceAnalysis.getAllVersion(analysis.getIdentifier()).stream()
						.sorted((v0, v1) -> {
							return NaturalOrderComparator.compareTo(v1, v0);
						}).findFirst().get();

				HistoryValidator validator = (HistoryValidator) serviceDataValidation.findByClass(History.class);

				if (validator == null)
					serviceDataValidation.register(validator = new HistoryValidator());

				final History history = new History();

				final JsonNode jsonNode = new ObjectMapper().readTree(value);

				final String author = jsonNode.get("author").asText();

				final Date date = new Date(System.currentTimeMillis());

				final String version = jsonNode.get("version").asText();

				final String comment = jsonNode.get("comment").asText();

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
						errors.put("version",
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

				final Worker worker = new WorkerCreateAnalysisVersion(analysisId, history, principal.getName());
				// register worker to tasklist
				if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
					executor.execute(worker);
					errors.put(ANALYSIS_TASK_ID, worker.getId());
				} else
					errors.put("analysis", messageSource.getMessage("error.task_manager.too.many", null,
							"Too many tasks running in background", locale));
			}

		} catch (TrickException e) {
			TrickLogManager.persist(e);
			errors.put("analysis", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			errors.put("analysis", messageSource.getMessage("error.analysis.duplicate.unknown", null,
					"An unknown error occurred during duplication!", locale));
		}

		return errors;
	}

	/**
	 * Deletes an analysis with the specified analysisId.
	 *
	 * @param analysisId The ID of the analysis to be deleted.
	 * @param attributes The redirect attributes.
	 * @param principal  The principal object representing the currently
	 *                   authenticated user.
	 * @param session    The HttpSession object.
	 * @param locale     The locale object representing the user's preferred
	 *                   language.
	 * @return A string representing the result of the deletion operation.
	 * @throws Exception If an error occurs during the deletion process.
	 */
	@RequestMapping(value = "/Delete/{analysisId}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.hasDeletePermission(#analysisId, #principal, false)")
	public @ResponseBody String deleteAnalysis(@PathVariable("analysisId") int analysisId,
			RedirectAttributes attributes, Principal principal, HttpSession session, Locale locale)
			throws Exception {
		try {
			customDelete.deleteAnalysis(analysisId, principal.getName());
			Integer selectedAnalysis = (Integer) session.getAttribute(SELECTED_ANALYSIS);
			if (selectedAnalysis != null && selectedAnalysis == analysisId)
				session.removeAttribute(SELECTED_ANALYSIS);
			// return success message
			return JsonMessage.success(messageSource.getMessage("success.analysis.delete.successfully", null,
					"Analysis was deleted successfully", locale));
		} catch (Exception e) {
			// return error message
			TrickLogManager.persist(e);
			return JsonMessage.error(
					messageSource.getMessage("failed.delete.analysis", null, "Analysis cannot be deleted!", locale));
		}
	}

	/**
	 * Deselects the currently selected analysis.
	 *
	 * @param session The HttpSession object.
	 * @return A string representing the result of the deselection operation.
	 * @throws Exception If an error occurs during the deselection process.
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

	/**
	 * Handles the home request and redirects to the appropriate analysis page.
	 *
	 * @param principal the principal object representing the authenticated user
	 * @param model     the model object for passing data to the view
	 * @param session   the HttpSession object for storing session attributes
	 * @param locale    the Locale object representing the user's locale
	 * @param request   the HttpServletRequest object representing the HTTP request
	 * @return a String representing the redirect URL
	 * @throws Exception if an error occurs during the request handling
	 */
	@RequestMapping
	public String home(Principal principal, Model model, HttpSession session, Locale locale, HttpServletRequest request)
			throws Exception {
		// retrieve analysisId if an analysis was already selected
		Integer selected = (Integer) session.getAttribute(SELECTED_ANALYSIS);
		OpenMode openMode = OpenMode.parseOrDefault(session.getAttribute(OPEN_MODE));
		// check if an analysis is selected
		if (selected != null)
			return String.format("redirect:/Analysis/%d/Select?open=%s", selected, openMode.getValue());
		else
			return "redirect:/Analysis/All";
	}

	// *****************************************************************
	// * request create new analysis
	// *****************************************************************

	// *****************************************************************
	// * request edit analysis
	// *****************************************************************

	/**
	 * Loads the setting manager for analysis.
	 *
	 * @param session   the HttpSession object
	 * @param model     the Model object
	 * @param principal the Principal object
	 * @param locale    the Locale object
	 * @return the view name for the setting manager form
	 */
	@RequestMapping(value = "Manage-settings", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String loadSettingManager(HttpSession session, Model model, Principal principal, Locale locale) {
		final Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Map<String, String> currentSettings = serviceAnalysis.getSettingsByIdAnalysis(integer);
		final Map<AnalysisSetting, Object> settings = new LinkedHashMap<>();
		final AnalysisType analysisType = serviceAnalysis.getAnalysisTypeById(integer);
		for (AnalysisSetting setting : AnalysisSetting.values()) {
			if (!setting.isSupported(analysisType))
				continue;
			settings.put(setting, Analysis.findSetting(setting, currentSettings.get(setting.name())));
		}
		// load all assets of analysis to model
		model.addAttribute("settings", settings);
		return "jsp/analyses/single/components/settings/form";
	}

	// *****************************************************************
	// * save or update analysis object
	// *****************************************************************

	/**
	 * Handles the request to edit an analysis.
	 * 
	 * @param principal  the principal object representing the currently
	 *                   authenticated user
	 * @param analysisId the ID of the analysis to be edited
	 * @param model      the model object to be populated with data for the view
	 * @param locale     the locale object representing the user's preferred
	 *                   language
	 * @return the name of the view to be rendered for editing the analysis
	 * @throws Exception if an error occurs during the processing of the request
	 */
	@RequestMapping("/Edit/{analysisId}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String requestEditAnalysis(Principal principal, @PathVariable("analysisId") Integer analysisId,
			Map<String, Object> model, Locale locale) throws Exception {
		// retrieve analysis

		// add languages
		model.put("languages", serviceLanguage.getAll());

		// add customers of user
		model.put("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));

		model.put("types", AnalysisType.values());

		// add the analysis object
		model.put("analysis", serviceAnalysis.get(analysisId));

		return "jsp/analyses/all/forms/editAnalysis";
	}

	// *****************************************************************
	// * delete analysis
	// *****************************************************************

	/**
	 * Saves the analysis with the provided value.
	 *
	 * @param value     The value of the analysis to be saved.
	 * @param session   The HttpSession object.
	 * @param principal The Principal object representing the currently
	 *                  authenticated user.
	 * @param locale    The Locale object representing the user's preferred
	 *                  language.
	 * @return A Map containing any errors that occurred during the save operation.
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> save(@RequestBody String value, HttpSession session, Principal principal,
			Locale locale) {
		final Map<String, String> errors = new LinkedHashMap<>();
		try {
			// prepare permission verifier
			final PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis,
					serviceUserAnalysisRight);
			final JsonNode contents = new ObjectMapper().readTree(value);
			// retrieve analysis id to compute
			final int analysisId = contents.get("id").asInt();
			// check if it is a new analysis or the user is authorized to modify
			// the analysis
			final User user = serviceUser.get(principal.getName());

			final boolean isProfile = serviceAnalysis.isProfile(analysisId);

			if (permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.ALL)
					|| isProfile && user.isAutorised(RoleType.ROLE_CONSULTANT)) {
				save(analysisId, serviceUser.get(principal.getName()), contents, errors, locale);
			} else
				// throw error
				throw new AccessDeniedException(
						messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
		} catch (TrickException e) {
			errors.put("analysis", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			errors.put("owner", messageSource.getMessage("error.user.not_found", null, "User cannot be found", locale));
			TrickLogManager.persist(e);
		}
		return errors;
	}

	// *****************************************************************
	// * create new version of analysis
	// *****************************************************************

	/**
	 * Saves the setting manager with the given current settings.
	 * 
	 * @param currentSettings the map of current settings
	 * @param session         the HttpSession object
	 * @param model           the Model object
	 * @param principal       the Principal object
	 * @param locale          the Locale object
	 * @return a String representing the success message after updating the analysis
	 *         settings
	 */
	@RequestMapping(value = "Manage-settings/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String saveSettingManager(@RequestBody Map<String, String> currentSettings,
			HttpSession session, Model model, Principal principal, Locale locale) {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		final AnalysisType analysisType = analysis.getType();
		final boolean isFullCostRelatedOld = analysis.findSetting(AnalysisSetting.ALLOW_FULL_COST_RELATED_TO_MEASURE);
		final boolean isILR = Analysis.isILR(analysis);

		currentSettings.forEach((key, value) -> {
			final AnalysisSetting setting = AnalysisSetting.valueOf(key);
			if (setting != null && setting.isSupported(analysisType))
				analysis.setSetting(setting.name(), Analysis.findSetting(setting, value));
		});

		if (isFullCostRelatedOld != (boolean) analysis
				.findSetting(AnalysisSetting.ALLOW_FULL_COST_RELATED_TO_MEASURE)) {
			computeMeasureCost(analysis);
		}

		if (!isILR && Analysis.isILR(analysis)) {
			DependencyGraphManager.computeImpact(analysis.getAssetNodes());
		}

		serviceAnalysis.saveOrUpdate(analysis);

		return JsonMessage.success(messageSource.getMessage("success.update.analysis.settings", null, locale));
	}

	/**
	 * Handles the "/Section" request and returns a String.
	 *
	 * @param request   the HttpServletRequest object
	 * @param principal the Principal object representing the currently
	 *                  authenticated user
	 * @param model     the Model object used to pass data to the view
	 * @return a String representing the result of the request
	 * @throws Exception if an error occurs during the request handling
	 */
	@RequestMapping("/Section")
	public String section(HttpServletRequest request, Principal principal, Model model) throws Exception {
		return loadUserAnalyses(request.getSession(), principal, model, null);
	}

	// *****************************************************************
	// * import form and import action
	// *****************************************************************

	/**
	 * Handles the section request for displaying analysis by customer.
	 *
	 * @param idCustomer The ID of the customer.
	 * @param name       The name of the analysis.
	 * @param session    The HttpSession object.
	 * @param principal  The Principal object.
	 * @param model      The Model object.
	 * @return The result of loading user analyses.
	 * @throws Exception if an error occurs during the process.
	 */
	@RequestMapping(value = "/DisplayByCustomer/{idCustomer}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String section(@PathVariable("idCustomer") Integer idCustomer, @RequestBody String name, HttpSession session,
			Principal principal, Model model) throws Exception {
		if (!StringUtils.hasText(name))
			name = "ALL";
		session.setAttribute(CURRENT_CUSTOMER, idCustomer);
		session.setAttribute(FILTER_ANALYSIS_NAME, name);
		User user = serviceUser.get(principal.getName());
		if (user == null)
			throw new AccessDeniedException("Access denied");
		user.setSetting(LAST_SELECTED_ANALYSIS_NAME, name);
		user.setSetting(LAST_SELECTED_CUSTOMER_ID, idCustomer);
		serviceUser.saveOrUpdate(user);
		return loadUserAnalyses(session, principal, model, user);
	}

	/**
	 * Selects an analysis based on the provided analysis ID and performs necessary
	 * operations.
	 *
	 * @param model      the model object for the view
	 * @param principal  the principal object representing the currently
	 *                   authenticated user
	 * @param analysisId the ID of the analysis to be selected
	 * @param open       the mode in which the analysis should be opened (default is
	 *                   "edit")
	 * @param session    the HttpSession object for storing session attributes
	 * @param locale     the locale object representing the user's preferred
	 *                   language
	 * @return the name of the view to be rendered
	 * @throws Exception if an error occurs during the selection process
	 */
	@RequestMapping("/{analysisId}/Select")
	public String selectAnalysis(Model model, Principal principal, @PathVariable("analysisId") Integer analysisId,
			@RequestParam(value = "open", defaultValue = "edit") String open,
			HttpSession session, Locale locale) throws Exception {
		// select the analysis
		OpenMode mode = OpenMode.parseOrDefault(open);
		session.setAttribute(SELECTED_ANALYSIS, analysisId);
		session.setAttribute(OPEN_MODE, mode);
		Analysis analysis = serviceAnalysis.get(analysisId);
		if (analysis == null)
			throw new ResourceNotFoundException(
					messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		User user = serviceUser.get(principal.getName());
		ValueFactory valueFactory = new ValueFactory(analysis.getParameters());
		boolean readOnly = OpenMode.isReadOnly(mode);

		boolean hasMaturity = false;
		boolean hasPermission = analysis.isProfile() ? user.isAutorised(RoleType.ROLE_CONSULTANT)
				: readOnly ? true : permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.READ);
		if (hasPermission) {
			Collections.reverse(analysis.getHistories());
			Collections.sort(analysis.getItemInformations(), new ComparatorItemInformation());
			final List<Standard> standards = analysis.findStandards();
			final boolean isILR = Analysis.isILR(analysis);
			final Map<String, List<Measure>> measuresByStandard = mapMeasures(analysis.getAnalysisStandards().values());
			hasMaturity = measuresByStandard.containsKey(Constant.STANDARD_MATURITY);
			model.addAttribute("soaThreshold",
					analysis.findParameter(PARAMETERTYPE_TYPE_SINGLE_NAME, SOA_THRESHOLD, 100.0));
			model.addAttribute("soas",
					analysis.getAnalysisStandards().values().stream().filter(AnalysisStandard::isSoaEnabled)
							.collect(Collectors.toMap(AnalysisStandard::getStandard,
									analysisStandard -> measuresByStandard
											.get(analysisStandard.getStandard().getName()).stream()
											.filter(b -> !(b.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
													|| b.getStatus().equals(Constant.MEASURE_STATUS_OPTIONAL)))
											.toList(),
									(e1, e2) -> e1, LinkedHashMap::new)));
			model.addAttribute("measuresByStandard", measuresByStandard);
			model.addAttribute("show_uncertainty", analysis.isUncertainty());
			model.addAttribute("type", analysis.getType());
			model.addAttribute("standards", standards);
			model.addAttribute("showHiddenComment", analysis.findSetting(AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT));

			if (analysis.isQualitative()) {
				model.addAttribute("showRawColumn",
						analysis.findSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN));
				model.addAttribute("estimations",
						Estimation.GenerateEstimation(analysis, valueFactory, Estimation.IdComparator()));
				setupQualitativeParameterUI(model, analysis);
			}

			if (analysis.isQuantitative()) {
				boolean showDynamicAnalysis = analysis.findSetting(AnalysisSetting.ALLOW_DYNAMIC_ANALYSIS);
				if (showDynamicAnalysis) {
					boolean showExcludeDynamic = analysis.findSetting(AnalysisSetting.ALLOW_EXCLUDE_DYNAMIC_ANALYSIS);
					model.addAttribute("showExcludeDynamic",
							showExcludeDynamic);
					if (showExcludeDynamic) {
						final List<String> idsNames = serviceIDS.getPrefixesByAnalysisId(analysis.getId());
						Map<String, List<String>> excludesMap = analysis.getExcludeAcronyms().stream()
								.map(e -> serviceExternalNotification.extractPrefixAndCategory(e, idsNames))
								.filter(e -> e.length == 2).collect(Collectors.groupingBy(e -> e[0],
										Collectors.mapping(e -> e[1], Collectors.toList())));

						if (excludesMap.keySet().size() < 2) {
							excludesMap.forEach((k, v) -> model.addAttribute("excludeAcronyms",
									serviceExternalNotification.findLastSeverities(k, v)));
						} else {
							final Map<String, Double> excludesValues = new LinkedHashMap<>();
							excludesMap.forEach(
									(k, v) -> excludesValues
											.putAll(serviceExternalNotification.findLastSeverities(k, v)));
							model.addAttribute("excludeAcronyms", excludesValues);
						}

					}
				}
				model.addAttribute("showDynamicAnalysis", showDynamicAnalysis);
			}

			if (analysis.isHybrid() && hasMaturity) {
				model.addAttribute("effectImpl27002",
						MeasureManager.computeMaturiyEfficiencyRate(measuresByStandard.get(Constant.STANDARD_27002),
								measuresByStandard.get(Constant.STANDARD_MATURITY),
								analysis.findByGroup(Constant.PARAMETER_CATEGORY_SIMPLE,
										Constant.PARAMETER_CATEGORY_MATURITY),
								true, valueFactory));
				model.addAttribute("hasMaturity", hasMaturity);
			}

			PhaseManager.updateStatistics(measuresByStandard.values(), valueFactory,
					(double) model.asMap().get("soaThreshold"));

			model.addAttribute("totalPhase", PhaseManager.computeTotal(analysis.getPhases()));

			if (!analysis.isProfile()) {
				final Map<String, List<RiskInformation>> riskInformations = RiskInformationManager
						.Split(analysis.getRiskInformations());
				riskInformations.computeIfAbsent(Constant.RI_TYPE_RISK, k -> Collections.emptyList());
				riskInformations.computeIfAbsent(Constant.RI_TYPE_VUL, k -> Collections.emptyList());
				riskInformations.computeIfAbsent(Constant.RI_TYPE_THREAT, k -> Collections.emptyList());
				model.addAttribute("riskInformationSplited", riskInformations);
			}

			if (isILR) {
				DependencyGraphManager.computeImpact(analysis.getAssetNodes());
				model.addAttribute("assetNodes", analysis.getAssetNodes().stream()
						.collect(Collectors.toMap(e -> e.getAsset().getId(), Function.identity())));
			}

			analysis.getAssets().sort(Comparators.ASSET());
			analysis.getHistories()
					.sort((a1, a2) -> NaturalOrderComparator.compareTo(a1.getVersion(), a2.getVersion()) * -1);
			model.addAttribute("standardChapters", spliteMeasureByChapter(measuresByStandard));
			model.addAttribute("valueFactory", valueFactory);
			model.addAttribute("open", mode);
			model.addAttribute("analysis", analysis);
			model.addAttribute("login", user.getLogin());
			model.addAttribute("reportSettings", loadReportSettings(analysis));
			model.addAttribute("exportFilenames", loadExportFileNames(analysis));
			model.addAttribute("isILR", isILR);
			loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, user);

			/**
			 * Log
			 */
			TrickLogManager.persist(LogType.ANALYSIS,
					readOnly ? "log.open.analysis"
							: mode == OpenMode.EDIT ? "log.edit.analysis" : "log.edit.analysis.measure",
					String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()),
					user.getLogin(), readOnly ? LogAction.OPEN : LogAction.EDIT,
					analysis.getIdentifier(), analysis.getVersion());
		} else {
			TrickLogManager.persist(LogLevel.ERROR, LogType.ANALYSIS, "log.analysis.access_deny",
					String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()),
					user.getLogin(), LogAction.DENY_ACCESS, analysis.getIdentifier(),
					analysis.getVersion());
			throw new AccessDeniedException(
					messageSource.getMessage("error.not_authorized", null, "Insufficient permissions!", locale));
		}
		return "jsp/analyses/single/home";
	}

	// *****************************************************************
	// * export and download
	// *****************************************************************

	/**
	 * Selects the analysis with the specified analysisId and sets the open mode for
	 * the session.
	 * 
	 * @param principal  the principal object representing the currently
	 *                   authenticated user
	 * @param analysisId the ID of the analysis to be selected
	 * @param open       the open mode for the session (default value is
	 *                   "read-only")
	 * @param session    the HttpSession object for storing session attributes
	 * @return true if the analysis was successfully selected, false otherwise
	 * @throws Exception if an error occurs during the selection process
	 */
	@RequestMapping(value = "/{analysisId}/SelectOnly", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody boolean selectOnly(Principal principal, @PathVariable("analysisId") Integer analysisId,
			@RequestParam(value = "open", defaultValue = "read-only") String open, HttpSession session)
			throws Exception {
		// select the analysis
		session.setAttribute(SELECTED_ANALYSIS, analysisId);
		session.setAttribute(OPEN_MODE, OpenMode.parseOrDefault(open));
		return session.getAttribute(SELECTED_ANALYSIS) == analysisId;
	}

	/**
	 * Updates the ALE (Annual Loss Expectancy) for the selected analysis.
	 * 
	 * @param session the HttpSession object
	 * @param locale  the Locale object representing the user's locale
	 * @return a String representing the result of the update operation
	 * @throws Exception if an error occurs during the update process
	 */
	@RequestMapping(value = "/Update/ALE", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String update(HttpSession session, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return JsonMessage.error(messageSource.getMessage("error.analysis.no_selected", null,
					"There is no selected analysis", locale));
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			AssessmentAndRiskProfileManager.UpdateAssetALE(analysis, null);
			serviceAnalysis.saveOrUpdate(analysis);
			return JsonMessage.success(messageSource.getMessage("success.analysis.ale.update", null,
					"ALE was successfully updated", locale));
		} catch (TrickException e) {
			return JsonMessage.error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			return JsonMessage.error(
					messageSource.getMessage("error.analysis.ale.update", null, "ALE cannot be updated", locale));
		}
	}

	/**
	 * Finds the ID associated with the given name in the provided JSON node.
	 *
	 * @param jsonNode the JSON node to search in
	 * @param name     the name to search for
	 * @return the ID associated with the name, or -1 if not found
	 */
	private int findId(JsonNode jsonNode, String name) {
		return jsonNode.has(name) ? jsonNode.get(name).asInt() : -1;
	}

	/**
	 * Loads the user analyses and returns a String representing the view name.
	 * 
	 * @param session   the HttpSession object
	 * @param principal the Principal object representing the currently
	 *                  authenticated user
	 * @param model     the Model object for adding attributes to the view
	 * @param user      the User object representing the currently authenticated
	 *                  user
	 * @return a String representing the view name
	 * @throws Exception if an error occurs during the loading of user analyses
	 */
	private String loadUserAnalyses(HttpSession session, Principal principal, Model model, User user) throws Exception {
		List<String> names = null;
		Integer customer = (Integer) session.getAttribute(CURRENT_CUSTOMER);
		List<Customer> customers = serviceCustomer.getAllNotProfileOfUser(principal.getName());
		String nameFilter = (String) session.getAttribute(FILTER_ANALYSIS_NAME);
		if (customer == null || !StringUtils.hasText(nameFilter)) {
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
			if (!StringUtils.hasText(nameFilter)) {
				nameFilter = user.getSetting(LAST_SELECTED_ANALYSIS_NAME);
				if (!StringUtils.hasText(nameFilter) && customer != null) {
					names = serviceAnalysis.getNamesByUserAndCustomerAndNotEmpty(principal.getName(), customer);
					if (!names.isEmpty()) {
						user.setSetting(LAST_SELECTED_ANALYSIS_NAME, nameFilter = names.get(0));
						serviceUser.saveOrUpdate(user);
					}
				}
			}
		}

		if (customer != null) {
			if (names == null || names.isEmpty())
				names = serviceAnalysis.getNamesByUserAndCustomerAndNotEmpty(principal.getName(), customer);
			// load model with objects by the selected customer
			if (!StringUtils.hasText(nameFilter) || nameFilter.equalsIgnoreCase("ALL") || !names.contains(nameFilter))
				model.addAttribute("analyses",
						serviceAnalysis.getAllNotEmptyFromUserAndCustomer(principal.getName(), customer));
			else
				model.addAttribute("analyses", serviceAnalysis
						.getAllByUserAndCustomerAndNameAndNotEmpty(principal.getName(), customer, nameFilter));
			loadUserSettings(principal, serviceTicketingSystem.findByCustomerId(customer), model, user);
		}

		model.addAttribute("names", names);
		model.addAttribute("analysisSelectedName", StringUtils.hasText(nameFilter) ? nameFilter : "ALL");
		model.addAttribute("customer", customer);
		model.addAttribute("customers", customers);
		model.addAttribute("login", principal.getName());
		model.addAttribute("allowIDS", serviceIDS.exists(true));
		return "jsp/analyses/all/home";
	}

	/**
	 * Maps a collection of analysis standards to a map of measures.
	 * The measures are sorted using a comparator before being added to the map.
	 *
	 * @param standards the collection of analysis standards
	 * @return a map of measures, where the key is the name of the standard and the
	 *         value is a list of measures
	 */
	private Map<String, List<Measure>> mapMeasures(Collection<AnalysisStandard> standards) {
		final Comparator<Measure> comparator = new MeasureComparator();
		final Map<String, List<Measure>> measuresmap = new LinkedHashMap<String, List<Measure>>();
		for (AnalysisStandard standard : standards) {
			Collections.sort(standard.getMeasures(), comparator);
			measuresmap.put(standard.getStandard().getName(), standard.getMeasures());
		}
		return measuresmap;
	}

	/**
	 * Saves the analysis with the specified ID, owner, contents, errors, and
	 * locale.
	 * 
	 * @param id       The ID of the analysis.
	 * @param owner    The owner of the analysis.
	 * @param contents The JSON contents of the analysis.
	 * @param errors   The map to store any errors encountered during the save
	 *                 process.
	 * @param locale   The locale for error messages.
	 * @return True if the analysis was successfully saved, false otherwise.
	 */
	private boolean save(int id, User owner, JsonNode contents, Map<String, String> errors, Locale locale) {
		try {

			final Analysis analysis = serviceAnalysis.get(id);

			final Language language = serviceLanguage.get(findId(contents, "language"));

			final String label = (contents.has("label") ? contents.get("label").asText("") : "").trim();

			boolean uncertainty = contents.has("uncertainty") ? !contents.get("uncertainty").asText().isEmpty() : false;

			Customer customer = serviceCustomer.get(findId(contents, "customer"));

			final AnalysisType type;

			if (analysis == null) {
				errors.put("analysis",
						messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
				type = null;
			} else {

				type = contents.has("type")
						? AnalysisType.valueOf(contents.get("type").asText(analysis.getType().name()).trim())
						: analysis.getType();

				final List<ScaleType> scaleTypes = analysis.findImpacts();
				final boolean hasQuantitativeScale = scaleTypes.stream()
						.anyMatch(e -> e.getName().contains(Constant.DEFAULT_IMPACT_NAME));
				final boolean hasQualitativeScale = !scaleTypes.isEmpty()
						&& scaleTypes.stream().anyMatch(e -> !e.getName().contains(Constant.DEFAULT_IMPACT_NAME));

				if (type == null)
					errors.put("type", messageSource.getMessage("error.analysis.type.not_valid", null,
							"Analysis type is not valid", locale));
				else if ((type == AnalysisType.QUALITATIVE || type == AnalysisType.HYBRID) && !hasQualitativeScale)
					errors.put("type", messageSource.getMessage("error.analysis.type.qualitative", null,
							"Please add a qualitative impact and try again! See Analysis -> Settings-> Manage impact scales.",
							locale));
				else if ((type == AnalysisType.QUANTITATIVE || type == AnalysisType.HYBRID) && !hasQuantitativeScale) {
					errors.put("type", messageSource.getMessage("error.analysis.type.quantitative", null,
							"Please add the total consequence impact and try again! See Analysis -> Settings-> Manage impact scales.",
							locale));
				}

				if (!analysis.isProfile()) {

					if (customer == null || !customer.isCanBeUsed() || !owner.containsCustomer(customer))
						errors.put("customer", messageSource.getMessage("error.customer.not_valid", null,
								"Customer is not valid", locale));
					else {
						if (!serviceAnalysis.isAnalysisCustomer(id, customer.getId()))
							customerManager.switchCustomer(serviceAnalysis.getIdentifierByIdAnalysis(id),
									customer.getId(), owner.getLogin());
						if (customer.getId() != analysis.getCustomer().getId())
							analysis.setCustomer(customer);
					}
				}

				if (label.isEmpty())
					errors.put("label",
							messageSource.getMessage("error.comment.null", null, "Comment cannot be empty", locale));
				else if (!(analysis.getLabel().equalsIgnoreCase(label) || customer == null)) {
					String identifier = serviceAnalysis.findIdentifierByCustomerAndLabel(customer.getId(), label);
					if (!(identifier == null || analysis.getIdentifier().equalsIgnoreCase(identifier))) {
						String error = analysis.isProfile()
								? messageSource.getMessage("error.analysis_profile.name_in_used", null,
										"Another analysis profile with the same description already exists", locale)
								: messageSource.getMessage("error.analysis.name.in_used.for.customer",
										new Object[] { customer.getOrganisation() },
										String.format("Name cannot be used for %s", customer.getOrganisation()),
										locale);
						errors.put("label", error);
					}
				}
			}

			if (language == null)
				errors.put("language",
						messageSource.getMessage("error.language.not_valid", null, "Language is not valid", locale));

			if (!errors.isEmpty())
				return false;

			analysis.setType(type);
			analysis.setLanguage(language);
			analysis.setUncertainty(analysis.getType() == AnalysisType.QUALITATIVE ? false : uncertainty);
			if (analysis.getId() > 0 && !analysis.isProfile())
				AssessmentAndRiskProfileManager.updateRiskDendencies(analysis, null);

			if (!analysis.getLabel().equalsIgnoreCase(label)) {
				serviceAnalysis.getAllByIdentifier(analysis.getIdentifier()).forEach(tmpAnalysis -> {
					String name = tmpAnalysis.getLabel();
					tmpAnalysis.setLabel(label);
					serviceAnalysis.saveOrUpdate(tmpAnalysis);
					/**
					 * Log
					 */
					TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.rename.analysis",
							String.format("Analysis: %s, version: %s, old: %s, new: %s ", analysis.getIdentifier(),
									analysis.getVersion(), name, tmpAnalysis.getLabel()),
							owner.getLogin(), LogAction.RENAME, analysis.getIdentifier(), analysis.getVersion(), name,
							tmpAnalysis.getLabel());
				});

			} else {
				serviceAnalysis.saveOrUpdate(analysis);
				/**
				 * Log
				 */
				TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.user.edit.analysis.information",
						String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()),
						owner.getLogin(), LogAction.UPDATE, analysis.getIdentifier(),
						analysis.getVersion());
			}

			return true;
		} catch (TrickException e) {
			errors.put("analysis", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.persist(e);
		} catch (Exception e) {
			errors.put("analysis",
					messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.persist(e);
		}
		return false;
	}

	// ******************************************************************************************************************
	// * Actions
	// ******************************************************************************************************************

	/**
	 * Computes the cost for each measure in the given analysis.
	 *
	 * @param analysis The analysis for which to compute the measure cost.
	 */
	private void computeMeasureCost(final Analysis analysis) {
		final ValueFactory factory = new ValueFactory(analysis.getExpressionParameters());
		final boolean isFullCostRelated = analysis.findSetting(AnalysisSetting.ALLOW_FULL_COST_RELATED_TO_MEASURE);
		final double internalSetupRate = analysis.findParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE);
		final double externalSetupRate = analysis.findParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE);
		final double defaultLifetime = analysis.findParameter(Constant.PARAMETER_LIFETIME_DEFAULT);
		analysis.getAnalysisStandards().values().stream().flatMap(e -> e.getMeasures().stream())
				.forEach(m -> {
					final double implementationRate = m.getImplementationRateValue(factory) * 0.01;
					final double cost = Analysis.computeCost(internalSetupRate, externalSetupRate, defaultLifetime,
							m.getInternalMaintenance(), m.getExternalMaintenance(),
							m.getRecurrentInvestment(), m.getInternalWL(), m.getExternalWL(),
							m.getInvestment(), m.getLifetime(), implementationRate, isFullCostRelated);
					m.setCost(cost > 0D ? cost : 0D);
				});
	}

	/**
	 * Splits the given map of measures by standard into a nested map of measures by
	 * chapter.
	 *
	 * @param measuresByStandard a map of measures grouped by standard
	 * @return a nested map of measures grouped by standard and chapter
	 */
	private Map<String, Map<String, List<Measure>>> spliteMeasureByChapter(
			Map<String, List<Measure>> measuresByStandard) {
		Map<String, Map<String, List<Measure>>> mapper = new LinkedHashMap<>();
		measuresByStandard.forEach((standard, measures) -> {
			Map<String, List<Measure>> chapters = new LinkedHashMap<>();
			measures.forEach(measure -> {
				String chapter = ActionPlanComputation
						.extractMainChapter(measure.getMeasureDescription().getReference());
				List<Measure> measuresByChpater = chapters.get(chapter);
				if (measuresByChpater == null)
					chapters.put(chapter, measuresByChpater = new LinkedList<Measure>());
				measuresByChpater.add(measure);
			});

			mapper.put(standard, chapters);
		});
		return mapper;
	}
}
