package lu.itrust.business.TS.model.api;

/**
 * Represents a notification sent by an external provider (e.g. an IDS).
 * This model is only used to describe notification objects passed to an API.
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
	 * The value is a UNIX timestamp (number of seconds elapsed since 1970-01-01).
	 */
	private long timestamp;
	
	/**
	 * Represents the time (in seconds) after which the notification has only half the effect on the alarm level.
	 */
	private long halfLife;
	
	/**
	 * Represents the severity of the notification.
	 * The severity is the conditional probability that an incident of this category occurs
	 * given that there has been an alert (a notification).
	 * Values lie in the range [0.0,infinity) where 0 is least severe.
	 */
	private double severity = 0;
	
	/**
	 * Represents the number of notifications incorporated by this object.
	 * In fact, this parameter allows the aggregation of equivalent notifications
	 * into a single one, thus saving traffic/storage space.
	 */
	private int number = 1;

	/**
	 * Gets the 'category' property of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getC() instead of getCategory(),
	 * we may use {"c":...} instead of {"category":...} in JSON data.
	 */
	public String getC() {
		return this.category;
	}

	/**
	 * Sets the 'category' property of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setC() instead of setCategory(),
	 * we may use {"c":...} instead of {"category":...} in JSON data.
	 */
	public void setC(String category) {
		this.category = category;
	}

	/**
	 * Gets the 'timestamp' property of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getT() instead of getTimestamp(),
	 * we may use {"t":...} instead of {"timestamp":...} in JSON data.
	 */
	public long getT() {
		return this.timestamp;
	}

	/**
	 * Sets the 'timestamp' property of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setT() instead of setTimestamp(),
	 * we may use {"t":...} instead of {"timestamp":...} in JSON data.
	 */
	public void setT(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets the 'halfLife' property of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getH() instead of getHalfLife(),
	 * we may use {"h":...} instead of {"halfLife":...} in JSON data.
	 */
	public long getH() {
		return this.halfLife;
	}

	/**
	 * Sets the 'halfLife' property of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setH() instead of setHalfLife(),
	 * we may use {"h":...} instead of {"halfLife":...} in JSON data.
	 */
	public void setH(long halfLife) {
		this.halfLife = halfLife;
	}

	/**
	 * Gets the 'severity' property of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getS() instead of getSeverity(),
	 * we may use {"s":...} instead of {"severity":...} in JSON data.
	 */
	public double getS() {
		return this.severity;
	}

	/**
	 * Sets the 'severity' property of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setS() instead of setSeverity(),
	 * we may use {"s":...} instead of {"severity":...} in JSON data.
	 */
	public void setS(double severity) {
		this.severity = severity;
	}

	/**
	 * Gets the 'number' property of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getN() instead of getNumber(),
	 * we may use {"n":...} instead of {"number":...} in JSON data.
	 */
	public int getN() {
		return this.number;
	}

	/**
	 * Sets the 'number' property of this notification.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setN() instead of setNumber(),
	 * we may use {"n":...} instead of {"number":...} in JSON data.
	 */
	public void setN(int number) {
		this.number = number;
	}
}
