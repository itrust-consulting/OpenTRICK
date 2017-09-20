package lu.itrust.business.TS.model.general.helper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAssessment;
import lu.itrust.business.TS.database.dao.DAOAsset;
import lu.itrust.business.TS.database.dao.DAORiskProfile;
import lu.itrust.business.TS.database.dao.DAOScenario;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.assessment.helper.AssessmentComparator;
import lu.itrust.business.TS.model.assessment.helper.AssetComparatorByALE;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.AbstractProbability;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.expressions.StringExpressionParser;

/**
 * AssessmentAndRiskProfileManager.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since May 12, 2014
 */
@Component
public class AssessmentAndRiskProfileManager {

	private DAOAnalysis daoAnalysis;

	private DAOAssessment daoAssessment;

	private DAOAsset daoAsset;

	private DAORiskProfile daoRiskProfile;

	private DAOScenario daoScenario;

	public AssessmentAndRiskProfileManager initialise(DAOAnalysis daoAnalysis, DAOAsset daoAsset, DAOAssessment daoAssessment, DAORiskProfile daoRiskProfile,
			DAOScenario daoScenario) {
		if (this.daoAnalysis == null)
			setDaoAnalysis(daoAnalysis);
		if (this.daoAssessment == null)
			setDaoAssessment(daoAssessment);
		if (this.daoAsset == null)
			setDaoAsset(daoAsset);
		if (this.daoScenario == null)
			setDaoScenario(daoScenario);
		if (this.daoRiskProfile == null)
			setDaoRiskProfile(daoRiskProfile);
		return this;
	}

	@Transactional
	public void build(Asset asset, int idAnalysis) {
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			return;
		if (asset.getId() < 1)
			analysis.add(asset);
		if (analysis.isQualitative())
			createAssessmentAndRiskProfile(asset, analysis);
		else
			createAssessment(asset, analysis);
		daoAnalysis.saveOrUpdate(analysis);
	}

	@Transactional
	public void build(int idScenario, int idAnalysis) {
		Analysis analysis = daoAnalysis.get(idAnalysis);
		Scenario scenario = daoScenario.getFromAnalysisById(idAnalysis, idScenario);
		buildOnly(scenario, analysis);
		daoAnalysis.saveOrUpdate(analysis);
	}

	public void buildOnly(Scenario scenario, Analysis analysis) {
		if (analysis == null || scenario == null)
			return;
		if (scenario.getId() < 1)
			analysis.add(scenario);
		if (analysis.isQualitative())
			createAssessmentAndRiskProfile(scenario, analysis);
		else
			createAssessment(scenario, analysis);
	}

	@Transactional
	public void selectAsset(Asset asset) {
		asset.setSelected(true);
		List<Assessment> assessments = daoAssessment.getAllUnSelectedFromAsset(asset);
		for (Assessment assessment : assessments) {
			if (assessment.getScenario().isSelected() && assessment.getScenario().hasInfluenceOnAsset(asset)) {
				assessment.setSelected(true);
				daoAssessment.saveOrUpdate(assessment);
			}
		}
	}

	@Transactional
	public void selectAsset(int idAsset) {
		Asset asset = daoAsset.get(idAsset);
		if (asset == null)
			return;
		selectAsset(asset);
	}

	@Transactional
	public void selectScenario(int idScenario) {
		Scenario scenario = daoScenario.get(idScenario);
		if (scenario == null)
			return;
		selectScenario(scenario);
	}

	@Transactional
	public void selectScenario(Scenario scenario) {
		scenario.setSelected(true);
		List<Assessment> assessments = daoAssessment.getAllUnselectedFromScenario(scenario);
		for (Assessment assessment : assessments) {
			if (assessment.getAsset().isSelected() && scenario.hasInfluenceOnAsset(assessment.getAsset())) {
				assessment.setSelected(true);
				daoAssessment.saveOrUpdate(assessment);
			}
		}
	}

	/**
	 * @param daoAnalysis
	 *            the daoAnalysis to set
	 */
	@Autowired
	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	/**
	 * @param daoAssessment
	 *            the daoAssessment to set
	 */
	@Autowired
	public void setDaoAssessment(DAOAssessment daoAssessment) {
		this.daoAssessment = daoAssessment;
	}

	/**
	 * @param daoAsset
	 *            the daoAsset to set
	 */
	@Autowired
	public void setDaoAsset(DAOAsset daoAsset) {
		this.daoAsset = daoAsset;
	}

