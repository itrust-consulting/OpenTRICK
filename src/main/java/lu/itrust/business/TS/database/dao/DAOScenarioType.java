package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.data.scenario.OldScenarioType;

/**
 * DAOScenarioType.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOScenarioType {
	public OldScenarioType get(Integer id) throws Exception;

	public OldScenarioType getByName(String scenarioTypeName) throws Exception;

	public List<OldScenarioType> getAll() throws Exception;

	public void save(OldScenarioType scenarioType) throws Exception;

	public void saveOrUpdate(OldScenarioType scenarioType) throws Exception;

	public void delete(OldScenarioType scenarioType) throws Exception;
}