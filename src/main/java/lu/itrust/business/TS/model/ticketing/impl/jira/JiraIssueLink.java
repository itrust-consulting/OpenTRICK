/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl.jira;

import lu.itrust.business.TS.model.ticketing.impl.AbstractField;

/**
 * @author eomar
 *
 */
public class JiraIssueLink extends AbstractField {

	private String link;

	/**
	 * 
	 */
	public JiraIssueLink() {
	}

	/**
	 * @param id
	 * @param name
	 */
	public JiraIssueLink(String id, String name, String link) {
		super(id, name);
		setValue(link);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.TicketingField#getValue()
	 */
	@Override
	public String getValue() {
		return getLink();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.TicketingField#setValue(java.lang.
	 * Object)
	 */
	@Override
	public void setValue(Object value) {
		setLink((String) value);
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

}
