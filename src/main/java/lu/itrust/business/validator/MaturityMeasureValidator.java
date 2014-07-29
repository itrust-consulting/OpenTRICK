/**
 * 
 */
package lu.itrust.business.validator;

import lu.itrust.business.TS.MaturityMeasure;
import lu.itrust.business.TS.Parameter;

/**
 * @author eomar
 * 
 */
public class MaturityMeasureValidator extends MeasureValidator {

	protected static final String REACHED_LEVEL = "reachedLevel";
	private static final String IMPLEMENTATION_RATE = "implementationRate";
	protected static final String SML1_COST = "SML1Cost";
	protected static final String SML2_COST = "SML2Cost";
	protected static final String SML3_COST = "SML3Cost";
	protected static final String SML4_COST = "SML4Cost";
	protected static final String SML5_COST = "SML5Cost";
	protected static final String ERROR_UNSUPPORTED_DATA_REACHED_LEVEL_REACHED_LEVEL_VALUE_IS_NOT_SUPPORTED = "error.maturity_measure.unsupported.reached_level::Reached level value is not supported";
	protected static final String ERROR_MATURITY_MEASURE_IMPLEMENTATION_RATE_NULL_A_MATURITY_MEASURE_SHOULD_ALWAYS_HAVE_AN_PARAMETER_FOR_IMPLEMENTATION_RATE = "error.maturity_measure.implementation_rate.null::A maturity measure should always have an parameter for implementation rate";
	protected static final String ERROR_UNSUPPORTED_DATA_IMPLEMENTATION_RATE_IMPLEMENTATION_RATE_VALUE_IS_NOT_SUPPORTED = "error.maturity_measure.unsupported.implementation_rate::Implementation rate value is not supported";
	protected static final String ERROR_MATURITY_MEASURE_IMPLEMENTATION_RATE_NULL_IMPLEMENTATION_RATE_SHOULD_BE_A_REAL_BETWEEN_0_AND_100 = "error.maturity_measure.implementation_rate.percentage::Implementation rate should be a real between 0 and 100";
	protected static final String ERROR_MATURITY_MEASURE_REACHED_LEVEL_INVALID_REACHED_LEVEL_SHOULD_BE_A_INTEGER_BETWEEN_0_AND_5 = "error.maturity_measure.reached_level.invalid::Reached level should be a integer between 0 and 5";
	protected static final String ERROR_UNSUPPORTED_DATA_SML1_COST_SML1_COST_VALUE_IS_NOT_SUPPORTED = "error.maturity_measure.unsupported.sml_cost:1:SML1 cost value is not supported";
	protected static final String ERROR_MATURITY_MEASURE_SML1_COST_INVALID_SML1_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0 = "error.maturity_measure.sml_cost.invalid:1:SML1 cost should be a real greater than or equal 0";
	protected static final String ERROR_UNSUPPORTED_DATA_SML2_COST_SML2_COST_VALUE_IS_NOT_SUPPORTED = "error.maturity_measure.unsupported.sml_cost:2:SML2 cost value is not supported";
	protected static final String ERROR_MATURITY_MEASURE_SML2_COST_INVALID_SML2_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0 = "error.maturity_measure.sml_cost.invalid:2:SML2 cost should be a real greater than or equal 0";
	protected static final String ERROR_UNSUPPORTED_DATA_SML3_COST_SML3_COST_VALUE_IS_NOT_SUPPORTED = "error.maturity_measure.unsupported.sml_cost:3:SML3 cost value is not supported";
	protected static final String ERROR_MATURITY_MEASURE_SML3_COST_INVALID_SML3_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0 = "error.maturity_measure.sml_cost.invalid:3:SML3 cost should be a real greater than or equal 0";
	protected static final String ERROR_UNSUPPORTED_DATA_SML4_COST_SML4_COST_VALUE_IS_NOT_SUPPORTED = "error.maturity_measure.unsupported.sml_cost:4:SML4 cost value is not supported";
	protected static final String ERROR_MATURITY_MEASURE_SML4_COST_INVALID_SML4_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0 = "error.maturity_measure.sml_cost.invalid:4:SML4 cost should be a real greater than or equal 0";
	protected static final String ERROR_UNSUPPORTED_DATA_SML5_COST_SML5_COST_VALUE_IS_NOT_SUPPORTED = "error.maturity_measure.unsupported.sml_cost:5:SML5 cost value is not supported";
	private static final String ERROR_MATURITY_MEASURE_SML5_COST_INVALID_SML5_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0 = "error.maturity_measure.sml_cost.invalid:5:SML5 cost should be a real greater than or equal 0";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.validator.MeasureValidator#validate(java.lang.Object,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate) {
		if (!supports(o.getClass()) || fieldName == null || fieldName.trim().isEmpty())
			return null;
		Double doubleCanditate = null;
		switch (fieldName) {
		case REACHED_LEVEL:
			if (!(candidate instanceof Integer))
				return ERROR_UNSUPPORTED_DATA_REACHED_LEVEL_REACHED_LEVEL_VALUE_IS_NOT_SUPPORTED;
			Integer intCanditate = (Integer) candidate;
			if (intCanditate == null || intCanditate < 0 || intCanditate > 5)
				return ERROR_MATURITY_MEASURE_REACHED_LEVEL_INVALID_REACHED_LEVEL_SHOULD_BE_A_INTEGER_BETWEEN_0_AND_5;
			break;
		case IMPLEMENTATION_RATE:
			if (candidate == null)
				return ERROR_MATURITY_MEASURE_IMPLEMENTATION_RATE_NULL_A_MATURITY_MEASURE_SHOULD_ALWAYS_HAVE_AN_PARAMETER_FOR_IMPLEMENTATION_RATE;
			else if (!(candidate instanceof Parameter))
				return ERROR_UNSUPPORTED_DATA_IMPLEMENTATION_RATE_IMPLEMENTATION_RATE_VALUE_IS_NOT_SUPPORTED;
			else if(((Parameter) candidate).getValue()<0 || ((Parameter) candidate).getValue()>100)
				return ERROR_MATURITY_MEASURE_IMPLEMENTATION_RATE_NULL_IMPLEMENTATION_RATE_SHOULD_BE_A_REAL_BETWEEN_0_AND_100;
			break;
		case SML1_COST:
			if (!(candidate instanceof Double))
				return ERROR_UNSUPPORTED_DATA_SML1_COST_SML1_COST_VALUE_IS_NOT_SUPPORTED;
			doubleCanditate = (Double) candidate;
			if (doubleCanditate == null || doubleCanditate < 0)
				return ERROR_MATURITY_MEASURE_SML1_COST_INVALID_SML1_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0;
			break;
		case SML2_COST:
			if (!(candidate instanceof Double))
				return ERROR_UNSUPPORTED_DATA_SML2_COST_SML2_COST_VALUE_IS_NOT_SUPPORTED;
			doubleCanditate = (Double) candidate;
			if (doubleCanditate == null || doubleCanditate < 0)
				return ERROR_MATURITY_MEASURE_SML2_COST_INVALID_SML2_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0;
			break;
		case SML3_COST:
			if (!(candidate instanceof Double))
				return ERROR_UNSUPPORTED_DATA_SML3_COST_SML3_COST_VALUE_IS_NOT_SUPPORTED;
			doubleCanditate = (Double) candidate;
			if (doubleCanditate == null || doubleCanditate < 0)
				return ERROR_MATURITY_MEASURE_SML3_COST_INVALID_SML3_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0;
			break;
		case SML4_COST:
			if (!(candidate instanceof Double))
				return ERROR_UNSUPPORTED_DATA_SML4_COST_SML4_COST_VALUE_IS_NOT_SUPPORTED;
			doubleCanditate = (Double) candidate;
			if (doubleCanditate == null || doubleCanditate < 0)
				return ERROR_MATURITY_MEASURE_SML4_COST_INVALID_SML4_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0;
			break;
		case SML5_COST:
			if (!(candidate instanceof Double))
				return ERROR_UNSUPPORTED_DATA_SML5_COST_SML5_COST_VALUE_IS_NOT_SUPPORTED;
			doubleCanditate = (Double) candidate;
			if (doubleCanditate == null || doubleCanditate < 0)
				return ERROR_MATURITY_MEASURE_SML5_COST_INVALID_SML5_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0;
			break;
		}
		return super.validate(o, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.MeasureValidator#supported()
	 */
	@Override
	public Class<?> supported() {
		return MaturityMeasure.class;
	}

}
