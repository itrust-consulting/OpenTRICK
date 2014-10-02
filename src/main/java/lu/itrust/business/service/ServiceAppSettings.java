/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.settings.AppSettings;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public interface ServiceAppSettings {
	
	AppSettings get(long id);
	
	AppSettings getFromUser(User user);
	
	AppSettings getFromUsername(String username);
	
	List<AppSettings> loadAll();
	
	AppSettings save(AppSettings appSettings);
	
	void saveOrUpdate(AppSettings appSettings);
	
	AppSettings merge(AppSettings appSettings);
	
	void delete(AppSettings appSettings);
	
	void delete(long id);

}
