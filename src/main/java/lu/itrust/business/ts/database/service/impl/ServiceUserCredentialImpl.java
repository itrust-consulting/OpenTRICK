/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOUserCredential;
import lu.itrust.business.ts.database.service.ServiceUserCredential;
import lu.itrust.business.ts.usermanagement.UserCredential;

/**
 * @author eomar
 *
 */
@Service
@Transactional(readOnly = true)
public class ServiceUserCredentialImpl implements ServiceUserCredential {

	@Autowired
	private DAOUserCredential daoUserCredential;
	
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoUserCredential.count();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.Collection)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends UserCredential> entities) {
		daoUserCredential.delete(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.Serializable)
	 */
	@Transactional
	@Override
	public void delete(Long id) {
		daoUserCredential.delete(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(UserCredential entity) {
		daoUserCredential.delete(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoUserCredential.deleteAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return daoUserCredential.exists(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<UserCredential> findAll() {
		return daoUserCredential.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<UserCredential> findAll(List<Long> ids) {
		return daoUserCredential.findAll(ids);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.Serializable)
	 */
	@Override
	public UserCredential findOne(Long id) {
		return daoUserCredential.findOne(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public UserCredential merge(UserCredential entity) {
		return daoUserCredential.merge(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Long> save(List<UserCredential> entities) {
		return daoUserCredential.save(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Long save(UserCredential entity) {
		return daoUserCredential.save(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<UserCredential> entities) {
		daoUserCredential.saveOrUpdate(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(UserCredential entity) {
		daoUserCredential.saveOrUpdate(entity);
	}

	@Override
	public List<UserCredential> findByUsername(String username) {
		return daoUserCredential.findByUsername(username);
	}

	@Override
	public UserCredential findByIdAndUsername(long id, String name) {
		return daoUserCredential.findByIdAndUsername(id, name);
	}

}
