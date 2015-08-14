/**
 * 
 */
package lu.itrust.business.TS.model.general;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author eomar
 *
 */
@Entity
public class TSSetting {

	@Id
	@Column(name = "idTSSetting")
	private String name;

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
	public TSSetting(String name, String value) {
		setName(name);
		setValue(value);
	}

	/**
	 * @param name
	 * @param value
	 */
	public TSSetting(String name, Object value) {
		setName(name);
		setValue(value);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
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
		return getName();
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
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		setName((String) (value == null ? null : String.valueOf(value)));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TSSetting [name=" + name + ", value=" + value + "]";
	}
	
}