	/**
	 * @param daoRiskProfile
	 *            the daoRiskProfile to set
	 */
	@Autowired
	public void setDaoRiskProfile(DAORiskProfile daoRiskProfile) {
		this.daoRiskProfile = daoRiskProfile;
	}

	/**
	 * @param daoScenario
	 *            the daoScenario to set
	 */
	@Autowired
	public void setDaoScenario(DAOScenario daoScenario) {
		this.daoScenario = daoScenario;
	}

	@Transactional
	public void toggledAsset(int idAsset) {
		Asset asset = daoAsset.get(idAsset);
		if (asset.isSelected())
			unSelectAsset(asset);
		else
			selectAsset(asset);
	}

	@Transactional
	public void toggledAssets(List<Integer> ids) {
		ids.forEach(idAsset -> toggledAsset(idAsset));
	}

	@Transactional
	public void toggledScenario(int idScenario) {
		Scenario scenario = daoScenario.get(idScenario);
		if (scenario.isSelected())
			unSelectScenario(scenario);
		else
			selectScenario(scenario);
	}

	@Transactional
	public void toggledScenarios(List<Integer> ids) {
		ids.forEach(idScenario -> toggledScenario(idScenario));
	}

	@Transactional
	public void unSelectAsset(Asset asset) {
		asset.setSelected(false);
		List<Assessment> assessments = daoAssessment.getAllSelectedFromAsset(asset);
		for (Assessment assessment : assessments) {
			assessment.setSelected(false);
			daoAssessment.saveOrUpdate(assessment);
		}
	}

	@Transactional
	public void unSelectAsset(int idAsset) {
		Asset asset = daoAsset.get(idAsset);
		if (asset == null)
			return;
		unSelectAsset(asset);
	}

	@Transactional
	public void unSelectScenario(int idScenario) {
		Scenario scenario = daoScenario.get(idScenario);
		if (scenario == null)
			return;
		unSelectScenario(scenario);
	}

	@Transactional
	public void unSelectScenario(Scenario scenario) {
		scenario.setSelected(false);
		daoAssessment.getAllSelectedFromScenario(scenario).forEach(assessment -> {
			if (assessment.isSelected()) {
				assessment.setSelected(false);
				daoAssessment.saveOrUpdate(assessment);
			}
		});
	}

	@Transactional
	public void updateAssessment() {
		int size = daoAnalysis.countNotEmpty(), pageSize = 30;
		for (int pageIndex = 1, pageCount = (size / pageSize) + 1; pageIndex <= pageCount; pageIndex++)
			for (Analysis analysis : daoAnalysis.getAllNotEmpty(pageIndex, pageSize))
				updateAssessment(analysis, null);
	}

	@Transactional
	public void updateAssessment(Analysis analysis, ValueFactory factory) {
		Map<String, Assessment> assessmentMapper = analysis.getAssessments().stream().collect(Collectors.toMap(Assessment::getKey, Function.identity()));
		if (factory == null)
			factory = new ValueFactory(analysis.getParameters());
		if (analysis.isQualitative()) {
			Map<String, RiskProfile> riskProfiles = analysis.mapRiskProfile();
			for (Asset asset : analysis.getAssets()) {
				for (Scenario scenario : analysis.getScenarios()) {
					Assessment assessment = assessmentMapper.get(Assessment.key(asset, scenario));
					RiskProfile riskProfile = riskProfiles.get(RiskProfile.key(asset, scenario));
					if (scenario.hasInfluenceOnAsset(asset)) {
						if (assessment == null)
							GenerateAssessment(analysis.getAssessments(), factory, asset, scenario);
						if (riskProfile == null)
							analysis.getRiskProfiles().add(new RiskProfile(asset, scenario));
					} else {
						if (assessment != null) {
							analysis.getAssessments().remove(assessment);
							daoAssessment.delete(assessment);
						}
						if (riskProfile != null) {
							analysis.getRiskProfiles().remove(riskProfile);
							daoRiskProfile.delete(riskProfile);
						}
					}
				}
			}
		} else {
			for (Asset asset : analysis.getAssets()) {
				for (Scenario scenario : analysis.getScenarios()) {
					Assessment assessment = assessmentMapper.get(Assessment.key(asset, scenario));
					if (scenario.hasInfluenceOnAsset(asset)) {
						if (assessment == null)
							GenerateAssessment(analysis.getAssessments(), factory, asset, scenario);
					} else {
						if (assessment != null) {
							analysis.getAssessments().remove(assessment);
							daoAssessment.delete(assessment);
						}
					}
				}
			}
		}

		if (analysis.isQuantitative())
			UpdateAssetALE(analysis, factory);
	}

