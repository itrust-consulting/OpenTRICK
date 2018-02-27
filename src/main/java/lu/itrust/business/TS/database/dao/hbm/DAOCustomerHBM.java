package lu.itrust.business.TS.database.dao.hbm;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOCustomer;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.usermanagement.User;

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
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#delete(lu.itrust.business.TS.model.general.Customer)
	 */
	@Override
	public void delete(Customer customer) {
		getSession().delete(customer);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#delete(java.lang.Integer)
	 */
	@Override
	public void delete(Integer customerId) {
		getSession().createQuery("delete from Analysis where customer.id = :customerId").setParameter("customerId", customerId).executeUpdate();
		getSession().createQuery("delete from Customer where id = :customerId").setParameter("customerId", customerId).executeUpdate();
	}

	@Override
	public boolean exists(int idCustomer) {
		return (boolean) getSession().createQuery("Select count(*)>0 From Customer where id = :idCustomer").setParameter("idCustomer", idCustomer).getSingleResult();
	}

	@Override
	public boolean existsByIdAndOrganisation(int id, String organisation) {
		return (Boolean) getSession().createQuery("Select count(customer)>0 From Customer as customer where customer.id <> :id and customer.organisation = :organisation")
				.setParameter("id", id).setParameter("organisation", organisation).getSingleResult();
	}

	/**
	 * customerExistsByOrganisation: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#customerExistsByOrganisation(java.lang.String)
	 */
	@Override
	public boolean existsByOrganisation(String organisation) {
		return (boolean) getSession().createQuery("Select count(customer)>0 From Customer as customer where customer.organisation = :organisation")
				.setParameter("organisation", organisation).getSingleResult();
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#get(int)
	 */
	@Override
	public Customer get(Integer id) {
		return (Customer) getSession().get(Customer.class, id);
	}

	/**
	 * getAllCustomers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getAllCustomers()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAll() {
		return (List<Customer>) getSession().createQuery("From Customer").getResultList();
	}

	/**
	 * getAllCustomersAndProfileOfUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getAllCustomersAndProfileOfUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllAndProfileOfUser(String username) {
		String query = "Select customer From Customer as customer where customer.canBeUsed = false or customer in (select customer1 From User as user inner join user.customers as customer1";
		query += " where user.login = :username)  order by customer.canBeUsed asc, customer.organisation asc, customer.contactPerson asc";
		return getSession().createQuery(query).setParameter("username", username).getResultList();
	}

	/**
	 * getAllCustomersNoProfilesOfUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getAllCustomersNoProfilesOfUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllNotProfileOfUser(String username) {
		String query = "Select customer From User as user inner join user.customers as customer where user.login = :username and customer.canBeUsed = true order by customer.organisation ";
		query += "asc, customer.contactPerson asc";
		return getSession().createQuery(query).setParameter("username", username).getResultList();
	}

	/**
	 * getAllCustomersNoProfiles: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getAllCustomersNoProfiles()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getAllNotProfiles() {
		return (List<Customer>) getSession().createQuery("From Customer WHERE canBeUsed=true").getResultList();
	}

	/**
	 * getCustomerFromContactPerson: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getCustomerFromContactPerson(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Customer getFromContactPerson(String contactPerson) {
		return (Customer) getSession().createQuery("From Customer where contactPerson = :contactPerson").setParameter("contactPerson", contactPerson).uniqueResultOptional()
				.orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Customer getFromUsernameAndId(String username, int idCustomer) {
		String query = "Select customer From User as user inner join user.customers as customer where user.login = :username and customer.id = :idCustomer";
		return (Customer) getSession().createQuery(query).setParameter("username", username).setParameter("idCustomer", idCustomer).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Customer getOneNoProfile() {
		return (Customer) getSession().createQuery("From Customer where canBeUsed = true").setMaxResults(1).uniqueResultOptional().orElse(null);
	}

	/**
	 * getProfileCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#getProfileCustomer()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Customer getProfile() {
		return (Customer) getSession().createQuery("From Customer where canBeUsed = false").uniqueResultOptional().orElse(null);
	}

	/**
	 * customerHasUsers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#customerHasUsers(int)
	 */
	@Override
	public boolean hasUsers(Integer idCustomer) {
		String query = "Select count(customer) > 0 From User as user inner join user.customers as customer where customer.id = :idCustomer";
		return (boolean) getSession().createQuery(query).setParameter("idCustomer", idCustomer).getSingleResult();
	}

	@Override
	public boolean isInUsed(Customer customer) {
		return (boolean) getSession().createQuery("Select count(*)>0 From Analysis where customer = :customer").setParameter("customer", customer).getSingleResult();
	}

	/**
	 * isProfileCustomer: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#isProfileCustomer(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isProfile(Integer idCustomer) {
		return !(boolean) getSession().createQuery("Select customer.canBeUsed From Customer as customer where customer.id = :idCustomer").setParameter("idCustomer", idCustomer)
				.uniqueResultOptional().orElse(true);

	}

	/**
	 * customerProfileExists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#customerProfileExists()
	 */
	@Override
	public boolean profileExists() {
		return (boolean) getSession().createQuery("Select count(customer)>0 From Customer as customer where customer.canBeUsed = false").getSingleResult();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#save(lu.itrust.business.TS.model.general.Customer)
	 */
	@Override
	public void save(Customer customer) {
		getSession().save(customer);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOCustomer#saveOrUpdate(lu.itrust.business.TS.model.general.Customer)
	 */
	@Override
	public void saveOrUpdate(Customer customer) {
		getSession().saveOrUpdate(customer);
	}

	@Override
	public boolean hasAccess(Integer idUser, Customer customer) {
		return customer.isCanBeUsed()
				? getSession()
						.createQuery("Select count(customer)> 0 From User user inner join user.customers customer where user.id = :id and customer = :customer", Boolean.class)
						.setParameter("id", idUser).setParameter("customer", customer).uniqueResult()
				: false;
	}

	@Override
	public Customer findByAnalysisId(int analysisId) {
		return getSession().createQuery("Select analysis.customer From Analysis analysis where analysis.id = :id", Customer.class).setParameter("id", analysisId).uniqueResult();
	}

	@Override
	public List<User> findUserByCustomer(Customer customer) {
		return customer.isCanBeUsed() ? getSession()
				.createQuery("Select distinct user From User user inner join user.customers customer where customer = :customer order by user.firstName, user.lastName, user.email",
						User.class)
				.setParameter("customer", customer).list() : Collections.emptyList();
	}

	@Override
	public boolean hasAccess(String username, int customerId) {
		return getSession().createQuery(
				"Select count(customer)> 0 From User user inner join user.customers customer where user.login = :username and customer.id = :customerId and customer.canBeUsed = true",
				Boolean.class).setParameter("username", username).setParameter("customerId", customerId).uniqueResult();
	}

	@Override
	public Customer findByReportTemplateId(Long reportTemplateId) {
		return getSession().createQuery("Select customer From Customer customer inner join customer.templates as template where template.id = :reportTemplateId", Customer.class)
				.setParameter("reportTemplateId", reportTemplateId).uniqueResult();
	}
}