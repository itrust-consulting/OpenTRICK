/**
 * 
 */
package lu.itrust.business.ts.validator;

import java.util.Collection;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.form.AnalysisForm;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;


/**
 * This class is a custom implementation of the ValidatorFieldImpl class. It provides validation logic for different fields in the AnalysisForm class.
 * The validate method is overridden to perform specific validation based on the field name and candidate value.
 * The supported method returns the class that this validator supports.
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

	/**
	 * Validates the given object and returns a string representation of the validation result.
	 *
	 * @param o the object to be validated
	 * @param fieldName the name of the field being validated
	 * @param candidate the candidate value to be validated
	 * @param choose an array of objects to choose from during validation
	 * @return a string representation of the validation result
	 * @throws TrickException if an error occurs during validation
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/**
	 * Validates the given object and returns a string representation of the validation result.
	 *
	 * @param o the object to be validated
	 * @param fieldName the name of the field being validated
	 * @param candidate the candidate value to be validated
	 * @param choose a collection of objects to choose from during validation
	 * @return a string representation of the validation result
	 * @throws TrickException if an error occurs during validation
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/**
	 * Returns the class that this validator supports.
	 *
	 * @return the supported class
	 */
	@Override
	public Class<?> supported() {
		return AnalysisForm.class;
	}

}
