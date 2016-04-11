/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl.jira;

import lu.itrust.business.TS.model.ticketing.TicketingField;

/**
 * @author eomar
 *
 */
public class JiraCustomField implements TicketingField {

	private String id;
	
	private String name;
	
	private Object value;
	
	/**
	 * 
	 */
	public JiraCustomField() {
	}

	/**
	 * @param id
	 * @param name
	 * @param value
	 */
	public JiraCustomField(String id, String name, Object value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingField#getValue()
	 */
	@Override
	public Object getValue() {
		return this.value;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		this.value = value;
	}

}
