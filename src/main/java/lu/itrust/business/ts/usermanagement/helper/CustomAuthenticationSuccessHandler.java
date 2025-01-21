package lu.itrust.business.ts.usermanagement.helper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.LocaleResolver;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOUser;
import lu.itrust.business.ts.database.service.AccountLockerManager;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.usermanagement.User;

/**
 * This class is a custom implementation of the Spring Security's
 * SavedRequestAwareAuthenticationSuccessHandler. It handles the logic for
 * successful authentication and performs additional actions based on the
 * authentication result.
 *
 * The class is responsible for setting the user's locale, logging the
 * authentication event, and managing the account locker.
 *
 * @see SavedRequestAwareAuthenticationSuccessHandler
 */
@Transactional(readOnly = true)
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private DAOUser daoUser;

	@Autowired
	private LocaleResolver localeResolver;

	@Autowired
	private AccountLockerManager accountLockerManager;


	public CustomAuthenticationSuccessHandler() {
		super();
	}

	public CustomAuthenticationSuccessHandler(String defaultTargetUrl) {
		super();
		setDefaultTargetUrl(defaultTargetUrl);
	}


	/**
	 * Called when a user successfully authenticates.
	 * This method is responsible for handling the logic after a successful authentication.
	 *
	 * @param request        the HttpServletRequest object representing the user's request
	 * @param response       the HttpServletResponse object representing the response to be sent back to the user
	 * @param authentication the Authentication object representing the authenticated user
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		try {
			User myUser = daoUser.get(authentication.getName());
			if (myUser.getLocale() == null)
				myUser.setLocale(Locale.ENGLISH.getLanguage());
			localeResolver.setLocale(request, response, new Locale(myUser.getLocale()));
			final String stringdate = new SimpleDateFormat("MMM d, yyyy HH:mm:ss").format(new Date());
			final String remoteaddr = AccountLockerManager.getIP(request);
			if (authentication.getAuthorities().stream()
					.anyMatch(role -> role.getAuthority().equals(Constant.ROLE_OTP_NAME))) {
				System.out.println(stringdate + " CustomAuthenticationSuccessHandler - Pre-authentication: "
						+ authentication.getName() + " is pre-authenticated! Requesting IP: "
						+ remoteaddr);
				TrickLogManager.persist(LogType.AUTHENTICATION, "log.user.pre_authenticated",
						String.format("%s is pre-authenticated from %s", authentication.getName(), remoteaddr),
						authentication.getName(), LogAction.SIGN_IN, remoteaddr);
			} else {
				System.out.println(
						stringdate + " CustomAuthenticationSuccessHandler - SUCCESS: Login success of user '"
								+ authentication.getName() + "'! Requesting IP: " + remoteaddr);
				TrickLogManager.persist(LogType.AUTHENTICATION, "log.user.connect",
						String.format("%s connects from %s", authentication.getName(), remoteaddr),
						authentication.getName(), LogAction.SIGN_IN, remoteaddr);
				accountLockerManager.clean(authentication.getName(), remoteaddr);
			}
		} catch (Exception e) {
			TrickLogManager.persist(e);
		} finally {
			if (!response.isCommitted())
				super.onAuthenticationSuccess(request, response, authentication);
			else
				clearAuthenticationAttributes(request);
		}
	}

}
