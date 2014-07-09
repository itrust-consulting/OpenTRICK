package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Customer;

/**
 * DAOCustomer.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOCustomer {
	public Customer get(Integer id) throws Exception;

	public Customer getProfile() throws Exception;

	public Customer getFromContactPerson(String contactPerson) throws Exception;

	public boolean isProfile(Integer id) throws Exception;

	public boolean profileExists() throws Exception;

	public boolean hasUsers(Integer idCustomer) throws Exception;

	public boolean existsByOrganisation(String organisation) throws Exception;

	public List<Customer> getAll() throws Exception;

	public List<Customer> getAllAndProfileOfUser(String username) throws Exception;

	public List<Customer> getAllNotProfileOfUser(String username) throws Exception;

	public List<Customer> getAllNotProfiles() throws Exception;

	public void save(Customer customer) throws Exception;

	public void saveOrUpdate(Customer customer) throws Exception;

	public void delete(Customer customer) throws Exception;

	public void delete(Integer customerId) throws Exception;
}