package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOCustomer;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.usermanagement.User;

/**
 * ServiceCustomerImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Transactional(readOnly = true)
@Service
public class ServiceCustomerImpl implements ServiceCustomer {

	@Autowired
	private DAOCustomer daoCustomer;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#get(int)
	 */
	@Override
	public Customer get(Integer id)  {
		return daoCustomer.get(id);
	}

	/**
	 * getProfileCustomer: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getProfileCustomer()
	 */
	@Override
	public Customer getProfile()  {
		return daoCustomer.getProfile();
	}

	/**
	 * getCustomerFromContactPerson: <br>
	 * Description
	 * 
	 * @param contactPerson
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getCustomerFromContactPerson(java.lang.String)
	 */
	@Override
	public Customer getFromContactPerson(String contactPerson)  {
		return daoCustomer.getFromContactPerson(contactPerson);
	}

	/**
	 * isProfileCustomer: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#isProfileCustomer(int)
	 */
	@Override
	public boolean isProfile(Integer id)  {
		return daoCustomer.isProfile(id);
	}

	/**
	 * customerProfileExists: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#customerProfileExists()
	 */
	@Override
	public boolean profileExists()  {
		return daoCustomer.profileExists();
	}

	/**
	 * customerHasUsers: <br>
	 * Description
	 * 
	 * @param idCustomer
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#customerHasUsers(int)
	 */
	@Override
	public boolean hasUsers(Integer idCustomer)  {
		return daoCustomer.hasUsers(idCustomer);
	}

	/**
	 * customerExistsByOrganisation: <br>
	 * Description
	 * 
	 * @param organisation
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#customerExistsByOrganisation(java.lang.String)
	 */
	@Override
	public boolean existsByOrganisation(String organisation)  {
		return daoCustomer.existsByOrganisation(organisation);
	}

	/**
	 * getAllCustomers: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getAllCustomers()
	 */
	@Override
	public List<Customer> getAll()  {
		return daoCustomer.getAll();
	}

	/**
	 * getAllCustomersAndProfileOfUser: <br>
	 * Description
	 * 
	 * @param username
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getAllCustomersAndProfileOfUser(java.lang.String)
	 */
	@Override
	public List<Customer> getAllAndProfileOfUser(String username)  {
		return daoCustomer.getAllAndProfileOfUser(username);
	}

	/**
	 * getAllCustomersNoProfilesOfUser: <br>
	 * Description
	 * 
	 * @param username
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getAllCustomersNoProfilesOfUser(java.lang.String)
	 */
	@Override
	public List<Customer> getAllNotProfileOfUser(String username)  {
		return daoCustomer.getAllNotProfileOfUser(username);
	}

	/**
	 * getAllCustomersNoProfiles: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getAllCustomersNoProfiles()
	 */
	@Override
	public List<Customer> getAllNotProfiles()  {
		return daoCustomer.getAllNotProfiles();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param customer
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#save(lu.itrust.business.TS.model.general.Customer)
	 */
	@Transactional
	@Override
	public void save(Customer customer)  {
		daoCustomer.save(customer);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param customer
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#saveOrUpdate(lu.itrust.business.TS.model.general.Customer)
	 */
	@Override
	@Transactional
	public void saveOrUpdate(Customer customer)  {
		daoCustomer.saveOrUpdate(customer);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param customerId
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#delete(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void delete(Integer customerId)  {
		daoCustomer.delete(customerId);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param customer
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#delete(lu.itrust.business.TS.model.general.Customer)
	 */
	@Transactional
	@Override
	public void delete(Customer customer)  {
		daoCustomer.delete(customer);
	}

	@Override
	public Customer getOneNoProfile() {
		return daoCustomer.getOneNoProfile();
	}

	@Override
	public Customer getFromUsernameAndId(String username, int idCustomer) {
		return daoCustomer.getFromUsernameAndId(username, idCustomer);
	}

	@Override
	public boolean exists(int idCustomer) {
		return daoCustomer.exists(idCustomer);
	}

	@Override
	public boolean existsByIdAndOrganisation(int id, String organisation) {
		return daoCustomer.existsByIdAndOrganisation(id, organisation);
	}

	@Override
	public Customer findByAnalysisId(int analysisId) {
		
		return daoCustomer.findByAnalysisId(analysisId);
	}

	@Override
	public boolean hasAccess(Integer idUser, Customer customer) {
		return daoCustomer.hasAccess(idUser, customer);
	}

	@Override
	public List<User> findUserByCustomer(Customer customer) {
		return daoCustomer.findUserByCustomer(customer);
	}

	@Override
	public boolean hasAccess(String username, int customerId) {
		return daoCustomer.hasAccess(username, customerId);
	}

	@Override
	public Customer findByReportTemplateId(Long reportTemplateId) {
		return daoCustomer.findByReportTemplateId(reportTemplateId);
	}
}