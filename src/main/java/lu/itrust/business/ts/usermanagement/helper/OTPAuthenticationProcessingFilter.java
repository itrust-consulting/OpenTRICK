/**
 * 
 */
package lu.itrust.business.ts.usermanagement.helper;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.ts.database.service.AccountLockerManager;
import lu.itrust.business.ts.exception.TrickOtpException;
import lu.itrust.business.ts.usermanagement.User;


/**
 * This class is responsible for processing OTP (One-Time Password) authentication.
 * It extends the AbstractAuthenticationProcessingFilter class from Spring Security.
 * 
 * The filter checks if the authentication method is supported and if the user session is expired.
 * It also verifies the OTP code provided by the user and authenticates the user if the code is valid.
 * 
 * This filter requires the following dependencies to be autowired:
 * - SessionFactory: Used to open a session for database operations.
 * - AccountLockerManager: Used to manage user account lockouts.
 * 
 * The filter is configured with a URL pattern and HTTP method for authentication requests.
 * 
 * @param url The URL pattern for authentication requests.
 */
public class OTPAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private AccountLockerManager accountLockerManager;

	/**
	 * 
	 */
	public OTPAuthenticationProcessingFilter(String url) {
		super(new AntPathRequestMatcher(url, "POST"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.
	 * AbstractAuthenticationProcessingFilter#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() {
	}

	/**
	 * Attempts to authenticate the user based on the provided request and response.
	 * 
	 * @param request  the HttpServletRequest object representing the user's request
	 * @param response the HttpServletResponse object representing the response to be sent back to the user
	 * @return an Authentication object representing the authenticated user
	 * @throws AuthenticationException if there is an error during the authentication process
	 * @throws IOException             if there is an error with the input/output operations
	 * @throws ServletException        if there is an error with the servlet handling
	 */
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		if (!request.getMethod().equals("POST"))
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null)
			throw new AuthenticationServiceException("User session has been expired!");
		else if (authentication.getAuthorities().stream().noneMatch(role -> role.getAuthority().equals(Constant.ROLE_OTP_NAME)))
			throw new AuthenticationServiceException("User is already authenticated");
		String code = request.getParameter(Constant.OTP_CHALLENGE_USER_RESPONSE);
		if (code == null)
			throw new TrickOtpException("error.otp.code.not_found");
		Session session = null;
		try {
			User user = new DAOUserHBM(session = sessionFactory.openSession()).get(authentication.getName());
			request.setAttribute("username", user.getLogin());
			Totp totp = (Totp) request.getSession().getAttribute(Constant.OTP_CHALLENGE_AUTHEN);
			if (totp == null) {
				String secret = user.getSecret();
				if (!StringUtils.hasText(secret))
					throw new TrickOtpException("error.otp.code.no_secret");
				totp = new Totp(secret);
			} else {
				Long timeout = (Long) request.getSession().getAttribute(Constant.OTP_CHALLENGE_AUTHEN_INIT_TIME);
				if (timeout == null || timeout < System.currentTimeMillis())
					throw new TrickOtpException("error.otp.timeout");
			}
			
			if (!totp.verify(code))
				throw new TrickOtpException("error.otp.invalid.code");
			Collection<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getType().name())).collect(Collectors.toList());
			UserDetails userDetails = new org.springframework.security.core.userdetails.User(authentication.getName(), user.getPassword(), user.isEnable(), true, true, true,
					authorities);
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), authorities);
			authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
			request.getSession().removeAttribute(Constant.OTP_CHALLENGE_AUTHEN);
			request.getSession().removeAttribute(Constant.OTP_CHALLENGE_AUTHEN_INIT_TIME);
			accountLockerManager.clean(user.getLogin(), AccountLockerManager.getIP(request) );
			return authRequest;
		} finally {
			if (session != null)
				session.close();
		}
	}
}
