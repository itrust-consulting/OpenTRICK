package lu.itrust.business.component;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.History;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.MaturityMeasure;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureProperties;
import lu.itrust.business.TS.NormMeasure;
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
 * Duplicator.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2014
 */
@Component
public class Duplicator {

	/**
	 * duplicateAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param copy
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Analysis duplicateAnalysis(Analysis analysis, Analysis copy) throws CloneNotSupportedException {

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
				phases.put(phase.getNumber(), phase.duplicate());
				copy.addUsedPhase(phases.get(phase.getNumber()));
			}

			copy.setAnalysisNorms(new ArrayList<AnalysisNorm>());
			
			for (AnalysisNorm analysisNorm : analysis.getAnalysisNorms())
				copy.addAnalysisNorm(duplicateAnalysisNorm(analysisNorm, phases, parameters, false));
			return copy;
		} finally {
			scenarios.clear();
			assets.clear();
			phases.clear();
			parameters.clear();
		}
	}

	/**
	 * duplicateAnalysisNorm: <br>
	 * Description
	 * 
	 * @param analysisNorm
	 * @param phases
	 * @param customNorm
	 * @param parameters
	 * @param anonymize
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public AnalysisNorm duplicateAnalysisNorm(AnalysisNorm analysisNorm, Map<Integer, Phase> phases, Map<Integer, Parameter> parameters, boolean anonymize) throws CloneNotSupportedException {
		AnalysisNorm norm = (AnalysisNorm) analysisNorm.duplicate();

		List<Measure> measures = new ArrayList<>(analysisNorm.getMeasures().size());
		for (Measure measure : analysisNorm.getMeasures())
			if (anonymize)
				measures.add(duplicateMeasure(measure, phases.get(Constant.PHASE_DEFAULT), norm, parameters, anonymize));
			else
				measures.add(duplicateMeasure(measure, phases.get(measure.getPhase().getNumber()), norm, parameters, anonymize));
		norm.setMeasures(measures);
		return norm;
	}

	/**
	 * duplicateMeasure: <br>
	 * Description
	 * 
	 * @param measure
	 * @param phase
	 * @param norm
	 * @param parameters
	 * @param anonymize
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Measure duplicateMeasure(Measure measure, Phase phase, AnalysisNorm norm, Map<Integer, Parameter> parameters, boolean anonymize) throws CloneNotSupportedException {
		Measure copy = measure.duplicate();
		copy.setAnalysisNorm(norm);
		copy.setPhase(phase);

		// get default implementationrate as parameter (0%)
		Parameter imprate = null;
		List<Parameter> parameterlist = new ArrayList<Parameter>(parameters.values());
		for (Parameter param : parameterlist) {
			if (param.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME) && param.getValue() == 0) {
				imprate = param;
				break;
			}
		}

		if (anonymize) {
			copy.setComment(Constant.EMPTY_STRING);
			copy.setToDo(Constant.EMPTY_STRING);
			copy.setCost(0);
			copy.setExternalWL(0);
			copy.setInternalWL(0);
			copy.setInvestment(0);
			copy.setLifetime(0);
			copy.setMaintenance(0);
		}
		if (copy instanceof MaturityMeasure) {
			MaturityMeasure matmeasure = (MaturityMeasure) copy;
			matmeasure.setImplementationRate(imprate);
			matmeasure.setReachedLevel(1);
			matmeasure.setSML1Cost(0);
			matmeasure.setSML2Cost(0);
			matmeasure.setSML3Cost(0);
			matmeasure.setSML4Cost(0);
			matmeasure.setSML5Cost(0);
		} else {
			NormMeasure normmeasure = (NormMeasure) copy;
			if (anonymize) {
				normmeasure.setToCheck(Constant.EMPTY_STRING);
				normmeasure.setImplementationRate(0);
				normmeasure.setMeasurePropertyList((MeasureProperties) normmeasure.getMeasurePropertyList().duplicate());
				normmeasure.getMeasurePropertyList().setSoaComment(Constant.EMPTY_STRING);
				normmeasure.getMeasurePropertyList().setSoaReference(Constant.EMPTY_STRING);
				normmeasure.getMeasurePropertyList().setSoaRisk(Constant.EMPTY_STRING);
				NormMeasure tmpnormmeasure = (NormMeasure) measure;
				for (AssetTypeValue atv : tmpnormmeasure.getAssetTypeValues()) {
					normmeasure.addAnAssetTypeValue(atv.duplicate());
				}
			}
		}
		return copy;
	}

	/**
	 * createProfile: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param analysisProfile
	 * @param serviceTaskFeedback
	 * @param idTask
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Analysis createProfile(Analysis analysis, AnalysisProfile analysisProfile, ServiceTaskFeedback serviceTaskFeedback, long idTask) {

		try {

			Map<Integer, Parameter> parameters = new LinkedHashMap<>();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.start", "Duplicate analysis base information", 2));

			// duplicate the analysis
			Analysis copy = analysis.duplicate();

			// set basic values
			copy.setVersion("0.0.1");
			copy.setBasedOnAnalysis(null);
			copy.setIdentifier(analysisProfile.getName());
			copy.setCreationDate(new Timestamp(System.currentTimeMillis()));
			copy.setLabel(analysisProfile.getComment());
			copy.setProfile(true);
			copy.setData(false);

			// do not set analysis specific data which are unused for profile

			// history
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.delete.history", "Delete analysis histories", 3));
			copy.setHistories(null);

			// analysis rights
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.delete.right", "Delete analysis rigths", 4));
			copy.setUserRights(null);

			// assets
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.asset", "empty assets", 30));
			copy.setAssets(null);

			// assessments
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.assessment", "empty assessments", 35));
			copy.setAssessments(null);

			// item information
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.itemInformation", "Empty item information", 10));
			copy.setItemInformations(null);

			// risk information
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.riskInformation", "empty risk information", 20));
			copy.setRiskInformations(null);

			// actionplans
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.actionplan", "empty actionplans and summaries", 20));
			copy.setActionPlans(null);
			copy.setSummaries(null);

			// risk register
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.riskregister", "empty risk register", 20));
			copy.setRiskRegisters(null);

			// copy nessesary data to profile

			// parameters
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.parameter", "Copy parameters", 15));
			copy.setParameters(new ArrayList<Parameter>(analysis.getParameters().size()));
			for (Parameter parameter : analysis.getParameters()) {
				parameters.put(parameter.getId(), parameter.duplicate());
				copy.getParameters().add(parameters.get(parameter.getId()));
			}

			// phases
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.phase", "empty phases", 44));

			copy.setUsedPhases(new ArrayList<Phase>());
			
			Map<Integer, Phase> phases = new LinkedHashMap<Integer, Phase>();
			
			for(Phase phase : analysis.getUsedPhases()){
				if(phase.getNumber()==Constant.PHASE_DEFAULT){
					Phase tmpPhase = phase.duplicate();
					
//					Calendar cal = Calendar.getInstance();
//					
//					Date date = new Date(cal.getTime().getTime());
//					
//					tmpPhase.setBeginDate(date);
//					
//					cal.add(Calendar.YEAR, 1);
//					
//					date = new Date(cal.getTime().getTime());
//					
//					tmpPhase.setEndDate(date);
//					
					phases.put(Constant.PHASE_DEFAULT, tmpPhase);
					copy.addUsedPhase(phases.get(Constant.PHASE_DEFAULT));
				}
					
			}
			
			
			// copy other data if requested

			// scenarios
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.scenario", "Copy scenarios", 25));
			if (analysisProfile.isScenario()) {

				copy.setScenarios(new ArrayList<Scenario>(analysis.getScenarios().size()));
				for (Scenario scenario : analysis.getScenarios()) {
					Scenario tmpscenario = scenario.duplicate();
					for (AssetTypeValue atv : scenario.getAssetTypeValues())
						tmpscenario.addAssetTypeValue(atv.duplicate());
					copy.getScenarios().add(tmpscenario);
				}
			} else
				copy.setScenarios(null);

			// norms
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.measure", "Copy norms", 45));

			int normSize = analysis.getAnalysisNorms().size();

			int copyCount = 0;

			int diviser = normSize * 50;

			copy.setAnalysisNorms(new ArrayList<AnalysisNorm>(normSize));

			if (analysisProfile.getNorms() != null && !analysisProfile.getNorms().isEmpty()) {
				for (AnalysisNorm analysisNorm : analysis.getAnalysisNorms()) {
					if (analysisProfile.getNorms().contains(analysisNorm.getNorm()))
						copy.addAnalysisNorm(duplicateAnalysisNorm(analysisNorm, phases, parameters, true));
					serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.measure", "Copy norms", (copyCount++ / diviser) * 50 + 45));
				}
			}
			return copy;
		} catch (CloneNotSupportedException cex) {
			cex.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
