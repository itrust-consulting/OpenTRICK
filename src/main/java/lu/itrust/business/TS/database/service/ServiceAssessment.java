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
	public Assessment get(Integer id) throws Exception;

	public Assessment getFromAnalysisById(Integer idAnalysis, Integer idAssessment) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer assessmentId) throws Exception;

	public List<Assessment> getAll() throws Exception;

	public List<Assessment> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Assessment> getAllFromAnalysisAndImpactLikelihoodAcronym(Integer idAnalysis, String acronym) throws Exception;

	public List<Assessment> getAllFromAnalysisAndSelectedScenario(Integer idAnalysis) throws Exception;

	public List<Assessment> getAllFromScenario(Integer scenarioID) throws Exception;

	public List<Assessment> getAllFromScenario(Scenario scenario) throws Exception;

	public List<Assessment> getAllSelectedFromScenario(Scenario scenario) throws Exception;

	public List<Assessment> getAllUnselectedFromScenario(Scenario scenario) throws Exception;

	public List<Assessment> getAllFromAnalysisAndSelectedAsset(Integer idAnalysis) throws Exception;

	public List<Assessment> getAllFromAsset(Integer assetID) throws Exception;

	public List<Assessment> getAllFromAsset(Asset asset) throws Exception;

	public List<Assessment> getAllSelectedFromAsset(Asset asset) throws Exception;

	public List<Assessment> getAllUnSelectedFromAsset(Asset asset) throws Exception;

	public void save(Assessment assessment) throws Exception;

	public void saveOrUpdate(Assessment assessment) throws Exception;

	public void saveOrUpdate(List<Assessment> assessments) throws Exception;

	public void delete(Assessment assessment) throws Exception;

	public List<Assessment> getAllFromAnalysisAndSelected(Integer idAnalysis);

	public Assessment getByAssetAndScenario(Asset asset, Scenario scenario);

	public Assessment getByAssetAndScenario(int idAsset, int idScenario);
}