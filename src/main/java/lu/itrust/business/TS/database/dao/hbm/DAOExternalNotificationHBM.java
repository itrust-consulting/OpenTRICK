package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.database.dao.DAOExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * Represents an implementation of the DAOExternalNotification interface
 * for Spring Hibernate.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
@Repository
public class DAOExternalNotificationHBM extends DAOHibernate implements DAOExternalNotification {

	/**
	 * Initializes a new DAOExternalNotificationHBM instance.
	 */
	public DAOExternalNotificationHBM() {
	}

	/**
	 * Initializes a new DAOExternalNotificationHBM instance.
	 * @param session The 'Hibernate' session passed to the 'DAOHibernate' constructor.
	 */
	public DAOExternalNotificationHBM(Session session) {
		super(session);
	}

	/**
	 * Retrieves the given external notification from the database.
	 * @param id The unique identifier of the external notification to retrieve.
	 */
	@Override
	public ExternalNotification get(Integer id) throws Exception {
		return (ExternalNotification) getSession().get(ExternalNotification.class, id);
	}

	/**
	 * Retrieves a list of all external notifications from the database.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExternalNotification> getAll() throws Exception {
		return (List<ExternalNotification>) getSession().createQuery("From ExternalNotification").list();
	}

	/**
	 * Saves the gives external notification into the database.
	 * @param externalNotification The object to save.
	 * @throws Exception
	 */
	@Override
	public void save(ExternalNotification externalNotification) throws Exception {
		getSession().save(externalNotification);
	}

	/**
	 * Inserts the given external notification into the database or updates it.
	 * @param externalNotification The object to save/update.
	 * @throws Exception
	 */
	@Override
	public void saveOrUpdate(ExternalNotification externalNotification) throws Exception {
		getSession().saveOrUpdate(externalNotification);
	}

	/**
	 * Removes the given external notification from the database.
	 * @param externalNotification The object to delete.
	 * @throws Exception
	 */
	@Override
	public void delete(ExternalNotification externalNotification) throws Exception {
		getSession().delete(externalNotification);
	}
}