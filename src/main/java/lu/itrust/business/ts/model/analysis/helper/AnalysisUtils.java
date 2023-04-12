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

	@Transient
	public static Map<Integer, List<Assessment>> MappedSelectedAssessmentByAsset(List<Assessment> assessments2) {
		return assessments2.stream().filter(Assessment::isSelected)
				.collect(Collectors.groupingBy(e -> e.getAsset().getId()));
	}

	@Transient
	public static Map<Integer, List<Assessment>> MappedSelectedAssessmentByScenario(List<Assessment> assessments2) {
		return assessments2.stream().filter(Assessment::isSelected)
				.collect(Collectors.groupingBy(e -> e.getScenario().getId()));
	}

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
