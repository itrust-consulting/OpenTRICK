package lu.itrust.business.TS.component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAssessment;
import lu.itrust.business.TS.database.dao.DAOAsset;
import lu.itrust.business.TS.database.dao.DAORiskProfile;
import lu.itrust.business.TS.database.dao.DAOScenario;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisUtils;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.assessment.helper.AssessmentComparator;
import lu.itrust.business.TS.model.assessment.helper.AssetComparatorByALE;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.scenario.Scenario;

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
		final Analysis analysis = daoAnalysis.get(idAnalysis);
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
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		final Scenario scenario = daoScenario.getFromAnalysisById(idAnalysis, idScenario);
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
		daoAssessment.getAllUnSelectedFromAsset(asset).stream().filter(a -> a.getScenario().isSelected() && a.getScenario().hasInfluenceOnAsset(asset)).forEach(a -> {
			a.setSelected(true);
			daoAssessment.saveOrUpdate(a);
		});
		daoAsset.saveOrUpdate(asset);
	}

	@Transactional
	public void selectAsset(int idAsset) {
		final Asset asset = daoAsset.get(idAsset);
		if (asset == null)
			return;
		selectAsset(asset);
	}

	@Transactional
	public void selectScenario(int idScenario) {
		final Scenario scenario = daoScenario.get(idScenario);
		if (scenario == null)
			return;
		selectScenario(scenario);
	}

	@Transactional
	public void selectScenario(Scenario scenario) {
		scenario.setSelected(true);
		daoAssessment.getAllUnselectedFromScenario(scenario).stream().filter(a -> a.getAsset().isSelected() && scenario.hasInfluenceOnAsset(a.getAsset())).forEach(a -> {
			a.setSelected(true);
			daoAssessment.saveOrUpdate(a);
		});
		daoScenario.saveOrUpdate(scenario);
	}

	/**
	 * @param daoAnalysis the daoAnalysis to set
	 */
	@Autowired
	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	/**
	 * @param daoAssessment the daoAssessment to set
	 */
	@Autowired
	public void setDaoAssessment(DAOAssessment daoAssessment) {
		this.daoAssessment = daoAssessment;
	}

	/**
	 * @param daoAsset the daoAsset to set
	 */
	@Autowired
	public void setDaoAsset(DAOAsset daoAsset) {
		this.daoAsset = daoAsset;
	}

	/**
	 * @param daoRiskProfile the daoRiskProfile to set
	 */
	@Autowired
	public void setDaoRiskProfile(DAORiskProfile daoRiskProfile) {
		this.daoRiskProfile = daoRiskProfile;
	}

	/**
	 * @param daoScenario the daoScenario to set
	 */
	@Autowired
	public void setDaoScenario(DAOScenario daoScenario) {
		this.daoScenario = daoScenario;
	}

	@Transactional
	public void toggledAsset(int idAsset) {
		final Asset asset = daoAsset.get(idAsset);
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
		final Scenario scenario = daoScenario.get(idScenario);
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
		daoAssessment.getAllSelectedFromAsset(asset).forEach(assessment -> {
			assessment.setSelected(false);
			daoAssessment.saveOrUpdate(assessment);
		});
		daoAsset.saveOrUpdate(asset);
	}

	@Transactional
	public void unSelectAsset(int idAsset) {
		final Asset asset = daoAsset.get(idAsset);
		if (asset == null)
			return;
		unSelectAsset(asset);
	}

	@Transactional
	public void unSelectScenario(int idScenario) {
		final Scenario scenario = daoScenario.get(idScenario);
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
		daoScenario.saveOrUpdate(scenario);
	}

	@Transactional
	public void updateAssessment() {
		final int size = daoAnalysis.countNotEmpty(), pageSize = 30;
		for (int pageIndex = 1, pageCount = (size / pageSize) + 1; pageIndex <= pageCount; pageIndex++)
			for (Analysis analysis : daoAnalysis.getAllNotEmpty(pageIndex, pageSize))
				updateAssessment(analysis, null);
	}

	@Transactional
	public void updateAssessment(Analysis analysis, ValueFactory factory, boolean generateIds) {
		final Map<String, Assessment> assessmentMapper = analysis.getAssessments().stream().collect(Collectors.toMap(Assessment::getKeyName, Function.identity()));
		if (factory == null)
			factory = new ValueFactory(analysis.getParameters());
		if (analysis.isQualitative()) {
			final Map<String, RiskProfile> riskProfiles = analysis.getRiskProfiles().stream().collect(Collectors.toMap(RiskProfile::getKeyName, Function.identity()));
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
			if (generateIds)
				GenerateRiskProfileIdentifer(analysis.getRiskProfiles());
		} else {
			for (Asset asset : analysis.getAssets()) {
				for (Scenario scenario : analysis.getScenarios()) {
					Assessment assessment = assessmentMapper.get(Assessment.keyName(asset, scenario));
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

	@Transactional
	public void updateAssessment(Analysis analysis, ValueFactory factory) {
		updateAssessment(analysis, factory, true);
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
		final List<Asset> assets = analysis.findSelectedAsset();
		final Map<Integer, List<Assessment>> assessmentsByAsset = analysis.getAssessments().stream().filter(a -> a.isSelected())
				.collect(Collectors.groupingBy(a -> a.getAsset().getId()));
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
					ComputeAlE(assessment);
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
		final Map<String, Assessment> assessmentMapper = analysis.getAssessments().stream().collect(Collectors.toMap(Assessment::getKeyName, Function.identity()));
		if (analysis.isQualitative()) {
			final Map<String, RiskProfile> riskProfiles = analysis.getRiskProfiles().stream().collect(Collectors.toMap(RiskProfile::getKeyName, Function.identity()));
			for (Asset asset : analysis.getAssets()) {
				for (Scenario scenario : analysis.getScenarios()) {
					final Assessment assessment = assessmentMapper.get(Assessment.keyName(asset, scenario));
					final RiskProfile riskProfile = riskProfiles.get(RiskProfile.keyName(asset, scenario));
					if (scenario.hasInfluenceOnAsset(asset)) {
						if (assessment == null)
							GenerateAssessment(analysis.getAssessments(), factory, asset, scenario);
						if (riskProfile == null)
							GenerateRiskProfile(analysis.getRiskProfiles(), asset, scenario);
					} else {
						if (assessment != null)
							analysis.getAssessments().remove(assessment);
						if (riskProfile != null)
							analysis.getRiskProfiles().remove(riskProfile);

					}
				}
			}
			GenerateRiskProfileIdentifer(analysis.getRiskProfiles());

		} else {
			for (Asset asset : analysis.getAssets()) {
				for (Scenario scenario : analysis.getScenarios()) {
					final Assessment assessment = assessmentMapper.get(Assessment.keyName(asset, scenario));
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

	public static void GenerateRiskProfileIdentifer(List<RiskProfile> riskProfiles) {
		final String maxId = riskProfiles.stream().filter(risk -> StringUtils.hasText(risk.getIdentifier())).map(RiskProfile::getIdentifier)
				.max((i1, i2) -> NaturalOrderComparator.compareTo(i1, i2)).orElse("R0");
		final Object[] numbering = extractNumbering(maxId);
		if (numbering[1] == null)
			return;
		AtomicLong id = new AtomicLong((long) numbering[1]);
		riskProfiles.stream().sorted((r1, r2) -> {
			int result = Boolean.compare(r1.isSelected(), r2.isSelected());
			if (result == 0)
				result = Integer.compare(r1.getComputedExpImportance(), r2.getComputedExpImportance());
			return result * -1;
		}).filter(risk -> !StringUtils.hasText(risk.getIdentifier())).forEach(riskProfile -> riskProfile.setIdentifier(numbering[0] + "" + id.incrementAndGet()));
	}

	/**
	 * Extract RiskProfile identifier naming parts. if error return [value, null]
	 * 
	 * @param value
	 * @return [String, Long]
	 */
	private static Object[] extractNumbering(String value) {
		try {
			boolean found = false;
			int i = value.length() - 1;
			for (; i >= 0; i--) {
				if (Character.isDigit(value.charAt(i)))
					found = true;
				else
					break;
			}
			return found ? i < 0 ? new Object[] { "", Long.parseLong(value) } : new Object[] { value.substring(0, i + 1), Long.parseLong(value.substring(i + 1)) }
					: new Object[] { value, null };
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return new Object[] { value, null };
		}
	}

	@Transactional
	public void WipeAssessment(Analysis analysis) {
		while (!analysis.getAssessments().isEmpty())
			daoAssessment.delete(analysis.getAssessments().remove(0));
	}

	private void createAssessment(Asset asset, Analysis analysis) {
		final Map<Integer, Assessment> assetAssessments = analysis.findAssessmentByAssetId(asset.getId());
		ValueFactory valueFactory = new ValueFactory(analysis.getImpactParameters());
		analysis.getScenarios().forEach(scenario -> createOrRemoveAssessment(scenario.getId(), asset, scenario, analysis.getAssessments(), assetAssessments, valueFactory));
	}

	private void createAssessment(Scenario scenario, Analysis analysis) {
		final Map<Integer, Assessment> sceanrioAssessments = analysis.findAssessmentByScenarioId(scenario.getId());
		ValueFactory valueFactory = new ValueFactory(analysis.getImpactParameters());
		analysis.getAssets().forEach(asset -> createOrRemoveAssessment(asset.getId(), asset, scenario, analysis.getAssessments(), sceanrioAssessments, valueFactory));
		daoAnalysis.saveOrUpdate(analysis);
	}

	private void createAssessmentAndRiskProfile(Asset asset, Analysis analysis) {
		final Map<Integer, Assessment> assetAssessments = analysis.findAssessmentByAssetId(asset.getId());
		final Map<Integer, RiskProfile> riskProfiles = analysis.findRiskProfileByAssetId(asset.getId());
		final ValueFactory valueFactory = new ValueFactory(analysis.getBoundedParamters());
		analysis.getScenarios().forEach(scenario -> createOrRemoveAssessmentAndRiskProfile(assetAssessments.get(scenario.getId()), scenario, asset,
				riskProfiles.get(scenario.getId()), analysis, valueFactory));
		GenerateRiskProfileIdentifer(analysis.getRiskProfiles());
	}

	private void createAssessmentAndRiskProfile(Scenario scenario, Analysis analysis) {
		final Map<Integer, Assessment> assetAssessments = analysis.findAssessmentByScenarioId(scenario.getId());
		final Map<Integer, RiskProfile> riskProfiles = analysis.findRiskProfileByScenarioId(scenario.getId());
		final ValueFactory valueFactory = new ValueFactory(analysis.getBoundedParamters());
		analysis.getAssets().forEach(
				asset -> createOrRemoveAssessmentAndRiskProfile(assetAssessments.get(asset.getId()), scenario, asset, riskProfiles.get(asset.getId()), analysis, valueFactory));
		GenerateRiskProfileIdentifer(analysis.getRiskProfiles());
	}

	/**
	 * Add or remove assessment Only for QUANTITATIVE Analysis.
	 * 
	 * @param id                asset or scenario
	 * @param asset
	 * @param scenario
	 * @param assessments
	 * @param mappedAssessments mapped by scenario.id
	 * @param valueFactory      impacts only
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
	 * @param valueFactory impacts + likelihood
	 */
	private void createOrRemoveAssessmentAndRiskProfile(Assessment assessment, Scenario scenario, Asset asset, RiskProfile riskProfile, Analysis analysis,
			ValueFactory valueFactory) {
		if (scenario.hasInfluenceOnAsset(asset)) {
			if (assessment == null)
				GenerateAssessment(analysis.getAssessments(), valueFactory, asset, scenario);
			if (riskProfile == null)
				GenerateRiskProfile(analysis.getRiskProfiles(), asset, scenario);
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

	public static Assessment GenerateAssessment(List<Assessment> assessments, ValueFactory factory, Asset asset, Scenario scenario) {
		final Assessment assessment = new Assessment(asset, scenario);
		factory.getImpactNames().forEach(impact -> createImpact(factory, assessment, impact));
		assessments.add(assessment);
		return assessment;
	}

	private static void createImpact(ValueFactory factory, Assessment assessment, String impact) {
		assessment.setImpact(factory.findValue(impact.equals(Constant.DEFAULT_IMPACT_NAME) ? 0D : 0, impact));
	}

	public static Assessment ComputeAlE(Assessment assessment) {
		return ComputeAlE(assessment, (IValue)null);
	}

	public static Assessment ComputeAlE(Assessment assessment, IValue value) {
		if (value == null || !value.getName().equals(Constant.DEFAULT_IMPACT_NAME))
			assessment.setImpactReal(assessment.getImpactValue(Constant.DEFAULT_IMPACT_NAME));
		else
			assessment.setImpactReal(value.getReal());
		assessment.setLikelihoodReal(assessment.getLikelihood() == null ? 0 : assessment.getLikelihood().getReal());
		assessment.setALE(assessment.getImpactReal() * assessment.getLikelihoodReal());
		assessment.setALEP(assessment.getALE() * assessment.getUncertainty());
		assessment.setALEO(assessment.getALE() / assessment.getUncertainty());
		return assessment;
	}

	public static void ComputeAlE(List<Assessment> assessments) {
		assessments.forEach(assessment -> ComputeAlE(assessment));
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
		Map<Integer, List<Assessment>>[] assessments = AnalysisUtils.MappedSelectedAssessment(analysis.getAssessments());
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
		final Map<Integer, ALE[]> ales = new LinkedHashMap<>();
		final Map<Integer, List<Assessment>> assessments = AnalysisUtils.MappedSelectedAssessmentByAsset(assessments2);
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
		final Map<Integer, ALE[]> ales = new LinkedHashMap<>();
		final Map<Integer, List<Assessment>> assessments = AnalysisUtils.MappedSelectedAssessmentByScenario(assessments2);
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
		final ALE[] ales = new ALE[3];
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
		final List<Assessment> assessments = new LinkedList<>();
		for (ALE ale : sortAles) {
			Collections.sort(assessmentByAssets.get(ale.getAssetName()), new AssessmentComparator());
			assessments.addAll(assessmentByAssets.get(ale.getAssetName()));
		}
		return assessments;
	}

	public static RiskProfile GenerateRiskProfile(List<RiskProfile> riskProfiles, Asset asset, Scenario scenario) {
		final RiskProfile riskProfile = new RiskProfile(asset, scenario);
		riskProfiles.add(riskProfile);
		return riskProfile;
	}

}
