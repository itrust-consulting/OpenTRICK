package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.data.general.Language;
import lu.itrust.business.TS.data.parameter.Parameter;
import lu.itrust.business.TS.data.standard.Standard;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.usermanagement.User;

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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#get(int)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getDefaultProfile()
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getFromIdentifierVersion(java.lang.String,
	 *      java.lang.String)
	 */

	@Override
	public Analysis getFromIdentifierVersionCustomer(String identifier, String version, Integer customerID) throws Exception {
		return daoAnalysis.getFromIdentifierVersionCustomer(identifier, version, customerID);
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#exists(int)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#exists(java.lang.String, java.lang.String)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#isProfile(int)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#hasData(java.lang.Integer)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllAnalysisIDs()
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAll()
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllNotEmpty()
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllProfiles()
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromUserNameAndCustomerId(java.lang.String,
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getFromUserNameAndCustomerIdAndNotEmpty(java.lang.String,
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getFromUserNameAndCustomer(java.lang.String,
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromCustomerIdAndProfile(int)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromCustomerIdAndProfileByPageAndSize(java.lang.Integer,
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromUser(lu.itrust.business.TS.usermanagement.User)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromCustomer(lu.itrust.business.TS.data.general.Customer)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromCustomerIdentifierVersion(lu.itrust.business.TS.data.general.Customer,
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getParameterFromAnalysis(java.lang.Integer,
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getLanguageOfAnalysis(int)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getVersionOfAnalysis(int)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#save(lu.itrust.business.TS.data.analysis.Analysis)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#saveOrUpdate(lu.itrust.business.TS.data.analysis.Analysis)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#delete(lu.itrust.business.TS.data.analysis.Analysis)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#delete(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void delete(Integer analysisId) throws Exception {
		daoAnalysis.delete(analysisId);
	}

	@Override
	public List<Analysis> getAllFromCustomer(Integer id) {
		return daoAnalysis.getAllFromCustomer(id);
	}

	@Override
	public List<Analysis> getAllProfileContainsStandard(List<Standard> standards) {
		return daoAnalysis.getAllProfileContainsStandard(standards);
	}

	@Override
	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String username) {
		return daoAnalysis.getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(id, username);
	}

	@Override
	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String username, String identifier) {
		return daoAnalysis.getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(id, username, identifier);
	}

	@Override
	public int getDefaultProfileId() {
		return daoAnalysis.getDefaultProfileId();
	}

	@Override
	public String getLabelFromId(int idAnalysis) {
		return daoAnalysis.getLabelFromId(idAnalysis);
	}

	@Override
	public String getCustomerNameFromId(int idAnalysis) {
		return daoAnalysis.getCustomerNameFromId(idAnalysis);
	}

	@Override
	public boolean isAnalysisUncertainty(Integer analysisID) throws Exception {
		return daoAnalysis.isAnalysisUncertainty(analysisID);
	}

	@Override
	public boolean isAnalysisCssf(Integer analysisID) throws Exception {
		return daoAnalysis.isAnalysisCssf(analysisID);
	}

	@Override
	public List<String> getAllNotEmptyVersion(int analysisId) {
		return daoAnalysis.getAllNotEmptyVersion(analysisId);
	}

	@Override
	public String getIdentifierByIdAnalysis(int analysisId) {
		return daoAnalysis.getIdentifierByIdAnalysis(analysisId);
	}

	@Override
	public List<String> getAllNotEmptyVersion(String identfier) {
		return daoAnalysis.getAllNotEmptyVersion(identfier);
	}

	@Override
	public Integer getCustomerIdByIdAnalysis(int analysisId) {
		return daoAnalysis.getCustomerIdByIdAnalysis(analysisId);
	}

	@Override
	public boolean isAnalysisOwner(Integer analysisId, String userName) {
		return daoAnalysis.isAnalysisOwner(analysisId, userName);
	}

	@Override
	public boolean exists(String identifier) {
		return daoAnalysis.exists(identifier);
	}

	@Override
	public Long countByIdentifier(String identifier) {
		return daoAnalysis.countByIdentifier(identifier);
	}

	@Override
	public List<Analysis> getAllByIdentifier(String identifier) {
		return daoAnalysis.getAllByIdentifier(identifier);
	}

	@Override
	public List<String> getAllVersion(String identifier) {
		return daoAnalysis.getAllVersion(identifier);
	}

	@Override
	public List<Customer> getCustomersByIdAnalysis(String identifier) {
		return daoAnalysis.getCustomersByIdAnalysis(identifier);
	}

	@Override
	public Integer getIdFromIdentifierAndVersion(String identifier, String string) {
		return daoAnalysis.getIdFromIdentifierAndVersion(identifier, string);
	}

	@Override
	public List<Customer> getCustomersByIdAnalysis(int analysisId) {
		return daoAnalysis.getCustomersByIdAnalysis(analysisId);
	}

	@Override
	public boolean isAnalysisCustomer(int idAnalysis, int idCustomer) {
		return daoAnalysis.isAnalysisCustomer(idAnalysis,idCustomer);
	}
}