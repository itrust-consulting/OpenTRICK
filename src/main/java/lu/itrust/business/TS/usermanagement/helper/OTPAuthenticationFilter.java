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
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import lu.itrust.business.TS.constants.Constant;

/**
 * @author eomar
 *
 */
public class OTPAuthenticationFilter extends GenericFilterBean {

	

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	private String redirectURL = "/OTP/Authorise";

	private RequestMatcher requestMatcher;

	/**
	 * 
	 */
	public OTPAuthenticationFilter(RequestMatcher requestMatcher) {
		setRequestMatcher(requestMatcher);
	}

	/**
	 * 
	 */
	public OTPAuthenticationFilter(String url, String redirectURL) {
		this(new AntPathRequestMatcher(url));
		setRedirectURL(redirectURL);
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
		
		if (logger.isDebugEnabled()) 
			logger.debug("Request is to process otp authentication");
	
		Authentication authentication = (Authentication) request.getSession().getAttribute(Constant.OTP_PRE_AUTHENTICATION);
		if(authentication == null)
			return;
		request.getSession().removeAttribute(Constant.OTP_PRE_AUTHENTICATION);
		SecurityContextHolder.clearContext();
		SecurityContextHolder.getContext().setAuthentication(authentication);
		if (StringUtils.hasText(redirectURL))
			redirectStrategy.sendRedirect(request, response, redirectURL);
	}

	/**
	 * @return the redirectStrategy
	 */
	public RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	/**
	 * @return the redirectURL
	 */
	public String getRedirectURL() {
		return redirectURL;
	}

	/**
	 * @return the requestMatcher
	 */
	public RequestMatcher getRequestMatcher() {
		return requestMatcher;
	}

	/**
	 * @param redirectStrategy the redirectStrategy to set
	 */
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	/**
	 * @param redirectURL
	 *            the redirectURL to set
	 */
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	/**
	 * @param requestMatcher
	 *            the requestMatcher to set
	 */
	public void setRequestMatcher(RequestMatcher requestMatcher) {
		this.requestMatcher = requestMatcher;
	}

}
