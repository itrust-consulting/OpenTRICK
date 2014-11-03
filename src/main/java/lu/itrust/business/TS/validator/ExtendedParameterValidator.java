/**
 * 
 */
package lu.itrust.business.TS.validator;

import lu.itrust.business.TS.data.basic.Bounds;
import lu.itrust.business.TS.data.basic.ExtendedParameter;

/**
 * @author eomar
 * 
 */
public class ExtendedParameterValidator extends ParameterValidator {

	private static final String ERROR_EXTENDED_PARAMETER_BOUNDS_NULL = "error.extended_parameter.bounds.null::Bounds cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_BOUNDS = "error.extended_parameter.bounds.unsupported::Bounds value is not supported";
	private static final String ERROR_EXTENDED_PARAMETER_LEVEL_NULL = "error.extended_parameter.level.null::Level cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_LEVEL = "error.extended_parameter.level.unsupported::Level value is not supported";
	private static final String ERROR_EXTENDED_PARAMETER_LEVEL_NOT_IN_BOUNDS = "error.extended_parameter.level.not_in_bounds::Level needs to be between 0 and 10 included";
	private static final String ERROR_EXTENDED_PARAMETER_ACRONYM_NULL = "error.extended_parameter.acronym.null::Acronym cannot be empty";
	private static final String ERROR_EXTENDED_PARAMETER_VALUE_NEGATIF = "error.extended_parameter.value.negatif::Value needs to be greater or equal 0";
	private static final String ERROR_EXTENDED_PARAMETER_VALUE_NULL = "error.extended_parameter.value.null::Value should be a numeric";
	private static final String ERROR_UNSUPPORTED_DATA_ACRONYM = "error.extended_parameter.acronym.unsupported::Acronym value is not supported";
	protected static final String BOUNDS = "bounds";
	protected static final String LEVEL = "level";
	protected static final String ACRONYM = "acronym";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.validator.ParameterValidator#validate(java.lang.Object
	 * , java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate) {
		if (o == null || !supports(o.getClass()) || fieldName == null
				|| fieldName.trim().isEmpty())
			return null;
		ExtendedParameter extendedParameter = (ExtendedParameter) o;
		switch (fieldName) {
		case BOUNDS:
			if (candidate == null)
				return ERROR_EXTENDED_PARAMETER_BOUNDS_NULL;
			else if (!(candidate instanceof Bounds))
				return ERROR_UNSUPPORTED_DATA_BOUNDS;
			break;
		case LEVEL:
			if (candidate == null)
				return ERROR_EXTENDED_PARAMETER_LEVEL_NULL;
			else if (!(candidate instanceof Integer))
				return ERROR_UNSUPPORTED_DATA_LEVEL;
			int level = (int) candidate;
			if (level < 0 || level > 10)
				return ERROR_EXTENDED_PARAMETER_LEVEL_NOT_IN_BOUNDS;
			break;
		case ACRONYM:
			if (candidate == null || candidate.toString().trim().isEmpty())
				return ERROR_EXTENDED_PARAMETER_ACRONYM_NULL;
			else if (!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_ACRONYM;
			break;
		case VALUE:
			if(candidate == null || !(candidate instanceof Double))
				return ERROR_EXTENDED_PARAMETER_VALUE_NULL;
			double value = (double) candidate;
			if(value<0)
				return ERROR_EXTENDED_PARAMETER_VALUE_NEGATIF;
			break;
			
		}
		return super.validate(extendedParameter, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.ParameterValidator#supported()
	 */
	@Override
	public Class<?> supported() {
		return ExtendedParameter.class;
	}

}
