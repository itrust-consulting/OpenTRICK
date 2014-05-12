package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.usermanagement.UserSQLite;

/**
 * DAOUserSqLite.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface DAOUserSqLite {

	public UserSQLite get(long id) throws Exception;

	public UserSQLite getByFileName(String fileName) throws Exception;

	public UserSQLite getByUserSQLiteIdAndUserLogin(long idFile, String username) throws Exception;

	public List<UserSQLite> getAllFromUser(String username) throws Exception;

	public List<UserSQLite> getAllFromUserLoginByPageAndSizeIndex(String username, int pageIndex, int pageSize) throws Exception;

	public UserSQLite save(UserSQLite userSqLite) throws Exception;

	public void saveOrUpdate(UserSQLite userSqLite) throws Exception;

	public UserSQLite merge(UserSQLite userSqLite) throws Exception;

	public void delete(long idUserSqLite) throws Exception;

	public void delete(String fileName) throws Exception;

	public void delete(UserSQLite userSqLite) throws Exception;
}