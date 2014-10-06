package lu.itrust.business.TS.usermanagement;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.LocaleResolver;

/**
 * AuthenticationSuccessHandler.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Sep 26, 2014
 */
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private LocaleResolver localeResolver;

	@Override
	@Transactional
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		UserDetails user = (UserDetails) authentication.getPrincipal();

		try {

			User myUser = serviceUser.get(user.getUsername());

			int nbr = myUser.getApplicationSettings().size();

			User.createDefaultSettings(myUser);

			if (nbr < myUser.getApplicationSettings().size())
				serviceUser.saveOrUpdate(myUser);

			Locale locale = new Locale(myUser.getApplicationSettingsAsMap().get(Constant.SETTING_DEFAULT_UI_LANGUAGE).getValue());

			localeResolver.setLocale(request, response, locale);
			
			DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");
			Date date = new Date();
			String stringdate = dateFormat.format(date);
			String remoteaddr = request.getRemoteAddr();
			
			System.out.println(stringdate +" CustomAuthenticationSuccessHandler - SUCCESS: Login success of user '"+request.getParameter("j_username") +"'! Requesting IP: "+remoteaddr);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}
}
