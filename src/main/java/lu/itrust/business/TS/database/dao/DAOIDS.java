/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;
import java.util.stream.Stream;

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
	
	void delete(Integer id);

	List<IDS> getAllByState(boolean enabled);

	List<IDS> getAllNoSubscribers();

	List<IDS> getAll();

	List<String> getPrefixesByAnalysisId(int idAnalysis);
	
	List<String> getPrefixesByAnalysis(Analysis analysis);

	boolean existByPrefix(String prefix);

	boolean exists(String token);

	List<IDS> getAllAnalysisNoSubscribe(Integer idAnalysis);

	IDS getByToken(String token);

	boolean exists(boolean state);

	Stream<Integer> findSubscriberIdByUsername(String username);
}
