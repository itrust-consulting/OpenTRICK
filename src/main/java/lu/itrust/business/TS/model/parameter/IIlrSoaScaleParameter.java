package lu.itrust.business.TS.model.parameter;

import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_ILR_SOA_SCALE;
import static lu.itrust.business.TS.constants.Constant.PARAMETER_CATEGORY_ILR_SOA_SCALE;

public interface IIlrSoaScaleParameter extends IColoredParameter {
    
	@Override
	default String getTypeName() {
		return PARAMETERTYPE_TYPE_ILR_SOA_SCALE;
	}

	@Override
	default String getGroup() {
		return PARAMETER_CATEGORY_ILR_SOA_SCALE;
	}
}
