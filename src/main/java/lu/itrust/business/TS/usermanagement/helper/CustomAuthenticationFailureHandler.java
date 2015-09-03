package lu.itrust.business.TS.usermanagement.helper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
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

	@Value("${app.settings.ldap.allowed.authentication}")
	private boolean allowedLDAPAutnetication;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		String stringdate = new SimpleDateFormat("MMM d, yyyy HH:mm:ss").format(new Date()), remoteaddr = request.getHeader("X-FORWARDED-FOR"), username = request
				.getParameter("username");
		if (remoteaddr == null)
			remoteaddr = request.getRemoteAddr();

		if (exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException
				&& !(allowedLDAPAutnetication || exception.getCause() instanceof CannotGetJdbcConnectionException)) {
			System.err.println(stringdate + " CustomAuthenticationFailureHandler - ERROR: User '" + username + "' does not exist! Requesting IP: " + remoteaddr);
			request.getSession().setAttribute("LOGIN_ERROR", "error.bad.credential");
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.AUTHENTICATION, "log.user.bad.credential", String.format("%s attempts to connect from %s", username, remoteaddr),
					"anonymous", LogAction.AUTHENTICATE, username, remoteaddr);
		} else if (exception instanceof DisabledException) {
			System.err.println(stringdate + " CustomAuthenticationFailureHandler -  ERROR: User '" + username + "' is disabled! Requesting IP: " + remoteaddr);
			request.getSession().setAttribute("LOGIN_ERROR", "error.account.disabled");
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.AUTHENTICATION, "log.user.account.disabled",
					String.format("%s's account is disabled but he tries to connect from %s", username, remoteaddr), "anonymous", LogAction.AUTHENTICATE, username, remoteaddr);
		} else if (exception.getCause() instanceof TrickException) {
			TrickException e = (TrickException) exception.getCause();
			System.err.println(String.format("%s CustomAuthenticationFailureHandler -  ERROR: User %s, Requesting IP: %s, Cause: %s", stringdate, username, remoteaddr,
					e.getMessage()));
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.ERROR, LogType.AUTHENTICATION, "log.user.account.processing",
					String.format("User: %s from %s, Error: %s", username, remoteaddr, e.getMessage()), "anonymous", LogAction.AUTHENTICATE, username, remoteaddr, e.getMessage());
			request.getSession().setAttribute("LOGIN_ERROR_EXCEPTION", e);
		} else if (exception instanceof InternalAuthenticationServiceException) {
			System.err.println(stringdate + " CustomAuthenticationFailureHandler -  ERROR: " + exception.getMessage());
			request.getSession().setAttribute("LOGIN_ERROR", "error.database.connection_failed");
		}
		super.onAuthenticationFailure(request, response, exception);
	}
}