/**
 * 
 */
package lu.itrust.business.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.User;
import lu.itrust.business.service.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author oensuifudine
 * 
 */
@Controller
public class ControllerHome {

	@Autowired
	private ServiceUser serviceUser;

	@Secured("ROLE_USER")
	@RequestMapping("/index")
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
	@RequestMapping("/fromTRESPASS")
	public String fromTresPass(){
		return "redirect:/analysis/all";
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
