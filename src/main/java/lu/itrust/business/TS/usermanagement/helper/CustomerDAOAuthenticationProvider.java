/**
 * 
 */
package lu.itrust.business.TS.usermanagement.helper;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author eomar
 *
 */
public class CustomerDAOAuthenticationProvider extends DaoAuthenticationProvider {

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if (authentication.getCredentials() == null || !userDetails.getPassword().startsWith("{SHA-256}"))
			super.additionalAuthenticationChecks(userDetails, authentication);
		else {
			String presentedPassword = authentication.getCredentials().toString() + "{" + authentication.getName() + "}";
			if (!getPasswordEncoder().matches(presentedPassword, userDetails.getPassword())) {
				logger.debug("Authentication failed: password does not match stored value");
				throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
			}
		}
	}
}
