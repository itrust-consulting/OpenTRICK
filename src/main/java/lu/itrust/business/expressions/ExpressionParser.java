package lu.itrust.business.expressions;

import java.util.Collection;
import java.util.Map;

import lu.itrust.business.TS.model.parameter.helper.ValueFactory;

/**
 * Interface for a parser of basic arithmetical expressions containing
 * variables.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 9, 2015
 */
public interface ExpressionParser {
	/**
	 * Parses the expression and extracts all involved variables.
	 * 
	 * @return Returns a collection of all variables found.
	 * @throws InvalidExpressionException
	 *             Throws an exception if the expression is invalid.
	 */
	public Collection<String> getInvolvedVariables() throws InvalidExpressionException;

	/**
	 * Evaluates the expression by plugging in the values of the variables and
	 * computing the result.
	 * 
	 * @param variableValueMap
	 *            A map which assigns a value to each variable.
	 * @return Returns the result from the computation.
	 * @throws InvalidExpressionException
	 *             Throws an exception if the expression is invalid.
	 * @throws IllegalArgumentException
	 *             Throws an exception if a variable has no assigned value.
	 */
	default Double evaluate(ValueFactory factory) throws InvalidExpressionException, IllegalArgumentException {
		return evaluate(factory, 0d);
	}
	
	default Double evaluate(Map<String, Double> variableValueMap) throws InvalidExpressionException, IllegalArgumentException{
		return evaluate(variableValueMap, 0d);
	}
	
	public Double evaluate(ValueFactory factory, Double defaultValue) throws InvalidExpressionException, IllegalArgumentException;
	
	public Double evaluate(Map<String, Double> variableValueMap, Double defaultValue) throws InvalidExpressionException, IllegalArgumentException;

	/**
	 * Checks the given expression for syntax errors.
	 * 
	 * @param variables
	 *            The set of allowed variables in the expression.
	 * @return Returns true iff the expression has no syntax errors and no
	 *         unknown variables.
	 */
	
	public boolean isValid(Collection<String> names);
	
	public boolean isValid(ValueFactory factory);
}
