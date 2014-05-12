package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Customer;
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

	public User get(int id) throws Exception;

	public User get(String login) throws Exception;

	public User get(String login, String password) throws Exception;

	public boolean noUsers() throws Exception;

	public List<User> getAllUsers() throws Exception;

	public List<User> getAllByFirstName(String name) throws Exception;

	public List<User> getAllByCountry(String name) throws Exception;

	public List<User> getAllUsersFromCustomer(int customer) throws Exception;

	public List<User> getAllUsersFromCustomer(Customer customer) throws Exception;

	public boolean hasRole(User user, Role role) throws Exception;

	public void save(User user) throws Exception;

	public void saveOrUpdate(User user) throws Exception;

	public void delete(int id) throws Exception;

	public void delete(User user) throws Exception;
}