package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.usermanagement.User;

/**
 * DAOAnalysis.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOAnalysis {
	public Analysis get(Integer idAnalysis) throws Exception;

	public Analysis getDefaultProfile() throws Exception;

	public Analysis getFromIdentifierVersion(String identifier, String version) throws Exception;

	public boolean exists(Integer idAnalysis) throws Exception;

	public boolean exists(String identifier, String version) throws Exception;

	public boolean isProfile(Integer idAnalysis) throws Exception;

	public List<Integer> getAllAnalysisIDs() throws Exception;

	public List<Analysis> getAll() throws Exception;

	public List<Analysis> getAllNotEmpty() throws Exception;

	public List<Analysis> getAllProfiles() throws Exception;

	public List<Analysis> getAllFromUserAndCustomer(String userName, Integer customerID) throws Exception;

	public List<Analysis> getAllNotEmptyFromUserAndCustomer(String userName, Integer idCustomer) throws Exception;

	public List<Analysis> getAllFromUserAndCustomerByPageAndSizeIndex(String login, Integer customer, Integer pageIndex, Integer pageSize) throws Exception;

	public List<Analysis> getAllFromCustomerAndProfile(Integer idCustomer) throws Exception;

	public List<Analysis> getAllFromCustomerAndProfileByPageAndSizeIndex(Integer customerID, Integer pageIndex, Integer pageSize) throws Exception;

	public List<Analysis> getAllFromUser(User user) throws Exception;

	public List<Analysis> getAllFromCustomer(Customer customer) throws Exception;

	public List<Analysis> getAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) throws Exception;

	public Parameter getParameterFromAnalysis(Integer idAnalysis, String Parameter) throws Exception;

	public Language getLanguageOfAnalysis(Integer idAnalysis) throws Exception;

	public String getVersionOfAnalysis(Integer idAnalysis) throws Exception;

	public void save(Analysis analysis) throws Exception;

	public void saveOrUpdate(Analysis analysis) throws Exception;

	public void delete(Integer idAnalysis) throws Exception;

	public void delete(Analysis analysis) throws Exception;
}