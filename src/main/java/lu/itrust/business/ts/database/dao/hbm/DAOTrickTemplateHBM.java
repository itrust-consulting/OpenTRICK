/**
 * 
 */
package lu.itrust.business.ts.database.dao.hbm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOTrickTemplate;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplate;

/**
 * @author eomar
 *
 */
@Repository
public class DAOTrickTemplateHBM extends DAOHibernate implements DAOTrickTemplate {

	public DAOTrickTemplateHBM() {
		super();
	}

	public DAOTrickTemplateHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.template.TemplateTrickTemplate#
	 * findByIdAndCustomer(long, int)
	 */
	@Override
	public TrickTemplate findByIdAndCustomer(long id, int customerId) {
		return getSession().createQuery(
				"Select template From Customer customer inner join customer.templates as template where template.id = :id and customer.id = :customerId",
				TrickTemplate.class).setParameter("id", id).setParameter("customerId", customerId).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.template.TemplateTrickTemplate#findByCustomer
	 * (int)
	 */
	@Override
	public List<TrickTemplate> findByCustomer(int customerId) {
		return getSession().createQuery(
				"Select template From Customer customer inner join customer.templates as template where customer.id = :customerId",
				TrickTemplate.class)
				.setParameter("customerId", customerId).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.template.TemplateTrickTemplate#
	 * findByCustomerAndType(int, lu.itrust.business.ts.model.analysis.AnalysisType)
	 */
	@Override
	public List<TrickTemplate> findByCustomerAndType(int customerId, AnalysisType type) {
		return getSession()
				.createQuery(
						"Select template From Customer customer inner join customer.templates as template where customer.id = :customerId and template.type = :type",
						TrickTemplate.class)
				.setParameter("customerId", customerId).setParameter("type", type).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return getSession().createQuery("Select count(*) From TrickTemplate", Long.class).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.
	 * Collection)
	 */
	@Override
	public void delete(Collection<? extends TrickTemplate> entities) {
		entities.forEach(entity -> delete(entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(Long id) {
		getSession().createQuery("Delete TrickTemplate where id = :id", Integer.class).setParameter("id", id)
				.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Override
	public void delete(TrickTemplate entity) {
		getSession().remove(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		getSession().createQuery("Delete TrickTemplate",Integer.class).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return getSession().createQuery("Select count(*)> 0 From TrickTemplate where id = :id", Boolean.class)
				.setParameter("id", id).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<TrickTemplate> findAll() {
		return getSession().createQuery("From TrickTemplate", TrickTemplate.class).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<TrickTemplate> findAll(List<Long> ids) {
		return ids.isEmpty() ? Collections.emptyList()
				: getSession().createQuery("From TrickTemplate where id in :ids", TrickTemplate.class)
						.setParameterList("ids", ids).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public TrickTemplate findOne(Long id) {
		return getSession().get(TrickTemplate.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public TrickTemplate merge(TrickTemplate entity) {
		return (TrickTemplate) getSession().merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Long> save(List<TrickTemplate> entities) {
		return entities.stream().map(e -> save(e)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Long save(TrickTemplate entity) {
		 getSession().persist(entity);
		 return entity.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.util.
	 * List)
	 */
	@Override
	public void saveOrUpdate(List<TrickTemplate> entities) {
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
	public void saveOrUpdate(TrickTemplate entity) {
		getSession().saveOrUpdate(entity);
	}

	@Override
	public List<TrickTemplate> findDefault() {
		return getSession().createQuery(
				"Select template From Customer customer inner join customer.templates as template where customer.canBeUsed = false",
				TrickTemplate.class)
				.list();
	}

	@Override
	public TrickTemplate findByIdAndCustomerOrDefault(Long id, Integer customerId) {
		return getSession().createQuery(
				"Select template From Customer customer inner join customer.templates as template where template.id = :id and (customer.id = :customerId or customer.canBeUsed = false)",
				TrickTemplate.class).setParameter("id", id).setParameter("customerId", customerId).uniqueResult();
	}

	@Override
	public boolean isUseAuthorised(Long id, Integer customerId) {
		return getSession().createQuery(
				"Select count(template) > 0 From Customer customer inner join customer.templates as template where template.id = :id and (customer.id = :customerId or customer.canBeUsed = false)",
				Boolean.class).setParameter("id", id).setParameter("customerId", customerId).uniqueResult();
	}

	@Override
	public AnalysisType findTypeById(Long id) {
		return getSession().createQuery("Select type From TrickTemplate where id = :id", AnalysisType.class)
				.setParameter("id", id).uniqueResult();
	}

}
