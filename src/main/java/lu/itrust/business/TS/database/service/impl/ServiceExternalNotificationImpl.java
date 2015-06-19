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
	public Map<String, List<ExternalNotificationOccurrence>> getOccurrences(Collection<String> categories, long minTimestamp, long maxTimestamp, String sourceUserName) throws Exception {
		if (maxTimestamp <= minTimestamp) {
			throw new IllegalArgumentException("minTimestamp must be strictly smaller than maxTimestamp.");
		}
		
		// Init default values
		HashMap<String, Double> frequencies = new HashMap<>(categories.size());
		for (String category : categories) {
			frequencies.put(category, 0.0);
		}

		// Count all notifications. Note that certain values in 'categories' may not be among the keys of 'countResult'. 
		List<ExternalNotificationOccurrence> countResult = daoExternalNotification.count(categories, minTimestamp, maxTimestamp, sourceUserName);

		// Compute likelihood for each category
		return this.aggregateCountResult(countResult);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, List<ExternalNotificationOccurrence>> getOccurrences(long minTimestamp, long maxTimestamp, String sourceUserName) throws Exception {
		if (maxTimestamp <= minTimestamp) {
			throw new IllegalArgumentException("minTimestamp must be strictly smaller than maxTimestamp.");
		}

		// Count all notifications 
		List<ExternalNotificationOccurrence> countResult = daoExternalNotification.countAll(minTimestamp, maxTimestamp, sourceUserName);

		// Compute likelihood for each category
		return this.aggregateCountResult(countResult);
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

}
