package lu.itrust.business.TS.database.dao.hbm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lu.itrust.business.TS.database.dao.DAOExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotificationOccurrence;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
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

	/** {@inheritDoc} */
	@Override
	public ExternalNotification get(Integer id) throws Exception {
		return (ExternalNotification) getSession().get(ExternalNotification.class, id);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExternalNotification> getAll() throws Exception {
		return (List<ExternalNotification>) getSession().createQuery("From ExternalNotification").list();
	}

	/** {@inheritDoc} */
	@Override
	public void save(ExternalNotification externalNotification) throws Exception {
		getSession().save(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void saveOrUpdate(ExternalNotification externalNotification) throws Exception {
		getSession().saveOrUpdate(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void delete(ExternalNotification externalNotification) throws Exception {
		getSession().delete(externalNotification);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExternalNotificationOccurrence> count(Collection<String> categories, long minTimestamp, long maxTimestamp) throws Exception {
		// NB: if the 'categories' list is empty, the HQL constructed below will not work
		// because we use Restrictions.in() - it will produce something like "WHERE category IN ()"
		// which is a syntax error.
		// However, if no categories have been specified, we know that the result is going to be empty anyway.
		if (categories.size() == 0)
			return new ArrayList<>();
		
		// Define what will be part of the result (SELECT)
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.groupProperty("category"), "category");
		projections.add(Projections.sum("number"), "occurrence");
		
		// Define filters acting on result set (WHERE)
		Criteria criteria = getSession()
				.createCriteria(ExternalNotification.class)
				.add(Restrictions.between("timestamp", minTimestamp, maxTimestamp))
				.add(Restrictions.in("category", categories))
				.setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(ExternalNotificationOccurrence.class));
		
		return (List<ExternalNotificationOccurrence>) criteria.list();
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<ExternalNotificationOccurrence> countAll(long minTimestamp, long maxTimestamp) throws Exception {
		// Define what will be part of the result (SELECT)
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.groupProperty("category"), "category");
		projections.add(Projections.sum("number"), "occurrence");
		
		// Define filters acting on result set (WHERE)
		Criteria criteria = getSession()
				.createCriteria(ExternalNotification.class)
				.add(Restrictions.between("timestamp", minTimestamp, maxTimestamp))
				.setProjection(projections)
				.setResultTransformer(Transformers.aliasToBean(ExternalNotificationOccurrence.class));
		
		return (List<ExternalNotificationOccurrence>) criteria.list();
	}
}