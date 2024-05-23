/**
 * 
 */
package lu.itrust.business.ts.model.riskinformation.helper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.itrust.business.ts.model.riskinformation.RiskInformation;


/**
 * The RiskInformationManager class provides methods to split a list of RiskInformation objects based on different criteria.
 */
public class RiskInformationManager {

	/**
	 * Splits the given list of RiskInformation objects based on their main category.
	 *
	 * @param riskInformations The list of RiskInformation objects to be split.
	 * @return A map where the keys are the main categories and the values are lists of RiskInformation objects belonging to each category.
	 */
	public static Map<String, List<RiskInformation>> Split(List<RiskInformation> riskInformations) {
		return riskInformations.stream().sorted(new RiskInformationComparator()).collect(Collectors.groupingBy(RiskInformation::getMainCategory));
	}

	/**
	 * Splits the given list of RiskInformation objects based on their main category and a specific category.
	 *
	 * @param riskInformations The list of RiskInformation objects to be split.
	 * @param category         The specific category to filter the RiskInformation objects.
	 * @return A map where the keys are the main categories and the values are lists of RiskInformation objects belonging to each category.
	 */
	public static Map<String, List<RiskInformation>> Split(List<RiskInformation> riskInformations, String category) {
		return riskInformations.stream().filter(riskInformation -> riskInformation.isMatch(category)).sorted(new RiskInformationComparator())
				.collect(Collectors.groupingBy(RiskInformation::getMainCategory));
	}
}
