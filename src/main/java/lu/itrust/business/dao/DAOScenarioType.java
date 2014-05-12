package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.ScenarioType;

/**
 * DAOScenarioType.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOScenarioType {
	public ScenarioType get(int id) throws Exception;

	public ScenarioType getByTypeName(String scenarioTypeName) throws Exception;

	public List<ScenarioType> getAllScenarioTypes() throws Exception;

	public void save(ScenarioType scenarioType) throws Exception;

	public void saveOrUpdate(ScenarioType scenarioType) throws Exception;

	public void delete(ScenarioType scenarioType) throws Exception;
}