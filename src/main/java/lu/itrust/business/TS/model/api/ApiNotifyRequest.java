package lu.itrust.business.TS.model.api;

import java.util.List;

/**
 * Represents an API request which contains external notifications.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 *
 */
public class ApiNotifyRequest {
	/**
	 * Represents the scope of all the notifications that have been sent.
	 * Scopes are mainly used to identify the source of notifications
	 * and grant appropriate access rights. The notifications scope agrees
	 * with the scope of the generated dynamic parameters.
	 */
	private String scope;
	
	/**
	 * Represents the actual data of the request, namely the collection
	 * of external notifications.
	 */
	private List<ApiExternalNotification> data;

	/**
	 * Gets the scope of all notifications contained in this request.
	 */
	public String getScope() {
		return this.scope;
	}

	/**
	 * Sets the scope of all notifications contained in this request.
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Gets the list of notifications contained in this request.
	 */
	public List<ApiExternalNotification> getData() {
		return data;
	}

	/**
	 * Sets the list of notifications contained in this request.
	 */
	public void setData(List<ApiExternalNotification> data) {
		this.data = data;
	}

}
