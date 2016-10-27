/**
 * 
 */
package lu.itrust.business.TS.model.parameter;

import lu.itrust.business.TS.constants.Constant;

/**
 * @author eomar
 *
 */
public interface IImpactParameter extends IBoundedParameter {

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.parameter.IParameter#getGroup()
	 */
	@Override
	default String getGroup() {
		return Constant.PARAMETER_CATEGORY_IMPACT;
	}

}
