/**
 * 
 */
package lu.itrust.business.ts.validator.field;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.expressions.ExpressionParser;
import lu.itrust.business.expressions.StringExpressionParser;

/**
 * The abstract class ValidatorFieldImpl implements the ValidatorField interface and provides common functionality for field validation.
 * It contains methods for checking the validity of expressions, validating objects and fields, and checking if an object contains a specific element.
 * Subclasses can extend this class to implement specific validation logic.
 */
public abstract class ValidatorFieldImpl implements ValidatorField {

	protected static boolean Contains(Object[] objects, Object element) {
		for (Object object : objects) {
			if (object == null && element == null || object != null
					&& object.equals(element))
				return true;
		}
		return false;
	}
	
	/**
	 * Wrapper for the #IsValidExpression(String expression, List<String> variables) method
	 * which only performs casting of the arguments.
	 * @param expression An arithmetic expression involving basic operations, parentheses and literals.
	 * @param variables The set of allowed variables in the expression.
	 * @return Returns true iff the expression has no syntax errors and no unknown variables.
	 */
	protected static boolean IsValidExpression(Object expression, Object[] variables) {
		List<String> variablesAsString = new ArrayList<>();
		for (int i = 0; i < variables.length; i++)
			variablesAsString.add(variables[i].toString());
		return IsValidExpression(expression.toString(), variablesAsString);
	}
	
	/**
	 * Checks the given expression for syntax errors.
	 * @param expression An arithmetic expression involving basic operations, parentheses and literals.
	 * @param variables The set of allowed variables in the expression.
	 * @return Returns true iff the expression has no syntax errors and no unknown variables.
	 */
	protected static boolean IsValidExpression(String expression, List<String> variables) {
		ExpressionParser exprParser = new StringExpressionParser(expression, StringExpressionParser.PROBABILITY);
		return exprParser.isValid(variables);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate) throws TrickException {
		if (o == null || !supports(o.getClass()))
			return null;
		return validate(fieldName, candidate);
	}

	public static boolean Contains(Object objects, Object element) {
		try {
			if (objects == null)
				return false;
			if (objects instanceof Object[])
				return Contains((Object[]) objects, element);
			else if (objects instanceof Collection<?>)
				return ((Collection<?>) objects).contains(element);
			else if (objects instanceof Map<?, ?>)
				return ((Map<?, ?>) objects).containsKey(element)
						|| ((Map<?, ?>) objects).containsValue(element);
			else if (objects instanceof String)
				return ((String) objects).contains(element.toString());
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return supported().isAssignableFrom(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object,
	 * java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> validate(Object o, Map<Object, Object> choose) throws TrickException {
		if (!supports(o.getClass()))
			return null;
		Map<String, String> errors = new LinkedHashMap<String, String>();
		for (Field field : o.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				String error = null;
				if (choose == null || !choose.containsKey(field.getName()))
					error = validate(o, field.getName(), field.get(o));
				else {
					Object chooses = choose.get(field.getName());
					if (chooses instanceof List<?>)
						error = validate(o, field.getName(), field.get(o),
								(List<Object>) chooses);
					else if (chooses instanceof Object[])
						error = validate(o, field.getName(), field.get(o),
								(Object[]) chooses);
					else
						error = validate(o, field.getName(), field.get(o));
				}
				if (error != null)
					errors.put(field.getName(), error);
			} catch (SecurityException e) {
				TrickLogManager.persist(e);
			} catch (IllegalArgumentException e) {
				TrickLogManager.persist(e);
			} catch (IllegalAccessException e) {
				TrickLogManager.persist(e);
			}
		}
		return errors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object)
	 */
	@Override
	public Map<String, String> validate(Object object) throws TrickException {
		if (!supports(object.getClass()))
			return null;
		Map<String, String> errors = new LinkedHashMap<String, String>();
		for (Field field : object.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				String error = validate(object, field.getName(),
						field.get(object));
				if (error != null)
					errors.put(field.getName(), error);
			} catch (SecurityException e) {
				TrickLogManager.persist(e);
			} catch (IllegalArgumentException e) {
				TrickLogManager.persist(e);
			} catch (IllegalAccessException e) {
				TrickLogManager.persist(e);
			}
		}
		return errors;
	}

}
