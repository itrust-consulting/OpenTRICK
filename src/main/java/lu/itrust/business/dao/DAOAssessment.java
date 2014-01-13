package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Scenario;


/** 
 * DAOAssessment.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAOAssessment {
	public Assessment get(int id) throws Exception;
	public List<Assessment> loadAllFromScenario(Scenario scenario) throws Exception;
	public List<Assessment> loadAllFromScenarioId(int scenarioID) throws Exception;
	public List<Assessment> loadAllFromAsset(Asset asset) throws Exception;
	public List<Assessment> loadAllFromAssetId(int assetID) throws Exception;
	public List<Assessment> loadAllFromAnalysisID(int idAnalysis) throws Exception;
	public List<Assessment> loadAll() throws Exception;
	public void save(Assessment assessment) throws Exception;
	public void saveOrUpdate(Assessment assessment) throws Exception;
	public void remove(Assessment assessment)throws Exception;
	public void saveOrUpdate(List<Assessment> assessments);
	public List<Assessment> findByAssetAndUnselected(Asset asset);
	public List<Assessment> findByAssetAndSelected(Asset asset);
	public List<Assessment> findByScenarioAndSelected(Scenario scenario);
	public List<Assessment> findByScenarioAndUnselected(Scenario scenario);
	public List<Assessment> findByAnalysisAndSelectedScenario(Integer idAnalysis);
	public List<Assessment> findByAnalysisAndAcronym(int idAnalysis,
			String acronym);
}
