/**
 * 
 */
package lu.itrust.business.TS.validator;

import java.util.List;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.helper.AnalysisForm;
import lu.itrust.business.TS.validator.field.ValidatorFieldImpl;

/**
 * @author eomar
 *
 */
public class CustomAnalysisValidator extends ValidatorFieldImpl {

	@Override
	public String validate(String fieldName, Object candidate) {
		if (fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case "customer":
			if (candidate == null || !(candidate instanceof Integer))
				return "error.analysis_custom.customer_id.unsupported::Customer id is not supported";
			else if ((int) candidate < 1)
				return "error.analysis_custom.customer_id::No customer selected";
			break;
		case "language":
			if (candidate == null || !(candidate instanceof Integer))
				return "error.analysis_custom.language_id.unsupported::Language id is not supported";
			else if ((int) candidate < 1)
				return "error.analysis_custom.language_id::No language selected";
			break;
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
		case "name":
			if (candidate == null || !(candidate instanceof String))
				return "error.analysis.label.unsupported::Name value is not supported";
			if (candidate.toString().trim().isEmpty())
				return "error.analysis.label.empty::Name cannot be empty";
			break;
		}
		return null;
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, List<Object> choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	@Override
	public Class<?> supported() {
		return AnalysisForm.class;
	}

}
