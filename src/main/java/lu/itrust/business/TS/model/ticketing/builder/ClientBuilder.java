/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.builder;

import lu.itrust.business.TS.model.ticketing.builder.jira.JiraClient;
import lu.itrust.business.TS.model.ticketing.builder.redmine.RedmineClient;

/**
 * @author eomar
 *
 */
public class ClientBuilder {

	public static Client Build(String name) {
		switch (name) {
		case "jira":
			return new JiraClient();
		case "redmine":
			return new RedmineClient();
		default:
			return null;
		}
	}

	public static String TicketLink(String name, String baseUser, String ticketId) {
		switch (name) {
		case "jira":
			return String.format("%s/browse/%s", baseUser, ticketId);
		case "redmine":
			return String.format("%s/issues/%s", baseUser, ticketId);
		default:
			return null;
		}
	}
	
	public static String ProjectLink(String name, String baseUser, String projectId) {
		switch (name) {
		case "jira":
			return String.format("%s/browse/%s", baseUser, projectId);
		case "redmine":
			return String.format("%s/projects/%s", baseUser, projectId);
		default:
			return null;
		}
	}
	
}
