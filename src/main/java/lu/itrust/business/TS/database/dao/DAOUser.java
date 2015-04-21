package lu.itrust.business.TS.database.dao;

import java.util.Collection;
import java.util.List;

import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;

/**
 * DAOUser.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 1, 2013
 */
public interface DAOUser {
	public User get(Integer id) throws Exception;

	public User get(String login) throws Exception;

	public User get(String login, String password) throws Exception;

	public boolean noUsers() throws Exception;

	public List<User> getAll() throws Exception;

	public List<User> getAllByFirstName(String name) throws Exception;

	public List<User> getAllByCountry(String name) throws Exception;

	public List<User> getAllFromCustomer(Integer customer) throws Exception;

	public List<User> getAllFromCustomer(Customer customer) throws Exception;

	public List<User> getAllAdministrators() throws Exception;
	
	public boolean hasRole(User user, Role role) throws Exception;

	public void save(User user) throws Exception;

	public void saveOrUpdate(User user) throws Exception;

	public void delete(Integer id) throws Exception;

	public void delete(User user) throws Exception;

	public User getByEmail(String email);

	public List<User> getAllOthers(Collection<User> users);
}