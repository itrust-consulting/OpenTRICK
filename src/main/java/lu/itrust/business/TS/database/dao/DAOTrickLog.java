/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.TrickLog;
import lu.itrust.business.TS.model.general.helper.TrickLogFilter;

/**
 * @author eomar
 *
 */
public interface DAOTrickLog {

	TrickLog get(Long id);

	Long count();

	Long countByLevel(LogLevel level);

	List<TrickLog> getAll();

	List<TrickLog> getAllByLevel(LogLevel level, int page, int size);

	List<TrickLog> getAllByLevel(LogLevel level);

	List<TrickLog> getAll(int page, int size);

	TrickLog save(TrickLog trickLog);

	void saveOrUpdate(TrickLog trickLog);

	void delete(Long id);

	void delete(TrickLog trickLog);

	List<TrickLog> getAll(Integer page, TrickLogFilter filter);

	List<String> getDistinctAuthor();

	List<LogLevel> getDistinctLevel();

	List<LogType> getDistinctType();

	List<LogAction> getDistinctAction();
}
