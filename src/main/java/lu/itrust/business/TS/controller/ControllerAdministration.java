package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.TrickService;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.data.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.database.dao.hbm.DAOHibernate;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceRole;
import lu.itrust.business.TS.database.service.ServiceTrickService;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.validator.UserValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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
import org.springframework.web.bind.annotation.ResponseBody;

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
		return "admin/administration";
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
		session.setAttribute("currentAdminCustomer", customerSection);
		model.addAttribute("customer", customerSection);
		model.addAttribute("analyses", serviceAnalysis.getAllFromCustomer(customerSection));
		model.addAttribute("customers", serviceCustomer.getAll());
		return "admin/analysis/analyses";
	}

	@RequestMapping(value = "/Analysis/Delete", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody boolean deleteAnalysis(@RequestBody List<Integer> ids, HttpSession session) {
		try {
			Integer selected = (Integer) session.getAttribute("selectedAnalysis");
			if (selected != null && ids.contains(selected))
				session.removeAttribute("selectedAnalysis");
			return customDelete.deleteAnalysis(ids);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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

			for (User user : serviceUser.getAll())
				userrights.put(DAOHibernate.Initialise(user), null);

			for (UserAnalysisRight uar : uars)
				userrights.put(DAOHibernate.Initialise(uar.getUser()), DAOHibernate.Initialise(uar.getRight()));

			model.addAttribute("currentUser", (DAOHibernate.Initialise(serviceUser.get(principal.getName())).getId()));
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

				user = DAOHibernate.Initialise(user);

				if (user.getLogin().equals(principal.getName()))
					continue;

				int useraccess = jsonNode.get("analysisRight_" + user.getId()).asInt();

				for (UserAnalysisRight uar : analysis.getUserRights())
					uar.setUser(DAOHibernate.Initialise(uar.getUser()));

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
						uar = new UserAnalysisRight(user, AnalysisRight.valueOf(useraccess));
						userrights.put(user, uar.getRight());
						serviceUserAnalysisRight.save(uar);
						serviceAnalysis.saveOrUpdate(analysis);
					}

				}
			}

			model.addAttribute("success", messageSource.getMessage("label.analysis.manage.users.success", null, "Analysis access rights, EXPECT your own, were successfully updated!", locale));

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
	public String section(Model model, HttpSession session, Principal principal) throws Exception {
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
	 * @return
	 */
	private User buildUser(Map<String, String> errors, String source, Locale locale, Principal principal) {

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
	public @ResponseBody Map<String, String> save(@RequestBody String value, Locale locale, Principal principal) throws Exception {

		Map<String, String> errors = new LinkedHashMap<>();
		try {

			User user = buildUser(errors, value, locale, principal);

			if (!errors.isEmpty())
				return errors;

			if (user.getId() < 1) {
				serviceUser.save(user);
			} else {
				serviceUser.saveOrUpdate(user);
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
	public @ResponseBody Boolean delete(@PathVariable("userId") int userId, Principal principal) throws Exception {
		try {

			User user = serviceUser.get(userId);

			if (!user.getLogin().equals(principal.getName())) {
				user.disable();
				serviceUser.saveOrUpdate(user);
				serviceUser.delete(userId);
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}