	/**
	 * UpdateAssetALE: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param factory
	 * @throws Exception
	 */
	public static void UpdateAssetALE(Analysis analysis, ValueFactory factory) {
		List<Asset> assets = analysis.findSelectedAsset();
		Map<Integer, List<Assessment>> assessmentsByAsset = analysis.findAssessmentByAssetAndSelected();
		try {
			if (factory == null)
				factory = new ValueFactory(analysis.getParameters());
			double ale = 0, alep = 0, aleo;
			for (Asset asset : assets) {
				ale = alep = aleo = 0;
				List<Assessment> assessments = assessmentsByAsset.get(asset.getId());
				if (assessments == null)
					continue;
				for (Assessment assessment : assessments) {
					ComputeAlE(assessment, factory);
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
		}
	}

	public static void UpdateRiskDendencies(Analysis analysis, ValueFactory factory) {
		if (factory == null)
			factory = new ValueFactory(analysis.getParameters());
		Map<String, Assessment> assessmentMapper = analysis.getAssessments().stream().collect(Collectors.toMap(Assessment::getKeyName, Function.identity()));
		if (analysis.isQualitative()) {
			Map<String, RiskProfile> riskProfiles = analysis.getRiskProfiles().stream().collect(Collectors.toMap(RiskProfile::getKeyName, Function.identity()));
			for (Asset asset : analysis.getAssets()) {
				for (Scenario scenario : analysis.getScenarios()) {
					Assessment assessment = assessmentMapper.get(Assessment.keyName(asset, scenario));
					RiskProfile riskProfile = riskProfiles.get(RiskProfile.keyName(asset, scenario));
					if (scenario.hasInfluenceOnAsset(asset)) {
						if (assessment == null)
							GenerateAssessment(analysis.getAssessments(), factory, asset, scenario);
						if (riskProfile == null)
							analysis.getRiskProfiles().add(new RiskProfile(asset, scenario));
					} else {
						if (assessment != null)
							analysis.getAssessments().remove(assessment);
						if (riskProfile != null)
							analysis.getRiskProfiles().remove(riskProfile);

					}
				}
			}
		} else {
			for (Asset asset : analysis.getAssets()) {
				for (Scenario scenario : analysis.getScenarios()) {
					Assessment assessment = assessmentMapper.get(Assessment.keyName(asset, scenario));
					if (scenario.hasInfluenceOnAsset(asset)) {
						if (assessment == null)
							GenerateAssessment(analysis.getAssessments(), factory, asset, scenario);
					} else {
						if (assessment != null)
							analysis.getAssessments().remove(assessment);
					}

				}
			}
		}

		if (analysis.isQuantitative())
			UpdateAssetALE(analysis, factory);
	}

	@Transactional
	public void WipeAssessment(Analysis analysis) {
		while (!analysis.getAssessments().isEmpty())
			daoAssessment.delete(analysis.getAssessments().remove(0));
	}

	private void createAssessment(Asset asset, Analysis analysis) {
		Map<Integer, Assessment> assetAssessments = analysis.findAssessmentByAssetId(asset.getId());
		ValueFactory valueFactory = new ValueFactory(analysis.getImpactParameters());
		analysis.getScenarios().forEach(scenario -> createOrRemoveAssessment(scenario.getId(), asset, scenario, analysis.getAssessments(), assetAssessments, valueFactory));
	}

	private void createAssessment(Scenario scenario, Analysis analysis) {
		Map<Integer, Assessment> sceanrioAssessments = analysis.findAssessmentByScenarioId(scenario.getId());
		ValueFactory valueFactory = new ValueFactory(analysis.getImpactParameters());
		analysis.getAssets().forEach(asset -> createOrRemoveAssessment(asset.getId(), asset, scenario, analysis.getAssessments(), sceanrioAssessments, valueFactory));
		daoAnalysis.saveOrUpdate(analysis);
	}

	private void createAssessmentAndRiskProfile(Asset asset, Analysis analysis) {
		Map<Integer, Assessment> assetAssessments = analysis.findAssessmentByAssetId(asset.getId());
		Map<Integer, RiskProfile> riskProfiles = analysis.findRiskProfileByAssetId(asset.getId());
		ValueFactory valueFactory = new ValueFactory(analysis.getBoundedParamters());
		analysis.getScenarios().forEach(scenario -> createOrRemoveAssessmentAndRiskProfile(assetAssessments.get(scenario.getId()), scenario, asset,
				riskProfiles.get(scenario.getId()), analysis, valueFactory));
	}

	private void createAssessmentAndRiskProfile(Scenario scenario, Analysis analysis) {
		Map<Integer, Assessment> assetAssessments = analysis.findAssessmentByScenarioId(scenario.getId());
		Map<Integer, RiskProfile> riskProfiles = analysis.findRiskProfileByScenarioId(scenario.getId());
		ValueFactory valueFactory = new ValueFactory(analysis.getBoundedParamters());
		analysis.getAssets().forEach(
				asset -> createOrRemoveAssessmentAndRiskProfile(assetAssessments.get(asset.getId()), scenario, asset, riskProfiles.get(asset.getId()), analysis, valueFactory));
	}

	/**
	 * Add or remove assessment Only for QUANTITATIVE Analysis.
	 * 
	 * @param id
	 *            asset or scenario
	 * @param asset
	 * @param scenario
	 * @param assessments
	 * @param mappedAssessments
	 *            mapped by scenario.id
	 * @param valueFactory
	 *            impacts only
	 */
	private void createOrRemoveAssessment(Integer id, Asset asset, Scenario scenario, List<Assessment> assessments, Map<Integer, Assessment> mappedAssessments,
			ValueFactory valueFactory) {
		if (!mappedAssessments.containsKey(id)) {
			if (scenario.hasInfluenceOnAsset(asset))
				GenerateAssessment(assessments, valueFactory, asset, scenario);
		} else if (!scenario.hasInfluenceOnAsset(asset)) {
			Assessment assessment = mappedAssessments.get(id);
			assessments.remove(assessment);
			daoAssessment.delete(assessment);
		}
	}

	/**
	 * Add or remove assessment and riskProfile<br>
	 * Only for QUALITATIVE Analysis.
	 * 
	 * @param assessment
	 * @param scenario
	 * @param asset
	 * @param riskProfile
	 * @param analysis
	 * @param valueFactory
	 *            impacts + likelihood
	 */
	private void createOrRemoveAssessmentAndRiskProfile(Assessment assessment, Scenario scenario, Asset asset, RiskProfile riskProfile, Analysis analysis,
			ValueFactory valueFactory) {
		if (scenario.hasInfluenceOnAsset(asset)) {
			if (assessment == null) {
				assessment = new Assessment(asset, scenario);
				for (String impact : valueFactory.getImpactNames())
					createImpact(valueFactory, assessment, impact);
				analysis.getAssessments().add(assessment);
			}
			if (riskProfile == null)
				analysis.getRiskProfiles().add(new RiskProfile(asset, scenario));
		} else {
			if (assessment != null) {
				analysis.getAssessments().remove(assessment);
				daoAssessment.delete(assessment);
			}
			if (riskProfile != null) {
				analysis.getRiskProfiles().remove(riskProfile);
				daoRiskProfile.delete(riskProfile);
			}
		}
	}

	private static void GenerateAssessment(List<Assessment> assessments, ValueFactory factory, Asset asset, Scenario scenario) {
		Assessment assessment;
		assessment = new Assessment(asset, scenario);
		factory.getImpactNames().forEach(impact -> createImpact(factory, assessment, impact));
		assessments.add(assessment);
	}

	private static void createImpact(ValueFactory factory, Assessment assessment, String impact) {
		assessment.setImpact(factory.findValue(impact.equals(Constant.DEFAULT_IMPACT_NAME) ? 0D : 0, impact));
	}

	public static Assessment ComputeAlE(Assessment assessment, ValueFactory factory) {
		return ComputeAlE(assessment, null, factory);
	}

	public static Assessment ComputeAlE(Assessment assessment, IValue value, ValueFactory factory) {
		if (value == null || !value.getName().equals(Constant.DEFAULT_IMPACT_NAME))
			assessment.setImpactReal(assessment.getImpactValue(Constant.DEFAULT_IMPACT_NAME));
		else
			assessment.setImpactReal(value.getReal());
		assessment.setLikelihoodReal(new StringExpressionParser(assessment.getLikelihood()).evaluate(factory));
		assessment.setALE(assessment.getImpactReal() * assessment.getLikelihoodReal());
		assessment.setALEP(assessment.getALE() * assessment.getUncertainty());
		assessment.setALEO(assessment.getALE() / assessment.getUncertainty());
		return assessment;
	}

	public static void ComputeAlE(List<Assessment> assessments, List<AbstractProbability> parameters, AnalysisType type) {
		ValueFactory factory = new ValueFactory(parameters);
		assessments.forEach(assessment -> ComputeAlE(assessment, factory));
	}

	/**
	 * Generate ALE: ALEO, ALE,ALEP for each scenario and asset
	 * 
	 * @param analysis
	 * @return Length : 2, [0] for Assets, [1] for Scenarios
	 */
	@SuppressWarnings("unchecked")
	public static Map<Integer, ALE[]>[] ComputeALE(Analysis analysis) {
		Map<Integer, ALE[]>[] ales = new LinkedHashMap[2];
		for (int i = 0; i < 2; i++)
			ales[i] = new LinkedHashMap<Integer, ALE[]>();
		Map<Integer, List<Assessment>>[] assessments = Analysis.MappedSelectedAssessment(analysis.getAssessments());
		for (Asset asset : analysis.getAssets()) {
			ALE[] ales2 = new ALE[3];
			for (int i = 0; i < ales2.length; i++)
				ales2[i] = new ALE(asset.getName(), 0);
			ComputeALE(assessments[0].get(asset.getId()), ales2[1], ales2[2], ales2[0]);
			ales[0].put(asset.getId(), ales2);
		}
		for (Scenario scenario : analysis.getScenarios()) {
			ALE[] ales2 = new ALE[3];
			for (int i = 0; i < ales2.length; i++)
				ales2[i] = new ALE(scenario.getName(), 0);
			ComputeALE(assessments[1].get(scenario.getId()), ales2[1], ales2[2], ales2[0]);
			ales[1].put(scenario.getId(), ales2);
		}
		for (int i = 0; i < assessments.length; i++)
			assessments[i].clear();
		return ales;
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

	public static void ComputeALE(List<Assessment> assessments, ALE ale, ALE alep, ALE aleo) {
		if (assessments == null)
			return;
		for (Assessment assessment : assessments) {
			ale.setValue(ale.getValue() + assessment.getALE());
			alep.setValue(alep.getValue() + assessment.getALEP());
			aleo.setValue(aleo.getValue() + assessment.getALEO());
		}
	}

	public static Map<Integer, ALE[]> ComputeAssetALE(List<Asset> assets, List<Assessment> assessments2) {
		Map<Integer, ALE[]> ales = new LinkedHashMap<>();
		Map<Integer, List<Assessment>> assessments = Analysis.MappedSelectedAssessmentByAsset(assessments2);
		for (Asset asset : assets) {
			ALE[] ales2 = new ALE[3];
			for (int i = 0; i < ales2.length; i++)
				ales2[i] = new ALE(asset.getName(), 0);
			ComputeALE(assessments.get(asset.getId()), ales2[1], ales2[2], ales2[0]);
			ales.put(asset.getId(), ales2);
		}
		return ales;

	}

	public static Map<Integer, ALE[]> ComputeScenarioALE(List<Scenario> scenarios, List<Assessment> assessments2) {
		Map<Integer, ALE[]> ales = new LinkedHashMap<>();
		Map<Integer, List<Assessment>> assessments = Analysis.MappedSelectedAssessmentByScenario(assessments2);
		for (Scenario scenario : scenarios) {
			ALE[] ales2 = new ALE[3];
			for (int i = 0; i < ales2.length; i++)
				ales2[i] = new ALE(scenario.getName(), 0);
			ComputeALE(assessments.get(scenario.getId()), ales2[1], ales2[2], ales2[0]);
			ales.put(scenario.getId(), ales2);
		}
		return ales;
	}

	public static ALE[] ComputeTotalALE(Map<Integer, ALE[]> alesByAsset) {
		ALE[] ales = new ALE[3];
		for (int i = 0; i < ales.length; i++)
			ales[i] = new ALE(null, 0);
		for (ALE[] ales2 : alesByAsset.values()) {
			for (int i = 0; i < 3; i++)
				ales[i].setValue(ales[i].getValue() + ales2[i].getValue());
		}
		return ales;
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

	public static List<Assessment> Sort(List<Assessment> assessments, ALE ale, ALE alep, ALE aleo) {
		ComputeALE(assessments, ale, alep, aleo);
		Collections.sort(assessments, new AssessmentComparator());
		return assessments;
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

	private static List<Assessment> Concact(List<ALE> sortAles, Map<String, List<Assessment>> assessmentByAssets) {
		List<Assessment> assessments = new LinkedList<>();
		for (ALE ale : sortAles) {
			Collections.sort(assessmentByAssets.get(ale.getAssetName()), new AssessmentComparator());
			assessments.addAll(assessmentByAssets.get(ale.getAssetName()));
		}
		return assessments;
	}

}
