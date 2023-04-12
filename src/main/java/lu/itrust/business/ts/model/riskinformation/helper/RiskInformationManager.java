/**
 * 
 */
package lu.itrust.business.ts.model.riskinformation.helper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.itrust.business.ts.model.riskinformation.RiskInformation;

/**
 * @author eomar
 *
 */
public class RiskInformationManager {

	public static Map<String, List<RiskInformation>> Split(List<RiskInformation> riskInformations) {
		return riskInformations.stream().sorted(new RiskInformationComparator()).collect(Collectors.groupingBy(RiskInformation::getMainCategory));
	}

	public static Map<String, List<RiskInformation>> Split(List<RiskInformation> riskInformations, String category) {
		return riskInformations.stream().filter(riskInformation -> riskInformation.isMatch(category)).sorted(new RiskInformationComparator())
				.collect(Collectors.groupingBy(RiskInformation::getMainCategory));
	}
}
