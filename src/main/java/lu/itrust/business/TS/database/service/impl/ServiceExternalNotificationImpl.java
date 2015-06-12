package lu.itrust.business.TS.database.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.database.dao.DAOExternalNotification;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotificationOccurrence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Represents a default implementation of the ServiceExternalNotification interface,
 * just passing method calls down to a DAOExternalNotification instance.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
@Service
public class ServiceExternalNotificationImpl implements ServiceExternalNotification {

	@Autowired
	private DAOExternalNotification daoExternalNotification;

	/** {@inheritDoc} */
	@Override
	public ExternalNotification get(Integer id) throws Exception {
		return daoExternalNotification.get(id); 
	}

	/** {@inheritDoc} */
	@Override
	public List<ExternalNotification> getAll() throws Exception {
		return daoExternalNotification.getAll();
	}

	/** {@inheritDoc} */
	@Override
	public void save(ExternalNotification externalNotification) throws Exception {
		daoExternalNotification.save(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void saveOrUpdate(ExternalNotification externalNotification) throws Exception {
		daoExternalNotification.save(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void delete(ExternalNotification externalNotification) throws Exception {
		daoExternalNotification.delete(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> getLikelihoods(Collection<String> categories, long minTimestamp, long maxTimestamp, double unitDuration) throws Exception {
		if (maxTimestamp <= minTimestamp) {
			throw new IllegalArgumentException("minTimestamp must be strictly smaller than maxTimestamp.");
		}
		if (unitDuration <= 0) {
			throw new IllegalArgumentException("unitDuration must be positive.");
		}
		
		// Init default values
		HashMap<String, Double> frequencies = new HashMap<>(categories.size());
		for (String category : categories) {
			frequencies.put(category, 0.0);
		}
		
		// Compute the time span between min & max in the given time unit
		final double timespanInUnits = (maxTimestamp - minTimestamp) / unitDuration;
		
		// Count all notifications. Note that certain values in 'categories' may not be among the keys of 'countResult'. 
		List<ExternalNotificationOccurrence> countResult = daoExternalNotification.count(categories, minTimestamp, maxTimestamp);

		// Compute likelihood for each category
		return this.computeLikelihoods(this.aggregateCountResult(countResult), timespanInUnits);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> getLikelihoods(long minTimestamp, long maxTimestamp, double unitDuration) throws Exception {
		if (maxTimestamp <= minTimestamp) {
			throw new IllegalArgumentException("minTimestamp must be strictly smaller than maxTimestamp.");
		}
		if (unitDuration <= 0) {
			throw new IllegalArgumentException("unitDuration must be positive.");
		}

		// Compute the time span between min & max in the given time unit
		final double timespanInUnits = (maxTimestamp - minTimestamp) / unitDuration;

		// Count all notifications 
		List<ExternalNotificationOccurrence> countResult = daoExternalNotification.countAll(minTimestamp, maxTimestamp);

		// Compute likelihood for each category
		return this.computeLikelihoods(this.aggregateCountResult(countResult), timespanInUnits);
	}

	/**
	 * Aggregates the given result set by grouping it by the 'category' property.
	 * @param occurrences The list of occurrences.
	 * @return Returns a map assigning the list of occurrences having the same category, to that very category.
	 */
	private Map<String, List<ExternalNotificationOccurrence>> aggregateCountResult(List<ExternalNotificationOccurrence> occurrences) {
		Map<String, List<ExternalNotificationOccurrence>> aggregatedResult = new HashMap<>();
		for (ExternalNotificationOccurrence occurrence : occurrences) {
			String key = occurrence.getCategory();
			
			// Fetch list associated to category, or create it if it does not exist
			List<ExternalNotificationOccurrence> aggregatedResultList = aggregatedResult.get(key);
			if (aggregatedResultList == null)
				aggregatedResult.put(key, aggregatedResultList = new ArrayList<>());
			
			// Push occurrence object into list
			aggregatedResultList.add(occurrence);
		}
		return aggregatedResult;
	}

	/**
	 * Computes, for each category, the likelihood that an incident of such a category occurs.
	 * @param occurrencesByCategory The list of notification occurrences, grouped by category, over a certain time period.
	 * @param timespanInUnits The time span (in abstract units) which the notification occurrences have been taken from.
	 * Regarding abstract units: the returned likelihood values are to be understood as 'expected number of times an incident occurs in an abstract unit'.
	 * For instance, if the abstract time unit is 1 year, and notifications have been taken from 1 month, then 'timespanInUnits'
	 * should equal 1/12. The returned likelihood values represent the 'expected number of times per year'.
	 * @return A map assigning a likelihood value to each incident category. The likelihood values are with respect to the abstract
	 * time unit (see description of 'timespanInUnits' parameter). 
	 */
	private Map<String, Double> computeLikelihoods(Map<String, List<ExternalNotificationOccurrence>> occurrencesByCategory, double timespanInUnits) {
		Map<String, Double> likelihoods = new HashMap<>(occurrencesByCategory.size());
		for (String key : occurrencesByCategory.keySet()) {
			// Iterate over all occurrence objects and compute overall likelihood.
			// The sum is weighted by the probability (0 <= p <= 1) of occurrence, associated to the severity. 
			double likelihood = 0.0;
			for (ExternalNotificationOccurrence occurrence : occurrencesByCategory.get(key))
				likelihood += occurrence.getOccurrence() / timespanInUnits * this.getSeverityProbability(occurrence.getSeverity());
			likelihoods.put(key, likelihood);
		}
		return likelihoods;
	}

	/**
	 * Gets the probability that an incident of the given severity occurs, given that a respective anomaly has been detected.
	 * Indeed, an anomaly/intrusion of low severity has a much lower chance to have any impact.
	 * @param level The severity level of the incident. Must be in the range [0, 10]. 
	 * @return Returns a probability value in the range [0.0, 1.0].
	 */
	private double getSeverityProbability(int level) {
		if (level <= 0) return 0.0;
		if (level >= 10) return 1.0;
		
		// TODO make this parametrizable
		
		return Math.log1p(level / 10.0) / Math.log(2);
	}

}
