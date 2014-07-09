/**
 * 
 */
package lu.itrust.business.validator;

import java.util.Date;
import java.util.List;

import lu.itrust.business.TS.History;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.validator.field.ValidatorFieldImpl;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author eom
 * 
 */
public class HistoryValidator extends ValidatorFieldImpl implements Validator {

	@Override
	public void validate(Object arg0, Errors arg1) {

		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "author",
				"error.history.author.empty", "Author cannot be empty");

		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "version",
				"error.history.version.empty", "Version cannot be empty");

		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "comment",
				"error.history.comment.empty", "Comment cannot be empty");

		History history = (History) arg0;
		if (history.getAuthor() == null)
			arg1.rejectValue("author", "error.history.author.null",
					"Author is required");

		if (history.getVersion() == null)
			arg1.rejectValue("version", "error.history.version.null",
					"Version is required");
		else if (!arg1.hasFieldErrors("version") && !history.getVersion().matches(
				Constant.REGEXP_VALID_ANALYSIS_VERSION))
			arg1.rejectValue("version", "error.history.version.not_meet_regex",
					"History version not acceptable");

		if (history.getComment() == null)
			arg1.rejectValue("comment", "error.history.comment.null",
					"Comment is required");
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate) {
		if (o == null || !supports(o.getClass()) || fieldName == null
				|| fieldName.trim().isEmpty())
			return null;
		History history = (History) o;
		switch (fieldName) {
		case "date":
			if (history.getId() > 1 && history.getDate() != null
					&& !history.getDate().equals(candidate))
				return "error.history.data.not_editable:date:History date cannot be edited";
			if (candidate == null || !(candidate instanceof Date))
				return "error.history.data.unsupported:date:Date value is not supported";
			break;
		case "author":
			if (candidate == null || !(candidate instanceof String))
				return "error.history.data.unsupported:author:Author value is not supported";
			String author = (String) candidate;
			if (author.trim().isEmpty())
				return "error.history.author.empty";
			break;
		case "version":
			if (candidate == null || !(candidate instanceof String))
				return "error.history.data.unsupported:Author:Author value is not supported";
			if (!candidate.toString().matches(
					Constant.REGEXP_VALID_ANALYSIS_VERSION))
				return "error.history.version.not_meet_regex::Version not acceptable";
			break;
		case "comment":
			if (candidate == null || !(candidate instanceof String))
				return "error.history.data.unsupported:comment:Comment value is not supported";
			if (candidate.toString().trim().isEmpty())
				return "error.history.comment.empty::Comment cannot be empty";
			break;
		}
		return null;
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate,
			Object[] choose) {
		return validate(o, fieldName, candidate);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate,
			List<Object> choose) {
		return validate(o, fieldName, candidate);
	}

	@Override
	public Class<?> supported() {
		return History.class;
	}
}
