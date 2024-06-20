/**
 * 
 */
package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.usermanagement.IDS;

/**
 * @author eomar
 *
 */
public interface ServiceIDS {

	IDS get(int id);
	
	IDS get(String prefix);
	
	IDS getByToken(String token);
	
	List<IDS> getByAnalysisId(int idAnalysis);
	
	List<IDS> getByAnalysis(Analysis analysis);
	
	List<IDS> getAllAnalysisNoSubscribe(Integer idAnalysis);
	
	Integer save(IDS ids);
	
	void saveOrUpdate(IDS ids);
	
	void delete(IDS ids);
	
	void delete(Integer id);

	List<IDS> getAllByState(boolean enabled);

	List<IDS> getAllNoSubscribers();

	List<IDS> getAll();

	List<String> getPrefixesByAnalysisId(int idAnalysis);
	
	List<String> getPrefixesByAnalysis(Analysis analysis);

	boolean existByPrefix(String prefix);

	/**
	 * 
	 * @param token
	 * @return true if token is already exist.
	 */
	boolean exists(String token);

	boolean exists(boolean state);
}
