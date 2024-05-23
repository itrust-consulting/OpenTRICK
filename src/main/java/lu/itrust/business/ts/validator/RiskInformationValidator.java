/**
 * 
 */
package lu.itrust.business.ts.validator;

import java.util.Collection;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;


/**
 * This class is responsible for validating the fields of a RiskInformation object.
 * It extends the ValidatorFieldImpl class and provides implementation for the validate method.
 * The validate method checks the validity of each field based on its name and value.
 * If a field is invalid, an error message is returned.
 * This class also provides implementation for the supported method, which specifies the class that this validator supports.
 */
public class RiskInformationValidator extends ValidatorFieldImpl{

	@Override
	public String validate(String fieldName, Object candidate) {
		if(fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case "label":
			if(candidate == null)
				return "error.risk_information.label.null::Label cannot be empty";
			else if(!(candidate instanceof String))
				return "error.risk_information.unsupported.label::Label value is not supported";
			String label = (String) candidate;
			if(label.trim().isEmpty())
				return "error.risk_information.label.empty::Label cannot be empty";
			break;
		case "category":
			if(candidate == null)
				return "error.risk_information.category.null::Category cannot be empty";
			else if(!(candidate instanceof String))
				return "error.risk_information.unsupported.category::Category value is not supported";
			String category = (String) candidate;
			if(category.trim().isEmpty())
				return "error.risk_information.exposed.empty::Exposed cannot be empty";
			else if(!category.matches(Constant.REGEXP_VALID_RISKINFORMATION_TYPE))
				return "error.risk_information.category.invalid::Category value cannot be accepted";
			break;
		case "exposed":
			if(candidate == null)
				return "error.risk_information.exposed.null::Exposed cannot be empty";
			else if(!(candidate instanceof String))
				return "error.risk_information.unsupported.exposed::Exposed value is not supported";
			String exposed = (String) candidate;
			if(!exposed.matches(Constant.REGEXP_VALID_RISKINFORMATION_EXPOSED))
				return "error.risk_information.exposed.invalid::Exposed value cannot be accepted";
			break;
		case "chapter":
			if(candidate == null)
				return "error.risk_information.chapter.null::Chapter cannot be empty";
			else if(!(candidate instanceof String))
				return "error.risk_information.unsupported.chapter::Chapter value is not supported";
			break;
		case "acronym":
			if(candidate == null)
				return "error.risk_information.acronym.null::Acronym cannot be empty";
			else if(!(candidate instanceof String))
				return "error.risk_information.unsupported.acronym::Acronym value is not supported";
			break;
		case "owner":
			if(candidate == null)
				return "error.risk_information.owner.null::Owner cannot be empty";
			else if(!(candidate instanceof String))
				return "error.risk_information.unsupported.owner::Owner value is not supported";
			break;
		case "comment":
			if(candidate == null)
				return "error.risk_information.comment.null::Comment cannot be empty";
			else if(!(candidate instanceof String))
				return "error.risk_information.unsupported.comment::Comment value is not supported";
			break;
		case "hiddenComment":
			if(candidate == null)
				return "error.risk_information.hiddenComment.null::Hidden comment cannot be empty";
			else if(!(candidate instanceof String))
				return "error.risk_information.unsupported.hidden_comment::Hidden comment value is not supported";
			break;
		default:
			break;
		}
		return null;
	}

	/**
	 * Validates the given object based on the provided field name, candidate value, and choose options.
	 *
	 * @param o the object to be validated
	 * @param fieldName the name of the field to be validated
	 * @param candidate the candidate value to be validated
	 * @param choose the choose options for validation
	 * @return the validation result as a string
	 * @throws TrickException if an error occurs during validation
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return validate(choose, fieldName, candidate);
	}

	/**
	 * Validates the given object based on the specified field name, candidate value, and collection of choices.
	 *
	 * @param o         the object to be validated
	 * @param fieldName the name of the field being validated
	 * @param candidate the candidate value to be validated
	 * @param choose    the collection of choices for validation
	 * @return the validation result as a string
	 * @throws TrickException if an error occurs during validation
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		return validate(choose, fieldName, candidate);
	}

	/**
	 * Returns the class that this validator supports.
	 *
	 * @return the class that this validator supports
	 */
	@Override
	public Class<?> supported() {
		return RiskInformation.class;
	}

}
