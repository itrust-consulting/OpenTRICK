/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.usermanagement.UserSQLite;
import lu.itrust.business.dao.DAOUserSqLite;
import lu.itrust.business.service.ServiceUserSqLite;

/**
 * @author eomar
 * 
 */
@Service
public class ServiceUserSqLiteImpl implements ServiceUserSqLite {

	@Autowired
	private DAOUserSqLite daoUserSqLite;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#findOne(long)
	 */
	@Override
	public UserSQLite findOne(long id) {
		return daoUserSqLite.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqLite#findByFileName(java.lang
	 * .String)
	 */
	@Override
	public UserSQLite findByFileName(String fileName) {
		return daoUserSqLite.findByFileName(fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqLite#findByUser(java.lang.String
	 * )
	 */
	@Override
	public List<UserSQLite> findByUser(String username) {
		return daoUserSqLite.findByUser(username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqLite#findByUser(java.lang.String
	 * , int, int)
	 */
	@Override
	public List<UserSQLite> findByUser(String username, int pageIndex, int pageSize) {
		return daoUserSqLite.findByUser(username, pageIndex, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqLite#save(lu.itrust.business
	 * .TS.usermanagement.UserSqLite)
	 */
	@Transactional
	@Override
	public UserSQLite save(UserSQLite userSqLite) {
		return daoUserSqLite.save(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqLite#saveOrUpdate(lu.itrust.
	 * business.TS.usermanagement.UserSqLite)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(UserSQLite userSqLite) {
		daoUserSqLite.saveOrUpdate(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqLite#merge(lu.itrust.business
	 * .TS.usermanagement.UserSqLite)
	 */
	@Transactional
	@Override
	public UserSQLite merge(UserSQLite userSqLite) {
		return daoUserSqLite.merge(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqLite#delete(lu.itrust.business
	 * .TS.usermanagement.UserSqLite)
	 */
	@Transactional
	@Override
	public void delete(UserSQLite userSqLite) {
		daoUserSqLite.delete(userSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#delete(long)
	 */
	@Transactional
	@Override
	public void delete(long idUserSqLite) {
		daoUserSqLite.delete(idUserSqLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqLite#delete(java.lang.String)
	 */
	@Transactional
	@Override
	public void delete(String fileName) {
		daoUserSqLite.delete(fileName);
	}

	@Override
	public UserSQLite findByIdAndUser(long idFile, String username) {
		return daoUserSqLite.findByIdAndUser(idFile, username);
	}

}
