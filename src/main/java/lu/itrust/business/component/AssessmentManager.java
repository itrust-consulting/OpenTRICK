/**
 * 
 */
package lu.itrust.business.component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.DAOAssessment;
import lu.itrust.business.dao.DAOAsset;
import lu.itrust.business.dao.DAOScenario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author eom
 * 
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

	@Transactional
	public void selectAsset(Asset asset) throws Exception {
		asset.setSelected(true);
		List<Assessment> assessments = daoAssessment
				.findByAssetAndUnselected(asset);
		for (Assessment assessment : assessments) {
			if (assessment.getScenario().isSelected()) {
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
		List<Assessment> assessments = daoAssessment
				.findByAssetAndSelected(asset);
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
		List<Assessment> assessments = daoAssessment
				.findByScenarioAndUnselected(scenario);
		for (Assessment assessment : assessments) {
			if (assessment.getAsset().isSelected()) {
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
		List<Assessment> assessments = daoAssessment
				.findByScenarioAndSelected(scenario);
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
		analysis.addAnAsset(asset);
		List<Assessment> assessments = analysis.getAssessments();
		List<Scenario> scenarios = daoScenario
				.findByAnalysisAndSelected(idAnalysis);
		for (Scenario scenario : scenarios)
			assessments.add(new Assessment(asset, scenario));
		daoAnalysis.saveOrUpdate(analysis);
	}

	@Transactional
	public void build(Scenario scenario, int idAnalysis) throws Exception {
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			return;
		analysis.addAScenario(scenario);
		List<Assessment> assessments = analysis.getAssessments();
		List<Asset> assets = daoAsset.findByAnalysisAndSelected(idAnalysis);
		for (Asset asset : assets)
			assessments.add(new Assessment(asset, scenario));
		daoAnalysis.saveOrUpdate(analysis);
	}

	@Transactional
	public void generateMissingAssessment(Analysis analysis) throws Exception {
		Map<String, Boolean> assessmentMapper = new LinkedHashMap<>();
		for (Assessment assessment : analysis.getAssessments())
			assessmentMapper.put(assessment.getAsset().getId() + "_"
					+ assessment.getScenario().getId(), true);
		for (Asset asset : analysis.getAssets()) {
			for (Scenario scenario : analysis.getScenarios()) {
				if (!assessmentMapper.containsKey(asset.getId() + "_"
						+ scenario.getId()))
					analysis.getAssessments().add(
							new Assessment(asset, scenario));
			}
		}
		daoAnalysis.saveOrUpdate(analysis);
	}

	private static void SplitAssessment(List<Assessment> assessments,
			Map<String, ALE> ales,
			Map<String, List<Assessment>> assessmentByAssets) {
		for (Assessment assessment : assessments) {
			if (assessment.isSelected()) {
				List<Assessment> assessments2 = assessmentByAssets
						.get(assessment.getAsset().getName());
				ALE ale = ales.get(assessment.getAsset().getName());
				if (assessments2 == null) {
					assessmentByAssets.put(assessment.getAsset().getName(),
							assessments2 = new LinkedList<>());
					ales.put(assessment.getAsset().getName(), ale = new ALE(
							assessment.getAsset().getName(), 0));
				}
				assessments2.add(assessment);
				ale.setValue(ale.getValue() + assessment.getALE());
			}
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
			Collections.sort(sortAles, new AleComparator());
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

	private static List<Assessment> Concact(List<ALE> sortAles,
			Map<String, List<Assessment>> assessmentByAssets) {
		List<Assessment> assessments = new LinkedList<>();
		for (ALE ale : sortAles) {
			Collections.sort(assessmentByAssets.get(ale.getAssetName()),
					new AssessmentComparator());
			assessments.addAll(assessmentByAssets.get(ale.getAssetName()));
		}
		return assessments;
	}
}
