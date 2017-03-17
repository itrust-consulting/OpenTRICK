/**
 * 
 */
package lu.itrust.business.TS.usermanagement.helper;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.service.AccountLockerManager;
import lu.itrust.business.TS.exception.TrickOtpException;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
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

	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		if (!request.getMethod().equals("POST"))
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null)
			throw new AuthenticationServiceException("User session has been expired!");
		else if (!authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals(Constant.ROLE_OTP_NAME)))
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
				if (StringUtils.isEmpty(secret))
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
