package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
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
	public Assessment loadFromAssetAndScenarioAnalysis(Analysis analysis, Asset asset, Scenario scenario);
	public List<Assessment> loadAllFromScenario(Scenario scenario) throws Exception;
	public List<Assessment> loadAllFromScenarioId(int scenarioID) throws Exception;
	public List<Assessment> loadAllFromAsset(Asset asset) throws Exception;
	public List<Assessment> loadAllFromAssetId(int assetID) throws Exception;
	public List<Assessment> loadAllFromAnalysis(Analysis analysis) throws Exception;
	public List<Assessment> loadAllFromAnalysisID(int idAnalysis) throws Exception;
	public List<Assessment> loadAllFromAnalysisIdentifierAndVersion(int idAnalysis, int identifier, int version) throws Exception;
	public List<Assessment> loadAll() throws Exception;
	public void save(Assessment assessment) throws Exception;
	public void saveOrUpdate(Assessment assessment) throws Exception;
	public void remove(Assessment assessment)throws Exception;
}
