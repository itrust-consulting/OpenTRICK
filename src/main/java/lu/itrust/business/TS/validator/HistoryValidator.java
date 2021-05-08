/**
 * 
 */
package lu.itrust.business.TS.validator;

import java.util.Collection;
import java.util.Date;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.validator.field.ValidatorFieldImpl;

/**
 * @author eom
 * 
 */
public class HistoryValidator extends ValidatorFieldImpl implements Validator {

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

	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	@Override
	public Class<?> supported() {
		return History.class;
	}

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
