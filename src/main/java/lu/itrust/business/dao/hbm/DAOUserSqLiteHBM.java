/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.usermanagement.UserSQLite;
import lu.itrust.business.dao.DAOUserSqLite;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * @author eomar
 * 
 */
@Repository
public class DAOUserSqLiteHBM extends DAOHibernate implements DAOUserSqLite {

	/**
	 * 
	 */
	public DAOUserSqLiteHBM() {
	}

	/**
	 * @param session
	 */
	public DAOUserSqLiteHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#findOne(long)
	 */
	@Override
	public UserSQLite findOne(long id) {
		return (UserSQLite) getSession().get(UserSQLite.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOUserSqLite#findByFileName(java.lang.String)
	 */
	@Override
	public UserSQLite findByFileName(String fileName) {
		return (UserSQLite) getSession().createQuery("From UserSQLite where fileName = :fileName").setParameter("fileName", fileName).uniqueResult();
	}

	@Override
	public UserSQLite findByIdAndUser(long idFile, String username) {
		return (UserSQLite) getSession().createQuery("From UserSQLite where id = :idFile and user.login = :username").setParameter("idFile", idFile)
				.setParameter("username", username).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#findByUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSQLite> findByUser(String username) {
		return getSession().createQuery("From UserSQLite where user.login = :username").setParameter("username", username).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#findByUser(java.lang.String,
	 * int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSQLite> findByUser(String username, int pageIndex, int pageSize) {
		return getSession().createQuery("From UserSQLite where user.login = :username").setParameter("username", username).setFirstResult((pageIndex - 1) * pageSize)
				.setMaxResults(pageSize).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#save(lu.itrust.business.TS.
	 * usermanagement.UserSqLite)
	 */
	@Override
	public UserSQLite save(UserSQLite userSqLite) {
		return (UserSQLite) getSession().save(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOUserSqLite#saveOrUpdate(lu.itrust.business
	 * .TS.usermanagement.UserSqLite)
	 */
	@Override
	public void saveOrUpdate(UserSQLite userSqLite) {
		getSession().saveOrUpdate(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#merge(lu.itrust.business.TS.
	 * usermanagement.UserSqLite)
	 */
	@Override
	public UserSQLite merge(UserSQLite userSqLite) {
		return (UserSQLite) getSession().merge(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#delete(lu.itrust.business.TS.
	 * usermanagement.UserSqLite)
	 */
	@Override
	public void delete(UserSQLite userSqLite) {
		getSession().delete(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#delete(long)
	 */
	@Override
	public void delete(long idUserSqLite) {
		delete(findOne(idUserSqLite));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#delete(java.lang.String)
	 */
	@Override
	public void delete(String fileName) {
		delete(findByFileName(fileName));
	}

}
