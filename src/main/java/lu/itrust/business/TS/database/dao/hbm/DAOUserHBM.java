/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;

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
	 * @see lu.itrust.business.TS.database.dao.DAOUser#get(int)
	 */
	@Override
	public User get(Integer id)  {
		return (User) getSession().get(User.class, id);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#get(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public User get(String login)  {
		return (User) getSession().createQuery("From User where login = :login").setParameter("login", login).uniqueResultOptional().orElse(null);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#get(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public User get(String login, String password)  {
		return (User) getSession().createQuery("From User where login = :login and password = :password").setParameter("login", login).setParameter("password", password).uniqueResultOptional().orElse(null);
	}

	/**
	 * noUsers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#noUsers()
	 */
	@Override
	public boolean noUsers()  {
		return (boolean) getSession().createQuery("Select count(*)>0 From User").getSingleResult();
	}

	/**
	 * getAllUsers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#getAllUsers()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAll()  {
		return getSession().createQuery("From User order by firstName").getResultList();
	}

	/**
	 * getAllByFirstName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#getAllByFirstName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllByFirstName(String name)  {
		return getSession().createQuery("From User where firstName = :name").setParameter("name", name).getResultList();
	}

	/**
	 * getAllByCountry: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#getAllByCountry(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllByCountry(String name)  {
		return getSession().createQuery("From User where country = :country").setParameter("country", name).getResultList();
	}

	/**
	 * getAllUsersFromCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#getAllUsersFromCustomer(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllFromCustomer(Integer customer)  {
		return getSession().createQuery("SELECT user From User as user inner join user.customers as customer where customer.id = :customer").setParameter("customer", customer).getResultList();
	}

	/**
	 * getAllUsersFromCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#getAllUsersFromCustomer(lu.itrust.business.TS.model.general.Customer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllFromCustomer(Customer customer)  {
		return getSession().createQuery("SELECT user From User as user inner join user.customers as customer where customer = :customer").setParameter("customer", customer).getResultList();
	}

	/**
	 * getAllAdministrators: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOUser#getAllAdministrators()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllAdministrators()  {
		return getSession().createQuery("SELECT user From User as user join user.roles as role where role.type = :role").setParameter("role", RoleType.ROLE_ADMIN).getResultList();
	}
	
	/**
	 * hasRole: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#hasRole(lu.itrust.business.TS.usermanagement.User,
	 *      lu.itrust.business.TS.usermanagement.Role)
	 */
	@Override
	public boolean hasRole(User user, Role role)  {
		return !(user==null || role==null) && user.hasRole(role.getType());
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#save(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public void save(User user)  {
		getSession().save(user);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#saveOrUpdate(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public void saveOrUpdate(User user)  {
		getSession().saveOrUpdate(user);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#delete(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public void delete(User user)  {
		getSession().delete(user);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUser#delete(long)
	 */
	@Override
	public void delete(Integer id)  {
		delete(get(id));
	}

	@SuppressWarnings("unchecked")
	@Override
	public User getByEmail(String email) {
		return (User) getSession().createQuery("From User where email = :email").setParameter("email", email).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllOthers(Collection<User> users) {
		return getSession().createQuery("From User user where user not in :users").setParameterList("users", users).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllOthers(User user) {
		return getSession().createQuery("From User user where user <> :user").setParameter("user", user).getResultList();
	}

	@Override
	public boolean existByUsername(String username) {
		return (boolean) getSession().createQuery("Select count(*)> 0 From User where login = :username").setParameter("username",username ).getSingleResult();
	}

	@Override
	public boolean existByEmail(String email) {
		return (boolean) getSession().createQuery("Select count(*)> 0 From User where email = :email").setParameter("email",email ).getSingleResult();
	}
}