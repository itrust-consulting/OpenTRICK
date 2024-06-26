package lu.itrust.business.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.value.IValue;

/**
 * Represents a simple expression parser with support for variables and basic
 * arithmetic operations.
 * 
 * @author  itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public class StringExpressionParser implements ExpressionParser {

	public static final int IMPLEMENTATION = 2;

	public static final int PROBABILITY = 1;

	public static final int IMPACT = 0;

	/** The expression to parse. */
	private final String expression;

	private final int type;

	/**
	 * Initializes a new expression parser for the given expression.
	 * 
	 * @param expression The expression to parse.
	 */
	public StringExpressionParser(String expression, int type) {
		this.expression = expression;
		this.type = type;
	}

	/** {@inheritDoc} */
	@Override
	public Collection<String> getInvolvedVariables() throws InvalidExpressionException {
		Set<String> involvedVariables = new HashSet<>();

		// Tokenize the expression and process each token
		Tokenizer source = new StringTokenizer(this.expression);
		Token<?> token;
		do {
			token = source.read();

			// Hold track of all variables encountered
			if (token.getType().equals(TokenType.Variable)) {
				involvedVariables.add((String) token.getParameter());
			}
		}
		// Read until the very end ("apocalypse now").
		while (!token.getType().equals(TokenType.End));

		// Return result
		return involvedVariables;
	}

	/** {@inheritDoc} */
	@Override
	public Double evaluate(ValueFactory factory, Double defaultValue) throws InvalidExpressionException {
		// Tokenize the expression and process each token
		UndoableTokenizer source = new UndoableTokenizer(new StringTokenizer(this.expression));
		// Start from the top-layer
		Double value = this.evaluateSum(source, factory, defaultValue);
		// We should have reached the end of the expression
		if (!source.read().getType().equals(TokenType.End))
			throw new InvalidExpressionException("Unexpected additional tokens after the end of the expression");
		return value;
	}

	@Override
	public Double evaluate(Map<String, Double> variableValueMap, Double defaultValue) throws InvalidExpressionException, IllegalArgumentException {
		// Tokenize the expression and process each token
		UndoableTokenizer source = new UndoableTokenizer(new StringTokenizer(this.expression));
		// Start from the top-layer
		double value = this.evaluateSum(source, variableValueMap, defaultValue);
		// We should have reached the end of the expression
		if (!source.read().getType().equals(TokenType.End))
			throw new InvalidExpressionException("Unexpected additional tokens after the end of the expression");
		return value;
	}

	/**
	 * Evaluates a function which is expected at the current position in the
	 * expression.
	 * 
	 * @param source       The tokenizer to read the function from. Must point to
	 *                     the opening (left) bracket of the function argument list.
	 * @param values       The map which assigns a value to each variable.
	 * @param functionName The name of the function to evaluate.
	 * @param defaultValue TODO
	 * @return Returns the value obtained by evaluating the expression.
	 * @throws InvalidExpressionException Throws an exception on syntax errors.
	 */
	private Double evaluateFunction(UndoableTokenizer source, Object values, String functionName, Double defaultValue) throws InvalidExpressionException {
		if (!source.read().getType().equals(TokenType.LeftBracket))
			throw new InvalidExpressionException("Expected opening bracket '('.");

		List<Double> arguments = new ArrayList<>();

		// Handle special case where function does not have any arguments
		Token<?> nextToken = source.read();
		if (nextToken.getType().equals(TokenType.RightBracket))
			return evaluateFunction(functionName, arguments, defaultValue);
		else
			source.putBack(nextToken);

		// Read all arguments
		while (true) {
			arguments.add(evaluateSum(source, values, defaultValue));
			Token<?> token = source.read();
			if (token.getType().equals(TokenType.RightBracket))
				return evaluateFunction(functionName, arguments, defaultValue);
			else if (!token.getType().equals(TokenType.Comma))
				throw new InvalidExpressionException("Expected comma or closing bracket, got token of type '" + token.getType() + "'.");
		}
	}

	private Double evaluateFunction(String functionName, List<Double> arguments, Double defaultValue) throws InvalidExpressionException {
		functionName = functionName.toUpperCase();
		if (functionName.equals("MIN")) {
			if (arguments.size() == 0)
				throw new InvalidExpressionException("Function MIN() expects at least one argument.");
			return evaluateFunction_min(arguments);
		} else if (functionName.equals("MAX")) {
			if (arguments.size() == 0)
				throw new InvalidExpressionException("Function MAX() expects at least one argument.");
			return evaluateFunction_max(arguments);
		} else {
			throw new InvalidExpressionException("Unknown function " + functionName + "().");
		}
	}

	private Double evaluateFunction_min(List<Double> arguments) {
		return arguments.parallelStream().mapToDouble(x -> x == null ? 0d : x).min().orElse(0);
	}

	private Double evaluateFunction_max(List<Double> arguments) {
		Double min = Double.NEGATIVE_INFINITY;
		for (Double value : arguments)
			if (value > min)
				min = value;
		return min;
	}

	/**
	 * Evaluates a literal (number or variable) which is expected at the current
	 * position in the expression.
	 * 
	 * @param source       The tokenizer to read the number/variable token from.
	 * @param values       The map which assigns a value to each variable.
	 * @param defaultValue TODO
	 * @return Returns the value obtained by evaluating the expression.
	 * @throws InvalidExpressionException Throws an exception on syntax errors.
	 */
	@SuppressWarnings("unchecked")
	private Double evaluateLiteral(UndoableTokenizer source, Object values, Double defaultValue) throws InvalidExpressionException {
		// Read the next token from the source
		Token<?> token = source.read();

		// If it is a number, then resolving is straight-forward
		if (token.getType().equals(TokenType.Number))
			return (Double) token.getParameter();

		// If it is a variable, look up its value in the map.
		// If it is a function, read the arguments and evaluate it.
		else if (token.getType().equals(TokenType.Variable)) {
			// Lookup the next token to check whether we are dealing with a
			// variable or a function
			Token<?> nextToken = source.read();
			boolean isFunction = nextToken.getType().equals(TokenType.LeftBracket);
			source.putBack(nextToken);

			final String variableName = (String) token.getParameter();
			if (isFunction)
				return evaluateFunction(source, values, variableName, defaultValue);
			else if (values instanceof ValueFactory) {
				final IValue value = (this.type == IMPLEMENTATION ? null
						: (this.type == PROBABILITY ? ((ValueFactory) values).findValue(variableName, Constant.PARAMETER_TYPE_PROPABILITY_NAME, false)
								: (this.type == IMPACT ? ((ValueFactory) values).findValue(variableName, Constant.PARAMETER_TYPE_IMPACT_NAME, false) : null)));
				return value == null ? ((ValueFactory) values).findDyn(variableName) : value.getReal();
			} else if (values instanceof Map) {
				final Map<String, Double> variableValueMap = (Map<String, Double>) values;
				if (variableValueMap.containsKey(variableName))
					return variableValueMap.get(variableName);
			}
			// throw new IllegalArgumentException("The variable '" +
			// variableName + "' is involved, but no value has been assigned
			// to it in the value map.");
			return defaultValue;

		}

		// If it is + or -, we are dealing with the unary operator
		else if (token.getType().equals(TokenType.PlusOperator)) {
			return this.evaluateParentheses(source, values, defaultValue);
		} else if (token.getType().equals(TokenType.MinusOperator)) {
			return -this.evaluateParentheses(source, values, defaultValue);
		}

		// In all other cases we have a syntax error
		else {
			throw new InvalidExpressionException("Expected number or variable, got token of type '" + token.getType() + "'.");
		}
	}

	/**
	 * Checks if there is a pair of parentheses at the current position in the
	 * expression and evaluates its content. Otherwise it just continues with
	 * evaluateLiteral().
	 * 
	 * @param source       The tokenizer to read the number/variable token from.
	 * @param values       The map which assigns a value to each variable.
	 * @param defaultValue TODO
	 * @return Returns the value obtained by evaluating the expression.
	 * @throws InvalidExpressionException Throws an exception on syntax errors.
	 */
	private Double evaluateParentheses(UndoableTokenizer source, Object values, Double defaultValue) throws InvalidExpressionException {
		Token<?> token;

		// Check if there is an opening bracket
		if ((token = source.read()).getType().equals(TokenType.LeftBracket)) {
			// Start over with the top-layer step
			Double value = this.evaluateSum(source, values, defaultValue);
			// Expecting a closing bracket here
			if (!(token = source.read()).getType().equals(TokenType.RightBracket))
				throw new InvalidExpressionException("Expected ')', got token of type '" + token.getType() + "'.");

			return value;
		} else {
			// The last token has been read too much; put it back
			source.putBack(token);
			// Go to the next layer
			return this.evaluateLiteral(source, values, defaultValue);
		}
	}

	/**
	 * Checks if there is a product of factors at the current position in the
	 * expression and evaluates its content. Otherwise it just continues with
	 * evaluateParentheses().
	 * 
	 * @param source       The tokenizer to read the number/variable token from.
	 * @param values       The map which assigns a value to each variable.
	 * @param defaultValue TODO
	 * @return Returns the value obtained by evaluating the expression.
	 * @throws InvalidExpressionException Throws an exception on syntax errors.
	 */
	private Double evaluateProduct(UndoableTokenizer source, Object values, Double defaultValue) throws InvalidExpressionException {
		// Read first factor in the product(there is always at least one factor,
		// even if there is not really a product)
		Double value = this.evaluateParentheses(source, values, defaultValue);

		if (value == null)
			return defaultValue;

		// Continously read tokens until no more '*' or '/' occurs
		Token<?> token;
		while (true) {
			token = source.read();

			// If token is '*', multiply by the next factor
			if (token.getType().equals(TokenType.TimesOperator)) {
				Double aux = this.evaluateParentheses(source, values, defaultValue);
				if (aux == null)
					return defaultValue;
				else
					value *= aux;
			}
			// If token is '/', divide by the next factor. Note that division by
			// zero yields Double.Infinity.
			else if (token.getType().equals(TokenType.DivideOperator)) {
				Double aux = this.evaluateParentheses(source, values, defaultValue);
				if (aux == null)
					return defaultValue;
				else
					value /= aux;
			} else {
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
	 * Checks if there is a sum of terms at the current position in the expression
	 * and evaluates its content. Otherwise it just continues with
	 * evaluateProduct().
	 * 
	 * @param source       The tokenizer to read the number/variable token from.
	 * @param defaultValue TODO
	 * @param factory      The map which assigns a value to each variable.
	 * @return Returns the value obtained by evaluating the expression.
	 * @throws InvalidExpressionException Throws an exception on syntax errors.
	 */
	private Double evaluateSum(UndoableTokenizer source, Object values, Double defaultValue) throws InvalidExpressionException {
		// Read first term in the sum (there is always at least one term, even
		// if there is not really a sum)
		Double value = this.evaluateProduct(source, values, defaultValue);

		// Continously read tokens until no more '+' or '-' occurs
		Token<?> token;
		while (true) {
			token = source.read();

			// If token is '+', add the next term
			if (token.getType().equals(TokenType.PlusOperator)) {
				Double aux = this.evaluateProduct(source, values, defaultValue);
				if (aux != null)
					value = aux + (value == null ? 0d : value);
			}
			// If token is '-', subtract the next term
			else if (token.getType().equals(TokenType.MinusOperator)) {
				Double aux = this.evaluateProduct(source, values, defaultValue);
				if (aux != null)
					value = (value == null ? 0d : value) - aux;
			} else {
				// The last token has been read too much; put it back
				source.putBack(token);
				// We are done
				break;
			}
		}

		// Return the result of the computation
		return value;
	}

	/** @{inheritDoc} */
	@Override
	public boolean isValid(ValueFactory factory) {
		// Assign an arbitrary value to each variable.
		// Since we are dealing with doubles, dividing by zero does not throw
		// exceptions.
		// Evaluate. If this succeeds, the expression is valid.
		try {
			this.evaluate(factory);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public boolean isValid(Collection<String> variables) {
		// Assign an arbitrary value to each variable.
		// Since we are dealing with doubles, dividing by zero does not throw
		// exceptions.
		Map<String, Double> values = new HashMap<>(variables.size());
		for (String variable : variables)
			values.put(variable, 0.0);
		// Evaluate. If this succeeds, the expression is valid.
		try {
			this.evaluate(values);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

}
