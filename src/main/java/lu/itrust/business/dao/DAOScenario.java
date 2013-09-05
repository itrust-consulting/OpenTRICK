package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.ScenarioType;

/**
 * DAOScenario.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOScenario {

	public Scenario get(int id) throws Exception;
	public Scenario loadFromNameAnalysis(String scenarioName, Analysis analysis) throws Exception;
	public List<Scenario> loadAllFromScenarioType(ScenarioType scenarioType, Analysis analysis) throws Exception;
	public List<Scenario> loadAllFromScenarioTypeID(int scenarioTypeID, Analysis analysis) throws Exception;
	public List<Scenario> loadAllFromAnalysis(Analysis analysis) throws Exception;
	public List<Scenario> loadAllFromAnalysisID(int idAnalysis) throws Exception;
	public List<Scenario> loadAllFromAnalysisIdentifierAndVersion(int idAnalysis, int identifier, int version) throws Exception;
	public List<Scenario> loadAll() throws Exception;
	public void save(Scenario scenario) throws Exception;
	public void saveOrUpdate(Scenario scenario) throws Exception;
	public void remove(Scenario scenario) throws Exception;
}