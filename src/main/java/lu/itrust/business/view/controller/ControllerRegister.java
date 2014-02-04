package lu.itrust.business.view.controller;

import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.validator.UserValidator;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerAdministration.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Dec 13, 2013
 */
@Controller
public class ControllerRegister {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceRole serviceRole;
	
	/**
	 * initBinder: <br>
	 * Description
	 * 
	 * @param binder
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.replaceValidators(new UserValidator());
	}

	/**
	 * add: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/Register")
	public String add(Map<String, Object> model) {
		
		// create new user object and add it to model
		model.put("user", new User());
		
		return "registerUserForm";
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
	@RequestMapping("/DoRegister")
	public String save(@ModelAttribute("user") @Valid User user, BindingResult result, RedirectAttributes attributes, Locale locale) throws Exception {

		try {
			
			// check if validator has errors
			if (result.hasErrors())
				
				// return to form
				return "registerUserForm";
			
			// encode password
			ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
			user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getLogin()));
			
			// check if users exist and give first user admin role
			Role role = null;
			if (!serviceUser.hasUsers()) {
				
				// check if admin role exists and create it if not
				role = serviceRole.findByName(RoleType.ROLE_ADMIN.name());
				if (role == null) {
					role = new Role(RoleType.ROLE_ADMIN);
					serviceRole.save(role);
				}
			} else {
				
				// check if user role exists and create it if not
				role = serviceRole.findByName(RoleType.ROLE_USER.name());
				if (role == null) {
					role = new Role(RoleType.ROLE_USER);
					serviceRole.save(role);
				}
			}
			
			// set role of new user
			user.addRole(role);
			
			// save user 
			this.serviceUser.save(user);
			
			// return success message and redirect to login page
			attributes.addFlashAttribute("success", messageSource.getMessage("success.create.account", null, "Account has been created successfully", locale));
			attributes.addFlashAttribute("login", user.getLogin());
			return "redirect:/login";
		} catch (ConstraintViolationException e) {
			
			// return error and return to register page
			e.printStackTrace();
			attributes.addFlashAttribute("errors", messageSource.getMessage("error.create.account.unknown", null, "Account creation failed, Please try again", locale));
			return "redirect:/Register";
		}
	}
}