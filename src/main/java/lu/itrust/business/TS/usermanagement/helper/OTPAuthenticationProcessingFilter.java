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
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class OTPAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	@Autowired
	private SessionFactory sessionFactory;

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
		if (authentication == null || !authentication.isAuthenticated())
			throw new AuthenticationServiceException("label.opt.error.user.not_pre_authenticated");
		String code = request.getParameter(Constant.OTP_CHALLENGE_USER_RESPONSE);
		if (code == null)
			throw new AuthenticationServiceException("label.otp.error.code.not_found");
		Session session = null;
		try {
			User user = new DAOUserHBM(session = sessionFactory.openSession()).get(authentication.getName());
			Totp totp = (Totp) request.getSession().getAttribute(Constant.OTP_CHALLENGE_AUTHEN);
			if (totp == null) {
				String secret = user.getSecret();
				if (StringUtils.isEmpty(secret))
					throw new AuthenticationServiceException("label.otp.error.code.no_secret");
				totp = new Totp(secret);
			}
			if (!totp.verify(code))
				throw new AuthenticationServiceException("label.otp.error.code");
			Collection<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getType().name())).collect(Collectors.toList());
			UserDetails userDetails = new org.springframework.security.core.userdetails.User(authentication.getName(), user.getPassword(), user.isEnable(), true, true, true,
					authorities);
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), authorities);
			authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
			return authRequest;
		} finally {
			request.getSession().removeAttribute(Constant.OTP_CHALLENGE_AUTHEN);
			if (session != null)
				session.close();
		}
	}
}
