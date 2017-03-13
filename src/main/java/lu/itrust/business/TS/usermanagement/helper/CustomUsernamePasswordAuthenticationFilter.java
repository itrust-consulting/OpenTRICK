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
				if (!user.isUsing2FA()) {
					List<GrantedAuthority> roles = new LinkedList<>();
					roles.add(new SimpleGrantedAuthority("ROLE_PRE_AUTHEN"));
					return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), roles);
				}
			}
			return authentication;
		} finally {
			if (session != null)
				session.close();
		}
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
