package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.externalnotification.ExternalNotification;

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
}
