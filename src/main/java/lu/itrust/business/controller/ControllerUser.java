/**
 * 
 */
package lu.itrust.business.controller;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.TS.Role;
import lu.itrust.business.TS.RoleType;
import lu.itrust.business.TS.User;
import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.validator.UserValidator;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author oensuifudine
 * 
 */
@RequestMapping("/user")
@Controller
public class ControllerUser {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceRole serviceRole;

	@Autowired
	private MessageSource messageSource;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.replaceValidators(new UserValidator());
	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping("/all")
	public String loadAll(Map<String, Object> model) throws Exception {
		model.put("users", serviceUser.loadAll());
		return "users";
	}

	@Secured("ROLE_USER")
	@RequestMapping("/{userId}")
	public String profil(@PathVariable("userId") Long userId,
			HttpSession session, Map<String, Object> model) throws Exception {
		User user = (User) session.getAttribute("user");
		if (user == null || user.getId() != userId)
			user = serviceUser.get(userId);
		model.put("userProfil", user);
		return "profilUser";
	}

	@RequestMapping("/add")
	public String add(Map<String, Object> model) {
		model.put("user", new User());
		return "addUserForm";
	}

	@RequestMapping("/save")
	public String save(@ModelAttribute("user") @Valid User user,
			BindingResult result, RedirectAttributes attributes, Locale locale)
			throws Exception {

		try {
			if (result.hasErrors())
				return "addUserForm";
			PasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
			user.setPassword(passwordEncoder.encodePassword(user.getPassword(),
					user.getLogin()));
			if (serviceUser.isEmpty()) {
				Role role = serviceRole.findByName(RoleType.ROLE_ADMIN.name());
				if (role == null) {
					role = new Role(RoleType.ROLE_ADMIN);
					serviceRole.save(role);
				}
				user.add(role);
			}
			this.serviceUser.saveOrUpdate(user);
			attributes.addFlashAttribute("success", messageSource.getMessage(
					"success.create.account", null,
					"Account has been created successfully", locale));
			return "redirect:/login";
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			if (e.getMessage().contains("dtLogin"))
				result.rejectValue("login", "error.duplicate.login", null,
						"Login is already used");
			else if (e.getMessage().contains("dtEmail"))
				result.rejectValue("login", "error.duplicate.email", null,
						"Email is already used");
			else {
				attributes
						.addFlashAttribute(
								"errors",
								messageSource
										.getMessage(
												"error.create.account.unknown",
												null,
												"Account creation failed, Please try again later",
												locale));
				return "redirect:/login";
			}
		}
		return "addUserForm";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping("/{userId}/delete")
	public String delete(@PathVariable("userId") Long userId) throws Exception {
		serviceUser.delete(userId);
		return "redirect:/index";
	}

	public ServiceUser getServiceUser() {
		return serviceUser;
	}

	public void setServiceUser(ServiceUser serviceUser) {
		this.serviceUser = serviceUser;
	}
}
