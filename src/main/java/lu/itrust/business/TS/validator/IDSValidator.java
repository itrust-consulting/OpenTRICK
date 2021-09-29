/**
 * 
 */
package lu.itrust.business.TS.validator;

import java.util.Collection;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.usermanagement.IDS;
import lu.itrust.business.TS.validator.field.ValidatorFieldImpl;

/**
 * @author eomar
 *
 */
public class IDSValidator extends ValidatorFieldImpl implements Validator {

	private static final String PREFIX = "prefix";
	private static final String DESCRIPTION = "description";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.validator.field.ValidatorField#validate(java.lang.
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
	 * lu.itrust.business.TS.validator.field.ValidatorField#validate(java.lang.
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
	 * lu.itrust.business.TS.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object, java.util.List)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		return super.validate(o, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.validator.field.ValidatorField#supported()
	 */
	@Override
	public Class<?> supported() {
		return IDS.class;
	}

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
