/**
 * 
 */
package lu.itrust.business.ts.validator;

import java.util.Collection;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;


/**
 * This class is responsible for validating the fields of a Language object.
 * It extends the ValidatorFieldImpl class and provides validation logic for the
 * "name", "altName", and "alpha3" fields.
 */
public class LanguageValidator extends ValidatorFieldImpl {

	private static final String ALT_NAME = "altName";
	private static final String NAME = "name";
	private static final String ALPHA3 = "alpha3";
	private static final String ERROR_LANGUAGE_ALT_NAME_EMPTY_ALT_NAME_CANNOT_BE_EMPTY = "error.language.alt_name.empty::Alternative name cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_ALT_NAME_ALT_NAME_VALUE_IS_NOT_SUPPORTED = "error.language.alt_name.unsupported::Alternative name value is not supported";
	private static final String ERROR_LANGUAGE_NAME_EMPTY_NAME_CANNOT_BE_EMPTY = "error.language.name.empty::Name cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_NAME_NAME_VALUE_IS_NOT_SUPPORTED = "error.language.name.unsupported::Name value is not supported";
	private static final String ERROR_LANGUAGE_ALPHA3_INVALID_ALPHA3_SHOULD_BE_THREE_CHARACTERS = "error.language.alpha3.invalid::Alpha 3 code should be three characters";
	private static final String ERROR_LANGUAGE_ALPHA3_EMPTY_ALPHA3_CANNOT_BE_EMPTY = "error.language.alpha_3.empty::Alpha 3 code cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_ALPHA3_ALPHA3_VALUE_IS_NOT_SUPPORTED = "error.language.alpha_3.unsupported::Alpha 3 code value is not supported";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(String fieldName, Object candidate) {
		if (fieldName == null || fieldName.trim().isEmpty())
			return null;
		String value = null;
		switch (fieldName) {
		case ALPHA3:
			if (!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_ALPHA3_ALPHA3_VALUE_IS_NOT_SUPPORTED;
			value = (String) candidate;
			if (value == null || value.trim().isEmpty())
				return ERROR_LANGUAGE_ALPHA3_EMPTY_ALPHA3_CANNOT_BE_EMPTY;
			else if (!value.matches(Constant.REGEXP_VALID_ALPHA_3))
				return ERROR_LANGUAGE_ALPHA3_INVALID_ALPHA3_SHOULD_BE_THREE_CHARACTERS;
			break;
		case NAME:
			if (!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_NAME_NAME_VALUE_IS_NOT_SUPPORTED;
			value = (String) candidate;
			if (value == null || value.trim().isEmpty())
				return ERROR_LANGUAGE_NAME_EMPTY_NAME_CANNOT_BE_EMPTY;
			break;
		case ALT_NAME:
			if (!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_ALT_NAME_ALT_NAME_VALUE_IS_NOT_SUPPORTED;
			value = (String) candidate;
			if (value == null || value.trim().isEmpty())
				return ERROR_LANGUAGE_ALT_NAME_EMPTY_ALT_NAME_CANNOT_BE_EMPTY;
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
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
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
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.field.ValidatorField#supported()
	 */
	@Override
	public Class<?> supported() {
		return Language.class;
	}

}
