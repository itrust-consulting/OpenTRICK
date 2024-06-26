/**
 * 
 */
package lu.itrust.business.ts.validator;

import java.util.Collection;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;

/**
 * This class is responsible for validating fields of the Standard class.
 * It extends the ValidatorFieldImpl class and provides implementation for the validate method.
 * The class defines constants for field names and error messages.
 * It also provides validation logic for each field based on its type.
 */
public class StandardValidator extends ValidatorFieldImpl {

	private static final String NAME = "name";
	private static final String LABEL = "label";
	private static final String VERSION = "version";
	private static final String DESCRIPTION = "description";
	private static final String TYPE = "type";
	
	private static final String ERROR_STANDARD_NAME_EMPTY_NAME_CANNOT_BE_EMPTY = "error.norm.name.empty::Display name cannot be empty";
	private static final String ERROR_STANDARD_LABEL_EMPTY_NAME_CANNOT_BE_EMPTY = "error.norm.label.empty::Name cannot be empty";
	private static final String ERROR_STANDARD_VERSION_VERSION_CANNOT_BE_EMPTY = "error.norm.version::Version cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_NAME_VALUE_IS_NOT_SUPPORTED = "error.norm.unsupported.name::Display name value is not supported";
	private static final String ERROR_UNSUPPORTED_DATA_LABEL_NAME_VALUE_IS_NOT_SUPPORTED = "error.norm.unsupported.label::Name value is not supported";
	private static final String ERROR_UNSUPPORTED_DATA_VERSION_VERSION_VALUE_SHOULD_BE_A_POSITIVE_INTEGER = "error.norm.unsupported.version::Version value should be a positive integer";
	private static final String VERSION_SHOULD_BE_A_POSITIVE_INTEGER = "error.norm.version.zero_or_negative::Version should be a positive integer";
	private static final String ERROR_UNSUPPORTED_DATA_DESCRIPTION_DESCRIPTION_VALUE_IS_NOT_SUPPORTED = "error.norm.unsupported.description::Description value is not supported";
	private static final String ERROR_UNSUPPORTED_DATA_TYPE = "error.norm.unsupported.type::Type value is not supported";
	private static final String ERROR_DATA_NOT_VALID_TYPE = "error.norm.not_valid.type::Type value is not valid";
	private static final String ERROR_STANDARD_DESCRIPTION_EMPTY_DESCRIPTION_CANNOT_BE_EMPTY = "error.norm.description.empty::Description cannot be empty";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unused")
	@Override
	public String validate(String fieldName, Object candidate) {
		if ( fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case LABEL:
			if (!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_LABEL_NAME_VALUE_IS_NOT_SUPPORTED;
			String label = (String) candidate;
			if (label == null || label.trim().isEmpty())
				return ERROR_STANDARD_LABEL_EMPTY_NAME_CANNOT_BE_EMPTY;
			break;
		case NAME:
			if (!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_NAME_VALUE_IS_NOT_SUPPORTED;
			String name = (String) candidate;
			if (name == null || name.trim().isEmpty())
				return ERROR_STANDARD_NAME_EMPTY_NAME_CANNOT_BE_EMPTY;
			break;
		case VERSION:
			if (candidate == null)
				return ERROR_STANDARD_VERSION_VERSION_CANNOT_BE_EMPTY;
			else if (!(candidate instanceof Integer))
				return ERROR_UNSUPPORTED_DATA_VERSION_VERSION_VALUE_SHOULD_BE_A_POSITIVE_INTEGER;
			int value = (int) candidate;
			if (value < 1)
				return VERSION_SHOULD_BE_A_POSITIVE_INTEGER;
			break;
		case DESCRIPTION:
			if (!(candidate instanceof String))
				return ERROR_UNSUPPORTED_DATA_DESCRIPTION_DESCRIPTION_VALUE_IS_NOT_SUPPORTED;
			String description = (String) candidate;
			if (description == null || description.trim().isEmpty())
				return ERROR_STANDARD_DESCRIPTION_EMPTY_DESCRIPTION_CANNOT_BE_EMPTY;
			break;
		case TYPE:
			if (!(candidate instanceof StandardType))
				return ERROR_UNSUPPORTED_DATA_TYPE;
			StandardType type = (StandardType) candidate;
			if (type == null)
				return ERROR_DATA_NOT_VALID_TYPE;
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
		return Standard.class;
	}

}
