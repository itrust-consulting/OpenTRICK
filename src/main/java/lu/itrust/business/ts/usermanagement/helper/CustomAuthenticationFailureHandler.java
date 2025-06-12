package lu.itrust.business.ts.usermanagement.helper;

import static lu.itrust.business.ts.constants.Constant.ANONYMOUS;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.service.AccountLockerManager;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exception.TrickOtpException;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;


/**
 * This class is a custom implementation of the Spring Security's
 * SimpleUrlAuthenticationFailureHandler. It handles authentication failure
 * scenarios and performs additional actions based on the type of exception
 * thrown.
 *
 * The class provides methods to lock user accounts, log authentication failure
 * events, and handle different types of authentication exceptions such as
 * BadCredentialsException, DisabledException, TrickException, TrickOtpException,
 * LockedException, and InternalAuthenticationServiceException.
 *
 * @see SimpleUrlAuthenticationFailureHandler
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	/**
	 *
	 */
	private static final String OTP_AUTHORISE = "/OTP/Authorise";

	@Value("${app.settings.ldap.allowed.authentication}")
	private boolean allowedLDAPAutnetication;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");

	@Autowired
	private AccountLockerManager accountLockerManager;

	public CustomAuthenticationFailureHandler() {
	}

	public CustomAuthenticationFailureHandler(String defaultFailureUrl) {
		super(defaultFailureUrl);
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String stringdate = dateFormat.format(new Date()), remoteaddr = AccountLockerManager.getIP(request),
				username = request.getParameter("username");
		if (exception instanceof BadCredentialsException
				|| exception instanceof InternalAuthenticationServiceException && !(allowedLDAPAutnetication
						|| exception.getCause() instanceof CannotGetJdbcConnectionException)) {
			lockAccount(request, remoteaddr, username, null);
			/**
			 * Log
			 */
			System.err.println(stringdate + " CustomAuthenticationFailureHandler - ERROR: User '" + username
					+ "' does not exist! Requesting IP: " + remoteaddr);
			TrickLogManager.persist(LogLevel.WARNING, LogType.AUTHENTICATION, "log.user.bad.credential",
					String.format("%s attempts to connect from %s", username, remoteaddr),
					ANONYMOUS, LogAction.AUTHENTICATE, username, remoteaddr);

		} else if (exception instanceof DisabledException) {
			System.err.println(stringdate + " CustomAuthenticationFailureHandler -  ERROR: User '" + username
					+ "' is disabled! Requesting IP: " + remoteaddr);
			request.getSession().setAttribute("LOGIN_ERROR", "error.account.disabled");
			/**
			 * Log
			 */
			TrickLogManager.persist(LogLevel.WARNING, LogType.AUTHENTICATION, "log.user.account.disabled",
					String.format("%s's account is disabled but he tries to connect from %s", username, remoteaddr),
					ANONYMOUS, LogAction.AUTHENTICATE, username, remoteaddr);
		} else if (exception.getCause() instanceof TrickException) {
			TrickException e = (TrickException) exception.getCause();
			System.err.println(
					String.format(
							"%s CustomAuthenticationFailureHandler -  ERROR: User %s, Requesting IP: %s, Cause: %s",
							stringdate, username, remoteaddr, e.getMessage()));
			/**
			 * Log
			 */
			TrickLogManager.persist(LogLevel.ERROR, LogType.AUTHENTICATION, "log.user.account.processing",
					String.format("User: %s from %s, Error: %s", username, remoteaddr, e.getMessage()), ANONYMOUS,
					LogAction.AUTHENTICATE, username, remoteaddr, e.getMessage());
			request.getSession().setAttribute("LOGIN_ERROR_EXCEPTION", e);
		} else if (exception instanceof TrickOtpException) {
			lockAccount(request, remoteaddr, username, exception);
			/**
			 * Log
			 */
			System.err.println(stringdate + " CustomAuthenticationFailureHandler - ERROR: User '" + username
					+ "' one time password failed! Requesting IP: " + remoteaddr);
			TrickLogManager.persist(LogLevel.ERROR, LogType.AUTHENTICATION, "log.user.otp.failure",
					String.format("%s attempts to connect from %s but one time password failed", username, remoteaddr),
					username, LogAction.AUTHENTICATE, username, remoteaddr);
		} else if (exception instanceof LockedException) {
			AccountLocker locker = accountLockerManager.lock(username, remoteaddr);
			if (locker == null)
				request.getSession().setAttribute("LOGIN_ERROR", "info.account.unlocked");
			else
				request.getSession().setAttribute("LOGIN_ERROR_HANDLER",
						new MessageHandler("error.wait.account.locked",
								new Object[] { DateFormat.getTimeInstance(DateFormat.MEDIUM, request.getLocale())
										.format(locker.getLockTime()) },
								"Your account has been locked, please try later"));
		} else if (exception instanceof InternalAuthenticationServiceException) {
			System.err.println(stringdate + " CustomAuthenticationFailureHandler -  ERROR: " + exception.getMessage());
			request.getSession().setAttribute("LOGIN_ERROR", "error.database.connection_failed");
		}
		super.onAuthenticationFailure(request, response, exception);
	}

	private void lockAccount(HttpServletRequest request, String remoteaddr, String username,
			AuthenticationException exception) {
		AccountLocker locker = accountLockerManager.lock(username, remoteaddr);
		if (locker == null || !locker.isLocked())
			request.getSession().setAttribute("LOGIN_ERROR",
					exception == null ? "error.bad.credential" : exception.getMessage());
		else {
			
			if (request.getServletPath().equalsIgnoreCase(OTP_AUTHORISE))
			  request.getSession().invalidate();

			request.getSession().setAttribute("LOGIN_ERROR_HANDLER",
					new MessageHandler("error.wait.account.locked",
							new Object[] { DateFormat.getTimeInstance(DateFormat.MEDIUM, request.getLocale())
									.format(locker.getLockTime()) },
							"Your account has been locked, please try later"));
		}
	}
}