/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.dao.DAOScenarioType;

/**
 * @author oensuifudine
 * 
 */
public interface ServiceScenarioType {

	public ScenarioType get(int id) throws Exception;

	public ScenarioType get(String scenarioTypeName) throws Exception;

	public List<ScenarioType> loadAll() throws Exception;

	public void save(ScenarioType scenarioType) throws Exception;

	public void saveOrUpdate(ScenarioType scenarioType) throws Exception;

	public void delete(ScenarioType scenarioType) throws Exception;
	
	public DAOScenarioType getDaoScenarioType();

}
