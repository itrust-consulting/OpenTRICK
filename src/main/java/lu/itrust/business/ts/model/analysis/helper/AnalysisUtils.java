/**
 * 
 */
package lu.itrust.business.ts.model.analysis.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.Transient;

import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.iteminformation.ItemInformation;
import lu.itrust.business.ts.model.parameter.IParameter;

/**
 * The AnalysisUtils class provides utility methods for mapping and splitting assessments and item informations.
 */
public final class AnalysisUtils {

	/**
	 * Maps the selected assessments based on asset and scenario IDs.
	 * 
	 * @param assessments2 the list of assessments to be mapped
	 * @return an array of two maps, where the first map contains assessments mapped by asset ID
	 *         and the second map contains assessments mapped by scenario ID
	 */
	@Transient
	@SuppressWarnings("unchecked")
	public static Map<Integer, List<Assessment>>[] MappedSelectedAssessment(List<Assessment> assessments2) {
		final Map<Integer, List<Assessment>>[] mappings = new LinkedHashMap[] { new LinkedHashMap<>(),
				new LinkedHashMap<>() };
		for (Assessment assessment : assessments2) {
			if (!assessment.isSelected())
				continue;
			mappings[0].computeIfAbsent(assessment.getAsset().getId(), e -> new ArrayList<>()).add(assessment);
			mappings[1].computeIfAbsent(assessment.getScenario().getId(), e -> new ArrayList<>()).add(assessment);
		}
		return mappings;
	}

	/**
	 * Maps the selected assessments by asset.
	 *
	 * @param assessments2 the list of assessments
	 * @return a map where the key is the asset ID and the value is a list of selected assessments
	 */
	@Transient
	public static Map<Integer, List<Assessment>> MappedSelectedAssessmentByAsset(List<Assessment> assessments2) {
		return assessments2.stream().filter(Assessment::isSelected)
				.collect(Collectors.groupingBy(e -> e.getAsset().getId()));
	}

	/**
	 * Maps the selected assessments by scenario.
	 *
	 * @param assessments2 the list of assessments
	 * @return a map where the key is the scenario ID and the value is a list of selected assessments for that scenario
	 */
	@Transient
	public static Map<Integer, List<Assessment>> MappedSelectedAssessmentByScenario(List<Assessment> assessments2) {
		return assessments2.stream().filter(Assessment::isSelected)
				.collect(Collectors.groupingBy(e -> e.getScenario().getId()));
	}

	/**
	 * Splits a list of ItemInformation objects into two separate lists based on their type.
	 *
	 * @param itemInformations The list of ItemInformation objects to be split.
	 * @return An array of two lists, where the first list contains ItemInformation objects with type "scope",
	 *         and the second list contains ItemInformation objects with other types.
	 */
	@Transient
	@SuppressWarnings("unchecked")
	public static List<ItemInformation>[] SplitItemInformations(List<ItemInformation> itemInformations) {
		final List<?>[] splits = new List<?>[] { new ArrayList<>(), new ArrayList<>() };
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
		return parameters.values().stream().flatMap(Collection::stream)
				.collect(Collectors.groupingBy(IParameter::getTypeName));
	}

}
