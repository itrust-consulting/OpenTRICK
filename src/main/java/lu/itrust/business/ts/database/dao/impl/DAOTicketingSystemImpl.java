/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOTicketingSystem;
import lu.itrust.business.ts.model.general.TicketingSystem;

/**
 * @author eomar
 *
 */
@Repository
public class DAOTicketingSystemImpl extends DAOHibernate implements DAOTicketingSystem {

	/**
	 * 
	 */
	public DAOTicketingSystemImpl() {
	}

	/**
	 * @param session
	 */
	public DAOTicketingSystemImpl(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return createQueryWithCache("Select count(*) From TicketingSystem", Long.class).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.
	 * Collection)
	 */
	@Override
	public void delete(Collection<? extends TicketingSystem> entities) {
		entities.forEach(e -> delete(e));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(Long id) {
		createQuery("Delete From TicketingSystem where id = :id").setParameter("id", id).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Override
	public void delete(TicketingSystem entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		createQuery("Delete From TicketingSystem").executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return createQueryWithCache("Select count(*)> 0 From TicketingSystem where id = :id", Boolean.class).setParameter("id", id).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<TicketingSystem> findAll() {
		return createQueryWithCache("From TicketingSystem",TicketingSystem.class ).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<TicketingSystem> findAll(List<Long> ids) {
		return createQueryWithCache("From TicketingSystem where id in :ids", TicketingSystem.class).setParameterList("ids", ids).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public TicketingSystem findOne(Long id) {
		return getSession().get(TicketingSystem.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public TicketingSystem merge(TicketingSystem entity) {
		return (TicketingSystem) getSession().merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Long> save(List<TicketingSystem> entities) {
		return entities.stream().map(e-> save(e)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Long save(TicketingSystem entity) {
		return (Long) getSession().save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.util.
	 * List)
	 */
	@Override
	public void saveOrUpdate(List<TicketingSystem> entities) {
		entities.forEach(e-> saveOrUpdate(e));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Override
	public void saveOrUpdate(TicketingSystem entity) {
		getSession().saveOrUpdate(entity);
	}

	@Override
	public TicketingSystem findByCustomerId(Integer customerId) {
		return createQueryWithCache("Select c.ticketingSystem From Customer c where c.id = :customerId", TicketingSystem.class).setParameter("customerId", customerId).uniqueResult();
	}

}
