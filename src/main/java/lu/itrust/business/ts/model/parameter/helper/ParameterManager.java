/**
 * 
 */
package lu.itrust.business.ts.model.parameter.helper;

import java.util.List;

import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;

/**
 * The ParameterManager class provides methods for computing impact and likelihood values for a list of parameters.
 */
public class ParameterManager {

	/**
	 * Sorts and computes scales for a list of ImpactParameters.
	 * 
	 * @param parameters the list of ImpactParameters to compute scales for
	 */
	public static void ComputeImpactValue(List<ImpactParameter> parameters) {
		ImpactParameter.ComputeScales(parameters);
	}

	/**
	 * Sorts and computes scales for a list of LikelihoodParameters.
	 * 
	 * @param parameters the list of LikelihoodParameters to compute scales for
	 */
	public static void ComputeLikehoodValue(List<LikelihoodParameter> parameters) {
		LikelihoodParameter.ComputeScales(parameters);
	}

}
