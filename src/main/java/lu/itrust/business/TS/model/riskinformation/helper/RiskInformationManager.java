/**
 * 
 */
package lu.itrust.business.TS.model.riskinformation.helper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.itrust.business.TS.model.riskinformation.RiskInformation;

/**
 * @author eomar
 *
 */
public class RiskInformationManager {
	
	public static Map<String, List<RiskInformation>> Split(List<RiskInformation> riskInformations){
		return riskInformations.stream().sorted(new RiskInformationComparator()).collect(Collectors.groupingBy(riskInformation -> riskInformation.getCategory().startsWith("Risk_TB") ? "Risk" : riskInformation.getCategory()));
	}
}
