/**
 * 
 */
package lu.itrust.business.expressions;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The TokenizerToString class represents a utility class that converts a tokenizer into a string representation.
 * It provides methods to load tokens from the tokenizer, get and set the tokenizer, get and set the list of tokens,
 * and convert the tokens into a string representation.
 * @author (SMU), itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public class TokenizerToString {

	private Tokenizer tokenizer;

	private List<Token<?>> tokens = new LinkedList<>();

	public TokenizerToString(String expression) {
		this(new StringTokenizer(expression));
	}

	public TokenizerToString(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	private void loadTokens() {
		if (!tokens.isEmpty() || tokenizer == null)
			return;
		while (true) {
			Token<?> token = tokenizer.read();
			if (token.getType() == TokenType.End)
				break;
			tokens.add(token);
		}
	}

	/**
	 * This class represents a tokenizer used for tokenizing strings.
	 * It breaks down a string into individual tokens based on a set of delimiters.
	 */
	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	/**
	 * Retrieves the list of tokens.
	 *
	 * @return The list of tokens.
	 */
	public List<Token<?>> getTokens() {
		loadTokens();
		return tokens;
	}

	public void setTokens(List<Token<?>> tokens) {
		this.tokens = tokens;
	}

	@Override
	public String toString() {
		return String.join("", getTokens().stream().map(t -> toString(t)).collect(Collectors.toList()));
	}

	private String toString(Token<?> token) {
		return token.getParameter() == null ? String.valueOf(token.getType().getSymbole()) : token.getParameter().toString();
	}
}
