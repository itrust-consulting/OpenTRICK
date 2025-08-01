/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOUserSqLite;
import lu.itrust.business.ts.model.general.document.impl.UserSQLite;
import lu.itrust.business.ts.model.general.helper.FilterControl;
import lu.itrust.business.ts.usermanagement.User;

/**
 * DAOUserSqLiteImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOUserSqLiteImpl extends DAOHibernate implements DAOUserSqLite {

	/**
	 * Constructor: <br>
	 */
	public DAOUserSqLiteImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOUserSqLiteImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#get(Long)
	 */
	@Override
	public UserSQLite get(Long id) {
		return (UserSQLite) getSession().get(UserSQLite.class, id);
	}

	/**
	 * getByFileName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#getByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UserSQLite getByName(String filename) {
		return (UserSQLite) createQueryWithCache("From UserSQLite where filename = :filename").setParameter("filename", filename).uniqueResultOptional().orElse(null);
	}

	/**
	 * getByUserSQLiteIdAndUserLogin: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#getByUserSQLiteIdAndUserLogin(long,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UserSQLite getByIdAndUser(Long id, String username) {
		String query = "From UserSQLite where id = :id and user.login = :username";
		return (UserSQLite) createQueryWithCache(query).setParameter("id", id).setParameter("username", username).uniqueResultOptional().orElse(null);
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#getAllFromUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSQLite> getAllFromUser(String username) {
		return createQueryWithCache("From UserSQLite where user.login = :username order by created desc").setParameter("username", username).getResultList();
	}

	/**
	 * getAllFromUserLoginByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#getAllFromUserLoginByPageAndSizeIndex(java.lang.String,
	 *      int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSQLite> getAllFromUserByPageAndSizeIndex(String username, Integer pageIndex, Integer pageSize) {
		String query = "From UserSQLite where user.login = :username order by created desc";
		return createQueryWithCache(query).setParameter("username", username).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#save(lu.itrust.business.ts.model.general.document.impl.UserSQLite)
	 */
	@Override
	public UserSQLite save(UserSQLite userSqLite) {
		return (UserSQLite) getSession().save(userSqLite);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#saveOrUpdate(lu.itrust.business.ts.model.general.document.impl.UserSQLite)
	 */
	@Override
	public void saveOrUpdate(UserSQLite userSqLite) {
		getSession().saveOrUpdate(userSqLite);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#merge(lu.itrust.business.ts.model.general.document.impl.UserSQLite)
	 */
	@Override
	public UserSQLite merge(UserSQLite userSqLite) {
		return (UserSQLite) getSession().merge(userSqLite);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#delete(Long)
	 */
	@Override
	public void delete(Long id) {
		delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#delete(java.lang.String)
	 */
	@Override
	public void delete(String filename) {
		delete(getByName(filename));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUserSqLite#delete(lu.itrust.business.ts.model.general.document.impl.UserSQLite)
	 */
	@Override
	public void delete(UserSQLite userSqLite) {
		getSession().delete(userSqLite);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDistinctIdentifierByUser(User user) {
		return createQueryWithCache("Select distinct identifier From UserSQLite where user = :user order by identifier desc").setParameter("user", user).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserSQLite> getAllFromUserByPageAndFilterControl(String username, Integer page, FilterControl filter) {
		if (!filter.validate())
			return Collections.emptyList();
		if ("ALL".equalsIgnoreCase(filter.getFilter()))
			return createQueryWithCache(String.format("From UserSQLite where user.login = :username order by %s %s", filter.getSort(), filter.getDirection()))
					.setParameter("username", username).setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize()).getResultList();
		else
			return getSession()
					.createQuery(String.format("From UserSQLite where user.login = :username and identifier = :filter order by %s %s", filter.getSort(), filter.getDirection()))
					.setParameter("username", username).setParameter("filter", filter.getFilter()).setFirstResult((page - 1) * filter.getSize()).setMaxResults(filter.getSize())
					.getResultList();
	}

	@Override
	public void deleteByUser(User user) {
		createQueryWithCache("Delete From UserSQLite where user =:user").setParameter("user", user).executeUpdate();
	}

	@Override
	public List<UserSQLite> findByCreatedBefore(Date date, int page, int size) {
		return createQueryWithCache("From UserSQLite where created < :deleteDate", UserSQLite.class).setParameter("deleteDate", date).setFirstResult((page - 1) * size)
				.setMaxResults(size).list();
	}

	@Override
	public long countByCreatedBefore(Date date) {
		return createQueryWithCache("Select count(*) From UserSQLite where created < :deleteDate", Long.class).setParameter("deleteDate", date).uniqueResult();
	}
}