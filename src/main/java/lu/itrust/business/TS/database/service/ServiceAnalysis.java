package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.standard.Standard;
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

	public void delete(Analysis analysis) ;

	public void delete(Integer idAnalysis) ;

	public boolean exists(Integer idAnalysis) ;

	public boolean exists(String identifier);

	public boolean exists(String identifier, String version) ;

	public Analysis get(Integer idAnalysis) ;

	public List<Analysis> getAll() ;

	public List<Integer> getAllAnalysisIDs() ;

	public List<Analysis> getAllByIdentifier(String identifier);

	public List<Analysis> getAllFromCustomer(Customer customer) ;

	public List<Analysis> getAllFromCustomer(Integer id);

	public List<Analysis> getAllFromCustomerAndProfile(Integer idCustomer) ;

	public List<Analysis> getAllFromCustomerAndProfileByPageAndSizeIndex(Integer customerID, Integer pageIndex, Integer pageSize) ;

	public List<Analysis> getAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) ;

	public List<Analysis> getAllFromUser(User user) ;

	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID) ;

	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex, Integer pageSize) ;

	public List<Analysis> getAllNotEmpty() ;

	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer) ;

	public List<String> getAllNotEmptyVersion(int analysisId);

	public List<String> getAllNotEmptyVersion(String identfier);

	public List<Analysis> getAllProfileContainsStandard(List<Standard> standards);

	public List<Analysis> getAllProfiles() ;

	public List<String> getAllVersion(String identifier);

	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String username, String identifier, List<AnalysisRight> rights);

	public Integer getCustomerIdByIdAnalysis(int analysisId);

	public String getCustomerNameFromId(int idAnalysis);

	public List<Customer> getCustomersByIdAnalysis(String identifier);

	public Analysis getDefaultProfile() ;

	public int getDefaultProfileId();

	public Analysis getFromIdentifierVersionCustomer(String identifier, String version, Integer customerID) ;

	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String name, List<AnalysisRight> rights);

	public String getIdentifierByIdAnalysis(int analysisId);

	public Integer getIdFromIdentifierAndVersion(String identifier, String string);

	public String getLabelFromId(int idAnalysis);

	public Language getLanguageOfAnalysis(Integer idAnalysis) ;

	public Parameter getParameterFromAnalysis(Integer idAnalysis, String Parameter) ;

	public String getVersionOfAnalysis(Integer idAnalysis) ;

	public boolean hasData(Integer idAnalysis) ;

	public boolean isAnalysisCssf(Integer analysisID) ;

	public boolean isAnalysisOwner(Integer analysisId, String userName);

	public boolean isAnalysisUncertainty(Integer analysisID) ;

	public boolean isProfile(Integer idAnalysis) ;

	public void save(Analysis analysis) ;

	public void saveOrUpdate(Analysis analysis) ;

	public List<Customer> getCustomersByIdAnalysis(int analysisId);

	public boolean isAnalysisCustomer(int idAnalysis, int idCustomer);

	public List<String> getNamesByUserAndCustomerAndNotEmpty(String username, Integer idCustomer);

	public List<Analysis> getAllByUserAndCustomerAndNameAndNotEmpty(String username, Integer idCustomer, String name);

	public boolean isProfileNameInUsed(String name);

	public List<String> getAllVersion(Integer analysisId);

	public List<Analysis> getAllFromOwner(User user);

	public List<Analysis> getAllHasRightsAndContainsStandard(String username, List<AnalysisRight> rights, List<Standard> standards);

	public boolean existsByNameAndCustomerId(String name, int idCustomer);

	public List<Analysis> getAllNotEmpty(int pageIndex, int pageSize);

	public int countNotEmpty();

	public Analysis getByCustomerAndLabelAndVersion(int customerId, String name, String version);

	public Analysis getByIdentifierAndVersion(String identifier, String version);

	public Analysis getProfileByName(String string);

	public int countNotEmptyNoItemInformationAndRiskInformation();
	
	public List<Analysis> getAllNotEmptyNoItemInformationAndRiskInformation(int pageIndex, int pageSize);

	public String getProjectIdById(Integer idAnalysis);
	
	public boolean hasProject(int idAnalysis);

	public String getProjectIdByIdentifier(String identifier);

	public List<String> getAllProjectIds();

	public List<Analysis> getByUsernameAndIds(String username, List<Integer> ids);


}