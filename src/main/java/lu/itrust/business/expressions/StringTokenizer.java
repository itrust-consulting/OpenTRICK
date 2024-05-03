package lu.itrust.business.expressions;

import java.util.regex.Pattern;

/**
 * Simple implementation of the Tokenizer interface, which reads tokens from a
 * given string.
 * 
 * @author (SMU), itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public class StringTokenizer implements Tokenizer {
	public StringTokenizer(String expressionString) {
		this.expressionString = expressionString;
	}

	private static final Pattern regexSpace = Pattern.compile(" ");

	/**
	 * The string containing the expression to parse. Note that according to
	 * http://stackoverflow.com/questions/8894258/fastest-way-to-iterate-over-
	 * all-the-chars-in-a-string the fastest method to iterate over small
	 * strings (up to 256 characters) is by using .charAt().
	 */
	private final String expressionString;
	/**
	 * Points to the next character in the string which has not been processed
	 * yet.
	 */
	private int pointer = 0;

	@Override
	public Token<?> read() throws InvalidExpressionException {
		// Read characters unless we find a valid token ('return' inside loop
		// body) or the string ends
		while (this.pointer < this.expressionString.length()) {
			// Read next character
			char nextChar = this.expressionString.charAt(this.pointer);

			// Ignore white spaces
			if (Character.isWhitespace(nextChar)) {
				this.pointer++;
				continue;
			}

			// Handle variables
			if (Character.isLetter(nextChar) || nextChar == '_') {
				return this.readVariable();
			}

			// Handle numbers
			if (Character.isDigit(nextChar) || nextChar == '.' || nextChar == ',') {
				return this.readNumber();
			}

			// Handle operators and the like
			switch (nextChar) {
			case '+':
				this.pointer++;
				return new Token<>(TokenType.PlusOperator);
			case '-':
				this.pointer++;
				return new Token<>(TokenType.MinusOperator);
			case '*':
				this.pointer++;
				return new Token<>(TokenType.TimesOperator);
			case '/':
				this.pointer++;
				return new Token<>(TokenType.DivideOperator);
			case '(':
				this.pointer++;
				return new Token<>(TokenType.LeftBracket);
			case ')':
				this.pointer++;
				return new Token<>(TokenType.RightBracket);
			case ';':
				this.pointer++;
				return new Token<>(TokenType.Comma);
			}

			// Everything that remains does not fit into any expected category
			// and is thus invalid
			throw new InvalidExpressionException("Unexpected character at position " + this.pointer + ": " + nextChar);
		}

		// When we arrive here, we reached the end of the expression
		return new Token<>(TokenType.End);
	}

	/**
	 * Reads a floating point number from the expression at the current
	 * position. Note that scientific notation (e.g. 1E-4) is not supported. The
	 * fractional part must be introduced by a period ('.') only. PRE-CONDITION:
	 * The pointer must point to the first character of a number.
	 * 
	 * @return Returns a token describing the parsed number.
	 * @throws InvalidExpressionException
	 */
	private Token<Double> readNumber() throws InvalidExpressionException {
		/** Points to the beginning of the number in the expression string. */
		int pointerBeginning = this.pointer;
		/**
		 * True iff the period (separating the fractional part) has been read.
		 */
		boolean periodRead = false;

		// Continuously read characters until end of expression has been
		// reached,
		// or the next character is not part of the number anymore.
		for (; this.pointer < this.expressionString.length(); this.pointer++) {
			// Get associated character
			char nextChar = this.expressionString.charAt(this.pointer);

			// Handle periods
			if (nextChar == '.' || nextChar == ',') {
				if (periodRead)
					throw new InvalidExpressionException("Two periods in number, at position " + pointerBeginning);
				else
					periodRead = true;
			}
			// Ignore spaces in numbers
			else if (nextChar == ' ')
				continue;
			// Everything else should be a digit: if we read something else,
			// our token ends here and we can return it.
			else if (!Character.isDigit(nextChar))
				break;
		}

		// Handle the case where the number consists of a mere period
		if (this.pointer - pointerBeginning <= 1 && periodRead)
			throw new InvalidExpressionException("Number cannot consist of a period only, at position " + pointerBeginning);
		else
			return new Token<>(TokenType.Number, Double.parseDouble(regexSpace.matcher(this.expressionString.substring(pointerBeginning, this.pointer).replace(',', '.')).replaceAll("")));
	}

	/**
	 * Reads a variable from the expression at the current position.
	 * PRE-CONDITION: The pointer must point to the first character of a
	 * variable.
	 * 
	 * @return Returns a token describing the parsed variable.
	 */
	private Token<String> readVariable() {
		/** Points to the beginning of the number in the expression string. */
		int pointerBeginning = this.pointer;

		// Continuously read characters until end of expression has been
		// reached,
		// or the next character is not part of the number anymore.
		for (; this.pointer < this.expressionString.length(); this.pointer++) {
			// Get associated character
			char nextChar = this.expressionString.charAt(this.pointer);

			// NB: Although variables are not allowed to begin with a digit, we
			// know that this is not
			// the case because of the PRE-CONDITION of this method. In other
			// words, we do not have
			// to treat the first character of a variable apart.

			// Valid characters are digits, letters and underscores;
			// if we read something else, our token ends here and we can return
			// it.
			if (!Character.isDigit(nextChar) && !Character.isLetter(nextChar) && nextChar != '_')
				break;
		}

		// We know the variable is NOT empty because of the PRE-CONDITION of
		// this method
		return new Token<>(TokenType.Variable, this.expressionString.substring(pointerBeginning, this.pointer));
	}

}
