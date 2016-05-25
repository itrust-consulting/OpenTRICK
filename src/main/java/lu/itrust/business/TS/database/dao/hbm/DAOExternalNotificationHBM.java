package lu.itrust.business.TS.database.dao.hbm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.database.dao.DAOExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotificationType;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;

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
			final String parameterName = ExternalNotificationHelper.createParameterName(sourceUserName, extNot.getCategory());

			// Get parameters resulting from previous step
			final double timeElapsed = timestamp - extNot.getTimestamp(); // we know this quantity to be >= 0
			final double p = extNot.getSeverity() * Math.pow(0.5, timeElapsed / extNot.getHalfLife());
			double totalProbability = probabilities.getOrDefault(parameterName, 0.0);
			
			// Determine new probability
			if (extNot.getType().equals(ExternalNotificationType.RELATIVE))
				totalProbability = 1.0 - (1.0 - totalProbability) * Math.pow(1.0 - p, extNot.getNumber());
			else if (extNot.getType().equals(ExternalNotificationType.ABSOLUTE))
				totalProbability = extNot.getSeverity();
			else
				throw new Exception("Unknown notification type: " + extNot.getType().name());

			// Store new computed probability
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

		// Note that the notifications are ordered by time of occurrence. Our strategy looks as follows.
		// Take the whole time interval and all notifications issued during that period, and whenever such a notification is issued, chop the interval into smaller segments. 
		// That one one gets a collection of intervals and a one-to-one mapping between them and the notifications.
		// Then integrate the probability function over that interval and sum all these partial integrals up to get the final probability.
		//
		// For relatively acting notifications (those with type = RELATIVE), we do some optimization:
		// Note that the probability function depends not only on the probability evolution of the notification associated to the interval in question,
		// but also on all prior notifications. For computational feasibility, we neglect the dependence of the previous notifications, though.
		// Thus, the probability function which usually looks like
		//     f_new(t) = 1 - (1 - f_old(t)) * (1 - p(t))
		// where p(t) := severity * 2 ^ (-Δt / halflife) is the probability evolution of the current notification,
		// now looks like
		//     f_new(t) = (1 - (1 - f_old(T)) * (1 - p(T))) * p(t) / p(T)
		// where T is the beginning time stamp of the interval (when the notification was issued).
		// It can be rewritten as
		//     f_new(t) = (f_old(T) + severity - f_old(T) * severity) * p(t) / p(T)
		// since severity = p(T).

		final long totalInterval = timestampEnd - timestampBegin;
		// NB: notifications are ordered by time of occurrence
		final Map<String, Double> probabilityLevel = new HashMap<>(); // last probability level per category (more precisely, the probability level at the last notification issue date, but not considering the impact of the last notification)
		final Map<String, Double> totalProbability = new HashMap<>(); // accumulated probability per category
		final Map<String, ExternalNotification> lastNotification = new HashMap<>(); // store last external notification per category

		while (iterator.hasNext()) {
			
			final ExternalNotification next = iterator.next();
			final String parameterName = ExternalNotificationHelper.createParameterName(sourceUserName, next.getCategory());
			final ExternalNotification last = lastNotification.put(parameterName, next);
			if (last == null) continue;

			// Get last known probability settings
			final double lastProbabilityLevelBeforeNotif = probabilityLevel.getOrDefault(parameterName, 0.0); // level at `last.getTimestamp()` before `last` was issued 
			final double lastTotalProbability = totalProbability.getOrDefault(parameterName, 0.0); // total probability before `last.getTimestamp()`

			// Get time differences relative to beginning of interval
			// IMPORTANT: immediately cast to double, otherwise integer division will apply below
			final double deltaTimeBegin = Math.max(timestampBegin, last.getTimestamp()) - last.getTimestamp();
			final double deltaTimeEnd = Math.max(timestampBegin, next.getTimestamp()) - last.getTimestamp();

			// Compute the new probability settings
			final double lastProbabilityLevelAfterNotif;
			final double addedProbability;
			if (last.getType().equals(ExternalNotificationType.RELATIVE)) {
				// 1 - (1 - last) * (1 - severity)
				lastProbabilityLevelAfterNotif = lastProbabilityLevelBeforeNotif + last.getSeverity() - lastProbabilityLevelBeforeNotif * last.getSeverity();
				addedProbability = lastProbabilityLevelAfterNotif * last.getHalfLife() / Math.log(2) * (Math.pow(.5, deltaTimeBegin / last.getHalfLife()) - Math.pow(.5, deltaTimeEnd / last.getHalfLife()));
			}
			else if (last.getType().equals(ExternalNotificationType.ABSOLUTE)) {
				lastProbabilityLevelAfterNotif = last.getSeverity();
				addedProbability = lastProbabilityLevelAfterNotif * (deltaTimeEnd - deltaTimeBegin);
			}
			else
				throw new Exception("Unknown external notification type: " + last.getType().name());
			
			// Store
			probabilityLevel.put(parameterName, lastProbabilityLevelAfterNotif * Math.pow(.5, (double)(next.getTimestamp() - last.getTimestamp()) / last.getHalfLife()));
			totalProbability.put(parameterName, lastTotalProbability + addedProbability / totalInterval);
		}

		// while loop is always one entry behind, do not forget to add it to the total probability
		for (String parameterName : lastNotification.keySet()) {
			final ExternalNotification last = lastNotification.get(parameterName);
			
			// Get last known probability settings
			final double lastProbabilityLevelBeforeNot = probabilityLevel.getOrDefault(parameterName, 0.0);
			final double lastTotalProbability = totalProbability.getOrDefault(parameterName, 0.0);

			// Get time differences relative to beginning of interval
			// IMPORTANT: immediately cast to double, otherwise integer division will apply below
			final double deltaTimeBegin = Math.max(timestampBegin, last.getTimestamp()) - last.getTimestamp();
			final double deltaTimeEnd = timestampEnd - last.getTimestamp();

			// Compute the new probability settings
			final double lastProbabilityLevelAfterNotif;
			final double addedProbability;
			if (last.getType().equals(ExternalNotificationType.RELATIVE)) {
				// 1 - (1 - last) * (1 - severity)
				lastProbabilityLevelAfterNotif = lastProbabilityLevelBeforeNot + last.getSeverity() - lastProbabilityLevelBeforeNot * last.getSeverity();
				addedProbability = lastProbabilityLevelAfterNotif * last.getHalfLife() / Math.log(2) * (Math.pow(.5, deltaTimeBegin / last.getHalfLife()) - Math.pow(.5, deltaTimeEnd / last.getHalfLife()));
			}
			else if (last.getType().equals(ExternalNotificationType.ABSOLUTE)) {
				lastProbabilityLevelAfterNotif = last.getSeverity();
				addedProbability = lastProbabilityLevelAfterNotif * (deltaTimeEnd - deltaTimeBegin);
			}
			else
				throw new Exception("Unknown external notification type: " + last.getType().name());

			// Store
			totalProbability.put(parameterName, lastTotalProbability + addedProbability / totalInterval);
		}

		return totalProbability;
	}
}