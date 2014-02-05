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
	 * getServiceUser: <br>
	 * Description
	 * 
	 * @return
	 */
	public ServiceUser getServiceUser() {
		return serviceUser;
	}

	/**
	 * setServiceUser: <br>
	 * Description
	 * 
	 * @param serviceUser
	 */
	public void setServiceUser(ServiceUser serviceUser) {
		this.serviceUser = serviceUser;
	}

	/**
	 * profile: <br>
	 * Description
	 * 
	 * @param principal
	 * @param session
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String profile(Principal principal, HttpSession session, Map<String, Object> model) throws Exception {

		// retrieve profile of the current user
		User user = serviceUser.get(principal.getName());

		// add profile to model
		model.put("userProfil", user);

		return "userProfile";
	}

	/**
	 * profileOfUser: <br>
	 * Description
	 * 
	 * @param userId
	 * @param session
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize(Constant.ROLE_MIN_ADMIN)
	@RequestMapping("/{userId}")
	public String profileOfUser(@PathVariable("userId") Long userId, HttpSession session, Map<String, Object> model) throws Exception {

		// retireve profile
		User user = serviceUser.get(userId);

		// add profile to model
		model.put("userProfil", user);

		return "profilUser";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param user
	 * @param result
	 * @param attributes
	 * @param locale
	 * @param model
	 * @return
	 * @throws Exception
	 */

	// TODO update

	@RequestMapping("/Update")
	public String save(@ModelAttribute("user") @Valid User user, BindingResult result, RedirectAttributes attributes, Locale locale, Map<String, Object> model, Principal principal)
			throws Exception {

		try {

			// check if user tries to update his own profile
			if (!user.getLogin().equals(principal.getName()))
				return "errors/403";

			// // check if profile has errors on validation
			// if (result.hasErrors())
			// return "profilUser";
			//
			// // TODO check if password needs to be reset
			// ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
			// user.setPassword(passwordEncoder.encodePassword(user.getPassword(),
			// user.getLogin()));
			//
			// // TODO do not let user change
			//
			// // update profile
			// this.serviceUser.saveOrUpdate(user);
			// attributes.addFlashAttribute("success",
			// messageSource.getMessage("success.create.account", null,
			// "Account has been created successfully", locale));
			// model.put("userProfil", user);
			return "profilUser";
		} catch (Exception e) {

			// return errors
			e.printStackTrace();
			attributes.addFlashAttribute("errors", messageSource.getMessage("error.update.account", null, e.getMessage(), locale));
			return "profilUser";

		}
	}
}