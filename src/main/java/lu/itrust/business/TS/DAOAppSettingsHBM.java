/**
 * 
 */
package lu.itrust.business.TS;

import java.util.List;

import javax.persistence.Entity;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.usermanagement.AppSettings;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOAppSettings;
import lu.itrust.business.dao.hbm.DAOHibernate;

/**
 * @author eomar
 *
 */
@Entity @Repository
public class DAOAppSettingsHBM extends DAOHibernate implements DAOAppSettings {

	/**
	 * 
	 */
	public DAOAppSettingsHBM() {
	}

	/**
	 * @param session
	 */
	public DAOAppSettingsHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAppSettings#get(long)
	 */
	@Override
	public AppSettings get(long id) {
		return (AppSettings) getSession().get(AppSettings.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettings#getFromUser(lu.itrust.business.
	 * TS.usermanagement.User)
	 */
	@Override
	public AppSettings getFromUser(User user) {
		return (AppSettings) getSession().createQuery("From AppSettings where user = :user").setParameter("user", user).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettings#getFromUsername(java.lang.String)
	 */
	@Override
	public AppSettings getFromUsername(String username) {
		return (AppSettings) getSession().createQuery("From AppSettings where user.login = :username").setParameter("username", username).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAppSettings#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AppSettings> loadAll() {
		return getSession().createQuery("From AppSettings").list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAppSettings#save(lu.itrust.business.TS.
	 * usermanagement.AppSettings)
	 */
	@Override
	public AppSettings save(AppSettings appSettings) {
		return (AppSettings) getSession().save(appSettings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettings#saveOrUpdate(lu.itrust.business
	 * .TS.usermanagement.AppSettings)
	 */
	@Override
	public void saveOrUpdate(AppSettings appSettings) {
		getSession().saveOrUpdate(appSettings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAppSettings#merge(lu.itrust.business.TS.
	 * usermanagement.AppSettings)
	 */
	@Override
	public AppSettings merge(AppSettings appSettings) {
		return (AppSettings) getSession().merge(appSettings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAppSettings#delete(lu.itrust.business.TS.
	 * usermanagement.AppSettings)
	 */
	@Override
	public void delete(AppSettings appSettings) {
		getSession().delete(appSettings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAppSettings#delete(long)
	 */
	@Override
	public void delete(long id) {
		getSession().delete(get(id));
	}

}
