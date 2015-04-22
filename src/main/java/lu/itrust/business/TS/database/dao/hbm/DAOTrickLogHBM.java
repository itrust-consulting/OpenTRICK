/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.database.dao.DAOTrickLog;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.TrickLog;
import lu.itrust.business.TS.model.general.helper.TrickLogFilter;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

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
		return getSession().createQuery("From TrickLog where level = :level").setParameter("level", level).setFirstResult((page - 1) * size).setMaxResults(size).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAllByLevel(LogLevel level) {
		return getSession().createQuery("From TrickLog where level = :level").setParameter("level", level).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAll(int page, int size) {
		return getSession().createQuery("From TrickLog").setFirstResult((page - 1) * size).setMaxResults(size).list();
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

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAll(Integer page, TrickLogFilter filter) {
		
		Criteria criteria = getSession().createCriteria(TrickLog.class);
		if (filter.isOrderDescending())
			criteria.addOrder(Order.desc("created"));
		else
			criteria.addOrder(Order.asc("created"));
		
		if(filter.getLevel()!=null)
			criteria.add(Property.forName("level").eq(filter.getLevel()));
		
		if (filter.getType()!=null)
			criteria.add(Property.forName("type").eq(filter.getType()));
		
		if(!(filter.getAuthor()==null || filter.getAuthor().isEmpty()))
			criteria.add(Property.forName("author").eq(filter.getAuthor()));
		
		if(filter.getAction()!=null)
			criteria.add(Property.forName("action").eq(filter.getAction()));

		return criteria.setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize()).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDistinctAuthor() {
		return getSession().createQuery("Select distinct author From TrickLog order by author").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogLevel> getDistinctLevel() {
		return getSession().createQuery("Select distinct level From TrickLog order by level").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogType> getDistinctType() {
		return getSession().createQuery("Select distinct type From TrickLog order by type").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogAction> getDistinctAction() {
		return getSession().createQuery("Select distinct action From TrickLog order by action").list();
	}
}
