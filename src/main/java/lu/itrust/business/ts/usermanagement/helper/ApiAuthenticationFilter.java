/**
 * 
 */
package lu.itrust.business.ts.usermanagement.helper;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lu.itrust.business.ts.constants.Constant;

/**
 * @author eomar
 *
 */
public class ApiAuthenticationFilter extends OncePerRequestFilter {

	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private AuthenticationEntryPoint authenticationEntryPoint;
	private AuthenticationManager authenticationManager;
	private RememberMeServices rememberMeServices = new NullRememberMeServices();
	private boolean ignoreFailure = false;

	/**
	 * @param authenticationManager
	 */
	public ApiAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * @param authenticationManager
	 * @param authenticationEntryPoint
	 */
	public ApiAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationManager = authenticationManager;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		final boolean debug = this.logger.isDebugEnabled();
		String token = request.getHeader(Constant.API_AUTHENTICATION_TOKEN_NAME);
		if (!StringUtils.hasText(token)) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			if (authenticationIsRequired(token)) {
				TokenRequestAuthentication authRequest = new TokenRequestAuthentication(this.authenticationDetailsSource.buildDetails(request), token);
				Authentication authResult = this.authenticationManager.authenticate(authRequest);
				
				if (debug) 
					this.logger.debug("Authentication success: " + authResult);
		
				SecurityContextHolder.getContext().setAuthentication(authResult);

				this.rememberMeServices.loginSuccess(request, response, authResult);

				onSuccessfulAuthentication(request, response, authResult);

			}
		} catch (AuthenticationException failed) {
			SecurityContextHolder.clearContext();
			if (debug) {
				this.logger.debug("Authentication request for failed: " + failed);
			}
			this.rememberMeServices.loginFail(request, response);
			onUnsuccessfulAuthentication(request, response, failed);
			if (this.ignoreFailure) {
				filterChain.doFilter(request, response);
			} else {
				this.authenticationEntryPoint.commence(request, response, failed);
			}
			return;
		}
		filterChain.doFilter(request, response);
	}

	protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

	}

	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.filter.GenericFilterBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws ServletException {
		Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");

		if (!isIgnoreFailure()) {
			Assert.notNull(this.authenticationEntryPoint, "An AuthenticationEntryPoint is required");
		}
	}

	private boolean authenticationIsRequired(String username) {
		// Only reauthenticate if username doesn't match SecurityContextHolder
		// and user
		// isn't authenticated
		// (see SEC-53)
		Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

		if (existingAuth == null || !existingAuth.isAuthenticated()) {
			return true;
		}

		if (existingAuth instanceof UsernamePasswordAuthenticationToken && !existingAuth.getCredentials().equals(username)) {
			return true;
		}

		// Handle unusual condition where an AnonymousAuthenticationToken is
		// already
		// present
		// This shouldn't happen very often, as BasicProcessingFitler is meant
		// to be
		// earlier in the filter
		// chain than AnonymousAuthenticationFilter. Nevertheless, presence of
		// both an
		// AnonymousAuthenticationToken
		// together with a BASIC authentication request header should indicate
		// reauthentication using the
		// BASIC protocol is desirable. This behaviour is also consistent with
		// that
		// provided by form and digest,
		// both of which force re-authentication if the respective header is
		// detected (and
		// in doing so replace
		// any existing AnonymousAuthenticationToken). See SEC-610.
		if (existingAuth instanceof AnonymousAuthenticationToken) {
			return true;
		}

		return false;
	}

	/**
	 * @return the authenticationDetailsSource
	 */
	public AuthenticationDetailsSource<HttpServletRequest, ?> getAuthenticationDetailsSource() {
		return authenticationDetailsSource;
	}

	/**
	 * @param authenticationDetailsSource
	 *            the authenticationDetailsSource to set
	 */
	public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
		this.authenticationDetailsSource = authenticationDetailsSource;
	}

	/**
	 * @return the authenticationEntryPoint
	 */
	public AuthenticationEntryPoint getAuthenticationEntryPoint() {
		return authenticationEntryPoint;
	}

	/**
	 * @param authenticationEntryPoint
	 *            the authenticationEntryPoint to set
	 */
	public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	/**
	 * @return the authenticationManager
	 */
	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	/**
	 * @param authenticationManager
	 *            the authenticationManager to set
	 */
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * @return the rememberMeServices
	 */
	public RememberMeServices getRememberMeServices() {
		return rememberMeServices;
	}

	/**
	 * @param rememberMeServices
	 *            the rememberMeServices to set
	 */
	public void setRememberMeServices(RememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}

	/**
	 * @return the ignoreFailure
	 */
	protected boolean isIgnoreFailure() {
		return ignoreFailure;
	}

	/**
	 * @param ignoreFailure
	 *            the ignoreFailure to set
	 */
	protected void setIgnoreFailure(boolean ignoreFailure) {
		this.ignoreFailure = ignoreFailure;
	}

}
