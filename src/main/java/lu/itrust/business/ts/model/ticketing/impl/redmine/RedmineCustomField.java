/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl.redmine;

import lu.itrust.business.ts.model.ticketing.impl.AbstractField;

/**
 * @author eomar
 *
 */
public class RedmineCustomField extends AbstractField {
	
	private Object value;
	
	/**
	 * 
	 */
	public RedmineCustomField() {
	}

	/**
	 * @param id
	 * @param name
	 */
	public RedmineCustomField(String id, String name, Object value) {
		super(id, name);
		setValue(value);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingField#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		this.value = value;
	}
}
