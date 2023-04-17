package lu.itrust.business.ts.controller.administration;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.ts.component.CustomDelete;
import lu.itrust.business.ts.component.CustomerManager;
import lu.itrust.business.ts.component.DefaultReportTemplateLoader;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceCustomer;
import lu.itrust.business.ts.database.service.ServiceDataValidation;
import lu.itrust.business.ts.database.service.ServiceIDS;
import lu.itrust.business.ts.database.service.ServiceLanguage;
import lu.itrust.business.ts.database.service.ServiceMessageNotifier;
import lu.itrust.business.ts.database.service.ServiceReportTemplate;
import lu.itrust.business.ts.database.service.ServiceRole;
import lu.itrust.business.ts.database.service.ServiceTSSetting;
import lu.itrust.business.ts.database.service.ServiceTrickLog;
import lu.itrust.business.ts.database.service.ServiceTrickService;
import lu.itrust.business.ts.database.service.ServiceUser;
import lu.itrust.business.ts.exception.ResourceNotFoundException;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.form.AnalysisRightForm;
import lu.itrust.business.ts.form.CustomerForm;
import lu.itrust.business.ts.form.NotificationForm;
import lu.itrust.business.ts.form.ReportTemplateForm;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.helper.SwitchAnalysisOwnerHelper;
import lu.itrust.business.ts.model.TrickService;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.ts.model.analysis.helper.ManageAnalysisRight;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.EmailTemplate;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.TSSetting;
import lu.itrust.business.ts.model.general.TSSettingName;
import lu.itrust.business.ts.model.general.TicketingSystem;
import lu.itrust.business.ts.model.general.TicketingSystemType;
import lu.itrust.business.ts.model.general.document.impl.ReportTemplate;
import lu.itrust.business.ts.model.general.helper.EmailTemplateForm;
import lu.itrust.business.ts.model.general.helper.Notification;
import lu.itrust.business.ts.model.general.helper.TrickLogFilter;
import lu.itrust.business.ts.usermanagement.Role;
import lu.itrust.business.ts.usermanagement.RoleType;
import lu.itrust.business.ts.usermanagement.User;
import lu.itrust.business.ts.usermanagement.helper.UserDeleteHelper;
import lu.itrust.business.ts.validator.UserValidator;
import lu.itrust.business.ts.validator.field.ValidatorField;

/**
 * ControllerAdministration.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Dec 13, 2013
 */

@PreAuthorize(Constant.ROLE_MIN_ADMIN)
@RequestMapping("/Admin")
@Controller
public class ControllerAdmin {

	private static final String TRICK_LOG_FILTER = "trick-log-filter";

	private static final String LOG_FILTER_TYPE = "LOG-FILTER-TYPE";

	private static final String LOG_FILTER_LEVEL = "LOG-FILTER-LEVEL";

	private static final String LOG_FILTER_SORT_DIRECTION = "LOG-FILTER-SORT-DIRECTION";

	private static final String LOG_FILTER_PAGE_SIZE = "LOG-FILTER-PAGE-SIZE";

	private static final String LOG_FILTER_ACTION = "LOG-FILTER-ACTION";

	private static final String LOG_FILTER_AUTHOR = "LOG-FILTER-AUTHOR";

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceRole serviceRole;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceIDS serviceIDS;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceTrickService serviceTrickService;

	@Autowired
	private ManageAnalysisRight manageAnalysisRight;

	@Autowired
	private CustomerManager customerManager;

	@Autowired
	private ServiceTrickLog serviceTrickLog;

	@Autowired
	private ServiceTSSetting serviceTSSetting;

	@Autowired
	private ServiceMessageNotifier serviceMessageNotifier;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceReportTemplate serviceReportTemplate;

	@Autowired
	private DefaultReportTemplateLoader defaultReportTemplateLoader;

	@Value("${app.settings.otp.enable}")
	private boolean enabledOTP = true;

	@Value("${app.settings.otp.force}")
	private boolean forcedOTP = true;

	@Value("${app.settings.version}")
	private String version;

	private Long maxTemplateSize;

