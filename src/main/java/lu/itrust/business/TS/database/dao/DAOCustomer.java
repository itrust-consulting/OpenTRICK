package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.usermanagement.User;

/**
 * DAOCustomer.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOCustomer {
	public Customer get(Integer id);

	public Customer getProfile();

	public Customer getFromContactPerson(String contactPerson);

	public boolean isProfile(Integer id);

	public boolean profileExists();

	public boolean hasUsers(Integer idCustomer);

	public boolean existsByOrganisation(String organisation);

	public List<Customer> getAll();

	public List<Customer> getAllAndProfileOfUser(String username);

	public List<Customer> getAllNotProfileOfUser(String username);

	public List<Customer> getAllNotProfiles();

	public void save(Customer customer);

	public void saveOrUpdate(Customer customer);

	public void delete(Customer customer);

	public void delete(Integer customerId);

	public Customer getOneNoProfile();

	public Customer getFromUsernameAndId(String username, int idCustomer);

	public boolean exists(int idCustomer);

	public boolean isInUsed(Customer customer);

	public boolean existsByIdAndOrganisation(int id, String organisation);

	public boolean hasAccess(Integer idUser, Customer customer);

	public Customer findByAnalysisId(int analysisId);

	public List<User> findUserByCustomer(Customer customer);

}