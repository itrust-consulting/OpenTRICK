/**
 * 
 */
package lu.itrust.business.TS.usermanagement;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * AppSettings.java: <br>
 * Detailed description...
 *
 * @author eomar itrust consulting s.a.rl.:
 * @version 
 * @since Aug 19, 2014
 */
@Entity
public class AppSettings {

	/** id */
	@Id @GeneratedValue
	@Column(name="idAppSettings")
	private int id = -1;
	
	/** user object */
	@ManyToOne
	@JoinColumn(name="fiUser", unique=true, nullable=false)
	private User user;

	/** list of settings for this user */
	@OneToMany
	@JoinColumn(name="fiAppSettings", nullable=false)
	@Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
	private List<AppSettingEntry> entries = new ArrayList<AppSettingEntry>();

	/**
	 * Constructor: <br>
	 */
	public AppSettings() {
	}

	/**
	 * Constructor: <br>
	 * @param user
	 */
	public AppSettings(User user) {
		this.user = user;
	}

	/**
	 * getUser: <br>
	 * Description
	 * 
	 * @return
	 */
	public User getUser() {
		return user;
	}

	/**
	 * setUser: <br>
	 * Description
	 * 
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * getEntries: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<AppSettingEntry> getEntries() {
		return entries;
	}

	/**
	 * setEntries: <br>
	 * Description
	 * 
	 * @param settingsEntries
	 */
	public void setEntries(List<AppSettingEntry> settingsEntries) {
		this.entries = settingsEntries;
	}

	/**
	 * findByGroupAndNameAndKey: <br>
	 * Description
	 * 
	 * @param group
	 * @param name
	 * @param key
	 * @return
	 */
	public String findByGroupAndNameAndKey(String group, String name, String key) {
		AppSettingEntry entry = findByGroupAndName(group, name);
		if (entry == null || key == null)
			return null;
		return entry.findByKey(key);
	}

	/**
	 * findByGroupAndName: <br>
	 * Description
	 * 
	 * @param group
	 * @param name
	 * @return
	 */
	public AppSettingEntry findByGroupAndName(String group, String name) {
		if (group == null || name == null || entries == null || entries.isEmpty())
			return null;
		for (AppSettingEntry entry : entries)
			if (group.equals(entry.getGroup()) && name.equals(entry.getName()))
				return entry;
		return null;
	}

	/**
	 * findByGroup: <br>
	 * Description
	 * 
	 * @param group
	 * @return
	 */
	public List<AppSettingEntry> findByGroup(String group) {
		if (entries == null || entries.isEmpty() || group == null)
			return null;
		List<AppSettingEntry> entries = new ArrayList<AppSettingEntry>();
		for (AppSettingEntry appSettingEntry : this.entries) {
			if (group.equals(appSettingEntry.getGroup()))
				entries.add(appSettingEntry);
		}
		return entries;
	}

	/**
	 * update: <br>
	 * Description
	 * 
	 * @param group
	 * @param name
	 * @param key
	 * @param value
	 */
	public synchronized void update(String group, String name, String key, String value) {
		AppSettingEntry entry = findByGroupAndName(group, name);
		if (entry == null) {
			if(entries == null)
				setEntries(new ArrayList<AppSettingEntry>());
			entries.add(entry = new AppSettingEntry(group, name));
		}
		entry.update(key, value);
	}

	/**
	 * getId: <br>
	 * Description
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Description
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
}