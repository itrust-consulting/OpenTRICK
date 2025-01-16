package lu.itrust.business.ts.database.dao;

import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.model.externalnotification.ExternalNotification;

public interface DAOExternalNotification {
	/** Retrieves an entity from the DAO. */
	public ExternalNotification get(Integer id);

	/** Retrieves all entities from the DAO. */
	public List<ExternalNotification> getAll();

	/** Saves an entity into the DAO. */
	public void save(ExternalNotification externalNotification);

	/** Saves an entity into the DAO, or updates it. */
	public void saveOrUpdate(ExternalNotification externalNotification);

	/** Removes an entity from the DAO. */
	public void delete(ExternalNotification externalNotification);

		/**
	 * Derives the probabilities of occurrence of categories from the external
	 * notifications in the database for a given instant.
	 * @param timestamp
	 *            The timestamp at which the probabilities shall be computed. In
	 *            general the right value to specify here is NOW, unless a
	 *            history entry is to be computed.
	 * @param prefix
	 *	          The name of the user who reported the external notifications.
	 *
	 * @param acronym
	 *            The name of the user and category who reported the external notifications.
	 *            Only the specific category is considered.
	 * @return Returns a map which assigns to each notification category the
	 *         probability that the associated event occurs.
	 * @throws Exception
	 */
	public Double computeProbabilityAtTime(long timestamp, String prefix, String acronym);


		/**
	 * Derives the probabilities of occurrence of categories from the external
	 * notifications in the database for a given instant.
	 * @param timestamp
	 *            The timestamp at which the probabilities shall be computed. In
	 *            general the right value to specify here is NOW, unless a
	 *            history entry is to be computed.
	 * @param sourceUserName
	 *            The name of the user who reported the external notifications.
	 * 
	 * @param categories
	 *            Only these categories user are considered.          
	 * 
	 * @return Returns a map which assigns to each notification category the
	 *         probability that the associated event occurs.
	 * @throws Exception
	 */
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName, List<String> categories);

	/**
	 * Derives the probabilities of occurrence of categories from the external
	 * notifications in the database for a given instant.
	 * @param timestamp
	 *            The timestamp at which the probabilities shall be computed. In
	 *            general the right value to specify here is NOW, unless a
	 *            history entry is to be computed.
	 * @param sourceUserName
	 *            The name of the user who reported the external notifications.
	 *            Only categories reported by this user are considered.
	 * 
	 * @return Returns a map which assigns to each notification category the
	 *         probability that the associated event occurs.
	 * @throws Exception
	 */
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName);

	/**
	 * Derives the probabilities of occurrence of categories from the external
	 * notifications in the database for a given instant.
	 * 
	 * @param timestampBegin
	 *            The timestamp of the beginning of the interval in which the
	 *            probabilities shall be computed.
	 * @param timestampEnd
	 *            The timestamp of the end of the interval in which the
	 *            probabilities shall be computed.
	 * @param sourceUserName
	 *            The name of the user who reported the external notifications.
	 *            Only categories reported by this user are considered.
	 * @param minimumProbability
	 *            The minimum probability that a parameter should have at all
	 *            time.
	 * @return Returns a map which assigns to each notification category the
	 *         probability that the associated event occurs.
	 * @throws Exception
	 */
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd, String sourceUserName, double minimumProbability);

	/**
	 * Retrives the last severety of given acronym
	 * @param prefix
	 * @param acronym
	 * @return
	 */
	public Double findLastSeverity(String prefix, String acronym);

	/**
	 * Retrives the last severeties of given categories
	 * @param sourceUserName
	 * @param categories
	 * @return
	 */
    public Map<String, Double> findLastSeverities(String sourceUserName, List<String> categories);

	/**
	 * Extract prefix and category from Parameter.
	 * @param parameter
	 * @param idsNames
	 * @return
	 */
	public String [] extractPrefixAndCategory(String parameter, List<String> idsNames);

	/**
	 *  Check if Notification exists by prefix and category
	 * @param prefix
	 * @param category
	 * @return
	 */
	public boolean exists(String prefix, String category);

}
