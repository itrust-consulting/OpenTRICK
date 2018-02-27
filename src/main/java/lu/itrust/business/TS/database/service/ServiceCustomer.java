package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.usermanagement.User;

/**
 * ServiceCustomer.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceCustomer {
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

	public boolean existsByIdAndOrganisation(int id, String organisation);

	public Customer findByAnalysisId(int analysisId);

	public boolean hasAccess(Integer idUser, Customer customer);

	public List<User> findUserByCustomer(Customer customer);

	public boolean hasAccess(String username, int customerId);

	public Customer findByReportTemplateId(Long reportTemplateId);
}