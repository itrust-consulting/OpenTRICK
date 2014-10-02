/**
 * 
 */
package lu.itrust.business.view.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserSqLite;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
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
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
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
	private MessageSource messageSource;

	@Autowired
	private ServiceUserSqLite serviceUserSqLite;

	@Autowired
	private LocaleResolver localeResolver;
	
	@PreAuthorize(Constant.ROLE_MIN_USER)
	@RequestMapping("/home")
	public String home(Model model, Principal principal, SessionLocaleResolver session, HttpServletRequest request, HttpServletResponse response) throws Exception {
		model.addAttribute("userSqLites", serviceUserSqLite.getByFileName(principal.getName()));
		return "index";
	}

	@RequestMapping(value = "/MessageResolver",method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String resolveMessage(@RequestBody MessageHandler message, Locale locale) throws JsonParseException, JsonMappingException, IOException {
		return String.format("{\"message\":\"%s\"}",messageSource.getMessage(message.getCode(), message.getParameters(), message.getMessage(), locale));
	}

	@PreAuthorize(Constant.ROLE_MIN_USER)
	@RequestMapping("/feedback")
	public @ResponseBody List<MessageHandler> revice(Principal principal) {
		return serviceTaskFeedback.recieve(principal.getName());
	}

	@RequestMapping("/login")
	public String login() {
		return "loginForm";
	}

	@RequestMapping(value = "/IsAuthenticate", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody boolean isAuthenticate(Principal principal) {
		return principal != null;
	}

	@RequestMapping("/login/error")
	public String login(RedirectAttributes attributes, Locale locale) {
		attributes.addFlashAttribute("errors", messageSource.getMessage("error.bad.credential", null, "Please check your credentials", locale));
		return "redirect:/login";
	}

	@RequestMapping("/logout")
	public String logout() {
		return "redirect:/j_spring_security_logout";
	}

	@RequestMapping(value = "/Success", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String success(@ModelAttribute("success") String success) {
		return JsonMessage.Success(success);
	}

}
