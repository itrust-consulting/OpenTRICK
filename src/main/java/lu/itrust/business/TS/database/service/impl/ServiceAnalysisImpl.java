package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.usermanagement.User;

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
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#get(int)
	 */

	@Override
	public Analysis get(Integer id)  {
		return daoAnalysis.get(id);
	}

	/**
	 * getDefaultProfile: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getDefaultProfile()
	 */

	@Override
	public Analysis getDefaultProfile(AnalysisType analysisType)  {
		return daoAnalysis.getDefaultProfile(analysisType);
	}

	/**
	 * getFromIdentifierVersion: <br>
	 * Description
	 * 
	 * @param identifier
	 * @param version
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getFromIdentifierVersion(java.lang.String,
	 *      java.lang.String)
	 */

	@Override
	public Analysis getFromIdentifierVersionCustomer(String identifier, String version, Integer customerID)  {
		return daoAnalysis.getFromIdentifierVersionCustomer(identifier, version, customerID);
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#exists(int)
	 */

	@Override
	public boolean exists(Integer idAnalysis)  {
		return daoAnalysis.exists(idAnalysis);
	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @param identifier
	 * @param version
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#exists(java.lang.String,
	 *      java.lang.String)
	 */

	@Override
	public boolean exists(String identifier, String version)  {
		return daoAnalysis.exists(identifier, version);
	}

	/**
	 * isProfile: <br>
	 * Description
	 * 
	 * @param analysisid
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#isProfile(int)
	 */

	@Override
	public boolean isProfile(Integer analysisid)  {
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
	public boolean hasData(Integer idAnalysis)  {
		return daoAnalysis.hasData(idAnalysis);
	}

	/**
	 * getAllAnalysisIDs: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllAnalysisIDs()
	 */

	@Override
	public List<Integer> getAllAnalysisIDs()  {
		return daoAnalysis.getAllAnalysisIDs();
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAll()
	 */

	@Override
	public List<Analysis> getAll()  {
		return daoAnalysis.getAll();
	}

	/**
	 * getAllNotEmpty: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllNotEmpty()
	 */

	@Override
	public List<Analysis> getAllNotEmpty()  {
		return daoAnalysis.getAllNotEmpty();
	}

	/**
	 * getAllProfiles: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllProfiles()
	 */

	@Override
	public List<Analysis> getAllProfiles()  {
		return daoAnalysis.getAllProfiles();
	}

	/**
	 * getAllFromUserNameAndCustomerId: <br>
	 * Description
	 * 
	 * @param userName
	 * @param customerID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromUserNameAndCustomerId(java.lang.String,
	 *      java.lang.Integer)
	 */

	@Override
	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID)  {
		return daoAnalysis.getAllFromUserAndCustomer(userName, customerID);
	}

	/**
	 * getFromUserNameAndCustomerIdAndNotEmpty: <br>
	 * Description
	 * 
	 * @param userName
	 * @param idCustomer
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getFromUserNameAndCustomerIdAndNotEmpty(java.lang.String,
	 *      int)
	 */

	@Override
	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer)  {
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
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getFromUserNameAndCustomer(java.lang.String,
	 *      java.lang.Integer, int, int)
	 */

	@Override
	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex, Integer pageSize)  {
		return daoAnalysis.getAllFromUserAndCustomerByPageAndSizeIndex(login, customer, pageIndex, pageSize);
	}

	/**
	 * getAllFromCustomerIdAndProfile: <br>
	 * Description
	 * 
	 * @param idCustomer
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromCustomerIdAndProfile(int)
	 */

	@Override
	public List<Analysis> getAllFromCustomerAndProfile(Integer idCustomer)  {
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
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromCustomerIdAndProfileByPageAndSize(java.lang.Integer,
	 *      java.lang.Integer, java.lang.Integer)
	 */

	@Override
	public List<Analysis> getAllFromCustomerAndProfileByPageAndSizeIndex(Integer customerID, Integer pageIndex, Integer pageSize)  {
		return daoAnalysis.getAllFromCustomerAndProfileByPageAndSizeIndex(customerID, pageIndex, pageSize);
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromUser(lu.itrust.business.TS.usermanagement.User)
	 */

	@Override
	public List<Analysis> getAllFromUser(User user)  {
		return daoAnalysis.getAllFromUser(user);
	}

	/**
	 * getAllFromCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromCustomer(lu.itrust.business.TS.model.general.Customer)
	 */

	@Override
	public List<Analysis> getAllFromCustomer(Customer customer)  {
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
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getAllFromCustomerIdentifierVersion(lu.itrust.business.TS.model.general.Customer,
	 *      java.lang.String, java.lang.String)
	 */

	@Override
	public List<Analysis> getAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version)  {
		return daoAnalysis.getAllFromCustomerIdentifierVersion(customer, identifier, version);
	}

	/**
	 * getParameterFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param SimpleParameter
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getParameterFromAnalysis(java.lang.Integer,
	 *      java.lang.String)
	 */

	@Override
	public IParameter getParameterFromAnalysis(Integer idAnalysis, String Parameter)  {
		return daoAnalysis.getParameterFromAnalysis(idAnalysis, Parameter);
	}

	/**
	 * getLanguageOfAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getLanguageOfAnalysis(int)
	 */

	@Override
	public Language getLanguageOfAnalysis(Integer analysisID)  {
		return daoAnalysis.getLanguageOfAnalysis(analysisID);
	}

	/**
	 * getVersionOfAnalysis: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#getVersionOfAnalysis(int)
	 */

	@Override
	public String getVersionOfAnalysis(Integer id)  {
		return daoAnalysis.getVersionOfAnalysis(id);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param analysis
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#save(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Transactional
	@Override
	public void save(Analysis analysis)  {
		daoAnalysis.save(analysis);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param analysis
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#saveOrUpdate(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Analysis analysis)  {
		daoAnalysis.saveOrUpdate(analysis);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param analysis
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#delete(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Transactional
	@Override
	public void delete(Analysis analysis)  {
		daoAnalysis.delete(analysis);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysis#delete(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void delete(Integer analysisId)  {
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
	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String username, List<AnalysisRight> rights) {
		return daoAnalysis.getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(id, username, rights);
	}

	@Override
	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String username, String identifier, List<AnalysisRight> rights) {
		return daoAnalysis.getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(id, username, identifier, rights);
	}

	@Override
	public int getDefaultProfileId(AnalysisType analysisType) {
		return daoAnalysis.getDefaultProfileId(analysisType);
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
	public boolean isAnalysisUncertainty(Integer analysisID)  {
		return daoAnalysis.isAnalysisUncertainty(analysisID);
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
		return daoAnalysis.isAnalysisCustomer(idAnalysis, idCustomer);
	}

	@Override
	public List<String> getNamesByUserAndCustomerAndNotEmpty(String username, Integer idCustomer) {
		return daoAnalysis.getNamesByUserAndCustomerAndNotEmpty(username, idCustomer);
	}

	@Override
	public List<Analysis> getAllByUserAndCustomerAndNameAndNotEmpty(String username, Integer idCustomer, String name) {
		return daoAnalysis.getAllByUserAndCustomerAndNameAndNotEmpty(username, idCustomer, name);
	}

	@Override
	public boolean isProfileNameInUsed(String name) {
		return daoAnalysis.isProfileNameInUsed(name);
	}

	@Override
	public List<String> getAllVersion(Integer analysisId) {
		return daoAnalysis.getAllVersion(analysisId);
	}

	@Override
	public List<Analysis> getAllFromOwner(User user) {
		return daoAnalysis.getAllFromOwner(user);
	}

	@Override
	public List<Analysis> getAllHasRightsAndContainsStandard(String username, List<AnalysisRight> rights, List<Standard> standards) {
		return daoAnalysis.getAllHasRightsAndContainsStandard(username, rights, standards);
	}

	@Override
	public boolean existsByNameAndCustomerId(String name, int idCustomer) {
		return daoAnalysis.existsByNameAndCustomerId(name, idCustomer);
	}

	@Override
	public List<Analysis> getFromUserNameAndNotEmpty(String userName, List<AnalysisRight> rights) {
		return daoAnalysis.getFromUserNameAndNotEmpty(userName, rights);
	}

	@Override
	public List<Analysis> getAllNotEmpty(int pageIndex, int pageSize) {
		return daoAnalysis.getAllNotEmpty(pageIndex, pageSize);
	}

	@Override
	public int countNotEmpty() {
		return daoAnalysis.countNotEmpty();
	}

	@Override
	public Analysis getByCustomerAndLabelAndVersion(int customerId, String name, String version) {
		return daoAnalysis.getByCustomerAndNameAndVersion(customerId,name, version);
	}

	@Override
	public Analysis getByIdentifierAndVersion(String identifier, String version) {
		return daoAnalysis.getByIdentifierAndVersion(identifier, version);
	}

	@Override
	public Analysis getProfileByName(String name) {
		return daoAnalysis.getProfileByName(name);
	}

	public int countNotEmptyNoItemInformationAndRiskInformation() {
		return daoAnalysis.countNotEmptyNoItemInformationAndRiskInformation();
	}

	@Override
	public List<Analysis> getAllNotEmptyNoItemInformationAndRiskInformation(int pageIndex, int pageSize) {
		return daoAnalysis.getAllNotEmptyNoItemInformationAndRiskInformation(pageIndex, pageSize);
	}

	@Override
	public String getProjectIdById(Integer idAnalysis) {
		return daoAnalysis.getProjectIdById(idAnalysis);
	}

	@Override
	public boolean hasProject(int idAnalysis) {
		return daoAnalysis.hasProject(idAnalysis);
	}

	@Override
	public String getProjectIdByIdentifier(String identifier) {
		return daoAnalysis.getProjectIdByIdentifier(identifier);
	}

	@Override
	public List<String> getAllProjectIds() {
		return daoAnalysis.getAllProjectIds();
	}

	@Override
	public List<Analysis> getByUsernameAndIds(String username, List<Integer> ids) {
		return daoAnalysis.getByUsernameAndIds(username,ids);
	}

	@Override
	public List<Analysis> getByUsernameAndCustomerAndNoEmptyAndGroupByIdentifier(String username, Integer customerId) {
		return daoAnalysis.getByUsernameAndCustomerAndNoEmptyAndGroupByIdentifier(username,customerId);
	}

	@Override
	public Analysis getByUsernameAndId(String username, Integer analysisId) {
		return daoAnalysis.getByUsernameAndId(username, analysisId);
	}

	/**
	 * Only analysis user can export.
	 * @param name
	 * @param idCustomer
	 * @return Object[2],  [0]=identifier, [1] = name
	 */
	@Override
	public List<Object[]> getIdentifierAndNameByUserAndCustomer(String username, Integer idCustomer) {
		return daoAnalysis.getIdentifierAndNameByUserAndCustomer(username,idCustomer);
	}

	/**
	 * Only analysis user can export.
	 * @param identifier
	 * @param idCustomer
	 * @param username
	 * @return Object[2],  [0]=id, [1] = version
	 */
	@Override
	public List<Object[]> getIdAndVersionByIdentifierAndCustomerAndUsername(String identifier, Integer idCustomer, String username) {
		return daoAnalysis.getIdAndVersionByIdentifierAndCustomerAndUsername(identifier,idCustomer,username);
	}

	@Override
	public AnalysisType getAnalysisTypeById(int idAnalysis) {
		return daoAnalysis.getAnalysisTypeById(idAnalysis);
	}

	@Override
	public boolean hasDefault(AnalysisType analysisType) {
		return daoAnalysis.hasDefault(analysisType);
	}

	@Override
	public List<Analysis> getDefaultProfiles() {
		return daoAnalysis.getDefaultProfiles();
	}

	@Override
	public boolean isDefaultProfile(int analysisId) {
		return daoAnalysis.isDefaultProfile(analysisId);
	}
}