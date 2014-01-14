/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.usermanagment.User;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	@Secured("ROLE_USER")
	@RequestMapping("/home")
	public String home(HttpSession session, Principal principal)
			throws Exception {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			user = serviceUser.get(principal.getName());
			if (user != null)
				session.setAttribute("user", user);
			else
				return "redirect:/logout";
		}
		return "index";
	}

	@Secured("ROLE_USER")
	@RequestMapping(value = "/MessageResolver", method = RequestMethod.GET, headers = "Accept=application/json")
    public @ResponseBody
    String resolveMessage(Locale locale, HttpServletRequest request) {
		String code = request.getParameter("code");
		String defaultText = request.getParameter("default");
        return messageSource.getMessage(code, null,defaultText,locale);
    }

	@Secured("ROLE_USER")
	@RequestMapping("/feedback")
	public @ResponseBody
	List<MessageHandler> revice(Principal principal) {
		return serviceTaskFeedback.recive(principal.getName());
	}

	@RequestMapping("/login")
	public String login() {
		return "loginForm";
	}

	@RequestMapping("/logout")
	public String logout() {
		return "redirect:/j_spring_security_logout";
	}
}
