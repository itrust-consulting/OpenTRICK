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
	
	private String url;

	/**
	 * 
	 */
	public RedmineIssueLink() {
	}

	/**
	 * @param id
	 * @param name
	 * @param integer 
	 * @param url 
	 */
	public RedmineIssueLink(String id, String name, String type, String url) {
		super(id, name);
		setValue(type);
		setUrl(url);
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
