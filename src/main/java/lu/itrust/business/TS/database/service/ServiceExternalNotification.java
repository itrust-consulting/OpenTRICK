package lu.itrust.business.TS.database.service;

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

	/** @see lu.itrust.business.TS.database.dao.DAOExternalNotification#computeProbabilitiesAtTime(long, String, Map) */
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName, double minimumProbability) throws Exception;
	
	/** @see lu.itrust.business.TS.database.dao.DAOExternalNotification#computeProbabilitiesInInterval(long, String, Map) */
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd, String sourceUserName, double minimumProbability) throws Exception;
}
