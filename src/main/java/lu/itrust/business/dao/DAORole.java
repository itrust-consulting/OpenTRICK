package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;

/**
 * DAORole.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 1, 2013
 */
public interface DAORole {

	public Role get(long id) throws Exception;

	public Role getRoleByName(String name) throws Exception;

	public List<Role> getAllRoles() throws Exception;

	public List<Role> getFromUser(String login) throws Exception;

	public List<Role> getFromUser(User user) throws Exception;

	public void save(Role role) throws Exception;

	public void saveOrUpdate(Role role) throws Exception;

	public void delete(long id) throws Exception;

	public void delete(String login) throws Exception;

	public void delete(Role role) throws Exception;

	public void delete(User user) throws Exception;
}