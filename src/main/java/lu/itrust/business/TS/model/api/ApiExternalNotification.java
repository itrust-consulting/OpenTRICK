package lu.itrust.business.TS.model.api;


/**
 * Represents a notification sent by an external provider (e.g. an IDS).
 * This model is only used to describe notification objects passed to/returned by an API.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 5, 2015
 */
public class ApiExternalNotification {
	/**
	 * Represents the type of the notification that has been sent.
	 */
	private String category;
	
	/**
	 * Represents the time when the notification was originally issued
	 * by the external provider.
	 */
	private long timestamp;
	
	/**
	 * Represents the number of notifications incorporated by this object.
	 * In fact, this parameter allows the aggregation of equivalent notifications
	 * into a single one, thus saving traffic/storage space.
	 */
	private int number;

	/**
	 * Gets the 'category' parameter of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getC() instead of getCategory(),
	 * we may use {"c":...} instead of {"category":...} in JSON data.
	 */
	public String getC() {
		return this.category;
	}

	/**
	 * Sets the 'category' parameter of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setC() instead of setCategory(),
	 * we may use {"c":...} instead of {"category":...} in JSON data.
	 */
	public void setC(String category) {
		this.category = category;
	}

	/**
	 * Gets the 'timestamp' parameter of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getT() instead of getTimestamp(),
	 * we may use {"t":...} instead of {"timestamp":...} in JSON data.
	 */
	public long getT() {
		return this.timestamp;
	}

	/**
	 * Sets the 'timestamp' parameter of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setT() instead of setTimestamp(),
	 * we may use {"t":...} instead of {"timestamp":...} in JSON data.
	 */
	public void setT(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets the 'number' parameter of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getN() instead of getNumber(),
	 * we may use {"n":...} instead of {"number":...} in JSON data.
	 */
	public int getN() {
		return this.number;
	}

	/**
	 * Sets the 'number' parameter of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setN() instead of setNumber(),
	 * we may use {"n":...} instead of {"number":...} in JSON data.
	 */
	public void setN(int number) {
		this.number = number;
	}
}
