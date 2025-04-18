/**
 * 
 */
package lu.itrust.business.ts.validator;

import java.util.List;

import lu.itrust.business.ts.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.ts.model.standard.measure.impl.NormalMeasure;

/**
 * This class is a validator for the NormalMeasure class. It extends the MeasureValidator class and provides validation logic for various fields of a NormalMeasure object.
 * 
 * The fields that are validated by this class include:
 * - implementationRate: The implementation rate value of the NormalMeasure object.
 * - assetTypeValues: The asset type values of the NormalMeasure object.
 * - measurePropertyList: The measure property list of the NormalMeasure object.
 * - toCheck: The to check value of the NormalMeasure object.
 * 
 * This class provides validation methods for each of these fields, and returns an error message if the validation fails.
 * 
 * This class also overrides the supported() method from the MeasureValidator class to specify that it supports the NormalMeasure class.
 */
public class NormalMeasureValidator extends MeasureValidator {
	private static final String IMPLEMENTATION_RATE = "implementationRate";
	private static final String ASSET_TYPE_VALUES = "assetTypeValues";
	private static final String MEASURE_PROPERTY_LIST = "measurePropertyList";
	private static final String TO_CHECK = "toCheck";
	private static final String ERROR_UNSUPPORTED_DATA_IMPLEMENTATION_RATE_IMPLEMENTATION_RATE_VALUE_IS_NOT_SUPPORTED = "error.norm_measure.unsupported.implementation_rate::Implementation rate value is not supported";
	protected static final String ERROR_STANDARD_MEASURE_IMPLEMENTATION_RATE_NULL_IMPLEMENTATION_RATE_SHOULD_BE_A_REAL_BETWEEN_0_AND_100 = "error.norm_measure.implementation_rate.null::Implementation rate should be a real between 0 and 100";
	protected static final String ERROR_MEASURE_ASSET_TYPE_VALUES_NULL_A_MEASURE_SHOULD_ALWAYS_BE_HAVE_A_ASSET_TYPE_VALUES = "error.measure.asset_type_values.null::A measure should always be have a asset type values";
	protected static final String ERROR_UNSUPPORTED_DATA_ASSET_TYPE_VALUES_ASSET_TYPE_VALUES_VALUE_IS_NOT_SUPPORTED = "error.norm_measure.unsupported.asset_type_values::Asset-type-values value is not supported";
	protected static final String ERROR_MEASURE_MEASURE_PROPERTY_LIST_NULL_A_MEASURE_SHOULD_ALWAYS_BE_HAVE_A_PROPERTIES = "error.measure.measure_property_list.null::A measure should always be have a properties";
	protected static final String ERROR_UNSUPPORTED_DATA_MEASURE_PROPERTY_LIST_MEASURE_PROPERTIES_VALUE_IS_NOT_SUPPORTED = "error.norm_measure.unsupported.measure_property_list::Measure properties value is not supported";
	protected static final String ERROR_UNSUPPORTED_DATA_TO_CHECK_TO_CHECK_VALUE_IS_NOT_SUPPORTED = "error.norm_measure.unsupported.to_check::To check value is not supported";

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
		Double doubleCanditate = null;
		switch (fieldName) {
			case IMPLEMENTATION_RATE:
				if (!(candidate instanceof Double))
					return ERROR_UNSUPPORTED_DATA_IMPLEMENTATION_RATE_IMPLEMENTATION_RATE_VALUE_IS_NOT_SUPPORTED;
				doubleCanditate = (Double) candidate;
				if (doubleCanditate < 0 || doubleCanditate > 100)
					return ERROR_STANDARD_MEASURE_IMPLEMENTATION_RATE_NULL_IMPLEMENTATION_RATE_SHOULD_BE_A_REAL_BETWEEN_0_AND_100;
				break;
			case ASSET_TYPE_VALUES:
				if (candidate == null)
					return ERROR_MEASURE_ASSET_TYPE_VALUES_NULL_A_MEASURE_SHOULD_ALWAYS_BE_HAVE_A_ASSET_TYPE_VALUES;
				else if (!(candidate instanceof List))
					return ERROR_UNSUPPORTED_DATA_ASSET_TYPE_VALUES_ASSET_TYPE_VALUES_VALUE_IS_NOT_SUPPORTED;
				break;
			case MEASURE_PROPERTY_LIST:
				if (candidate == null)
					return ERROR_MEASURE_MEASURE_PROPERTY_LIST_NULL_A_MEASURE_SHOULD_ALWAYS_BE_HAVE_A_PROPERTIES;
				else if (!(candidate instanceof MeasureProperties))
					return ERROR_UNSUPPORTED_DATA_MEASURE_PROPERTY_LIST_MEASURE_PROPERTIES_VALUE_IS_NOT_SUPPORTED;
				break;
			case TO_CHECK:
				if (!(candidate instanceof String))
					return ERROR_UNSUPPORTED_DATA_TO_CHECK_TO_CHECK_VALUE_IS_NOT_SUPPORTED;
				break;
			default:
				return super.validate(fieldName, candidate);

		}
		return super.validate(fieldName, candidate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.MeasureValidator#supported()
	 */
	@Override
	public Class<?> supported() {
		return NormalMeasure.class;
	}

}
