/**
 * 
 */
package lu.itrust.business.TS.usermanagement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class AppSettings {

	private long id = -1;

	private User user;

	private List<AppSettingEntry> entries = new ArrayList<AppSettingEntry>();

	public AppSettings() {
	}

	public AppSettings(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<AppSettingEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<AppSettingEntry> settingsEntries) {
		this.entries = settingsEntries;
	}

	public String findByGroupAndNameAndKey(String group, String name, String key) {
		AppSettingEntry entry = findByGroupAndName(group, name);
		if (entry == null || key == null)
			return null;
		return entry.findByKey(key);
	}

	public AppSettingEntry findByGroupAndName(String group, String name) {
		if (group == null || name == null || entries == null || entries.isEmpty())
			return null;
		for (AppSettingEntry entry : entries)
			if (group.equals(entry.getGroup()) && name.equals(entry.getName()))
				return entry;
		return null;
	}

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

	public synchronized void update(String group, String name, String key, String value) {
		AppSettingEntry entry = findByGroupAndName(group, name);
		if (entry == null) {
			if(entries == null)
				setEntries(new ArrayList<AppSettingEntry>());
			entries.add(entry = new AppSettingEntry(group, name));
		}
		entry.update(key, value);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
