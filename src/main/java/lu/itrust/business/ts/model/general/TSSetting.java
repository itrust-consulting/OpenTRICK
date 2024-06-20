/**
 * 
 */
package lu.itrust.business.ts.model.general;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonSetter;


/**
 * Represents a TSSetting, which is a configuration setting in the system.
 * Each TSSetting has a name and a value.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TSSetting {

	/**
	 * Enumeration representing the name of the TSSetting.
	 */
	@Id
	@Column(name = "idTSSetting")
	@Enumerated(EnumType.STRING)
	private TSSettingName name;

	/**
	 * The value of the TSSetting.
	 */
	@Column(name = "dtValue")
	private String value;

	/**
	 * Default constructor for TSSetting.
	 */
	public TSSetting() {
	}

	/**
	 * Constructor for TSSetting with name and value.
	 *
	 * @param name  the name of the TSSetting
	 * @param value the value of the TSSetting
	 */
	public TSSetting(TSSettingName name, String value) {
		setName(name);
		setValue(value);
	}

	/**
	 * Constructor for TSSetting with name and value.
	 *
	 * @param name  the name of the TSSetting
	 * @param value the value of the TSSetting
	 */
	public TSSetting(TSSettingName name, Object value) {
		setName(name);
		setValue(value);
	}

	/**
	 * Constructor for TSSetting with name and value.
	 *
	 * @param name  the name of the TSSetting
	 * @param value the value of the TSSetting
	 */
	public TSSetting(String name, String value) {
		setName(name);
		setValue(value);
	}

	/**
	 * Get the name of the TSSetting.
	 *
	 * @return the name of the TSSetting
	 */
	public TSSettingName getName() {
		return name;
	}

	/**
	 * Get the lowercase name of the TSSetting.
	 *
	 * @return the lowercase name of the TSSetting
	 */
	public String getNameLower() {
		return String.valueOf(name).toLowerCase();
	}

	/**
	 * Get the string representation of the name of the TSSetting.
	 *
	 * @return the string representation of the name of the TSSetting
	 */
	public String getNameString() {
		return String.valueOf(name);
	}

	/**
	 * Set the name of the TSSetting.
	 *
	 * @param name the name to set
	 */
	@JsonSetter("name")
	public void setName(String name) {
		this.name = TSSettingName.valueOf(name);
	}

	/**
	 * Set the name of the TSSetting.
	 *
	 * @param name the name to set
	 */
	public void setName(TSSettingName name) {
		this.name = name;
	}

	/**
	 * Get the value of the TSSetting.
	 *
	 * @return the value of the TSSetting
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Get the string representation of the value of the TSSetting.
	 *
	 * @return the string representation of the value of the TSSetting
	 */
	public String getString() {
		return getValue();
	}

	/**
	 * Get the integer representation of the value of the TSSetting.
	 * If the value is null, returns the default value 0.
	 *
	 * @return the integer representation of the value of the TSSetting
	 */
	public Integer getInt() {
		return getInt(0);
	}

	/**
	 * Get the integer representation of the value of the TSSetting.
	 * If the value is null, returns the default value.
	 *
	 * @param defaultValue the default value to return if the value is null
	 * @return the integer representation of the value of the TSSetting
	 */
	public Integer getInt(Integer defaultValue) {
		return value == null ? defaultValue : Integer.valueOf(value);
	}

	/**
	 * Get the double representation of the value of the TSSetting.
	 * If the value is null, returns the default value NaN.
	 *
	 * @return the double representation of the value of the TSSetting
	 */
	public Double getDouble() {
		return getDouble(Double.NaN);
	}

	/**
	 * Get the double representation of the value of the TSSetting.
	 * If the value is null, returns the default value.
	 *
	 * @param defaultValue the default value to return if the value is null
	 * @return the double representation of the value of the TSSetting
	 */
	public Double getDouble(double defaultValue) {
		return value == null ? defaultValue : Double.valueOf(value);
	}

	/**
	 * Get the boolean representation of the value of the TSSetting.
	 * If the value is null, returns the default value false.
	 *
	 * @return the boolean representation of the value of the TSSetting
	 */
	public Boolean getBoolean() {
		return getBoolean(false);
	}

	/**
	 * Get the boolean representation of the value of the TSSetting.
	 * If the value is null, returns the default value.
	 *
	 * @param defaultValue the default value to return if the value is null
	 * @return the boolean representation of the value of the TSSetting
	 */
	public Boolean getBoolean(Boolean defaultValue) {
		return value == null ? defaultValue : Boolean.valueOf(value);
	}

	/**
	 * Set the value of the TSSetting.
	 *
	 * @param value the value to set
	 */
	@JsonSetter("value")
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Set the value of the TSSetting.
	 *
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		setValue((String) (value == null ? null : String.valueOf(value)));
	}

	/**
	 * Get the string representation of the TSSetting.
	 *
	 * @return the string representation of the TSSetting
	 */
	@Override
	public String toString() {
		return "TSSetting [name=" + name + ", value=" + value + "]";
	}

}
