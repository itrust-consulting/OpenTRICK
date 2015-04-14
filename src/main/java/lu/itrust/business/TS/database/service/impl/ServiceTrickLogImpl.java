/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lu.itrust.business.TS.database.dao.DAOTrickLog;
import lu.itrust.business.TS.database.service.ServiceTrickLog;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.TrickLog;

/**
 * @author eomar
 *
 */
@Service
public class ServiceTrickLogImpl implements ServiceTrickLog {

	@Autowired
	private DAOTrickLog daoTrickLog;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#get(java.lang.Long)
	 */
	@Override
	public TrickLog get(Long id) {
		return daoTrickLog.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#count()
	 */
	@Override
	public Long count() {
		return daoTrickLog.count();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#countByLevel(lu.itrust.business.TS.model.general.LogLevel)
	 */
	@Override
	public Long countByLevel(LogLevel level) {
		return daoTrickLog.countByLevel(level);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#getAll()
	 */
	@Override
	public List<TrickLog> getAll() {
		return daoTrickLog.getAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#getAllByLevel(lu.itrust.business.TS.model.general.LogLevel, int, int)
	 */
	@Override
	public List<TrickLog> getAllByLevel(LogLevel level, int page, int size) {
		return daoTrickLog.getAllByLevel(level, page, size);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#getAllByLevel(lu.itrust.business.TS.model.general.LogLevel)
	 */
	@Override
	public List<TrickLog> getAllByLevel(LogLevel level) {
		return daoTrickLog.getAllByLevel(level);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#getAll(int, int)
	 */
	@Override
	public List<TrickLog> getAll(int page, int size) {
		return daoTrickLog.getAll(page, size);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#save(lu.itrust.business.TS.model.general.TrickLog)
	 */
	@Transactional
	@Override
	public void save(TrickLog trickLog) {
		daoTrickLog.save(trickLog);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#saveOrUpdate(lu.itrust.business.TS.model.general.TrickLog)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(TrickLog trickLog) {
		daoTrickLog.saveOrUpdate(trickLog);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#delete(java.lang.Long)
	 */
	@Transactional
	@Override
	public void delete(Long id) {
		daoTrickLog.delete(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceTrickLog#delete(lu.itrust.business.TS.model.general.TrickLog)
	 */
	@Transactional
	@Override
	public void delete(TrickLog trickLog) {
		daoTrickLog.delete(trickLog);
	}

}
