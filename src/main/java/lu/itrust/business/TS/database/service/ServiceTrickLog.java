/**
 * 
 */
package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.TrickLog;

/**
 * @author eomar
 *
 */
public interface ServiceTrickLog {

	TrickLog get(Long id);

	Long count();

	Long countByLevel(LogLevel level);
	
	List<TrickLog> getAll();
	
	List<TrickLog> getAllByLevel(LogLevel level, int page, int size);
	
	List<TrickLog> getAllByLevel(LogLevel level);
	
	List<TrickLog> getAll(int page, int size);
	
	void save(TrickLog trickLog);

	void saveOrUpdate(TrickLog trickLog);

	void delete(Long id);

	void delete(TrickLog trickLog);

}
