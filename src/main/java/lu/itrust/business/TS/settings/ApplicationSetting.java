package lu.itrust.business.TS.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * ApplicationSetting.java: <br>
 * Detailed description...
 * 
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Sep 23, 2014
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiUser", "dtKey" }))
public class ApplicationSetting {

	@Id
	@GeneratedValue
	@Column(name = "idApplicationSetting")
	private Integer id = -1;

	@Column(name = "dtKey", nullable = false)
	private String key = "";

	@Column(name = "dtValue", nullable = false)
	private String value = "";

	public ApplicationSetting() {

	}

	public ApplicationSetting(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * getValue: <br>
	 * Returns the value field value.
	 * 
	 * @return The value of the value field
	 */
	public String getValue() {
		return value;
	}

	/**
	 * setValue: <br>
	 * Sets the Field "value" with a value.
	 * 
	 * @param value
	 *            The Value to set the value field
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * getKey: <br>
	 * Returns the key field value.
	 * 
	 * @return The value of the key field
	 */
	public String getKey() {
		return key;
	}

	/**
	 * setKey: <br>
	 * Sets the Field "key" with a value.
	 * 
	 * @param key
	 *            The Value to set the key field
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id
	 *            The Value to set the id field
	 */
	public void setId(Integer id) {
		this.id = id;
	}
}
