/**
 * 
 */
package lu.itrust.business.component;

import java.sql.Timestamp;
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
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.helper.AnalysisProfile;
import lu.itrust.business.service.ServiceTaskFeedback;

import org.springframework.stereotype.Component;

/**
 * @author eom
 * 
 */
@Component
public class Duplicator {

	public Analysis duplicate(Analysis analysis, Analysis copy) throws CloneNotSupportedException {

		Map<Integer, Phase> phases = new LinkedHashMap<>();

		Map<Integer, Scenario> scenarios = new LinkedHashMap<Integer, Scenario>(analysis.getScenarios().size());

		Map<Integer, Asset> assets = new LinkedHashMap<Integer, Asset>(analysis.getAssets().size());

		Map<Integer, Parameter> parameters = new LinkedHashMap<>(analysis.getParameters().size());

		try {
			copy = analysis.duplicateTo(copy);

			copy.setUserRights(new ArrayList<UserAnalysisRight>(analysis.getUserRights().size()));
			for (UserAnalysisRight uar : analysis.getUserRights()) {
				UserAnalysisRight uarcopy = uar.duplicate();
				uarcopy.setAnalysis(copy);
				copy.addUserRight(uarcopy);
			}

			copy.setHistories(new ArrayList<History>(analysis.getHistories().size()));
			for (History history : analysis.getHistories())
				copy.getHistories().add(history.duplicate());

			copy.setItemInformations(new ArrayList<ItemInformation>(analysis.getItemInformations().size()));
			for (ItemInformation itemInformation : analysis.getItemInformations())
				copy.getItemInformations().add(itemInformation.duplicate());

			copy.setParameters(new ArrayList<Parameter>(analysis.getParameters().size()));
			for (Parameter parameter : analysis.getParameters()) {
				parameters.put(parameter.getId(), parameter.duplicate());
				copy.getParameters().add(parameters.get(parameter.getId()));
			}

			copy.setRiskInformations(new ArrayList<RiskInformation>(analysis.getRiskInformations().size()));
			for (RiskInformation riskInformation : analysis.getRiskInformations())
				copy.getRiskInformations().add(riskInformation.duplicate());

			copy.setScenarios(new ArrayList<Scenario>(analysis.getScenarios().size()));
			for (Scenario scenario : analysis.getScenarios()) {
				scenarios.put(scenario.getId(), scenario.duplicate());
				copy.getScenarios().add(scenarios.get(scenario.getId()));
			}

			copy.setAssets(new ArrayList<Asset>(analysis.getAssets().size()));
			for (Asset asset : analysis.getAssets()) {
				assets.put(asset.getId(), asset.duplicate());
				copy.getAssets().add(assets.get(asset.getId()));
			}

			copy.setAssessments(new ArrayList<Assessment>(analysis.getAssessments().size()));

			for (Assessment assessment : analysis.getAssessments()) {
				Assessment clone = assessment.duplicate();
				clone.setScenario(scenarios.get(assessment.getScenario().getId()));
				clone.setAsset(assets.get(assessment.getAsset().getId()));
				copy.getAssessments().add(clone);
			}

			copy.setUsedPhases(new ArrayList<Phase>(analysis.getUsedPhases().size()));

			for (Phase phase : analysis.getUsedPhases()) {
				phases.put(phase.getId(), phase.duplicate());
				copy.addUsedPhase(phases.get(phase.getId()));
			}

			Norm customNorm = new Norm("Custom");

			copy.setAnalysisNorms(new ArrayList<AnalysisNorm>(analysis.getAnalysisNorms().size()));

			for (AnalysisNorm analysisNorm : analysis.getAnalysisNorms())
				copy.addAnalysisNorm(duplicate(analysisNorm, phases, customNorm, parameters));
			return copy;
		} finally {
			scenarios.clear();
			assets.clear();
			phases.clear();
			parameters.clear();
		}
	}

	public AnalysisNorm duplicate(AnalysisNorm analysisNorm, Map<Integer, Phase> phases, Norm customNorm, Map<Integer, Parameter> parameters) throws CloneNotSupportedException {
		AnalysisNorm norm = (AnalysisNorm) analysisNorm.duplicate();

		List<Measure> measures = new ArrayList<>(analysisNorm.getMeasures().size());
		for (Measure measure : analysisNorm.getMeasures())
			measures.add(duplicate(measure, phases.get(measure.getPhase().getId()), norm, parameters));
		norm.setMeasures(measures);
		return norm;
	}

	public Measure duplicate(Measure measure, Phase phase, AnalysisNorm norm, Map<Integer, Parameter> parameters) throws CloneNotSupportedException {
		Measure copy = measure.duplicate();
		copy.setAnalysisNorm(norm);
		copy.setPhase(phase);
		if (norm.getNorm().getLabel().equalsIgnoreCase(Constant.NORM_CUSTOM))
			copy.setMeasureDescription(duplicate(measure.getMeasureDescription(), copy));
		else if (copy instanceof MaturityMeasure)
			copy.setImplementationRate(parameters.get(((Parameter) measure.getImplementationRate()).getId()));
		return copy;
	}

	public MeasureDescription duplicate(MeasureDescription measureDescription, Measure measure) throws CloneNotSupportedException {
		MeasureDescription description = measureDescription.duplicate();
		description.setNorm(measure.getAnalysisNorm().getNorm());
		description.setMeasureDescriptionTexts(new ArrayList<MeasureDescriptionText>(measureDescription.getMeasureDescriptionTexts().size()));
		for (MeasureDescriptionText measureDescriptionText : measureDescription.getMeasureDescriptionTexts())
			description.getMeasureDescriptionTexts().add(duplicate(measureDescriptionText, description));
		return description;
	}