	private Long maxUploadFileSize;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String showAdministration(HttpSession session, Principal principal, Map<String, Object> model)
			throws Exception {
		model.put("users", serviceUser.getAll());
		model.put("IDSs", serviceIDS.getAll());
		List<Customer> customers = serviceCustomer.getAll();
		Integer customerID = (Integer) session.getAttribute("currentAdminCustomer");
		Integer profileId = null;
		if (customerID == null) {
			for (Customer customer : customers) {
				if (customer.isCanBeUsed()) {
					// use first customer as selected customer
					session.setAttribute("currentAdminCustomer", customerID = customer.getId());
					break;
				} else
					profileId = customer.getId();
			}
			if (customerID == null)
				customerID = profileId;
		} else {
			boolean find = false;
			for (Customer customer : customers) {
				if (customer.getId() == customerID) {
					find = true;
					break;
				} else if (!customer.isCanBeUsed())
					profileId = customer.getId();
			}
			if (!find)
				customerID = profileId;
		}
		model.put("status", getStatus());

		if (customers != null && customers.size() > 0) {
			model.put("customers", customers);
			List<Analysis> analyses = Collections.emptyList();
			if (customerID != null) {
				model.put("customer", customerID);
				analyses = serviceAnalysis.getAllFromCustomer(customerID);
				analyses.sort(new AnalysisComparator().reversed());
			}
			model.put("analyses", analyses);
		}

		final List<TSSetting> tsSettings = new LinkedList<>();
		final List<TSSetting> ticketingSystems = new LinkedList<>();

		for (TSSettingName name : TSSettingName.values()) {
			TSSetting tsSetting = serviceTSSetting.get(name);
			if (tsSetting == null) {
				switch (name) {
					case SETTING_ALLOWED_RESET_PASSWORD:
					case SETTING_ALLOWED_SIGNUP:
						tsSetting = new TSSetting(name, true);
						break;
					case USER_GUIDE_URL:
						tsSetting = new TSSetting(name, "/static/user-guide.html");
						break;
					case SETTING_ALLOWED_TICKETING_SYSTEM_LINK:
						tsSetting = new TSSetting(name, false);
						break;
					case USER_GUIDE_URL_TYPE:
					default:
						tsSetting = new TSSetting(name, null);
				}

				if (tsSetting != null)
					serviceTSSetting.saveOrUpdate(tsSetting);
			}

			if (tsSetting.getNameString().contains("TICKETING_SYSTEM"))
				ticketingSystems.add(tsSetting);
			else
				tsSettings.add(tsSetting);

		}
		boolean adminAllowedTicketing = ticketingSystems.stream()
				.anyMatch(e -> e.getName() == TSSettingName.SETTING_ALLOWED_TICKETING_SYSTEM_LINK && e.getBoolean());
		model.put(Constant.ADMIN_ALLOWED_TICKETING, adminAllowedTicketing);
		model.put("ticketingSystems", ticketingSystems);
		model.put("tsSettings", tsSettings);
		model.put("enabledOTP", enabledOTP);
		model.put("forcedOTP", forcedOTP);
		model.put("logFilter", loadLogFilter(session, principal.getName()));
		model.put("logLevels", serviceTrickLog.getDistinctLevel());
		model.put("logTypes", serviceTrickLog.getDistinctType());
		model.put("actions", serviceTrickLog.getDistinctAction());
		model.put("authors", serviceTrickLog.getDistinctAuthor());
		if (adminAllowedTicketing)
			model.put("ticketingTypes", TicketingSystemType.values());
		return "jsp/admin/administration";

	}

	private TrickLogFilter loadLogFilter(HttpSession session, String username) throws Exception {
		TrickLogFilter filter = (TrickLogFilter) session.getAttribute(TRICK_LOG_FILTER);
		if (filter != null)
			return filter;
		User user = serviceUser.get(username);
		filter = new TrickLogFilter(user.getInteger(LOG_FILTER_PAGE_SIZE), user.getSetting(LOG_FILTER_LEVEL),
				user.getSetting(LOG_FILTER_TYPE), user.getSetting(LOG_FILTER_ACTION),
				user.getSetting(LOG_FILTER_AUTHOR), user.getSetting(LOG_FILTER_SORT_DIRECTION));
		session.setAttribute(TRICK_LOG_FILTER, filter);
		return filter;
	}

	/**
	 * getStatus: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 */
	public TrickService getStatus() throws Exception {

		TrickService status = serviceTrickService.getStatus();

		boolean installed = false;

		if (status != null) {

			if (!status.isInstalled() && serviceAnalysis.hasDefault(AnalysisType.QUALITATIVE)
					&& serviceAnalysis.hasDefault(AnalysisType.QUANTITATIVE))
				status.setInstalled(true);

			if (version.equals(status.getVersion()))
				status.setVersion(version);

			serviceTrickService.saveOrUpdate(status);

			return status;

		}

		status = new TrickService(version, installed);

		status.setInstalled(serviceAnalysis.hasDefault(AnalysisType.QUANTITATIVE)
				&& serviceAnalysis.hasDefault(AnalysisType.QUALITATIVE));

		serviceTrickService.saveOrUpdate(status);

		return status;

	}

	@RequestMapping(value = "/TSSetting/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody boolean updateSetting(@RequestBody TSSetting tsSetting, Principal principal, Locale locale) {
		if (tsSetting.getName() == null)
			return false;
		try {
			TSSetting setting = serviceTSSetting.get(tsSetting.getName());
			if (setting == null)
				setting = tsSetting;
			else
				setting.setValue(tsSetting.getValue());
			if (ObjectUtils.isEmpty(setting.getValue()) && !tsSetting.getName().equals(TSSettingName.USER_GUIDE_URL)) {
				if (!setting.equals(tsSetting))
					serviceTSSetting.delete(tsSetting.getName().name());
			} else
				serviceTSSetting.saveOrUpdate(setting);
			return true;
		} finally {
			String settingName = messageSource.getMessage("label." + tsSetting.getNameLower(), null,
					tsSetting.getNameLower(), Locale.ENGLISH);
			TrickLogManager.Persist(LogLevel.INFO, LogType.ADMINISTRATION, "log.setting.change",
					String.format("Settings: %s, value: %s", settingName, tsSetting.getString()),
					principal.getName(), LogAction.CHANGE, settingName, tsSetting.getString());
		}

	}

