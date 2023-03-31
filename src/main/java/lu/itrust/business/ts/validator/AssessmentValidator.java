/**
 * 
 */
package lu.itrust.business.ts.validator;

import java.util.Collection;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;

/**
 * @author eomar
 * 
 */
public class AssessmentValidator extends ValidatorFieldImpl {

	protected double toNumeric(String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			// TrickLogManager.Persist(e);
			return Double.NaN;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(String fieldName, Object candidate) {
		if (fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case "ALEO":
		case "ALE":
		case "ALEP":
			if (candidate == null || !(candidate instanceof Double))
				return String.format("error.assessment.invalid.value.%s::%s should be a numeric", fieldName.toLowerCase(), fieldName);
			double ale = (double) candidate;
			if (ale < 0)
				return String.format("error.assessment.negatif.value.%s::%s needs to be greater or equal 0", fieldName.toLowerCase(), fieldName);
			break;
		case "uncertainty":
			if (candidate == null || !(candidate instanceof Double))
				return String.format("error.assessment.invalid.value.%s::%s should be a numeric", fieldName.toLowerCase(), fieldName);
			double uncertainty = (double) candidate;
			if (uncertainty <= 1)
				return "error.assessment.uncertainty.less_one::Uncertainty needs to be greater than 1";
			break;
		case "likelihoodReal":
			if (candidate == null || !(candidate instanceof Double))
				return "error.assessment.invalid.value.probability::Probability should be a numeric";
			double likelihoodReal = (double) candidate;
			if (likelihoodReal <= 1)
				return "error.assessment.negatif.value.probability::Probability needs to be greater than 1";
			break;
		case "impactReal":
			if (candidate == null || !(candidate instanceof Double))
				return "error.assessment.invalid.value.impact_real::Real impact should be a numeric";
			double impactReal = (double) candidate;
			if (impactReal <= 1)
				return "error.assessment.negatif.value.impact_real::Impact needs to be greater than 1";
			break;
		case "asset":
			if (candidate == null || !(candidate instanceof Asset))
				return "error.assessment.asset.null::Asset cannot be empty";
			break;
		case "scenario":
			if (candidate == null || !(candidate instanceof Scenario))
				return "error.assessment.scenario.null::Scenario cannot be empty";
			break;
		case "selected":
			if (candidate == null || !(candidate instanceof Boolean))
				return "error.assessment.selected.null::Select is required";
			break;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object,
	 * java.lang.String, java.lang.Object, java.lang.Object[])
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		if (choose == null || choose.length == 0)
			return validate(o, fieldName, candidate);
		if (o == null || !supports(o.getClass()) || fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case "impactRep":
			if (candidate == null || !(candidate instanceof String))
				return "error.assessment.impact_rep.null::Impact reputation cannot be empty";
			else if (Double.isNaN(toNumeric(candidate.toString())) && !Contains(choose, candidate))
				return "error.assessment.invalid.impact_rep::Impact reputation is not valid";
			break;
		case "impactOp":
			if (candidate == null || !(candidate instanceof String))
				return "error.assessment.impact_op.null::Impact operational cannot be empty";
			else if (Double.isNaN(toNumeric(candidate.toString())) && !Contains(choose, candidate))
				return "error.assessment.invalid.impact_op::Impact operational is not valid";
			break;
		case "impactLeg":
			if (candidate == null || !(candidate instanceof String))
				return "error.assessment.impact_leg.null::Impact legal cannot be empty";
			else if (Double.isNaN(toNumeric(candidate.toString())) && !Contains(choose, candidate))
				return "error.assessment.invalid.impact_leg::Impact legal is not valid";
			break;
		case "impactFin":
			if (candidate == null || !(candidate instanceof String))
				return "error.assessment.impact_fin.null::Impact financial cannot be empty";
			else if (Double.isNaN(toNumeric(candidate.toString())) && !Contains(choose, candidate))
				return "error.assessment.invalid.impact_fin::Impact financial is not valid";
			break;
		case "likelihood":
			if (candidate == null || !(candidate instanceof String))
				return "error.assessment.probability.null::Probability cannot be empty";
			else if (Double.isNaN(toNumeric(candidate.toString())) && !IsValidExpression(candidate, choose))
				return "error.assessment.invalid.probability::Probability is not valid";
			break;
		}
		return validate(o, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object,
	 * java.lang.String, java.lang.Object, java.util.List)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		if (choose == null || choose.isEmpty())
			return validate(choose, fieldName, candidate);
		return validate(choose, fieldName, candidate, choose.toArray());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#supported()
	 */
	@Override
	public Class<?> supported() {
		return Assessment.class;
	}

}
