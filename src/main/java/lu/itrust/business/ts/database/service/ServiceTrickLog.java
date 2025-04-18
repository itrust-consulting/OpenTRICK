/**
 * 
 */
package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.TrickLog;
import lu.itrust.business.ts.model.general.helper.TrickLogFilter;

/**
 * @author eomar
 *
 */
public interface ServiceTrickLog {

	TrickLog get(Long id);

	Long count();

	Long countByLevel(LogLevel level);
	
	List<TrickLog> getAll();
	
	List<TrickLog> getAll(Integer page, TrickLogFilter filter);
	
	List<TrickLog> getAllByLevel(LogLevel level, int page, int size);
	
	List<TrickLog> getAllByLevel(LogLevel level);
	
	List<TrickLog> getAll(int page, int size);
	
	void save(TrickLog trickLog);

	void saveOrUpdate(TrickLog trickLog);

	void delete(Long id);

	void delete(TrickLog trickLog);

	List<String> getDistinctAuthor();

	List<LogLevel> getDistinctLevel();

	List<LogType> getDistinctType();

	List<LogAction> getDistinctAction();

}
