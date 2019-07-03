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

	public static String TicketLink(String name, String baseURL, String ticketId) {
		switch (name) {
		case "jira":
			return String.format("%sbrowse/%s", getURL(baseURL), ticketId);
		case "redmine":
			return String.format("%sissues/%s", getURL(baseURL), ticketId);
		default:
			return null;
		}
	}

	public static String ProjectLink(String name, String baseURL, String projectId) {
		switch (name) {
		case "jira":
			return String.format("%sbrowse/%s", getURL(baseURL), projectId);
		case "redmine":
			return String.format("%sprojects/%s", getURL(baseURL), projectId);
		default:
			return null;
		}
	}

	public static final String getURL(String url) {
		final String tmp = url == null ? "" : url.trim();
		return tmp + (tmp.endsWith("/") ? "" : "/");
	}

}
