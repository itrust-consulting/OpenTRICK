/**
 * 
 */
package lu.itrust.business.TS.model.parameter;

import lu.itrust.business.TS.constants.Constant;

/**
 * @author eomar
 *
 */
public interface IProbabilityParameter extends ILevelParameter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.IParameter#getCategory()
	 */
	@Override
	default String getGroup() {
		return Constant.PARAMETER_CATEGORY_PROBABILITY;
	}

}
