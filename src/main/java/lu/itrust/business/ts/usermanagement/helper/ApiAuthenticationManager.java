/**
 * 
 */
package lu.itrust.business.ts.usermanagement.helper;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOIDS;
import lu.itrust.business.ts.usermanagement.IUser;

/**
 * This class is responsible for authenticating API requests.
 * It implements the AuthenticationManager interface from Spring Security.
 * The authentication process involves validating the token provided in the request,
 * checking if the user associated with the token exists and is enabled,
 * and creating an authentication token with the user's details and authorities.
 */
public class ApiAuthenticationManager implements AuthenticationManager {

	@Autowired
	private DAOIDS daoIDS;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.authentication.AuthenticationManager#
	 * authenticate(org.springframework.security.core.Authentication)
	 */
	@Transactional(readOnly = true)
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!(authentication instanceof TokenRequestAuthentication))
			return authentication;
		IUser user = daoIDS.getByToken(authentication.getCredentials().toString());
		if (user == null)
			throw new BadCredentialsException("IDS cannot be found");
		if (!user.isEnable())
			throw new AccountExpiredException("IDS authorisation off");
		Collection<GrantedAuthority> authorities = new LinkedList<>();
		authorities.add(new SimpleGrantedAuthority(user.getAccess().name()));
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(), authorities);
		authenticationToken.setDetails(authentication.getDetails());
		return authenticationToken;
	}

}
