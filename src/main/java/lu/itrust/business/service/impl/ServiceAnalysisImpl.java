package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.helper.AnalysisBaseInfo;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.service.ServiceAnalysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceAnalysisImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
public class ServiceAnalysisImpl implements ServiceAnalysis {

	@Autowired
	private DAOAnalysis daoAnalysis;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#get(int)
	 */
	@Override
	public Analysis get(Integer id) throws Exception {
		return daoAnalysis.get(id);
	}

	/**
	 * getDefaultProfile: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getDefaultProfile()
	 */
	@Override
	public Analysis getDefaultProfile() throws Exception {
		return daoAnalysis.getDefaultProfile();
	}

	/**
	 * getFromIdentifierVersion: <br>
	 * Description
	 * 
	 * @param identifier
	 * @param version
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getFromIdentifierVersion(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Analysis getFromIdentifierVersion(String identifier, String version) throws Exception {
		return daoAnalysis.getFromIdentifierVersion(identifier, version);
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#exists(int)
	 */
	@Override
	public boolean exists(Integer idAnalysis) throws Exception {
		return daoAnalysis.exists(idAnalysis);
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @param identifier
	 * @param version
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#exists(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean exists(String identifier, String version) throws Exception {
		return daoAnalysis.exists(identifier, version);
	}

	/**
	 * isProfile: <br>
	 * Description
	 * 
	 * @param analysisid
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#isProfile(int)
	 */
	@Override
	public boolean isProfile(Integer analysisid) throws Exception {
		return daoAnalysis.isProfile(analysisid);
	}

	/**
	 * hasData: <br>
	 * Description
	 * 
	 * @{tags
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#hasData(java.lang.Integer)
	 */
	@Override
	public boolean hasData(Integer idAnalysis) throws Exception {
		return daoAnalysis.hasData(idAnalysis);
	}

	/**
	 * getAllAnalysisIDs: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getAllAnalysisIDs()
	 */
	@Override
	public List<Integer> getAllAnalysisIDs() throws Exception {
		return daoAnalysis.getAllAnalysisIDs();
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getAll()
	 */
	@Override
	public List<Analysis> getAll() throws Exception {
		return daoAnalysis.getAll();
	}

	/**
	 * getAllNotEmpty: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getAllNotEmpty()
	 */
	@Override
	public List<Analysis> getAllNotEmpty() throws Exception {
		return daoAnalysis.getAllNotEmpty();
	}

	/**
	 * getAllProfiles: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getAllProfiles()
	 */
	@Override
	public List<Analysis> getAllProfiles() throws Exception {
		return daoAnalysis.getAllProfiles();
	}

	/**
	 * getAllFromUserNameAndCustomerId: <br>
	 * Description
	 * 
	 * @param userName
	 * @param customerID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getAllFromUserNameAndCustomerId(java.lang.String,
	 *      java.lang.Integer)
	 */
	@Override
	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID) throws Exception {
		return daoAnalysis.getAllFromUserAndCustomer(userName, customerID);
	}

