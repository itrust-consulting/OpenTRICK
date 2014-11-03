/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOAppSettingEntry;
import lu.itrust.business.TS.database.service.ServiceAppSettingEntry;
import lu.itrust.business.TS.settings.AppSettingEntry;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Service
public class ServiceAppSettingEntryImpl implements ServiceAppSettingEntry {

	@Autowired
	private DAOAppSettingEntry daoAppSettingEntry;

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#get(long)
	 */
	@Override
	public AppSettingEntry get(long id) {
		return daoAppSettingEntry.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#getByGroupAndName(java.lang.String, java.lang.String)
	 */
	@Override
	public List<AppSettingEntry> getByGroupAndName(String group, String name) {
		return daoAppSettingEntry.getByGroupAndName(group, name);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#getByUserAndGroup(lu.itrust.business.TS.usermanagement.User, java.lang.String)
	 */
	@Override
	public List<AppSettingEntry> getByUserAndGroup(User user, String group) {
		return daoAppSettingEntry.getByUserAndGroup(user, group);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#getByUsernameAndGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public List<AppSettingEntry> getByUsernameAndGroup(String username, String group) {
		return daoAppSettingEntry.getByUsernameAndGroup(username, group);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#getByUserAndGroupAndName(lu.itrust.business.TS.usermanagement.User, java.lang.String, java.lang.String)
	 */
	@Override
	public AppSettingEntry getByUserAndGroupAndName(User user, String group, String name) {
		return daoAppSettingEntry.getByUserAndGroupAndName(user, group, name);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#getByUsernameAndGroupAndName(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public AppSettingEntry getByUsernameAndGroupAndName(String username, String group, String name) {
		return daoAppSettingEntry.getByUsernameAndGroupAndName(username, group, name);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#loadAll()
	 */
	@Override
	public List<AppSettingEntry> loadAll() {
		return daoAppSettingEntry.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#save(lu.itrust.business.TS.usermanagement.AppSettingEntry)
	 */
	@Transactional
	@Override
	public AppSettingEntry save(AppSettingEntry appSettingEntry) {
		return daoAppSettingEntry.save(appSettingEntry);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#merge(lu.itrust.business.TS.usermanagement.AppSettingEntry)
	 */
	@Transactional
	@Override
	public AppSettingEntry merge(AppSettingEntry appSettingEntry) {
		return daoAppSettingEntry.merge(appSettingEntry);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#saveOrUpdate(lu.itrust.business.TS.usermanagement.AppSettingEntry)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(AppSettingEntry appSettingEntry) {
		daoAppSettingEntry.saveOrUpdate(appSettingEntry);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#delete(lu.itrust.business.TS.usermanagement.AppSettingEntry)
	 */
	@Transactional
	@Override
	public void delete(AppSettingEntry appSettingEntry) {
		daoAppSettingEntry.delete(appSettingEntry);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettingEntry#delete(long)
	 */
	@Transactional
	@Override
	public void delete(long id) {
		daoAppSettingEntry.delete(id);

	}

}
