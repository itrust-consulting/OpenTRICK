/**
 * 
 */
package lu.itrust.business.TS.validator;

import java.util.Collection;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.validator.field.ValidatorFieldImpl;

/**
 * @author eomar
 * 
 */
public class MeasureValidator extends ValidatorFieldImpl {

	protected static final String IMPORTANCE = "Importance";
	protected static final String ANALYSIS_STANDARD = "analysisStandard";
	protected static final String MEASURE_DESCRIPTION = "measureDescription";
	protected static final String PHASE = "phase";
	protected static final String STATUS = "status";
	protected static final String INTERNAL_WL = "internalWL";
	protected static final String EXTERNAL_WL = "externalWL";
	protected static final String INVESTMENT = "investment";
	protected static final String LIFETIME = "lifetime";
	protected static final String MAINTENANCE = "maintenance";
	protected static final String COST = "cost";
	protected static final String COMMENT = "comment";
	protected static final String TO_DO = "toDo";
	protected static final String ERROR_MEASURE_IMPORTANCE_INVALID_IMPORTANCE_SHOULD_BE_A_INTEGER_BETWEEN_1_AND_3 = "error.measure.importance.invalid::Importance should be a integer between 1 and 3!";
	protected static final String ERROR_MEASURE_UNSUPPORTED_IMPORTANCE_IMPORTANCE_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.importance::Importance value is not supported";
	protected static final String ERROR_MEASURE_ANALYSIS_STANDARD_NULL_A_MEASURE_SHOULD_ALWAYS_BE_ATTACHED_TO_AN_ANALYSIS = "error.measure.analysis_norm.null::A measure should always be  attached to an analysis";
	protected static final String ERROR_UNSUPPORTED_DATA_ANALYSIS_STANDARD_ANALYSIS_STANDARD_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.analysis_norm::Analysis norm value is not supported";
	protected static final String ERROR_MEASURE_MEASURE_DESCRIPTION_NULL_A_MEASURE_SHOULD_ALWAYS_HAVE_A_DESCRIPTION = "error.measure.measure_description.null::A measure should always have a description";
	protected static final String ERROR_UNSUPPORTED_DATA_MEASURE_DESCRIPTION_MEASURE_DESCRIPTION_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.measure_description::Measure description value is not supported";
	protected static final String ERROR_MEASURE_PHASE_NULL_A_MEASURE_SHOULD_ALWAYS_BE_IN_A_PHASE = "error.measure.phase.null::A measure should always be in a phase";
	protected static final String ERROR_UNSUPPORTED_DATA_PHASE_PHASE_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.phase::Phase value is not supported";
	protected static final String ERROR_UNSUPPORTED_DATA_STATUS_STATUS_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.status::Status value is not supported";
	protected static final String ERROR_MEASURE_STATUS_NULL_A_MEASURE_SHOULD_ALWAYS_HAVE_A_STATUS = "error.measure.status.null::A measure should always have a status";
	protected static final String ERROR_MEASURE_STATUS_INVALID_MEASURE_STATUS_VALUE_NEEDS_TO_BE_ONE_OF_THESE_VALUES_AP_NA_M = "error.measure.status.invalid::Measure Status value needs to be one of these values (AP, NA, M)";
	protected static final String ERROR_UNSUPPORTED_DATA_INTERNAL_WL_INTERNAL_WORKLOAD_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.internal_wl::Internal workload value is not supported";
	protected static final String ERROR_MEASURE_INTERNAL_WL_INVALID_INTERNAL_WORKLOAD_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0 = "error.measure.internal_wl.invalid::Internal workload should be a real greater than or equal 0";
	protected static final String ERROR_UNSUPPORTED_DATA_EXTERNAL_WL_EXTERNAL_WORKLOAD_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.external_wl::External workload value is not supported";
	protected static final String ERROR_MEASURE_EXTERNAL_WL_INVALID_EXTERNAL_WORKLOAD_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0 = "error.measure.external_wl.invalid::External workload should be a real greater than or equal 0";
	protected static final String ERROR_UNSUPPORTED_DATA_INVESTMENT_INVESTMENT_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.investment::Investment value is not supported";
	protected static final String ERROR_MEASURE_INVESTMENT_INVALID_INVESTMENT_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0 = "error.measure.investment.invalid::Investment should be a real greater than or equal 0";
	protected static final String ERROR_UNSUPPORTED_DATA_LIFETIME_LIFETIME_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.lifetime::Lifetime value is not supported";
	protected static final String ERROR_MEASURE_LIFETIME_INVALID_LIFETIME_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0 = "error.measure.lifetime.invalid::Lifetime should be a real greater than or equal 0";
	protected static final String ERROR_UNSUPPORTED_DATA_MAINTENANCE_MAINTENANCE_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.maintenance::MaintenanceRecurrentInvestment value is not supported";
	protected static final String ERROR_MEASURE_MAINTENANCE_INVALID_MAINTENANCE_SHOULD_BE_1_OR_A_REAL_GREATER_OR_EQUAL_0 = "error.measure.maintenance.invalid::MaintenanceRecurrentInvestment should be -1 or a real greater than or equal 0";
	protected static final String ERROR_UNSUPPORTED_DATA_COST_COST_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.cost::Cost value is not supported";
	protected static final String ERROR_MEASURE_COST_INVALID_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0 = "error.measure.cost.invalid::Cost should be a real greater than or equal 0";
	protected static final String ERROR_UNSUPPORTED_DATA_COMMENT_COMMENT_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.comment::Comment value is not supported";
	protected static final String ERROR_UNSUPPORTED_DATA_TO_DO_TO_DO_VALUE_IS_NOT_SUPPORTED = "error.measure.unsupported.to_do::To do value is not supported";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(String fieldName, Object candidate) {
		if (fieldName == null || fieldName.trim().isEmpty())
			return null;
		String stringCandidate = null;
		Double doubleCanditate = null;
		switch (fieldName) {
			case ANALYSIS_STANDARD:
				if (candidate == null)
					return ERROR_MEASURE_ANALYSIS_STANDARD_NULL_A_MEASURE_SHOULD_ALWAYS_BE_ATTACHED_TO_AN_ANALYSIS;
				else if (!(candidate instanceof AnalysisStandard))
					return ERROR_UNSUPPORTED_DATA_ANALYSIS_STANDARD_ANALYSIS_STANDARD_VALUE_IS_NOT_SUPPORTED;
				break;
			case MEASURE_DESCRIPTION:
				if (candidate == null)
					return ERROR_MEASURE_MEASURE_DESCRIPTION_NULL_A_MEASURE_SHOULD_ALWAYS_HAVE_A_DESCRIPTION;
				else if (!(candidate instanceof MeasureDescription))
					return ERROR_UNSUPPORTED_DATA_MEASURE_DESCRIPTION_MEASURE_DESCRIPTION_VALUE_IS_NOT_SUPPORTED;
				break;
			case PHASE:
				if (candidate == null)
					return ERROR_MEASURE_PHASE_NULL_A_MEASURE_SHOULD_ALWAYS_BE_IN_A_PHASE;
				else if (!(candidate instanceof MeasureDescription))
					return ERROR_UNSUPPORTED_DATA_PHASE_PHASE_VALUE_IS_NOT_SUPPORTED;
				break;
			case STATUS:
				if (!(candidate instanceof String))
					return ERROR_UNSUPPORTED_DATA_STATUS_STATUS_VALUE_IS_NOT_SUPPORTED;
				stringCandidate = (String) candidate;
				if (stringCandidate.trim().isEmpty())
					return ERROR_MEASURE_STATUS_NULL_A_MEASURE_SHOULD_ALWAYS_HAVE_A_STATUS;
				else if (!stringCandidate.matches(Constant.REGEXP_VALID_MEASURE_STATUS))
					return ERROR_MEASURE_STATUS_INVALID_MEASURE_STATUS_VALUE_NEEDS_TO_BE_ONE_OF_THESE_VALUES_AP_NA_M;
				break;
			case INTERNAL_WL:
				if (!(candidate instanceof Double))
					return ERROR_UNSUPPORTED_DATA_INTERNAL_WL_INTERNAL_WORKLOAD_VALUE_IS_NOT_SUPPORTED;
				doubleCanditate = (Double) candidate;
				if (doubleCanditate < 0)
					return ERROR_MEASURE_INTERNAL_WL_INVALID_INTERNAL_WORKLOAD_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0;
				break;
			case EXTERNAL_WL:
				if (!(candidate instanceof Double))
					return ERROR_UNSUPPORTED_DATA_EXTERNAL_WL_EXTERNAL_WORKLOAD_VALUE_IS_NOT_SUPPORTED;
				doubleCanditate = (Double) candidate;
				if (doubleCanditate < 0)
					return ERROR_MEASURE_EXTERNAL_WL_INVALID_EXTERNAL_WORKLOAD_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0;
				break;
			case INVESTMENT:
				if (!(candidate instanceof Double))
					return ERROR_UNSUPPORTED_DATA_INVESTMENT_INVESTMENT_VALUE_IS_NOT_SUPPORTED;
				doubleCanditate = (Double) candidate;
				if (doubleCanditate < 0)
					return ERROR_MEASURE_INVESTMENT_INVALID_INVESTMENT_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0;
				break;
			case IMPORTANCE:
				if (!(candidate instanceof Integer))
					return ERROR_MEASURE_UNSUPPORTED_IMPORTANCE_IMPORTANCE_VALUE_IS_NOT_SUPPORTED;
				int importance = (int) candidate;
				if (importance < 1 || importance > 3)
					return ERROR_MEASURE_IMPORTANCE_INVALID_IMPORTANCE_SHOULD_BE_A_INTEGER_BETWEEN_1_AND_3;
				break;
			case LIFETIME:
				if (!(candidate instanceof Double))
					return ERROR_UNSUPPORTED_DATA_LIFETIME_LIFETIME_VALUE_IS_NOT_SUPPORTED;
				doubleCanditate = (Double) candidate;
				if (doubleCanditate < 0)
					return ERROR_MEASURE_LIFETIME_INVALID_LIFETIME_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0;
				break;
			case MAINTENANCE:
				if (!(candidate instanceof Double))
					return ERROR_UNSUPPORTED_DATA_MAINTENANCE_MAINTENANCE_VALUE_IS_NOT_SUPPORTED;
				doubleCanditate = (Double) candidate;
				if (doubleCanditate < 0 && doubleCanditate != -1)
					return ERROR_MEASURE_MAINTENANCE_INVALID_MAINTENANCE_SHOULD_BE_1_OR_A_REAL_GREATER_OR_EQUAL_0;
				break;
			case COST:
				if (!(candidate instanceof Double))
					return ERROR_UNSUPPORTED_DATA_COST_COST_VALUE_IS_NOT_SUPPORTED;
				doubleCanditate = (Double) candidate;
				if (doubleCanditate < 0)
					return ERROR_MEASURE_COST_INVALID_COST_SHOULD_BE_A_REAL_GREATER_OR_EQUAL_0;
				break;
			case COMMENT:
				if (!(candidate instanceof String))
					return ERROR_UNSUPPORTED_DATA_COMMENT_COMMENT_VALUE_IS_NOT_SUPPORTED;
				break;
			case TO_DO:
				if (!(candidate instanceof String))
					return ERROR_UNSUPPORTED_DATA_TO_DO_TO_DO_VALUE_IS_NOT_SUPPORTED;
				break;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object, java.lang.Object[])
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.validator.field.ValidatorField#validate(java.lang.
	 * Object, java.lang.String, java.lang.Object, java.util.List)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose)
			throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.field.ValidatorField#supported()
	 */
	@Override
	public Class<?> supported() {
		return Measure.class;
	}

}
