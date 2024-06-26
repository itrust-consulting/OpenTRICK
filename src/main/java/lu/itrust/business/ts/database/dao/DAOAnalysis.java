package lu.itrust.business.ts.database.dao;

import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.usermanagement.User;

/**
 * DAOAnalysis.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOAnalysis {

	public Long countByIdentifier(String identifier);

	public int countNotEmpty();

	public int countNotEmptyNoItemInformationAndRiskInformation();

	public Long countNotProfileDistinctIdentifier();

	public void delete(Analysis analysis);

	public void delete(Integer idAnalysis);

	public boolean exists(Integer idAnalysis);

	public boolean exists(String identifier);

	public boolean exists(String identifier, String version);

	public boolean existsByNameAndCustomerId(String name, int idCustomer);

	public Analysis get(Integer idAnalysis);

	public List<Analysis> getAll();

	public List<Analysis> getAll(List<Integer> ids);

	public List<Integer> getAllAnalysisIDs();

	public List<Analysis> getAllByIdentifier(String identifier);

	public List<Analysis> getAllByUserAndCustomerAndNameAndNotEmpty(String username, Integer idCustomer, String name);

	public List<Analysis> getAllContains(MeasureDescription measureDescription);

	public List<Analysis> getAllFromCustomer(Customer customer);

	public List<Analysis> getAllFromCustomer(Integer id);

	public List<Analysis> getAllFromCustomerAndProfile(Integer idCustomer);

	public List<Analysis> getAllFromCustomerAndProfileByPageAndSizeIndex(Integer customerID, Integer pageIndex, Integer pageSize);

	public List<Analysis> getAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version);

	public List<Analysis> getAllFromOwner(User user);

	public List<Analysis> getAllFromUser(User user);

	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID);

	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex, Integer pageSize);

	public List<Analysis> getAllHasRightsAndContainsStandard(String username, List<AnalysisRight> rights, List<Standard> standards, AnalysisType ... analysisTypes);

	public List<Analysis> getAllNotEmpty();

	public List<Analysis> getAllNotEmpty(int pageIndex, int pageSize);

	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer);
	
	public List<Analysis> getAllNotEmptyNoItemInformationAndRiskInformation(int pageIndex, int pageSize);

	public List<String> getAllNotEmptyVersion(int analysisId);

	public List<String> getAllNotEmptyVersion(String identfier);

	public List<Analysis> getAllProfileContainsStandard(List<Standard> standards, AnalysisType ... analysisTypes);

	public List<Analysis> getAllProfiles();

	public List<String> getAllProjectIds();

	public List<String> getAllVersion(Integer analysisId);

	public List<String> getAllVersion(String identifier);

	public AnalysisType getAnalysisTypeById(int idAnalysis);

	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String username, String identifier, AnalysisType type,
			List<AnalysisRight> rights);

	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String username, String identifier, List<AnalysisRight> rights);

	public Analysis getByAnalysisStandardId(int idAnalysisStandard);

	public Analysis getByCustomerAndNameAndVersion(int customerId, String name, String version);

	public Analysis getByIdentifierAndVersion(String identifier, String version);

	public List<Analysis> getByUsernameAndCustomerAndNoEmptyAndGroupByIdentifier(String username, Integer customerId);

	public Analysis getByUsernameAndId(String username, Integer analysisId);

	public List<Analysis> getByUsernameAndIds(String username, List<Integer> ids);

	public Integer getCustomerIdByIdAnalysis(int analysisId);

	public String getCustomerNameFromId(int idAnalysis);

	public List<Customer> getCustomersByIdAnalysis(int analysisId);

	public List<Customer> getCustomersByIdAnalysis(String identifier);

	public Analysis getDefaultProfile(AnalysisType analysisType);

	public int findDefaultProfileIdByAnalysisTypeAndLanguage(AnalysisType analysisType, Language language);

	public List<Analysis> getDefaultProfiles();

	public Analysis getFromIdentifierVersionCustomer(String identifier, String version, Integer customerID);

	public List<Analysis> getFromUserNameAndNotEmpty(String userName, List<AnalysisRight> rights);

	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String username, AnalysisType type, List<AnalysisRight> rights);

	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String username, List<AnalysisRight> rights);

	/**
	 * Only analysis user can export.
	 * @param identifier
	 * @param idCustomer
	 * @param username
	 * @return Object[2],  [0]=id, [1] = version
	 */
	public List<Object[]> getIdAndVersionByIdentifierAndCustomerAndUsername(String identifier, Integer idCustomer, String username);

	/**
	 * Only analysis user can export.
	 * @param name
	 * @param idCustomer
	 * @return Object[2],  [0]=identifier, [1] = name
	 */
	public List<Object[]> getIdentifierAndNameByUserAndCustomer(String username, Integer idCustomer);

	public String getIdentifierByIdAnalysis(int analysisId);

	public Integer getIdFromIdentifierAndVersion(String identifier, String string);

	public String getLabelFromId(int idAnalysis);

	public Language getLanguageOfAnalysis(Integer idAnalysis);

	public List<String> getNamesByUserAndCustomerAndNotEmpty(String username, Integer idCustomer);

	public List<String> getNotProfileIdentifiers(int page, int size);

	public IParameter getParameterFromAnalysis(Integer idAnalysis, String Parameter);

	public Analysis getProfileByName(String name);

	public String getProjectIdById(Integer idAnalysis);

	public String getProjectIdByIdentifier(String identifier);

	public String getVersionOfAnalysis(Integer idAnalysis);

	public boolean hasData(Integer idAnalysis);

	public boolean hasData(String identifier);

	public boolean hasDefault(AnalysisType analysisType);

	public boolean hasProject(int idAnalysis);

	public boolean isAnalysisCustomer(int idAnalysis, int idCustomer);

	public boolean isAnalysisOwner(Integer analysisId, String userName);

	public boolean isAnalysisUncertainty(Integer analysisID);

	public boolean isDefaultProfile(int analysisId);

	public boolean isProfile(Integer idAnalysis);

	public boolean isProfileNameInUsed(String name);

	public void save(Analysis analysis);

	public void saveOrUpdate(Analysis analysis);

	public Map<String, String> getSettingsByIdAnalysis(Integer idAnalysis);

	public <T> T findSetting(Integer idAnalysis, AnalysisSetting setting);

	public Analysis findByIdAndEager(Integer analysisId);

	public String findIdentifierByCustomerAndLabel(int customerId, String label);

	public Analysis findByIdAndCustomer(Integer id, Customer customer);

}
