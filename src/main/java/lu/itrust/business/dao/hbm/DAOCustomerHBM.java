package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.dao.DAOCustomer;

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
	 * @see lu.itrust.business.dao.DAOCustomer#get(int)
	 */
	@Override
	public Customer get(int id) throws Exception {
		return (Customer) getSession().get(Customer.class, id);
	}

	/**
	 * getProfileCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#getProfileCustomer()
	 */
	@Override
	public Customer getProfileCustomer() throws Exception {
		return (Customer) getSession().createQuery("From Customer where canBeUsed = false").uniqueResult();
	}

	/**
	 * getCustomerFromContactPerson: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#getCustomerFromContactPerson(java.lang.String)
	 */
	@Override
	public Customer getCustomerFromContactPerson(String contactPerson) throws Exception {
		return (Customer) getSession().createQuery("From Customer where contactPerson = :contactPerson").setParameter("contactPerson", contactPerson).uniqueResult();
	}

	/**
	 * isProfileCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#isProfileCustomer(int)
	 */
	@Override
	public boolean isProfileCustomer(int idCustomer) throws Exception {
		Boolean result =
			(Boolean) getSession().createQuery("Select customer.canBeUsed From Customer as customer where customer.id = :idCustomer").setParameter("idCustomer", idCustomer).uniqueResult();
		return result == null ? false : !result;
	}

	/**
	 * customerProfileExists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#customerProfileExists()
	 */
	@Override
	public boolean customerProfileExists() throws Exception {
		return ((Long) getSession().createQuery("Select count(customer) From Customer as customer where customer.canBeUsed = false").uniqueResult()).intValue() == 1;
	}

	/**
	 * customerHasUsers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#customerHasUsers(int)
	 */
	@Override
	public boolean customerHasUsers(int idCustomer) throws Exception {
		String query = "Select count(customer) From User as user inner join user.customers as customer where customer.id = :idCustomer";
		return ((Long) getSession().createQuery(query).setParameter("idCustomer", idCustomer).uniqueResult()).intValue() != 0;
	}

	/**
	 * customerExistsByOrganisation: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#customerExistsByOrganisation(java.lang.String)
	 */
	@Override
	public boolean customerExistsByOrganisation(String organisation) throws Exception {
		String query = "Select count(customer) From Customer as customer where customer.organisation = :organisation";
		return ((Long) getSession().createQuery(query).setParameter("organisation", organisation).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAllCustomers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#getAllCustomers()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllCustomers() throws Exception {
		return (List<Customer>) getSession().createQuery("From Customer").list();
	}

	/**
	 * getAllCustomersAndProfileOfUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#getAllCustomersAndProfileOfUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllCustomersAndProfileOfUser(String username) throws Exception {
		String query = "Select customer From Customer as customer where customer.canBeUsed = false or customer in (select customer1 From User as user inner join user.customers as customer1";
		query += " where user.login = :username)  order by customer.canBeUsed asc, customer.organisation asc, customer.contactPerson asc";
		return getSession().createQuery(query).setParameter("username", username).list();
	}

	/**
	 * getAllCustomersNoProfilesOfUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#getAllCustomersNoProfilesOfUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllCustomersNoProfilesOfUser(String username) throws Exception {
		String query = "Select customer From User as user inner join user.customers as customer where user.login = :username and customer.canBeUsed = true order by customer.organisation ";
		query += "asc, customer.contactPerson asc";
		return getSession().createQuery(query).setParameter("username", username).list();
	}

	/**
	 * getAllCustomersNoProfiles: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#getAllCustomersNoProfiles()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllCustomersNoProfiles() throws Exception {
		return (List<Customer>) getSession().createQuery("From Customer WHERE canBeUsed=true").list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#save(lu.itrust.business.TS.Customer)
	 */
	@Override
	public void save(Customer customer) throws Exception {
		getSession().save(customer);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#saveOrUpdate(lu.itrust.business.TS.Customer)
	 */
	@Override
	public void saveOrUpdate(Customer customer) throws Exception {
		getSession().saveOrUpdate(customer);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#delete(lu.itrust.business.TS.Customer)
	 */
	@Override
	public void delete(Customer customer) throws Exception {
		getSession().delete(customer);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#delete(java.lang.Integer)
	 */
	@Override
	public void delete(Integer customerId) throws Exception {
		getSession().createQuery("delete from Analysis where customer.id = :customerId").setParameter("customerId", customerId).executeUpdate();
		getSession().createQuery("delete from Customer where id = :customerId").setParameter("customerId", customerId).executeUpdate();
	}
}