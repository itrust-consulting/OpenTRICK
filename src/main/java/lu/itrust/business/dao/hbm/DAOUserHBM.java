/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOUser;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

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
	 * @see lu.itrust.business.dao.DAOUser#get(int)
	 */
	@Override
	public User get(int id) throws Exception {
		return (User) getSession().get(User.class, id);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#get(java.lang.String)
	 */
	@Override
	public User get(String login) throws Exception {
		return (User) getSession().createQuery("From User where login = :login").setString("login", login).uniqueResult();
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#get(java.lang.String, java.lang.String)
	 */
	@Override
	public User get(String login, String password) throws Exception {
		return (User) getSession().createQuery("From User where login = :login and password = :password").setString("login", login).setString("password", password).uniqueResult();
	}

	/**
	 * noUsers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#noUsers()
	 */
	@Override
	public boolean noUsers() throws Exception {
		return ((Long) getSession().createQuery("Select count(*) From User").uniqueResult()).intValue() == 0;
	}

	/**
	 * getAllUsers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#getAllUsers()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllUsers() throws Exception {
		return getSession().createQuery("From User order by firstName").list();
	}

	/**
	 * getAllByFirstName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#getAllByFirstName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllByFirstName(String name) throws Exception {
		return getSession().createQuery("From User where firstName = :name").setString("name", name).list();
	}

	/**
	 * getAllByCountry: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#getAllByCountry(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllByCountry(String name) throws Exception {
		return getSession().createQuery("From User where country = :country").setString("country", name).list();
	}

	/**
	 * getAllUsersFromCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#getAllUsersFromCustomer(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllUsersFromCustomer(int customer) throws Exception {
		return getSession().createQuery("SELECT user From User as user inner join user.customers as customer where customer.id = :customer").setInteger("customer", customer).list();
	}

	/**
	 * getAllUsersFromCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#getAllUsersFromCustomer(lu.itrust.business.TS.Customer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllUsersFromCustomer(Customer customer) throws Exception {
		return getSession().createQuery("SELECT user From User as user inner join user.customers as customer where customer = :customer").setParameter("customer", customer).list();
	}

	/**
	 * hasRole: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#hasRole(lu.itrust.business.TS.usermanagement.User,
	 *      lu.itrust.business.TS.usermanagement.Role)
	 */
	@Override
	public boolean hasRole(User user, Role role) throws Exception {
		return user.hasRole(role.getType());
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#save(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public void save(User user) throws Exception {
		getSession().save(user);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#saveOrUpdate(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public void saveOrUpdate(User user) throws Exception {
		getSession().saveOrUpdate(user);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#delete(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public void delete(User user) throws Exception {
		getSession().delete(user);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUser#delete(long)
	 */
	@Override
	public void delete(int id) throws Exception {
		delete(get(id));
	}
}