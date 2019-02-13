/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl.redmine;

import lu.itrust.business.TS.model.ticketing.impl.AbstractField;

/**
 * @author eomar
 *
 */
public class RedmineIssueLink extends AbstractField {
	
	private String value;

	/**
	 * 
	 */
	public RedmineIssueLink() {
	}

	/**
	 * @param id
	 * @param name
	 * @param integer 
	 */
	public RedmineIssueLink(String id, String name, String integer) {
		super(id, name);
		setValue(integer);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingField#getValue()
	 */
	@Override
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		this.value = value == null? null : value.toString();
	}

}
