package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.view.model.Role;
import lu.itrust.business.view.model.RoleType;
import lu.itrust.business.view.model.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerAdministration.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 
 * @since Dec 13, 2013
 */
@Secured("ROLE_ADMIN")
@Controller
@RequestMapping("/Admin")
public class ControllerAdministration {
	

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ServiceRole serviceRole;
	
	@Autowired
	private ServiceUser serviceUser;
	
	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String showAdministration(Map<String, Object> model) throws Exception {
		model.put("users", serviceUser.loadAll());
		return "admin/administration";
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
	@RequestMapping("/User/{userId}")
	public String manageUserRole(@PathVariable("userId") Long userId,
			Map<String, Object> model, HttpSession session) throws Exception {
		User user = (User) session.getAttribute("user");
		if (user == null || user.getId() != userId) {
			model.put("userManageRole", serviceUser.get(userId));
			model.put("roles", RoleType.values());
			model.put("userRole", new Role());
			return "roleManagerForm";
		}
		return "redirect:/index";
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
	@RequestMapping("/User/Save")
	public String save(@ModelAttribute("user") @Valid User user, BindingResult result, RedirectAttributes attributes, Locale locale) throws Exception {

		try {
			if (result.hasErrors())
				return "addUserForm";
			PasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
			user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getLogin()));
			if (serviceUser.isEmpty()) {
				Role role = serviceRole.findByName(RoleType.ROLE_ADMIN.name());
				if (role == null) {
					role = new Role(RoleType.ROLE_ADMIN);
					serviceRole.save(role);
				}
				user.addRole(role);
			}
			this.serviceUser.saveOrUpdate(user);
			attributes.addFlashAttribute("success", messageSource.getMessage("success.create.account", null, "Account has been created successfully", locale));
			return "redirect:/login";
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			if (e.getMessage().contains("dtLogin"))
				result.rejectValue("login", "error.duplicate.login", null, "Login is already used");
			else if (e.getMessage().contains("dtEmail"))
				result.rejectValue("login", "error.duplicate.email", null, "Email is already used");
			else {
				attributes.addFlashAttribute("errors", messageSource.getMessage("error.create.account.unknown", null, "Account creation failed, Please try again later", locale));
				return "redirect:/login";
			}
		}
		return "addUserForm";
	}
	
	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping("/User/delete/{userId}")
	public String delete(@PathVariable("userId") Long userId) throws Exception {
		serviceUser.delete(userId);
		return "redirect:/index";
	}
	
	public void setServiceRole(ServiceRole serviceRole) {
		this.serviceRole = serviceRole;
	}


	public void setServiceUser(ServiceUser serviceUser) {
		this.serviceUser = serviceUser;
	}
	
}