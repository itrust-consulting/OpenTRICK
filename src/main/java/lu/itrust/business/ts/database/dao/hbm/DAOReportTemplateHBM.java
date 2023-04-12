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

import lu.itrust.business.ts.database.dao.DAOReportTemplate;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.document.impl.ReportTemplate;

/**
 * @author eomar
 *
 */
@Repository
public class DAOReportTemplateHBM extends DAOHibernate implements DAOReportTemplate {
	
	public DAOReportTemplateHBM() {
		super();
	}

	public DAOReportTemplateHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.template.TemplateReportTemplate#
	 * findByIdAndCustomer(long, int)
	 */
	@Override
	public ReportTemplate findByIdAndCustomer(long id, int customerId) {
		return getSession().createQuery("Select template From Customer customer inner join customer.templates as template where template.id = :id and customer.id = :customerId",
				ReportTemplate.class).setParameter("id", id).setParameter("customerId", customerId).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.template.TemplateReportTemplate#findByCustomer
	 * (int)
	 */
	@Override
	public List<ReportTemplate> findByCustomer(int customerId) {
		return getSession().createQuery("Select template From Customer customer inner join customer.templates as template where customer.id = :customerId", ReportTemplate.class)
				.setParameter("customerId", customerId).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.template.TemplateReportTemplate#
	 * findByCustomerAndType(int, lu.itrust.business.ts.model.analysis.AnalysisType)
	 */
	@Override
	public List<ReportTemplate> findByCustomerAndType(int customerId, AnalysisType type) {
		return getSession()
				.createQuery("Select template From Customer customer inner join customer.templates as template where customer.id = :customerId and template.type = :type",
						ReportTemplate.class)
				.setParameter("customerId", customerId).setParameter("type", type).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return getSession().createQuery("Select count(*) From ReportTemplate", Long.class).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.
	 * Collection)
	 */
	@Override
	public void delete(Collection<? extends ReportTemplate> entities) {
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
		getSession().createQuery("Delete ReportTemplate where id = :id").setParameter("id", id).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Override
	public void delete(ReportTemplate entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		getSession().createQuery("Delete ReportTemplate").executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return getSession().createQuery("Select count(*)> 0 From ReportTemplate where id = :id", Boolean.class).setParameter("id", id).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<ReportTemplate> findAll() {
		return getSession().createQuery("From ReportTemplate", ReportTemplate.class).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<ReportTemplate> findAll(List<Long> ids) {
		return ids.isEmpty() ? Collections.emptyList() : getSession().createQuery("From ReportTemplate where id in :ids", ReportTemplate.class).setParameterList("ids", ids).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public ReportTemplate findOne(Long id) {
		return getSession().get(ReportTemplate.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public ReportTemplate merge(ReportTemplate entity) {
		return (ReportTemplate) getSession().merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Long> save(List<ReportTemplate> entities) {
		return entities.stream().map(e -> save(e)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Long save(ReportTemplate entity) {
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
	public void saveOrUpdate(List<ReportTemplate> entities) {
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
	public void saveOrUpdate(ReportTemplate entity) {
		getSession().saveOrUpdate(entity);
	}

	@Override
	public List<ReportTemplate> findDefault() {
		return getSession().createQuery("Select template From Customer customer inner join customer.templates as template where customer.canBeUsed = false", ReportTemplate.class)
				.list();
	}

	@Override
	public ReportTemplate findByIdAndCustomerOrDefault(Long id, Integer customerId) {
		return getSession().createQuery(
				"Select template From Customer customer inner join customer.templates as template where template.id = :id and (customer.id = :customerId or customer.canBeUsed = false)",
				ReportTemplate.class).setParameter("id", id).setParameter("customerId", customerId).uniqueResult();
	}

	@Override
	public Boolean isUseAuthorised(Long id, Integer customerId) {
		return getSession().createQuery(
				"Select count(template) > 0 From Customer customer inner join customer.templates as template where template.id = :id and (customer.id = :customerId or customer.canBeUsed = false)",
				Boolean.class).setParameter("id", id).setParameter("customerId", customerId).uniqueResult();
	}

	@Override
	public AnalysisType findTypeById(Long id) {
		return getSession().createQuery("Select type From ReportTemplate where id = :id", AnalysisType.class).setParameter("id", id).uniqueResult();
	}

}
