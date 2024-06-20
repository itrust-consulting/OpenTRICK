/**
 * 
 */
package lu.itrust.business.ts.validator;

import java.util.Collection;
import java.util.Date;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.history.History;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;

/**
 * The HistoryValidator class is responsible for validating the fields of a History object.
 * It implements the Validator interface and extends the ValidatorFieldImpl class.
 */
public class HistoryValidator extends ValidatorFieldImpl implements Validator {

	/**
	 * Validates the fields of a History object.
	 * 
	 * @param arg0 The object to be validated.
	 * @param arg1 The Errors object to store any validation errors.
	 */
	@Override
	public void validate(Object arg0, Errors arg1) {

		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "author", "error.history.author.empty", "Author cannot be empty");

		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "version", "error.history.version.empty", "Version cannot be empty");

		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "comment", "error.history.comment.empty", "Comment cannot be empty");

		History history = (History) arg0;
		if (history.getAuthor() == null)
			arg1.rejectValue("author", "error.history.author.null", "Author is required");

		if (history.getVersion() == null)
			arg1.rejectValue("version", "error.history.version.null", "Version is required");
		else if (!arg1.hasFieldErrors("version") && !history.getVersion().matches(Constant.REGEXP_VALID_ANALYSIS_VERSION))
			arg1.rejectValue("version", "error.history.version.not_meet_regex", "History version not acceptable");

		if (history.getComment() == null)
			arg1.rejectValue("comment", "error.history.comment.null", "Comment is required");
	}

	/**
	 * Validates a specific field of a History object.
	 * 
	 * @param o         The object to be validated.
	 * @param fieldName The name of the field to be validated.
	 * @param candidate The value of the field to be validated.
	 * @return A validation error message if the field is invalid, or null if the field is valid.
	 * @throws TrickException If the validation method is not allowed for the specified field.
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate) throws TrickException {
		if (o == null || !supports(o.getClass()) || fieldName == null || fieldName.trim().isEmpty())
			return null;
		History history = (History) o;
		switch (fieldName) {
		case "date":
			if (history.getId() > 1 && history.getDate() != null && !history.getDate().equals(candidate))
				return "error.history.date.not_editable::History date cannot be edited";
			if (candidate == null || !(candidate instanceof Date))
				return "error.history.date.unsupported::Date value is not supported";
			break;
		default:
			return validate(fieldName, candidate);
		}
		return null;
	}

	
	/**
	 * Validates a specific field of a History object with a set of allowed values.
	 * 
	 * @param o         The object to be validated.
	 * @param fieldName The name of the field to be validated.
	 * @param candidate The value of the field to be validated.
	 * @param choose    The collection of allowed values for the field.
	 * @return A validation error message if the field is invalid, or null if the field is valid.
	 * @throws TrickException If the validation method is not allowed for the specified field.
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/**
	 * Validates a specific field of a History object with a collection of allowed values.
	 * 
	 * @param o         The object to be validated.
	 * @param fieldName The name of the field to be validated.
	 * @param candidate The value of the field to be validated.
	 * @param choose    The collection of allowed values for the field.
	 * @return A validation error message if the field is invalid, or null if the field is valid.
	 * @throws TrickException If the validation method is not allowed for the specified field.
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/**
	 * Returns the class supported by this validator.
	 * 
	 * @return The class supported by this validator.
	 */
	@Override
	public Class<?> supported() {
		return History.class;
	}

	/**
	 * Validates a specific field of a History object.
	 * 
	 * @param fieldName The name of the field to be validated.
	 * @param candidate The value of the field to be validated.
	 * @return A validation error message if the field is invalid, or null if the field is valid.
	 * @throws TrickException If the validation method is not allowed for the specified field.
	 */
	@Override
	public String validate(String fieldName, Object candidate) throws TrickException {
		if (fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case "date":
			throw new TrickException("error.validator.method.not_allowed", "Validator method does not allowed");
		case "author":
			if (candidate == null || !(candidate instanceof String))
				return "error.history.author.unsupported::Author value is not supported";
			String author = (String) candidate;
			if (author.trim().isEmpty())
				return "error.history.author.empty::Author value cannot be empty";
			break;
		case "version":
			if (candidate == null || !(candidate instanceof String))
				return "error.history.version.unsupported::Version value is not supported";
			if (!candidate.toString().matches(Constant.REGEXP_VALID_ANALYSIS_VERSION))
				return "error.history.version.not_meet_regex::Version not acceptable";
			break;
		case "comment":
			if (candidate == null || !(candidate instanceof String))
				return "error.history.comment.unsupported::Comment value is not supported";
			if (candidate.toString().trim().isEmpty())
				return "error.history.comment.empty::Comment cannot be empty";
			break;
		}
		return null;
	}
}
