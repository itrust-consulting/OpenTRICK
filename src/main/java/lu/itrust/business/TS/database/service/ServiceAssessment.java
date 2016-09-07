package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * ServiceAssessment.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceAssessment {
	public Assessment get(Integer id);

	public Assessment getFromAnalysisById(Integer idAnalysis, Integer idAssessment);

	public boolean belongsToAnalysis(Integer analysisId, Integer assessmentId);

	public List<Assessment> getAll();

	public List<Assessment> getAllFromAnalysis(Integer idAnalysis);

	public List<Assessment> getAllFromAnalysisAndImpactLikelihoodAcronym(Integer idAnalysis, String acronym);

	public List<Assessment> getAllFromAnalysisAndSelectedScenario(Integer idAnalysis);

	public List<Assessment> getAllFromScenario(Integer scenarioID);

	public List<Assessment> getAllFromScenario(Scenario scenario);

	public List<Assessment> getAllSelectedFromScenario(Scenario scenario);

	public List<Assessment> getAllUnselectedFromScenario(Scenario scenario);

	public List<Assessment> getAllFromAnalysisAndSelectedAsset(Integer idAnalysis);

	public List<Assessment> getAllFromAsset(Integer assetID);

	public List<Assessment> getAllFromAsset(Asset asset);

	public List<Assessment> getAllSelectedFromAsset(Asset asset);

	public List<Assessment> getAllUnSelectedFromAsset(Asset asset);

	public void save(Assessment assessment);

	public void saveOrUpdate(Assessment assessment);

	public void saveOrUpdate(List<Assessment> assessments);

	public void delete(Assessment assessment);

	public List<Assessment> getAllFromAnalysisAndSelected(Integer idAnalysis);

	public Assessment getByAssetAndScenario(Asset asset, Scenario scenario);

	public Assessment getByAssetAndScenario(int idAsset, int idScenario);

	public List<String> getDistinctOwnerByIdAnalysis(Integer analysisId);
}