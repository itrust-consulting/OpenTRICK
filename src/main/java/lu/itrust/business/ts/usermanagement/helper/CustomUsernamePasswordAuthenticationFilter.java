/**
 * 
 */
package lu.itrust.business.ts.usermanagement.helper;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.AccountLockerManager;
import lu.itrust.business.ts.database.service.ServiceUser;


/**
 * This class extends the Spring Security's `UsernamePasswordAuthenticationFilter` class
 * to provide custom authentication behavior.
 */
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


	@Autowired
	private AccountLockerManager accountLockerManager;

	@Autowired
	private ServiceUser serviceUser;

	private boolean enable2FA = true;

	private boolean force2FA = true;

	/**
	 * 
	 */
	public CustomUsernamePasswordAuthenticationFilter() {
		this("/Signin");
	}

	public CustomUsernamePasswordAuthenticationFilter(String filterProcessesUrl) {
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(filterProcessesUrl, "POST"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.
	 * UsernamePasswordAuthenticationFilter#obtainUsername(jakarta.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected String obtainUsername(HttpServletRequest request) {
		String username = super.obtainUsername(request);
		if (accountLockerManager.isLocked(username, AccountLockerManager.getIP(request)))
			throw new LockedException("User account is locked");
		return username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.
	 * UsernamePasswordAuthenticationFilter#attemptAuthentication(jakarta.servlet.
	 * http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse)
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		Authentication authentication = super.attemptAuthentication(request, response);
		if (enable2FA && authentication != null && authentication.isAuthenticated()
				&& (force2FA || isUser2FEnabled(authentication)))
			return prepareOTPAuthentication(request, authentication);
		return authentication;
	}

	private boolean isUser2FEnabled(Authentication authentication) {
		return serviceUser.isUsing2FA(authentication.getName());
	}

	private Authentication prepareOTPAuthentication(HttpServletRequest request, Authentication authentication) {
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				authentication.getName(), authentication.getCredentials(),
				Arrays.asList(new SimpleGrantedAuthority(Constant.ROLE_OTP_NAME)));
		request.getSession().setAttribute(Constant.OTP_PRE_AUTHENTICATION, authRequest);
		setDetails(request, authRequest);
		return authRequest;
	}

	/**
	 * @return the isEnable2FA
	 */
	public boolean isEnable2FA() {
		return enable2FA;
	}

	/**
	 * @param isEnable2FA
	 *                    the isEnable2FA to set
	 */
	public void setEnable2FA(boolean enable2FA) {
		this.enable2FA = enable2FA;
	}

	/**
	 * @return the force2FA
	 */
	public boolean isForce2FA() {
		return force2FA;
	}

	/**
	 * @param force2fa
	 *                 the force2FA to set
	 */
	public void setForce2FA(boolean force2fa) {
		force2FA = force2fa;
	}

}
