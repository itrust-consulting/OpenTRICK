/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.AccountLockerManager;
import lu.itrust.business.TS.database.service.ServiceEmailValidatingRequest;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.helper.ManageAnalysisRight;
import lu.itrust.business.TS.model.general.TSSetting;
import lu.itrust.business.TS.model.general.TSSettingName;
import lu.itrust.business.TS.usermanagement.EmailValidatingRequest;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author oensuifudine
 * 
 */
@Controller
public class ControllerHome {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceTSSetting serviceTSSetting;

	@Autowired
	private AccountLockerManager accountLockerManager;

	@Autowired
	private ManageAnalysisRight manageAnalysisRight;

	@Autowired
	private ServiceEmailValidatingRequest serviceEmailValidatingRequest;

	@PreAuthorize(Constant.ROLE_MIN_USER)
	@RequestMapping({ "", "/Home" })
	public String home() throws Exception {
		return "default/home";
	}

	@RequestMapping(value = "/MessageResolver", method = RequestMethod.POST, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String resolveMessage(@RequestBody MessageHandler message, Locale locale) {
		return String.format("{\"message\":\"%s\"}",
				messageSource.getMessage(message.getCode(), message.getParameters(), message.getMessage(), locale));
	}

	@PreAuthorize(Constant.ROLE_MIN_USER)
	@RequestMapping("/Feedback")
	public @ResponseBody List<MessageHandler> revice(Principal principal) {
		return serviceTaskFeedback.recieve(principal.getName());
	}

	@RequestMapping("/Login")
	public String login(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) {
		loadSettings(model, locale);
		if (request.getParameter("registerSuccess") != null) {
			model.addAttribute("success", messageSource.getMessage("success.create.account", null,
					"Account has been created successfully", locale));
			model.addAttribute("username", request.getParameter("login") == null ? "" : request.getParameter("login"));
		}
		return "default/login";
	}

	private void loadSettings(Model model, Locale locale) {
		try {
			TSSetting register = serviceTSSetting.get(TSSettingName.SETTING_ALLOWED_SIGNUP),
					resetPassword = serviceTSSetting.get(TSSettingName.SETTING_ALLOWED_RESET_PASSWORD);
			model.addAttribute("allowRegister", register == null || register.getBoolean());
			model.addAttribute("resetPassword", resetPassword == null || resetPassword.getBoolean());
		} catch (GenericJDBCException e) {
			model.addAttribute("error", messageSource.getMessage("error.database.connection_failed", null,
					"No connection to the database...", locale));
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error",
					messageSource.getMessage("error.setting.not.loaded", null, "Settings cannot be loaded", locale));
		}
	}

	@RequestMapping(value = "/IsAuthenticate", method = { RequestMethod.GET,
			RequestMethod.POST }, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody boolean isAuthenticate(Principal principal, HttpSession session, HttpServletResponse response)
			throws Exception {
		if (principal == null)
			return false;
		User user = serviceUser.get(principal.getName());
		if (user == null) {
			session.invalidate();
			response.reset();
			return false;
		} else
			return true;
	}

	@RequestMapping("/Login/Error")
	public String login(RedirectAttributes attributes, Locale locale, HttpServletRequest request) {
		return "redirect:/Login";
	}

	@RequestMapping("/Logout")
	public String logout() {
		return "redirect:/signout";
	}

	@RequestMapping(value = "/Success", method = RequestMethod.GET, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String success(@ModelAttribute("success") String success) {
		return JsonMessage.Success(success);
	}

	@RequestMapping(value = "/Error", method = RequestMethod.GET, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String error(@ModelAttribute("error") String error) {
		return JsonMessage.Error(error);
	}

	@RequestMapping(value = "/Unlock-account/{code}", method = RequestMethod.GET)
	public String unlockAccount(@PathVariable String code, RedirectAttributes attributes, Locale locale) {
		accountLockerManager.unlock(code);
		attributes.addFlashAttribute("success", "success.unlock.account");
		return "redirect:/Login";
	}

	@GetMapping("/Analysis-access-management/{token}/Reject")
	public String rejectInvitation(@PathVariable String token, Principal principal, RedirectAttributes attributes) {
		manageAnalysisRight.cancelInvitation(principal, token);
		attributes.addFlashAttribute("success", "success.cancel.invitation");
		return principal == null ? "redirect:/Login" : "redirect:/Analysis/All";
	}

	@GetMapping("/Validate/{token}/Email")
	public String validateEmail(@PathVariable String token, Principal principal, RedirectAttributes attributes) {
		EmailValidatingRequest validatingRequest = serviceEmailValidatingRequest.findByToken(token);
		if (validatingRequest == null)
			throw new ResourceNotFoundException("Token for email validation cannot be found");
		else if (!validatingRequest.getUser().getEmail().equalsIgnoreCase(validatingRequest.getEmail()))
			attributes.addFlashAttribute("error", "error.email.validation.change");
		else {
			validatingRequest.getUser().setEmailValidated(true);
			serviceUser.saveOrUpdate(validatingRequest.getUser());
			serviceEmailValidatingRequest.delete(validatingRequest);
			attributes.addFlashAttribute("success", "success.email.validation");
		}
		return principal == null ? "redirect:/Login" : "redirect:/Account";
	}

}
