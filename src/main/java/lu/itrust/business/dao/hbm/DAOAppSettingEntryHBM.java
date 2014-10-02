/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import javax.persistence.Entity;

import lu.itrust.business.TS.settings.AppSettingEntry;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOAppSettingEntry;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * @author eomar
 *
 */
@Entity @Repository
public class DAOAppSettingEntryHBM extends DAOHibernate implements DAOAppSettingEntry {

	public DAOAppSettingEntryHBM() {
	}

	public DAOAppSettingEntryHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAppSettingEntry#get(long)
	 */
	@Override
	public AppSettingEntry get(long id) {
		return (AppSettingEntry) getSession().get(AppSettingEntry.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettingEntry#getByGroupAndName(java.lang
	 * .String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AppSettingEntry> getByGroupAndName(String group, String name) {
		return getSession().createQuery("From AppSettingEntry where group = :group and name = :name").setString("group", group).setString("name", name).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettingEntry#getByUserAndGroup(lu.itrust
	 * .business.TS.usermanagement.User, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AppSettingEntry> getByUserAndGroup(User user, String group) {
		return getSession()
				.createQuery("Select entry From AppSettings appSettings inner join appSettings.entries as entry where appSettings.user = :user and entry.group = :group")
				.setParameter("user", user).setString("group", group).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettingEntry#getByUsernameAndGroup(java.
	 * lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AppSettingEntry> getByUsernameAndGroup(String username, String group) {
		return getSession()
				.createQuery("Select entry From AppSettings appSettings inner join appSettings.entries as entry where appSettings.user.login = :username and entry.group = :group")
				.setParameter("username", username).setString("group", group).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettingEntry#getByUserAndGroupAndName(lu
	 * .itrust.business.TS.usermanagement.User, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public AppSettingEntry getByUserAndGroupAndName(User user, String group, String name) {
		return (AppSettingEntry) getSession()
				.createQuery(
						"Select entry From AppSettings appSettings inner join appSettings.entries as entry where appSettings.user = :user and entry.group = :group and entry.name = :name")
				.setParameter("user", user).setString("group", group).setString("name", name).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettingEntry#getByUsernameAndGroupAndName
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public AppSettingEntry getByUsernameAndGroupAndName(String username, String group, String name) {
		return (AppSettingEntry) getSession()
				.createQuery(
						"Select entry From AppSettings appSettings inner join appSettings.entries as entry where appSettings.user.login = :username and entry.group = :group and entry.name = :name")
				.setParameter("username", username).setString("group", group).setString("name", name).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAppSettingEntry#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AppSettingEntry> loadAll() {
		return getSession()
				.createQuery(
						"Select entry From AppSettings appSettings inner join appSettings.entries as entry where appSettings.user.login = :username and entry.group = :group and entry.name = :name")
				.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettingEntry#save(lu.itrust.business.TS.
	 * usermanagement.AppSettingEntry)
	 */
	@Override
	public AppSettingEntry save(AppSettingEntry appSettingEntry) {
		return (AppSettingEntry) getSession().save(appSettingEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettingEntry#merge(lu.itrust.business.TS
	 * .usermanagement.AppSettingEntry)
	 */
	@Override
	public AppSettingEntry merge(AppSettingEntry appSettingEntry) {
		return (AppSettingEntry) getSession().merge(appSettingEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettingEntry#saveOrUpdate(lu.itrust.business
	 * .TS.usermanagement.AppSettingEntry)
	 */
	@Override
	public void saveOrUpdate(AppSettingEntry appSettingEntry) {
		getSession().saveOrUpdate(appSettingEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAppSettingEntry#delete(lu.itrust.business.TS
	 * .usermanagement.AppSettingEntry)
	 */
	@Override
	public void delete(AppSettingEntry appSettingEntry) {
		getSession().delete(appSettingEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAppSettingEntry#delete(long)
	 */
	@Override
	public void delete(long id) {
		getSession().delete(get(id));
	}

}
