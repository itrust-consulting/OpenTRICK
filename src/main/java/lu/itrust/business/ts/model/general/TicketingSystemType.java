package lu.itrust.business.ts.model.general;

public enum TicketingSystemType {
	REDMINE(false), JIRA(false), EMAIL(true)/* , CSV(true)*/;

	private boolean noClient = true;

	public boolean isNoClient() {
		return noClient;
	}

	private void setNoClient(boolean noClient) {
		this.noClient = noClient;
	}

	private TicketingSystemType(boolean noClient) {
		setNoClient(noClient);
	}
}
