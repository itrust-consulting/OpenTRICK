/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Scenario;

/**
 * @author eom
 *
 */
public interface ServiceAssessment {

	Assessment get(int id) throws Exception;
	List<Assessment> loadAllFromScenario(Scenario scenario) throws Exception;
	List<Assessment> loadAllFromScenarioId(int scenarioID) throws Exception;
	List<Assessment> loadAllFromAsset(Asset asset) throws Exception;
	List<Assessment> loadAllFromAssetId(int assetID) throws Exception;
	List<Assessment> loadAllFromAnalysisID(int idAnalysis) throws Exception;
	List<Assessment> loadAll() throws Exception;
	void save(Assessment assessment) throws Exception;
	void saveOrUpdate(Assessment assessment) throws Exception;
	void remove(Assessment assessment)throws Exception;
	void saveOrUpdate(List<Assessment> assessments);
	List<Assessment> findByAssetAndUnselected(Asset asset);
	List<Assessment> findByAssetAndSelected(Asset asset);
	List<Assessment> findByScenarioAndSelected(Scenario scenario);
	List<Assessment> findByScenarioAndUnselected(Scenario scenario);
}
