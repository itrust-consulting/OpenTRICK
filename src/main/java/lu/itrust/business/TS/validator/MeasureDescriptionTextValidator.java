/**
 * 
 */
package lu.itrust.business.TS.validator;

import java.util.List;

import lu.itrust.business.TS.data.general.Language;
import lu.itrust.business.TS.data.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.validator.field.ValidatorFieldImpl;

/**
 * @author eomar
 * 
 */
public class MeasureDescriptionTextValidator extends ValidatorFieldImpl {

	private static final String MEASURE_DESCRIPTION = "measureDescription";
	private static final String LANGUAGE = "language";
	private static final String DOMAIN = "domain";
	private static final String DESCRIPTION = "description";
	private static final String ERROR_MEASURE_DESCRIPTION_TEXT_MEASURE_DESCRIPTION_NULL_A_MEASURE_DESCRIPTION_TEXT_SHOULD_ALWAYS_BE_ATTACHED_TO_A_MEASURE_DESCRIPTION = "error.measure_description_text.measure_description.null::A measure description text should always be attached to a measure description";
	private static final String ERROR_UNSUPPORTED_DATA_MEASURE_DESCRIPTION_MEASURE_DESCRIPTION_VALUE_IS_NOT_SUPPORTED = "error.measure_description_text.unsupported.data.measure_description::Measure description value is not supported";
	private static final String ERROR_MEASURE_DESCRIPTION_TEXT_LANGUAGE_NULL_A_MEASURE_DESCRIPTION_TEXT_SHOULD_ALWAYS_HAVE_A_LANGUAGE = "error.measure_description_text.language.null::A measure description text should always have a language";
	private static final String ERROR_UNSUPPORTED_DATA_LANGUAGE_LANGUAGE_VALUE_IS_NOT_SUPPORTED = "error.measure_description_text.unsupported.data.language::Language value is not supported";
	private static final String ERROR_UNSUPPORTED_DATA_DOMAIN_DOMAIN_VALUE_IS_NOT_SUPPORTED = "error.measure_description_text.unsupported.data.domain::Domain value is not supported";
	private static final String ERROR_MEASURE_DESCRIPTION_TEXT_DOMAIN_DOMAIN_CANNOT_BE_EMPTY = "error.measure_description_text.domain::Domain cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_DESCRIPTION_DESCRIPTION_VALUE_IS_NOT_SUPPORTED = "error.measure_description_text.unsupported.data.description::Description value is not supported";
	private static final String ERROR_MEASURE_DESCRIPTION_TEXT_DESCRIPTION_DESCRIPTION_CANNOT_BE_EMPTY = "error.measure_description_text.description::Description cannot be empty";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate) {
		if (!supports(o.getClass()) || fieldName == null || fieldName.trim().isEmpty())
			return null;
		String stringCanditate = null;
		switch (fieldName) {
		case MEASURE_DESCRIPTION:
			if (candidate == null)
				return ERROR_MEASURE_DESCRIPTION_TEXT_MEASURE_DESCRIPTION_NULL_A_MEASURE_DESCRIPTION_TEXT_SHOULD_ALWAYS_BE_ATTACHED_TO_A_MEASURE_DESCRIPTION;
			if (!(candidate instanceof MeasureDescriptionText))
				return ERROR_UNSUPPORTED_DATA_MEASURE_DESCRIPTION_MEASURE_DESCRIPTION_VALUE_IS_NOT_SUPPORTED;
			break;
		case LANGUAGE:
			if (candidate == null)
				return ERROR_MEASURE_DESCRIPTION_TEXT_LANGUAGE_NULL_A_MEASURE_DESCRIPTION_TEXT_SHOULD_ALWAYS_HAVE_A_LANGUAGE;
			if (!(candidate instanceof Language))
				return ERROR_UNSUPPORTED_DATA_LANGUAGE_LANGUAGE_VALUE_IS_NOT_SUPPORTED;
			break;
		case DOMAIN:
			if (!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_DOMAIN_DOMAIN_VALUE_IS_NOT_SUPPORTED;
			stringCanditate = (String) candidate;
			if (stringCanditate == null || stringCanditate.trim().isEmpty())
				return ERROR_MEASURE_DESCRIPTION_TEXT_DOMAIN_DOMAIN_CANNOT_BE_EMPTY;
			break;
		case DESCRIPTION:
			if (!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_DESCRIPTION_DESCRIPTION_VALUE_IS_NOT_SUPPORTED;
			stringCanditate = (String) candidate;
			if (stringCanditate == null || stringCanditate.trim().isEmpty())
				return ERROR_MEASURE_DESCRIPTION_TEXT_DESCRIPTION_DESCRIPTION_CANNOT_BE_EMPTY;
			break;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object, java.lang.Object[])
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) {
		return validate(o, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object, java.util.List)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, List<Object> choose) {
		return validate(o, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.field.ValidatorField#supported()
	 */
	@Override
	public Class<?> supported() {
		return MeasureDescriptionText.class;
	}

}
