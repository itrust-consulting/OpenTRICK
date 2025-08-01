/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOUserCredential;
import lu.itrust.business.ts.usermanagement.UserCredential;

/**
 * @author eomar
 *
 */
@Repository
public class DAOUserCredentialImpl extends DAOHibernate implements DAOUserCredential {

	public DAOUserCredentialImpl() {
		super();
	}

	public DAOUserCredentialImpl(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return createQueryWithCache("Select count(*) From UserCredential", Long.class).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.
	 * Collection)
	 */
	@Override
	public void delete(Collection<? extends UserCredential> entities) {
		entities.forEach(c -> delete(c));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(Long id) {
		createQuery("Delete From UserCredential where id = :id").setParameter("id", id).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Override
	public void delete(UserCredential entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		createQuery("Delete From UserCredential").executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return createQueryWithCache("Select count(*) > 0 From UserCredential where id = :id", Boolean.class)
				.setParameter("id", id).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<UserCredential> findAll() {
		return createQueryWithCache("From UserCredential", UserCredential.class).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<UserCredential> findAll(List<Long> ids) {
		return ids == null || ids.isEmpty() ? Collections.emptyList()
				: createQueryWithCache("From UserCredential where id in :ids", UserCredential.class)
						.setParameterList("ids", ids).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public UserCredential findOne(Long id) {
		return getSession().get(UserCredential.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public UserCredential merge(UserCredential entity) {
		return (UserCredential) getSession().merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Long> save(List<UserCredential> entities) {
		return entities.stream().map(e -> save(e)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Long save(UserCredential entity) {
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
	public void saveOrUpdate(List<UserCredential> entities) {
		entities.forEach(e -> saveOrUpdate(e));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Override
	public void saveOrUpdate(UserCredential entity) {
		getSession().saveOrUpdate(entity);
	}

	@Override
	public List<UserCredential> findByUsername(String username) {
		return createQueryWithCache(
				"Select credential From User user inner join user.credentials as credential where user.login = :username order by credential.ticketingSystem.customer.organisation",
				UserCredential.class).setParameter("username", username).list();
	}

	@Override
	public UserCredential findByIdAndUsername(long id, String name) {
		return createQueryWithCache(
				"Select credential From User user inner join user.credentials as credential where user.login = :username and credential.id = :id",
				UserCredential.class).setParameter("id", id).setParameter("username", name).uniqueResult();
	}

}
