/**
 * 
 */
package lu.itrust.business.ts.usermanagement.helper;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;


/**
 * This class is responsible for handling authentication errors that occur during API requests.
 * It implements the AuthenticationEntryPoint interface from Spring Security.
 */
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/* (non-Javadoc)
	 * @see org.springframework.security.web.AuthenticationEntryPoint#commence(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		response.sendError( HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage() );
	}
}
