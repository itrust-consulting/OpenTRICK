package lu.itrust.business.ts.database.dao;

import java.util.Collection;
import java.util.List;

import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.TicketingSystem;
import lu.itrust.business.ts.usermanagement.Role;
import lu.itrust.business.ts.usermanagement.User;

/**
 * DAOUser.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Jan 1, 2013
 */
public interface DAOUser {
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

	public List<User> getAllOthers(User user);

	public boolean existByUsername(String username);

	public boolean existByEmail(String email);

	public List<User> getAll(Collection<Integer> ids);

	public String findLocaleByUsername(String username);

	public String findUsernameById(Integer id);

	public List<User> findByTicketingSystem(TicketingSystem ticketingSystem);

    public boolean isUsing2FA(String username);

    public String findUsernameByEmail(String email);
}