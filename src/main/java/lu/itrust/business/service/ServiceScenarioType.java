package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.ScenarioType;

/**
 * ServiceScenarioType.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceScenarioType {
	public ScenarioType get(Integer id) throws Exception;

	public ScenarioType getByName(String scenarioTypeName) throws Exception;

	public List<ScenarioType> getAll() throws Exception;

	public void save(ScenarioType scenarioType) throws Exception;

	public void saveOrUpdate(ScenarioType scenarioType) throws Exception;

	public void delete(ScenarioType scenarioType) throws Exception;
}