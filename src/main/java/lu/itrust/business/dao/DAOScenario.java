package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Scenario;

/**
 * DAOScenario.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOScenario {

	public Scenario get(int id) throws Exception;

	public Scenario getScenarioFromAnalysisByScenarioId(int idAnalysis, int scenarioId) throws Exception;

	public boolean belongsToAnalysis(Integer scenarioId, Integer analysisId) throws Exception;

	public List<Scenario> getAllScenarios() throws Exception;

	public List<Scenario> getAllFromAnalysisId(int idAnalysis) throws Exception;

	public List<Scenario> getAllFromAnalysisIdAndSelected(int idAnalysis) throws Exception;

	public List<Scenario> getAllFromAnalysisByScenarioTypeId(Analysis analysis, int scenarioTypeID) throws Exception;

	public List<Scenario> getAllScenariosFromAnalysisByScenarioIdList(int idAnalysis, List<Integer> scenarios) throws Exception;

	public void save(Scenario scenario) throws Exception;

	public void saveOrUpdate(Scenario scenario) throws Exception;

	public Scenario merge(Scenario scenario) throws Exception;

	public void delete(Scenario scenario) throws Exception;
}