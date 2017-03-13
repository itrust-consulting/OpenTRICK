/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.Locale;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@PreAuthorize(Constant.ROLE_OTP_ONLY)
@RequestMapping("OTP")
public class ControllerOTP {
	
	@Autowired
	private ServiceUser serviceUser;
	
	public String authorise(Principal principal, Model model, HttpRequest request, HttpResponse response,Locale locale){
		User user = serviceUser.get(principal.getName());
		Totp totp = new Totp(user.getSecret());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("code", totp.now());
		return "otp/form";
	}
}
