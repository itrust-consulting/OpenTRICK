package lu.itrust.business.TS.model.externalnotification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lu.itrust.business.TS.exception.TrickException;

/**
 * Represents a notification sent by an external provider (e.g. an IDS).
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @date Jun 8, 2015
 */
@Entity
public class ExternalNotification {
	/**
	 * Represents the type of the notification that has been sent.
	 */
	@Id
	@GeneratedValue
	@Column(name = "idExternalNotification", nullable = false)
	private Integer id;

	/**
	 * Represents the type of the notification that has been sent.
	 */
	@Column(name = "dtCategory", nullable = false)
	private String category;
	
	/**
	 * Represents the time when the notification was originally issued
	 * by the external provider.
	 */
	@Column(name = "dtTimestamp", nullable = false)
	private long timestamp;
	
	/**
	 * Represents the number of notifications incorporated by this object.
	 * In fact, this parameter allows the aggregation of equivalent notifications
	 * into a single one, thus saving traffic/storage space.
	 */
	@Column(name = "dtNumber", nullable = false)
	private int number;

	public Integer getId() {
		return id;
	}

	/* Changing id is not supported: public void setId(Integer id); */

	/**
	 * Gets the notification category from the database entity.
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the notification category of the database entity.
	 */
	public void setCategory(String category) throws TrickException {
		if (category == null || category.trim().isEmpty())
			throw new TrickException("error.externalnotification.category_null", "External notification category cannot be empty");

		this.category = category;
	}

	/**
	 * Gets the notification creation timestamp from the database entity.
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the notification creation timestamp of the database entity.
	 */
	public void setTimestamp(long timestamp) throws TrickException {
		if (timestamp < 0)
			throw new TrickException("error.externalnotification.timestamp_negative", "External notification creation timestamp cannot be negative");

		this.timestamp = timestamp;
	}

	/**
	 * Gets the number of notification this (virtual) notification represents.
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the number of notification this (virtual) notification represents.
	 */
	public void setNumber(int number) throws TrickException {
		if (number < 0)
			throw new TrickException("error.externalnotification.number_negative", "Number of external notifications cannot be negative");
		this.number = number;
	}
	
}