	@RequestMapping(value = "/Analysis/{idAnalysis}/Switch/Owner", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String uiSwitchAnalysisOwner(@PathVariable Integer idAnalysis, Model model, Principal principal,
			RedirectAttributes attributes, Locale locale) {
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis == null || analysis.isProfile()) {
				attributes.addFlashAttribute("error", messageSource.getMessage("error.action.not_authorise", null,
						"Action does not authorised", locale));
				return "redirect:/Error";
			}
			model.addAttribute("analysis", analysis);
			Map<User, AnalysisRight> userAnalysisRights = new LinkedHashMap<User, AnalysisRight>();
			analysis.getUserRights()
					.forEach(userRight -> userAnalysisRights.put(userRight.getUser(), userRight.getRight()));
			serviceUser.getAllOthers(userAnalysisRights.keySet()).forEach(user -> userAnalysisRights.put(user, null));
			userAnalysisRights.remove(analysis.getOwner());
			model.addAttribute("userAnalysisRights", userAnalysisRights);
			return "jsp/admin/analysis/switch-owner";
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		}
	}

	@RequestMapping(value = "/Analysis/{idAnalysis}/Switch/Owner/{idOwner}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String switchAnalysisOwner(@PathVariable Integer idAnalysis, @PathVariable Integer idOwner,
			Model model, Principal principal,
			RedirectAttributes attributes, Locale locale) {
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis == null || analysis.isProfile())
				return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null,
						"Action does not authorised", locale));
			else if (analysis.getOwner().getId() == idOwner)
				return JsonMessage
						.Success(messageSource.getMessage("info.nothing.changed", null, "Nothing was changed", locale));
			User owner = serviceUser.get(idOwner);
			if (owner == null)
				return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null,
						"Action does not authorised", locale));
			new SwitchAnalysisOwnerHelper(serviceAnalysis).switchOwner(principal, analysis, owner);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.switch.owner", null,
					"Analysis owner was successfully updated", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(
					messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
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
	@RequestMapping("/Analysis/DisplayByCustomer/{customerSection}")
	public String section(@PathVariable Integer customerSection, HttpSession session, Principal principal, Model model)
			throws Exception {
		List<Analysis> analyses = serviceAnalysis.getAllFromCustomer(customerSection);
		Collections.sort(analyses, Collections.reverseOrder(new AnalysisComparator()));
		session.setAttribute("currentAdminCustomer", customerSection);
		model.addAttribute("customer", customerSection);
		model.addAttribute("analyses", analyses);
		model.addAttribute("customers", serviceCustomer.getAll());
		return "jsp/admin/analysis/analyses";
	}

	@RequestMapping(value = "/Analysis/Delete", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody boolean deleteAnalysis(@RequestBody List<Integer> ids, Principal principal,
			HttpSession session) {
		try {
			Integer selected = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (selected != null && ids.contains(selected))
				session.removeAttribute(Constant.SELECTED_ANALYSIS);
			return customDelete.deleteAnalysis(ids, principal.getName());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	@RequestMapping(value = "/Log/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String sectionLog(@RequestParam(defaultValue = "1") Integer page, HttpSession session, Principal principal,
			Model model) {
		try {
			model.addAttribute("trickLogs", serviceTrickLog.getAll(page, loadLogFilter(session, principal.getName())));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
		return "jsp/admin/log/section";
	}

	@RequestMapping(value = "/Log/Filter/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String updateLogFilter(@RequestBody TrickLogFilter logFilter, Principal principal,
			HttpSession session, Locale locale) {
		try {
			User user = serviceUser.get(principal.getName());
			user.setSetting(LOG_FILTER_LEVEL, logFilter.getLevel());
			user.setSetting(LOG_FILTER_TYPE, logFilter.getType());
			user.setSetting(LOG_FILTER_AUTHOR, logFilter.getAuthor());
			user.setSetting(LOG_FILTER_ACTION, logFilter.getAction());
			user.setSetting(LOG_FILTER_PAGE_SIZE, logFilter.getSize());
			user.setSetting(LOG_FILTER_SORT_DIRECTION, logFilter.getDirection());
			session.setAttribute(TRICK_LOG_FILTER, logFilter);
			serviceUser.saveOrUpdate(user);
			return JsonMessage.Success(messageSource.getMessage("success.filter.updated", null,
					"Filter has been successfully updated", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.invalid.data", null, "Invalid data", locale));
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
	@RequestMapping("/Analysis/{analysisID}/ManageAccess")
	public String manageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model)
			throws Exception {
		Map<User, AnalysisRight> userrights = new LinkedHashMap<>();
		Analysis analysis = serviceAnalysis.get(analysisID);
		if (!analysis.isProfile()) {
			List<UserAnalysisRight> uars = analysis.getUserRights();
			serviceUser.getAll().forEach(user -> userrights.put(user, null));
			uars.forEach(uar -> userrights.put(uar.getUser(), uar.getRight()));
			model.addAttribute("isAdmin", true);
			model.addAttribute("analysis", analysis);
			model.addAttribute("userrights", userrights);
			model.addAttribute("ownerId", analysis.getOwner().getId());
			model.addAttribute("myId", serviceUser.get(principal.getName()).getId());
			return "jsp/analyses/all/forms/rights";
		} else {
			return "redirect:Administration";
		}
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
	@RequestMapping(value = "/Analysis/ManageAccess/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String updatemanageaccessrights(@RequestBody AnalysisRightForm rightsForm, Principal principal,
			Locale locale) throws Exception {
		try {
			manageAnalysisRight.updateAnalysisRights(principal, rightsForm);
			return JsonMessage.Success(messageSource.getMessage("success.update.analysis.right", null,
					"Analysis access rights were successfully updated!", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (e instanceof TrickException)
				return JsonMessage.Error(messageSource.getMessage(((TrickException) e).getCode(),
						((TrickException) e).getParameters(), e.getMessage(), locale));
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@RequestMapping("/Analysis/{analysisId}/Switch/Customer")
	public String switchCUstomerForm(@PathVariable("analysisId") int analysisId, Principal principal, Model model,
			RedirectAttributes attributes, Locale locale) throws Exception {
		model.addAttribute("idAnalysis", analysisId);
		model.addAttribute("currentCustomers", serviceAnalysis.getCustomersByIdAnalysis(analysisId));
		model.addAttribute("customers", serviceCustomer.getAllNotProfiles());
		return "jsp/admin/analysis/switch-customer";
	}

	@RequestMapping(value = "/Analysis/{idAnalysis}/Switch/Customer/{idCustomer}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String switchCUstomerForm(@PathVariable("idAnalysis") int idAnalysis,
			@PathVariable("idCustomer") int idCustomer, Principal principal, Locale locale)
			throws Exception {
		String identifier = serviceAnalysis.getIdentifierByIdAnalysis(idAnalysis);
		if (identifier == null)
			return JsonMessage.Error(
					messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		else if (serviceCustomer.isProfile(idCustomer))
			return JsonMessage.Error(
					messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
		else if (!serviceCustomer.exists(idCustomer))
			return JsonMessage.Error(
					messageSource.getMessage("error.customer.not_found", null, "Customer cannot be found", locale));
		customerManager.switchCustomer(identifier, idCustomer, principal.getName());
		return JsonMessage.Success(
				messageSource.getMessage("success.analyses.updated", null, "Analyses have been updated", locale));
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/User/Section", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String userSection(Model model, HttpSession session, Principal principal) {
		model.addAttribute("users", serviceUser.getAll());
		model.addAttribute("enabledOTP", enabledOTP);
		model.addAttribute("forcedOTP", forcedOTP);
		return "jsp/admin/user/users";
	}

	/**
	 * getAllRoles: <br>
	 * Description
	 * 
	 * @param userId
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/User/Add", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String getAllRoles(Map<String, Object> model, HttpSession session) {
		model.put("roles", RoleType.ROLES);
		model.put("enabledOTP", enabledOTP);
		model.put("forcedOTP", forcedOTP);
		model.put("user", new User());
		return "jsp/admin/user/form";

	}

	/**
	 * manageUserRole: <br>
	 * Description
	 * 
	 * @param userId
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/User/Edit/{userId}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String getUserRoles(@PathVariable("userId") int userId, Map<String, Object> model, HttpSession session) {
		model.put("user", serviceUser.get(userId));
		model.put("enabledOTP", enabledOTP);
		model.put("forcedOTP", forcedOTP);
		model.put("roles", RoleType.ROLES);
		return "jsp/admin/user/form";

	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param user
	 * @param result
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/User/Save", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> saveUser(@RequestBody String value, Locale locale, Principal principal) {

		Map<String, String> errors = new LinkedHashMap<>();
		try {
			List<Role> userRoles = new LinkedList<>();
			User user = buildUser(errors, value, locale, userRoles, principal);
			if (!errors.isEmpty())
				return errors;
			RoleType userAccess = user.getAccess();
			String userRole = userAccess == null ? "none" : userAccess.name().toLowerCase().replace("role_", "");
			if (user.getId() < 1) {
				serviceUser.save(user);
				/**
				 * Log
				 */
				TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.add.user",
						String.format("Target: %s, access: %s", user.getLogin(), userRole),
						principal.getName(), LogAction.CREATE, user.getLogin(),
						userAccess == null ? "none" : userAccess.name());
				// give access
				user.getRoles().stream().filter(role -> role.getType() != userAccess)
						.forEach(role -> TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION,
								"log.user.get.access",
								String.format("Target: %s, access: %s", user.getLogin(),
										role.getRoleName().toLowerCase()),
								principal.getName(), LogAction.GIVE_ACCESS,
								user.getLogin(), role.getType().name()));
				errors.put("success", messageSource.getMessage("success.user.created", null,
						"User was successfully created", locale));
			} else {
				serviceUser.saveOrUpdate(user);
				/**
				 * Log
				 */
				// remove access
				userRoles.stream().filter(role -> !user.hasRole(role))
						.forEach(role -> TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION,
								"log.user.remove.access",
								String.format("Target: %s, access: %s", user.getLogin(),
										role.getRoleName().toLowerCase()),
								principal.getName(), LogAction.REMOVE_ACCESS,
								user.getLogin(), role.getType().name()));
				// give access
				user.getRoles().stream().filter(role -> !userRoles.contains(role))
						.forEach(role -> TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION,
								"log.user.grant.access",
								String.format("Target: %s, access: %s", user.getLogin(),
										role.getRoleName().toLowerCase()),
								principal.getName(), LogAction.GRANT_ACCESS,
								user.getLogin(), role.getType().name()));
				errors.put("success",
						messageSource.getMessage("success.user.update", null, "User was successfully updated", locale));
			}
		} catch (TrickException e) {
			errors.put("user", messageSource.getMessage(e.getCode(),
					e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			errors.put("user",
					messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
		return errors;
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/User/{idUser}/Prepare-to-delete", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String deleteUser(@PathVariable int idUser, Model model, RedirectAttributes attributes, Locale locale) {
		try {
			User user = serviceUser.get(idUser);
			if (user == null) {
				attributes.addFlashAttribute("error", messageSource.getMessage("error.action.not_authorise", null,
						"Action does not authorised", locale));
				return "redirect:/Error";
			}
			model.addAttribute("user", user);
			model.addAttribute("users", serviceUser.getAllOthers(user));
			model.addAttribute("analyses", serviceAnalysis.getAllFromOwner(user));
			return "jsp/admin/user/delete-dialog";
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		}
	}

	@PostMapping(value = "/User/Delete", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object deleteUser(@RequestBody UserDeleteHelper deleteHelper, Principal principal,
			Locale locale) {
		Map<Object, String> errors = new LinkedHashMap<>();
		try {
			customDelete.deleteUser(deleteHelper, errors, principal, messageSource, locale);
			if (errors.isEmpty())
				return JsonMessage.Success(
						messageSource.getMessage("success.delete.user", null, "User was successfully deleted", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			if (errors.isEmpty())
				return JsonMessage.Error(
						messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}
		return errors;
	}

	/**
	 * loadCustomerUsers: <br>
	 * Description
	 * 
	 * @param customerID
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/Customer/{customerID}/Manage-access")
	public String loadCustomerUsers(@PathVariable("customerID") int customerID, Model model, Principal principal)
			throws Exception {
		model.addAttribute("customer", serviceCustomer.get(customerID));
		model.addAttribute("users", serviceUser.getAll());
		model.addAttribute("customerUsers", serviceUser.getAllFromCustomer(customerID).stream()
				.collect(Collectors.toMap(User::getLogin, user -> true)));
		return "jsp/admin/customer/manage-access";
	}

	/**
	 * updateCustomerUsers: <br>
	 * Description
	 * 
	 * @param customerID
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@PostMapping(value = "/Customer/{customerID}/Manage-access/Update", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String updateCustomerUsers(@RequestBody Map<Integer, Boolean> accesses,
			@PathVariable("customerID") int customerID, Model model, Principal principal,
			Locale locale, RedirectAttributes redirectAttributes) throws Exception {
		// create errors list
		try {
			Customer customer = serviceCustomer.get(customerID);
			// create json parser
			serviceUser.getAll(accesses.keySet()).forEach(user -> {
				boolean userHasAccess = accesses.getOrDefault(user.getId(), false);
				if (userHasAccess) {
					if (!user.containsCustomer(customer)) {
						user.addCustomer(customer);
						serviceUser.saveOrUpdate(user);
						TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.give.access.to.customer",
								String.format("Customer: %s, target: %s", customer.getOrganisation(), user.getLogin()),
								principal.getName(), LogAction.GIVE_ACCESS,
								customer.getOrganisation(), user.getLogin());
					}
				} else
					customDelete.removeCustomerByUser(customerID, user.getLogin(), principal.getName());
			});
			return JsonMessage.Success(messageSource.getMessage("label.customer.manage.users.success", null,
					"Customer users successfully updated!", locale));
		} catch (Exception e) {
			// return errors
			TrickLogManager.Persist(e);
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@GetMapping(value = "/Customer/{customerID}/Email-template/Edit", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String editEmailTemplate(@PathVariable int customerID, Model model) {
		final Customer customer = serviceCustomer.get(customerID);
		if (customer == null)
			throw new ResourceNotFoundException();

		final TicketingSystem ticketingSystem = customer.getTicketingSystem();

		if (ticketingSystem == null || ticketingSystem.getType() != TicketingSystemType.EMAIL)
			throw new IllegalArgumentException("No ticketing system or invalid type");

		final EmailTemplate emailTemplate = ticketingSystem.getEmailTemplate();
		if (emailTemplate == null)
			model.addAttribute("form", new EmailTemplate());
		else
			model.addAttribute("form", emailTemplate);

		model.addAttribute("customerID", customerID);

		return "jsp/admin/customer/eamil-template";
	}

	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@PostMapping(value = "/Customer/{customerID}/Email-template/Save", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object editEmailTemplate(@PathVariable int customerID, @RequestBody EmailTemplateForm form,
			Locale locale) {

		final Customer customer = serviceCustomer.get(customerID);
		if (customer == null)
			throw new ResourceNotFoundException();

		final TicketingSystem ticketingSystem = customer.getTicketingSystem();
		if (ticketingSystem == null || ticketingSystem.getType() != TicketingSystemType.EMAIL)
			throw new IllegalArgumentException("No ticketing system or invalid type");

		final Map<String, Object> result = new HashMap<>();

		final Map<String, Object> errors = new HashMap<>();

		final EmailTemplate emailTemplate = ticketingSystem.getEmailTemplate();

		result.put("errors", errors);

		if (ObjectUtils.isEmpty(form.getEmail()))
			errors.put("email", messageSource.getMessage("error.email.template.email.empty", null,
					"Email cannot be empty", locale));
		else if (Constant.REGEXP_VALID_EMAIL.matches(form.getEmail()))
			errors.put("email", messageSource.getMessage("error.email.template.email.invalid", null,
					"Pleasse check the email address", locale));

		if (ObjectUtils.isEmpty(form.getTitle()))
			errors.put("title", messageSource.getMessage("error.email.template.title.empty", null,
					"Title cannot be empty", locale));

		if (ObjectUtils.isEmpty(form.getTemplate()))
			errors.put("template", messageSource.getMessage("error.email.template.template.empty", null,
					"Template cannot be empty", locale));

		if (form.getInternalTime() < 0)
			errors.put("internalTime", messageSource.getMessage("error.email.template.internal_time.negative", null,
					"Internal time cannot be negative", locale));

		if (errors.isEmpty()) {

			if (emailTemplate == null) {
				form.setId(0);
				ticketingSystem.setEmailTemplate(new EmailTemplate(form));
			} else
				emailTemplate.update(form);

			serviceCustomer.saveOrUpdate(customer);

			result.put("success", messageSource.getMessage("success.update.email.template", null,
					"Email template had been updated successfully", locale));
		}

		return result;

	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Customer/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String section(Model model, HttpSession session, Principal principal, HttpServletRequest request) {
		model.addAttribute(Constant.ADMIN_ALLOWED_TICKETING,
				serviceTSSetting.isAllowed(TSSettingName.SETTING_ALLOWED_TICKETING_SYSTEM_LINK));
		model.addAttribute("customers", serviceCustomer.getAll());
		return "jsp/admin/customer/customers";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "Customer/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> save(@RequestBody CustomerForm value, Principal principal, Locale locale) {
		Map<String, String> errors = new LinkedHashMap<>();
		try {
			Customer customer = customerManager.buildCustomer(errors, value, locale, true);
			if (!errors.isEmpty())
				return errors;
			User user = serviceUser.get(principal.getName());

			if (customer.getId() < 1) {
				if (customer.isCanBeUsed()) {
					user.addCustomer(customer);
					serviceUser.saveOrUpdate(user);
				} else if (!serviceCustomer.profileExists())
					serviceCustomer.save(customer);
				else
					errors.put("canBeUsed", messageSource.getMessage("error.customer.profile.duplicate", null,
							"A customer profile already exists", locale));
			} else if (customer.isCanBeUsed() && !serviceCustomer.isProfile(customer.getId())
					|| !(customer.isCanBeUsed() || serviceCustomer.isProfile(customer.getId()))
					|| !(serviceCustomer.hasUsers(customer.getId()) || customer.isCanBeUsed())
							&& (!serviceCustomer.profileExists() || serviceCustomer.isProfile(customer.getId())))
				serviceCustomer.saveOrUpdate(customer);
			else
				errors.put("canBeUsed",
						messageSource.getMessage("error.customer.profile.attach.user", null,
								"Only a customer who is not attached to a user can be used as profile", locale));

			/**
			 * Log
			 */
			if (errors.isEmpty())
				TrickLogManager.Persist(LogType.ANALYSIS, "log.add_or_update.customer",
						String.format("Customer: %s", customer.getOrganisation()), principal.getName(),
						LogAction.CREATE_OR_UPDATE, customer.getOrganisation());
		} catch (Exception e) {
			errors.put("customer",
					messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}
		return errors;
	}

	/**
	 * 
	 * Delete single customer
	 * 
	 */
	@RequestMapping(value = "Customer/{customerId}/Delete", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String deleteCustomer(@PathVariable("customerId") int customerId, Principal principal,
			HttpServletRequest request, Locale locale) throws Exception {
		try {
			customDelete.deleteCustomer(customerId, principal.getName());
			return JsonMessage.Success(messageSource.getMessage("success.customer.delete.successfully", null,
					"Customer was deleted successfully", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
	}

	@GetMapping(value = "Customer/{customerId}/Report-template/Manage", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String reportTemplateForm(@PathVariable("customerId") int customerId, Model model, Principal principal,
			Locale locale) {
		final Customer customer = serviceCustomer.get(customerId);
		if (customer == null || customer.isCanBeUsed())
			throw new AccessDeniedException(
					messageSource.getMessage("error.customer.not_exist", null, "Customer does not exist", locale));
		model.addAttribute("customer", customer);
		model.addAttribute("reportTemplates", defaultReportTemplateLoader.findAll());
		model.addAttribute("types", AnalysisType.values());
		model.addAttribute("languages", serviceLanguage.getByAlpha3("ENG", "FRA"));
		model.addAttribute("maxFileSize", Math.min(maxUploadFileSize, maxTemplateSize));
		return "jsp/knowledgebase/customer/form/report-template";
	}

	@PostMapping(value = "Customer/{customerId}/Report-template/Save", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object reportTemplateSave(@PathVariable("customerId") int customerId,
			@ModelAttribute ReportTemplateForm templateForm, Model model, Principal principal,
			Locale locale) {
		Customer customer = serviceCustomer.get(customerId);
		if (customer == null || customer.isCanBeUsed())
			throw new AccessDeniedException(
					messageSource.getMessage("error.customer.not_exist", null, "Customer does not exist", locale));
		ReportTemplate template = serviceReportTemplate.findByIdAndCustomer(templateForm.getId(), customerId);
		if (template == null)
			return JsonMessage.Error(
					messageSource.getMessage("error.customer.not_exist", null, "Customer does not exist", locale));
		Map<String, Object> result = new LinkedHashMap<>();
		try {
			long maxSize = Math.min(maxUploadFileSize, maxTemplateSize);
			if (templateForm.getFile().getSize() > maxSize)
				result.put("file", messageSource.getMessage("error.file.too.large", new Object[] { maxSize },
						"File is to large", locale));
			else {
				template.setName(templateForm.getFile().getOriginalFilename());
				template.setData(templateForm.getFile().getBytes());
				template.setLength(templateForm.getFile().getSize());
				if (!DefaultReportTemplateLoader.isDocx(templateForm.getFile().getInputStream()))
					result.put("file",
							messageSource.getMessage("error.file.no.docx", null, "Docx file is excepted", locale));
			}
		} catch (IOException e) {
			result.put("file",
					messageSource.getMessage("error.file.not.updated", null, "File cannot be loaded", locale));
		}

		if (template.getData() == null || template.getData().length == 0)
			result.put("file",
					messageSource.getMessage("error.report.template.file.empty", null, "File cannot be empty", locale));

		if (result.isEmpty()) {
			template.setEditable(false);
			template.setCreated(new Timestamp(System.currentTimeMillis()));
			serviceReportTemplate.saveOrUpdate(template);
			return JsonMessage.Success(messageSource.getMessage("success.report.template.update", null, locale));
		}
		return result;
	}

	/**
	 * download: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Customer/Report-template/{id}/Download")
	public String downloadReport(@PathVariable Long id, Principal principal, HttpServletResponse response,
			Locale locale) throws Exception {

		ReportTemplate reportTemplate = serviceReportTemplate.findOne(id);

		// if file could not be found retrun 404 error
		if (reportTemplate == null)
			return "jsp/errors/404";

		Customer customer = serviceCustomer.findByReportTemplateId(id);

		if (customer == null)
			return "jsp/errors/404";

		if (customer.isCanBeUsed())
			throw new AccessDeniedException(
					messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));

		// set response contenttype to sqlite
		response.setContentType("docx");

		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ String.format("%s_v%s.docx", reportTemplate.getLabel(), reportTemplate.getVersion()) + "\"");

		// set sqlite file size as response size
		response.setContentLength((int) reportTemplate.getLength());

		// return the sqlite file (as copy) to the response outputstream ( whihc
		// creates on the
		// client side the sqlite file)
		FileCopyUtils.copy(reportTemplate.getData(), response.getOutputStream());
		/**
		 * Log
		 */
		TrickLogManager.Persist(LogType.ANALYSIS, "log.customer.report.template.download",
				String.format("Customer: %s, Template: %s, version: %s, created at: %s, type: %s, Language: %s",
						customer.getContactPerson(), reportTemplate.getLabel(),
						reportTemplate.getVersion(), reportTemplate.getCreated(), reportTemplate.getType(),
						reportTemplate.getLanguage().getAlpha3()),
				principal.getName(), LogAction.DOWNLOAD, customer.getContactPerson(), reportTemplate.getLabel(),
				reportTemplate.getVersion(),
				String.valueOf(reportTemplate.getCreated()), String.valueOf(reportTemplate.getType()),
				reportTemplate.getLanguage().getAlpha3());

		// return
		return null;
	}

	@GetMapping(value = "/Notification/ALL", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody List<Notification> notification(Model model, Locale locale) {
		return serviceMessageNotifier.findAll();
	}

	@GetMapping(value = "/Notification/Add", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String add(Model model, Locale locale) {
		return edit(null, model, locale);
	}

	@GetMapping(value = "/Notification/{id}/Edit", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String edit(@PathVariable String id, Model model, Locale locale) {
		Notification notification = serviceMessageNotifier.findById(id);
		if (notification == null)
			model.addAttribute("form", new NotificationForm());
		else
			model.addAttribute("form", new NotificationForm(notification));
		model.addAttribute("langues", new Locale[] { Locale.FRENCH, Locale.ENGLISH });
		model.addAttribute("types", LogLevel.values());
		model.addAttribute("locale", locale);
		return "jsp/admin/notification/form";
	}

	@DeleteMapping(value = "/Notification/{id}/Delete", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object deleteNotification(@PathVariable String id, Locale locale) {
		serviceMessageNotifier.remove(id);
		return JsonMessage.Success(messageSource.getMessage("success.delete.message", null, locale));
	}

	@DeleteMapping(value = "/Notification/Clear", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object clearNotification(Locale locale) {
		serviceMessageNotifier.clear(null);
		return JsonMessage.Success(messageSource.getMessage("success.clear.notification", null,
				"Notifications had been successfully cleared", locale));
	}

	@PostMapping(value = "/Notification/Save", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object saveNotification(@RequestBody NotificationForm form, Locale locale) {
		Notification notification = form.getData();
		if (!notification.update())
			return JsonMessage.Error(messageSource.getMessage("error.notification.message.empty", null, locale));
		else {
			Notification old = serviceMessageNotifier.findById(notification.getId());
			if (old != null) {
				serviceMessageNotifier.notifyAll(updateMessages(old.update(notification)));
				form.setData(old);
			} else {
				updateMessages(notification);
				if (form.isAll())
					serviceMessageNotifier.notifyAll(notification);
				else
					form.getTargets().stream().map(id -> serviceUser.findUsernameById(id))
							.filter(username -> username != null)
							.forEach(username -> serviceMessageNotifier.notifyUser(username, notification));
			}
		}
		return new Object[] { messageSource.getMessage("success.notification.save", null, locale), form.getData() };
	}

	private Notification updateMessages(Notification notification) {
		String[] locales = { "en", "fr" };
		String defaultMessage = notification.getMessages().values().stream()
				.map(value -> value == null ? "" : value.trim()).filter(value -> !value.isEmpty()).findAny()
				.orElse(null);
		for (String langue : locales) {
			String message = notification.getMessages().get(langue);
			if (message == null || message.trim().isEmpty()) {
				if (!StringUtils.hasText(notification.getCode()))
					notification.getMessages().put(langue, defaultMessage);
				else
					notification.getMessages().put(langue, messageSource.getMessage(notification.getCode(),
							notification.getParameters(), defaultMessage, new Locale(langue)));
			} else if (StringUtils.hasText(notification.getCode()))
				notification.getMessages().put(langue, messageSource.getMessage(notification.getCode(),
						notification.getParameters(), message, new Locale(langue)));
		}
		return notification;
	}

	@Value("${app.settings.report.template.max.size}")
	public void setMaxTemplateSize(String value) {
		this.maxTemplateSize = DataSize.parse(value).toBytes();
	}

	@Value("${spring.servlet.multipart.max-file-size}")
	public void setMaxUploadFileSize(String value) {
		this.maxUploadFileSize = DataSize.parse(value).toBytes();
	}

	/**
	 * buildUser: <br>
	 * Description
	 * 
	 * @param errors
	 * @param customer
	 * @param source
	 * @param locale
	 * @param userRoles
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	private User buildUser(Map<String, String> errors, String source, Locale locale, List<Role> userRoles,
			Principal principal) throws JsonProcessingException, IOException {
		User user = null;
		String error = null;
		boolean newUser = false;
		ValidatorField validator = serviceDataValidation.findByClass(User.class);
		if (validator == null)
			serviceDataValidation.register(validator = new UserValidator());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(source);
		String login = jsonNode.get("login").asText(), password = jsonNode.get("password").asText(),
				firstname = jsonNode.get("firstName").asText(),
				lastname = jsonNode.get("lastName").asText(), email = jsonNode.get("email").asText();
		int id = jsonNode.get("id").asInt(), connexionType = jsonNode.get("connexionType").asInt();
		if (id > 0) {
			user = serviceUser.get(id);
		} else {
			newUser = true;
			user = new User();
			error = validator.validate(user, "login", login);
			if (error != null)
				errors.put("login", serviceDataValidation.ParseError(error, messageSource, locale));
			else if (serviceUser.existByUsername(login))
				errors.put("login",
						messageSource.getMessage("error.username.in_use", null, "Username is in use", locale));
			else
				user.setLogin(login);
		}

		if (connexionType >= User.STANDARD_CONNEXION && connexionType <= User.LADP_CONNEXION)
			user.setConnexionType(connexionType);
		else
			user.setConnexionType(User.BOTH_CONNEXION);

		if (user.getConnexionType() != User.LADP_CONNEXION) {
			if (newUser || StringUtils.hasText(password)) {
				error = validator.validate(user, "password", password);
				if (error != null)
					errors.put("password", serviceDataValidation.ParseError(error, messageSource, locale));
				else {
					user.setPassword(password);
					user.setPassword(passwordEncoder.encode(user.getPassword()));
				}
			}
		} else if (!User.LDAP_KEY_PASSWORD.equals(user.getPassword()))
			user.setPassword(User.LDAP_KEY_PASSWORD);

		if (enabledOTP) {
			boolean using2FA = jsonNode.get("using2FA").asBoolean(forcedOTP);
			user.setUsing2FA(using2FA);
			if (!(using2FA || forcedOTP))
				user.setSecret(null);
		}

		error = validator.validate(user, "firstName", firstname);
		if (error != null)
			errors.put("firstName", serviceDataValidation.ParseError(error, messageSource, locale));
		else
			user.setFirstName(firstname);

		error = validator.validate(user, "lastName", lastname);
		if (error != null)
			errors.put("lastName", serviceDataValidation.ParseError(error, messageSource, locale));
		else
			user.setLastName(lastname);

		error = validator.validate(user, "email", email);
		if (error != null)
			errors.put("email", serviceDataValidation.ParseError(error, messageSource, locale));
		else if (!email.equals(user.getEmail())) {
			if (serviceUser.existByEmail(email))
				errors.put("email", messageSource.getMessage("error.email.in_use", null, "Email is in use", locale));
			else
				user.setEmail(email);
		}

		if (!principal.getName().equals(user.getLogin())) {

			user.getRoles().forEach(role -> userRoles.add(role));

			user.disable();

			RoleType[] roletypes = RoleType.values();
			for (int i = RoleType.ROLE_USER.ordinal(); i < roletypes.length; i++) {
				Role role = serviceRole.getByName(roletypes[i].name());
				if (role == null)
					serviceRole.save(role = new Role(roletypes[i]));
				JsonNode roleNode = jsonNode.get(role.getType().name());
				if (roleNode != null && roleNode.asText().equals(Constant.CHECKBOX_CONTROL_ON))
					user.addRole(role);
			}
		}
		return user;
	}

}