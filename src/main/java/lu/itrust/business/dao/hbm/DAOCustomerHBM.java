package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.dao.DAOCustomer;

import org.hibernate.Query;
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
	 * 
	 */
	public DAOCustomerHBM() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sessionFactory
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
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#get(java.lang.String)
	 */
	@Override
	public Customer loadByCustomerName(String fullName) throws Exception {
		Query query = getSession().createQuery("From Customer where contactPerson = :contactPerson");
		query.setString("contactPerson", fullName);
		return (Customer) query.uniqueResult();

	}

	/**
	 * loadByOrganasition: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#loadByOrganasition(java.lang.String)
	 */
	@Override
	public List<Customer> loadByOrganasition(String organisation) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadByCountry: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#loadByCountry(java.lang.String)
	 */
	@Override
	public List<Customer> loadByCountry(String city) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> loadAll() throws Exception {

		Query query = getSession().createQuery("From Customer");

		return (List<Customer>) query.list();
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
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOCustomer#remove(lu.itrust.business.TS.Customer)
	 */
	@Override
	public void remove(Customer customer) throws Exception {
		Query query = getSession().createQuery("delete from Analysis where Customer = :customer");
		query.setParameter("customer", customer);
		query.executeUpdate();
		getSession().delete(customer);
	}

	@Override
	public void remove(Integer customerId) throws Exception {
		Query query = getSession().createQuery("delete from Analysis where customer.id = :customerId");
		query.setParameter("customerId", customerId);
		query.executeUpdate();
		query = getSession().createQuery("delete from Customer where id = :customerId");
		query.setParameter("customerId", customerId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> loadByUser(String username) {
		return getSession()
				.createQuery(
				"Select customer From User as user inner join user.customers as customer where user.login = :username order by customer.organisation asc, customer.contactPerson asc")
				.setParameter("username", username).list();
	}

}
