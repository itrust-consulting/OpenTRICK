package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.usermanagement.Role;
import lu.itrust.business.ts.usermanagement.RoleType;
import lu.itrust.business.ts.usermanagement.User;

/**
 * ServiceRole.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceRole {
	public Role get(Integer id);

	public Role getByName(String name);
	
	public Role getByType(RoleType type);

	public List<Role> getAll();

	public List<Role> getAllFromUser(String login);

	public List<Role> getAllFromUser(User user);

	public void save(Role role);

	public void saveOrUpdate(Role role);

	public void delete(Integer id);

	public void delete(String login);

	public void delete(Role role);

	public void delete(User user);
}