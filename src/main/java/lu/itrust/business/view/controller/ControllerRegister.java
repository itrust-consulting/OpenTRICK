package lu.itrust.business.view.controller;

import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import lu.itrust.business.TS.usermanagment.Role;
import lu.itrust.business.TS.usermanagment.RoleType;
import lu.itrust.business.TS.usermanagment.User;
import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.validator.UserValidator;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
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
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.replaceValidators(new UserValidator());
	}

	@RequestMapping("/register")
	public String add(Map<String, Object> model) {
		model.put("user", new User());
		return "registerUserForm";
	}

	@RequestMapping("/DoRegister")
	public String save(@ModelAttribute("user") @Valid User user, BindingResult result, RedirectAttributes attributes, Locale locale) throws Exception {

		try {
			if (result.hasErrors())
				return "registerUserForm";
			PasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
			user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getLogin()));
			Role role = null;
			if (serviceUser.isEmpty()) {
				role = serviceRole.findByName(RoleType.ROLE_ADMIN.name());
				if (role == null) {
					role = new Role(RoleType.ROLE_ADMIN);
					serviceRole.save(role);
				}
			} else {
				role = serviceRole.findByName(RoleType.ROLE_USER.name());
				if (role == null) {
					role = new Role(RoleType.ROLE_USER);
					serviceRole.save(role);
				}
			}
			user.addRole(role);
			this.serviceUser.saveOrUpdate(user);
			attributes.addFlashAttribute("success", messageSource.getMessage("success.create.account", null, "Account has been created successfully", locale));
			return "redirect:/login";
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			attributes.addFlashAttribute("errors", messageSource.getMessage("error.create.account.unknown", null, "Account creation failed, Please try again", locale));
			return "redirect:/login";
		}
	}
}
