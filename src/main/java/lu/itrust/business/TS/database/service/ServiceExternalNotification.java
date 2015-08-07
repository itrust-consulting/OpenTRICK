package lu.itrust.business.TS.database.service;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotificationOccurrence;

/**
 * Interface for a service of external notifications.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
public interface ServiceExternalNotification {
	/**
	 * Retrieves the given external notification from the database.
	 * @param id The unique identifier of the external notification to retrieve.
	 */
	public ExternalNotification get(Integer id) throws Exception;

	/**
	 * Retrieves a list of all external notifications from the database.
	 */
	public List<ExternalNotification> getAll() throws Exception;

	/**
	 * Saves the gives external notification into the database.
	 * @param externalNotification The object to save.
	 */
	public void save(ExternalNotification externalNotification) throws Exception;

	/**
	 * Inserts the given external notification into the database or updates it.
	 * @param externalNotification The object to save/update.
	 */
	public void saveOrUpdate(ExternalNotification externalNotification) throws Exception;

	/**
	 * Removes the given external notification from the database.
	 * @param externalNotification The object to delete.
	 */
	public void delete(ExternalNotification externalNotification) throws Exception;

	/**
	 * Fetches the occurrence likelihoods of each notification category, weighted by severity, over the given time span.
	 * @param scope The notification scope. Only variables in this scope are considered.
	 * @param minTimestamp The inclusive lower bound of the timestamp of all notifications to consider.
	 * @param maxTimestamp The exclusive upper bound of the timestamp of all notifications to consider.
	 * @param unitDuration The duration (in seconds) of a 'unit' in the definition of the frequency.
	 * For example, if unitDuration is 3600.0 (= 1 hour), the returned frequency values are to be
	 * interpreted as 'number of times per hour'.
	 * @param sourceUserName The name of the user who has reported the notifications to consider.
	 * @return Returns the frequency for each category in the scope.
	 * Here, 'frequency' denotes the sum of all 'number' fields of the considered notifications divided by the specified time span.
	 */
	public Map<String, List<ExternalNotificationOccurrence>> getOccurrences(long minTimestamp, long maxTimestamp, String sourceUserName) throws Exception;

	/** @see lu.itrust.business.TS.database.dao.DAOExternalNotification#computeProbabilitiesAtTime(long, String, Map) */
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName, Map<Integer, Double> severityProbabilities, double minimumProbability) throws Exception;
	
	/** @see lu.itrust.business.TS.database.dao.DAOExternalNotification#computeProbabilitiesInInterval(long, String, Map) */
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd, String sourceUserName, Map<Integer, Double> severityProbabilities, double minimumProbability) throws Exception;
}
