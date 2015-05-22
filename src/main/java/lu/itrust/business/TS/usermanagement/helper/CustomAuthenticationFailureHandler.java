package lu.itrust.business.TS.usermanagement.helper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * CustomAuthenticationFailureHandler.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Oct 6, 2014
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");
		Date date = new Date();
		String stringdate = dateFormat.format(date);
		String remoteaddr = request.getHeader("X-FORWARDED-FOR");
		if (remoteaddr == null)
			remoteaddr = request.getRemoteAddr();
		if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
			System.err.println(stringdate + " CustomAuthenticationFailureHandler - ERROR: User '" + request.getParameter("j_username") + "' does not exist! Requesting IP: "
					+ remoteaddr);
			request.getSession().setAttribute("LOGIN_ERROR", "error.bad.credential");
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.AUTHENTICATION, "log.user.bad.credential",
					String.format("%s attempts to connect from %s", request.getParameter("j_username"), remoteaddr), "anonymous", LogAction.AUTHENTICATE,
					request.getParameter("j_username"), remoteaddr);
		} else if (exception.getClass().isAssignableFrom(DisabledException.class)) {
			System.err.println(stringdate + " CustomAuthenticationFailureHandler -  ERROR: User '" + request.getParameter("j_username") + "' is disabled! Requesting IP: "
					+ remoteaddr);
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.AUTHENTICATION, "log.user.account.disabled",
					String.format("%s's account is disabled but he tries to connect from %s", request.getParameter("j_username"), remoteaddr), "anonymous", LogAction.AUTHENTICATE,
					request.getParameter("j_username"), remoteaddr);
		} else if (exception.getClass().isAssignableFrom(InternalAuthenticationServiceException.class)) {
			System.err.println(stringdate + " CustomAuthenticationFailureHandler -  ERROR: Database Connection Failed!");
			request.getSession().setAttribute("LOGIN_ERROR", "error.database.connection_failed");
		}
		super.onAuthenticationFailure(request, response, exception);
	}
}