/**
 * 
 */
package lu.itrust.business.validator;

import lu.itrust.business.TS.History;
import lu.itrust.business.TS.tsconstant.Constant;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author eom
 * 
 */
public class HistoryValidator implements Validator {

	@Override
	public boolean supports(Class<?> arg0) {
		return History.class.isAssignableFrom(arg0);
	}

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
		else if (!history.getVersion().matches(
				Constant.REGEXP_VALID_ANALYSIS_VERSION))
			arg1.rejectValue("version", "error.history.version.not_meet_regex",
					"History version not acceptable");

		if (history.getComment() == null)
			arg1.rejectValue("comment", "error.history.comment.null",
					"Comment is required");

	}
}
