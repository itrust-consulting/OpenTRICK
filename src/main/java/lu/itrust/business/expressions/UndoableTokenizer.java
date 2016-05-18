package lu.itrust.business.expressions;

import java.util.Stack;

/**
 * Represents a tokenizer with support of undoing reading tokens. This allows to
 * look ahead at the next token and to put it back in case it is not the one one
 * is looking for.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public class UndoableTokenizer implements Tokenizer {
	/**
	 * Initializes a new UndoableTokenizer instance.
	 * 
	 * @param base
	 *            The base tokenizer to get tokens from.
	 */
	public UndoableTokenizer(Tokenizer base) {
		this.base = base;
	}

	private Tokenizer base;
	private Stack<Token> putBackStack = new Stack<>();

	/** {@inheritDoc} */
	@Override
	public Token read() throws InvalidExpressionException {
		// If nothing has been put back, read straight from the source
		if (putBackStack.empty())
			return this.base.read();
		// Otherwise read last element pushed onto the stack
		else
			return putBackStack.pop();
	}

	/**
	 * Puts a token back so that it can be read again (using read()).
	 * 
	 * @param token
	 *            The token to be put back.
	 */
	public void putBack(Token token) {
		this.putBackStack.push(token);
	}
}
