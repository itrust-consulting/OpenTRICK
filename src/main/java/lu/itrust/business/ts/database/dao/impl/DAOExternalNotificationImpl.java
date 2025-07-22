package lu.itrust.business.ts.database.dao.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.expressions.StringExpressionHelper;
import lu.itrust.business.ts.database.dao.DAOExternalNotification;
import lu.itrust.business.ts.model.externalnotification.ExternalNotification;
import lu.itrust.business.ts.model.externalnotification.helper.ExternalNotificationHelper;

/**
 * Represents an implementation of the DAOExternalNotification interface
 * for Spring Hibernate.
 * 
 * @author Steve Muller itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
@Repository
public class DAOExternalNotificationImpl extends DAOHibernate implements DAOExternalNotification {

	/**
	 * Initializes a new DAOExternalNotificationImpl instance.
	 */
	public DAOExternalNotificationImpl() {
	}

	/**
	 * Initializes a new DAOExternalNotificationImpl instance.
	 * 
	 * @param session The 'Hibernate' session passed to the 'DAOHibernate'
	 *                constructor.
	 */
	public DAOExternalNotificationImpl(Session session) {
		super(session);
	}

	/** {@inheritDoc} */
	@Override
	public ExternalNotification get(Integer id) {
		return getSession().get(ExternalNotification.class, id);
	}

	/** {@inheritDoc} */
	@Override
	public List<ExternalNotification> getAll() {
		return createQueryWithCache("From ExternalNotification", ExternalNotification.class).getResultList();
	}

	/** {@inheritDoc} */
	@Override
	public void save(ExternalNotification externalNotification) {
		getSession().persist(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void saveOrUpdate(ExternalNotification externalNotification) {
		getSession().saveOrUpdate(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void delete(ExternalNotification externalNotification) {
		getSession().remove(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName) {

		// Select the latest external notification for each category
		// (Take the external notifications. Join them to all external notifications
		// that occur later. Those who do not have a later external notification are the
		// latest ones. Fetch these.)
		final String query = ""
				+ "SELECT extnot "
				+ "FROM ExternalNotification extnot "
				+ "LEFT JOIN ExternalNotification extnotmax WITH extnotmax.sourceUserName=:sourceUserName AND extnotmax.timestamp <= :now AND extnotmax.category=extnot.category AND extnotmax.timestamp > extnot.timestamp "
				+ "WHERE extnot.sourceUserName = :sourceUserName AND extnot.timestamp <= :now AND extnotmax.id IS NULL";

		final Map<String, Double> probabilities = new HashMap<>();
		createQueryWithCache(query, ExternalNotification.class).setParameter("now", timestamp)
				.setParameter("sourceUserName", sourceUserName).getResultStream().forEach(notification -> {
					// Deduce parameter name from category
					final String parameterName = ExternalNotificationHelper.createParameterName(sourceUserName,
							notification.getCategory());

					// Compute probability level:
					// Pr[event] follows the exponential-decay model, i.e. Pr[event](t) = p0 * (1/2)
					// ^ ((t - t0)/T)
					// where p0 := initial probability = severity, t0 := timestamp of notification,
					// T := half-life.
					final double timeElapsed = timestamp - (double) notification.getTimestamp(); // we know this
																									// quantity to be
					// >= 0
					final double p = notification.getSeverity()
							* Math.pow(0.5, timeElapsed / notification.getHalfLife());
					// Store new computed probability
					probabilities.put(parameterName, p);
				});

		return probabilities;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd,
			String sourceUserName, double minimumProbability) {
		// Ignore all external notifications that are older than seven times their
		// half-life time, because their influence is (1/2)^7 < 1%,
		// which can be neglected if the severity is roughly the same for all
		// notifications of the same category (which we assume).
		String query = "FROM ExternalNotification extnot WHERE extnot.timestamp + extnot.halfLife*7.0 >= :begin AND extnot.timestamp <= :end AND extnot.sourceUserName = :sourceUserName ORDER BY extnot.timestamp DESC";

		// Our strategy looks as follows.
		// Take the whole time interval and all notifications issued during that period.
		// Chop the interval into smaller segments such that each segment starts with an
		// external notification.
		// On each such interval, the time-dependent probability function can be
		// explicitly inferred from that single external notification.
		// Then integrate the probability function over that interval.
		// Finally, sum all these partial integrals up to get the final probability.

		// Note that the notifications are ordered by time of occurrence in DESCENDING
		// order (i.e. starting with the most recent one!).

		final long totalInterval = timestampEnd - timestampBegin;
		final Map<String, Double> nextProbabilityLevel = new HashMap<>(); // initial probability level of the next more
																			// recent notification, per category
		final Map<String, Long> nextNotificationTime = new HashMap<>(); // store next more recent external notification,
																		// per category
		final Map<String, Double> totalProbability = new HashMap<>(); // accumulated probability, per category

		createQueryWithCache(query, ExternalNotification.class).setParameter("begin", timestampBegin)
				.setParameter("end", timestampEnd).setParameter("sourceUserName", sourceUserName).getResultStream()
				.forEach(notification -> {
					final String parameterName = ExternalNotificationHelper.createParameterName(sourceUserName,
							notification.getCategory());

					// Compute aggregated probability for this notification until the time where it
					// is no longer applicable

					// The probability level at any time t is given by severity*(1/2)^(t-t0)/T where
					// t0 is the time of reporting.
					// The integral of this expression is severity*T/log(1/2)*(1/2)^(t-t0)/T.
					// Compute the time deltas (t-t0) respectively for the beginning and end of the
					// applicable interval.
					final double deltaTimeBegin = Math.max(timestampBegin, notification.getTimestamp())
							- (double) notification.getTimestamp();
					final double deltaTimeEnd = nextNotificationTime.getOrDefault(parameterName, timestampEnd)
							- (double) notification.getTimestamp();

					// Compute integrated probability
					final double integratedProbability = notification.getSeverity() * notification.getHalfLife()
							/ Math.log(0.5) * (Math.pow(0.5, deltaTimeEnd / notification.getHalfLife())
									- Math.pow(0.5, deltaTimeBegin / notification.getHalfLife()));
					totalProbability.put(parameterName,
							totalProbability.getOrDefault(parameterName, minimumProbability)
									+ integratedProbability / totalInterval);

					// Prepare next iteration
					nextProbabilityLevel.put(parameterName, notification.getSeverity());
					nextNotificationTime.put(parameterName, Math.max(timestampBegin, notification.getTimestamp()));
				});

		return totalProbability;
	}

	/** {@inheritDoc} */
	@Override
	public Double computeProbabilityAtTime(long timestamp, String prefix, String category) {
		// Select the latest external notification for each category
		// (Take the external notifications. Join them to all external notifications
		// that occur later. Those who do not have a later external notification are the
		// latest ones. Fetch these.)

		final String query = ""
				+ "SELECT extnot "
				+ "FROM ExternalNotification extnot "
				+ "LEFT JOIN ExternalNotification extnotmax WITH extnotmax.sourceUserName=:sourceUserName AND extnotmax.timestamp <= :now AND extnotmax.category=extnot.category AND extnotmax.timestamp > extnot.timestamp "
				+ "WHERE extnot.sourceUserName = :sourceUserName and extnot.category = :category AND extnot.timestamp <= :now AND extnotmax.id IS NULL";

		final double[] probability = { 0.0 };
		createQueryWithCache(query, ExternalNotification.class).setParameter("now", timestamp)
				.setParameter("sourceUserName", prefix).setParameter("category",category).getResultStream()
				.forEach(notification -> {

					// Compute probability level:
					// Pr[event] follows the exponential-decay model, i.e. Pr[event](t) = p0 * (1/2)
					// ^ ((t - t0)/T)
					// where p0 := initial probability = severity, t0 := timestamp of notification,
					// T := half-life.
					final double timeElapsed = timestamp - (double) notification.getTimestamp(); // we know this
																									// quantity to be
					// >= 0
					probability[0] = notification.getSeverity()
							* Math.pow(0.5, timeElapsed / notification.getHalfLife());
					// Store new computed probability

				});

		return probability[0];
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName,
			List<String> categories) {
		// Select the latest external notification for each category
		// (Take the external notifications. Join them to all external notifications
		// that occur later. Those who do not have a later external notification are the
		// latest ones. Fetch these.)
		final String query = ""
				+ "SELECT extnot "
				+ "FROM ExternalNotification extnot "
				+ "LEFT JOIN ExternalNotification extnotmax WITH extnotmax.sourceUserName=:sourceUserName AND extnotmax.timestamp <= :now AND extnotmax.category=extnot.category AND extnotmax.timestamp > extnot.timestamp "
				+ "WHERE extnot.sourceUserName = :sourceUserName AND extnot.category in :categories AND extnot.timestamp <= :now AND extnotmax.id IS NULL ORDER BY extnot.category ASC";

		Map<String, Double> probabilities = new LinkedHashMap<>();
		createQueryWithCache(query, ExternalNotification.class).setParameter("now", timestamp)
				.setParameter("sourceUserName", sourceUserName).setParameter("categories", categories).getResultStream()
				.forEach(notification -> {
					// Deduce parameter name from category
					final String parameterName = ExternalNotificationHelper.createParameterName(sourceUserName,
							notification.getCategory());

					// Compute probability level:
					// Pr[event] follows the exponential-decay model, i.e. Pr[event](t) = p0 * (1/2)
					// ^ ((t - t0)/T)
					// where p0 := initial probability = severity, t0 := timestamp of notification,
					// T := half-life.
					final double timeElapsed = timestamp - (double) notification.getTimestamp(); // we know this
																									// quantity to be
					// >= 0
					final double p = notification.getSeverity()
							* Math.pow(0.5, timeElapsed / notification.getHalfLife());
					// Store new computed probability
					probabilities.put(parameterName, p);
				});

		return probabilities;
	}

	@Override
	public Double findLastSeverity(String prefix, String category) {
		// Select the latest external notification for each category
		// (Take the external notifications. Join them to all external notifications
		// that occur later. Those who do not have a later external notification are the
		// latest ones. Fetch these.)


		final String query = ""
				+ "SELECT extnot "
				+ "FROM ExternalNotification extnot "
				+ "LEFT JOIN ExternalNotification extnotmax WITH extnotmax.sourceUserName=:sourceUserName AND extnotmax.timestamp <= :now AND extnotmax.category=extnot.category AND extnotmax.timestamp > extnot.timestamp "
				+ "WHERE extnot.sourceUserName = :sourceUserName and extnot.category = :category AND extnot.timestamp <= :now AND extnotmax.id IS NULL";

		final double[] probability = { 0.0 };
		createQueryWithCache(query, ExternalNotification.class).setParameter("now", System.currentTimeMillis())
				.setParameter("sourceUserName", prefix).setParameter("category", category).getResultStream()
				.forEach(notification -> {
					probability[0] = notification.getSeverity();
				});

		return probability[0];
	}

	@Override
	public Map<String, Double> findLastSeverities(String sourceUserName, List<String> categories) {
		// Select the latest external notification for each category
		// (Take the external notifications. Join them to all external notifications
		// that occur later. Those who do not have a later external notification are the
		// latest ones. Fetch these.)
		final String query = ""
				+ "SELECT extnot "
				+ "FROM ExternalNotification extnot "
				+ "LEFT JOIN ExternalNotification extnotmax WITH extnotmax.sourceUserName=:sourceUserName AND extnotmax.timestamp <= :now AND extnotmax.category=extnot.category AND extnotmax.timestamp > extnot.timestamp "
				+ "WHERE extnot.sourceUserName = :sourceUserName AND extnot.category in :categories AND extnot.timestamp <= :now AND extnotmax.id IS NULL ORDER BY extnot.category ASC";

		Map<String, Double> probabilities = new LinkedHashMap<>();
		createQueryWithCache(query, ExternalNotification.class).setParameter("now", System.currentTimeMillis())
				.setParameter("sourceUserName", sourceUserName).setParameter("categories", categories).getResultStream()
				.forEach(notification -> {
					// Deduce parameter name from category
					final String parameterName = ExternalNotificationHelper.createParameterName(sourceUserName,
							notification.getCategory());
					// Store new computed probability
					probabilities.put(parameterName, notification.getSeverity());
				});

		return probabilities;
	}

	@Override
	public String[] extractPrefixAndCategory(String parameter, List<String> idsNames) {
		for (var prefix : idsNames) {
			if (parameter.startsWith(StringExpressionHelper.makeValidVariable(prefix)) && parameter.length() > (prefix.length() + 1)) {
				var categorie = parameter.substring(prefix.length() + 1);
				if (exists(prefix, categorie))
					return new String[] { prefix, categorie };

			}

		}
		return new String[] {};
	}

	@Override
	public boolean exists(String prefix, String category) {
		return createQueryWithCache(
				"Select count(*)>0 From ExternalNotification where sourceUserName = :prefix and category = :category",
				Boolean.class).setParameter("prefix", prefix).setParameter("category", category).uniqueResultOptional()
				.orElse(false);
	}
}