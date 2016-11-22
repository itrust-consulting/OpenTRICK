package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.general.UserSQLite;
import lu.itrust.business.TS.model.general.helper.FilterControl;
import lu.itrust.business.TS.usermanagement.User;

/**
 * ServiceUserSqLite.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceUserSqLite {
	public UserSQLite get(Integer id);

	public UserSQLite getByFilename(String filename);

	public UserSQLite getByIdAndUser(Integer id, String username);

	public List<UserSQLite> getAllFromUser(String username);

	public List<UserSQLite> getAllFromUserByPageAndSizeIndex(String username, Integer pageIndex, Integer pageSize);

	public UserSQLite save(UserSQLite userSqLite);

	public void saveOrUpdate(UserSQLite userSqLite);

	public UserSQLite merge(UserSQLite userSqLite);

	public void delete(Integer id);

	public void delete(String filename);

	public void delete(UserSQLite userSqLite);

	public List<String> getDistinctIdentifierByUser(User user);

	public List<UserSQLite> getAllFromUserByFilterControl(String username, Integer page, FilterControl filter);
}