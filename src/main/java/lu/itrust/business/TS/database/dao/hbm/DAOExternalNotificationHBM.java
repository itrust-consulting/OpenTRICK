package lu.itrust.business.TS.database.dao.hbm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.database.dao.DAOExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotificationType;
import lu.itrust.business.expressions.StringExpressionHelper;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

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
	public ExternalNotification get(Integer id) throws Exception {
		return (ExternalNotification) getSession().get(ExternalNotification.class, id);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExternalNotification> getAll() throws Exception {
		return (List<ExternalNotification>) getSession().createQuery("From ExternalNotification").list();
	}

	/** {@inheritDoc} */
	@Override
	public void save(ExternalNotification externalNotification) throws Exception {
		getSession().save(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void saveOrUpdate(ExternalNotification externalNotification) throws Exception {
		getSession().saveOrUpdate(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void delete(ExternalNotification externalNotification) throws Exception {
		getSession().delete(externalNotification);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName, double minimumProbability) throws Exception {
		// Note that the probability decays exponentially with time. In particular, it never becomes zero.
		// However, for performance reasons, we start neglecting it once it reaches 1/128 (which is < 1%). It does so after 7 times the half life.  
		String query = "FROM ExternalNotification extnot WHERE extnot.timestamp <= :now AND extnot.timestamp + extnot.halfLife*7 > :now AND extnot.sourceUserName = :sourceUserName ORDER BY extnot.timestamp ASC";
		Iterator<ExternalNotification> iterator = getSession().createQuery(query).setParameter("now", timestamp).setParameter("sourceUserName", sourceUserName).iterate();

		// TODO: An analysis of the forward-error is needed here!
		// TODO: Do this in HQL for a huge performance increase. Hint: the PRODUCT aggregation function can be simulated by EXP(SUM(LOG(...))).
		// The reason why I left it like this is because I plan to also support "setting" a probability
		// level, ignoring the past probability level. Unfortunately this cannot be achieved using a SUM
		// and thus the HQL approach seems infeasible (need to figure out a solution to this!). -- smuller, 07.08.2015
		
		// Compute probability for each category using the formula:
		//    totalProb := 1 - (1 - inverseAssertiveness * totalProb) * (1 - Pr[event])
		// for each event, starting at totalProb := 0.
		// Note that Pr[event] follows the exponential-decay model, i.e. Pr[event](t) = p0 * (1/2) ^ ((t - t0)/T)
		// where p0 := initial probability = severity, t0 := timestamp of notification, T := half-life. 
		
		Map<String, Double> probabilities = new HashMap<>();
		while (iterator.hasNext()) {
			ExternalNotification extNot = iterator.next();
			
			// Deduce parameter name from category
			String parameterName = StringExpressionHelper.makeValidVariable(String.format("%s_%s", sourceUserName, extNot.getCategory()));

			final double timeElapsed = timestamp - extNot.getTimestamp(); // we know this quantity to be >= 0
			final double p = extNot.getSeverity() * Math.pow(0.5, timeElapsed / extNot.getHalfLife());
			final double inverseAssertiveness = extNot.getType().equals(ExternalNotificationType.ABSOLUTE) ? 0.0 : 1.0;

			double totalProbability = probabilities.getOrDefault(parameterName, 0.0);
			totalProbability = 1.0 - (1.0 - inverseAssertiveness * totalProbability) * Math.pow(1.0 - p, extNot.getNumber());
			totalProbability = Math.max(minimumProbability, totalProbability);
			probabilities.put(parameterName, totalProbability);
		}

		return probabilities;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd, String sourceUserName, double minimumProbability) throws Exception {
		String query = "FROM ExternalNotification extnot WHERE extnot.timestamp + extnot.halfLife*7 >= :begin AND extnot.timestamp <= :end AND extnot.sourceUserName = :sourceUserName ORDER BY extnot.category, extnot.timestamp ASC";
		Iterator<ExternalNotification> iterator = getSession().createQuery(query).setParameter("begin", timestampBegin).setParameter("end", timestampEnd).setParameter("sourceUserName", sourceUserName).iterate();

		// NOTA BENE: in fact one would have to integrate the curves; we take a more simple approach by just computing the values
		// at the critical spots (discontinuities due to new notifications being reported) and assume that the level remains constant
		// until the next notification or until the notification effect reaches <1% (which happens at roughly 7 times the half-life).
		// 
		// The results are not exact, but reflect somehow the history. Mathematically speaking, we compute a discrete version of the lower Darboux sum here.

		final long totalInterval = timestampEnd - timestampBegin;
		// NB: notifications are ordered by time of occurrence
		final Map<String, Double> probabilityLevel = new HashMap<>(); // last probability level per category
		final Map<String, Double> totalProbability = new HashMap<>(); // accumulated probability per category
		final Map<String, ExternalNotification> lastNotification = new HashMap<>(); // store last external notification per category

		while (iterator.hasNext()) {
			final ExternalNotification next = iterator.next();
			final String parameterName = StringExpressionHelper.makeValidVariable(String.format("%s_%s", sourceUserName, next.getCategory()));
			final ExternalNotification last = lastNotification.put(parameterName, next);
			if (last == null) continue;

			// Get last known probability settings
			final double lastProbabilityLevel = probabilityLevel.getOrDefault(parameterName, 0.0);
			final double lastTotalProbability = totalProbability.getOrDefault(parameterName, 0.0);

			// Compute the new probability settings
			final double inverseAssertiveness = last.getType().equals(ExternalNotificationType.ABSOLUTE) ? 0.0 : 1.0;
			final long deltaTimeBegin = Math.max(timestampBegin, last.getTimestamp()) - last.getTimestamp();
			final long deltaTimeEnd = next.getTimestamp() - last.getTimestamp();
			final double intervalWeight = Math.max(0, next.getTimestamp() - Math.max(timestampBegin, last.getTimestamp())) / (double)totalInterval;
			final double probabilityLevelBegin = 1.0 - (1.0 - inverseAssertiveness * lastProbabilityLevel) * (1.0 - last.getSeverity() * Math.pow(0.5, deltaTimeBegin / last.getHalfLife()));
			final double probabilityLevelEnd   = 1.0 - (1.0 - inverseAssertiveness * lastProbabilityLevel) * (1.0 - last.getSeverity() * Math.pow(0.5, deltaTimeEnd   / last.getHalfLife()));
			final double totalProbabilityDiff = (probabilityLevelEnd + probabilityLevelBegin) / 2.0 * intervalWeight;
			probabilityLevel.put(parameterName, probabilityLevelEnd);
			totalProbability.put(parameterName, lastTotalProbability + totalProbabilityDiff);
		}
		
		// while loop is always one entry behind, do not forget to add it to the total probability
		for (String parameterName : lastNotification.keySet()) {
			final ExternalNotification last = lastNotification.get(parameterName);
			
			// Get last known probability settings
			final double lastProbabilityLevel = probabilityLevel.getOrDefault(parameterName, 0.0);
			final double lastTotalProbability = totalProbability.getOrDefault(parameterName, 0.0);

			// Compute the new probability settings
			final double inverseAssertiveness = last.getType().equals(ExternalNotificationType.ABSOLUTE) ? 0.0 : 1.0;
			final long deltaTimeBegin = Math.max(timestampBegin, last.getTimestamp()) - last.getTimestamp();
			final long deltaTimeEnd = timestampEnd - last.getTimestamp();
			final long intervalWeight = Math.max(0, timestampEnd - Math.max(timestampBegin, last.getTimestamp())) / totalInterval;
			double probabilityLevelBegin = 1.0 - (1.0 - inverseAssertiveness * lastProbabilityLevel) * (1.0 - last.getSeverity() * Math.pow(0.5, deltaTimeBegin / last.getHalfLife()));
			double probabilityLevelEnd   = 1.0 - (1.0 - inverseAssertiveness * lastProbabilityLevel) * (1.0 - last.getSeverity() * Math.pow(0.5, deltaTimeEnd   / last.getHalfLife()));
			probabilityLevelBegin = Math.max(minimumProbability, probabilityLevelBegin);
			probabilityLevelEnd = Math.max(minimumProbability, probabilityLevelEnd);
			final double totalProbabilityDiff = (probabilityLevelEnd + probabilityLevelBegin) / 2.0 * intervalWeight;
			probabilityLevel.put(parameterName, probabilityLevelEnd);
			totalProbability.put(parameterName, lastTotalProbability + totalProbabilityDiff);
		}
		
		return totalProbability;
		//*/
	}
}