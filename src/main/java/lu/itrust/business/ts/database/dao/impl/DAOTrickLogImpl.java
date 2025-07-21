/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOTrickLog;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.TrickLog;
import lu.itrust.business.ts.model.general.helper.TrickLogFilter;

/**
 * @author eomar
 *
 */
@Repository
public class DAOTrickLogImpl extends DAOHibernate implements DAOTrickLog {

	/**
	 * 
	 */
	public DAOTrickLogImpl() {
	}

	/**
	 * @param session
	 */
	public DAOTrickLogImpl(Session session) {
		super(session);
	}

	@Override
	public TrickLog get(Long id) {
		return (TrickLog) getSession().get(TrickLog.class, id);
	}

	@Override
	public Long count() {
		return (Long) createQueryWithCache("Select count(*) From TrickLog").getSingleResult();
	}

	@Override
	public Long countByLevel(LogLevel level) {
		return (Long) createQueryWithCache("Select count(*) From TrickLog where level = :level")
				.setParameter("level", level).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAll() {
		return createQueryWithCache("From TrickLog").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAllByLevel(LogLevel level, int page, int size) {
		return createQueryWithCache("From TrickLog where level = :level").setParameter("level", level)
				.setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAllByLevel(LogLevel level) {
		return createQueryWithCache("From TrickLog where level = :level").setParameter("level", level).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAll(int page, int size) {
		return createQueryWithCache("From TrickLog").setFirstResult((page - 1) * size).setMaxResults(size)
				.getResultList();
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

	@Override
	public List<TrickLog> getAll(Integer page, TrickLogFilter filter) {
		CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
		CriteriaQuery<TrickLog> criteria = criteriaBuilder.createQuery(TrickLog.class);
		Root<TrickLog> root = criteria.from(TrickLog.class);
		if (filter.isOrderDescending())
			criteria.orderBy(criteriaBuilder.desc(root.get("created")));
		else
			criteria.orderBy(criteriaBuilder.asc(root.get("created")));
		List<Predicate> predicates = new LinkedList<>();
		if (filter.getLevel() != null)
			predicates.add(criteriaBuilder.equal(root.get("level"), filter.getLevel()));
		if (filter.getType() != null)
			predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("type"), filter.getType())));
		if (!(filter.getAuthor() == null || filter.getAuthor().isEmpty()))
			predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("author"), filter.getAuthor())));
		if (filter.getAction() != null)
			predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("action"), filter.getAction())));
		if (!predicates.isEmpty())
			criteria.where(predicates.toArray(new Predicate[predicates.size()]));
		return getSession().createQuery(criteria).setHint("org.hibernate.cacheable", true)
				.setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize()).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDistinctAuthor() {
		return createQueryWithCache("Select distinct author From TrickLog order by author").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogLevel> getDistinctLevel() {
		return createQueryWithCache("Select distinct level From TrickLog order by level").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogType> getDistinctType() {
		return createQueryWithCache("Select distinct type From TrickLog order by type").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogAction> getDistinctAction() {
		return createQueryWithCache("Select distinct action From TrickLog order by action").getResultList();
	}

	@Override
	public long countByDateBefore(Date date) {
		return createQuery("select count(*) from TrickLog where created < :date", Long.class)
				.setParameter("date", date).getSingleResult();
	}

	/** 
	 * Deletes TrickLogs created before the specified date.
	 * 
	 * @param date the date before which TrickLogs will be deleted
	 */
	@Override
	public void deleteByDateBefore(Date date) {
		createQuery("delete from TrickLog where created < :date")
				.setParameter("date", date)
				.executeUpdate();
	}

	/** 
	 * Deletes TrickLogs created before the specified date, with pagination support.
	*/
	@Override
	public void deleteByDateBefore(Date date, int page, int size) {
		int offset = (page - 1) * size;
		createQuery("From TrickLog where created < :date", TrickLog.class)
				.setParameter("date", date)
				.setFirstResult(offset)
				.setMaxResults(size)
				.list()
				.forEach(this::delete);
	}
}
