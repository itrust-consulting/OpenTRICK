package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.settings.AnalysisSetting;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.helper.AnalysisBaseInfo;

/**
 * ServiceAnalysis.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceAnalysis {
	public Analysis get(Integer idAnalysis) throws Exception;

	public Analysis getDefaultProfile() throws Exception;

	public Analysis getFromIdentifierVersion(String identifier, String version) throws Exception;

	public boolean exists(Integer idAnalysis) throws Exception;

	public boolean exists(String identifier, String version) throws Exception;

	public boolean isProfile(Integer idAnalysis) throws Exception;

	public boolean hasData(Integer idAnalysis) throws Exception;
	
	public List<Integer> getAllAnalysisIDs() throws Exception;

	public List<Analysis> getAll() throws Exception;

	public List<Analysis> getAllNotEmpty() throws Exception;

	public List<Analysis> getAllProfiles() throws Exception;

	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID) throws Exception;

	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer) throws Exception;

	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex, Integer pageSize) throws Exception;

	public List<Analysis> getAllFromCustomerAndProfile(Integer idCustomer) throws Exception;

	public List<Analysis> getAllFromCustomerAndProfileByPageAndSizeIndex(Integer customerID, Integer pageIndex, Integer pageSize) throws Exception;
	
	public List<AnalysisBaseInfo> getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(Integer id, String name);

	public List<Analysis> getAllFromUser(User user) throws Exception;

	public List<Analysis> getAllFromCustomer(Customer customer) throws Exception;

	public List<Analysis> getAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) throws Exception;
	
	public List<AnalysisSetting> getAllAnalysisSettings() throws Exception;
	
	public List<AnalysisSetting> getAllAnalysisSettingsFromAnalysis(Integer analysisID) throws Exception;
	
	public List<AnalysisSetting> getAllAnalysisSettingsFromAnalysisAndUser(Integer analysisID, User user) throws Exception;

	public AnalysisSetting getAnalysisSettingsFromAnalysisAndUserByKey(Integer analysisID, String username, String key) throws Exception;
	
	public Parameter getParameterFromAnalysis(Integer idAnalysis, String Parameter) throws Exception;

	public Language getLanguageOfAnalysis(Integer idAnalysis) throws Exception;

	public String getVersionOfAnalysis(Integer idAnalysis) throws Exception;

	public void save(Analysis analysis) throws Exception;

	public void saveOrUpdate(Analysis analysis) throws Exception;

	public void delete(Integer idAnalysis) throws Exception;

	public void delete(Analysis analysis) throws Exception;

	public List<Analysis> getAllFromCustomer(Integer id);

	public List<Analysis> getAllProfileContainsNorm(List<Norm> norms);

	public List<AnalysisBaseInfo> getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(Integer id, String name, String identifier);

	public int getDefaultProfileId();

	public String getLabelFromId(int idAnalysis);

	public String getCustomerNameFromId(int scenario);

	
}