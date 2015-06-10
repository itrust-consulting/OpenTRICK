package lu.itrust.business.TS.model.externalnotification;

/**
 * Represents an aggregated set of external notifications.
 * Useful in combination with summing over database entries.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
public class ExternalNotificationOccurrence {
	/**
	 * The unique category of all external notifications this instance represents.
	 */
	private String category;
	
	/**
	 * The total number of external notifications represented by this instance.
	 */
	private long occurrence;

	/**
	 * Gets the unique category of all external notifications this instance represents.
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the unique category of all external notifications this instance represents.
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Gets total number of external notifications represented by this instance.
	 */
	public long getOccurrence() {
		return this.occurrence;
	}

	/**
	 * Sets total number of external notifications represented by this instance.
	 */
	public void setOccurrence(long occurrence) {
		this.occurrence = occurrence;
	}
}
