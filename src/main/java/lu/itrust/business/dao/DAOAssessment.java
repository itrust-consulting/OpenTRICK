package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Scenario;

/**
 * DAOAssessment.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOAssessment {
	public Assessment get(int id) throws Exception;

	public boolean belongsToAnalysis(Integer assessmentId, Integer analysisId) throws Exception;

	public List<Assessment> getAll() throws Exception;

	public List<Assessment> getAllFromAnalysisID(int idAnalysis) throws Exception;

	public List<Assessment> getAssessmentFromAnalysisAndImpactLikelihoodAcronym(int idAnalysis, String acronym) throws Exception;

	public List<Assessment> getAllFromAnalysisAndSelectedScenario(Integer idAnalysis) throws Exception;

	public List<Assessment> getAllFromScenarioId(int scenarioID) throws Exception;

	public List<Assessment> getAllFromScenario(Scenario scenario) throws Exception;

	public List<Assessment> getAllSelectedAssessmentFromScenario(Scenario scenario) throws Exception;

	public List<Assessment> getAllUnselectedAssessmentFromScenario(Scenario scenario) throws Exception;

	public List<Assessment> getAllFromAnalysisAndSelectedAsset(Integer idAnalysis) throws Exception;

	public List<Assessment> getAllFromAssetId(int assetID) throws Exception;

	public List<Assessment> getAllFromAsset(Asset asset) throws Exception;

	public List<Assessment> getAllSelectedAssessmentFromAsset(Asset asset) throws Exception;

	public List<Assessment> getAllUnSelectedAssessmentFromAsset(Asset asset) throws Exception;

	public void save(Assessment assessment) throws Exception;

	public void saveOrUpdate(Assessment assessment) throws Exception;

	public void saveOrUpdate(List<Assessment> assessments) throws Exception;
	
	public void delete(Assessment assessment) throws Exception;
}