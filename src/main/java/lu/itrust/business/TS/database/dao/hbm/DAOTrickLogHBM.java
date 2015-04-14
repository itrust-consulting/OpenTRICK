/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOTrickLog;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.TrickLog;

/**
 * @author eomar
 *
 */
@Repository
public class DAOTrickLogHBM extends DAOHibernate implements DAOTrickLog {
	
	/**
	 * 
	 */
	public DAOTrickLogHBM() {
	}

	/**
	 * @param session
	 */
	public DAOTrickLogHBM(Session session) {
		super(session);
	}

	@Override
	public TrickLog get(Long id) {
		return (TrickLog) getSession().get(TrickLog.class, id);
	}

	@Override
	public Long count() {
		return (Long) getSession().createQuery("Select count(*) From TrickLog").uniqueResult();
	}

	@Override
	public Long countByLevel(LogLevel level) {
		return (Long) getSession().createQuery("Select count(*) From TrickLog where level = :level").setParameter("level", level).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAll() {
		return getSession().createQuery("From TrickLog").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAllByLevel(LogLevel level, int page, int size) {
		return getSession().createQuery("From TrickLog where level = :level").setParameter("level", level).setFirstResult((page -1)*size).setMaxResults(size).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAllByLevel(LogLevel level) {
		return getSession().createQuery("From TrickLog where level = :level").setParameter("level", level).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAll(int page, int size) {
		return getSession().createQuery("From TrickLog").setFirstResult((page -1)*size).setMaxResults(size).list();
	}

	@Override
	public TrickLog save(TrickLog trickLog) {
		return (TrickLog) getSession().save(trickLog);
		
	}

	@Override
	public void saveOrUpdate(TrickLog trickLog) {
		getSession().saveOrUpdate(trickLog);
	}

	@Override
	public void delete(Long id) {
		delete(get(id));
	}

	@Override
	public void delete(TrickLog trickLog) {
		getSession().delete(trickLog);
	}

}
