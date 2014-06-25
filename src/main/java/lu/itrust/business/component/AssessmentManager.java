package lu.itrust.business.component;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.component.helper.ALE;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.DAOAssessment;
import lu.itrust.business.dao.DAOAsset;
import lu.itrust.business.dao.DAOParameter;
import lu.itrust.business.dao.DAOScenario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * AssessmentManager.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since May 12, 2014
 */
@Component
public class AssessmentManager {

	@Autowired
	private DAOAssessment daoAssessment;

	@Autowired
	private DAOAsset daoAsset;

	@Autowired
	private DAOScenario daoScenario;

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private DAOParameter daoParameter;

	@Transactional
	public void selectAsset(Asset asset) throws Exception {
		asset.setSelected(true);
		List<Assessment> assessments = daoAssessment.getAllSelectedFromAsset(asset);
		for (Assessment assessment : assessments) {
			if (assessment.getScenario().isSelected() && assessment.getScenario().hasInfluenceOnAsset(asset.getAssetType())) {
				assessment.setSelected(true);
				daoAssessment.saveOrUpdate(assessment);
			}
		}
	}

	@Transactional
	public void selectAsset(int idAsset) throws Exception {
		Asset asset = daoAsset.get(idAsset);
		if (asset == null)
			return;
		selectAsset(asset);
	}

	@Transactional
	public void unSelectAsset(Asset asset) throws Exception {
		asset.setSelected(false);
		List<Assessment> assessments = daoAssessment.getAllSelectedFromAsset(asset);
		for (Assessment assessment : assessments) {
			if (assessment.isSelected()) {
				assessment.setSelected(false);
				daoAssessment.saveOrUpdate(assessment);
			}
		}
	}

