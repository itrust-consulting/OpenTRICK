package lu.itrust.business.TS.database.dao;

import java.util.Collection;
import java.util.List;

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
	 * Counts all notifications (summing over the 'number' column) in the database in the given time range.
	 * @param categories The categories which notifications must have in order to be included in the result.
	 * @param minTimestamp The inclusive lower bound of the timestamp of all notifications to consider.
	 * @param maxTimestamp The exclusive upper bound of the timestamp of all notifications to consider.
	 * @return Returns the occurrence for each category specified in the 'categories' parameter.
	 * If a category is unknown or has no associated notifications, it may be omitted in the list.
	 * Here, 'occurrence' denotes the sum of all 'number' columns of the considered notifications.
	 */
	public List<ExternalNotificationOccurrence> countAll(Collection<String> categories, long minTimestamp, long maxTimestamp) throws Exception;
}
