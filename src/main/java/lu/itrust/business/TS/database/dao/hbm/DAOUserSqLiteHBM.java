/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.data.general.UserSQLite;
import lu.itrust.business.TS.database.dao.DAOUserSqLite;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

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
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#get(long)
	 */
	@Override
	public UserSQLite get(Integer id) throws Exception {
		return (UserSQLite) getSession().get(UserSQLite.class, id);
	}

	/**
	 * getByFileName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#getByFileName(java.lang.String)
	 */
	@Override
	public UserSQLite getByFileName(String fileName) throws Exception {
		return (UserSQLite) getSession().createQuery("From UserSQLite where fileName = :fileName").setParameter("fileName", fileName).uniqueResult();
	}

	/**
	 * getByUserSQLiteIdAndUserLogin: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#getByUserSQLiteIdAndUserLogin(long,
	 *      java.lang.String)
	 */
	@Override
	public UserSQLite getByIdAndUser(Integer idFile, String username) throws Exception {
		String query = "From UserSQLite where id = :idFile and user.login = :username";
		return (UserSQLite) getSession().createQuery(query).setParameter("idFile", idFile).setParameter("username", username).uniqueResult();
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#getAllFromUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSQLite> getAllFromUser(String username) throws Exception {
		return getSession().createQuery("From UserSQLite where user.login = :username").setParameter("username", username).list();
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
	public List<UserSQLite> getAllFromUserByPageAndSizeIndex(String username, Integer pageIndex, Integer pageSize) throws Exception {
		String query = "From UserSQLite where user.login = :username";
		return getSession().createQuery(query).setParameter("username", username).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#save(lu.itrust.business.TS.data.general.UserSQLite)
	 */
	@Override
	public UserSQLite save(UserSQLite userSqLite) throws Exception {
		return (UserSQLite) getSession().save(userSqLite);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#saveOrUpdate(lu.itrust.business.TS.data.general.UserSQLite)
	 */
	@Override
	public void saveOrUpdate(UserSQLite userSqLite) throws Exception {
		getSession().saveOrUpdate(userSqLite);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#merge(lu.itrust.business.TS.data.general.UserSQLite)
	 */
	@Override
	public UserSQLite merge(UserSQLite userSqLite) throws Exception {
		return (UserSQLite) getSession().merge(userSqLite);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#delete(long)
	 */
	@Override
	public void delete(Integer idUserSqLite) throws Exception {
		delete(get(idUserSqLite));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#delete(java.lang.String)
	 */
	@Override
	public void delete(String fileName) throws Exception {
		delete(getByFileName(fileName));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserSqLite#delete(lu.itrust.business.TS.data.general.UserSQLite)
	 */
	@Override
	public void delete(UserSQLite userSqLite) throws Exception {
		getSession().delete(userSqLite);
	}
}