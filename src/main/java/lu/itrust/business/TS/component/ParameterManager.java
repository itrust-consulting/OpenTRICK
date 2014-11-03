/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.List;

import lu.itrust.business.TS.data.basic.ExtendedParameter;

/**
 * @author eom
 * 
 */
public class ParameterManager {

	public static void ComputeImpactValue(List<ExtendedParameter> parameters) {
		int limit = parameters.size() - 1;
		for (int i = 0; i < limit; i++) {
			if ((i % 2) != 0)
				ExtendedParameter.ComputeScales(parameters.get(i),
						parameters.get(i - 1), parameters.get(i + 1));
		}

	}
}
