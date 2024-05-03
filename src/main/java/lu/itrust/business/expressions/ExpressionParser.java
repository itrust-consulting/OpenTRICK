package lu.itrust.business.expressions;

import java.util.Collection;
import java.util.Map;

import lu.itrust.business.ts.model.parameter.helper.ValueFactory;

/**
 * Interface for a parser of basic arithmetical expressions containing
 * variables.
 * The ExpressionParser interface represents a parser for mathematical expressions.
 * It provides methods to parse expressions, extract variables, evaluate expressions,
 * and check for syntax errors.
 * @author (SMU), itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public interface ExpressionParser {
	/**
	 * Parses the expression and extracts all involved variables.
	 * 
	 * @return Returns a collection of all variables found.
	 * @throws InvalidExpressionException Throws an exception if the expression is invalid.
	 */
	public Collection<String> getInvolvedVariables() throws InvalidExpressionException;

	/**
	 * Evaluates the expression by plugging in the values of the variables and computing the result.
	 * 
	 * @param variableValueMap A map which assigns a value to each variable.
	 * @return Returns the result from the computation.
	 * @throws InvalidExpressionException Throws an exception if the expression is invalid.
	 * @throws IllegalArgumentException Throws an exception if a variable has no assigned value.
	 */
	default Double evaluate(ValueFactory factory) throws InvalidExpressionException, IllegalArgumentException {
		return evaluate(factory, 0d);
	}
	
	/**
	 * Evaluates the expression by plugging in the values of the variables and computing the result.
	 * 
	 * @param variableValueMap A map which assigns a value to each variable.
	 * @return Returns the result from the computation.
	 * @throws InvalidExpressionException Throws an exception if the expression is invalid.
	 * @throws IllegalArgumentException Throws an exception if a variable has no assigned value.
	 */
	default Double evaluate(Map<String, Double> variableValueMap) throws InvalidExpressionException, IllegalArgumentException{
		return evaluate(variableValueMap, 0d);
	}
	
	/**
	 * Evaluates the expression by plugging in the values of the variables and computing the result.
	 * 
	 * @param factory The value factory used to create values for variables.
	 * @param defaultValue The default value to use for variables without assigned values.
	 * @return Returns the result from the computation.
	 * @throws InvalidExpressionException Throws an exception if the expression is invalid.
	 * @throws IllegalArgumentException Throws an exception if a variable has no assigned value.
	 */
	public Double evaluate(ValueFactory factory, Double defaultValue) throws InvalidExpressionException, IllegalArgumentException;
	
	/**
	 * Evaluates the expression by plugging in the values of the variables and computing the result.
	 * 
	 * @param variableValueMap A map which assigns a value to each variable.
	 * @param defaultValue The default value to use for variables without assigned values.
	 * @return Returns the result from the computation.
	 * @throws InvalidExpressionException Throws an exception if the expression is invalid.
	 * @throws IllegalArgumentException Throws an exception if a variable has no assigned value.
	 */
	public Double evaluate(Map<String, Double> variableValueMap, Double defaultValue) throws InvalidExpressionException, IllegalArgumentException;

	/**
	 * Checks the given expression for syntax errors.
	 * 
	 * @param names The set of allowed variables in the expression.
	 * @return Returns true if the expression has no syntax errors and no unknown variables.
	 */
	public boolean isValid(Collection<String> names);
	
	/**
	 * Checks the given expression for syntax errors.
	 * 
	 * @param factory The value factory used to create values for variables.
	 * @return Returns true if the expression has no syntax errors and no unknown variables.
	 */
	public boolean isValid(ValueFactory factory);
}
