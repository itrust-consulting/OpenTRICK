/**
 * 
 */
package lu.itrust.business.ts.model.parameter;

import lu.itrust.business.ts.constants.Constant;

/**
 * @author eomar
 *
 */
public interface IProbabilityParameter extends ILevelParameter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.parameter.IParameter#getCategory()
	 */
	@Override
	default String getGroup() {
		return Constant.PARAMETER_CATEGORY_PROBABILITY;
	}

}
