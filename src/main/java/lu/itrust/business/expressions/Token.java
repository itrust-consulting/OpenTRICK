package lu.itrust.business.expressions;

/**
 * Represents a token in an expression. Tokens are the most basic semantic parts
 * of an expression, e.g. variables, operators.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public class Token<T> {
	/**
	 * Initializes a new token without any parameter.
	 * 
	 * @param type
	 *            The token type.
	 */
	public Token(TokenType type) {
		this(type, null);
	}

	/**
	 * Initializes a new token with a parameter.
	 * 
	 * @param type
	 *            The token type.
	 * @param parameter
	 *            The token parameter.
	 */
	public Token(TokenType type, T parameter) {
		this.type = type;
		this.parameter = parameter;
	}

	/** The type of token (e.g. variable, "plus" operator ...). */
	private TokenType type;

	/**
	 * The token parameter further specifies the token (e.g. for variables, this
	 * would be the variable name). May be null.
	 */
	private T parameter;

	/** Gets the token type. */
	public TokenType getType() {
		return this.type;
	}

	/** Gets the token parameter. May be null. */
	public T getParameter() {
		return this.parameter;
	}

	@SuppressWarnings("unchecked")
	public void setParameter(Object parameter) {
		if (this.parameter == null || parameter == null)
			return;
		else if (this.parameter.getClass().isAssignableFrom(parameter.getClass()))
			this.parameter = (T) parameter;
	}

}
