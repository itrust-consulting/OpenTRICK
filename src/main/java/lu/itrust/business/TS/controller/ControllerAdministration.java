package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.CustomerManager;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceRole;
import lu.itrust.business.TS.database.service.ServiceTrickLog;
import lu.itrust.business.TS.database.service.ServiceTrickService;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.TrickService;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.TS.model.analysis.helper.ManageAnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.helper.TrickLogFilter;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.validator.UserValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ControllerAdministration.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Dec 13, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_ADMIN)
@Controller
@RequestMapping("/Admin")
public class ControllerAdministration {

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
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

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

	@Value("${app.settings.version}")
	private String version;

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String showAdministration(HttpSession session, Principal principal, Map<String, Object> model) throws Exception {
		model.put("adminView", true);
		model.put("users", serviceUser.getAll());
		List<Customer> customers = serviceCustomer.getAll();
		Integer customerID = (Integer) session.getAttribute("currentAdminCustomer");
		// check if the current customer is set -> no
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
			if (customerID != null) {
				model.put("customer", customerID);
				model.put("analyses", serviceAnalysis.getAllFromCustomer(customerID));
			} else
				model.put("analyses", serviceAnalysis.getAll());
		}

		model.put("logFilter", loadLogFilter(session, principal.getName()));
		model.put("logLevels", serviceTrickLog.getDistinctLevel());
		model.put("logTypes", serviceTrickLog.getDistinctType());
		model.put("actions", serviceTrickLog.getDistinctAction());
		model.put("authors", serviceTrickLog.getDistinctAuthor());

