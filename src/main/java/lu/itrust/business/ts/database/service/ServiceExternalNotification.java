package lu.itrust.business.ts.database.service;

import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.model.externalnotification.ExternalNotification;

/**
 * Interface for a service of external notifications.
 * @author Steve Muller  itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
public interface ServiceExternalNotification {
	/**
	 * Retrieves the given external notification from the database.
	 * @param id The unique identifier of the external notification to retrieve.
	 */
	public ExternalNotification get(Integer id);

	/**
	 * Retrieves a list of all external notifications from the database.
	 */
	public List<ExternalNotification> getAll();

	/**
	 * Saves the gives external notification into the database.
	 * @param externalNotification The object to save.
	 */
	public void save(ExternalNotification externalNotification);

	/**
	 * Inserts the given external notification into the database or updates it.
	 * @param externalNotification The object to save/update.
	 */
	public void saveOrUpdate(ExternalNotification externalNotification);

	/**
	 * Removes the given external notification from the database.
	 * @param externalNotification The object to delete.
	 */
	public void delete(ExternalNotification externalNotification);

	/** 
	 * @see lu.itrust.business.ts.database.dao.DAOExternalNotification#computeProbabilityAtTime(long,String, String) */
	public Double computeProbabilityAtTime(long timestamp, String prefix, String acronym);

	/** @see lu.itrust.business.ts.database.dao.DAOExternalNotification#computeProbabilitiesAtTime(long, String,List) */
	public Map<String,Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName, List<String> categories);

	/** @see lu.itrust.business.ts.database.dao.DAOExternalNotification#computeProbabilitiesAtTime(long, String) */
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName);
	
	/** @see lu.itrust.business.ts.database.dao.DAOExternalNotification#computeProbabilitiesInInterval(long, String, Map) */
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd, String sourceUserName, double minimumProbability);

	/**
	 * @see lu.itrust.business.ts.database.dao.DAOExternalNotification#findLastSeverity(String, String) */
	public Double findLastSeverity(String prefix, String acronym);

	/** @see lu.itrust.business.ts.database.dao.DAOExternalNotification#findLastSeverities(String, Map) */
	public Map<String, Double> findLastSeverities(String sourceUserName,
			List<String> categories);

    public String [] extractPrefixAndCategory(String parameter, List<String> idsNames);
}
