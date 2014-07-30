/**
 * 
 */
package lu.itrust.business.validator;

import java.util.List;

import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.validator.field.ValidatorFieldImpl;

/**
 * @author eomar
 *
 */
public class RiskInformationValidator extends ValidatorFieldImpl{

	@Override
	public String validate(Object o, String fieldName, Object candidate) {
		if(o == null || !supports(o.getClass()) || fieldName == null || fieldName.trim().isEmpty())
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
			String chapter = (String) candidate;
			if(!chapter.matches(Constant.REGEXP_VALID_RISKINFORMATION_EXPOSED))
				return "error.risk_information.chapter.invalid::Chapter value cannot be accepted";
			break;
		case "acronym":
			if(candidate == null)
				return "error.risk_information.acronym.null::Acronym cannot be empty";
			else if(!(candidate instanceof String))
				return "error.risk_information.unsupported.acronym::Acronym value is not supported";
			break;
		case "comment":
			if(candidate == null)
				return "error.risk_information.comment.null::Label cannot be empty";
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

	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) {
		return validate(choose, fieldName, candidate);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, List<Object> choose) {
		return validate(choose, fieldName, candidate);
	}

	@Override
	public Class<?> supported() {
		return RiskInformation.class;
	}

}
