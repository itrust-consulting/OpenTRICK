package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Customer;

/**
 * DAOCustomer.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOCustomer {

	public Customer get(int id) throws Exception;

	public Customer getProfileCustomer() throws Exception;

	public Customer getCustomerFromContactPerson(String contactPerson) throws Exception;

	public boolean isProfileCustomer(int id) throws Exception;

	public boolean customerProfileExists() throws Exception;

	public boolean customerHasUsers(int idCustomer) throws Exception;

	public boolean customerExistsByOrganisation(String organisation) throws Exception;

	public List<Customer> getAllCustomers() throws Exception;

	public List<Customer> getAllCustomersAndProfileOfUser(String username) throws Exception;

	public List<Customer> getAllCustomersNoProfilesOfUser(String username) throws Exception;

	public List<Customer> getAllCustomersNoProfiles() throws Exception;

	public void save(Customer customer) throws Exception;

	public void saveOrUpdate(Customer customer) throws Exception;

	public void delete(Customer customer) throws Exception;

	public void delete(Integer customerId) throws Exception;
}