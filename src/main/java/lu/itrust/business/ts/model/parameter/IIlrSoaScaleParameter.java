package lu.itrust.business.ts.model.parameter;

import static lu.itrust.business.ts.constants.Constant.PARAMETERTYPE_TYPE_ILR_SOA_SCALE_NAME;
import static lu.itrust.business.ts.constants.Constant.PARAMETER_CATEGORY_ILR_SOA_SCALE;

/**
 * This interface represents a scale parameter for the ILR SOA (Service-Oriented Architecture).
 * It extends the {@link IColoredParameter} interface.
 */
public interface IIlrSoaScaleParameter extends IColoredParameter {
	
	/**
	 * Returns the type name of the scale parameter.
	 *
	 * @return the type name of the scale parameter
	 */
	@Override
	default String getTypeName() {
		return PARAMETERTYPE_TYPE_ILR_SOA_SCALE_NAME;
	}

	/**
	 * Returns the group of the scale parameter.
	 *
	 * @return the group of the scale parameter
	 */
	@Override
	default String getGroup() {
		return PARAMETER_CATEGORY_ILR_SOA_SCALE;
	}
}
