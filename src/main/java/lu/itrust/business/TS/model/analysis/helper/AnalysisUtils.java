/**
 * 
 */
package lu.itrust.business.TS.model.analysis.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Transient;

import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * @author eomar
 *
 */
public final class AnalysisUtils {

	/**
	 * Mapping selected assessment by asset and scenario
	 * 
	 * @return Length : 2, 0 : Asset, 1 : Scenario
	 */
	@Transient
	@SuppressWarnings("unchecked")
	public static Map<Integer, List<Assessment>>[] MappedSelectedAssessment(List<Assessment> assessments2) {
		Map<Integer, List<Assessment>>[] mappings = new LinkedHashMap[2];
		for (int i = 0; i < mappings.length; i++)
			mappings[i] = new LinkedHashMap<Integer, List<Assessment>>();
		for (Assessment assessment : assessments2) {
			if (!assessment.isSelected())
				continue;
			Asset asset = assessment.getAsset();
			List<Assessment> assessments = mappings[0].get(asset.getId());
			if (assessments == null)
				mappings[0].put(asset.getId(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
			Scenario scenario = assessment.getScenario();
			assessments = mappings[1].get(scenario.getId());
			if (assessments == null)
				mappings[1].put(scenario.getId(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
		}
		return mappings;
	}

	@Transient
	public static Map<Integer, List<Assessment>> MappedSelectedAssessmentByAsset(List<Assessment> assessments2) {
		Map<Integer, List<Assessment>> mappings = new LinkedHashMap<>();
		for (Assessment assessment : assessments2) {
			if (!assessment.isSelected())
				continue;
			Asset asset = assessment.getAsset();
			List<Assessment> assessments = mappings.get(asset.getId());
			if (assessments == null)
				mappings.put(asset.getId(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
		}
		return mappings;
	}

	@Transient
	public static Map<Integer, List<Assessment>> MappedSelectedAssessmentByScenario(List<Assessment> assessments2) {
		Map<Integer, List<Assessment>> mappings = new LinkedHashMap<>();
		for (Assessment assessment : assessments2) {
			if (!assessment.isSelected())
				continue;
			Scenario scenario = assessment.getScenario();
			List<Assessment> assessments = mappings.get(scenario.getId());
			if (assessments == null)
				mappings.put(scenario.getId(), assessments = new ArrayList<Assessment>());
			assessments.add(assessment);
		}
		return mappings;
	}

	@Transient
	@SuppressWarnings("unchecked")
	public static List<ItemInformation>[] SplitItemInformations(List<ItemInformation> itemInformations) {
		List<?>[] splits = new List<?>[2];
		splits[0] = new ArrayList<ItemInformation>();
		splits[1] = new ArrayList<ItemInformation>();
		for (ItemInformation itemInformation : itemInformations) {
			if (itemInformation.getType().equalsIgnoreCase("scope"))
				((List<ItemInformation>) splits[0]).add(itemInformation);
			else
				((List<ItemInformation>) splits[1]).add(itemInformation);
		}
		return (List<ItemInformation>[]) splits;
	}

	/**
	 * Retrieves parameter by type
	 * 
	 * @param parameters
	 * @return Map<String, SimpleParameter>
	 */
	@Transient
	public static Map<String, List<IParameter>> SplitParameters(List<? extends IParameter> parameters) {
		return parameters.stream().collect(Collectors.groupingBy(IParameter::getTypeName));
	}

	/**
	 * Retrieves parameter by type
	 * 
	 * @param parameters
	 * @return Map<String, SimpleParameter>
	 */
	@Transient
	public static Map<String, List<IParameter>> SplitParameters(Map<String, List<? extends IParameter>> parameters) {
		return parameters.values().stream().flatMap(a -> a.stream()).collect(Collectors.groupingBy(IParameter::getTypeName));
	}

}
