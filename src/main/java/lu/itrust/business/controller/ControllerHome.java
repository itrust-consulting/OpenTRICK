/**
 * 
 */
package lu.itrust.business.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.User;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@Secured("ROLE_USER")
	@RequestMapping("/home")
	public String home(HttpSession session, Principal principal)
			throws Exception {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			user = serviceUser.get(principal.getName());
			if (user != null)
				session.setAttribute("user", user);
			else return "redirect:/logout";
		}
		return "index";
	}
	
	@Secured("ROLE_USER")
	@RequestMapping("/feedback")
	public @ResponseBody List<MessageHandler> revice(Principal principal){
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

	public void setServiceUser(ServiceUser serviceUser) {
		this.serviceUser = serviceUser;
	}
}
