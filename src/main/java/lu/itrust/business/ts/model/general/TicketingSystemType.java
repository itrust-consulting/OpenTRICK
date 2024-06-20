package lu.itrust.business.ts.model.general;

/**
 * Represents the types of ticketing systems.
 */
public enum TicketingSystemType {
	REDMINE(false), JIRA(false), EMAIL(true)/* , CSV(true)*/;

	private boolean noClient = true;

	/**
	 * Checks if the ticketing system requires a client.
	 *
	 * @return true if the ticketing system requires a client, false otherwise
	 */
	public boolean isNoClient() {
		return noClient;
	}

	/**
	 * Sets the value indicating whether the client is present or not.
	 *
	 * @param noClient true if the client is not present, false otherwise
	 */
	private void setNoClient(boolean noClient) {
		this.noClient = noClient;
	}

	/**
	 * Represents a type of ticketing system.
	 */
	private TicketingSystemType(boolean noClient) {
		setNoClient(noClient);
	}
}
