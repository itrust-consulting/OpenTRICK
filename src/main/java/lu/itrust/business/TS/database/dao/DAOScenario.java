package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.data.scenario.Scenario;
import lu.itrust.business.TS.data.scenario.ScenarioType;

/**
 * DAOScenario.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOScenario {
	public Scenario get(Integer id) throws Exception;

	public Scenario getFromAnalysisById(Integer idAnalysis, Integer scenarioId) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer scenarioId) throws Exception;

	public boolean exist(Integer idAnalysis, String name) throws Exception;
	
	public List<Scenario> getAll() throws Exception;

	public List<Scenario> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Scenario> getAllSelectedFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Scenario> getAllSelectedFromAnalysisByType(Integer idAnalysis, ScenarioType scenariotype) throws Exception;
	
	public List<Scenario> getAllFromAnalysisByType(Integer idAnalysis, ScenarioType scenarioType) throws Exception;

	public List<Scenario> getAllFromAnalysisByIdList(Integer idAnalysis, List<Integer> scenarios) throws Exception;

	public Integer getAnalysisIdFromScenario(Integer scenarioId) throws Exception;
	
	public void save(Scenario scenario) throws Exception;

	public void saveOrUpdate(Scenario scenario) throws Exception;

	public Scenario merge(Scenario scenario) throws Exception;

	public void delete(Scenario scenario) throws Exception;
	
}