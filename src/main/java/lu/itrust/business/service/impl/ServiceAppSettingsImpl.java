/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.usermanagement.AppSettings;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOAppSettings;
import lu.itrust.business.service.ServiceAppSettings;

/**
 * @author eomar
 *
 */
@Service
public class ServiceAppSettingsImpl implements ServiceAppSettings {

	@Autowired
	private DAOAppSettings daoAppSettings;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettings#get(long)
	 */
	@Override
	public AppSettings get(long id) {
		return daoAppSettings.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettings#getFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public AppSettings getFromUser(User user) {
		return daoAppSettings.getFromUser(user);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettings#getFromUsername(java.lang.String)
	 */
	@Override
	public AppSettings getFromUsername(String username) {
		return daoAppSettings.getFromUsername(username);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettings#loadAll()
	 */
	@Override
	public List<AppSettings> loadAll() {
		return daoAppSettings.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettings#save(lu.itrust.business.TS.usermanagement.AppSettings)
	 */
	@Transactional
	@Override
	public AppSettings save(AppSettings appSettings) {
		return daoAppSettings.save(appSettings);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettings#saveOrUpdate(lu.itrust.business.TS.usermanagement.AppSettings)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(AppSettings appSettings) {
		daoAppSettings.saveOrUpdate(appSettings);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettings#merge(lu.itrust.business.TS.usermanagement.AppSettings)
	 */
	@Transactional
	@Override
	public AppSettings merge(AppSettings appSettings) {
		return daoAppSettings.merge(appSettings);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettings#delete(lu.itrust.business.TS.usermanagement.AppSettings)
	 */
	@Transactional
	@Override
	public void delete(AppSettings appSettings) {
		daoAppSettings.delete(appSettings);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAppSettings#delete(long)
	 */
	@Transactional
	@Override
	public void delete(long id) {
		daoAppSettings.delete(id);
	}

}
