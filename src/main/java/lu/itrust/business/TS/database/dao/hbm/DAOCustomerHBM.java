package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.database.dao.DAOCustomer;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOCustomerHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
@Repository
public class DAOCustomerHBM extends DAOHibernate implements DAOCustomer {

	/**
	 * Constructor: <br>
	 */
	public DAOCustomerHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOCustomerHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#get(int)
	 */
	@Override
	public Customer get(Integer id) throws Exception {
		return (Customer) getSession().get(Customer.class, id);
	}

	/**
	 * getProfileCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getProfileCustomer()
	 */
	@Override
	public Customer getProfile() throws Exception {
		return (Customer) getSession().createQuery("From Customer where canBeUsed = false").uniqueResult();
	}

	/**
	 * getCustomerFromContactPerson: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getCustomerFromContactPerson(java.lang.String)
	 */
	@Override
	public Customer getFromContactPerson(String contactPerson) throws Exception {
		return (Customer) getSession().createQuery("From Customer where contactPerson = :contactPerson").setParameter("contactPerson", contactPerson).uniqueResult();
	}

	/**
	 * isProfileCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#isProfileCustomer(int)
	 */
	@Override
	public boolean isProfile(Integer idCustomer) throws Exception {
		Boolean result = (Boolean) getSession().createQuery("Select customer.canBeUsed From Customer as customer where customer.id = :idCustomer")
				.setParameter("idCustomer", idCustomer).uniqueResult();
		return result == null ? false : !result;
	}

	/**
	 * customerProfileExists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#customerProfileExists()
	 */
	@Override
	public boolean profileExists() throws Exception {
		return ((Long) getSession().createQuery("Select count(customer) From Customer as customer where customer.canBeUsed = false").uniqueResult()).intValue() == 1;
	}

	/**
	 * customerHasUsers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#customerHasUsers(int)
	 */
	@Override
	public boolean hasUsers(Integer idCustomer) throws Exception {
		String query = "Select count(customer) From User as user inner join user.customers as customer where customer.id = :idCustomer";
		return ((Long) getSession().createQuery(query).setParameter("idCustomer", idCustomer).uniqueResult()).intValue() != 0;
	}

	/**
	 * customerExistsByOrganisation: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#customerExistsByOrganisation(java.lang.String)
	 */
	@Override
	public boolean existsByOrganisation(String organisation) throws Exception {
		String query = "Select count(customer) From Customer as customer where customer.organisation = :organisation";
		return ((Long) getSession().createQuery(query).setParameter("organisation", organisation).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAllCustomers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getAllCustomers()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAll() throws Exception {
		return (List<Customer>) getSession().createQuery("From Customer").list();
	}

	/**
	 * getAllCustomersAndProfileOfUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getAllCustomersAndProfileOfUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllAndProfileOfUser(String username) throws Exception {
		String query = "Select customer From Customer as customer where customer.canBeUsed = false or customer in (select customer1 From User as user inner join user.customers as customer1";
		query += " where user.login = :username)  order by customer.canBeUsed asc, customer.organisation asc, customer.contactPerson asc";
		return getSession().createQuery(query).setParameter("username", username).list();
	}

	/**
	 * getAllCustomersNoProfilesOfUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getAllCustomersNoProfilesOfUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllNotProfileOfUser(String username) throws Exception {
		String query = "Select customer From User as user inner join user.customers as customer where user.login = :username and customer.canBeUsed = true order by customer.organisation ";
		query += "asc, customer.contactPerson asc";
		return getSession().createQuery(query).setParameter("username", username).list();
	}

	/**
	 * getAllCustomersNoProfiles: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getAllCustomersNoProfiles()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllNotProfiles() throws Exception {
		return (List<Customer>) getSession().createQuery("From Customer WHERE canBeUsed=true").list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#save(lu.itrust.business.TS.data.general.Customer)
	 */
	@Override
	public void save(Customer customer) throws Exception {
		getSession().save(customer);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#saveOrUpdate(lu.itrust.business.TS.data.general.Customer)
	 */
	@Override
	public void saveOrUpdate(Customer customer) throws Exception {
		getSession().saveOrUpdate(customer);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#delete(lu.itrust.business.TS.data.general.Customer)
	 */
	@Override
	public void delete(Customer customer) throws Exception {
		getSession().delete(customer);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#delete(java.lang.Integer)
	 */
	@Override
	public void delete(Integer customerId) throws Exception {
		getSession().createQuery("delete from Analysis where customer.id = :customerId").setParameter("customerId", customerId).executeUpdate();
		getSession().createQuery("delete from Customer where id = :customerId").setParameter("customerId", customerId).executeUpdate();
	}

	@Override
	public Customer getOneNoProfile() {
		return (Customer) getSession().createQuery("From Customer where canBeUsed = true").setMaxResults(1).uniqueResult();
	}

	@Override
	public Customer getFromUsernameAndId(String username, int idCustomer) {
		String query = "Select customer From User as user inner join user.customers as customer where user.login = :username and customer.id = :idCustomer";
		return (Customer) getSession().createQuery(query).setParameter("username", username).setInteger("idCustomer", idCustomer).uniqueResult();
	}

	@Override
	public boolean exists(int idCustomer) {
		return (boolean) getSession().createQuery("Select count(*)>0 From Customer where id = :idCustomer").setInteger("idCustomer", idCustomer).uniqueResult();
	}
}