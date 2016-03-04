package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;

/**
 * DAOScenario.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOScenario {
	public Scenario get(Integer id) ;

	public Scenario getFromAnalysisById(Integer idAnalysis, Integer scenarioId) ;

	public boolean belongsToAnalysis(Integer analysisId, Integer scenarioId) ;

	public boolean exist(Integer idAnalysis, String name) ;
	
	public List<Scenario> getAll() ;

	public List<Scenario> getAllFromAnalysis(Integer idAnalysis) ;

	public List<Scenario> getAllSelectedFromAnalysis(Integer idAnalysis) ;

	public List<Scenario> getAllSelectedFromAnalysisByType(Integer idAnalysis, ScenarioType scenariotype) ;
	
	public List<Scenario> getAllFromAnalysisByType(Integer idAnalysis, ScenarioType scenarioType) ;

	public List<Scenario> getAllFromAnalysisByIdList(Integer idAnalysis, List<Integer> scenarios) ;

	public Integer getAnalysisIdFromScenario(Integer scenarioId) ;
	
	public void save(Scenario scenario) ;

	public void saveOrUpdate(Scenario scenario) ;

	public Scenario merge(Scenario scenario) ;

	public void delete(Scenario scenario) ;

	public Scenario getByNameAndAnalysisId(String name, int analysisId);
	
}