/**
 * 
 */
package lu.itrust.business.ts.database.dao.hbm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import lu.itrust.business.ts.database.dao.DAOUser;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.TicketingSystem;
import lu.itrust.business.ts.usermanagement.Role;
import lu.itrust.business.ts.usermanagement.RoleType;
import lu.itrust.business.ts.usermanagement.User;

/**
 * DAOUserHBM.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl. :
 * @version
 * @since Feb , 2013
 */
@Repository
public class DAOUserHBM extends DAOHibernate implements DAOUser {

	/**
	 * Constructor: <br>
	 */
	public DAOUserHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOUserHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#get(int)
	 */
	@Override
	public User get(Integer id) {
		return (User) getSession().get(User.class, id);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#get(java.lang.String)
	 */
	@Override
	public User get(String login) {
		return getSession().createQuery("From User where login = :login", User.class).setParameter("login", login)
				.uniqueResultOptional().orElse(null);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#get(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public User get(String login, String password) {
		return getSession().createQuery("From User where login = :login and password = :password", User.class)
				.setParameter("login", login).setParameter("password", password)
				.uniqueResultOptional().orElse(null);
	}

	/**
	 * noUsers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#noUsers()
	 */
	@Override
	public boolean noUsers() {
		return getSession().createQuery("Select count(*) = 0 From User", Boolean.class).getSingleResult();
	}

	/**
	 * getAllUsers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#getAllUsers()
	 */
	@Override
	public List<User> getAll() {
		return getSession().createQuery("From User order by firstName", User.class).getResultList();
	}

	/**
	 * getAllByFirstName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#getAllByFirstName(java.lang.String)
	 */
	@Override
	public List<User> getAllByFirstName(String name) {
		return getSession().createQuery("From User where firstName = :name", User.class).setParameter("name", name)
				.getResultList();
	}

	/**
	 * getAllByCountry: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#getAllByCountry(java.lang.String)
	 */
	@Override
	public List<User> getAllByCountry(String name) {
		return getSession().createQuery("From User where country = :country", User.class).setParameter("country", name)
				.getResultList();
	}

	/**
	 * getAllUsersFromCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#getAllUsersFromCustomer(int)
	 */
	@Override
	public List<User> getAllFromCustomer(Integer customer) {
		return getSession().createQuery(
				"SELECT user From User as user inner join user.customers as customer where customer.id = :customer",
				User.class).setParameter("customer", customer)
				.getResultList();
	}

	/**
	 * getAllUsersFromCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#getAllUsersFromCustomer(lu.itrust.business.ts.model.general.Customer)
	 */
	@Override
	public List<User> getAllFromCustomer(Customer customer) {
		return getSession().createQuery(
				"SELECT user From User as user inner join user.customers as customer where customer = :customer",
				User.class).setParameter("customer", customer)
				.getResultList();
	}

	/**
	 * getAllAdministrators: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOUser#getAllAdministrators()
	 */
	@Override
	public List<User> getAllAdministrators() {
		return getSession().createQuery("SELECT user From User as user join user.roles as role where role.type = :role",
				User.class).setParameter("role", RoleType.ROLE_ADMIN).getResultList();
	}

	/**
	 * hasRole: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#hasRole(lu.itrust.business.ts.usermanagement.User,
	 *      lu.itrust.business.ts.usermanagement.Role)
	 */
	@Override
	public boolean hasRole(User user, Role role) {
		return !(user == null || role == null) && user.hasRole(role.getType());
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#save(lu.itrust.business.ts.usermanagement.User)
	 */
	@Override
	public void save(User user) {
		getSession().persist(user);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#saveOrUpdate(lu.itrust.business.ts.usermanagement.User)
	 */
	@Override
	public void saveOrUpdate(User user) {
		getSession().saveOrUpdate(user);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#delete(lu.itrust.business.ts.usermanagement.User)
	 */
	@Override
	public void delete(User user) {
		getSession().remove(user);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOUser#delete(long)
	 */
	@Override
	public void delete(Integer id) {
		delete(get(id));
	}

	@Override
	public User getByEmail(String email) {
		return (User) getSession().createQuery("From User where email = :email", User.class)
				.setParameter("email", email).uniqueResultOptional().orElse(null);
	}

	@Override
	public List<User> getAllOthers(Collection<User> users) {
		return getSession().createQuery("From User user where user not in :users", User.class)
				.setParameterList("users", users).getResultList();
	}

	@Override
	public List<User> getAllOthers(User user) {
		return getSession().createQuery("From User user where user <> :user", User.class).setParameter("user", user)
				.getResultList();
	}

	@Override
	public boolean existByUsername(String username) {
		return (boolean) getSession().createQuery("Select count(*)> 0 From User where login = :username", Boolean.class)
				.setParameter("username", username).getSingleResult();
	}

	@Override
	public boolean existByEmail(String email) {
		return (boolean) getSession().createQuery("Select count(*)> 0 From User where email = :email", Boolean.class)
				.setParameter("email", email).getSingleResult();
	}

	@Override
	public List<User> getAll(Collection<Integer> ids) {
		return ids.isEmpty() ? Collections.emptyList()
				: getSession().createQuery("From User where id in :ids", User.class).setParameterList("ids", ids)
						.getResultList();
	}

	@Override
	public String findLocaleByUsername(String username) {
		return getSession().createQuery("Select locale From User where login = :username", String.class)
				.setParameter("username", username).uniqueResultOptional()
				.filter(StringUtils::hasText).orElse("en");
	}

	@Override
	public String findUsernameById(Integer id) {
		return getSession().createQuery("Select login From User where id = :id", String.class).setParameter("id", id)
				.uniqueResult();
	}

	@Override
	public List<User> findByTicketingSystem(TicketingSystem ticketingSystem) {
		return getSession().createQuery(
				"Select user From User user inner join user.credentials credential where credential.ticketingSystem = :ticketingSystem",
				User.class).setParameter("ticketingSystem", ticketingSystem).getResultList();
	}

	@Override
	public boolean isUsing2FA(String username) {
		return getSession().createQuery(
				"Select count(*)>0 From User user join user.userSettings setting where user.login = :username and KEY(setting) = :name and VALUE(setting) = 'true'",
				Boolean.class).setParameter("username", username)
				.setParameter("name", User.USER_USING_2_FACTOR_AUTHENTICATION).uniqueResult();
	}
}