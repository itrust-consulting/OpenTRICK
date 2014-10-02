package lu.itrust.business.TS.settings;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * AppSettingEntry.java: <br>
 * Detailed description...
 *
 * @author eomar itrust consulting s.a.rl.:
 * @version 
 * @since Aug 19, 2014
 */
@Entity
public class AppSettingEntry {

	@Id @GeneratedValue 
	@Column(name="idAppSettingEntry", nullable=false)
	private int id = -1;

	@Column(name="dtGroup", nullable=false)
	private String group;

	@Column(name="dtName", nullable=false)
	private String name;
	
	@ElementCollection
    @MapKeyColumn(name="idEntryKey", nullable=false)
    @Column(name="dtEntryValue", nullable=false)
    @CollectionTable(name="AppSettingEntryValues", joinColumns=@JoinColumn(name="idAppSettingEntry", nullable=false))
	@Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
	private Map<String, String> values = new LinkedHashMap<String, String>();

	/**
	 * Constructor: <br>
	 */
	public AppSettingEntry() {
	}

	/**
	 * Constructor: <br>
	 * @param group
	 * @param name
	 */
	public AppSettingEntry(String group, String name) {
		setGroup(group);
		setName(name);
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

	/**
	 * getGroup: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * setGroup: <br>
	 * Description
	 * 
	 * @param name
	 */
	public void setGroup(String name) {
		this.group = name;
	}

	/**
	 * getName: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * setName: <br>
	 * Description
	 * 
	 * @param subName
	 */
	public void setName(String subName) {
		this.name = subName;
	}

	/**
	 * getValues: <br>
	 * Description
	 * 
	 * @return
	 */
	public Map<String, String> getValues() {
		return values;
	}

	/**
	 * setValues: <br>
	 * Description
	 * 
	 * @param values
	 */
	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	/**
	 * findByKey: <br>
	 * Description
	 * 
	 * @param key
	 * @return
	 */
	public String findByKey(String key) {
		if (values == null)
			return null;
		return values.get(key);
	}

	/**
	 * update: <br>
	 * Description
	 * 
	 * @param key
	 * @param value
	 */
	public void update(String key, String value) {
		if (values == null)
			setValues(new LinkedHashMap<String, String>());
		values.put(key, value);
	}
}
