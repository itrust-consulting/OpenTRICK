/**
 * 
 */
package lu.itrust.business.validator;

import java.util.List;

import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.validator.field.ValidatorFieldImpl;

/**
 * @author eomar
 * 
 */
public class AssessmentValidator extends ValidatorFieldImpl {

	protected double toNumeric(String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
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
	public String validate(Object o, String fieldName, Object candidate) {
		if (o == null || !supports(o.getClass()) || fieldName == null
				|| fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case "ALEO":
		case "ALE":
		case "ALEP":
			if (candidate == null || !(candidate instanceof Double))
				return "error.assessment.invalid.value:" + fieldName + ":"
						+ fieldName + " should be a numeric";
			double ale = (double) candidate;
			if (ale < 0)
				return "error.assessment.negatif.value:" + fieldName + ":"
						+ fieldName + " needs to be greater or equal 0";
			break;
		case "uncertainty":
			if (candidate == null || !(candidate instanceof Double))
				return "error.assessment.invalid.value:" + fieldName + ":"
						+ fieldName + " should be a numeric";
			double uncertainty = (double) candidate;
			if (uncertainty <= 1)
				return "error.assessment.uncertainty.less_one:" + fieldName
						+ ":" + fieldName + " needs to be greater than 1";
			break;
		case "likelihoodReal":
			if (candidate == null || !(candidate instanceof Double))
				return "error.assessment.invalid.value:" + fieldName + ":"
						+ fieldName + " should be a numeric";
			double likelihoodReal = (double) candidate;
			if (likelihoodReal <= 1)
				return "error.assessment.negatif.value.likelihoodReal::Probability needs to be greater than 1";
			break;
		case "impactReal":
			if (candidate == null || !(candidate instanceof Double))
				return "error.assessment.invalid.value:" + fieldName + ":"
						+ fieldName + " should be a numeric";
			double impactReal = (double) candidate;
			if (impactReal <= 1)
				return "error.assessment.negatif.value.impactReal::Impact needs to be greater than 1";
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
	public String validate(Object o, String fieldName, Object candidate,
			Object[] choose) {
		if (choose == null || choose.length == 0)
			return validate(o, fieldName, candidate);
		if (o == null || !supports(o.getClass()) || fieldName == null
				|| fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case "impactRep":
			if (candidate == null || !(candidate instanceof String))
				return "error.assessment.impactRep.null::Impact reputation cannot be empty";
			else if (Double.isNaN(toNumeric(candidate.toString()))
					&& !Contains(choose, candidate))
				return "error.assessment.invalid.impactRep::Impact reputation is not valid";
			break;
		case "impactOp":
			if (candidate == null || !(candidate instanceof String))
				return "error.assessment.impactOp.null::Impact operationnel cannot be empty";
			else if (Double.isNaN(toNumeric(candidate.toString()))
					&& !Contains(choose, candidate))
				return "error.assessment.invalid.impactOp::Impact operationnel is not valid";
			break;
		case "impactLeg":
			if (candidate == null || !(candidate instanceof String))
				return "error.assessment.impactLeg.null::Impact legal cannot be empty";
			else if (Double.isNaN(toNumeric(candidate.toString()))
					&& !Contains(choose, candidate))
				return "error.assessment.invalid.impactLeg::Impact legal is not valid";
			break;
		case "impactFin":
			if (candidate == null || !(candidate instanceof String))
				return "error.assessment.impactFin.null::Impact financial cannot be empty";
			else if (Double.isNaN(toNumeric(candidate.toString()))
					&& !Contains(choose, candidate))
				return "error.assessment.invalid.impactFin::Impact financial is not valid";
			break;
		case "likelihood":
			if (candidate == null || !(candidate instanceof String))
				return "error.assessment.likelihood.null::Probabilty cannot be empty";
			else if (Double.isNaN(toNumeric(candidate.toString()))
					&& !Contains(choose, candidate))
				return "error.assessment.invalid.likelihood::Probabilty is not valid";
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
	public String validate(Object o, String fieldName, Object candidate,
			List<Object> choose) {
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
