/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOWordReport;
import lu.itrust.business.TS.model.general.document.impl.WordReport;
import lu.itrust.business.TS.model.general.helper.FilterControl;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Repository
public class DAOWordReportHBM extends DAOHibernate implements DAOWordReport {

	/**
	 * 
	 */
	public DAOWordReportHBM() {
	}

	/**
	 * @param session
	 */
	public DAOWordReportHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#get(java.lang.Integer)
	 */
	@Override
	public WordReport get(Long id) {
		return (WordReport) getSession().get(WordReport.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#getByFileName(java.lang
	 * .String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public WordReport getByName(String name) {
		return (WordReport) getSession().createQuery("From WordReport where name = :name").setParameter("name", name)
				.uniqueResultOptional().orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#getByIdAndUser(java.
	 * lang.Integer, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public WordReport getByIdAndUser(Long id, String username) {
		return (WordReport) getSession().createQuery("From WordReport where id = :id and user.login = :username")
				.setParameter("id", id).setParameter("username", username)
				.uniqueResultOptional().orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#getAllFromUser(java.
	 * lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<WordReport> getAllFromUser(String username) {
		return getSession().createQuery("From WordReport where user.login = :username order by created desc")
				.setParameter("username", username).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#getAllFromUser(java.
	 * lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<WordReport> getAllFromUser(String username, Integer pageIndex, Integer pageSize) {
		return getSession().createQuery("From WordReport where user.login = :username order by created desc")
				.setParameter("username", username).setFirstResult((pageIndex - 1) * pageSize)
				.setMaxResults(pageSize).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#getAllFromUserAndI(java
	 * .lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<WordReport> getAllFromUserAndIdentifier(String username, String identifier, Integer pageIndex,
			Integer pageSize) {
		return getSession().createQuery(
				"From WordReport where identifier = :identifier and user.login = :username order by created desc")
				.setParameter("identifier", identifier)
				.setParameter("username", username).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize)
				.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#save(lu.itrust.business
	 * .TS.data.general.WordReport)
	 */
	@Override
	public WordReport save(WordReport wordReport) {
		return (WordReport) getSession().save(wordReport);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#saveOrUpdate(lu.itrust
	 * .business.TS.data.general.WordReport)
	 */
	@Override
	public void saveOrUpdate(WordReport wordReport) {
		getSession().saveOrUpdate(wordReport);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#merge(lu.itrust.business
	 * .TS.data.general.WordReport)
	 */
	@Override
	public WordReport merge(WordReport wordReport) {
		return (WordReport) getSession().merge(wordReport);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#delete(java.lang.Integer
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
	 * lu.itrust.business.TS.database.dao.DAOWordReport#delete(lu.itrust.business
	 * .TS.data.general.WordReport)
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
		return getSession()
				.createQuery("Select distinct identifier From WordReport where user = :user order by identifier desc")
				.setParameter("user", user).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WordReport> getAllFromUserByFilterControl(String username, Integer page, FilterControl filter) {
		if (!filter.validate())
			return Collections.emptyList();
		if ("ALL".equalsIgnoreCase(filter.getFilter()))
			return getSession()
					.createQuery(String.format("From WordReport where user.login = :username order by %s %s",
							filter.getSort(), filter.getDirection()))
					.setParameter("username", username).setFirstResult((page - 1) * filter.getSize())
					.setMaxResults(filter.getSize()).getResultList();
		else
			return getSession()
					.createQuery(String.format(
							"From WordReport where user.login = :username and identifier = :filter order by %s %s",
							filter.getSort(), filter.getDirection()))
					.setParameter("username", username).setParameter("filter", filter.getFilter())
					.setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize())
					.getResultList();
	}

	@Override
	public void deleteByUser(User user) {
		getSession().createQuery("Delete From WordReport where user =:user").setParameter("user", user).executeUpdate();
	}

	@Override
	public List<WordReport> findByCreatedBefore(Date date, int page, int size) {
		return getSession().createQuery("From WordReport where created < :deleteDate", WordReport.class)
				.setParameter("deleteDate", date).setFirstResult((page - 1) * size)
				.setMaxResults(size).list();
	}

	@Override
	public long countByCreatedBefore(Date date) {
		return getSession().createQuery("Select count(*) From WordReport where created < :deleteDate", Long.class)
				.setParameter("deleteDate", date).uniqueResult();
	}

}
