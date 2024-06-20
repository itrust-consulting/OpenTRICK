/**
 * 
 */
package lu.itrust.business.ts.model.parameter;

import lu.itrust.business.ts.constants.Constant;


/**
 * This interface represents a probability parameter in a business time series model.
 * It extends the {@link ILevelParameter} interface.
 */
public interface IProbabilityParameter extends ILevelParameter {

	/**
	 * Returns the category of the probability parameter.
	 * 
	 * @return the category of the probability parameter
	 */
	@Override
	default String getGroup() {
		return Constant.PARAMETER_CATEGORY_PROBABILITY;
	}

}
