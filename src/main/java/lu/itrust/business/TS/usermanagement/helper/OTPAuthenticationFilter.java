/**
 * 
 */
package lu.itrust.business.TS.usermanagement.helper;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import lu.itrust.business.TS.constants.Constant;

/**
 * @author eomar
 *
 */
public class OTPAuthenticationFilter extends GenericFilterBean {

	private RequestMatcher requestMatcher;

	/**
	 * 
	 */
	public OTPAuthenticationFilter(String url) {
		this(new AntPathRequestMatcher(url));
	}

	/**
	 * 
	 */
	public OTPAuthenticationFilter(RequestMatcher requestMatcher) {
		setRequestMatcher(requestMatcher);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (!requestMatcher.matches(request)) {
			chain.doFilter(request, response);
			return;
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals(Constant.ROLE_OTP_NAME))) {
			chain.doFilter(request, response);
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Request is to process authentication");
		}
		response.sendRedirect("/OTP/Authorise");
	}

	/**
	 * @return the requestMatcher
	 */
	public RequestMatcher getRequestMatcher() {
		return requestMatcher;
	}

	/**
	 * @param requestMatcher
	 *            the requestMatcher to set
	 */
	public void setRequestMatcher(RequestMatcher requestMatcher) {
		this.requestMatcher = requestMatcher;
	}

}
