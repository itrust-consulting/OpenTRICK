/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl.redmine;

import lu.itrust.business.ts.model.ticketing.impl.AbstractField;


/**
 * Represents a custom field in Redmine.
 */
public class RedmineCustomField extends AbstractField {
	
	private Object value;
	
	/**
	 * Default constructor.
	 */
	public RedmineCustomField() {
	}

	/**
	 * Constructs a RedmineCustomField with the specified id, name, and value.
	 *
	 * @param id    the id of the custom field
	 * @param name  the name of the custom field
	 * @param value the value of the custom field
	 */
	public RedmineCustomField(String id, String name, Object value) {
		super(id, name);
		setValue(value);
	}

	/**
	 * Gets the value of the custom field.
	 *
	 * @return the value of the custom field
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the value of the custom field.
	 *
	 * @param value the value to set
	 */
	@Override
	public void setValue(Object value) {
		this.value = value;
	}
}
