package lu.itrust.business.TS.validator;

import java.util.List;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.scenario.Scenario;
import lu.itrust.business.TS.data.scenario.ScenarioType;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.validator.field.ValidatorFieldImpl;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ScenarioValidator extends ValidatorFieldImpl implements Validator {

	@Override
	public String validate(String fieldName, Object candidate) {
		if ( fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case "name":
			if (candidate == null || !(candidate instanceof String))
				return "error.scenario.name.unsupported::Name type is not valid";
			else if (candidate.toString().trim().isEmpty())
				return "error.scenario.name.empty::Name cannot be empty";
			break;
		case "scenarioType":
			if (candidate == null || !(candidate instanceof ScenarioType))
				return "error.scenario.scenario_type.unsupported::Scenario Type is not valid";
			break;
		case "description":
			if (candidate == null || !(candidate instanceof String))
				return "error.scenario.description.unsupported::Description type is not valid";
			break;
		case "selected":
			if (candidate == null || !(candidate instanceof Boolean))
				return "error.scenario.selected.unsupported::Selected type is not valid";
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
		return Scenario.class;
	}

	@Override
	public void validate(Object arg0, Errors arg1) {
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "name", "error.scneario.name.empty", "Name cannot be empty");

		Scenario scenario = (Scenario) arg0;
		if (!arg1.hasFieldErrors("name") && !scenario.getName().matches(Constant.REGEXP_VALID_NAME))
			arg1.rejectValue("name", "error.scenario.name.rejected", "Name is not valid");

		if (!arg1.hasFieldErrors("scnearioType") && !(scenario.getType() instanceof ScenarioType))
			arg1.rejectValue("scnearioType", "error.scenario.scneario_type.rejected", "Scenario Type is not valid");
	}

}
