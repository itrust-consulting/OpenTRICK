/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOWordReport;
import lu.itrust.business.ts.model.general.document.impl.WordReport;
import lu.itrust.business.ts.model.general.helper.FilterControl;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
@Repository
public class DAOWordReportImpl extends DAOHibernate implements DAOWordReport {

	/**
	 * 
	 */
	public DAOWordReportImpl() {
	}

	/**
	 * @param session
	 */
	public DAOWordReportImpl(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#get(java.lang.Integer)
	 */
	@Override
	public WordReport get(Long id) {
		return (WordReport) getSession().get(WordReport.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#getByFileName(java.lang
	 * .String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public WordReport getByName(String name) {
		return (WordReport) createQueryWithCache("From WordReport where name = :name").setParameter("name", name)
				.uniqueResultOptional().orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#getByIdAndUser(java.
	 * lang.Integer, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public WordReport getByIdAndUser(Long id, String username) {
		return (WordReport) createQueryWithCache("From WordReport where id = :id and user.login = :username")
				.setParameter("id", id).setParameter("username", username)
				.uniqueResultOptional().orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#getAllFromUser(java.
	 * lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<WordReport> getAllFromUser(String username) {
		return createQueryWithCache("From WordReport where user.login = :username order by created desc")
				.setParameter("username", username).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#getAllFromUser(java.
	 * lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<WordReport> getAllFromUser(String username, Integer pageIndex, Integer pageSize) {
		return createQueryWithCache("From WordReport where user.login = :username order by created desc")
				.setParameter("username", username).setFirstResult((pageIndex - 1) * pageSize)
				.setMaxResults(pageSize).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#getAllFromUserAndI(java
	 * .lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<WordReport> getAllFromUserAndIdentifier(String username, String identifier, Integer pageIndex,
			Integer pageSize) {
		return createQueryWithCache(
				"From WordReport where identifier = :identifier and user.login = :username order by created desc")
				.setParameter("identifier", identifier)
				.setParameter("username", username).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize)
				.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#save(lu.itrust.business
	 * .ts.data.general.WordReport)
	 */
	@Override
	public WordReport save(WordReport wordReport) {
		return (WordReport) getSession().save(wordReport);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#saveOrUpdate(lu.itrust
	 * .business.ts.data.general.WordReport)
	 */
	@Override
	public void saveOrUpdate(WordReport wordReport) {
		getSession().saveOrUpdate(wordReport);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#merge(lu.itrust.business
	 * .ts.data.general.WordReport)
	 */
	@Override
	public WordReport merge(WordReport wordReport) {
		return (WordReport) getSession().merge(wordReport);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#delete(java.lang.Integer
	 * )
	 */
	@Override
	public void delete(Long id) {
		delete(get(id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOWordReport#delete(lu.itrust.business
	 * .ts.data.general.WordReport)
	 */
	@Override
	public void delete(WordReport wordReport) {
		getSession().delete(wordReport);
	}

	@Override
	public void delete(String filename) {
		delete(getByName(filename));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDistinctIdentifierByUser(User user) {
		return createQueryWithCache("Select distinct identifier From WordReport where user = :user order by identifier desc")
				.setParameter("user", user).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WordReport> getAllFromUserByFilterControl(String username, Integer page, FilterControl filter) {
		if (!filter.validate())
			return Collections.emptyList();
		if ("ALL".equalsIgnoreCase(filter.getFilter()))
			return createQueryWithCache(String.format("From WordReport where user.login = :username order by %s %s",
							filter.getSort(), filter.getDirection()))
					.setParameter("username", username).setFirstResult((page - 1) * filter.getSize())
					.setMaxResults(filter.getSize()).getResultList();
		else
			return createQueryWithCache(String.format(
							"From WordReport where user.login = :username and identifier = :filter order by %s %s",
							filter.getSort(), filter.getDirection()))
					.setParameter("username", username).setParameter("filter", filter.getFilter())
					.setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize())
					.getResultList();
	}

	@Override
	public void deleteByUser(User user) {
		createQueryWithCache("Delete From WordReport where user =:user").setParameter("user", user).executeUpdate();
	}

	@Override
	public List<WordReport> findByCreatedBefore(Date date, int page, int size) {
		return createQueryWithCache("From WordReport where created < :deleteDate", WordReport.class)
				.setParameter("deleteDate", date).setFirstResult((page - 1) * size)
				.setMaxResults(size).list();
	}

	@Override
	public long countByCreatedBefore(Date date) {
		return createQueryWithCache("Select count(*) From WordReport where created < :deleteDate", Long.class)
				.setParameter("deleteDate", date).uniqueResult();
	}

}
