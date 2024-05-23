/**
 * 
 */
package lu.itrust.business.ts.validator;

import lu.itrust.business.ts.model.parameter.IBoundedParameter;
import lu.itrust.business.ts.model.parameter.helper.Bounds;


/**
 * This class is a validator for bounded parameters. It extends the {@link ParameterValidator} class.
 * It provides validation logic for different fields such as bounds, level, acronym, and value.
 * 
 * The class supports the following fields:
 * - {@code bounds}: Represents the bounds of the parameter. It should be an instance of the {@link Bounds} class.
 * - {@code level}: Represents the level of the parameter. It should be an integer between 0 and 10 (inclusive).
 * - {@code acronym}: Represents the acronym of the parameter. It cannot be edited.
 * - {@code value}: Represents the value of the parameter. It should be a numeric value greater than or equal to 0.
 * 
 * The class overrides the {@link ParameterValidator#validate(String, Object)} method to provide custom validation logic
 * for each field. It also overrides the {@link ParameterValidator#supported()} method to specify the supported parameter type.
 * 
 * Example usage:
 * 
 * BounedParameterValidator validator = new BounedParameterValidator();
 * String fieldName = "bounds";
 * Bounds bounds = new Bounds(0, 100);
 * String validationError = validator.validate(fieldName, bounds);
 * if (validationError != null) {
 *     // Handle validation error
 * }
 * 
 * @see ParameterValidator
 * @see Bounds
 * @see IBoundedParameter
 */
public class BounedParameterValidator extends ParameterValidator {

	private static final String ERROR_EXTENDED_PARAMETER_BOUNDS_NULL = "error.extended_parameter.bounds.null::Bounds cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_BOUNDS = "error.extended_parameter.bounds.unsupported::Bounds value is not supported";
	private static final String ERROR_EXTENDED_PARAMETER_LEVEL_NULL = "error.extended_parameter.level.null::Level cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_LEVEL = "error.extended_parameter.level.unsupported::Level value is not supported";
	private static final String ERROR_EXTENDED_PARAMETER_LEVEL_NOT_IN_BOUNDS = "error.extended_parameter.level.not_in_bounds::Level needs to be between 0 and 10 included";
	//private static final String ERROR_EXTENDED_PARAMETER_ACRONYM_NULL = "error.extended_parameter.acronym.null::Acronym cannot be empty";
	private static final String ERROR_EXTENDED_PARAMETER_VALUE_NEGATIF = "error.extended_parameter.value.negatif::Value needs to be greater or equal 0";
	private static final String ERROR_EXTENDED_PARAMETER_VALUE_NULL = "error.extended_parameter.value.null::Value should be a numeric";
	//private static final String ERROR_UNSUPPORTED_DATA_ACRONYM = "error.extended_parameter.acronym.unsupported::Acronym value is not supported";
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
	public String validate(String fieldName, Object candidate) {
		if (fieldName == null || fieldName.trim().isEmpty())
			return null;
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
			return "error.extended_parameter.acronym.not_editable::Acronym cannot be edited";
		case VALUE:
			if (candidate == null || !(candidate instanceof Double))
				return ERROR_EXTENDED_PARAMETER_VALUE_NULL;
			double value = (double) candidate;
			if (value < 0)
				return ERROR_EXTENDED_PARAMETER_VALUE_NEGATIF;
			break;

		}
		return super.validate(fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.ParameterValidator#supported()
	 */
	@Override
	public Class<?> supported() {
		return IBoundedParameter.class;
	}

}
