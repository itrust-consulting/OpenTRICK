/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper;

import java.util.List;

import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;

/**
 * @author eom
 * 
 */
public class ParameterManager {

	/**
	 * Sort + compute scales
	 * 
	 * @param parameters
	 */
	public static void ComputeImpactValue(List<ImpactParameter> parameters) {
		ImpactParameter.ComputeScales(parameters);
	}

	/**
	 * Sort + compute scales
	 * 
	 * @param parameters
	 */
	public static void ComputeLikehoodValue(List<LikelihoodParameter> parameters) {
		LikelihoodParameter.ComputeScales(parameters);
	}

}
