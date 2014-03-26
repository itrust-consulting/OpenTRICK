/**
 * 
 */
package lu.itrust.business.validator;

import java.util.List;

import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.validator.field.ValidatorFieldImpl;

/**
 * @author eomar
 *
 */
public class MeasureDescriptionValidator extends ValidatorFieldImpl {

	private static final String NORM = "norm";
	private static final String LEVEL2 = "level";
	private static final String REFERENCE2 = "reference";
	private static final String COMPUTABLE2 = "computable";
	private static final String ERROR_MEASURE_DESCRIPTION_NORM_NULL_A_MEASURE_DESCRIPTION_SHOULD_ALWAYS_BE_ATTACHED_TO_A_NORM = "error.measure_description.norm.null::A measure description should always be attached to a norm";
	private static final String ERROR_UNSUPPORTED_DATA_NORM_NORM_VALUE_IS_NOT_SUPPORTED = "error.unsupported.data.norm::Norm value is not supported";
	private static final String ERROR_MEASURE_DESCRIPTION_MEASURE_DESCRIPTION_TEXTS_NULL_A_MEASURE_DESCRIPTION_SHOULD_ALWAYS_HAVE_DESCRIPTION_TEXT = "error.measure_description.measure_description_texts.null::A measure description should always have description text";
	private static final String ERROR_UNSUPPORTED_DATA_LEVEL_LEVEL_VALUE_IS_NOT_SUPPORTED = "error.unsupported.data.level::Level value is not supported";
	private static final String ERROR_MEASURE_DESCRIPTION_LEVEL_LEVEL_SHOULD_BE_A_INTEGER_GREATER_THAN_OR_EQUAL_TO_1 = "error.measure_description.level::Level should be a integer greater than or equal to 1";
	private static final String ERROR_UNSUPPORTED_DATA_REFERENCE_REFERENCE_VALUE_IS_NOT_SUPPORTED = "error.unsupported.data.reference::Reference value is not supported";
	private static final String ERROR_MEASURE_DESCRIPTION_REFERENCE_REFERENCE_CANNOT_BE_EMPTY = "error.measure_description.reference::Reference cannot be empty";
	private static final String ERROR_MEASURE_DESCRIPTION_COMPUTABLE_CANNOT_BE_EMPTY = "error.measure_description.computable::Computable cannot be empty";
	private static final String ERROR_MEASURE_DESCRIPTION_COMPUTABLE_NOT_BOOLEAN = "error.measure_description.computable::Computable has to be boolean";
	

	/* (non-Javadoc)
	 * @see lu.itrust.business.validator.field.ValidatorField#validate(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate) {
		if (!supports(o.getClass()) || fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case NORM:
			if(candidate == null)
				return ERROR_MEASURE_DESCRIPTION_NORM_NULL_A_MEASURE_DESCRIPTION_SHOULD_ALWAYS_BE_ATTACHED_TO_A_NORM;
			else if(!(candidate instanceof Norm))
				return ERROR_UNSUPPORTED_DATA_NORM_NORM_VALUE_IS_NOT_SUPPORTED;
			break;
		case "measureDescriptionTexts":
			if(candidate == null)
				return ERROR_MEASURE_DESCRIPTION_MEASURE_DESCRIPTION_TEXTS_NULL_A_MEASURE_DESCRIPTION_SHOULD_ALWAYS_HAVE_DESCRIPTION_TEXT;
			else if(!(candidate instanceof List))
				return ERROR_UNSUPPORTED_DATA_NORM_NORM_VALUE_IS_NOT_SUPPORTED;
			if(((List<?>) candidate).isEmpty())
				return ERROR_MEASURE_DESCRIPTION_MEASURE_DESCRIPTION_TEXTS_NULL_A_MEASURE_DESCRIPTION_SHOULD_ALWAYS_HAVE_DESCRIPTION_TEXT;
			break;
		case LEVEL2:
			if(!(candidate instanceof Integer))
				return ERROR_UNSUPPORTED_DATA_LEVEL_LEVEL_VALUE_IS_NOT_SUPPORTED;
			Integer level = (Integer) candidate;
			if(level<1)
				return ERROR_MEASURE_DESCRIPTION_LEVEL_LEVEL_SHOULD_BE_A_INTEGER_GREATER_THAN_OR_EQUAL_TO_1;
			break;
		case REFERENCE2:
			if(!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_REFERENCE_REFERENCE_VALUE_IS_NOT_SUPPORTED;
			String reference = (String) candidate;
			if(reference==null || reference.trim().isEmpty())
				return ERROR_MEASURE_DESCRIPTION_REFERENCE_REFERENCE_CANNOT_BE_EMPTY;
			break;
		case COMPUTABLE2:
			if(candidate==null)
				return ERROR_MEASURE_DESCRIPTION_COMPUTABLE_CANNOT_BE_EMPTY;
			if(!(candidate instanceof Boolean))
				return ERROR_MEASURE_DESCRIPTION_COMPUTABLE_NOT_BOOLEAN;
			break;
		default:
			break;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.validator.field.ValidatorField#validate(java.lang.Object, java.lang.String, java.lang.Object, java.lang.Object[])
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) {
		return validate(o, fieldName, candidate);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.validator.field.ValidatorField#validate(java.lang.Object, java.lang.String, java.lang.Object, java.util.List)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, List<Object> choose) {
		return validate(o, fieldName, candidate);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.validator.field.ValidatorField#supported()
	 */
	@Override
	public Class<?> supported() {
		return MeasureDescription.class;
	}

}
