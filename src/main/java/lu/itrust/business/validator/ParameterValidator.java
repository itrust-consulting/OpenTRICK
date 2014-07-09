/**
 * 
 */
package lu.itrust.business.validator;

import java.util.List;

import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.validator.field.ValidatorFieldImpl;

/**
 * @author eomar
 * 
 */
public class ParameterValidator extends ValidatorFieldImpl {

	protected static final String ERROR_EXTENDED_PARAMETER_VALUE_NULL = "error.extendedParameter.value.null::Value should be a numeric";
	protected static final String VALUE = "value";
	protected static final String TYPE = "type";
	protected static final String DESCRIPTION = "description";
	private static final String ERROR_PARAMETER_TYPE_NULL_TYPE_CANNOT_BE_EMPTY = "error.parameter.type.null::Type cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_TYPE_TYPE_VALUE_IS_NOT_SUPPORTED = "error.unsupported.data:type:Type value is not supported";
	private static final String ERROR_UNSUPPORTED_DATA_DESCRIPTION_DESCRIPTION_VALUE_IS_NOT_SUPPORTED = "error.unsupported.data:description:Description value is not supported";
	private static final String ERROR_PARAMETER_DESCRIPTION_NULL_OR_EMPTY = "error.parameter.description.null::Description cannot be empty";


	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate) {
		if (!supports(o.getClass()))
			return null;
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
		return Parameter.class;
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate,
			Object[] choose) {
		return validate(choose, fieldName, candidate);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate,
			List<Object> choose) {
		return validate(choose, fieldName, candidate);
	}

}
