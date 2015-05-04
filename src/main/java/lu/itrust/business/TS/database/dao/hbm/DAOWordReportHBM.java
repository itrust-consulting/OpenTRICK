/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.database.dao.DAOWordReport;
import lu.itrust.business.TS.model.general.WordReport;
import lu.itrust.business.TS.model.general.helper.FilterControl;
import lu.itrust.business.TS.usermanagement.User;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

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
	public WordReport get(Integer id) {
		return (WordReport) getSession().get(WordReport.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#getByFileName(java.lang
	 * .String)
	 */
	@Override
	public WordReport getByFilename(String fileName) {
		return (WordReport) getSession().createQuery("From WordReport where filename = :filename").setString("filename", fileName).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOWordReport#getByIdAndUser(java.
	 * lang.Integer, java.lang.String)
	 */
	@Override
	public WordReport getByIdAndUser(Integer id, String username) {
		return (WordReport) getSession().createQuery("From WordReport where id = :id and user.login = :username").setInteger("id", id).setString("username", username)
				.uniqueResult();
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
		return getSession().createQuery("From WordReport where user.login = :username order by created desc").setString("username", username).list();
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
		return getSession().createQuery("From WordReport where user.login = :username order by created desc").setString("username", username).setFirstResult((pageIndex - 1) * pageSize)
				.setMaxResults(pageSize).list();
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
	public List<WordReport> getAllFromUserAndIdentifier(String username, String identifier, Integer pageIndex, Integer pageSize) {
		return getSession().createQuery("From WordReport where identifier = :identifier and user.login = :username order by created desc").setString("identifier", identifier)
				.setString("username", username).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).list();
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
	public void delete(Integer id) {
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
		delete(getByFilename(filename));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDistinctIdentifierByUser(User user) {
		return getSession().createQuery("Select distinct identifier From WordReport where user = :user order by identifier desc").setParameter("user", user).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WordReport> getAllFromUserByFilterControl(String username, Integer page, FilterControl filter) {
		if (!filter.validate())
			throw new IllegalArgumentException();
		if ("ALL".equalsIgnoreCase(filter.getFilter()))
			return getSession().createQuery(String.format("From WordReport where user.login = :username order by %s %s", filter.getSort(), filter.getDirection()))
					.setParameter("username", username).setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize()).list();
		else
			return getSession()
					.createQuery(String.format("From WordReport where user.login = :username and identifier = :filter order by %s %s", filter.getSort(), filter.getDirection()))
					.setParameter("username", username).setString("filter", filter.getFilter()).setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize())
					.list();
	}

	@Override
	public void deeleteByUser(User user) {
		getSession().createQuery("Delete From WordReport where user =:user").setParameter("user", user).executeUpdate();
	}

}
