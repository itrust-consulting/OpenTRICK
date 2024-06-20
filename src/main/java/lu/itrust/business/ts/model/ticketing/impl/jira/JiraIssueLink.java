/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl.jira;

import lu.itrust.business.ts.model.ticketing.impl.AbstractField;


/**
 * Represents a Jira issue link.
 */
public class JiraIssueLink extends AbstractField {

	private String link;

	/**
	 * Default constructor.
	 */
	public JiraIssueLink() {
	}

	/**
	 * Constructs a JiraIssueLink object with the specified id, name, and link.
	 *
	 * @param id   the id of the issue link
	 * @param name the name of the issue link
	 * @param link the link associated with the issue
	 */
	public JiraIssueLink(String id, String name, String link) {
		super(id, name);
		setValue(link);
	}

	/**
	 * Returns the value of the issue link.
	 *
	 * @return the link associated with the issue
	 */
	@Override
	public String getValue() {
		return getLink();
	}

	/**
	 * Sets the value of the issue link.
	 *
	 * @param value the link associated with the issue
	 */
	@Override
	public void setValue(Object value) {
		setLink((String) value);
	}

	/**
	 * Returns the link associated with the issue.
	 *
	 * @return the link associated with the issue
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Sets the link associated with the issue.
	 *
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

}
