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
	
	public Customer loadProfileCustomer();

	public boolean hasProfileCustomer();
	
	public boolean hasUser(int idCustomer);
	
	public boolean exist(String organisation);
	
	public Customer loadByCustomerName(String fullName) throws Exception;
	
	public List<Customer> loadByUserAndProfile(String username);
	
	public List<Customer> loadByOrganasition(String organisation) throws Exception;
	
	public List<Customer> loadByCountry(String city) throws Exception;
	
	public List<Customer> loadAll() throws Exception;
	
	public List<Customer> loadAllNotProfile() throws Exception;
	
	public void save(Customer customer) throws Exception;
	
	public void saveOrUpdate(Customer customer) throws Exception;
	
	public void remove(Customer customer)throws Exception;

	public void remove(Integer customerId) throws Exception;

	public List<Customer> loadByUser(String username);

	public boolean isProfile(int id);

	

}
