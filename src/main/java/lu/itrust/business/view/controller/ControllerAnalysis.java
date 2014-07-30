package lu.itrust.business.view.controller;

import java.io.File;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.History;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MaturityMeasure;
import lu.itrust.business.TS.MaturityNorm;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureNorm;
import lu.itrust.business.TS.MeasureProperties;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.export.ExportAnalysisReport;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.AppSettingEntry;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.usermanagement.UserSQLite;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.Duplicator;
import lu.itrust.business.component.GeneralComperator;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.dao.hbm.DAOHibernate;
import lu.itrust.business.exception.ResourceNotFoundException;
import lu.itrust.business.exception.TrickException;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;
import lu.itrust.business.service.ServiceActionPlan;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceActionPlanType;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAppSettingEntry;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceHistory;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceNorm;
import lu.itrust.business.service.ServiceRiskRegister;
import lu.itrust.business.service.ServiceRole;
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
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private ServiceNorm serviceNorm;

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
	private ServiceAppSettingEntry serviceAppSettingEntry;

	@Autowired
	private ServiceRole serviceRole;

	// ******************************************************************************************************************
	// * Request mappers
	// ******************************************************************************************************************

	// *****************************************************************
	// * default request (/Analysis) display selected or all analyses
	// *****************************************************************

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
	public String displayAll(Principal principal, Model model, HttpSession session, RedirectAttributes attributes, Locale locale) throws Exception {

		// retrieve analysisId if an analysis was already selected
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");

		// check if an analysis is selected
		if (selected != null) {

			Boolean permissiondenied = false;

			// prepare permission evaluator
			PermissionEvaluatorImpl permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceAnalysis, serviceUserAnalysisRight);

			Analysis analysis = serviceAnalysis.get(selected);

			if (analysis == null) {
				attributes.addFlashAttribute("errors", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
				throw new ResourceNotFoundException((String) attributes.getFlashAttributes().get("errors"));
			}
			
			
			AppSettingEntry settings = serviceAppSettingEntry.getByUsernameAndGroupAndName(principal.getName(), "analysis", selected.toString());
			
			if (settings != null) {
				model.addAttribute("show_uncertainty", settings.findByKey("show_uncertainty"));
				model.addAttribute("show_cssf", settings.findByKey("show_cssf"));
			}

			User user = serviceUser.get(principal.getName());

			permissiondenied = analysis.isProfile() ? user.hasRole(RoleType.ROLE_CONSULTANT) || user.hasRole(RoleType.ROLE_ADMIN) : permissionEvaluator.userIsAuthorized(selected,
					principal, AnalysisRight.READ);

			if (permissiondenied) {
				// initialise and send data to the data model
				Hibernate.initialize(analysis.getLanguage());
				model.addAttribute("login", principal.getName());
				model.addAttribute("language", analysis.getLanguage().getAlpha3());
				model.addAttribute("analysis", analysis);
				model.addAttribute("KowledgeBaseView", analysis.isProfile());
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
		return "analysis/analyse";
	}

	@RequestMapping(value = "/Add/Standard", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String addStandardForm(HttpSession session, Principal principal, Model model, RedirectAttributes attributes, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<Norm> norms = serviceNorm.getAllNotInAnalysis(idAnalysis);
		if (norms.isEmpty()) {
			attributes.addFlashAttribute("error",
					messageSource.getMessage("error.analysis.add.standard", null, "Unfortunately, you cannot append a new standard to this analysis", locale));
			return "redirect:/Error";
		}
		model.addAttribute("norms", norms);
		model.addAttribute("currentNorms", serviceNorm.getAllFromAnalysis(idAnalysis));
		model.addAttribute("idAnalysis", idAnalysis);
		return "analysis/components/forms/standard";
	}

	@RequestMapping(value = "/Save/Standard/{idStandard}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String addStandard(@PathVariable int idStandard, HttpSession session, Principal principal, RedirectAttributes attributes, Locale locale) throws Exception {
		try {
			Norm norm = serviceNorm.get(idStandard);
			if (norm == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.add.standard.not_found", null, "Unfortunately, selected standard does not exist", locale));
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			Measure measure = null;
			AnalysisNorm analysisNorm = null;
			List<MeasureDescription> measureDescriptions = serviceMeasureDescription.getAllByNorm(norm);
			Object implementationRate = null;
			if (norm.getLabel().equals(Constant.NORM_MATURITY)) {
				analysisNorm = new MaturityNorm();
				measure = new MaturityMeasure();
				for (Parameter parameter : analysis.getParameters()) {
					if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME) && parameter.getValue() == 0) {
						implementationRate = parameter;
						break;
					}
				}
			} else {
				analysisNorm = new MeasureNorm();
				measure = new NormMeasure();
				List<AssetType> assetTypes = serviceAssetType.getAll();
				List<AssetTypeValue> assetTypeValues = ((NormMeasure) measure).getAssetTypeValues();
				for (AssetType assetType : assetTypes)
					assetTypeValues.add(new AssetTypeValue(assetType, 0));
				((NormMeasure) measure).setMeasurePropertyList(new MeasureProperties());
				implementationRate = new Double(0);
			}
			Phase phase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
			if (phase == null)
				analysis.addUsedPhase(phase = new Phase(Constant.PHASE_DEFAULT));
			measure.setPhase(phase);
			analysisNorm.setAnalysis(analysis);
			analysisNorm.setNorm(norm);
			measure.setImplementationRate(implementationRate);
			for (MeasureDescription measureDescription : measureDescriptions) {
				Measure measure2 = measure.duplicate();
				measure2.setMeasureDescription(measureDescription);
				measure2.setAnalysisNorm(analysisNorm);
				analysisNorm.getMeasures().add(measure2);
			}
			analysis.addAnalysisNorm(analysisNorm);

			serviceAnalysis.saveOrUpdate(analysis);

			return JsonMessage.Success(messageSource.getMessage("success.analysis.add.standard", null, "A standard was successfully added", locale));
		} catch (TrickException e) {
			return JsonMessage.Success(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.analysis.add.standard", null, "An unknown error occurred during analysis saving", locale));
		}
	}

	/**
	 * manageaccessrights: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{analysisID}/ManageAccess")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisID, #principal, T(lu.itrust.business.TS.AnalysisRight).ALL)")
	public String manageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model, HttpSession session) throws Exception {

		Map<User, AnalysisRight> userrights = new LinkedHashMap<>();

		Analysis analysis = serviceAnalysis.get(analysisID);

		List<UserAnalysisRight> uars = analysis.getUserRights();

		for (User user : serviceUser.getAll())
			userrights.put(DAOHibernate.Initialise(user), null);

		for (UserAnalysisRight uar : uars)
			userrights.put(DAOHibernate.Initialise(uar.getUser()), DAOHibernate.Initialise(uar.getRight()));

		model.addAttribute("currentUser", (DAOHibernate.Initialise(serviceUser.get(principal.getName())).getId()));
		model.addAttribute("analysisRights", AnalysisRight.values());
		model.addAttribute("analysis", analysis);
		model.addAttribute("userrights", userrights);
		return "analysis/forms/manageUserAnalysisRights";
	}

	/**
	 * updatemanageaccessrights: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{analysisID}/ManageAccess/Update")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisID, #principal, T(lu.itrust.business.TS.AnalysisRight).ALL)")
	public String updatemanageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model, @RequestBody String value, Locale locale) throws Exception {

		try {

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);

			Map<User, AnalysisRight> userrights = new LinkedHashMap<>();

			Analysis analysis = serviceAnalysis.get(analysisID);

			List<UserAnalysisRight> uars = analysis.getUserRights();

			for (User user : serviceUser.getAll())
				userrights.put(DAOHibernate.Initialise(user), null);

			for (UserAnalysisRight uar : uars)
				userrights.put(DAOHibernate.Initialise(uar.getUser()), DAOHibernate.Initialise(uar.getRight()));

			int currentUser = jsonNode.get("userselect").asInt();

			model.addAttribute("currentUser", currentUser);

			for (User user : serviceUser.getAll()) {

				if (user.getLogin().equals(principal.getName()))
					continue;

				int useraccess = jsonNode.get("analysisRight_" + user.getId()).asInt();

				UserAnalysisRight uar = analysis.getRightsforUser(user);

				if (uar != null) {

					if (useraccess == -1) {
						analysis.removeRights(user);
						serviceUserAnalysisRight.delete(uar);
						serviceAnalysis.saveOrUpdate(analysis);
						userrights.put(user, null);
					} else {
						uar.setRight(AnalysisRight.valueOf(useraccess));
						serviceUserAnalysisRight.saveOrUpdate(uar);
						serviceAnalysis.saveOrUpdate(analysis);
						userrights.put(user, uar.getRight());
					}
				} else {

					if (!user.getCustomers().contains(analysis.getCustomer()))
						user.addCustomer(analysis.getCustomer());

					if (useraccess != -1) {
						uar = new UserAnalysisRight(user, analysis, AnalysisRight.valueOf(useraccess));
						serviceUserAnalysisRight.save(uar);
						serviceAnalysis.saveOrUpdate(analysis);
						userrights.put(user, uar.getRight());
					}

				}
			}
			model.addAttribute("success",
					messageSource.getMessage("label.analysis.manage.users.success", null, "Analysis access rights, EXPECT your own, were successfully updated!", locale));
			model.addAttribute("analysisRights", AnalysisRight.values());
			model.addAttribute("analysis", analysis);
			model.addAttribute("userrights", userrights);
			return "analysis/forms/manageUserAnalysisRights";
		} catch (Exception e) {
			// return errors
			model.addAttribute("errors", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return "analysis/forms/manageUserAnalysisRights";
		}
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

		return "analysis/analyses";
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
		return "analysis/analyses";
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
	 */
	@RequestMapping(value = "/Update/ALE", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String update(HttpSession session, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", locale));
			assessmentManager.UpdateAssetALE(analysis);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.ale.update", null, "ALE was successfully updated", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			e.printStackTrace();
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
		session.removeAttribute("selectedAnalysis");
		return "redirect:/Analysis";
	}

	// *****************************************************************
	// * request create new analysis
	// *****************************************************************

	/**
	 * requestnewAnalysis: <br>
	 * Description
	 * 
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/New")
	public String requestnewAnalysis(Principal principal, Map<String, Object> model) throws Exception {

		// add languages
		model.put("languages", serviceLanguage.getAll());

		// add only customers of the current user
		model.put("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));

		model.put("profiles", serviceAnalysis.getAllProfiles());
		// set author as the username

		User user = serviceUser.get(principal.getName());

		model.put("author", user.getFirstName() + " " + user.getLastName());

		return "analysis/forms/newAnalysis";
	}

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

			return "analysis/forms/editAnalysis";
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
	public @ResponseBody String deleteAnalysis(@PathVariable("analysisId") int analysisId, RedirectAttributes attributes, Locale locale, Principal principal, HttpSession session)
			throws Exception {
		try {

			Analysis analysis = serviceAnalysis.getDefaultProfile();

			if (analysis.getId() == analysisId)
				return JsonMessage.Error(messageSource.getMessage("error.profile.delete.failed", null, "Default profile cannot be deleted!", locale));

			// delete the analysis
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

		return "analysis/forms/newVersion";
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
	public @ResponseBody Map<String, String> createNewVersion(@RequestBody String value, BindingResult result, @PathVariable int analysisId, Principal principal, Locale locale)
			throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();

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

			// duplicate analysis
			Duplicator duplicator = new Duplicator();
			Analysis copy = duplicator.duplicateAnalysis(analysis, null);
			// attribute new values to new analysis
			copy.setBasedOnAnalysis(analysis);
			copy.addAHistory(history);
			copy.setVersion(history.getVersion());
			copy.setLabel(analysis.getLabel());
			copy.setCreationDate(new Timestamp(System.currentTimeMillis()));
			copy.setProfile(false);
			copy.setDefaultProfile(false);

			// save the new version
			serviceAnalysis.saveOrUpdate(copy);

		} catch (CloneNotSupportedException e) {
			// return dubplicate error message
			e.printStackTrace();
			errors.put("analysis", messageSource.getMessage("error.analysis.duplicate", null, "Analysis cannot be duplicated!", locale));
		} catch (TrickException e) {
			errors.put("analysis", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return general error message
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
		return "analysis/forms/importAnalysis";
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

		// retrieve the customer
		Customer customer = serviceCustomer.get(customerId);

		// if the customer or the file are not correct
		if (customer == null || file.isEmpty()) {
			attributes.addFlashAttribute("errors", messageSource.getMessage("error.customer_or_file.import.analysis", null, "Customer or file are not set or empty!", locale));
			return "analysis/forms/importAnalysis";
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
	public String exportReport(@PathVariable Integer analysisId, HttpServletResponse response, HttpServletRequest request, RedirectAttributes attributes, Principal principal,
			Locale locale) throws Exception {

		File file = null;

		try {

			ExportAnalysisReport exportAnalysisReport = new ExportAnalysisReport();

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

			analysis = serviceAnalysis.get(id);

			int idLanguage = jsonNode.has("analysislanguage") ? jsonNode.get("analysislanguage").asInt() : -1;
			String comment = jsonNode.has("label") ? jsonNode.get("label").asText() : "";
			int idCustomer = jsonNode.has("analysiscustomer") ? jsonNode.get("analysiscustomer").asInt() : -1;

			if (analysis != null) {
				Language lang = serviceLanguage.get(idLanguage);
				if (analysis.isProfile()) {

					analysis.setLanguage(lang);
					analysis.setLabel(comment);
				} else {

					analysis = serviceAnalysis.get(id);

					if (analysis == null) {
						errors.put("analysis", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
						return false;
					}

					analysis.setLabel(comment);

					Customer customer = serviceCustomer.get(idCustomer);
					analysis.setCustomer(customer);

					analysis.setLanguage(lang);

				}
				serviceAnalysis.saveOrUpdate(analysis);
			} else {
				int idProfile = jsonNode.has("profile") ? jsonNode.get("profile").asInt() : -1;
				String author = jsonNode.has("author") ? jsonNode.get("author").asText() : "";
				String version = jsonNode.has("version") ? jsonNode.get("version").asText() : "";
				if (idCustomer < 1)
					errors.put("analysiscustomer", messageSource.getMessage("error.customer.null", null, "Customer cannot be empty", locale));
				if (idLanguage < 1)
					errors.put("analysislanguage", messageSource.getMessage("error.language.null", null, "Language cannot be empty", locale));
				if (comment.trim().isEmpty())
					errors.put("comment", messageSource.getMessage("error.comment.null", null, "Comment cannot be empty", locale));
				if (author.trim().isEmpty() && id < 1)
					errors.put("author", messageSource.getMessage("error.author.null", null, "Author cannot be empty", locale));

				if (version.trim().isEmpty())
					errors.put("version", messageSource.getMessage("error.version.null", null, "Version cannot be empty", locale));
				else if (!version.matches(Constant.REGEXP_VALID_ANALYSIS_VERSION))
					errors.put("version", messageSource.getMessage("error.version.invalid", null, "Invalid version format, Please respect this format (0.0.1)", locale));

				if (!errors.isEmpty())
					return false;

				Customer customer = serviceCustomer.get(idCustomer);
				Language language = serviceLanguage.get(idLanguage);
				String label = jsonNode.get("label").asText();
				Date date = new Date();
				Timestamp creationDate = new Timestamp(date.getTime());
				String ts = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(creationDate);
				String identifier = language.getAlpha3() + "_" + ts;

				analysis = null;

				Analysis profile = null;

				// create from profile
				profile = serviceAnalysis.get(idProfile);
				if (profile == null)
					profile = serviceAnalysis.getDefaultProfile();

				analysis = new Duplicator().duplicateAnalysis(profile, null);
				analysis.setProfile(false);
				analysis.setDefaultProfile(false);
				if (analysis.getAnalysisNorms().size() > 0)
					analysis.setData(true);

				analysis.getHistories().clear();
				analysis.setBasedOnAnalysis(null);
				analysis.setCreationDate(creationDate);
				analysis.setCustomer(customer);

				analysis.setIdentifier(identifier.toUpperCase());
				analysis.setLabel(label);
				analysis.setLanguage(language);
				analysis.setOwner(owner);
				analysis.setVersion(version);
				History history = new History(version, date, author, comment);
				analysis.addAHistory(history);
				UserAnalysisRight uar = new UserAnalysisRight(owner, analysis, AnalysisRight.ALL);
				analysis.addUserRight(uar);
				serviceAnalysis.save(analysis);

				if (session != null)
					session.setAttribute("currentCustomer", customer.getOrganisation());
			}
			return true;
		} catch (Exception e) {
			errors.put("analysis", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		}
	}
}
