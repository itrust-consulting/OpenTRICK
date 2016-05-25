package lu.itrust.business.TS.model.api;

/**
 * Represents a request to set the value of a dynamic parameter sent by an external provider (e.g. an IDS).
 * This model is only used to describe objects passed to an API.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Aug 21, 2015
 */
public class ApiParameterSetter {
	/**
	 * Represents the type of the request that has been sent.
	 */
	private String category;

	/**
	 * Represents the time when the parameter should have been set-
	 * The value is a UNIX timestamp (number of seconds elapsed since 1970-01-01).
	 */
	private long timestamp;
	
	/**
	 * Represents the value of the parameter to set.
	 * Values lie in the range [0.0,1.0].
	 */
	private double value = 0;

	/**
	 * Gets the 'category' property of this object.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getC() instead of getCategory(),
	 * we may use {"c":...} instead of {"category":...} in JSON data.
	 */
	public String getC() {
		return this.category;
	}

	/**
	 * Sets the 'category' property of this object.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setC() instead of setCategory(),
	 * we may use {"c":...} instead of {"category":...} in JSON data.
	 */
	public void setC(String category) {
		this.category = category;
	}

	/**
	 * Gets the 'timestamp' property of this object.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getT() instead of getTimestamp(),
	 * we may use {"t":...} instead of {"timestamp":...} in JSON data.
	 */
	public long getT() {
		return this.timestamp;
	}

	/**
	 * Sets the 'timestamp' property of this object.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setT() instead of setTimestamp(),
	 * we may use {"t":...} instead of {"timestamp":...} in JSON data.
	 */
	public void setT(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets the 'value' property of this object.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it getV() instead of getValue(),
	 * we may use {"v":...} instead of {"value":...} in JSON data.
	 */
	public double getV() {
		return this.value;
	}

	/**
	 * Sets the 'value' property of this object.
	 * NB: Jackson/Spring uses this method to map the value of this property
	 * to the JSON data field. By calling it setV() instead of setValue(),
	 * we may use {"v":...} instead of {"value":...} in JSON data.
	 */
	public void setV(double severity) {
		this.value = severity;
	}

}
