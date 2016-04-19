/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.builder;

import lu.itrust.business.TS.model.ticketing.builder.jira.JiraClient;

/**
 * @author eomar
 *
 */
public class ClientBuilder {
	
	public static Client build(String name){
		switch (name) {
		case "jira":
			return  new JiraClient();
		default:
			return null;
		}
	}
}
