/**
 * 
 */
package lu.itrust.business.service;

import java.sql.Timestamp;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author oensuifudine
 * 
 */
public interface ServiceAnalysis {

	public Analysis get(int id) throws Exception;

	public boolean exist(int id);

	public Analysis get(int id, String identifier, String version, String creationDate) throws Exception;

	public Analysis get(int id, String identifier, String version, Timestamp creationDate) throws Exception;
	
	public String getVersionOfAnalysis(int id) throws Exception;

	public boolean analysisExist(String identifier, String version) throws Exception;

	public Analysis getFromIdentifierVersion(String identifier, String version) throws Exception;

	public Language getLanguageFromAnalysis(int analysisID) throws Exception;

	public List<Analysis> loadByUserAndCustomer(String login, String customer);
	
	public List<Analysis> loadByUserAndCustomer(String login, String customer, int pageIndex, int pageSize);

	public List<Analysis> loadAllFromCustomerIdentifierVersion(Customer customer, String identifier, String version) throws Exception;

	public List<Analysis> loadAllFromCustomer(Customer customer) throws Exception;

	public List<Analysis> loadAllFromUser(User user) throws Exception;

	public List<Analysis> loadAll() throws Exception;

	public List<Analysis> loadAllNotEmpty() throws Exception;

	public void save(Analysis analysis) throws Exception;

	public void saveOrUpdate(Analysis analysis) throws Exception;

	public void remove(Analysis analysis) throws Exception;

	public void remove(Integer analysisId) throws Exception;

}
