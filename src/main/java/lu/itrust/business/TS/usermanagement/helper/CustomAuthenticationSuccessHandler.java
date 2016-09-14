package lu.itrust.business.TS.usermanagement.helper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.LocaleResolver;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.usermanagement.User;

/**
 * AuthenticationSuccessHandler.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Sep 26, 2014
 */
@Transactional
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private DAOUser daoUser;

	@Autowired
	private LocaleResolver localeResolver;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		try {
			User myUser = daoUser.get(authentication.getName());
			if (myUser.getLocale() == null) {
				myUser.setLocale("en");
				daoUser.saveOrUpdate(myUser);
			}
			localeResolver.setLocale(request, response, new Locale(myUser.getLocale()));
			String stringdate = new SimpleDateFormat("MMM d, yyyy HH:mm:ss").format(new Date());
			String remoteaddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteaddr == null)
				remoteaddr = request.getRemoteAddr();
			System.out
					.println(stringdate + " CustomAuthenticationSuccessHandler - SUCCESS: Login success of user '" + authentication.getName() + "'! Requesting IP: " + remoteaddr);
			TrickLogManager.Persist(LogType.AUTHENTICATION, "log.user.connect", String.format("%s connects from %s", authentication.getName(), remoteaddr),
					authentication.getName(), LogAction.SIGN_IN, remoteaddr);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		} finally {
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}
}
