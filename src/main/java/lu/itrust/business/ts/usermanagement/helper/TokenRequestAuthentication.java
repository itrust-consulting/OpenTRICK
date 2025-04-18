/**
 * 
 */
package lu.itrust.business.ts.usermanagement.helper;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


/**
 * Represents an authentication object for token-based authentication.
 */
public class TokenRequestAuthentication implements Authentication {
	
	private Object details;
	private String token;
	
	/**
	 * Constructs a new TokenRequestAuthentication object with the specified token.
	 * 
	 * @param token the authentication token
	 */
	public TokenRequestAuthentication(String token) {
		this.token = token;
	}
	
	/**
	 * Constructs a new TokenRequestAuthentication object with the specified details and token.
	 * 
	 * @param details the authentication details
	 * @param token the authentication token
	 */
	public TokenRequestAuthentication(Object details, String token) {
		this.details = details;
		this.token = token;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Returns the name of the authentication principal.
	 * 
	 * @return the name of the authentication principal
	 */
	@Override
	public String getName() {
		return "IDS";
	}

	/**
	 * Returns an empty list of granted authorities.
	 * 
	 * @return an empty list of granted authorities
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	/**
	 * Returns the authentication credentials.
	 * 
	 * @return the authentication credentials
	 */
	@Override
	public Object getCredentials() {
		return token;
	}

	/**
	 * Returns the authentication details.
	 * 
	 * @return the authentication details
	 */
	@Override
	public Object getDetails() {
		return details;
	}

	/**
	 * Sets the authentication details.
	 * 
	 * @param details the authentication details to set
	 */
	public void setDetails(Object details) {
		this.details = details;
	}

	/**
	 * Returns the authentication principal.
	 * 
	 * @return the authentication principal
	 */
	@Override
	public Object getPrincipal() {
		return getName();
	}

	/**
	 * Returns whether the authentication is authenticated.
	 * 
	 * @return true if the authentication is authenticated, false otherwise
	 */
	@Override
	public boolean isAuthenticated() {
		return false;
	}

	/**
	 * Sets whether the authentication is authenticated.
	 * 
	 * @param isAuthenticated true if the authentication is authenticated, false otherwise
	 * @throws IllegalArgumentException if the argument is invalid
	 */
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		// Implementation not required for this class
	}

}
