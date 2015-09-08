package lu.itrust.business.TS.controller.helper;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

public class CsrfEnabledRequestMatcher implements RequestMatcher {
	@Override
	public boolean matches(HttpServletRequest request) {
		// Disable CSRF for retrieval requests
		final String method = request.getMethod();
		if (method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("HEAD") || method.equalsIgnoreCase("TRACE") || method.equalsIgnoreCase("OPTIONS"))
			return false;

		// Disable CSRF for API
		if (request.getServletPath().startsWith("/Api/"))
			return false;

		// Enable CSRF for everything else
		return true;
	}

}
