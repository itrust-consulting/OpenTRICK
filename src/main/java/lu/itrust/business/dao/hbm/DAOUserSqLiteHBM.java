/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.usermanagement.UserSqLite;
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
	public UserSqLite findOne(long id) {
		return (UserSqLite) getSession().get(UserSqLite.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOUserSqLite#findByFileName(java.lang.String)
	 */
	@Override
	public UserSqLite findByFileName(String fileName) {
		return (UserSqLite) getSession().createQuery("From UserSqLite where fileName = :fileName").setParameter("fileName", fileName).uniqueResult();
	}

	@Override
	public UserSqLite findByIdAndUser(long idFile, String username) {
		return (UserSqLite) getSession().createQuery("From UserSqLite where id = :idFile and user.login = :username").setParameter("idFile", idFile)
				.setParameter("username", username).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#findByUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSqLite> findByUser(String username) {
		return getSession().createQuery("From UserSqLite where user.login = :username").setParameter("username", username).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#findByUser(java.lang.String,
	 * int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserSqLite> findByUser(String username, int pageIndex, int pageSize) {
		return getSession().createQuery("From UserSqLite where user.login = :username").setParameter("username", username).setFirstResult((pageIndex - 1) * pageSize)
				.setMaxResults(pageSize).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#save(lu.itrust.business.TS.
	 * usermanagement.UserSqLite)
	 */
	@Override
	public UserSqLite save(UserSqLite userSqLite) {
		return (UserSqLite) getSession().save(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOUserSqLite#saveOrUpdate(lu.itrust.business
	 * .TS.usermanagement.UserSqLite)
	 */
	@Override
	public void saveOrUpdate(UserSqLite userSqLite) {
		getSession().saveOrUpdate(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#merge(lu.itrust.business.TS.
	 * usermanagement.UserSqLite)
	 */
	@Override
	public UserSqLite merge(UserSqLite userSqLite) {
		return (UserSqLite) getSession().merge(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUserSqLite#delete(lu.itrust.business.TS.
	 * usermanagement.UserSqLite)
	 */
	@Override
	public void delete(UserSqLite userSqLite) {
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
