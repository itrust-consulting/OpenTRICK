/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.validator.UserValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize(Constant.ROLE_MIN_USER)
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

	@RequestMapping
	public String profile(Principal principal, HttpSession session, Map<String, Object> model) throws Exception {
		User user = (User) session.getAttribute("user");
		if (user == null)
			user = serviceUser.get(principal.getName());
		model.put("userProfil", user);
		return "userProfile";
	}
	
	@RequestMapping("/{userId}")
	public String e(@PathVariable("userId") Long userId, HttpSession session, Map<String, Object> model) throws Exception {
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
			ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
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
}
