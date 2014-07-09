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
}