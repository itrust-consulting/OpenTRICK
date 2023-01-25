/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAssessment;
import lu.itrust.business.TS.database.dao.DAOImpactParameter;
import lu.itrust.business.TS.database.dao.DAOScaleType;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.impl.RealValue;
import lu.itrust.business.TS.model.parameter.value.impl.Value;
import lu.itrust.business.TS.model.scale.ScaleType;

/**
 * @author eomar
 *
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
