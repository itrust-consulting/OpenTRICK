/**
 * 
 */
package lu.itrust.business.ts.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author eomar
 *
 */
public class TrickOtpException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg
	 */
	public TrickOtpException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param t
	 */
	public TrickOtpException(String msg, Throwable t) {
		super(msg, t);
	}

}
