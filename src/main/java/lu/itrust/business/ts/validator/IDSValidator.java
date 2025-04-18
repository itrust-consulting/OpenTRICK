/**
 * 
 */
package lu.itrust.business.ts.validator;

import java.util.Collection;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.usermanagement.IDS;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;

/**
 * This class represents a validator for IDS (Intrusion Detection System) objects.
 * It implements the Validator interface and extends the ValidatorFieldImpl class.
 * IDSValidator is responsible for validating the fields of an IDS object.
 */
public class IDSValidator extends ValidatorFieldImpl implements Validator {

	private static final String PREFIX = "prefix";
	private static final String DESCRIPTION = "description";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.validator.field.ValidatorField#validate(java.lang.
	 * String, java.lang.Object)
	 */
	@Override
	public String validate(String fieldName, Object candidate) throws TrickException {
		switch (fieldName) {
		case DESCRIPTION:
			if (candidate == null || !StringUtils.hasText(candidate.toString()) || !(candidate instanceof String))
				return "error.ids.description.empty::Description cannot be empty";
			if (candidate.toString().length() > 255)
				return "error.description.too.long:255:Description length can not be greater than 255";
			break;
		case PREFIX:
			if (candidate == null || !StringUtils.hasText(candidate.toString()) || !(candidate instanceof String))
				return "error.ids.prefix.empty::Name cannot be empty";
			if (candidate.toString().length() > 32)
				return "error.ids.prefix.too.long:32:Name length can not be greater than 32";
			break;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object, java.lang.Object[])
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return super.validate(o, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object, java.util.List)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		return super.validate(o, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.validator.field.ValidatorField#supported()
	 */
	@Override
	public Class<?> supported() {
		return IDS.class;
	}

	/**
	 * Validates the given target object and populates any validation errors in the provided Errors object.
	 *
	 * @param target  the object to be validated
	 * @param errors  the Errors object to store any validation errors
	 */
	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, PREFIX, "error.ids.prefix.empty", "Name cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, DESCRIPTION, "error.ids.description", "Description cannot be empty");
		IDS ids = (IDS) target;
		if (!errors.hasFieldErrors(DESCRIPTION) && ids.getDescription().length() > 255)
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, DESCRIPTION, "error.description.too.long", new Object[] { 255 }, "Description length can not be greater than 255");

		if (!errors.hasFieldErrors(PREFIX) && ids.getDescription().length() > 32)
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, PREFIX, "error.prefix.too.long", new Object[] { 32 }, "Name length can not be greater than 32");

	}

}
