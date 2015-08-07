package lu.itrust.business.TS.database.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotificationOccurrence;

public interface DAOExternalNotification {
	/** Retrieves an entity from the DAO. */
	public ExternalNotification get(Integer id) throws Exception;

	/** Retrieves all entities from the DAO. */
	public List<ExternalNotification> getAll() throws Exception;

	/** Saves an entity into the DAO. */
	public void save(ExternalNotification externalNotification) throws Exception;

	/** Saves an entity into the DAO, or updates it. */
	public void saveOrUpdate(ExternalNotification externalNotification) throws Exception;

	/** Removes an entity from the DAO. */
	public void delete(ExternalNotification externalNotification) throws Exception;

	/**
	 * Counts all notifications (summing over the 'number' column) in the database in the given time range,
	 * grouping by category and severity.
	 * @param categories The categories which notifications must have in order to be included in the result.
	 * @param minTimestamp The inclusive lower bound of the timestamp of all notifications to consider.
	 * @param maxTimestamp The exclusive upper bound of the timestamp of all notifications to consider.
	 * @param sourceUserName The name of the user who has reported the notifications to consider.
	 * @return Returns the occurrence for each category specified in the 'categories' parameter.
	 * If a category is unknown or has no associated notifications, it may be omitted in the list.
	 * Here, 'occurrence' denotes the sum of all 'number' columns of the considered notifications.
	 */
	public List<ExternalNotificationOccurrence> count(Collection<String> categories, long minTimestamp, long maxTimestamp, String sourceUserName) throws Exception;

	/**
	 * Counts all notifications (summing over the 'number' column) in the database in the given time range,
	 * grouping by category and severity.
	 * @param minTimestamp The inclusive lower bound of the timestamp of all notifications to consider.
	 * @param maxTimestamp The exclusive upper bound of the timestamp of all notifications to consider.
	 * @param sourceUserName The name of the user who has reported the notifications to consider.
	 * @return Returns the occurrence for each category in the given scope.
	 * Here, 'occurrence' denotes the sum of all 'number' columns of the considered notifications.
	 */
	public List<ExternalNotificationOccurrence> countAll(long minTimestamp, long maxTimestamp, String sourceUserName) throws Exception;
	
	/**
	 * Derives the probabilities of occurrence of categories from the external notifications in the database for a given instant.
	 * @param timestamp The timestamp at which the probabilities shall be computed. In general the right value to specify here is NOW, unless a history entry is to be computed.
	 * @param sourceUserName The name of the user who reported the external notifications. Only categories reported by this user are considered.
	 * @param minimumProbability The minimum probability to return.
	 * @return Returns a map which assigns to each notification category the probability that the associated event occurs.
	 * @throws Exception
	 */
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName, Map<Integer, Double> severityProbabilities, double minimumProbability) throws Exception;

	/**
	 * Derives the probabilities of occurrence of categories from the external notifications in the database for a given instant.
	 * @param timestampBegin The timestamp of the beginning of the interval in which the probabilities shall be computed.
	 * @param timestampEnd The timestamp of the end of the interval in which the probabilities shall be computed.
	 * @param sourceUserName The name of the user who reported the external notifications. Only categories reported by this user are considered.
	 * @param minimumProbability The minimum probability to return.
	 * @return Returns a map which assigns to each notification category the probability that the associated event occurs.
	 * @throws Exception
	 */
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd, String sourceUserName, Map<Integer, Double> severityProbabilities, double minimumProbability) throws Exception;
}
