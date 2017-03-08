/**
 * 
 */
package lu.itrust.business.TS.model.general;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author eomar
 *
 */
@Entity
public class TSSetting {

	
	
	@Id
	@Column(name = "idTSSetting")
	@Enumerated(EnumType.STRING)
	private TSSettingName name;

	@Column(name = "dtValue")
	private String value;

	/**
	 * 
	 */
	public TSSetting() {
	}
	
	
	/**
	 * @param name
	 * @param value
	 */
	public TSSetting(TSSettingName name, String value) {
		setName(name);
		setValue(value);
	}

	/**
	 * @param name
	 * @param value
	 */
	public TSSetting(TSSettingName name, Object value) {
		setName(name);
		setValue(value);
	}

	public TSSetting(String name, String value) {
		setName(name);
		setValue(value);
	}

	/**
	 * @return the name
	 */
	public TSSettingName getName() {
		return name;
	}
	
	/**
	 * @return the name
	 */
	public String getNameLower() {
		return String.valueOf(name).toLowerCase();
	}
	
	/**
	 * @return the name
	 */
	public String getNameString() {
		return String.valueOf(name);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@JsonSetter("name")
	public void setName(String name) {
		this.name = TSSettingName.valueOf(name);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(TSSettingName name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @return the value
	 */
	public String getString() {
		return getValue();
	}

	/**
	 * @return the value, default value 0
	 */
	public Integer getInt() {
		return getInt(0);
	}

	/**
	 * @return the value
	 */
	public Integer getInt(Integer defaultValue) {
		return value == null ? defaultValue : Integer.valueOf(value);
	}
	
	/**
	 * @return the value, default value nan
	 */
	public Double getDouble() {
		return getDouble(Double.NaN);
	}

	/**
	 * @return the value
	 */
	public Double getDouble(double defaultValue) {
		return value == null ? defaultValue : Double.valueOf(value);
	}
	
	/**
	 * @return the value, default value false
	 */
	public Boolean getBoolean() {
		return getBoolean(false);
	}

	/**
	 * @return the value
	 */
	public Boolean getBoolean(Boolean defaultValue) {
		return value == null ? defaultValue : Boolean.valueOf(value);
	}

	/**
	 * @param value
	 *            the value to set
	 */
	@JsonSetter("value")
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		setValue((String) (value == null ? null : String.valueOf(value)));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TSSetting [name=" + name + ", value=" + value + "]";
	}
	
}
