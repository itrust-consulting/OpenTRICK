package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Customer;

/** 
 * DAOCustomer.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAOCustomer {
	
	public Customer get(int id) throws Exception;
	
	public Customer loadByCustomerName(String fullName) throws Exception;
	
	public List<Customer> loadByOrganasition(String organisation) throws Exception;
	
	public List<Customer> loadByCountry(String city) throws Exception;
	
	public List<Customer> loadAll() throws Exception;
	
	public void save(Customer customer) throws Exception;
	
	public void saveOrUpdate(Customer customer) throws Exception;
	
	public void remove(Customer customer)throws Exception;

	public void remove(Integer customerId);

}
