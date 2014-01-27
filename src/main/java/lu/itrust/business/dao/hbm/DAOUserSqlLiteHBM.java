/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.usermanagement.UserSqlLite;
import lu.itrust.business.dao.DAOUserSqlLite;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * @author eomar
 * 
 */
@Repository
public class DAOUserSqlLiteHBM extends DAOHibernate implements DAOUserSqlLite {

	/**
	 * 
	 */
	public DAOUserSqlLiteHBM() {
	}

	/**
	 * @param session
	 */
	public DAOUserSqlLiteHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqlLite#findOne(long)
	 */
	@Override
	public UserSqlLite findOne(long id) {
		return (UserSqlLite) getSession().get(UserSqlLite.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOUserSqlLite#findByFileName(java.lang.String)
	 */
	@Override
	public UserSqlLite findByFileName(String fileName) {
		return (UserSqlLite) getSession().createQuery("From UserSqlLite where fileName = :fileName").setParameter("fileName", fileName).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqlLite#findByUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSqlLite> findByUser(String username) {
		return getSession().createQuery("From UserSqlLite where user.login = :username").setParameter("username", username).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqlLite#findByUser(java.lang.String,
	 * int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSqlLite> findByUser(String username, int pageIndex, int pageSize) {
		return getSession().createQuery("From UserSqlLite where user.login = :username").setParameter("username", username).setFirstResult((pageIndex - 1) * pageSize)
				.setMaxResults(pageSize).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqlLite#save(lu.itrust.business.TS.
	 * usermanagement.UserSqlLite)
	 */
	@Override
	public UserSqlLite save(UserSqlLite userSqlLite) {
		return (UserSqlLite) getSession().save(userSqlLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOUserSqlLite#saveOrUpdate(lu.itrust.business
	 * .TS.usermanagement.UserSqlLite)
	 */
	@Override
	public void saveOrUpdate(UserSqlLite userSqlLite) {
		getSession().saveOrUpdate(userSqlLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqlLite#merge(lu.itrust.business.TS.
	 * usermanagement.UserSqlLite)
	 */
	@Override
	public UserSqlLite merge(UserSqlLite userSqlLite) {
		return (UserSqlLite) getSession().merge(userSqlLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqlLite#delete(lu.itrust.business.TS.
	 * usermanagement.UserSqlLite)
	 */
	@Override
	public void delete(UserSqlLite userSqlLite) {
		getSession().delete(userSqlLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqlLite#delete(long)
	 */
	@Override
	public void delete(long idUserSqlLite) {
		delete(findOne(idUserSqlLite));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqlLite#delete(java.lang.String)
	 */
	@Override
	public void delete(String fileName) {
		delete(findByFileName(fileName));
	}

}
