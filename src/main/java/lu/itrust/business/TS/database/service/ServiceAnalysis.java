package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.data.general.Language;
import lu.itrust.business.TS.data.parameter.Parameter;
import lu.itrust.business.TS.data.standard.Standard;
import lu.itrust.business.TS.usermanagement.User;

/**
 * ServiceAnalysis.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceAnalysis {
	
	public Long countByIdentifier(String identifier);

	public void delete(Analysis analysis) throws Exception;

	public void delete(Integer idAnalysis) throws Exception;

	public boolean exists(Integer idAnalysis) throws Exception;

	public boolean exists(String identifier);

	public boolean exists(String identifier, String version) throws Exception;
	
	public Analysis get(Integer idAnalysis) throws Exception;

	public List<Analysis> getAll() throws Exception;

	public List<Integer> getAllAnalysisIDs() throws Exception;

	public List<Analysis> getAllByIdentifier(String identifier);

	public List<Analysis> getAllFromCustomer(Customer customer) throws Exception;

	public List<Analysis> getAllFromCustomer(Integer id);

	public List<Analysis> getAllFromCustomerAndProfile(Integer idCustomer) throws Exception;

	public List<Analysis> getAllFromCustomerAndProfileByPageAndSizeIndex(Integer customerID, Integer pageIndex, Integer pageSize) throws Exception;

	public List<Analysis> getAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) throws Exception;

	public List<Analysis> getAllFromUser(User user) throws Exception;

	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID) throws Exception;

	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex, Integer pageSize) throws Exception;

	public List<Analysis> getAllNotEmpty() throws Exception;
	
	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer) throws Exception;
	
	public List<String> getAllNotEmptyVersion(int analysisId);
	
	public List<String> getAllNotEmptyVersion(String identfier);

	public List<Analysis> getAllProfileContainsStandard(List<Standard> standards);

	public List<Analysis> getAllProfiles() throws Exception;

	public List<String> getAllVersion(String identifier);

	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String username, String identifier);

	public Integer getCustomerIdByIdAnalysis(int analysisId);

	public String getCustomerNameFromId(int idAnalysis);

	public List<Customer> getCustomersByIdAnalysis(String identifier);

	public Analysis getDefaultProfile() throws Exception;

	public int getDefaultProfileId();

	public Analysis getFromIdentifierVersionCustomer(String identifier, String version, Integer customerID) throws Exception;

	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String name);

	public String getIdentifierByIdAnalysis(int analysisId);

	public Integer getIdFromIdentifierAndVersion(String identifier, String string);

	public String getLabelFromId(int idAnalysis);

	public Language getLanguageOfAnalysis(Integer idAnalysis) throws Exception;

	public Parameter getParameterFromAnalysis(Integer idAnalysis, String Parameter) throws Exception;

	public String getVersionOfAnalysis(Integer idAnalysis) throws Exception;

	public boolean hasData(Integer idAnalysis) throws Exception;

	public boolean isAnalysisCssf(Integer analysisID) throws Exception;

	public boolean isAnalysisOwner(Integer analysisId, String userName);

	public boolean isAnalysisUncertainty(Integer analysisID) throws Exception;

	public boolean isProfile(Integer idAnalysis) throws Exception;

	public void save(Analysis analysis) throws Exception;

	public void saveOrUpdate(Analysis analysis) throws Exception;

	public List<Customer> getCustomersByIdAnalysis(int analysisId);

	public boolean isAnalysisCustomer(int idAnalysis ,int idCustomer);

	public List<String> getNamesByUserAndCustomerAndNotEmpty(String username, Integer idCustomer);

	public List<Analysis> getAllByUserAndCustomerAndNameAndNotEmpty(String username, Integer idCustomer, String name);

}