/**
 * 
 */
package lu.itrust.business.view.controller;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.validator.UserValidator;
import lu.itrust.business.view.model.User;

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
@Secured("ROLE_USER")
@RequestMapping("/Profile")
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

	@RequestMapping("/{userId}")
	public String profil(@PathVariable("userId") Long userId, HttpSession session, Map<String, Object> model) throws Exception {
		User user = (User) session.getAttribute("user");
		if (user == null || user.getId() != userId)
			user = serviceUser.get(userId);
		model.put("userProfil", user);
		return "profilUser";
	}

	@RequestMapping("/Update")
	public String save(@ModelAttribute("user") @Valid User user, BindingResult result, RedirectAttributes attributes, Locale locale, Map<String, Object> model) throws Exception {

		try {
			if (result.hasErrors())
				return "profilUser";
			PasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
			user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getLogin()));
			this.serviceUser.saveOrUpdate(user);
			attributes.addFlashAttribute("success", messageSource.getMessage("success.create.account", null, "Account has been created successfully", locale));
			model.put("userProfil", user);
			return "profilUser";
		} catch (Exception e) {
			e.printStackTrace();
			attributes.addFlashAttribute("errors", messageSource.getMessage("error.update.account", null, e.getMessage(), locale));
			return "profilUser";
			
		}
	}

	public ServiceUser getServiceUser() {
		return serviceUser;
	}

	public void setServiceUser(ServiceUser serviceUser) {
		this.serviceUser = serviceUser;
	}
}
