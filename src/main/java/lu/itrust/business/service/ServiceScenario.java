/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Scenario;

/**
 * @author eom
 * 
 */
public interface ServiceScenario {

	public Scenario get(int id) throws Exception;

	public Scenario loadFromNameAnalysis(String scenarioName, Analysis analysis)
			throws Exception;

	public List<Scenario> loadAllFromAnalysisID(int idAnalysis)
			throws Exception;
	
	public List<Scenario> findByAnalysisAndSelected(int idAnalysis);

	public List<Scenario> loadAll() throws Exception;

	public void save(Scenario scenario) throws Exception;

	public void saveOrUpdate(Scenario scenario) throws Exception;

	public void remove(Scenario scenario) throws Exception;

	public Scenario merge(Scenario scenario);
}
