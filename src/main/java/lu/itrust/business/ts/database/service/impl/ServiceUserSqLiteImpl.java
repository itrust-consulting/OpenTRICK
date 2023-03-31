package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOUserSqLite;
import lu.itrust.business.ts.database.service.ServiceUserSqLite;
import lu.itrust.business.ts.model.general.document.impl.UserSQLite;
import lu.itrust.business.ts.model.general.helper.FilterControl;
import lu.itrust.business.ts.usermanagement.User;

/**
 * ServiceUserSqLiteImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2014
 */
@Service
@Transactional(readOnly = true)
public class ServiceUserSqLiteImpl implements ServiceUserSqLite {

	@Autowired
	private DAOUserSqLite daoUserSqLite;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#get(Long)
	 */
	@Override
	public UserSQLite get(Long id)  {
		return daoUserSqLite.get(id);
	}

	/**
	 * getByFileName: <br>
	 * Description
	 * 
	 * @param filename
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#getByName(java.lang.String)
	 */
	@Override
	public UserSQLite getByName(String filename)  {
		return daoUserSqLite.getByName(filename);
	}

	/**
	 * getByUserSQLiteIdAndUserLogin: <br>
	 * Description
	 * 
	 * @param idFile
	 * @param username
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#getByUserSQLiteIdAndUserLogin(long,
	 *      java.lang.String)
	 */
	@Override
	public UserSQLite getByIdAndUser(Long id, String username)  {
		return daoUserSqLite.getByIdAndUser(id, username);
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @param username
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#getAllFromUser(java.lang.String)
	 */
	@Override
	public List<UserSQLite> getAllFromUser(String username)  {
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
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#getAllFromUserLoginByPageAndSizeIndex(java.lang.String,
	 *      int, int)
	 */
	@Override
	public List<UserSQLite> getAllFromUserByPageAndSizeIndex(String username, Integer pageIndex, Integer pageSize)  {
		return daoUserSqLite.getAllFromUserByPageAndSizeIndex(username, pageIndex, pageSize);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param userSqLite
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#save(lu.itrust.business.ts.model.general.document.impl.UserSQLite)
	 */
	@Transactional
	@Override
	public UserSQLite save(UserSQLite userSqLite)  {
		return daoUserSqLite.save(userSqLite);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param userSqLite
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#saveOrUpdate(lu.itrust.business.ts.model.general.document.impl.UserSQLite)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(UserSQLite userSqLite)  {
		daoUserSqLite.saveOrUpdate(userSqLite);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param userSqLite
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#merge(lu.itrust.business.ts.model.general.document.impl.UserSQLite)
	 */
	@Transactional
	@Override
	public UserSQLite merge(UserSQLite userSqLite)  {
		return daoUserSqLite.merge(userSqLite);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param idUserSqLite
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#delete(Long)
	 */
	@Transactional
	@Override
	public void delete(Long id)  {
		daoUserSqLite.delete(id);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param filename
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#delete(java.lang.String)
	 */
	@Transactional
	@Override
	public void delete(String filename)  {
		daoUserSqLite.delete(filename);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param userSqLite
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceUserSqLite#delete(lu.itrust.business.ts.model.general.document.impl.UserSQLite)
	 */
	@Transactional
	@Override
	public void delete(UserSQLite userSqLite)  {
		daoUserSqLite.delete(userSqLite);
	}

	@Override
	public List<String> getDistinctIdentifierByUser(User user) {
		return daoUserSqLite.getDistinctIdentifierByUser(user);
	}

	@Override
	public List<UserSQLite> getAllFromUserByFilterControl(String username, Integer page, FilterControl filter) {
		return daoUserSqLite.getAllFromUserByPageAndFilterControl(username,page, filter);
	}
}