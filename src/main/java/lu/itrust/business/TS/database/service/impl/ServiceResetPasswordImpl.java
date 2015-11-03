/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lu.itrust.business.TS.database.dao.DAOResetPassword;
import lu.itrust.business.TS.database.service.ServiceResetPassword;
import lu.itrust.business.TS.usermanagement.ResetPassword;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Service
public class ServiceResetPasswordImpl implements ServiceResetPassword {

	@Autowired
	private DAOResetPassword daoResetPassword;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceResetPassword#get(long)
	 */
	@Override
	public ResetPassword get(long id) {
		return daoResetPassword.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceResetPassword#get(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public ResetPassword get(User user) {
		return daoResetPassword.get(user);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceResetPassword#get(java.lang.String)
	 */
	@Override
	public ResetPassword get(String keyControl) {
		return daoResetPassword.get(keyControl);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceResetPassword#getAll()
	 */
	@Override
	public List<ResetPassword> getAll() {
		return daoResetPassword.getAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceResetPassword#getAll(int, int)
	 */
	@Override
	public List<ResetPassword> getAll(int page, int size) {
		return daoResetPassword.getAll(page, size);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceResetPassword#saveOrUpdate(lu.itrust.business.TS.usermanagement.ResetPassword)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ResetPassword resetPassword) {
		daoResetPassword.saveOrUpdate(resetPassword);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceResetPassword#save(lu.itrust.business.TS.usermanagement.ResetPassword)
	 */
	@Transactional
	@Override
	public ResetPassword save(ResetPassword resetPassword) {
		return daoResetPassword.save(resetPassword);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceResetPassword#merge(lu.itrust.business.TS.usermanagement.ResetPassword)
	 */
	@Transactional
	@Override
	public ResetPassword merge(ResetPassword resetPassword) {
		return daoResetPassword.merge(resetPassword);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceResetPassword#delete(lu.itrust.business.TS.usermanagement.ResetPassword)
	 */
	@Transactional
	@Override
	public void delete(ResetPassword resetPassword) {
		daoResetPassword.delete(resetPassword);
	}

}
