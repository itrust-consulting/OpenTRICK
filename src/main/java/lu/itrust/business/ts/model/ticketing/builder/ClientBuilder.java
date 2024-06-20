/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.builder;

import lu.itrust.business.ts.model.ticketing.builder.jira.JiraClient;
import lu.itrust.business.ts.model.ticketing.builder.redmine.RedmineClient;

/**
 * The ClientBuilder class provides static methods for building clients and generating ticket and project links.
 */
public class ClientBuilder {

	/**
	 * Builds a client based on the given name.
	 *
	 * @param name The name of the client.
	 * @return The built client object.
	 */
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

	/**
	 * Generates a ticket link based on the given client name, base URL, and ticket ID.
	 *
	 * @param name     The name of the client.
	 * @param baseURL  The base URL of the client.
	 * @param ticketId The ID of the ticket.
	 * @return The generated ticket link.
	 */
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

	/**
	 * Generates a project link based on the given client name, base URL, and project ID.
	 *
	 * @param name       The name of the client.
	 * @param baseURL    The base URL of the client.
	 * @param projectId  The ID of the project.
	 * @return The generated project link.
	 */
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

	/**
	 * Returns the URL with trailing slashes removed.
	 *
	 * @param url The URL to process.
	 * @return The processed URL.
	 */
	public static final String getURL(String url) {
		final String tmp = url == null ? "" : url.trim();
		return tmp + (tmp.endsWith("/") ? "" : "/");
	}
}
