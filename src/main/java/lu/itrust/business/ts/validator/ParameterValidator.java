/**
 * 
 */
package lu.itrust.business.ts.validator;

import java.util.Collection;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.parameter.impl.SimpleParameter;
import lu.itrust.business.ts.model.parameter.type.impl.ParameterType;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;


/**
 * This class is responsible for validating the parameters of a SimpleParameter object.
 * It extends the ValidatorFieldImpl class.
 */
public class ParameterValidator extends ValidatorFieldImpl {

	protected static final String ERROR_EXTENDED_PARAMETER_VALUE_NULL = "error.extended_parameter.value.null::Value should be a numeric";
	protected static final String VALUE = "value";
	protected static final String TYPE = "type";
	protected static final String DESCRIPTION = "description";
	private static final String ERROR_PARAMETER_TYPE_NULL_TYPE_CANNOT_BE_EMPTY = "error.extended_parameter.type.null::Type cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_TYPE_TYPE_VALUE_IS_NOT_SUPPORTED = "error.extended_parameter.unsupported.type::Type value is not supported";
	private static final String ERROR_UNSUPPORTED_DATA_DESCRIPTION_DESCRIPTION_VALUE_IS_NOT_SUPPORTED = "error.extended_parameter.unsupported.description::Description value is not supported";
	private static final String ERROR_PARAMETER_DESCRIPTION_NULL_OR_EMPTY = "error.extended_parameter.description.null::Description cannot be empty";


	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(String fieldName, Object candidate) {
		switch (fieldName) {
		case DESCRIPTION:
			if (!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_DESCRIPTION_DESCRIPTION_VALUE_IS_NOT_SUPPORTED;

			String description = (String) candidate;
			if (description == null || description.trim().isEmpty())
				return ERROR_PARAMETER_DESCRIPTION_NULL_OR_EMPTY;
			break;
		case TYPE:
			if (candidate == null)
				return ERROR_PARAMETER_TYPE_NULL_TYPE_CANNOT_BE_EMPTY;
			else if (!(candidate instanceof ParameterType))
				return ERROR_UNSUPPORTED_DATA_TYPE_TYPE_VALUE_IS_NOT_SUPPORTED;
			break;
		case VALUE:
			if(candidate == null || !(candidate instanceof Double))
				return ERROR_EXTENDED_PARAMETER_VALUE_NULL;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#supported()
	 */
	@Override
	public Class<?> supported() {
		return SimpleParameter.class;
	}

	/**
	 * Validates the given object and returns a string representation of the validation result.
	 *
	 * @param o the object to be validated
	 * @param fieldName the name of the field being validated
	 * @param candidate the candidate value to be validated
	 * @param choose an array of objects to choose from during validation
	 * @return a string representation of the validation result
	 * @throws TrickException if an error occurs during validation
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate,
			Object[] choose) throws TrickException {
		return validate(choose, fieldName, candidate);
	}

	/**
	 * Validates the given object against the specified field name and candidate value.
	 * 
	 * @param o the object to be validated
	 * @param fieldName the name of the field being validated
	 * @param candidate the candidate value to be validated against
	 * @param choose a collection of objects to choose from during validation
	 * @return a string representing the validation result
	 * @throws TrickException if an error occurs during validation
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate,
			Collection<Object> choose) throws TrickException {
		return validate(choose, fieldName, candidate);
	}

}
