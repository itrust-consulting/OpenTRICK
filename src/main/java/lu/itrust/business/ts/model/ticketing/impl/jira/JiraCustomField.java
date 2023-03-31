/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl.jira;

import lu.itrust.business.ts.model.ticketing.impl.AbstractField;

/**
 * @author eomar
 *
 */
public class JiraCustomField extends AbstractField {

	private String value;
	
	/**
	 * 
	 */
	public JiraCustomField() {
	}

	/**
	 * @param id
	 * @param name
	 */
	public JiraCustomField(String id, String name, String value) {
		super(id, name);
		setValue(value);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingField#getValue()
	 */
	@Override
	public String getValue() {
		return this.value;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		setValue((String)value);
	}
	
	public void setValue(String value) {
		this.value = value;
	}

}