	public MeasureDescriptionText duplicate(MeasureDescriptionText measureDescriptionText, MeasureDescription description) throws CloneNotSupportedException {
		MeasureDescriptionText descriptionText = measureDescriptionText.duplicate();
		descriptionText.setMeasureDescription(description);
		return descriptionText;
	}

	public Analysis duplicate(Analysis analysis, AnalysisProfile analysisProfile, ServiceTaskFeedback serviceTaskFeedback, long idTask) throws CloneNotSupportedException {
		Map<Integer, Phase> phases = new LinkedHashMap<>();

		Map<Integer, Scenario> scenarios = new LinkedHashMap<Integer, Scenario>(analysis.getScenarios().size());

		Map<Integer, Asset> assets = new LinkedHashMap<Integer, Asset>(analysis.getAssets().size());

		Map<Integer, Parameter> parameters = new LinkedHashMap<>(analysis.getParameters().size());

		try {

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.start", "Duplicate analysis base information", 2));

			Analysis copy = analysis.duplicate();

			copy.setVersion("0.0.1");

			copy.setBasedOnAnalysis(null);

			copy.setIdentifier(analysisProfile.getName());

			copy.setCreationDate(new Timestamp(System.currentTimeMillis()));

			copy.setLabel(analysisProfile.getComment());

			copy.setProfile(true);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.delete.history", "Delete analysis histories", 3));

			copy.setHistories(null);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.delete.right", "Delete analysis rigths", 4));

			copy.setUserRights(null);

			copy.setData(analysis.getData()
					&& (analysisProfile.isAsset() || analysisProfile.isItemInformation() || analysisProfile.isParameter() || analysisProfile.isRiskInformation()
							|| analysisProfile.isScenario() || analysisProfile.getNorms() != null && !analysisProfile.getNorms().isEmpty()));
			
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.itemInformation", "Copy item information", 10));
			
			if (analysisProfile.isItemInformation()) {
				
				copy.setItemInformations(new ArrayList<ItemInformation>(analysis.getItemInformations().size()));
				for (ItemInformation itemInformation : analysis.getItemInformations())
					copy.getItemInformations().add(itemInformation.duplicate());
			} else
				copy.setItemInformations(null);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.parameter", "Copy parameters", 15));
			
			if (analysisProfile.isParameter()) {
				
				copy.setParameters(new ArrayList<Parameter>(analysis.getParameters().size()));
				for (Parameter parameter : analysis.getParameters()) {
					parameters.put(parameter.getId(), parameter.duplicate());
					copy.getParameters().add(parameters.get(parameter.getId()));
				}
			} else
				copy.setParameters(null);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.riskInformation", "Copy risk information", 20));
			
			if (analysisProfile.isRiskInformation()) {
				
				copy.setRiskInformations(new ArrayList<RiskInformation>(analysis.getRiskInformations().size()));
				for (RiskInformation riskInformation : analysis.getRiskInformations())
					copy.getRiskInformations().add(riskInformation.duplicate());
			} else
				copy.setRiskInformations(null);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.scenario", "Copy scenarios", 25));
			
			if (analysisProfile.isScenario()) {
				
				copy.setScenarios(new ArrayList<Scenario>(analysis.getScenarios().size()));
				for (Scenario scenario : analysis.getScenarios()) {
					scenarios.put(scenario.getId(), scenario.duplicate());
					copy.getScenarios().add(scenarios.get(scenario.getId()));
				}
			} else
				copy.setScenarios(null);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.asset", "Copy assets", 30));
			
			if (analysisProfile.isAsset()) {
				
				copy.setAssets(new ArrayList<Asset>(analysis.getAssets().size()));
				for (Asset asset : analysis.getAssets()) {
					assets.put(asset.getId(), asset.duplicate());
					copy.getAssets().add(assets.get(asset.getId()));
				}
			} else
				copy.setAssets(null);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.assessment", "Copy assessments", 35));
			
			if (analysisProfile.isScenario() && analysisProfile.isAsset()) {
				
				copy.setAssessments(new ArrayList<Assessment>(analysis.getAssessments().size()));

				for (Assessment assessment : analysis.getAssessments()) {
					Assessment clone = assessment.duplicate();
					clone.setScenario(scenarios.get(assessment.getScenario().getId()));
					clone.setAsset(assets.get(assessment.getAsset().getId()));
					copy.getAssessments().add(clone);
				}
			} else
				copy.setAssessments(null);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.phase", "Copy phases", 44));
			copy.setUsedPhases(new ArrayList<Phase>(analysis.getUsedPhases().size()));

			for (Phase phase : analysis.getUsedPhases()) {
				phases.put(phase.getId(), phase.duplicate());
				copy.addUsedPhase(phases.get(phase.getId()));
			}

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.measure", "Copy measures", 45));

			int measureSize = analysis.getAnalysisNorms().size();

			int copyCount = 0;

			int diviser = measureSize * 50;

			Norm customNorm = new Norm("Custom");

			copy.setAnalysisNorms(new ArrayList<AnalysisNorm>(measureSize));

			if (analysisProfile.getNorms() != null && !analysisProfile.getNorms().isEmpty()) {
				for (AnalysisNorm analysisNorm : analysis.getAnalysisNorms()) {
					if (analysisProfile.getNorms().contains(analysisNorm.getNorm()))
						copy.addAnalysisNorm(duplicate(analysisNorm, phases, customNorm, parameters));
					serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.measure", "Copy measures", (copyCount++ / diviser) * 50 + 45));
				}
			}
			return copy;
		} finally {
			scenarios.clear();
			assets.clear();
			phases.clear();
			parameters.clear();
		}
	}

}
