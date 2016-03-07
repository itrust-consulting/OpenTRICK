package lu.itrust.business.TS.database.dao;

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
	public Role get(Integer id) ;

	public Role getByName(String name) ;

	public List<Role> getAll() ;

	public List<Role> getAllFromUser(String login) ;

	public List<Role> getAllFromUser(User user) ;

	public void save(Role role) ;

	public void saveOrUpdate(Role role) ;

	public void delete(Integer id) ;

	public void delete(String login) ;

	public void delete(Role role) ;

	public void delete(User user) ;
}