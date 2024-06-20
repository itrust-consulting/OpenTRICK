/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl.redmine;

import lu.itrust.business.ts.model.ticketing.impl.AbstractField;


/**
 * Represents a Redmine issue link.
 */
public class RedmineIssueLink extends AbstractField {
	
	private String value;
	
	private String url;

	/**
	 * Default constructor.
	 */
	public RedmineIssueLink() {
	}

	/**
	 * Constructs a RedmineIssueLink object with the specified parameters.
	 *
	 * @param id   the ID of the issue link
	 * @param name the name of the issue link
	 * @param type the type of the issue link
	 * @param url  the URL of the issue link
	 */
	public RedmineIssueLink(String id, String name, String type, String url) {
		super(id, name);
		setValue(type);
		setUrl(url);
	}

	/**
	 * Retrieves the value of the issue link.
	 *
	 * @return the value of the issue link
	 */
	@Override
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the issue link.
	 *
	 * @param value the value to be set
	 */
	@Override
	public void setValue(Object value) {
		this.value = value == null? null : value.toString();
	}

	/**
	 * Retrieves the URL of the issue link.
	 *
	 * @return the URL of the issue link
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL of the issue link.
	 *
	 * @param url the URL to be set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
