/**
 * 
 */
package lu.itrust.business.ts.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOAssessment;
import lu.itrust.business.ts.database.dao.DAOImpactParameter;
import lu.itrust.business.ts.database.dao.DAOScaleType;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.parameter.value.impl.RealValue;
import lu.itrust.business.ts.model.parameter.value.impl.Value;
import lu.itrust.business.ts.model.scale.ScaleType;

/**
 * @author eomar
 *
 */

/**
 * The AnalysisImpactManager class is responsible for managing the impact scale of an analysis.
 * It provides methods for adding and removing impact scales, as well as updating the assessment and risk profile.
 */
@Component
public class AnalysisImpactManager {

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private DAOAssessment daoAssessment;

	@Autowired
	private DAOImpactParameter daoImpactParameter;

	@Autowired
	private DAOScaleType daoScaleType;
	
	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	/**
	 * Manages the impact scale for a given analysis by updating the impact parameters based on the provided map of impacts.
	 * 
	 * @param idAnalysis The ID of the analysis.
	 * @param impacts    A map of impact IDs and their corresponding boolean values indicating whether the impact is present or not.
	 * @return           A boolean value indicating whether any changes were made to the impact scale.
	 */
	@Transactional
	public boolean manageImpactScaleSave(Integer idAnalysis, Map<Integer, Boolean> impacts) {
		Analysis analysis = daoAnalysis.get(idAnalysis);
		Map<Integer, List<ImpactParameter>> parameters = analysis.getImpactParameters().stream()
				.collect(Collectors.groupingBy(impactParameter -> impactParameter.getType().getId()));
		Map<Integer, String> labels = parameters.entrySet().stream().map(Entry::getValue).findAny().orElse(Collections.emptyList()).stream()
				.collect(Collectors.toMap(ImpactParameter::getLevel, ImpactParameter::getLabel));
		int maxLevel = analysis.getLikelihoodParameters().size();
		double maxValue = analysis.getImpactParameters().parallelStream().mapToDouble(ImpactParameter::getValue).max().orElse(300000);
		boolean[] change = new boolean[] { false };
		impacts.forEach((id, present) -> {
			if (parameters.containsKey(id) && !present)
				change[0] |= removeImpactScale(parameters.get(id), analysis);
			else if (present)
				change[0] |= addImpactScale(id, maxLevel, maxValue, labels, analysis);
		});
		if (change[0]) {
			analysis.updateType();
			assessmentAndRiskProfileManager.updateAssessment(analysis, null);
			daoAnalysis.saveOrUpdate(analysis);
			parameters.entrySet().stream().filter(entry -> !impacts.getOrDefault(entry.getKey(), true)).forEach(entry -> daoImpactParameter.delete(entry.getValue()));
		}
		return change[0];
	}

	/**
	 * Adds impact scales to the analysis based on the given parameters.
	 *
	 * @param id      The ID of the scale type.
	 * @param maxLevel The maximum level of the impact scale.
	 * @param maxValue The maximum value of the impact scale.
	 * @param labels   The map of labels for each level of the impact scale.
	 * @param analysis The analysis object to which the impact scales will be added.
	 * @return True if the impact scales were successfully added, false otherwise.
	 */
	private boolean addImpactScale(Integer id, int maxLevel, double maxValue, Map<Integer, String> labels, Analysis analysis) {
		ScaleType scaleType = daoScaleType.findOne(id);
		List<ImpactParameter> impacts = new ArrayList<>(maxLevel);
		double currentValue = maxValue;
		if (maxLevel % 2 == 0) {
			for (int level = maxLevel - 1; level >= 0; level--) {
				if (impacts.isEmpty())
					impacts.add(new ImpactParameter(scaleType, level, scaleType.getAcronym() + level, currentValue));
				else
					impacts.add(new ImpactParameter(scaleType, level, scaleType.getAcronym() + level, currentValue *= 0.5));
			}
		} else {
			ImpactParameter prev = null;
			for (int level = maxLevel - 2; level > 0; level -= 2) {
				ImpactParameter current = new ImpactParameter(scaleType, level, scaleType.getAcronym() + level),
						next = prev == null ? new ImpactParameter(scaleType, level + 1, scaleType.getAcronym() + (level + 1), currentValue) : prev;
				if (prev == null)
					impacts.add(next);
				prev = new ImpactParameter(scaleType, level - 1, scaleType.getAcronym() + (level - 1));
				prev.setValue(currentValue *= 0.5);
				impacts.add(current);
				impacts.add(prev);
				current.setValue(Math.sqrt(next.getValue() * prev.getValue()));
			}
		}

		if (impacts.isEmpty())
			return false;
		ImpactParameter.ComputeScales(impacts);
		ImpactParameter impactParameter = impacts.get(0);
		if (impactParameter.isMatch(Constant.DEFAULT_IMPACT_NAME))
			analysis.getAssessments().stream().forEach(assessment -> assessment.getImpacts().add(new RealValue(0d, impactParameter)));
		else
			analysis.getAssessments().stream().forEach(assessment -> assessment.getImpacts().add(new Value(impactParameter)));
		impacts.forEach(impact -> {
			impact.setLabel(labels.getOrDefault(impact.getLevel(), ""));
			analysis.getImpactParameters().add(impact);
		});
		return !impacts.isEmpty();
	}

	/**
	 * Removes the specified impact parameters from the analysis.
	 * 
	 * @param parameters the list of impact parameters to be removed
	 * @param analysis the analysis from which the impact parameters should be removed
	 * @return true if the impact parameters were successfully removed, false otherwise
	 */
	private boolean removeImpactScale(List<ImpactParameter> parameters, Analysis analysis) {
		if (parameters == null || parameters.isEmpty())
			return true;
		analysis.getImpactParameters().removeAll(parameters);
		ScaleType scaleType = parameters.get(0).getType();
		analysis.getAssessments().forEach(assessment -> {
			IValue value = assessment.getImpacts().stream().filter(impact -> impact.getName().equals(scaleType.getName())).findAny().orElse(null);
			if (value != null) {
				assessment.getImpacts().remove(value);
				daoAssessment.delete(value);
			}
		});
		analysis.getRiskProfiles().forEach(riskProfile -> riskProfile.remove(scaleType));

		return true;
	}

}
