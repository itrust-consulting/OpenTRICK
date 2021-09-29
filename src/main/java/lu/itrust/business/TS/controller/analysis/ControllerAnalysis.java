package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.ANALYSIS_TASK_ID;
import static lu.itrust.business.TS.constants.Constant.CURRENT_CUSTOMER;
import static lu.itrust.business.TS.constants.Constant.FILTER_ANALYSIS_NAME;
import static lu.itrust.business.TS.constants.Constant.LAST_SELECTED_ANALYSIS_NAME;
import static lu.itrust.business.TS.constants.Constant.LAST_SELECTED_CUSTOMER_ID;
import static lu.itrust.business.TS.constants.Constant.OPEN_MODE;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_SINGLE_NAME;
import static lu.itrust.business.TS.constants.Constant.ROLE_MIN_USER;
import static lu.itrust.business.TS.constants.Constant.SELECTED_ANALYSIS;
import static lu.itrust.business.TS.constants.Constant.SELECTED_ANALYSIS_LANGUAGE;
import static lu.itrust.business.TS.constants.Constant.SOA_THRESHOLD;

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
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerCreateAnalysisVersion;
import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.CustomerManager;
import lu.itrust.business.TS.component.MeasureManager;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceIDS;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceTicketingSystem;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.actionplan.helper.ActionPlanComputation;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.helper.Estimation;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.general.helper.PhaseManager;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.iteminformation.helper.ComparatorItemInformation;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.riskinformation.helper.RiskInformationManager;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureComparator;
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
@RequestMapping("/Analysis")
@Controller
public class ControllerAnalysis extends AbstractController {

	@Autowired
	private ServiceUser serviceUser;

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

	@Value("${app.settings.upload.file.max.size}")
	private Long maxUploadFileSize;

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
	@PreAuthorize("@permissionEvaluator.hasPermission(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public String addHistory(@PathVariable("analysisId") Integer analysisId, Map<String, Object> model, Principal principal, HttpSession session) throws Exception {
		// retrieve user
		User user = serviceUser.get(principal.getName());
		// add data to model
		model.put("oldVersion", serviceAnalysis.getVersionOfAnalysis(analysisId));
		model.put("analysisId", analysisId);
		model.put("author", user.getFirstName() + " " + user.getLastName());
		return "analyses/all/forms/newVersion";
	}

