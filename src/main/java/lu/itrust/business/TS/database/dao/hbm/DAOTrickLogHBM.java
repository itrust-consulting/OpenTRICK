/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOTrickLog;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.TrickLog;
import lu.itrust.business.TS.model.general.helper.TrickLogFilter;

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
		return (Long) getSession().createQuery("Select count(*) From TrickLog").getSingleResult();
	}

	@Override
	public Long countByLevel(LogLevel level) {
		return (Long) getSession().createQuery("Select count(*) From TrickLog where level = :level").setParameter("level", level).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAll() {
		return getSession().createQuery("From TrickLog").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAllByLevel(LogLevel level, int page, int size) {
		return getSession().createQuery("From TrickLog where level = :level").setParameter("level", level).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAllByLevel(LogLevel level) {
		return getSession().createQuery("From TrickLog where level = :level").setParameter("level", level).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrickLog> getAll(int page, int size) {
		return getSession().createQuery("From TrickLog").setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
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
		return getSession().createQuery(criteria).setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize()).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDistinctAuthor() {
		return getSession().createQuery("Select distinct author From TrickLog order by author").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogLevel> getDistinctLevel() {
		return getSession().createQuery("Select distinct level From TrickLog order by level").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogType> getDistinctType() {
		return getSession().createQuery("Select distinct type From TrickLog order by type").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogAction> getDistinctAction() {
		return getSession().createQuery("Select distinct action From TrickLog order by action").getResultList();
	}
}
