package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.externalnotification.ExternalNotification;

/**
 * Interface for a service of external notifications.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
public interface ServiceExternalNotification {
	/** Retrieves an entity from the database. */
	public ExternalNotification get(Integer id) throws Exception;

	/** Retrieves all entities from the database. */
	public List<ExternalNotification> getAll() throws Exception;

	/** Saves an entity into the database. */
	public void save(ExternalNotification externalNotification) throws Exception;

	/** Saves an entity into the database, or updates it. */
	public void saveOrUpdate(ExternalNotification externalNotification) throws Exception;

	/** Removes an entity from the database. */
	public void delete(ExternalNotification externalNotification) throws Exception;
}
