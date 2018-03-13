/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOUserSqLite;
import lu.itrust.business.TS.model.general.document.impl.UserSQLite;
import lu.itrust.business.TS.model.general.helper.FilterControl;
import lu.itrust.business.TS.usermanagement.User;

/**
 * DAOUserSqLiteHBM.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOUserSqLiteHBM extends DAOHibernate implements DAOUserSqLite {

	/**
	 * Constructor: <br>
	 */
	public DAOUserSqLiteHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOUserSqLiteHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#get(Long)
	 */
	@Override
	public UserSQLite get(Long id) {
		return (UserSQLite) getSession().get(UserSQLite.class, id);
	}

	/**
	 * getByFileName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#getByFilename(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UserSQLite getByFilename(String filename) {
		return (UserSQLite) getSession().createQuery("From UserSQLite where filename = :filename").setParameter("filename", filename).uniqueResultOptional().orElse(null);
	}

	/**
	 * getByUserSQLiteIdAndUserLogin: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#getByUserSQLiteIdAndUserLogin(long,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UserSQLite getByIdAndUser(Long id, String username) {
		String query = "From UserSQLite where id = :id and user.login = :username";
		return (UserSQLite) getSession().createQuery(query).setParameter("id", id).setParameter("username", username).uniqueResultOptional().orElse(null);
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#getAllFromUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSQLite> getAllFromUser(String username) {
		return getSession().createQuery("From UserSQLite where user.login = :username order by created desc").setParameter("username", username).getResultList();
	}

	/**
	 * getAllFromUserLoginByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#getAllFromUserLoginByPageAndSizeIndex(java.lang.String,
	 *      int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSQLite> getAllFromUserByPageAndSizeIndex(String username, Integer pageIndex, Integer pageSize) {
		String query = "From UserSQLite where user.login = :username order by created desc";
		return getSession().createQuery(query).setParameter("username", username).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#save(lu.itrust.business.TS.model.general.document.impl.UserSQLite)
	 */
	@Override
	public UserSQLite save(UserSQLite userSqLite) {
		return (UserSQLite) getSession().save(userSqLite);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#saveOrUpdate(lu.itrust.business.TS.model.general.document.impl.UserSQLite)
	 */
	@Override
	public void saveOrUpdate(UserSQLite userSqLite) {
		getSession().saveOrUpdate(userSqLite);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#merge(lu.itrust.business.TS.model.general.document.impl.UserSQLite)
	 */
	@Override
	public UserSQLite merge(UserSQLite userSqLite) {
		return (UserSQLite) getSession().merge(userSqLite);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#delete(Long)
	 */
	@Override
	public void delete(Long id) {
		delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#delete(java.lang.String)
	 */
	@Override
	public void delete(String filename) {
		delete(getByFilename(filename));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#delete(lu.itrust.business.TS.model.general.document.impl.UserSQLite)
	 */
	@Override
	public void delete(UserSQLite userSqLite) {
		getSession().delete(userSqLite);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDistinctIdentifierByUser(User user) {
		return getSession().createQuery("Select distinct identifier From UserSQLite where user = :user order by identifier desc").setParameter("user", user).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserSQLite> getAllFromUserByPageAndFilterControl(String username, Integer page, FilterControl filter) {
		if (!filter.validate())
			return Collections.emptyList();
		if ("ALL".equalsIgnoreCase(filter.getFilter()))
			return getSession().createQuery(String.format("From UserSQLite where user.login = :username order by %s %s", filter.getSort(), filter.getDirection()))
					.setParameter("username", username).setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize()).getResultList();
		else
			return getSession()
					.createQuery(String.format("From UserSQLite where user.login = :username and identifier = :filter order by %s %s", filter.getSort(), filter.getDirection()))
					.setParameter("username", username).setParameter("filter", filter.getFilter()).setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize())
					.getResultList();
	}

	@Override
	public void deeleteByUser(User user) {
		getSession().createQuery("Delete From UserSQLite where user =:user").setParameter("user", user).executeUpdate();
	}

	@Override
	public List<UserSQLite> findByCreatedBefore(Date date, int page, int size) {
		return getSession().createQuery("From UserSQLite where created < :deleteDate", UserSQLite.class).setParameter("deleteDate", date).setFirstResult((page - 1) * size)
				.setMaxResults(size).list();
	}

	@Override
	public long countByCreatedBefore(Date date) {
		return getSession().createQuery("Select count(*) From UserSQLite where created < :deleteDate", Long.class).setParameter("deleteDate", date).uniqueResult();
	}
}