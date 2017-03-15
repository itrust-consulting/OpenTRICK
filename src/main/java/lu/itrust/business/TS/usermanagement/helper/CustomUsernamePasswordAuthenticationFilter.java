/**
 * 
 */
package lu.itrust.business.TS.usermanagement.helper;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
	private SessionFactory sessionFactory;

	private boolean enable2FA = false;

	/**
	 * 
	 */
	public CustomUsernamePasswordAuthenticationFilter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.
	 * UsernamePasswordAuthenticationFilter#attemptAuthentication(javax.servlet.
	 * http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		Session session = null;
		try {
			Authentication authentication = super.attemptAuthentication(request, response);
			if (enable2FA && authentication != null && authentication.isAuthenticated()) {
				User user = new DAOUserHBM(session = sessionFactory.openSession()).get(authentication.getName());
				if (user.isUsing2FA())
					return prepareOTPAuthentication(request, authentication);
			}
			return authentication;
		} finally {
			if (session != null)
				session.close();
		}
	}

	private Authentication prepareOTPAuthentication(HttpServletRequest request, Authentication authentication) {
		List<GrantedAuthority> roles = new LinkedList<>();
		roles.add(new SimpleGrantedAuthority(Constant.ROLE_OTP_NAME));
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(authentication.getName(), authentication.getCredentials(), roles);
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
	 *            the isEnable2FA to set
	 */
	public void setEnable2FA(boolean enable2FA) {
		this.enable2FA = enable2FA;
	}

}
