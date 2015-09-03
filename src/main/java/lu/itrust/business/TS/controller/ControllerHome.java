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

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserSqLite;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.general.TSSetting;
import lu.itrust.business.TS.model.general.TSSettingName;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;

import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	private PermissionEvaluator permissionEvaluator;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceUserSqLite serviceUserSqLite;

	@Autowired
	private LocaleResolver localeResolver;
	
	@Autowired
	private ServiceTSSetting serviceTSSetting;

	@PreAuthorize(Constant.ROLE_MIN_USER)
	@RequestMapping("/Home")
	public String home() throws Exception {
		return "default/home";
	}

	@RequestMapping(value = "/MessageResolver", method = RequestMethod.POST, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String resolveMessage(@RequestBody MessageHandler message, Locale locale) {
		Locale customLocale = message.getLanguage() != null ? new Locale(message.getLanguage().length() == 2 ? message.getLanguage() : message.getLanguage().substring(0, 2))
				: null;
		return String.format("{\"message\":\"%s\"}",
				messageSource.getMessage(message.getCode(), message.getParameters(), message.getMessage(), customLocale != null ? customLocale : locale));
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
			model.addAttribute("success", messageSource.getMessage("success.create.account", null, "Account has been created successfully", locale));
			model.addAttribute("username", request.getParameter("login") == null ? "" : request.getParameter("login"));
		}
		return "default/login";
	}

	private void loadSettings(Model model, Locale locale) {
		try {
			TSSetting register = serviceTSSetting.get(TSSettingName.SETTING_ALLOWED_SIGNUP), resetPassword = serviceTSSetting.get(TSSettingName.SETTING_ALLOWED_RESET_PASSWORD);
			model.addAttribute("allowRegister", register == null || register.getBoolean());
			model.addAttribute("resetPassword", register == null || resetPassword.getBoolean());
		} catch (GenericJDBCException e) {
			model.addAttribute("error", messageSource.getMessage("error.database.connection_failed", null, "No connection to the database...", locale));
		}catch(Exception e){
			model.addAttribute("error", messageSource.getMessage("error.setting.not.loaded", null, "Settings cannot be loaded", locale));
		}
	}

	@RequestMapping(value = "/IsAuthenticate", method = RequestMethod.GET, headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody boolean isAuthenticate(Principal principal, HttpSession session, HttpServletResponse response) throws Exception {
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

}
