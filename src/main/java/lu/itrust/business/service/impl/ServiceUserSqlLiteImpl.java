/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.usermanagement.UserSqlLite;
import lu.itrust.business.dao.DAOUserSqlLite;
import lu.itrust.business.service.ServiceUserSqlLite;

/**
 * @author eomar
 * 
 */
@Service
public class ServiceUserSqlLiteImpl implements ServiceUserSqlLite {

	@Autowired
	private DAOUserSqlLite daoUserSqlLite;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqlLite#findOne(long)
	 */
	@Override
	public UserSqlLite findOne(long id) {
		return daoUserSqlLite.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqlLite#findByFileName(java.lang
	 * .String)
	 */
	@Override
	public UserSqlLite findByFileName(String fileName) {
		return daoUserSqlLite.findByFileName(fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqlLite#findByUser(java.lang.String
	 * )
	 */
	@Override
	public List<UserSqlLite> findByUser(String username) {
		return daoUserSqlLite.findByUser(username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqlLite#findByUser(java.lang.String
	 * , int, int)
	 */
	@Override
	public List<UserSqlLite> findByUser(String username, int pageIndex, int pageSize) {
		return daoUserSqlLite.findByUser(username, pageIndex, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqlLite#save(lu.itrust.business
	 * .TS.usermanagement.UserSqlLite)
	 */
	@Transactional
	@Override
	public UserSqlLite save(UserSqlLite userSqlLite) {
		return daoUserSqlLite.save(userSqlLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqlLite#saveOrUpdate(lu.itrust.
	 * business.TS.usermanagement.UserSqlLite)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(UserSqlLite userSqlLite) {
		daoUserSqlLite.saveOrUpdate(userSqlLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqlLite#merge(lu.itrust.business
	 * .TS.usermanagement.UserSqlLite)
	 */
	@Transactional
	@Override
	public UserSqlLite merge(UserSqlLite userSqlLite) {
		return daoUserSqlLite.merge(userSqlLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqlLite#delete(lu.itrust.business
	 * .TS.usermanagement.UserSqlLite)
	 */
	@Transactional
	@Override
	public void delete(UserSqlLite userSqlLite) {
		daoUserSqlLite.delete(userSqlLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqlLite#delete(long)
	 */
	@Transactional
	@Override
	public void delete(long idUserSqlLite) {
		daoUserSqlLite.delete(idUserSqlLite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceUserSqlLite#delete(java.lang.String)
	 */
	@Transactional
	@Override
	public void delete(String fileName) {
		daoUserSqlLite.delete(fileName);
	}

}
