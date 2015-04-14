package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.general.UserSQLite;
import lu.itrust.business.TS.model.general.helper.FilterControl;
import lu.itrust.business.TS.usermanagement.User;

/**
 * DAOUserSqLite.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface DAOUserSqLite {
	public UserSQLite get(Integer id) throws Exception;

	public UserSQLite getByFilename(String filename) throws Exception;

	public UserSQLite getByIdAndUser(Integer idFile, String username) throws Exception;

	public List<UserSQLite> getAllFromUser(String username) throws Exception;

	public List<UserSQLite> getAllFromUserByPageAndSizeIndex(String username, Integer pageIndex, Integer pageSize) throws Exception;

	public UserSQLite save(UserSQLite userSqLite) throws Exception;

	public void saveOrUpdate(UserSQLite userSqLite) throws Exception;

	public UserSQLite merge(UserSQLite userSqLite) throws Exception;

	public void delete(Integer idUserSqLite) throws Exception;

	public void delete(String filename) throws Exception;

	public void delete(UserSQLite userSqLite) throws Exception;

	public List<String> getDistinctIdentifierByUser(User user);

	public List<UserSQLite> getAllFromUserByPageAndFilterControl(String username, Integer page, FilterControl filter);
}