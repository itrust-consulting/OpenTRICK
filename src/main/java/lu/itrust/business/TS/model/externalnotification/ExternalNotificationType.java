package lu.itrust.business.TS.model.externalnotification;

/**
 * Enumerates different types of external notifications.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Aug 19, 2015
 */
public enum ExternalNotificationType {
	/** The external notification increases the current severity level, all by taking the latter into account. */
	RELATIVE,
	/** The external notification fixes the severity level to the severity of the external notification, discarding all history. */
	ABSOLUTE,
}