	/**
	 * getFromUserNameAndCustomerIdAndNotEmpty: <br>
	 * Description
	 * 
	 * @param userName
	 * @param idCustomer
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getFromUserNameAndCustomerIdAndNotEmpty(java.lang.String,
	 *      int)
	 */
	@Override
	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer) throws Exception {
		return daoAnalysis.getAllNotEmptyFromUserAndCustomer(userName, idCustomer);
	}

	/**
	 * getFromUserNameAndCustomer: <br>
	 * Description
	 * 
	 * @param login
	 * @param customer
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getFromUserNameAndCustomer(java.lang.String,
	 *      java.lang.Integer, int, int)
	 */
	@Override
	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex, Integer pageSize) throws Exception {
		return daoAnalysis.getAllFromUserAndCustomerByPageAndSizeIndex(login, customer, pageIndex, pageSize);
	}

	/**
	 * getAllFromCustomerIdAndProfile: <br>
	 * Description
	 * 
	 * @param idCustomer
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getAllFromCustomerIdAndProfile(int)
	 */
	@Override
	public List<Analysis> getAllFromCustomerAndProfile(Integer idCustomer) throws Exception {
		return daoAnalysis.getAllFromCustomerAndProfile(idCustomer);
	}

	/**
	 * getAllFromCustomerIdAndProfileByPageAndSize: <br>
	 * Description
	 * 
	 * @param customerID
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getAllFromCustomerIdAndProfileByPageAndSize(java.lang.Integer,
	 *      java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Analysis> getAllFromCustomerAndProfileByPageAndSizeIndex(Integer customerID, Integer pageIndex, Integer pageSize) throws Exception {
		return daoAnalysis.getAllFromCustomerAndProfileByPageAndSizeIndex(customerID, pageIndex, pageSize);
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getAllFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public List<Analysis> getAllFromUser(User user) throws Exception {
		return daoAnalysis.getAllFromUser(user);
	}

	/**
	 * getAllFromCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getAllFromCustomer(lu.itrust.business.TS.Customer)
	 */
	@Override
	public List<Analysis> getAllFromCustomer(Customer customer) throws Exception {
		return daoAnalysis.getAllFromCustomer(customer);
	}

	/**
	 * getAllFromCustomerIdentifierVersion: <br>
	 * Description
	 * 
	 * @param customer
	 * @param identifier
	 * @param version
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getAllFromCustomerIdentifierVersion(lu.itrust.business.TS.Customer,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public List<Analysis> getAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) throws Exception {
		return daoAnalysis.getAllFromCustomerIdentifierVersion(customer, identifier, version);
	}

	/**
	 * getParameterFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param Parameter
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getParameterFromAnalysis(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	public Parameter getParameterFromAnalysis(Integer idAnalysis, String Parameter) throws Exception {
		return daoAnalysis.getParameterFromAnalysis(idAnalysis, Parameter);
	}

	/**
	 * getLanguageOfAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getLanguageOfAnalysis(int)
	 */
	@Override
	public Language getLanguageOfAnalysis(Integer analysisID) throws Exception {
		return daoAnalysis.getLanguageOfAnalysis(analysisID);
	}

	/**
	 * getVersionOfAnalysis: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#getVersionOfAnalysis(int)
	 */
	@Override
	public String getVersionOfAnalysis(Integer id) throws Exception {
		return daoAnalysis.getVersionOfAnalysis(id);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#save(lu.itrust.business.TS.Analysis)
	 */
	@Transactional
	@Override
	public void save(Analysis analysis) throws Exception {
		daoAnalysis.save(analysis);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#saveOrUpdate(lu.itrust.business.TS.Analysis)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Analysis analysis) throws Exception {
		daoAnalysis.saveOrUpdate(analysis);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#delete(lu.itrust.business.TS.Analysis)
	 */
	@Transactional
	@Override
	public void delete(Analysis analysis) throws Exception {
		daoAnalysis.delete(analysis);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysis#delete(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void delete(Integer analysisId) throws Exception {
		daoAnalysis.delete(analysisId);
	}

	@Override
	public List<Analysis> getAllFromCustomer(Integer id) {
		// TODO Auto-generated method stub
		return daoAnalysis.getAllFromCustomer(id);
	}

	@Override
	public List<Analysis> getAllProfileContainsNorm(List<Norm> norms) {
		return daoAnalysis.getAllProfileContainsNorm(norms);
	}

	@Override
	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String username) {
		return daoAnalysis.getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(id,username);
	}

	@Override
	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String username, String identifier) {
		return daoAnalysis.getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(id,username,identifier );
	}

	@Override
	public int getDefaultProfileId() {
		return daoAnalysis.getDefaultProfileId();
	}

	@Override
	public String getLabelFromId(int idAnalysis) {
		return daoAnalysis.getLabelFromId(idAnalysis);
	}


}
