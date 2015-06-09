package lu.itrust.business.expressions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lu.itrust.business.TS.exception.InvalidExpressionException;

/**
 * Represents a simple expression parser with support for variables and basic arithmetic operations.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public class StringExpressionParser implements ExpressionParser {
	/**
	 * Initializes a new expression parser for the given expression.
	 * @param expression The expression to parse.
	 */
	public StringExpressionParser(String expression) {
		this.expression = expression;
	}
	
	/** The expression to parse. */
	private final String expression;

	/** {@inheritDoc} */
	@Override
	public Collection<String> getInvolvedVariables() throws InvalidExpressionException {
		Set<String> involvedVariables = new HashSet<>();

		// Tokenize the expression and process each token
		Tokenizer source = new StringTokenizer(this.expression);
		Token token;
		do {
			token = source.read();
			
			// Hold track of all variables encountered
			if (token.getType().equals(TokenType.Variable)) {
				involvedVariables.add(token.getParameter());
			}
		}
		// Read until the very end ("apocalypse now").
		while (!token.getType().equals(TokenType.End));
		
		// Return result
		return involvedVariables;
	}

	/** {@inheritDoc} */
	@Override
	public double evaluate(Map<String, Double> variableValueMap) throws InvalidExpressionException, IllegalArgumentException {
		// Tokenize the expression and process each token
		UndoableTokenizer source = new UndoableTokenizer(new StringTokenizer(this.expression));

		// Start from the top-layer
		double value = this.evaluateSum(source, variableValueMap);
		
		// We should have reached the end of the expression
		if (!source.read().getType().equals(TokenType.End))
			throw new InvalidExpressionException("Unexpected additional tokens after the end of the expression");
		
		return value;
	}
	
	/**
	 * Evaluates a literal (number or variable) which is expected at the current position in the expression.
	 * @param source The tokenizer to read the number/variable token from.
	 * @param variableValueMap The map which assigns a value to each variable.
	 * @return Returns the value obtained by evaluating the expression.
	 * @throws InvalidExpressionException Throws an exception on syntax errors.
	 */
	private double evaluateLiteral(UndoableTokenizer source, Map<String, Double> variableValueMap) throws InvalidExpressionException {
		// Read the next token from the source
		Token token = source.read();
		
		// If it is a number, then resolving is straight-forward
		if (token.getType().equals(TokenType.Number))
			return token.getParameter();
		
		// If it is a variable, look up its value in the map
		else if (token.getType().equals(TokenType.Variable)) {
			final String variableName = token.getParameter();
			if (variableValueMap.containsKey(variableName))
				return variableValueMap.get(variableName);
			else
				throw new IllegalArgumentException("The variable '" + variableName + "' is involved, but no value has been assigned to it in the value map.");
		}
		
		// In all other cases we have a syntax error
		else {
			throw new InvalidExpressionException("Expected number or variable, got token of type '" + token.getType() + "'.");
		}
	}

	/**
	 * Checks if there is a pair of parentheses at the current position in the expression and evaluates its content.
	 * Otherwise it just continues with evaluateLiteral().
	 * @param source The tokenizer to read the number/variable token from.
	 * @param variableValueMap The map which assigns a value to each variable.
	 * @return Returns the value obtained by evaluating the expression.
	 * @throws InvalidExpressionException Throws an exception on syntax errors.
	 */
	private double evaluateParentheses(UndoableTokenizer source, Map<String, Double> variableValueMap) throws InvalidExpressionException {
		Token token;
		
		// Check if there is an opening bracket
		if ((token = source.read()).getType().equals(TokenType.LeftBracket)) {
			// Start over with the top-layer step
			double value = this.evaluateSum(source, variableValueMap);

			// Expecting a closing bracket here
			if (!(token = source.read()).getType().equals(TokenType.RightBracket))
				throw new InvalidExpressionException("Expected ')', got token of type '" + token.getType() + "'.");
			
			return value;
		}
		else {
			// The last token has been read too much; put it back
			source.putBack(token);
			// Go to the next layer
			return this.evaluateLiteral(source, variableValueMap);
		}
	}

	/**
	 * Checks if there is a product of factors at the current position in the expression and evaluates its content.
	 * Otherwise it just continues with evaluateParentheses().
	 * @param source The tokenizer to read the number/variable token from.
	 * @param variableValueMap The map which assigns a value to each variable.
	 * @return Returns the value obtained by evaluating the expression.
	 * @throws InvalidExpressionException Throws an exception on syntax errors.
	 */
	private double evaluateProduct(UndoableTokenizer source, Map<String, Double> variableValueMap) throws InvalidExpressionException {
		// Read first factor in the product(there is always at least one factor, even if there is not really a product)
		double value = this.evaluateParentheses(source, variableValueMap);
		
		// Continously read tokens until no more '*' or '/' occurs
		Token token;
		while (true) {
			token = source.read();
			
			// If token is '*', multiply by the next factor
			if (token.getType().equals(TokenType.TimesOperator)) {
				value *= this.evaluateParentheses(source, variableValueMap);
			}
			// If token is '/', divide by the next factor
			// TODO: not sure how to handle division by zero (exception? infinity?)
			else if (token.getType().equals(TokenType.DivideOperator)) {
				value /= this.evaluateParentheses(source, variableValueMap);
			}
			else {
				// The last token has been read too much; put it back
				source.putBack(token);
				// We are done
				break;
			}
		}
		
		// Return the result of the computation
		return value;
	}

	/**
	 * Checks if there is a sum of terms at the current position in the expression and evaluates its content.
	 * Otherwise it just continues with evaluateProduct().
	 * @param source The tokenizer to read the number/variable token from.
	 * @param variableValueMap The map which assigns a value to each variable.
	 * @return Returns the value obtained by evaluating the expression.
	 * @throws InvalidExpressionException Throws an exception on syntax errors.
	 */
	private double evaluateSum(UndoableTokenizer source, Map<String, Double> variableValueMap) throws InvalidExpressionException {
		// Read first term in the sum (there is always at least one term, even if there is not really a sum)
		double value = this.evaluateProduct(source, variableValueMap);
		
		// Continously read tokens until no more '+' or '-' occurs
		Token token;
		while (true) {
			token = source.read();
			
			// If token is '+', add the next term
			if (token.getType().equals(TokenType.PlusOperator)) {
				value += this.evaluateProduct(source, variableValueMap);
			}
			// If token is '-', subtract the next term
			else if (token.getType().equals(TokenType.MinusOperator)) {
				value -= this.evaluateProduct(source, variableValueMap);
			}
			else {
				// The last token has been read too much; put it back
				source.putBack(token);
				// We are done
				break;
			}
		}
		
		// Return the result of the computation
		return value;
	}
}
