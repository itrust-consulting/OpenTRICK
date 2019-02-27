/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOTicketingSystem;
import lu.itrust.business.TS.database.service.ServiceTicketingSystem;
import lu.itrust.business.TS.model.general.TicketingSystem;

/**
 * @author eomar
 *
 */
@Service
public class ServiceTicketingSystemImpl implements ServiceTicketingSystem {
	
	@Autowired
	private DAOTicketingSystem daoTicketingSystem;

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoTicketingSystem.count();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.util.Collection)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends TicketingSystem> entities) {
		daoTicketingSystem.delete(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.io.Serializable)
	 */
	@Transactional
	@Override
	public void delete(Long id) {
		daoTicketingSystem.delete(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(TicketingSystem entity) {
		daoTicketingSystem.delete(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoTicketingSystem.deleteAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return daoTicketingSystem.exists(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<TicketingSystem> findAll() {
		return daoTicketingSystem.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<TicketingSystem> findAll(List<Long> ids) {
		return daoTicketingSystem.findAll(ids);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.Serializable)
	 */
	@Override
	public TicketingSystem findOne(Long id) {
		return daoTicketingSystem.findOne(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public TicketingSystem merge(TicketingSystem entity) {
		return daoTicketingSystem.merge(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Long> save(List<TicketingSystem> entities) {
		return daoTicketingSystem.save(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Long save(TicketingSystem entity) {
		return daoTicketingSystem.save(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<TicketingSystem> entities) {
		daoTicketingSystem.saveOrUpdate(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.lang.Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(TicketingSystem entity) {
		daoTicketingSystem.saveOrUpdate(entity);
	}

	@Override
	public TicketingSystem findByCustomerId(Integer customerId) {
		return daoTicketingSystem.findByCustomerId(customerId);
	}

}
