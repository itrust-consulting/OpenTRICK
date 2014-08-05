/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.usermanagement.AppSettingEntry;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public interface ServiceAppSettingEntry {

	AppSettingEntry get(long id);

	List<AppSettingEntry> getByGroupAndName(String group, String name);

	List<AppSettingEntry> getByUserAndGroup(User user, String group);

	List<AppSettingEntry> getByUsernameAndGroup(String username, String group);

	AppSettingEntry getByUserAndGroupAndName(User user, String group, String name);

	AppSettingEntry getByUsernameAndGroupAndName(String username, String group, String name);

	List<AppSettingEntry> loadAll();

	AppSettingEntry save(AppSettingEntry appSettingEntry);

	AppSettingEntry merge(AppSettingEntry appSettingEntry);

	void saveOrUpdate(AppSettingEntry appSettingEntry);

	void delete(AppSettingEntry appSettingEntry);

	void delete(long id);
}
