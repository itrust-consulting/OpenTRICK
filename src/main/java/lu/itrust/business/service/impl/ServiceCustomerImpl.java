/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.dao.DAOCustomer;
import lu.itrust.business.service.ServiceCustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author oensuifudine
 * 
 */
@Service
public class ServiceCustomerImpl implements ServiceCustomer {

	@Autowired
	private DAOCustomer daoCustomer;

	public void setDaoCustomer(DAOCustomer daoCustomer) {
		this.daoCustomer = daoCustomer;
	}
	
	

	/**
	 * @return the daoCustomer
	 */
	public DAOCustomer getDaoCustomer() {
		return daoCustomer;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceCustomer#get(int)
	 */
	@Override
	public Customer get(int id) throws Exception {
		// TODO Auto-generated method stub
		return daoCustomer.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceCustomer#loadByCustomerName(java.lang
	 * .String)
	 */
	@Override
	public Customer loadByCustomerName(String fullName) throws Exception {
		// TODO Auto-generated method stub
		return daoCustomer.loadByCustomerName(fullName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceCustomer#loadByOrganasition(java.lang
	 * .String)
	 */
	@Override
	public List<Customer> loadByOrganasition(String organisation)
			throws Exception {
		// TODO Auto-generated method stub
		return daoCustomer.loadByOrganasition(organisation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceCustomer#loadByCountry(java.lang.String
	 * )
	 */
	@Override
	public List<Customer> loadByCountry(String city) throws Exception {
		// TODO Auto-generated method stub
		return daoCustomer.loadByCountry(city);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceCustomer#loadAll()
	 */
	@Override
	public List<Customer> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return daoCustomer.loadAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceCustomer#save(lu.itrust.business.TS
	 * .Customer)
	 */
	@Transactional
	@Override
	public void save(Customer customer) throws Exception {
		daoCustomer.save(customer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceCustomer#saveOrUpdate(lu.itrust.business
	 * .TS.Customer)
	 */
	@Override
	@Transactional
	public void saveOrUpdate(Customer customer) throws Exception {
		daoCustomer.saveOrUpdate(customer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceCustomer#remove(lu.itrust.business.
	 * TS.Customer)
	 */
	@Transactional
	@Override
	public void remove(Customer customer) throws Exception {
		daoCustomer.remove(customer);
	}

	@Transactional
	@Override
	public void remove(Integer customerId)throws Exception {
		daoCustomer.remove(customerId);
		
	}



	@Override
	public List<Customer> loadByUser(String username) {
		return daoCustomer.loadByUser(username);
	}

}
