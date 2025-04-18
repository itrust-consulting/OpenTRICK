package lu.itrust.business.ts.model.parameter;

import static lu.itrust.business.ts.constants.Constant.PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME;
import static lu.itrust.business.ts.constants.Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE;

/**
 * This interface represents a risk acceptance parameter.
 * It extends the {@link IColoredParameter} interface.
 */
public interface IRiskAcceptanceParameter extends IColoredParameter {

	/**
	 * Returns the label of the risk acceptance parameter.
	 *
	 * @return the label of the risk acceptance parameter
	 */
	String getLabel();

	/**
	 * Returns the type name of the risk acceptance parameter.
	 * This method overrides the default implementation in the {@link IColoredParameter} interface.
	 *
	 * @return the type name of the risk acceptance parameter
	 */
	@Override
	default String getTypeName() {
		return PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME;
	}

	/**
	 * Returns the group of the risk acceptance parameter.
	 * This method overrides the default implementation in the {@link IColoredParameter} interface.
	 *
	 * @return the group of the risk acceptance parameter
	 */
	@Override
	default String getGroup() {
		return PARAMETER_CATEGORY_RISK_ACCEPTANCE;
	}

}
