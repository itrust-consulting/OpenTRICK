package lu.itrust.business.TS.model.parameter;

import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE;

public interface IRiskAcceptanceParameter extends IColoredParameter {

	String getLabel();

	@Override
	default String getTypeName() {
		return PARAMETERTYPE_TYPE_RISK_ACCEPTANCE_NAME;
	}

	@Override
	default String getGroup() {
		return PARAMETER_CATEGORY_RISK_ACCEPTANCE;
	}

}
