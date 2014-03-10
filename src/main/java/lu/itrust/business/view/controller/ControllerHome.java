/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserSqLite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

	@PreAuthorize(Constant.ROLE_MIN_USER)
	@RequestMapping("/home")
	public String home(Model model, Principal principal) throws Exception {
		model.addAttribute("userSqLites", serviceUserSqLite.findByFileName(principal.getName()));
		return "index";
	}

//	@Secured("ROLE_USER")
	@PreAuthorize(Constant.ROLE_MIN_USER)
	@RequestMapping(value = "/MessageResolver", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	String resolveMessage(Locale locale, HttpServletRequest request) {
		String code = request.getParameter("code");
		String defaultText = request.getParameter("default");
		return messageSource.getMessage(code, null, defaultText, locale);
	}

	@PreAuthorize(Constant.ROLE_MIN_USER)
	@RequestMapping("/feedback")
	public @ResponseBody
	List<MessageHandler> revice(Principal principal) {
		return serviceTaskFeedback.recive(principal.getName());
	}

	@RequestMapping("/login")
	public String login() {
		return "loginForm";
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
}
