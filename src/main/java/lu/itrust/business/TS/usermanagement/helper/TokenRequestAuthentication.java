/**
 * 
 */
package lu.itrust.business.TS.usermanagement.helper;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author eomar
 *
 */
public class TokenRequestAuthentication implements Authentication {
	
	private Object details;
	
	private String token;
	
	/**
	 * @param token
	 */
	public TokenRequestAuthentication(String token) {
		this.token = token;
	}
	
	/**
	 * @param details
	 * @param token
	 */
	public TokenRequestAuthentication(Object details, String token) {
		this.details = details;
		this.token = token;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	@Override
	public String getName() {
		return "IDS";
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.Authentication#getAuthorities()
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.Authentication#getCredentials()
	 */
	@Override
	public Object getCredentials() {
		return token;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.Authentication#getDetails()
	 */
	@Override
	public Object getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(Object details) {
		this.details = details;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.Authentication#getPrincipal()
	 */
	@Override
	public Object getPrincipal() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.Authentication#isAuthenticated()
	 */
	@Override
	public boolean isAuthenticated() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.Authentication#setAuthenticated(boolean)
	 */
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
	}

}