	@Transactional
	public void unSelectAsset(int idAsset) throws Exception {
		Asset asset = daoAsset.get(idAsset);
		if (asset == null)
			return;
		unSelectAsset(asset);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////

	@Transactional
	public void selectScenario(Scenario scenario) throws Exception {
		scenario.setSelected(true);
		List<Assessment> assessments = daoAssessment.getAllUnselectedFromScenario(scenario);
		for (Assessment assessment : assessments) {
			if (assessment.getAsset().isSelected() && scenario.hasInfluenceOnAsset(assessment.getAsset().getAssetType())) {
				assessment.setSelected(true);
				daoAssessment.saveOrUpdate(assessment);
			}
		}
	}

	@Transactional
	public void selectScenario(int idScenario) throws Exception {
		Scenario scenario = daoScenario.get(idScenario);
		if (scenario == null)
			return;
		selectScenario(scenario);
	}

	@Transactional
	public void unSelectScenario(Scenario scenario) throws Exception {
		scenario.setSelected(false);
		List<Assessment> assessments = daoAssessment.getAllSelectedFromScenario(scenario);
		for (Assessment assessment : assessments) {
			if (assessment.isSelected()) {
				assessment.setSelected(false);
				daoAssessment.saveOrUpdate(assessment);
			}
		}
	}

	@Transactional
	public void unSelectScenario(int idScenario) throws Exception {
		Scenario scenario = daoScenario.get(idScenario);
		if (scenario == null)
			return;
		unSelectScenario(scenario);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////

	@Transactional
	public void build(Asset asset, int idAnalysis) throws Exception {
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			return;
		if (asset.getId() < 1)
			analysis.addAnAsset(asset);
		Map<Integer, Assessment> assetAssessments = analysis.findAssessmentByAssetId(asset.getId());
		List<Assessment> assessments = analysis.getAssessments();
		List<Scenario> scenarios = analysis.getScenarios();
		for (Scenario scenario : scenarios) {
			if (!assetAssessments.containsKey(scenario.getId())) {
				if (scenario.hasInfluenceOnAsset(asset.getAssetType()))
					assessments.add(new Assessment(asset, scenario));
			} else if (!scenario.hasInfluenceOnAsset(asset.getAssetType())) {
				Assessment assessment = assetAssessments.get(scenario.getId());
				assessments.remove(assessment);
				daoAssessment.delete(assessment);
			}
		}
		daoAnalysis.saveOrUpdate(analysis);
	}

	@Transactional
	public void build(Scenario scenario, int idAnalysis) throws Exception {
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			return;
		if (scenario.getId() < 1)
			analysis.addAScenario(scenario);
		List<Assessment> assessments = analysis.getAssessments();
		Map<Integer, Assessment> sceanrioAssessments = analysis.findAssessmentByScenarioId(scenario.getId());
		List<Asset> assets = analysis.getAssets();
		for (Asset asset : assets) {
			if (!sceanrioAssessments.containsKey(asset.getId())) {
				if (scenario.hasInfluenceOnAsset(asset.getAssetType()))
					assessments.add(new Assessment(asset, scenario));
			} else if (!scenario.hasInfluenceOnAsset(asset.getAssetType())) {
				Assessment assessment = sceanrioAssessments.get(asset.getId());
				assessments.remove(assessment);
				daoAssessment.delete(assessment);
			}
		}
		daoAnalysis.saveOrUpdate(analysis);
	}

	@Transactional
	public void UpdateAssessment(Analysis analysis) throws Exception {
		Map<String, Boolean> assessmentMapper = new LinkedHashMap<>();
		for (Assessment assessment : analysis.getAssessments())
			assessmentMapper.put(assessment.getAsset().getId() + "_" + assessment.getScenario().getId(), true);
		Map<String, ExtendedParameter> parameters = new LinkedHashMap<>();

		for (Parameter parameter : analysis.getParameters())
			if (parameter instanceof ExtendedParameter)
				parameters.put(((ExtendedParameter) parameter).getAcronym(), (ExtendedParameter) parameter);
		for (Asset asset : analysis.getAssets()) {
			for (Scenario scenario : analysis.getScenarios()) {
				if (!assessmentMapper.containsKey(asset.getId() + "_" + scenario.getId())) {
					Assessment assessment = new Assessment(asset, scenario);
					analysis.getAssessments().add(assessment);
					ComputeAlE(assessment, parameters);
				}
			}
		}
		UpdateAssetALE(analysis);
	}

	@Transactional
	public void WipeAssessment(Analysis analysis) throws Exception {
		Iterator<Assessment> iterator = analysis.getAssessments().iterator();
		while (iterator.hasNext()) {
			Assessment assessment = iterator.next();
			iterator.remove();
			daoAssessment.delete(assessment);
		}
	}

	public static void SplitAssessment(List<Assessment> assessments, Map<String, ALE> ales, Map<String, List<Assessment>> assessmentByAssets) {
		for (Assessment assessment : assessments) {
			if (assessment.isSelected()) {
				List<Assessment> assessments2 = assessmentByAssets.get(assessment.getAsset().getName());
				ALE ale = ales.get(assessment.getAsset().getName());
				if (assessments2 == null) {
					assessmentByAssets.put(assessment.getAsset().getName(), assessments2 = new LinkedList<>());
					ales.put(assessment.getAsset().getName(), ale = new ALE(assessment.getAsset().getName(), 0));
				}
				assessments2.add(assessment);
				ale.setValue(ale.getValue() + assessment.getALE());
			}
		}
	}

	/**
	 * UpdateAssetALE: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 */
	@Transactional
	public void UpdateAssetALE(Analysis analysis) throws Exception {
		List<ExtendedParameter> extendedParameters = analysis.findExtendedByAnalysis();
		Map<String, ExtendedParameter> parametersMapping = new LinkedHashMap<>(extendedParameters.size());
		List<Asset> assets = analysis.findAssessmentBySelected();
		Map<Integer, List<Assessment>> assessmentsByAsset = analysis.findAssessmentByAssetAndSelected();
		try {
			double ale = 0, alep = 0, aleo;
			for (ExtendedParameter extendedParameter : extendedParameters)
				parametersMapping.put(extendedParameter.getAcronym(), extendedParameter);
			for (Asset asset : assets) {
				ale = alep = aleo = 0;
				List<Assessment> assessments = assessmentsByAsset.get(asset.getId());
				if (assessments == null)
					continue;
				for (Assessment assessment : assessments) {
					ComputeAlE(assessment, parametersMapping);
					ale += assessment.getALE();
					aleo += assessment.getALEO();
					alep += assessment.getALEP();
				}
				asset.setALE(ale);
				asset.setALEO(aleo);
				asset.setALEP(alep);
				assessments.clear();
			}
		} finally {
			assets.clear();
			assessmentsByAsset.clear();
			parametersMapping.clear();
			extendedParameters.clear();
		}
	}

	public static List<Assessment> Sort(List<Assessment> assessments) {
		Map<String, List<Assessment>> assessmentByAssets = null;
		Map<String, ALE> ales = null;
		List<ALE> sortAles = null;
		try {
			assessmentByAssets = new LinkedHashMap<>();
			ales = new LinkedHashMap<>();
			SplitAssessment(assessments, ales, assessmentByAssets);
			sortAles = new LinkedList<>(ales.values());
			Collections.sort(sortAles, new AssetComparatorByALE());
			return Concact(sortAles, assessmentByAssets);
		} finally {
			if (assessmentByAssets != null)
				assessmentByAssets.clear();
			if (ales != null)
				ales.clear();
			if (sortAles != null)
				sortAles.clear();
		}
	}

	private static double StringToDouble(String value, Map<String, ExtendedParameter> parameters) {
		try {
			if (parameters.containsKey(value.trim().toLowerCase()))
				return parameters.get(value.trim().toLowerCase()).getValue();
			return Double.parseDouble(value);
		} catch (Exception e) {
			return 0;
		}
	}

	public static void ComputeAlE(List<Assessment> assessments, List<ExtendedParameter> parameters) {
		Map<String, ExtendedParameter> parametersMapping = new LinkedHashMap<>(parameters.size());
		try {
			for (ExtendedParameter extendedParameter : parameters)
				parametersMapping.put(extendedParameter.getAcronym(), extendedParameter);
			for (Assessment assessment : assessments)
				ComputeAlE(assessment, parametersMapping);
		} finally {
			parametersMapping.clear();
		}
	}

	public static void ComputeAlE(Assessment assessment, Map<String, ExtendedParameter> parameters) {
		double impactRep = StringToDouble(assessment.getImpactRep(), parameters);
		double impactOP = StringToDouble(assessment.getImpactOp(), parameters);
		double impactLeg = StringToDouble(assessment.getImpactLeg(), parameters);
		double impactFin = StringToDouble(assessment.getImpactFin(), parameters);
		double probability = StringToDouble(assessment.getLikelihood(), parameters);
		assessment.setImpactReal(Math.max(impactRep, Math.max(impactOP, Math.max(impactLeg, impactFin))));
		assessment.setLikelihoodReal(probability);
		assessment.setALE(assessment.getImpactReal() * probability);
		assessment.setALEP(assessment.getALE() * assessment.getUncertainty());
		assessment.setALEO(assessment.getALE() / assessment.getUncertainty());
	}

	private static List<Assessment> Concact(List<ALE> sortAles, Map<String, List<Assessment>> assessmentByAssets) {
		List<Assessment> assessments = new LinkedList<>();
		for (ALE ale : sortAles) {
			Collections.sort(assessmentByAssets.get(ale.getAssetName()), new AssessmentComparator());
			assessments.addAll(assessmentByAssets.get(ale.getAssetName()));
		}
		return assessments;
	}

	public static ALE ComputeALE(List<Assessment> assessments) {
		ALE ale = null;
		for (Assessment assessment : assessments) {
			if (ale == null)
				ale = new ALE(assessment.getAsset().getName(), 0);
			ale.setValue(ale.getValue() + assessment.getALE());
		}
		return ale;
	}

	public static List<Assessment> Sort(List<Assessment> assessments, ALE ale, ALE alep, ALE aleo) {
		ComputeALE(assessments, ale, alep, aleo);
		Collections.sort(assessments, new AssessmentComparator());
		return assessments;
	}

	private static void ComputeALE(List<Assessment> assessments, ALE ale, ALE alep, ALE aleo) {
		for (Assessment assessment : assessments) {
			ale.setValue(ale.getValue() + assessment.getALE());
			alep.setValue(alep.getValue() + assessment.getALEP());
			aleo.setValue(aleo.getValue() + assessment.getALEO());
		}

	}

}
