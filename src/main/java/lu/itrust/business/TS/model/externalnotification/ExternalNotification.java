package lu.itrust.business.TS.model.externalnotification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lu.itrust.business.TS.constants.Constant;
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
	@Column(name = "idExternalNotification")
	private Integer id;

	/**
	 * Represents the type of the notification that has been sent.
	 */
	@Column(name = "dtCategory", nullable = false)
	private String category;

	/**
	 * Represents the name of the user who reported the notification.
	 * Used to identify the notification source.
	 */
	@Column(name = "dtSourceUserName", nullable = false)
	private String sourceUserName;
	
	/**
	 * Represents the time when the notification was originally issued
	 * by the external provider.
	 */
	@Column(name = "dtTimestamp", nullable = false)
	private long timestamp;

	/**
	 * Represents the time (in seconds) after which the notification has half the effect on the alarm level.
	 * Value is a positive integer.
	 */
	@Column(name = "dtHalfLife", nullable = false)
	private long halfLife;

	/**
	 * Specifies how strong the impact on the overall probability is.
	 * More precisely, an assertiveness of 1.0 will cause the overall probability to be withdrawn and replaced by the probability of this external notification.
	 * On the other hand, an assertiveness value of 0.0 will cause the overall probability to take this external notification into account
	 * Value is a floating-point number in the range [0.0, 1.0].
	 */
	@Column(name = "dtAssertiveness", nullable = false)
	private double assertiveness;

	/**
	 * Represents the number of notifications incorporated by this object.
	 * In fact, this parameter allows the aggregation of equivalent notifications
	 * into a single one, thus saving traffic/storage space.
	 * Value is a non-negative integer.
	 */
	@Column(name = "dtNumber", nullable = false)
	private int number;

	/**
	 * Represents the severity of the notification.
	 * The severity level is mapped to the "s0", "s1" ... parameters of an analysis.
	 * Values lie in the range [EXTERNAL_NOTIFICATION_MIN_SEVERITY, EXTERNAL_NOTIFICATION_MAX_SEVERITY].
	 */
	@Column(name = "dtSeverity", nullable = false)
	private int severity;

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
			throw new TrickException("error.externalnotification.category_null", "External notification category cannot be empty.");

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
			throw new TrickException("error.externalnotification.timestamp_negative", "External notification creation timestamp cannot be negative.");

		this.timestamp = timestamp;
	}

	/**
	 * Gets the half-life of the database entity.
	 */
	public long getHalfLife() {
		return halfLife;
	}

	/**
	 * Sets the half-life of the database entity.
	 */
	public void setHalfLife(long halfLife) throws TrickException {
		if (halfLife <= 0)
			throw new TrickException("error.externalnotification.halfLife_nonpositive", "External notification half-life cannot be negative or zero.");

		this.halfLife = halfLife;
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
			throw new TrickException("error.externalnotification.number_negative", "Number of external notifications cannot be negative.");
		this.number = number;
	}

	/**
	 * Gets the severity of this notification.
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * Sets the severity of this notification.
	 */
	public void setSeverity(int severity) throws TrickException {
		if (severity < Constant.EXTERNAL_NOTIFICATION_MIN_SEVERITY || severity > Constant.EXTERNAL_NOTIFICATION_MAX_SEVERITY)
			throw new TrickException("error.externalnotification.severity_out_of_range", "The notification severity must lie in [{0},{1}].", new Object[] { Constant.EXTERNAL_NOTIFICATION_MIN_SEVERITY, Constant.EXTERNAL_NOTIFICATION_MAX_SEVERITY });
		this.severity = severity;
	}

	/**
	 * Gets the assertiveness of this notification.
	 */
	public double getAssertiveness() {
		return assertiveness;
	}

	/**
	 * Sets the assertiveness of this notification.
	 */
	public void setAssertiveness(double assertiveness) throws TrickException {
		if (assertiveness < Constant.EXTERNAL_NOTIFICATION_MIN_SEVERITY || assertiveness > Constant.EXTERNAL_NOTIFICATION_MAX_SEVERITY)
			throw new TrickException("error.externalnotification.assertiveness_out_of_range", "The notification assertiveness must lie in [{0},{1}].", new Object[] { 0.0, 1.0 });
		this.assertiveness = assertiveness;
	}

	/**
	 * Gets the name of the reporting user.
	 */
	public String getSourceUserName() {
		return sourceUserName;
	}

	/**
	 * Sets the name of the reporting user.
	 */
	public void setSourceUserName(String sourceUserName) {
		this.sourceUserName = sourceUserName;
	}

}
