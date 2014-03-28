package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserAnalysisRight;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
	private ServiceUserAnalysisRight serviceUserAnalysisRight;
	
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
		model.put("users", serviceUser.loadAll());
		
		List<Customer> customers = serviceCustomer.loadAllNotProfile(); 
		
		Integer customerID = (Integer) session.getAttribute("currentCustomer");
		
		if (customerID == null) {
			customerID = customers.get(0).getId();
			session.setAttribute("currentCustomer", customerID);
		}
		
		customers = serviceCustomer.loadAll(); 
		
		if (customers != null && customers.size()>0) {
			
			model.put("customer", customerID);
			model.put("customers", customers);
			model.put("analyses", serviceAnalysis.loadAllFromCustomerAndProfile(customerID));
		}
			
		return "admin/administration";
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
		session.setAttribute("currentCustomer", customerSection);
		model.addAttribute("analyses", serviceAnalysis.loadAllFromCustomerAndProfile(customerSection));
		model.addAttribute("customer", customerSection);
		model.addAttribute("customers", serviceCustomer.loadAll());
		return "admin/analysis/analyses";
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

		if(!analysis.isProfile()){
		
			List<UserAnalysisRight> uars = analysis.getUserRights();
	
			for (UserAnalysisRight uar : uars) {
				userrights.put(uar.getUser(), uar.getRight());
			}
	
			for (User user : serviceUser.loadAll()) {
				if (!userrights.containsKey(user))
					userrights.put(user, null);
			}
	
			model.addAttribute("analysisRigths", AnalysisRight.values());
			model.addAttribute("analysis", analysis);
			model.addAttribute("userrights", userrights);
			return "analysis/manageuseranalysisrights";
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

			Map<User, UserAnalysisRight> userrights = new LinkedHashMap<>();

			Analysis analysis = serviceAnalysis.get(analysisID);

			List<UserAnalysisRight> uars = analysis.getUserRights();

			for (UserAnalysisRight uar : uars) {
				userrights.put(uar.getUser(), uar);
			}

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);

			for (User user : serviceUser.loadAll()) {

				int useraccess = jsonNode.get("analysisRight_" + user.getId()).asInt();

				if (userrights.containsKey(user)) {
					UserAnalysisRight uar = userrights.get(user);
					if (useraccess == -1) {
						analysis.removeRights(user);
						serviceUserAnalysisRight.delete(uar);
						serviceAnalysis.saveOrUpdate(analysis);
						userrights.remove(user);
					} else {
						uar.setRight(AnalysisRight.valueOf(useraccess));
						serviceUserAnalysisRight.saveOrUpdate(uar);
						serviceAnalysis.saveOrUpdate(analysis);
					}
				} else {

					if (!user.getCustomers().contains(analysis.getCustomer()))
						user.addCustomer(analysis.getCustomer());

					if (useraccess != -1) {
						UserAnalysisRight uar = new UserAnalysisRight(user, analysis, AnalysisRight.valueOf(useraccess));
						userrights.put(user, uar);
						serviceUserAnalysisRight.save(uar);
						serviceAnalysis.saveOrUpdate(analysis);
					}

				}
			}

			model.addAttribute("success", messageSource.getMessage("label.analysis.manage.users.success", null, "Analysis access rights users successfully updated!", locale));

			model.addAttribute("analysisRigths", AnalysisRight.values());
			model.addAttribute("analysis", analysis);
			model.addAttribute("userrights", userrights);

			return manageaccessrights(analysisID, principal, model);
		} catch (Exception e) {
			// return errors
			model.addAttribute("errors", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return manageaccessrights(analysisID, principal, model);
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
		model.addAttribute("users", serviceUser.loadAll());
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

		List<Role> userRoles = serviceRole.getByUser(serviceUser.get(userId));

		List<RoleType> roleTypes = new ArrayList<RoleType>();

		for (Role role : userRoles) {
			roleTypes.add(role.getType());
		}

		model.put("userRoles", roleTypes);
		model.put("roles", RoleType.values());
		return "admin/user/userRoles";

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
	private User buildUser(List<String[]> errors, String source, Locale locale, Principal principal) {

		User user = null;

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			if (id > 0) {
				user = serviceUser.get(jsonNode.get("id").asInt());
			} else {
				user = new User();
				user.setLogin(jsonNode.get("login").asText());
			}

			if (!jsonNode.get("password").asText().equals(Constant.EMPTY_STRING)) {
				user.setPassword(jsonNode.get("password").asText());
				ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
				user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getLogin()));
			}

			user.setFirstName(jsonNode.get("firstName").asText());
			user.setLastName(jsonNode.get("lastName").asText());

			user.setEmail(jsonNode.get("email").asText());

			if (!principal.getName().equals(user.getLogin())) {

				user.disable();

				RoleType[] roletypes = RoleType.values();

				for (int i = 0; i < roletypes.length; i++) {
					Role role = serviceRole.findByName(roletypes[i].name());

					if (role == null) {
						role = new Role(roletypes[i]);
						serviceRole.save(role);
					}

					if (jsonNode.get(role.getType().name()).asText().equals(Constant.CHECKBOX_CONTROL_ON)) {
						user.addRole(role);
					}

				}

			}

			return user;

		} catch (Exception e) {

			errors.add(new String[] { "user", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
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
	@RequestMapping(value = "/User/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, Locale locale, Principal principal) throws Exception {

		List<String[]> errors = new LinkedList<>();
		try {

			User user = buildUser(errors, value, locale, principal);

			if (user == null) {
				return errors;
			} else {
				if (user.getId() < 1) {
					serviceUser.save(user);
				} else {
					serviceUser.saveOrUpdate(user);
				}
			}
		} catch (Exception e) {
			errors.add(new String[] { "user", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
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
	@RequestMapping("/User/Delete/{userId}")
	public @ResponseBody
	Boolean delete(@PathVariable("userId") int userId, Principal principal) throws Exception {
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