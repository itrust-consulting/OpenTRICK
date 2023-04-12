package lu.itrust.business.ts.model.api;

import java.util.List;

/**
 * Represents an API request which contains external notifications.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 *
 */
public class ApiNotifyRequest {
	/**
	 * Represents the actual data of the request, namely the collection
	 * of external notifications.
	 */
	private List<ApiExternalNotification> data;

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
