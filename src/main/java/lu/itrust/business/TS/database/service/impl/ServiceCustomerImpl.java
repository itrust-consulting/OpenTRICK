package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import lu.itrust.business.TS.database.dao.DAOCustomer;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.model.general.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceCustomerImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#get(int)
	 */
	@Override
	public Customer get(Integer id) throws Exception {
		return daoCustomer.get(id);
	}

	/**
	 * getProfileCustomer: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getProfileCustomer()
	 */
	@Override
	public Customer getProfile() throws Exception {
		return daoCustomer.getProfile();
	}

	/**
	 * getCustomerFromContactPerson: <br>
	 * Description
	 * 
	 * @param contactPerson
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getCustomerFromContactPerson(java.lang.String)
	 */
	@Override
	public Customer getFromContactPerson(String contactPerson) throws Exception {
		return daoCustomer.getFromContactPerson(contactPerson);
	}

	/**
	 * isProfileCustomer: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#isProfileCustomer(int)
	 */
	@Override
	public boolean isProfile(Integer id) throws Exception {
		return daoCustomer.isProfile(id);
	}

	/**
	 * customerProfileExists: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#customerProfileExists()
	 */
	@Override
	public boolean profileExists() throws Exception {
		return daoCustomer.profileExists();
	}

	/**
	 * customerHasUsers: <br>
	 * Description
	 * 
	 * @param idCustomer
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#customerHasUsers(int)
	 */
	@Override
	public boolean hasUsers(Integer idCustomer) throws Exception {
		return daoCustomer.hasUsers(idCustomer);
	}

	/**
	 * customerExistsByOrganisation: <br>
	 * Description
	 * 
	 * @param organisation
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#customerExistsByOrganisation(java.lang.String)
	 */
	@Override
	public boolean existsByOrganisation(String organisation) throws Exception {
		return daoCustomer.existsByOrganisation(organisation);
	}

	/**
	 * getAllCustomers: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getAllCustomers()
	 */
	@Override
	public List<Customer> getAll() throws Exception {
		return daoCustomer.getAll();
	}

	/**
	 * getAllCustomersAndProfileOfUser: <br>
	 * Description
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getAllCustomersAndProfileOfUser(java.lang.String)
	 */
	@Override
	public List<Customer> getAllAndProfileOfUser(String username) throws Exception {
		return daoCustomer.getAllAndProfileOfUser(username);
	}

	/**
	 * getAllCustomersNoProfilesOfUser: <br>
	 * Description
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getAllCustomersNoProfilesOfUser(java.lang.String)
	 */
	@Override
	public List<Customer> getAllNotProfileOfUser(String username) throws Exception {
		return daoCustomer.getAllNotProfileOfUser(username);
	}

	/**
	 * getAllCustomersNoProfiles: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#getAllCustomersNoProfiles()
	 */
	@Override
	public List<Customer> getAllNotProfiles() throws Exception {
		return daoCustomer.getAllNotProfiles();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param customer
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#save(lu.itrust.business.TS.model.general.Customer)
	 */
	@Transactional
	@Override
	public void save(Customer customer) throws Exception {
		daoCustomer.save(customer);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param customer
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#saveOrUpdate(lu.itrust.business.TS.model.general.Customer)
	 */
	@Override
	@Transactional
	public void saveOrUpdate(Customer customer) throws Exception {
		daoCustomer.saveOrUpdate(customer);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param customerId
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#delete(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void delete(Integer customerId) throws Exception {
		daoCustomer.delete(customerId);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param customer
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceCustomer#delete(lu.itrust.business.TS.model.general.Customer)
	 */
	@Transactional
	@Override
	public void delete(Customer customer) throws Exception {
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
}