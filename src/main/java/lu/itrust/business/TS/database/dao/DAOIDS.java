/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.usermanagement.IDS;

/**
 * @author eomar
 *
 */
public interface DAOIDS {
	
	IDS get(int id);
	
	IDS get(String prefix);
	
	List<IDS> getByAnalysisId(int idAnalysis);
	
	List<IDS> getByAnalysis(Analysis analysis);
	
	Integer save(IDS ids);
	
	void saveOrUpdate(IDS ids);
	
	void delete(IDS ids);

	List<IDS> getAllByState(boolean enabled);

	List<IDS> getAllNoSubscribers();

	List<IDS> getAll();

	List<String> getPrefixesByAnalysisId(int idAnalysis);
	
	List<String> getPrefixesByAnalysis(Analysis analysis);

	boolean existByPrefix(String prefix);

	boolean exists(String token);
}
