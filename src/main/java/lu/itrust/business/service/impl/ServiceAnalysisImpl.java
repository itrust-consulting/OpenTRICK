/**
 * 
 */
package lu.itrust.business.service.impl;

import java.sql.Timestamp;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.service.ServiceAnalysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author oensuifudine
 * 
 */
@Service
public class ServiceAnalysisImpl implements ServiceAnalysis {

	@Autowired
	private DAOAnalysis daoAnalysis;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#get(int)
	 */
	@Override
	public Analysis get(int id) throws Exception {
		return daoAnalysis.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#get(int, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Analysis get(int id, String identifier, String version, String creationDate) throws Exception {
		return daoAnalysis.get(id, identifier, version, creationDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#get(int, java.lang.String, java.lang.String,
	 * java.sql.Timestamp)
	 */
	@Override
	public Analysis get(int id, String identifier, String version, Timestamp creationDate) throws Exception {
		return daoAnalysis.get(id, identifier, version, creationDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#analysisExist(java.lang.String ,
	 * java.lang.String)
	 */
	@Override
	public boolean analysisExist(String identifier, String version) throws Exception {
		return daoAnalysis.analysisExist(identifier, version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getFromIdentifierVersion(java .lang.String,
	 * java.lang.String)
	 */
	@Override
	public Analysis getFromIdentifierVersion(String identifier, String version) throws Exception {
		return daoAnalysis.getFromIdentifierVersion(identifier, version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#
	 * loadAllFromCustomerIdentifierVersion(lu.itrust.business.TS.Customer, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<Analysis> loadAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) throws Exception {
		return daoAnalysis.loadAllFromCustomerIdentifierVersion(customer, identifier, version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#loadAllFromCustomer(lu.itrust
	 * .business.TS.Customer)
	 */
	@Override
	public List<Analysis> loadAllFromCustomer(Customer customer) throws Exception {
		return daoAnalysis.loadAllFromCustomer(customer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#loadAll()
	 */
	@Override
	public List<Analysis> loadAll() throws Exception {
		return daoAnalysis.loadAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#save(lu.itrust.business.TS .Analysis)
	 */
	@Transactional
	@Override
	public void save(Analysis analysis) throws Exception {
		daoAnalysis.save(analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#saveOrUpdate(lu.itrust.business .TS.Analysis)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Analysis analysis) throws Exception {
		daoAnalysis.saveOrUpdate(analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#remove(lu.itrust.business. TS.Analysis)
	 */
	@Transactional
	@Override
	public void remove(Analysis analysis) throws Exception {
		daoAnalysis.remove(analysis);
	}

	@Transactional
	@Override
	public void remove(Integer analysisId) throws Exception {
		daoAnalysis.remove(analysisId);
	}

	@Override
	public List<Analysis> loadAllNotEmpty() throws Exception {
		return daoAnalysis.loadAllNotEmpty();
	}

	@Override
	public boolean exist(int id) {

		return daoAnalysis.exist(id);
	}

	@Override
	public List<Analysis> loadAllFromUser(User user) throws Exception {
		return daoAnalysis.loadAllFromUser(user);
	}

	@Override
	public Language getLanguageFromAnalysis(int analysisID) throws Exception {
		return daoAnalysis.getLanguageOfAnalysis(analysisID);
	}

	@Override
	public List<Analysis> loadByUserAndCustomer(String login, String customer) {
		return daoAnalysis.loadByUserAndCustomer(login, customer);
	}

	@Override
	public List<Analysis> loadByUserAndCustomer(String login, String customer, int pageIndex, int pageSize) {
		return daoAnalysis.loadByUserAndCustomer(login, customer,pageIndex, pageSize );
	}

	@Override
	public boolean isProfile(String name) {
		return daoAnalysis.isProfile(name);
	}

	@Override
	public Analysis findProfileByName(String name) {
		return daoAnalysis.findProfileByName(name);
	}

	@Override
	public List<Analysis> loadProfiles() {
		return daoAnalysis.loadProfiles();
	}
}
