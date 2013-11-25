/**
 * 
 */
package lu.itrust.business.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.History;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.MaturityMeasure;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.tsconstant.Constant;

import org.springframework.stereotype.Component;

/**
 * @author eom
 * 
 */
@Component
public class Duplicator {

	public Analysis duplicate(Analysis analysis)
			throws CloneNotSupportedException {

		Map<Integer, Phase> phases = new LinkedHashMap<>();

		Map<Integer, Scenario> scenarios = new LinkedHashMap<Integer, Scenario>(
				analysis.getScenarios().size());

		Map<Integer, Asset> assets = new LinkedHashMap<Integer, Asset>(analysis
				.getAssets().size());

		Map<Integer, Parameter> parameters = new LinkedHashMap<>(analysis
				.getParameters().size());

		try {
			Analysis copy = analysis.duplicate();

			copy.setHistories(new ArrayList<History>(analysis.getHistories()
					.size()));
			for (History history : analysis.getHistories())
				copy.getHistories().add(history.duplicate());

			copy.setItemInformations(new ArrayList<ItemInformation>(analysis
					.getItemInformations().size()));
			for (ItemInformation itemInformation : analysis
					.getItemInformations())
				copy.getItemInformations().add(itemInformation.duplicate());

			copy.setParameters(new ArrayList<Parameter>(analysis
					.getParameters().size()));
			for (Parameter parameter : analysis.getParameters()) {
				parameters.put(parameter.getId(), parameter.duplicate());
				copy.getParameters().add(parameters.get(parameter.getId()));
			}

			copy.setRiskInformations(new ArrayList<RiskInformation>(analysis
					.getRiskInformations().size()));
			for (RiskInformation riskInformation : analysis
					.getRiskInformations())
				copy.getRiskInformations().add(riskInformation.duplicate());

			copy.setScenarios(new ArrayList<Scenario>(analysis.getScenarios()
					.size()));
			for (Scenario scenario : analysis.getScenarios()) {
				scenarios.put(scenario.getId(), scenario.duplicate());
				copy.getScenarios().add(scenarios.get(scenario.getId()));
			}

			copy.setAssets(new ArrayList<Asset>(analysis.getAssets().size()));
			for (Asset asset : analysis.getAssets()) {
				assets.put(asset.getId(), asset.duplicate());
				copy.getAssets().add(assets.get(asset.getId()));
			}

			copy.setAssessments(new ArrayList<Assessment>(analysis
					.getAssessments().size()));

			for (Assessment assessment : analysis.getAssessments()) {
				Assessment clone = assessment.duplicate();
				clone.setScenario(scenarios.get(assessment.getScenario()
						.getId()));
				clone.setAsset(assets.get(assessment.getAsset().getId()));
				copy.getAssessments().add(clone);
			}

			copy.setUsedPhases(new ArrayList<Phase>(analysis.getUsedPhases()
					.size()));

			for (Phase phase : analysis.getUsedPhases()) {
				phases.put(phase.getId(), phase.duplicate());
				copy.addUsedPhase(phases.get(phase.getId()));
			}

			Norm customNorm = new Norm("Custom");

			copy.setAnalysisNorms(new ArrayList<AnalysisNorm>(analysis
					.getAnalysisNorms().size()));

			for (AnalysisNorm analysisNorm : analysis.getAnalysisNorms())
				copy.addAnalysisNorm(duplicate(analysisNorm, phases,
						customNorm, parameters));
			return copy;
		} finally {
			scenarios.clear();
			assets.clear();
			phases.clear();
			parameters.clear();
		}
	}

	public AnalysisNorm duplicate(AnalysisNorm analysisNorm,
			Map<Integer, Phase> phases, Norm customNorm,
			Map<Integer, Parameter> parameters)
			throws CloneNotSupportedException {
		AnalysisNorm norm = (AnalysisNorm) analysisNorm.duplicate();
		if (norm.getNorm().getLabel().equalsIgnoreCase(Constant.NORM_CUSTOM))
			norm.setNorm(customNorm);
		List<Measure> measures = new ArrayList<>(analysisNorm.getMeasures()
				.size());
		for (Measure measure : analysisNorm.getMeasures())
			measures.add(duplicate(measure,
					phases.get(measure.getPhase().getId()), norm, parameters));
		norm.setMeasures(measures);
		return norm;
	}

	public Measure duplicate(Measure measure, Phase phase, AnalysisNorm norm,
			Map<Integer, Parameter> parameters)
			throws CloneNotSupportedException {
		Measure copy = measure.duplicate();
		copy.setAnalysisNorm(norm);
		copy.setPhase(phase);
		if (norm.getNorm().getLabel().equalsIgnoreCase(Constant.NORM_CUSTOM))
			copy.setMeasureDescription(duplicate(
					measure.getMeasureDescription(), copy));
		else if (copy instanceof MaturityMeasure)
			copy.setImplementationRate(parameters.get(((Parameter) measure
					.getImplementationRate()).getId()));
		return copy;
	}

	public MeasureDescription duplicate(MeasureDescription measureDescription,
			Measure measure) throws CloneNotSupportedException {
		MeasureDescription description = measureDescription.duplicate();
		description.setNorm(measure.getAnalysisNorm().getNorm());
		description
				.setMeasureDescriptionTexts(new ArrayList<MeasureDescriptionText>(
						measureDescription.getMeasureDescriptionTexts().size()));
		for (MeasureDescriptionText measureDescriptionText : measureDescription
				.getMeasureDescriptionTexts())
			description.getMeasureDescriptionTexts().add(
					duplicate(measureDescriptionText, description));
		return description;
	}

	public MeasureDescriptionText duplicate(
			MeasureDescriptionText measureDescriptionText,
			MeasureDescription description) throws CloneNotSupportedException {
		MeasureDescriptionText descriptionText = measureDescriptionText
				.duplicate();
		descriptionText.setMeasureDescription(description);
		return descriptionText;
	}

}
