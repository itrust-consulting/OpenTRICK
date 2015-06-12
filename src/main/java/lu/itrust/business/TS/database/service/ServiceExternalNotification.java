package lu.itrust.business.TS.database.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.externalnotification.ExternalNotification;

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
	 * @param categories The categories which notifications must have in order to be included in the result.
	 * @param minTimestamp The inclusive lower bound of the timestamp of all notifications to consider.
	 * @param maxTimestamp The exclusive upper bound of the timestamp of all notifications to consider.
	 * @param unitDuration The duration (in seconds) of a 'unit' in the definition of the frequency.
	 * For example, if unitDuration is 3600.0 (= 1 hour), the returned frequency values are to be
	 * interpreted as 'number of times per hour'.
	 * @return Returns the frequency for each category specified in the 'categories' parameter.
	 * The keys of the returned Map<String, Double> are exactly the values in 'categories';
	 * unknown categories get assigned a default value of 0.
	 * Here, 'frequency' denotes the sum of all 'number' fields of the considered notifications divided by the specified time span.
	 */
	public Map<String, Double> getLikelihoods(Collection<String> categories, long minTimestamp, long maxTimestamp, double unitDuration) throws Exception;

	/**
	 * Fetches the occurrence likelihoods of each notification category, weighted by severity, over the given time span.
	 * @param scope The notification scope. Only variables in this scope are considered.
	 * @param minTimestamp The inclusive lower bound of the timestamp of all notifications to consider.
	 * @param maxTimestamp The exclusive upper bound of the timestamp of all notifications to consider.
	 * @param unitDuration The duration (in seconds) of a 'unit' in the definition of the frequency.
	 * For example, if unitDuration is 3600.0 (= 1 hour), the returned frequency values are to be
	 * interpreted as 'number of times per hour'.
	 * @return Returns the frequency for each category in the scope.
	 * Here, 'frequency' denotes the sum of all 'number' fields of the considered notifications divided by the specified time span.
	 */
	public Map<String, Double> getLikelihoods(long minTimestamp, long maxTimestamp, double unitDuration) throws Exception;
}
