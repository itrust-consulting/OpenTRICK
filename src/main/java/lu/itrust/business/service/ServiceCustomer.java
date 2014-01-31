/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.dao.DAOCustomer;

/**
 * @author oensuifudine
 * 
 */
public interface ServiceCustomer {

	public Customer get(int id) throws Exception;

	public Customer loadByCustomerName(String fullName) throws Exception;

	public List<Customer> loadByOrganasition(String organisation) throws Exception;

	public Customer loadProfileCustomer();

	public boolean hasProfileCustomer();
	
	public boolean hasUser(int idCustomer);

	public List<Customer> loadByUser(String username);
	
	public List<Customer> loadByUserAndProfile(String username);

	public List<Customer> loadByCountry(String city) throws Exception;

	public List<Customer> loadAll() throws Exception;

	public void save(Customer customer) throws Exception;

	public void saveOrUpdate(Customer customer) throws Exception;

	public void remove(Customer customer) throws Exception;

	public void remove(Integer customerId) throws Exception;

	public DAOCustomer getDaoCustomer();

	

	

}
