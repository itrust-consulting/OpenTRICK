/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOResetPassword;
import lu.itrust.business.TS.usermanagement.ResetPassword;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Repository
public class DAOResetPasswordHBM extends DAOHibernate implements DAOResetPassword {
	
	/**
	 * 
	 */
	public DAOResetPasswordHBM() {
	}

	/**
	 * @param session
	 */
	public DAOResetPasswordHBM(Session session) {
		super(session);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.dao.DAOResetPassword#get(long)
	 */
	@Override
	public ResetPassword get(long id) {
		return (ResetPassword) getSession().get(ResetPassword.class, id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.dao.DAOResetPassword#get(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public ResetPassword get(User user) {
		return (ResetPassword) getSession().createQuery("From ResetPassword where user = :user").setParameter("user", user).uniqueResult();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.dao.DAOResetPassword#get(java.lang.String)
	 */
	@Override
	public ResetPassword get(String keyControl) {
		return (ResetPassword) getSession().createQuery("From ResetPassword where keyControl = :keyControl").setParameter("keyControl", keyControl).uniqueResult();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.dao.DAOResetPassword#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResetPassword> getAll() {
		return getSession().createQuery("From ResetPassword").list();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.dao.DAOResetPassword#getAll(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResetPassword> getAll(int page, int size) {
		return getSession().createQuery("From ResetPassword").setMaxResults(size).setFirstResult((page-1)*size).list();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.dao.DAOResetPassword#saveOrUpdate(lu.itrust.business.TS.usermanagement.ResetPassword)
	 */
	@Override
	public void saveOrUpdate(ResetPassword resetPassword) {
		getSession().saveOrUpdate(resetPassword);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.dao.DAOResetPassword#save(lu.itrust.business.TS.usermanagement.ResetPassword)
	 */
	@Override
	public ResetPassword save(ResetPassword resetPassword) {
		return (ResetPassword) getSession().save(resetPassword);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.dao.DAOResetPassword#merge(lu.itrust.business.TS.usermanagement.ResetPassword)
	 */
	@Override
	public ResetPassword merge(ResetPassword resetPassword) {
		return (ResetPassword) getSession().merge(resetPassword);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.dao.DAOResetPassword#delete(lu.itrust.business.TS.usermanagement.ResetPassword)
	 */
	@Override
	public void delete(ResetPassword resetPassword) {
		getSession().delete(resetPassword);
	}

}
