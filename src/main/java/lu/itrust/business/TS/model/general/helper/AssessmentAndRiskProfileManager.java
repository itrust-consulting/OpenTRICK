package lu.itrust.business.TS.model.general.helper;

import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_LEG_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_OPE_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_REP_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME;

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
import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
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

	@Transactional
	public void build(Asset asset, int idAnalysis) {
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			return;
		if (asset.getId() < 1)
			analysis.addAnAsset(asset);
		if (analysis.getType() != AnalysisType.QUALITATIVE && analysis.getRiskProfiles().isEmpty())
			buildAssessment(asset, analysis);
		else
			build(asset, analysis);
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
			analysis.addAScenario(scenario);
		if (analysis.getType() != AnalysisType.QUALITATIVE && analysis.getRiskProfiles().isEmpty())
			buildAssessment(scenario, analysis);
		else
			build(scenario, analysis);
	}

	@Transactional
	public void selectAsset(Asset asset) {
		asset.setSelected(true);
		List<Assessment> assessments = daoAssessment.getAllUnSelectedFromAsset(asset);
		for (Assessment assessment : assessments) {
			if (assessment.getScenario().isSelected() && assessment.getScenario().hasInfluenceOnAsset(asset.getAssetType())) {
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
			if (assessment.getAsset().isSelected() && scenario.hasInfluenceOnAsset(assessment.getAsset().getAssetType())) {
				assessment.setSelected(true);
				daoAssessment.saveOrUpdate(assessment);
			}
		}
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
		List<Assessment> assessments = daoAssessment.getAllSelectedFromScenario(scenario);
		for (Assessment assessment : assessments) {
			if (assessment.isSelected()) {
				assessment.setSelected(false);
				daoAssessment.saveOrUpdate(assessment);
			}
		}
	}

	@Transactional
	public void UpdateAcronym(int idAnalysis, ExtendedParameter extendedParameter, String acronym) {
		// retrieve assessments by acronym and analysis
		List<Assessment> assessments = daoAssessment.getAllFromAnalysisAndImpactLikelihoodAcronym(idAnalysis, acronym);
		// parse assessments and update impact value to parameter acronym
		for (Assessment assessment : assessments) {
			if (acronym.equals(assessment.getImpactFin()))
				assessment.setImpactFin(extendedParameter.getAcronym());
			else if (acronym.equals(assessment.getImpactLeg()))
				assessment.setImpactLeg(extendedParameter.getAcronym());
			else if (acronym.equals(assessment.getImpactOp()))
				assessment.setImpactOp(extendedParameter.getAcronym());
			else if (acronym.equals(assessment.getImpactRep()))
				assessment.setImpactRep(extendedParameter.getAcronym());
			else if (acronym.equals(assessment.getLikelihood()))
				assessment.setLikelihood(extendedParameter.getAcronym());
			// update assessment
			daoAssessment.saveOrUpdate(assessment);
		}
	}

	@Transactional
	public void UpdateAssessment() {
		int size = daoAnalysis.countNotEmpty(), pageSize = 30;
		for (int pageIndex = 1, pageCount = (size / pageSize) + 1; pageIndex <= pageCount; pageIndex++)
			for (Analysis analysis : daoAnalysis.getAllNotEmpty(pageIndex, pageSize))
				UpdateAssessment(analysis);
	}

	@Transactional
	public void UpdateAssessment(Analysis analysis) {
		Map<String, Assessment> assessmentMapper = analysis.getAssessments().stream().collect(Collectors.toMap(Assessment::getKey, Function.identity()));
		Map<String, AcronymParameter> expressionParameters = analysis.mapAcronymParameterByKey();
		if (analysis.getType() == AnalysisType.QUALITATIVE || !analysis.getRiskProfiles().isEmpty()) {
			Map<String, RiskProfile> riskProfiles = analysis.mapRiskProfile();
			for (Asset asset : analysis.getAssets()) {
				for (Scenario scenario : analysis.getScenarios()) {
					Assessment assessment = assessmentMapper.get(Assessment.key(asset, scenario));
					RiskProfile riskProfile = riskProfiles.get(RiskProfile.key(asset, scenario));
					if (scenario.hasInfluenceOnAsset(asset.getAssetType())) {
						if (assessment == null)
							analysis.getAssessments().add(ComputeAlE(new Assessment(asset, scenario), expressionParameters));
						if (riskProfile == null)
							analysis.getRiskProfiles().add(riskProfile);
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
					if (scenario.hasInfluenceOnAsset(asset.getAssetType())) {
						if (assessment == null)
							analysis.getAssessments().add(ComputeAlE(new Assessment(asset, scenario), expressionParameters));
					} else {
						if (assessment != null) {
							analysis.getAssessments().remove(assessment);
							daoAssessment.delete(assessment);
						}
					}
				}
			}
		}
		UpdateAssetALE(analysis);
	}

	public void UpdateRiskDendencies(Analysis analysis, Map<String, AcronymParameter> parametersMapped) {
		Map<String, Assessment> assessmentMapper = analysis.getAssessments().stream().collect(Collectors.toMap(Assessment::getKeyName, Function.identity()));
		if (analysis.getType() == AnalysisType.QUALITATIVE || !analysis.getRiskProfiles().isEmpty()) {
			Map<String, RiskProfile> riskProfiles = analysis.getRiskProfiles().stream().collect(Collectors.toMap(RiskProfile::getKeyName, Function.identity()));
			for (Asset asset : analysis.getAssets()) {
				for (Scenario scenario : analysis.getScenarios()) {
					Assessment assessment = assessmentMapper.get(Assessment.keyName(asset, scenario));
					RiskProfile riskProfile = riskProfiles.get(RiskProfile.keyName(asset, scenario));
					if (scenario.hasInfluenceOnAsset(asset.getAssetType())) {
						if (assessment == null)
							analysis.getAssessments().add(ComputeAlE(new Assessment(asset, scenario), parametersMapped));
						if (riskProfile == null)
							analysis.getRiskProfiles().add(riskProfile);
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
					if (scenario.hasInfluenceOnAsset(asset.getAssetType())) {
						if (assessment == null)
							analysis.getAssessments().add(ComputeAlE(new Assessment(asset, scenario), parametersMapped));
					} else {
						if (assessment != null)
							analysis.getAssessments().remove(assessment);
					}

				}
			}
		}
		UpdateAssetALE(analysis);
	}

	/**
	 * UpdateAssetALE: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 */
	@Transactional
	public void UpdateAssetALE(Analysis analysis) {
		Map<String, AcronymParameter> expressionParameters = analysis.mapAcronymParameterByKey();
		List<Asset> assets = analysis.findAssessmentBySelected();
		Map<Integer, List<Assessment>> assessmentsByAsset = analysis.findAssessmentByAssetAndSelected();
		try {
			double ale = 0, alep = 0, aleo;
			for (Asset asset : assets) {
				ale = alep = aleo = 0;
				List<Assessment> assessments = assessmentsByAsset.get(asset.getId());
				if (assessments == null)
					continue;
				for (Assessment assessment : assessments) {
					ComputeAlE(assessment, expressionParameters);
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
			expressionParameters.clear();
		}
	}

	@Transactional
	public void WipeAssessment(Analysis analysis) {
		while (!analysis.getAssessments().isEmpty())
			daoAssessment.delete(analysis.getAssessments().remove(0));
	}

	private void build(Asset asset, Analysis analysis) {
		Map<Integer, Assessment> assetAssessments = analysis.findAssessmentByAssetId(asset.getId());
		Map<Integer, RiskProfile> riskProfiles = analysis.findRiskProfileByAssetId(asset.getId());
		analysis.getScenarios().forEach(scenario -> {
			Assessment assessment = assetAssessments.get(scenario.getId());
			RiskProfile riskProfile = riskProfiles.get(scenario.getId());
			Update(assessment, scenario, asset, riskProfile, analysis);
		});

	}

	private void build(Scenario scenario, Analysis analysis) {
		Map<Integer, Assessment> assetAssessments = analysis.findAssessmentByScenarioId(scenario.getId());
		Map<Integer, RiskProfile> riskProfiles = analysis.findRiskProfileByScenarioId(scenario.getId());
		analysis.getAssets().forEach(asset -> {
			Assessment assessment = assetAssessments.get(asset.getId());
			RiskProfile riskProfile = riskProfiles.get(asset.getId());
			Update(assessment, scenario, asset, riskProfile, analysis);
		});
	}

	private void buildAssessment(Asset asset, Analysis analysis) {
		Map<Integer, Assessment> assetAssessments = analysis.findAssessmentByAssetId(asset.getId());
		analysis.getScenarios().forEach(scenario -> buildAssessment(asset, scenario, analysis.getAssessments(), assetAssessments));
	}

	/**
	 * 
	 * @param asset
	 * @param scenario
	 * @param assessments
	 * @param assetAssessments
	 *            mapped by scenario.id
	 */
	private void buildAssessment(Asset asset, Scenario scenario, List<Assessment> assessments, Map<Integer, Assessment> assetAssessments) {
		if (!assetAssessments.containsKey(scenario.getId())) {
			if (scenario.hasInfluenceOnAsset(asset.getAssetType()))
				assessments.add(new Assessment(asset, scenario));
		} else if (!scenario.hasInfluenceOnAsset(asset.getAssetType())) {
			Assessment assessment = assetAssessments.get(scenario.getId());
			assessments.remove(assessment);
			daoAssessment.delete(assessment);
		}
	}

	/**
	 * Parses the given expression for an assessment IMPACT and replaces any of
	 * the given parameters by their respective value.
	 * 
	 * @param expression
	 *            The expression to parse.
	 * @param parameters
	 *            A list of parameters which are known to the expression
	 *            evaluation engine. The latter replaces each encountered
	 *            parameter name in an expression by its respective value.
	 * @return Returns the computed value.
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 * @param string
	 * @since Jun 15, 2015
	 */
	private static double ImpactStringToDouble(String expression, String type, Map<String, AcronymParameter> parameters) {
		// Parse number
		try {
			return Double.parseDouble(expression);
		} catch (NumberFormatException ex) {
			AcronymParameter value = parameters.get(AcronymParameter.key(type, expression));
			return value == null ? 0.0 : value.getValue();
		}
	}

	/**
	 * Parses the given expression for an assessment PROBABILITY and replaces
	 * any of the given parameters by their respective value.
	 * 
	 * @param expression
	 *            The expression to parse.
	 * @param parameters
	 *            A list of parameters which are known to the expression
	 *            evaluation engine. The latter replaces each encountered
	 *            parameter name in an expression by its respective value.
	 * @return Returns the computed value.
	 * @author Steve Muller (SMU), itrust consulting s.à r.l.
	 * @since Jun 12, 2015
	 */
	private static double ProbabilityStringToDouble(String expression, Map<String, AcronymParameter> parameters) {
		// Create map which assigns a value to a parameter acronym from the
		// given parameter list
		// Parse expression
		try {
			return new StringExpressionParser(expression)
					.evaluate(parameters.values().stream().filter(parameter -> (parameter instanceof DynamicParameter) || parameter.isMatch(PARAMETERTYPE_TYPE_PROPABILITY_NAME))
							.collect(Collectors.toMap(AcronymParameter::getAcronym, AcronymParameter::getValue)));
		} catch (Exception e) {
			return 0.0;
		}
	}

	private void buildAssessment(Scenario scenario, Analysis analysis) {
		Map<Integer, Assessment> sceanrioAssessments = analysis.findAssessmentByScenarioId(scenario.getId());
		analysis.getAssets().forEach(asset -> buildAssessment(scenario, asset, analysis.getAssessments(), sceanrioAssessments));
		daoAnalysis.saveOrUpdate(analysis);
	}

	/**
	 * @param scenario
	 * @param asset
	 * @param assessments
	 * @param sceanrioAssessments
	 *            mapped by asset.id
	 * @throws Exception
	 */
	private void buildAssessment(Scenario scenario, Asset asset, List<Assessment> assessments, Map<Integer, Assessment> sceanrioAssessments) {
		if (!sceanrioAssessments.containsKey(asset.getId())) {
			if (scenario.hasInfluenceOnAsset(asset.getAssetType()))
				assessments.add(new Assessment(asset, scenario));
		} else if (!scenario.hasInfluenceOnAsset(asset.getAssetType())) {
			Assessment assessment = sceanrioAssessments.get(asset.getId());
			assessments.remove(assessment);
			daoAssessment.delete(assessment);
		}
	}

	private void Update(Assessment assessment, Scenario scenario, Asset asset, RiskProfile riskProfile, Analysis analysis) {
		if (scenario.hasInfluenceOnAsset(asset.getAssetType())) {
			if (assessment == null)
				analysis.getAssessments().add(new Assessment(asset, scenario));
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

	public static Assessment ComputeAlE(Assessment assessment, Map<String, AcronymParameter> expressionParameters) {
		double impactRep = ImpactStringToDouble(PARAMETERTYPE_TYPE_IMPACT_REP_NAME, assessment.getImpactRep(), expressionParameters);
		double impactOP = ImpactStringToDouble(PARAMETERTYPE_TYPE_IMPACT_OPE_NAME, assessment.getImpactOp(), expressionParameters);
		double impactLeg = ImpactStringToDouble(PARAMETERTYPE_TYPE_IMPACT_LEG_NAME, assessment.getImpactLeg(), expressionParameters);
		double impactFin = ImpactStringToDouble(PARAMETERTYPE_TYPE_IMPACT_NAME, assessment.getImpactFin(), expressionParameters);
		double probability = ProbabilityStringToDouble(assessment.getLikelihood(), expressionParameters);
		assessment.setImpactReal(Math.max(impactRep, Math.max(impactOP, Math.max(impactLeg, impactFin))));
		assessment.setLikelihoodReal(probability);
		assessment.setALE(assessment.getImpactReal() * probability);
		assessment.setALEP(assessment.getALE() * assessment.getUncertainty());
		assessment.setALEO(assessment.getALE() / assessment.getUncertainty());
		return assessment;
	}

	public static void ComputeAlE(List<Assessment> assessments, List<AcronymParameter> parameters) {
		Map<String, AcronymParameter> parametersMapping = parameters.stream().collect(Collectors.toMap(AcronymParameter::getKey, Function.identity()));
		assessments.forEach(assessment -> ComputeAlE(assessment, parametersMapping));
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

}
