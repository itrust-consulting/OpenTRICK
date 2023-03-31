/**
 * 
 */
package lu.itrust.business.ts.model.parameter;

import lu.itrust.business.ts.constants.Constant;

/**
 * @author eomar
 *
 */
public interface IImpactParameter extends IBoundedParameter {

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.parameter.IParameter#getGroup()
	 */
	@Override
	default String getGroup() {
		return Constant.PARAMETER_CATEGORY_IMPACT;
	}

}
