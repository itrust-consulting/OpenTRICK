package lu.itrust.business.ts.model.externalnotification;

/**
 * Enumerates different types of external notifications.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Aug 19, 2015
 */
public enum ExternalNotificationType {
	//
	// NB: we use @Enumerated(Enumtype.ORDINAL) elsewhere, so the order of the enum members is important!
	// Do not remove entries, and place new entries to the very end of the enumeration!
	//

	/**
	 * Invalid type.
	 * Value: 0
	 */
	INVALID,

	/**
	 * The external notification increases the current severity level, all by taking the latter into account.
	 * Value: 1
	 */
	RELATIVE,

	/**
	 * The external notification fixes the severity level to the severity of the external notification, discarding all history.
	 * Value: 2
	 */
	ABSOLUTE,
}
