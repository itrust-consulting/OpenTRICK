/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl.jira;

import lu.itrust.business.ts.model.ticketing.impl.AbstractField;


/**
 * Represents a custom field in Jira.
 */
public class JiraCustomField extends AbstractField {

	private String value;
	
	/**
	 * Default constructor.
	 */
	public JiraCustomField() {
	}

	/**
	 * Constructs a JiraCustomField object with the specified id, name, and value.
	 * 
	 * @param id    the id of the custom field
	 * @param name  the name of the custom field
	 * @param value the value of the custom field
	 */
	public JiraCustomField(String id, String name, String value) {
		super(id, name);
		setValue(value);
	}

	/**
	 * Retrieves the value of the custom field.
	 * 
	 * @return the value of the custom field
	 */
	@Override
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value of the custom field.
	 * 
	 * @param value the value to be set
	 */
	@Override
	public void setValue(Object value) {
		setValue((String)value);
	}
	
	/**
	 * Sets the value of the custom field.
	 * 
	 * @param value the value to be set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