	@RequestMapping("/All")
	public String AllAnalysis(Model model, Principal principal, HttpSession session) throws Exception {
		session.removeAttribute(OPEN_MODE);
		session.removeAttribute(SELECTED_ANALYSIS);
		session.removeAttribute(SELECTED_ANALYSIS_LANGUAGE);
		return LoadUserAnalyses(session, principal, model, null);
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
	@RequestMapping(value = "/Archive/{analysisId}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.hasPermission(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).ALL)")
	public @ResponseBody String archiving(@PathVariable Integer analysisId, HttpServletRequest request, Principal principal, Locale locale) {
		try {
			Analysis analysis = serviceAnalysis.get(analysisId);
			if (analysis.isProfile())
				return JsonMessage.Success(messageSource.getMessage("error.archive.profile", null, "An analysis profile cannot be archived", locale));
			else if (analysis.isArchived())
				return JsonMessage.Success(messageSource.getMessage("error.archived.analysis", null, "An analysis is already archived", locale));
			analysis.setArchived(true);
			serviceAnalysis.saveOrUpdate(analysis);
			TrickLogManager.Persist(LogType.ANALYSIS, "log.archive.analysis", String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()),
					principal.getName(), LogAction.ARCHIVE, analysis.getIdentifier(), analysis.getVersion());
			return JsonMessage.Success(messageSource.getMessage("success.analysis.archived", null, "Analysis has been successfully archived", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
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
	@PreAuthorize("@permissionEvaluator.hasPermission(#analysisId, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Map<String, String> createNewVersion(@RequestBody String value, BindingResult result, @PathVariable int analysisId, Principal principal, Locale locale)
			throws Exception {

		final Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			// retrieve analysis object
			final Analysis analysis = serviceAnalysis.get(analysisId);

			// check if object is not null
			if (analysis == null)
				errors.put("analysis", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", locale));

			final String lastVersion = serviceAnalysis.getAllVersion(analysis.getIdentifier()).stream().sorted((v0, v1) -> {
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

			final Worker worker = new WorkerCreateAnalysisVersion(analysisId, history, principal.getName());
			// register worker to tasklist
			if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
				executor.execute(worker);
				errors.put(ANALYSIS_TASK_ID, worker.getId());
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
	@PreAuthorize("@permissionEvaluator.hasDeletePermission(#analysisId, #principal, false)")
	public @ResponseBody String deleteAnalysis(@PathVariable("analysisId") int analysisId, RedirectAttributes attributes, Principal principal, HttpSession session, Locale locale)
			throws Exception {
		try {
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

	// *****************************************************************
	// * request create new analysis
	// *****************************************************************

	// *****************************************************************
	// * request edit analysis
	// *****************************************************************

	@RequestMapping(value = "Manage-settings", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String loadSettingManager(HttpSession session, Model model, Principal principal, Locale locale) {
		Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Map<String, String> currentSettings = serviceAnalysis.getSettingsByIdAnalysis(integer);
		Map<AnalysisSetting, Object> settings = new LinkedHashMap<>();
		AnalysisType analysisType = serviceAnalysis.getAnalysisTypeById(integer);
		for (AnalysisSetting setting : AnalysisSetting.values()) {
			if (!setting.isSupported(analysisType))
				continue;
			settings.put(setting, Analysis.findSetting(setting, currentSettings.get(setting.name())));
		}
		// load all assets of analysis to model
		model.addAttribute("settings", settings);
		return "analyses/single/components/settings/form";
	}

	// *****************************************************************
	// * save or update analysis object
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
	// * delete analysis
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
			final PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);
			final JsonNode contents = new ObjectMapper().readTree(value);
			// retrieve analysis id to compute
			final int analysisId = contents.get("id").asInt();
			// check if it is a new analysis or the user is authorized to modify
			// the analysis
			final User user = serviceUser.get(principal.getName());

			final boolean isProfile = serviceAnalysis.isProfile(analysisId);

			if (permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.ALL) || isProfile && user.isAutorised(RoleType.ROLE_CONSULTANT)) {
				save(analysisId, serviceUser.get(principal.getName()), contents, errors, locale);
			} else
				// throw error
				throw new AccessDeniedException(messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));
		} catch (TrickException e) {
			errors.put("analysis", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			errors.put("owner", messageSource.getMessage("error.user.not_found", null, "User cannot be found", locale));
			TrickLogManager.Persist(e);
		}
		return errors;
	}

	// *****************************************************************
	// * create new version of analysis
	// *****************************************************************

	@RequestMapping(value = "Manage-settings/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String saveSettingManager(@RequestBody Map<String, String> currentSettings, HttpSession session, Model model, Principal principal, Locale locale) {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		final AnalysisType analysisType = analysis.getType();
		currentSettings.forEach((key, value) -> {
			final AnalysisSetting setting = AnalysisSetting.valueOf(key);
			if (setting != null && setting.isSupported(analysisType))
				analysis.setSetting(setting.name(), Analysis.findSetting(setting, value));
		});
		serviceAnalysis.saveOrUpdate(analysis);
		return JsonMessage.Success(messageSource.getMessage("success.update.analysis.settings", null, locale));
	}

	@RequestMapping("/Section")
	public String section(HttpServletRequest request, Principal principal, Model model) throws Exception {
		return LoadUserAnalyses(request.getSession(), principal, model, null);
	}

	// *****************************************************************
	// * import form and import action
	// *****************************************************************

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
		return LoadUserAnalyses(session, principal, model, user);
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

		boolean hasMaturity = false;
		boolean hasPermission = analysis.isProfile() ? user.isAutorised(RoleType.ROLE_CONSULTANT)
				: readOnly ? true : permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.MODIFY);
		if (hasPermission) {
			Collections.reverse(analysis.getHistories());
			Collections.sort(analysis.getItemInformations(), new ComparatorItemInformation());
			final List<Standard> standards = analysis.findStandards();
			final Map<String, List<Measure>> measuresByStandard = mapMeasures(analysis.getAnalysisStandards().values());
			hasMaturity = measuresByStandard.containsKey(Constant.STANDARD_MATURITY);
			model.addAttribute("soaThreshold", analysis.findParameter(PARAMETERTYPE_TYPE_SINGLE_NAME, SOA_THRESHOLD, 100.0));
			model.addAttribute("soas",
					analysis.getAnalysisStandards().values().stream().filter(AnalysisStandard::isSoaEnabled)
							.collect(Collectors.toMap(analysisStandard -> analysisStandard.getStandard(),
									analysisStandard -> measuresByStandard.get(analysisStandard.getStandard().getName()), (e1, e2) -> e1, LinkedHashMap::new)));
			model.addAttribute("measuresByStandard", measuresByStandard);
			model.addAttribute("show_uncertainty", analysis.isUncertainty());
			model.addAttribute("type", analysis.getType());
			model.addAttribute("standards", standards);
			model.addAttribute("showHiddenComment", analysis.findSetting(AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT));

			if (analysis.isQualitative()) {
				model.addAttribute("showRawColumn", analysis.findSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN));
				model.addAttribute("estimations", Estimation.GenerateEstimation(analysis, valueFactory, Estimation.IdComparator()));
				setupQualitativeParameterUI(model, analysis);
			}

			if (analysis.isQuantitative())
				model.addAttribute("showDynamicAnalysis", analysis.findSetting(AnalysisSetting.ALLOW_DYNAMIC_ANALYSIS));

			if (analysis.isHybrid() && hasMaturity) {
				model.addAttribute("effectImpl27002",
						MeasureManager.ComputeMaturiyEfficiencyRate(measuresByStandard.get(Constant.STANDARD_27002), measuresByStandard.get(Constant.STANDARD_MATURITY),
								analysis.findByGroup(Constant.PARAMETER_CATEGORY_SIMPLE, Constant.PARAMETER_CATEGORY_MATURITY), true, valueFactory));
				model.addAttribute("hasMaturity", hasMaturity);
			}

			PhaseManager.updateStatistics(measuresByStandard.values(), valueFactory, (double) model.asMap().get("soaThreshold"));

			model.addAttribute("totalPhase", PhaseManager.computeTotal(analysis.getPhases()));

			if (!analysis.isProfile()) {
				final Map<String, List<RiskInformation>> riskInformations = RiskInformationManager.Split(analysis.getRiskInformations());
				if (!riskInformations.containsKey(Constant.RI_TYPE_RISK))
					riskInformations.put(Constant.RI_TYPE_RISK, Collections.emptyList());
				if (!riskInformations.containsKey(Constant.RI_TYPE_VUL))
					riskInformations.put(Constant.RI_TYPE_VUL, Collections.emptyList());
				if (!riskInformations.containsKey(Constant.RI_TYPE_THREAT))
					riskInformations.put(Constant.RI_TYPE_THREAT, Collections.emptyList());
				model.addAttribute("riskInformationSplited", riskInformations);
			}
			analysis.getHistories().sort((a1, a2) -> NaturalOrderComparator.compareTo(a1.getVersion(), a2.getVersion()) * -1);
			model.addAttribute("standardChapters", spliteMeasureByChapter(measuresByStandard));
			model.addAttribute("valueFactory", valueFactory);
			model.addAttribute("open", mode);
			model.addAttribute("analysis", analysis);
			model.addAttribute("login", user.getLogin());
			model.addAttribute("reportSettings", loadReportSettings(analysis));
			loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, user);

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

	// *****************************************************************
	// * export and download
	// *****************************************************************

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
			AssessmentAndRiskProfileManager.UpdateAssetALE(analysis, null);
			serviceAnalysis.saveOrUpdate(analysis);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.ale.update", null, "ALE was successfully updated", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.analysis.ale.update", null, "ALE cannot be updated", locale));
		}
	}

	private int findId(JsonNode jsonNode, String name) {
		return jsonNode.has(name) ? jsonNode.get(name).asInt() : -1;
	}

	private String LoadUserAnalyses(HttpSession session, Principal principal, Model model, User user) throws Exception {
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
				model.addAttribute("analyses", serviceAnalysis.getAllNotEmptyFromUserAndCustomer(principal.getName(), customer));
			else
				model.addAttribute("analyses", serviceAnalysis.getAllByUserAndCustomerAndNameAndNotEmpty(principal.getName(), customer, nameFilter));
			loadUserSettings(principal, serviceTicketingSystem.findByCustomerId(customer), model, user);
		}

		model.addAttribute("names", names);
		model.addAttribute("analysisSelectedName", StringUtils.hasText(nameFilter) ?  nameFilter : "ALL" );
		model.addAttribute("customer", customer);
		model.addAttribute("customers", customers);
		model.addAttribute("login", principal.getName());
		model.addAttribute("allowIDS", serviceIDS.exists(true));
		return "analyses/all/home";
	}

	/**
	 * mapMeasures: <br>
	 * Description
	 * 
	 * @param standards
	 * @return
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
	 * buildAnalysis: <br>
	 * Description
	 * 
	 * @param id
	 * @param owner
	 * @param contents
	 * @param errors
	 * @param locale
	 * @param session
	 * 
	 * @return
	 */
	private boolean save(int id, User owner, JsonNode contents, Map<String, String> errors, Locale locale) {
		try {

			Analysis analysis = serviceAnalysis.get(id);

			Language language = serviceLanguage.get(findId(contents, "language"));

			String label = (contents.has("label") ? contents.get("label").asText("") : "").trim();

			boolean uncertainty = contents.has("uncertainty") ? !contents.get("uncertainty").asText().isEmpty() : false;

			Customer customer = serviceCustomer.get(findId(contents, "customer"));

			if (analysis == null)
				errors.put("analysis", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
			else {
				if (!analysis.isProfile()) {
					if (customer == null || !customer.isCanBeUsed() || !owner.containsCustomer(customer))
						errors.put("customer", messageSource.getMessage("error.customer.not_valid", null, "Customer is not valid", locale));
					else {
						if (!serviceAnalysis.isAnalysisCustomer(id, customer.getId()))
							customerManager.switchCustomer(serviceAnalysis.getIdentifierByIdAnalysis(id), customer.getId(), owner.getLogin());
						if (customer.getId() != analysis.getCustomer().getId())
							analysis.setCustomer(customer);
					}
				}

				if (label.isEmpty())
					errors.put("label", messageSource.getMessage("error.comment.null", null, "Comment cannot be empty", locale));
				else if (!(analysis.getLabel().equalsIgnoreCase(label) || customer == null)) {
					String identifier = serviceAnalysis.findIdentifierByCustomerAndLabel(customer.getId(), label);
					if (!(identifier == null || analysis.getIdentifier().equalsIgnoreCase(identifier))) {
						String error = analysis.isProfile()
								? messageSource.getMessage("error.analysis_profile.name_in_used", null, "Another analysis profile with the same description already exists", locale)
								: messageSource.getMessage("error.analysis.name.in_used.for.customer", new Object[] { customer.getOrganisation() },
										String.format("Name cannot be used for %s", customer.getOrganisation()), locale);
						errors.put("label", error);
					}
				}
			}

			if (language == null)
				errors.put("language", messageSource.getMessage("error.language.not_valid", null, "Language is not valid", locale));

			if (!errors.isEmpty())
				return false;

			analysis.setLanguage(language);
			analysis.setUncertainty(analysis.getType() == AnalysisType.QUALITATIVE ? false : uncertainty);
			if (analysis.getId() > 0 && !analysis.isProfile())
				AssessmentAndRiskProfileManager.UpdateRiskDendencies(analysis, null);

			if (!analysis.getLabel().equalsIgnoreCase(label)) {
				serviceAnalysis.getAllByIdentifier(analysis.getIdentifier()).forEach(tmpAnalysis -> {
					String name = tmpAnalysis.getLabel();
					tmpAnalysis.setLabel(label);
					serviceAnalysis.saveOrUpdate(tmpAnalysis);
					/**
					 * Log
					 */
					TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.rename.analysis",
							String.format("Analysis: %s, version: %s, old: %s, new: %s ", analysis.getIdentifier(), analysis.getVersion(), name, tmpAnalysis.getLabel()),
							owner.getLogin(), LogAction.RENAME, analysis.getIdentifier(), analysis.getVersion(), name, tmpAnalysis.getLabel());
				});

			} else {
				serviceAnalysis.saveOrUpdate(analysis);
				/**
				 * Log
				 */
				TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.user.edit.analysis.information",
						String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), owner.getLogin(), LogAction.UPDATE, analysis.getIdentifier(),
						analysis.getVersion());
			}

			return true;
		} catch (TrickException e) {
			errors.put("analysis", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			errors.put("analysis", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}
		return false;
	}

	// ******************************************************************************************************************
	// * Actions
	// ******************************************************************************************************************

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
}
