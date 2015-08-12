package lu.itrust.business.TS.database.dao.hbm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.database.dao.DAOExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotificationOccurrence;
import lu.itrust.business.expressions.StringExpressionHelper;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
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
	@Deprecated
	public List<ExternalNotificationOccurrence> count(Collection<String> categories, long minTimestamp, long maxTimestamp, String sourceUserName) throws Exception {
		// NB: if the 'categories' list is empty, the HQL constructed below will not work
		// because we use Restrictions.in() - it will produce something like "WHERE category IN ()"
		// which is a syntax error.
		// However, if no categories have been specified, we know that the result is going to be empty anyway.
		if (categories.size() == 0)
			return new ArrayList<>();
		
		// Define what will be part of the result (SELECT)
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.groupProperty("category"), "category");
		projections.add(Projections.groupProperty("severity"), "severity");
		projections.add(Projections.sum("number"), "occurrence");
		
		// Define filters acting on result set (WHERE)
		Criteria criteria = getSession()
				.createCriteria(ExternalNotification.class)
				.add(Restrictions.between("timestamp", minTimestamp, maxTimestamp))
				.add(Restrictions.in("category", categories))
				.add(Restrictions.eq("sourceUserName", sourceUserName))
				.setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(ExternalNotificationOccurrence.class));
		
		return (List<ExternalNotificationOccurrence>) criteria.list();
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public List<ExternalNotificationOccurrence> countAll(long minTimestamp, long maxTimestamp, String sourceUserName) throws Exception {
		// Define what will be part of the result (SELECT)
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.groupProperty("category"), "category");
		projections.add(Projections.groupProperty("severity"), "severity");
		projections.add(Projections.sum("number"), "occurrence");
		
		// Define filters acting on result set (WHERE)
		Criteria criteria = getSession()
				.createCriteria(ExternalNotification.class)
				.add(Restrictions.between("timestamp", minTimestamp, maxTimestamp))
				.add(Restrictions.eq("sourceUserName", sourceUserName))
				.setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(ExternalNotificationOccurrence.class));
		
		return (List<ExternalNotificationOccurrence>) criteria.list();
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName, Map<Integer, Double> severityProbabilities, double minimumProbability) throws Exception {
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
		//    totalProb := 1 - (1 - assertiveness * totalProb) * (1 - Pr[event])
		// for each event, starting at totalProb := 0.
		// Note that Pr[event] follows the exponential-decay model, i.e. Pr[event](t) = p0 * (1/2) ^ ((t - t0)/T)
		// where p0 := initial probability = severity, t0 := timestamp of notification, T := half-life. 
		
		Map<String, Double> probabilities = new HashMap<>();
		while (iterator.hasNext()) {
			ExternalNotification extNot = iterator.next();
			
			// Deduce parameter name from category
			String parameterName = StringExpressionHelper.makeValidVariable(String.format("%s_%s", sourceUserName, extNot.getCategory()));
			
			double severity = severityProbabilities.getOrDefault(extNot.getSeverity(), 0.0);
			double timeElapsed = timestamp - extNot.getTimestamp(); // we know this quantity to be >= 0
			double p = severity * Math.pow(0.5, timeElapsed / extNot.getHalfLife());

			double totalProbability = probabilities.getOrDefault(parameterName, 0.0);
			totalProbability = 1.0 - (1.0 - (1.0 - extNot.getAssertiveness()) * totalProbability) * Math.pow(1.0 - p, extNot.getNumber());
			probabilities.put(parameterName, totalProbability);
		}

		return probabilities;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd, String sourceUserName, Map<Integer, Double> severityProbabilities, double minimumProbability) throws Exception {
		String query = "FROM ExternalNotification extnot WHERE extnot.timestamp + extnot.halfLife*7 >= :begin AND extnot.timestamp <= :end AND extnot.sourceUserName = :sourceUserName ORDER BY extnot.timestamp ASC";
		Iterator<ExternalNotification> iterator = getSession().createQuery(query).setParameter("begin", timestampBegin).setParameter("end", timestampEnd).setParameter("sourceUserName", sourceUserName).iterate();

		if (!iterator.hasNext())
			return new HashMap<>();
		
		// NOTA BENE: in fact one would have to integrate the curves; we take a more simple approach by just computing the values
		// at the critical spots (discontinuities due to new notifications being reported) and assume that the level remains constant
		// until the next notification or until the notification effect reaches <1% (which happens at roughly 7 times the half-life).
		// 
		// The results are not exact, but reflect somehow the history. Mathematically speaking, we compute a discrete version of the lower Darboux sum here.

		final long totalInterval = timestampEnd - timestampBegin;
		// NB: notifications are ordered by time of occurrence
		final Map<String, Double> probabilityLevel = new HashMap<>(); // last probability level per category
		final Map<String, Long> probabilityLevelTimestamp = new HashMap<>(); // last notification timestamp per category
		final Map<String, Long> probabilityInterval = new HashMap<>(); // interval size of last notification per category (= max interval after which we neglect effect on probability level)
		final Map<String, Double> totalProbability = new HashMap<>(); // accumulated probability per category

		while (iterator.hasNext()) {
			// Get boundary times of the interval of the current ExternalNotification instance
			final ExternalNotification current = iterator.next();
			final long startTime = Math.max(timestampBegin, current.getTimestamp());

			// Get severity
			String parameterName = StringExpressionHelper.makeValidVariable(String.format("%s_%s", sourceUserName, current.getCategory()));
			final double severity = severityProbabilities.getOrDefault(current.getSeverity(), 0.0);
			
			// Get last known probability settings
			final double lastProbabilityLevel = probabilityLevel.getOrDefault(parameterName, 0.0);
			final long lastProbabilityLevelTimestamp = probabilityLevelTimestamp.getOrDefault(parameterName, timestampBegin);
			final long lastProbabilityInterval = probabilityInterval.getOrDefault(parameterName, 0L);
			final double lastTotalProbability = totalProbability.getOrDefault(parameterName, 0.0);
			final long lastInterval = Math.min(lastProbabilityInterval, startTime - lastProbabilityLevelTimestamp);

			// Compute the new probability settings
			final double p = severity * Math.pow(0.5, (startTime - current.getTimestamp()) / current.getHalfLife());
			probabilityLevel.put(parameterName, 1.0 - (1.0 - lastProbabilityLevel) * (1.0 - p));
			probabilityLevelTimestamp.put(parameterName, startTime);
			totalProbability.put(parameterName, lastTotalProbability + lastProbabilityLevel * lastInterval / totalInterval);
			probabilityInterval.put(parameterName, current.getHalfLife() * 7);
		}
		
		// while loop is always one entry behind, do not forget to add it to the total probability
		for (String parameterName : totalProbability.keySet()) {
			final long lastProbabilityLevelTimestamp = probabilityLevelTimestamp.getOrDefault(parameterName, timestampBegin);
			final long lastProbabilityInterval = probabilityInterval.getOrDefault(parameterName, 0L);
			final double lastTotalProbability = totalProbability.getOrDefault(parameterName, 0.0);
			final long lastInterval = Math.min(lastProbabilityInterval, timestampEnd - lastProbabilityLevelTimestamp);

			totalProbability.put(parameterName, lastTotalProbability + lastTotalProbability * lastInterval / totalInterval);
		}
		
		return totalProbability;
	}
}