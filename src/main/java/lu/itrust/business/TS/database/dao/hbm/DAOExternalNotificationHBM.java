package lu.itrust.business.TS.database.dao.hbm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;

/**
 * Represents an implementation of the DAOExternalNotification interface
 * for Spring Hibernate.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
@Repository
public class DAOExternalNotificationHBM extends DAOHibernate implements DAOExternalNotification {

	/**
	 * Initializes a new DAOExternalNotificationHBM instance.
	 */
	public DAOExternalNotificationHBM() {
	}

	/**
	 * Initializes a new DAOExternalNotificationHBM instance.
	 * @param session The 'Hibernate' session passed to the 'DAOHibernate' constructor.
	 */
	public DAOExternalNotificationHBM(Session session) {
		super(session);
	}

	/** {@inheritDoc} */
	@Override
	public ExternalNotification get(Integer id){
		return (ExternalNotification) getSession().get(ExternalNotification.class, id);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExternalNotification> getAll(){
		return (List<ExternalNotification>) getSession().createQuery("From ExternalNotification").getResultList();
	}

	/** {@inheritDoc} */
	@Override
	public void save(ExternalNotification externalNotification){
		getSession().save(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void saveOrUpdate(ExternalNotification externalNotification){
		getSession().saveOrUpdate(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void delete(ExternalNotification externalNotification){
		getSession().delete(externalNotification);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName, double minimumProbability){

		// Select the latest external notification for each category
		// (Take the external notifications. Join them to all external notifications that occur later. Those who do not have a later external notification are the latest ones. Fetch these.)
		final String query = ""
				+ "SELECT extnot "
				+ "FROM ExternalNotification extnot "
				+ "LEFT JOIN ExternalNotification extnotmax WITH extnotmax.sourceUserName=:sourceUserName AND extnotmax.timestamp <= :now AND extnotmax.category=extnot.category AND extnotmax.timestamp > extnot.timestamp "
				+ "WHERE extnot.sourceUserName = :sourceUserName AND extnot.timestamp <= :now AND extnotmax.id IS NULL";

		Map<String, Double> probabilities = new HashMap<>();
		getSession().createQuery(query).setParameter("now", timestamp).setParameter("sourceUserName", sourceUserName).getResultStream().forEach(notificationObject -> {
			ExternalNotification notification = (ExternalNotification) notificationObject;
			// Deduce parameter name from category
			final String parameterName = ExternalNotificationHelper.createParameterName(sourceUserName, notification.getCategory());

			// Compute probability level:
			// Pr[event] follows the exponential-decay model, i.e. Pr[event](t) = p0 * (1/2) ^ ((t - t0)/T)
			// where p0 := initial probability = severity, t0 := timestamp of notification, T := half-life.
			final double timeElapsed = timestamp - notification.getTimestamp(); // we know this quantity to be >= 0
			final double p = notification.getSeverity() * Math.pow(0.5, timeElapsed / notification.getHalfLife());

			// Store new computed probability
			probabilities.put(parameterName, p);
		});

		return probabilities;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd, String sourceUserName, double minimumProbability){
		// Ignore all external notifications that are older than seven times their half-life time, because their influence is (1/2)^7 < 1%,
		// which can be neglected if the severity is roughly the same for all notifications of the same category (which we assume).
		String query = "FROM ExternalNotification extnot WHERE extnot.timestamp + extnot.halfLife*7 >= :begin AND extnot.timestamp <= :end AND extnot.sourceUserName = :sourceUserName ORDER BY extnot.timestamp DESC";

		// Our strategy looks as follows.
		// Take the whole time interval and all notifications issued during that period.
		// Chop the interval into smaller segments such that each segment starts with an external notification.
		// On each such interval, the time-dependent probability function can be explicitly inferred from that single external notification.
		// Then integrate the probability function over that interval.
		// Finally, sum all these partial integrals up to get the final probability.

		// Note that the notifications are ordered by time of occurrence in DESCENDING order (i.e. starting with the most recent one!).

		final long totalInterval = timestampEnd - timestampBegin;
		final Map<String, Double> nextProbabilityLevel = new HashMap<>(); // initial probability level of the next more recent notification, per category
		final Map<String, Long> nextNotificationTime = new HashMap<>(); // store next more recent external notification, per category
		final Map<String, Double> totalProbability = new HashMap<>(); // accumulated probability, per category

		getSession().createQuery(query).setParameter("begin", timestampBegin).setParameter("end", timestampEnd).setParameter("sourceUserName", sourceUserName).getResultStream().forEach(notificationObject -> {
			final ExternalNotification notification = (ExternalNotification)notificationObject;
			final String parameterName = ExternalNotificationHelper.createParameterName(sourceUserName, notification.getCategory());

			// Compute aggregated probability for this notification until the time where it is no longer applicable

			// The probability level at any time t is given by severity*(1/2)^(t-t0)/T where t0 is the time of reporting.
			// The integral of this expression is severity*T/log(1/2)*(1/2)^(t-t0)/T.
			// Compute the time deltas (t-t0) respectively for the beginning and end of the applicable interval.
			final double deltaTimeBegin = Math.max(timestampBegin, notification.getTimestamp()) - notification.getTimestamp();
			final double deltaTimeEnd = nextNotificationTime.getOrDefault(parameterName, timestampEnd) - notification.getTimestamp();

			// Compute integrated probability
			final double integratedProbability = notification.getSeverity() * notification.getHalfLife() / Math.log(0.5) * (Math.pow(0.5, deltaTimeEnd / notification.getHalfLife()) - Math.pow(0.5, deltaTimeBegin / notification.getHalfLife()));
			totalProbability.put(parameterName, totalProbability.getOrDefault(parameterName, 0.0) + integratedProbability / totalInterval);

			// Prepare next iteration
			nextProbabilityLevel.put(parameterName, notification.getSeverity());
			nextNotificationTime.put(parameterName, Math.max(timestampBegin, notification.getTimestamp()));
		});

		return totalProbability;
	}
}