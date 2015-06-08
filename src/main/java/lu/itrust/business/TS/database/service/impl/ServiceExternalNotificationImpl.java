package lu.itrust.business.TS.database.service.impl;

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
	public Map<String, Double> getFrequencies(List<String> categories, long minTimestamp, long maxTimestamp, double unitDuration) throws Exception {
		if (maxTimestamp <= minTimestamp) {
			throw new IllegalArgumentException("minTimestamp must be strictly smaller than maxTimestamp.");
		}
		if (unitDuration <= 0) {
			throw new IllegalArgumentException("unitDuration must be positive.");
		}
		
		// Count all notifications. Note that certain values in 'categories' may not be among the keys of 'countResult'. 
		List<ExternalNotificationOccurrence> countResult = daoExternalNotification.countAll(categories, minTimestamp, maxTimestamp);
		
		// Init default values
		HashMap<String, Double> frequencies = new HashMap<String, Double>(categories.size());
		for (String category : categories) {
			frequencies.put(category, 0.0);
		}
		
		// Compute the time span between min & max in the given time unit
		final double timespanInUnits = (maxTimestamp - minTimestamp) / unitDuration;
		
		// Compute frequencies
		for (ExternalNotificationOccurrence entry : countResult) {
			double frequency = entry.getOccurrence() / timespanInUnits;
			frequencies.put(entry.getCategory(), frequency);
		}

		return frequencies;
	}

}
