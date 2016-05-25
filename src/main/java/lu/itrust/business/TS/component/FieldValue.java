package lu.itrust.business.TS.component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class FieldValue {

	private String name;

	private Object value;

	private String title;

	private Object realValue;

	/**
	 * @param name
	 * @param value
	 */
	public FieldValue(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * @param name
	 * @param value
	 * @param title
	 */
	public FieldValue(String name, Object value, String title) {
		this.name = name;
		this.value = value;
		this.title = title;
	}

	/**
	 * @param name
	 * @param value
	 * @param title
	 * @param realValue
	 */
	public FieldValue(String name, Object value, String title, Object realValue) {
		this.name = name;
		this.value = value;
		this.title = title;
		this.realValue = realValue;
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
	public Object getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the realValue
	 */
	public Object getRealValue() {
		return realValue;
	}

	/**
	 * @param realValue
	 *            the realValue to set
	 */
	public void setRealValue(Object realValue) {
		this.realValue = realValue;
	}

}
