package lu.itrust.business.TS.database.service;

import java.util.Collection;
import java.util.List;

import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;

/**
 * ServiceUser.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 1, 2013
 */
public interface ServiceUser {
	public User get(Integer id);

	public User get(String login);

	public User get(String login, String password);

	public boolean noUsers();

	public List<User> getAll();

	public List<User> getAllByFirstName(String name);

	public List<User> getAllByCountry(String name);

	public List<User> getAllFromCustomer(Integer customer);

	public List<User> getAllFromCustomer(Customer customer);

	public List<User> getAllAdministrators();
	
	public boolean hasRole(User user, Role role);

	public void save(User user);

	public void saveOrUpdate(User user);

	public void delete(Integer id);

	public void delete(User user);

	public User getByEmail(String email);

	public List<User> getAllOthers(Collection<User> users);

	public List<User> getAllOthers(User owner);

	public boolean existByUsername(String username);

	public boolean existByEmail(String email);

	public List<User> getAll(Collection<Integer> ids);

	public String findUsernameById(Integer id);
}