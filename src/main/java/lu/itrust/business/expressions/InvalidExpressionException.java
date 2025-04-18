package lu.itrust.business.expressions;

/**
 * Exception thrown if an expression could not be parsed.
 * 
 * @author  itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public class InvalidExpressionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidExpressionException() {
	}

	public InvalidExpressionException(String arg0) {
		super(arg0);
	}

	public InvalidExpressionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidExpressionException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public InvalidExpressionException(Throwable arg0) {
		super(arg0);
	}
}