		return "admin/administration";
	}

	private TrickLogFilter loadLogFilter(HttpSession session, String username) throws Exception {
		TrickLogFilter filter = (TrickLogFilter) session.getAttribute(TRICK_LOG_FILTER);
		if (filter != null)
			return filter;
		User user = serviceUser.get(username);
		filter = new TrickLogFilter(user.getInteger(LOG_FILTER_PAGE_SIZE), user.getSetting(LOG_FILTER_LEVEL), user.getSetting(LOG_FILTER_TYPE), user.getSetting(LOG_FILTER_ACTION),
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

			if (status.isInstalled() == false && serviceAnalysis.getDefaultProfile() != null)
				status.setInstalled(true);

			if (version.equals(status.getVersion()))
				status.setVersion(version);

			serviceTrickService.saveOrUpdate(status);

			return status;

		}

		status = new TrickService(version, installed);

		if (serviceAnalysis.getDefaultProfile() != null)
			status.setInstalled(true);

		serviceTrickService.save(status);

		return status;

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
	public String section(@PathVariable Integer customerSection, HttpSession session, Principal principal, Model model) throws Exception {
		List<Analysis> analyses = serviceAnalysis.getAllFromCustomer(customerSection);
		Collections.sort(analyses, Collections.reverseOrder(new AnalysisComparator()));
		session.setAttribute("currentAdminCustomer", customerSection);
		model.addAttribute("customer", customerSection);
		model.addAttribute("analyses", analyses);
		model.addAttribute("customers", serviceCustomer.getAll());
		return "admin/analysis/analyses";
	}

	@RequestMapping(value = "/Analysis/Delete", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody boolean deleteAnalysis(@RequestBody List<Integer> ids, Principal principal, HttpSession session) {
		try {
			Integer selected = (Integer) session.getAttribute("selectedAnalysis");
			if (selected != null && ids.contains(selected))
				session.removeAttribute("selectedAnalysis");
			return customDelete.deleteAnalysis(ids, principal.getName());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@RequestMapping(value = "/Log/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public String sectionLog(@RequestParam(defaultValue = "1") Integer page, HttpSession session, Principal principal, Model model) {
		try {
			model.addAttribute("trickLogs", serviceTrickLog.getAll(page, loadLogFilter(session, principal.getName())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "admin/log/section";
	}

	@RequestMapping(value = "/Log/Filter/Update", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String updateLogFilter(@RequestBody TrickLogFilter logFilter, Principal principal, HttpSession session, Locale locale) {
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
			return JsonMessage.Success(messageSource.getMessage("success.filter.updated", null, "Filter has been successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
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
	public String manageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model) throws Exception {
		Map<User, AnalysisRight> userrights = new LinkedHashMap<>();
		Analysis analysis = serviceAnalysis.get(analysisID);
		if (!analysis.isProfile()) {
			List<UserAnalysisRight> uars = analysis.getUserRights();
			serviceUser.getAll().forEach(user -> userrights.put(user, null));
			uars.forEach(uar -> userrights.put(uar.getUser(), uar.getRight()));
			model.addAttribute("currentUser", serviceUser.get(principal.getName()).getId());
			model.addAttribute("analysisRights", AnalysisRight.values());
			model.addAttribute("analysis", analysis);
			model.addAttribute("userrights", userrights);
			return "analyses/allAnalyses/forms/manageUserAnalysisRights";
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
	@RequestMapping("/Analysis/{analysisID}/ManageAccess/Update")
	public String updatemanageaccessrights(@PathVariable("analysisID") int analysisID, Principal principal, Model model, @RequestBody String value, Locale locale) throws Exception {

		try {
			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);
			int currentUser = jsonNode.get("userselect").asInt();
			model.addAttribute("currentUser", currentUser);
			manageAnalysisRight.updateAnalysisRights(principal, analysisID, jsonNode);
			model.addAttribute("success",
					messageSource.getMessage("label.analysis.manage.users.success", null, "Analysis access rights, EXPECT your own, were successfully updated!", locale));
			Analysis analysis = serviceAnalysis.get(analysisID);
			Map<User, AnalysisRight> userrights = new LinkedHashMap<User, AnalysisRight>();
			analysis.getUserRights().forEach(useraccess -> userrights.put(useraccess.getUser(), useraccess.getRight()));
			model.addAttribute("analysisRights", AnalysisRight.values());
			model.addAttribute("analysis", analysis);
			model.addAttribute("userrights", userrights);
			return "analyses/allAnalyses/forms/manageUserAnalysisRights";
		} catch (Exception e) {
			// return errors
			model.addAttribute("errors", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return "analyses/allAnalyses/forms/manageUserAnalysisRights";
		}
	}

	@RequestMapping("/Analysis/{analysisId}/Switch/Customer")
	public String switchCUstomerForm(@PathVariable("analysisId") int analysisId, Principal principal, Model model, RedirectAttributes attributes, Locale locale) throws Exception {
		model.addAttribute("idAnalysis", analysisId);
		model.addAttribute("currentCustomers", serviceAnalysis.getCustomersByIdAnalysis(analysisId));
		model.addAttribute("customers", serviceCustomer.getAllNotProfiles());
		return "admin/analysis/switch-customer";
	}

	@RequestMapping(value = "/Analysis/{idAnalysis}/Switch/Customer/{idCustomer}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String switchCUstomerForm(@PathVariable("idAnalysis") int idAnalysis, @PathVariable("idCustomer") int idCustomer, Principal principal, Model model,
			RedirectAttributes attributes, Locale locale) throws Exception {
		String identifier = serviceAnalysis.getIdentifierByIdAnalysis(idAnalysis);
		if (identifier == null)
			return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		else if (serviceCustomer.isProfile(idCustomer))
			return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
		else if (!serviceCustomer.exists(idCustomer))
			return JsonMessage.Error(messageSource.getMessage("error.customer.not_found", null, "Customer cannot be found", locale));
		customerManager.switchCustomer(identifier, idCustomer, principal.getName());
		return JsonMessage.Success(messageSource.getMessage("success.analyses.updated", null, "Analyses have been updated", locale));
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
	@RequestMapping(value = "/User/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public String userSection(Model model, HttpSession session, Principal principal) throws Exception {
		model.addAttribute("users", serviceUser.getAll());
		return "admin/user/users";
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
	@RequestMapping(value = "/Roles", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public String getAllRoles(Map<String, Object> model, HttpSession session) throws Exception {

		model.put("roles", RoleType.values());

		return "admin/user/roles";

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
	@RequestMapping(value = "/User/Roles/{userId}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public String getUserRoles(@PathVariable("userId") int userId, Map<String, Object> model, HttpSession session) throws Exception {

		List<Role> userRoles = serviceRole.getAllFromUser(serviceUser.get(userId));

		List<RoleType> roleTypes = new ArrayList<RoleType>();

		for (Role role : userRoles) {
			roleTypes.add(role.getType());
		}

		model.put("userRoles", roleTypes);
		model.put("roles", RoleType.values());
		return "admin/user/roles";

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
	 */
	private User buildUser(Map<String, String> errors, String source, Locale locale, List<Role> userRoles, Principal principal) {

		User user = null;
		String error = null;
		String login = "";
		String password = "";
		String firstname = "";
		String lastname = "";
		String email = "";
		boolean newUser = false;
		try {
			ValidatorField validator = serviceDataValidation.findByClass(User.class);
			if (validator == null)
				serviceDataValidation.register(validator = new UserValidator());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			login = jsonNode.get("login").asText();
			password = jsonNode.get("password").asText();
			firstname = jsonNode.get("firstName").asText();
			lastname = jsonNode.get("lastName").asText();
			email = jsonNode.get("email").asText();

			int id = jsonNode.get("id").asInt();

			if (id > 0) {
				user = serviceUser.get(jsonNode.get("id").asInt());
			} else {
				newUser = true;
				user = new User();
				error = validator.validate(user, "login", login);
				if (error != null)
					errors.put("login", serviceDataValidation.ParseError(error, messageSource, locale));
				else {
					user.setLogin(login);
				}
			}

			if (newUser || !password.equals(Constant.EMPTY_STRING)) {

				error = validator.validate(user, "password", password);
				if (error != null)
					errors.put("password", serviceDataValidation.ParseError(error, messageSource, locale));
				else {
					user.setPassword(password);
					ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
					user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getLogin()));
				}
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
			else
				user.setEmail(email);

			if (!principal.getName().equals(user.getLogin())) {

				user.getRoles().forEach(role -> userRoles.add(role));
				
				user.disable();

				RoleType[] roletypes = RoleType.values();

				for (int i = 0; i < roletypes.length; i++) {
					Role role = serviceRole.getByName(roletypes[i].name());

					if (role == null) {
						role = new Role(roletypes[i]);
						serviceRole.save(role);
					}

					if (jsonNode.get(role.getType().name()).asText().equals(Constant.CHECKBOX_CONTROL_ON)) {
						user.addRole(role);
					}
				}
			}

			if (errors.isEmpty())
				return user;
			else
				return null;

		} catch (TrickException e) {
			errors.put("user", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			e.printStackTrace();
			return null;
		} catch (Exception e) {

			errors.put("user", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return null;
		}

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
	@RequestMapping(value = "/User/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody Map<String, String> saveUser(@RequestBody String value, Locale locale, Principal principal) throws Exception {

		Map<String, String> errors = new LinkedHashMap<>();
		try {
			List<Role> userRoles = new LinkedList<Role>();
			User user = buildUser(errors, value, locale,userRoles, principal);
			if (!errors.isEmpty())
				return errors;
			RoleType userAccess = user.getAccess();
			String userRole = userAccess == null ? "none" : userAccess.name().toLowerCase().replace("role_", "");
			if (user.getId() < 1) {
				serviceUser.save(user);
				/**
				 * Log
				 */
				TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.add.user", String.format("Target: %s, access: %s", user.getLogin(), userRole),
						principal.getName(), LogAction.CREATE, user.getLogin(), userAccess == null ? "none" : userAccess.name());
				//give access
				user.getRoles()
						.stream()
						.filter(role -> role.getType() != userAccess)
						.forEach(
								role -> TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.user.get.access",
										String.format("Target: %s, access: %s", user.getLogin(), role.getRoleName().toLowerCase()), principal.getName(), LogAction.GIVE_ACCESS,
										user.getLogin(), role.getType().name()));
			} else {
				serviceUser.saveOrUpdate(user);
				/**
				 * Log
				 */
				//remove access
				userRoles
						.stream()
						.filter(role -> !user.hasRole(role))
						.forEach(
								role -> TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.user.remove.access",
										String.format("Target: %s, access: %s", user.getLogin(), role.getRoleName().toLowerCase()), principal.getName(), LogAction.REMOVE_ACCESS,
										user.getLogin(), role.getType().name()));
				//give access
				user.getRoles()
						.stream()
						.filter(role -> !userRoles.contains(role))
						.forEach(
								role -> TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.user.grant.access",
										String.format("Target: %s, access: %s", user.getLogin(), role.getRoleName().toLowerCase()), principal.getName(), LogAction.GRANT_ACCESS,
										user.getLogin(), role.getType().name()));
			}
			return errors;

		} catch (Exception e) {
			errors.put("user", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return errors;
		}

	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/User/Delete/{userId}")
	public @ResponseBody Map<String, String> deleteUser(@PathVariable("userId") int userId, Principal principal, Locale locale) throws Exception {
		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {
			User user = serviceUser.get(userId);
			if (!user.getLogin().equals(principal.getName())) {
				RoleType userAccess = user.getAccess();
				String userRole = userAccess == null ? "none" : userAccess.name().toLowerCase().replace("role_", "");
				customDelete.deleteUser(user);
				/**
				 * Log
				 */
				TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.delete.user", String.format("User: %s, access: %s", user.getLogin(), userRole),
						principal.getName(), LogAction.DELETE, user.getLogin(), userAccess == null ? "none" : userAccess.name());
			} else {
				errors.put("error", messageSource.getMessage("error.user.delete_your_account", null, "You cannot delete your own account!", locale));
				return errors;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errors.put("error", messageSource.getMessage("error.user.delete_failed", null, "Could not delete the account! Make sure the user does not own any analyses!", locale));
		}
		return errors;

	}
}