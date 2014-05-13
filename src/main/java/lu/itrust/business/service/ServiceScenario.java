package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Scenario;

/**
 * ServiceScenario.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceScenario {
	public Scenario get(Integer id) throws Exception;

	public Scenario getFromAnalysisById(Integer idAnalysis, Integer scenarioId) throws Exception;

	public boolean belongsToAnalysis(Integer scenarioId, Integer analysisId) throws Exception;

	public List<Scenario> getAll() throws Exception;

	public List<Scenario> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Scenario> getAllSelectedFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Scenario> getAllFromAnalysisByType(Analysis analysis, Integer scenarioTypeID) throws Exception;

	public List<Scenario> getAllFromAnalysisByIdList(Integer idAnalysis, List<Integer> scenarios) throws Exception;

	public void save(Scenario scenario) throws Exception;

	public void saveOrUpdate(Scenario scenario) throws Exception;

	public Scenario merge(Scenario scenario) throws Exception;

	public void delete(Scenario scenario) throws Exception;
}