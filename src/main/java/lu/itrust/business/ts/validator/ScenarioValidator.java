package lu.itrust.business.ts.validator;

import java.util.Collection;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.scenario.ScenarioType;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;

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
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	@Override
	public Class<?> supported() {
		return Scenario.class;
	}

	@Override
	public void validate(Object arg0, Errors arg1) {
		Scenario scenario = (Scenario) arg0;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "name", "error.scneario.name.empty", "Name cannot be empty");

		if (!arg1.hasFieldErrors("scnearioType") && !(scenario.getType() instanceof ScenarioType))
			arg1.rejectValue("scnearioType", "error.scenario.scneario_type.rejected", "Scenario Type is not valid");
	}

}
