/**
 * 
 */
package lu.itrust.business.TS.usermanagement;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author eomar
 *
 */
public class AppSettingEntry {

	private long id = -1;

	private String group;

	private String name;

	private Map<String, String> values = new LinkedHashMap<String, String>();

	public AppSettingEntry() {
	}

	public AppSettingEntry(String group, String name) {
		setGroup(group);
		setName(name);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String name) {
		this.group = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String subName) {
		this.name = subName;
	}

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	public String findByKey(String key) {
		if (values == null)
			return null;
		return values.get(key);
	}

	public void update(String key, String value) {
		if (values == null)
			setValues(new LinkedHashMap<String, String>());
		values.put(key, value);
	}
}
