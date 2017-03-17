/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.jboss.aerogear.security.otp.api.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceEmailSender;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Controller
@PreAuthorize(Constant.ROLE_OTP_ONLY)
@RequestMapping("/OTP")
public class ControllerOTP {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceEmailSender serviceEmailSender;

	@Value("${app.settings.otp.attempt.timeout}")
	private long otpTimeout;

	@RequestMapping("/Options")
	public String options(Principal principal, Model model, Locale locale) {
		User user = serviceUser.get(principal.getName());
		char[] characters = user.getEmail().toCharArray();
		for (int i = 0; i < characters.length; i++) {
			if (characters[i] == '@')
				break;
			characters[i] = '*';
		}
		model.addAttribute("email", new String(characters));
		model.addAttribute("application", StringUtils.hasText(user.getSecret()));
		//model.addAttribute("phoneNumber", "**********86");
		return "otp/options";
	}

	@RequestMapping("/Generate-code")
	public String generateCode(@RequestParam(name = "otp-method") String method, @RequestParam(name = "otp-method-value") String value, HttpSession session, Principal principal,
			Model model, RedirectAttributes attributes, Locale locale) {
		Totp totp = (Totp) session.getAttribute(Constant.OTP_CHALLENGE_AUTHEN);
		Long timeout = (Long) session.getAttribute(Constant.OTP_CHALLENGE_AUTHEN_INIT_TIME);
		if (totp == null || timeout < (System.currentTimeMillis() + 20000)) {
			session.setAttribute(Constant.OTP_CHALLENGE_AUTHEN, totp = new Totp(Base32.random(), new Clock((int) (otpTimeout*.001))));
			session.setAttribute(Constant.OTP_CHALLENGE_AUTHEN_INIT_TIME, timeout = (System.currentTimeMillis() + otpTimeout));
		}
		User user = serviceUser.get(principal.getName());
		switch (method) {
		/*case "tel":
			break;*/
		case "email":
			if (user.getEmail().equalsIgnoreCase(value))
				serviceEmailSender.sendOTPCode(totp.now(), timeout, user);
			break;
		default:
			attributes.addFlashAttribute("error", "error.otp.method.not_found");
			return "redirect:/OTP/Options";
		}
		model.addAttribute("otp-method", method);
		return "otp/form";
	}
}
