package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.general.document.impl.UserSQLite;
import lu.itrust.business.ts.model.general.helper.FilterControl;
import lu.itrust.business.ts.usermanagement.User;

/**
 * ServiceUserSqLite.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceUserSqLite {
	public UserSQLite get(Long id);

	public UserSQLite getByName(String filename);

	public UserSQLite getByIdAndUser(Long id, String username);

	public List<UserSQLite> getAllFromUser(String username);

	public List<UserSQLite> getAllFromUserByPageAndSizeIndex(String username, Integer pageIndex, Integer pageSize);

	public UserSQLite save(UserSQLite userSqLite);

	public void saveOrUpdate(UserSQLite userSqLite);

	public UserSQLite merge(UserSQLite userSqLite);

	public void delete(Long id);

	public void delete(String filename);

	public void delete(UserSQLite userSqLite);

	public List<String> getDistinctIdentifierByUser(User user);

	public List<UserSQLite> getAllFromUserByFilterControl(String username, Integer page, FilterControl filter);
}