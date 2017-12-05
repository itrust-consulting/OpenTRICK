package lu.itrust.business.TS.usermanagement.helper;
import static lu.itrust.business.TS.constants.Constant.ANONYMOUS;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.service.AccountLockerManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exception.TrickOtpException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;

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

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");

	@Autowired
	private AccountLockerManager accountLockerManager;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		String stringdate = dateFormat.format(new Date()), remoteaddr = AccountLockerManager.getIP(request), username = request.getParameter("username");
		if (exception instanceof BadCredentialsException
				|| exception instanceof InternalAuthenticationServiceException && !(allowedLDAPAutnetication || exception.getCause() instanceof CannotGetJdbcConnectionException)) {
			lockAccount(request, stringdate, remoteaddr, username, null);
			/**
			 * Log
			 */
			System.err.println(stringdate + " CustomAuthenticationFailureHandler - ERROR: User '" + username + "' does not exist! Requesting IP: " + remoteaddr);
			TrickLogManager.Persist(LogLevel.WARNING, LogType.AUTHENTICATION, "log.user.bad.credential", String.format("%s attempts to connect from %s", username, remoteaddr),
					ANONYMOUS, LogAction.AUTHENTICATE, username, remoteaddr);

		} else if (exception instanceof DisabledException) {
			System.err.println(stringdate + " CustomAuthenticationFailureHandler -  ERROR: User '" + username + "' is disabled! Requesting IP: " + remoteaddr);
			request.getSession().setAttribute("LOGIN_ERROR", "error.account.disabled");
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.AUTHENTICATION, "log.user.account.disabled",
					String.format("%s's account is disabled but he tries to connect from %s", username, remoteaddr), ANONYMOUS, LogAction.AUTHENTICATE, username, remoteaddr);
		} else if (exception.getCause() instanceof TrickException) {
			TrickException e = (TrickException) exception.getCause();
			System.err.println(
					String.format("%s CustomAuthenticationFailureHandler -  ERROR: User %s, Requesting IP: %s, Cause: %s", stringdate, username, remoteaddr, e.getMessage()));
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.ERROR, LogType.AUTHENTICATION, "log.user.account.processing",
					String.format("User: %s from %s, Error: %s", username, remoteaddr, e.getMessage()), ANONYMOUS, LogAction.AUTHENTICATE, username, remoteaddr, e.getMessage());
			request.getSession().setAttribute("LOGIN_ERROR_EXCEPTION", e);
		} else if (exception instanceof TrickOtpException) {
			lockAccount(request, stringdate, remoteaddr, username, exception);
			/**
			 * Log
			 */
			System.err.println(stringdate + " CustomAuthenticationFailureHandler - ERROR: User '" + username + "' on time password failed! Requesting IP: " + remoteaddr);
			TrickLogManager.Persist(LogLevel.ERROR, LogType.AUTHENTICATION, "log.user.otp.failure",
					String.format("%s attempts to connect from %s but on time password failed", username, remoteaddr), username, LogAction.AUTHENTICATE, username, remoteaddr);
		} else if (exception instanceof LockedException) {
			AccountLocker locker = accountLockerManager.lock(username, remoteaddr);
			if (locker == null)
				request.getSession().setAttribute("LOGIN_ERROR", "info.account.unlocked");
			else
				request.getSession().setAttribute("LOGIN_ERROR_HANDLER",
						new MessageHandler("error.wait.account.locked",
								new Object[] { DateFormat.getTimeInstance(DateFormat.MEDIUM, request.getLocale()).format(locker.getLockTime()) },
								"Your account has been locked, please try later"));
		} else if (exception instanceof InternalAuthenticationServiceException) {
			System.err.println(stringdate + " CustomAuthenticationFailureHandler -  ERROR: " + exception.getMessage());
			request.getSession().setAttribute("LOGIN_ERROR", "error.database.connection_failed");
		}
		super.onAuthenticationFailure(request, response, exception);
	}

	private void lockAccount(HttpServletRequest request, String stringDate, String remoteaddr, String username, AuthenticationException exception) {
		AccountLocker locker = accountLockerManager.lock(username, remoteaddr);
		if (locker == null || !locker.isLocked())
			request.getSession().setAttribute("LOGIN_ERROR", exception == null ? "error.bad.credential" : exception.getMessage());
		else
			request.getSession().setAttribute("LOGIN_ERROR_HANDLER",
					new MessageHandler("error.wait.account.locked",
							new Object[] { DateFormat.getTimeInstance(DateFormat.MEDIUM, request.getLocale()).format(locker.getLockTime()) },
							"Your account has been locked, please try later"));
	}
}