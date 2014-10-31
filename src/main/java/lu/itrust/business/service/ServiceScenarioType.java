package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.OldScenarioType;

/**
 * ServiceScenarioType.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceScenarioType {
	public OldScenarioType get(Integer id) throws Exception;

	public OldScenarioType getByName(String scenarioTypeName) throws Exception;

	public List<OldScenarioType> getAll() throws Exception;

	public void save(OldScenarioType scenarioType) throws Exception;

	public void saveOrUpdate(OldScenarioType scenarioType) throws Exception;

	public void delete(OldScenarioType scenarioType) throws Exception;
}