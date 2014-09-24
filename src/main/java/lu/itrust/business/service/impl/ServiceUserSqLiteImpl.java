package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.export.UserSQLite;
import lu.itrust.business.dao.DAOUserSqLite;
import lu.itrust.business.service.ServiceUserSqLite;

/**
 * ServiceUserSqLiteImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2014
 */
@Service
public class ServiceUserSqLiteImpl implements ServiceUserSqLite {

	@Autowired
	private DAOUserSqLite daoUserSqLite;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#get(long)
	 */
	@Override
	public UserSQLite get(Integer id) throws Exception {
		return daoUserSqLite.get(id);
	}

	/**
	 * getByFileName: <br>
	 * Description
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#getByFileName(java.lang.String)
	 */
	@Override
	public UserSQLite getByFileName(String fileName) throws Exception {
		return daoUserSqLite.getByFileName(fileName);
	}

	/**
	 * getByUserSQLiteIdAndUserLogin: <br>
	 * Description
	 * 
	 * @param idFile
	 * @param username
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#getByUserSQLiteIdAndUserLogin(long,
	 *      java.lang.String)
	 */
	@Override
	public UserSQLite getByIdAndUser(Integer idFile, String username) throws Exception {
		return daoUserSqLite.getByIdAndUser(idFile, username);
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#getAllFromUser(java.lang.String)
	 */
	@Override
	public List<UserSQLite> getAllFromUser(String username) throws Exception {
		return daoUserSqLite.getAllFromUser(username);
	}

	/**
	 * getAllFromUserLoginByPageAndSizeIndex: <br>
	 * Description
	 * 
	 * @param username
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#getAllFromUserLoginByPageAndSizeIndex(java.lang.String,
	 *      int, int)
	 */
	@Override
	public List<UserSQLite> getAllFromUserByPageAndSizeIndex(String username, Integer pageIndex, Integer pageSize) throws Exception {
		return daoUserSqLite.getAllFromUserByPageAndSizeIndex(username, pageIndex, pageSize);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param userSqLite
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#save(lu.itrust.business.TS.export.UserSQLite)
	 */
	@Transactional
	@Override
	public UserSQLite save(UserSQLite userSqLite) throws Exception {
		return daoUserSqLite.save(userSqLite);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param userSqLite
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#saveOrUpdate(lu.itrust.business.TS.export.UserSQLite)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(UserSQLite userSqLite) throws Exception {
		daoUserSqLite.saveOrUpdate(userSqLite);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param userSqLite
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#merge(lu.itrust.business.TS.export.UserSQLite)
	 */
	@Transactional
	@Override
	public UserSQLite merge(UserSQLite userSqLite) throws Exception {
		return daoUserSqLite.merge(userSqLite);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param idUserSqLite
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#delete(long)
	 */
	@Transactional
	@Override
	public void delete(Integer idUserSqLite) throws Exception {
		daoUserSqLite.delete(idUserSqLite);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param fileName
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#delete(java.lang.String)
	 */
	@Transactional
	@Override
	public void delete(String fileName) throws Exception {
		daoUserSqLite.delete(fileName);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param userSqLite
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUserSqLite#delete(lu.itrust.business.TS.export.UserSQLite)
	 */
	@Transactional
	@Override
	public void delete(UserSQLite userSqLite) throws Exception {
		daoUserSqLite.delete(userSqLite);
	}
}