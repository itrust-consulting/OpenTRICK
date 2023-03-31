/**
 * 
 */
package lu.itrust.business.ts.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOResetPassword;
import lu.itrust.business.ts.usermanagement.ResetPassword;
import lu.itrust.business.ts.usermanagement.User;

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
	 * @see lu.itrust.business.ts.database.dao.DAOResetPassword#get(long)
	 */
	@Override
	public ResetPassword get(long id) {
		return (ResetPassword) getSession().get(ResetPassword.class, id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.dao.DAOResetPassword#get(lu.itrust.business.ts.usermanagement.User)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResetPassword get(User user) {
		return (ResetPassword) getSession().createQuery("From ResetPassword where user = :user").setParameter("user", user).uniqueResultOptional().orElse(null);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.dao.DAOResetPassword#get(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResetPassword get(String keyControl) {
		return (ResetPassword) getSession().createQuery("From ResetPassword where keyControl = :keyControl").setParameter("keyControl", keyControl).uniqueResultOptional().orElse(null);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.dao.DAOResetPassword#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResetPassword> getAll() {
		return getSession().createQuery("From ResetPassword").getResultList();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.dao.DAOResetPassword#getAll(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResetPassword> getAll(int page, int size) {
		return getSession().createQuery("From ResetPassword").setMaxResults(size).setFirstResult((page-1)*size).getResultList();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.dao.DAOResetPassword#saveOrUpdate(lu.itrust.business.ts.usermanagement.ResetPassword)
	 */
	@Override
	public void saveOrUpdate(ResetPassword resetPassword) {
		getSession().saveOrUpdate(resetPassword);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.dao.DAOResetPassword#save(lu.itrust.business.ts.usermanagement.ResetPassword)
	 */
	@Override
	public ResetPassword save(ResetPassword resetPassword) {
		return (ResetPassword) getSession().save(resetPassword);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.dao.DAOResetPassword#merge(lu.itrust.business.ts.usermanagement.ResetPassword)
	 */
	@Override
	public ResetPassword merge(ResetPassword resetPassword) {
		return (ResetPassword) getSession().merge(resetPassword);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.dao.DAOResetPassword#delete(lu.itrust.business.ts.usermanagement.ResetPassword)
	 */
	@Override
	public void delete(ResetPassword resetPassword) {
		getSession().delete(resetPassword);
	}

}
