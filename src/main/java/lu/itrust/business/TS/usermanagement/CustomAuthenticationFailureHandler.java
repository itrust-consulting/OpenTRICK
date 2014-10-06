package lu.itrust.business.TS.usermanagement;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * CustomAuthenticationFailureHandler.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Oct 6, 2014
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		//exception.printStackTrace();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String stringdate = dateFormat.format(date);
		
	
		if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
			System.err.println("ERROR: ("+ stringdate +"): Bad credentials for user '"+request.getParameter("j_username") +"'!");
			request.getSession().setAttribute("LOGIN_ERROR", "error.bad.credential");
		} 
		
		if (exception.getClass().isAssignableFrom(DisabledException.class)) {
			System.err.println("ERROR: ("+ stringdate +"): User '"+ request.getParameter("j_username") +"' is disabled!");
		}
		
		if(exception.getClass().isAssignableFrom(InternalAuthenticationServiceException.class)) {
			
			System.err.println("ERROR: ("+ stringdate +"): Database Connection Failed!");
			request.getSession().setAttribute("LOGIN_ERROR", "error.database.connection_failed");
		}
		
		super.onAuthenticationFailure(request, response, exception);
	}
}