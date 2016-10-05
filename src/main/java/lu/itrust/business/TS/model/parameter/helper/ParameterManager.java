/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper;

import java.util.List;

import lu.itrust.business.TS.model.parameter.ExtendedParameter;

/**
 * @author eom
 * 
 */
public class ParameterManager {

	/**
	 * Sort + compute scales
	 * @param parameters
	 */
	public static void ComputeImpactValue(List<ExtendedParameter> parameters) {
		parameters.sort((p1, p2) -> Integer.compare(p1.getLevel(), p2.getLevel()));
		int limit = parameters.size() - 1;
		for (int i = 0; i < limit; i++) {
			if ((i % 2) != 0)
				ExtendedParameter.ComputeScales(parameters.get(i),
						parameters.get(i - 1), parameters.get(i + 1));
		}

	}
}
