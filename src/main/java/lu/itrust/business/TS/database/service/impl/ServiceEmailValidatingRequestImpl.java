/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOEmailValidatingRequest;
import lu.itrust.business.TS.database.service.ServiceEmailValidatingRequest;
import lu.itrust.business.TS.usermanagement.EmailValidatingRequest;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Service
public class ServiceEmailValidatingRequestImpl implements ServiceEmailValidatingRequest {
	
	@Autowired
	private DAOEmailValidatingRequest daoEmailValidatingRequest;

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoEmailValidatingRequest.count();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.util.Collection)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends EmailValidatingRequest> entities) {
		daoEmailValidatingRequest.delete(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(EmailValidatingRequest entity) {
		daoEmailValidatingRequest.delete(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.io.Serializable)
	 */
	@Transactional
	@Override
	public void delete(Long id) {
		daoEmailValidatingRequest.delete(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoEmailValidatingRequest.deleteAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.template.TemplateEmailValidatingRequest#deleteByUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@Transactional
	@Override
	public void deleteByUser(User user) {
		daoEmailValidatingRequest.deleteByUser(user);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return daoEmailValidatingRequest.exists(id);
	}
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.template.TemplateEmailValidatingRequest#existsByEmail(java.lang.String)
	 */
	@Override
	public boolean existsByEmail(String email) {
		return daoEmailValidatingRequest.existsByEmail(email);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.template.TemplateEmailValidatingRequest#existsByToken(java.lang.String)
	 */
	@Override
	public boolean existsByToken(String token) {
		return daoEmailValidatingRequest.existsByToken(token);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.template.TemplateEmailValidatingRequest#existsByUsername(java.lang.String)
	 */
	@Override
	public boolean existsByUsername(String username) {
		return daoEmailValidatingRequest.existsByUsername(username);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<EmailValidatingRequest> findAll() {
		return daoEmailValidatingRequest.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<EmailValidatingRequest> findAll(List<Long> ids) {
		return daoEmailValidatingRequest.findAll(ids);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.template.TemplateEmailValidatingRequest#findByEmail(java.lang.String)
	 */
	@Override
	public EmailValidatingRequest findByEmail(String email) {
		return daoEmailValidatingRequest.findByEmail(email);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.template.TemplateEmailValidatingRequest#findByToken(java.lang.String)
	 */
	@Override
	public EmailValidatingRequest findByToken(String token) {
		return daoEmailValidatingRequest.findByToken(token);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.template.TemplateEmailValidatingRequest#findByUsername(java.lang.String)
	 */
	@Override
	public EmailValidatingRequest findByUsername(String username) {
		return daoEmailValidatingRequest.findByUsername(username);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.Serializable)
	 */
	@Override
	public EmailValidatingRequest findOne(Long id) {
		return daoEmailValidatingRequest.findOne(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public EmailValidatingRequest merge(EmailValidatingRequest entity) {
		return daoEmailValidatingRequest.merge(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Long save(EmailValidatingRequest entity) {
		return daoEmailValidatingRequest.save(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Long> save(List<EmailValidatingRequest> entities) {
		return daoEmailValidatingRequest.save(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.lang.Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(EmailValidatingRequest entity) {
		daoEmailValidatingRequest.saveOrUpdate(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<EmailValidatingRequest> entities) {
		daoEmailValidatingRequest.saveOrUpdate(entities);
	}

}
