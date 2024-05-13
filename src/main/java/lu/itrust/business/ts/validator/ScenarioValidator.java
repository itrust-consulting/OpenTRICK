package lu.itrust.business.ts.validator;

import java.util.Collection;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.scenario.ScenarioType;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;

/**
 * The ScenarioValidator class is responsible for validating scenarios.
 * It implements the Validator interface and extends the ValidatorFieldImpl class.
 * This class provides methods to validate different fields of a scenario object.
 */
public class ScenarioValidator extends ValidatorFieldImpl implements Validator {

	@Override
	public String validate(String fieldName, Object candidate) {
		if (fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
			case "name":
				if (!(candidate instanceof String))
					return "error.scenario.name.unsupported::Name type is not valid";
				else if (candidate.toString().trim().isEmpty())
					return "error.scenario.name.empty::Name cannot be empty";
				break;
			case "scenarioType":
				if (!(candidate instanceof ScenarioType))
					return "error.scenario.scenario_type.unsupported::Scenario Type is not valid";
				break;
			case "description":
				if (!(candidate instanceof String))
					return "error.scenario.description.unsupported::Description type is not valid";
				break;
			case "selected":
				if (!(candidate instanceof Boolean))
					return "error.scenario.selected.unsupported::Selected type is not valid";
				break;
			default:
				return null;
		}
		return null;
	}

	/**
	 * Validates the given object and returns a string representation of the validation result.
	 *
	 * @param o the object to be validated
	 * @param fieldName the name of the field being validated
	 * @param candidate the candidate value for the field
	 * @param choose an array of objects to choose from
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
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose)
			throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/**
	 * Returns the class that this validator supports.
	 *
	 * @return the supported class
	 */
	@Override
	public Class<?> supported() {
		return Scenario.class;
	}

	/**
	 * Validates a scenario object.
	 *
	 * @param arg0 The scenario object to be validated.
	 * @param arg1 The Errors object to store any validation errors.
	 */
	@Override
	public void validate(Object arg0, Errors arg1) {
		Scenario scenario = (Scenario) arg0;

		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "name", "error.scneario.name.empty", "Name cannot be empty");

		if (!arg1.hasFieldErrors("scnearioType") && !(scenario.getType() instanceof ScenarioType))
			arg1.rejectValue("scnearioType", "error.scenario.scneario_type.rejected", "Scenario Type is not valid");
	}

}
