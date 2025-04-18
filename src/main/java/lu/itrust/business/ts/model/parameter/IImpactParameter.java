package lu.itrust.business.ts.model.parameter;

import lu.itrust.business.ts.constants.Constant;

/**
 * This interface represents an impact parameter, which is a type of bounded parameter.
 * It extends the {@link IBoundedParameter} interface.
 */
public interface IImpactParameter extends IBoundedParameter {

	/**
	 * Returns the group of the impact parameter.
	 *
	 * @return the group of the impact parameter
	 */
	@Override
	default String getGroup() {
		return Constant.PARAMETER_CATEGORY_IMPACT;
	}

}
