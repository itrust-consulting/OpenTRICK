/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOReportTemplate;
import lu.itrust.business.ts.database.service.ServiceReportTemplate;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.document.impl.ReportTemplate;

/**
 * @author eomar
 *
 */
@Service
@Transactional(readOnly = true)
public class ServiceReportTemplateImpl implements ServiceReportTemplate {
	
	@Autowired
	private DAOReportTemplate daoReportTemplate;

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.template.TemplateReportTemplate#findByIdAndCustomer(long, int)
	 */
	@Override
	public ReportTemplate findByIdAndCustomer(long id, int customerId) {
		return daoReportTemplate.findByIdAndCustomer(id, customerId);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.template.TemplateReportTemplate#findByCustomer(int)
	 */
	@Override
	public List<ReportTemplate> findByCustomer(int customerId) {
		return daoReportTemplate.findByCustomer(customerId);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.template.TemplateReportTemplate#findByCustomerAndType(int, lu.itrust.business.ts.model.analysis.AnalysisType)
	 */
	@Override
	public List<ReportTemplate> findByCustomerAndType(int customerId, AnalysisType type) {
		return daoReportTemplate.findByCustomerAndType(customerId, type);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoReportTemplate.count();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.Collection)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends ReportTemplate> entities) {
		daoReportTemplate.delete(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.Serializable)
	 */
	@Transactional
	@Override
	public void delete(Long id) {
		daoReportTemplate.delete(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(ReportTemplate entity) {
		daoReportTemplate.delete(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoReportTemplate.deleteAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return daoReportTemplate.exists(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<ReportTemplate> findAll() {
		return daoReportTemplate.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<ReportTemplate> findAll(List<Long> ids) {
		return daoReportTemplate.findAll(ids);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.Serializable)
	 */
	@Override
	public ReportTemplate findOne(Long id) {
		return daoReportTemplate.findOne(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public ReportTemplate merge(ReportTemplate entity) {
		return daoReportTemplate.merge(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Long> save(List<ReportTemplate> entities) {
		return daoReportTemplate.save(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Long save(ReportTemplate entity) {
		return daoReportTemplate.save(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<ReportTemplate> entities) {
		daoReportTemplate.saveOrUpdate(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ReportTemplate entity) {
		daoReportTemplate.saveOrUpdate(entity);
	}

	@Override
	public List<ReportTemplate> findDefault() {
		return daoReportTemplate.findDefault();
	}

	@Override
	public ReportTemplate findByIdAndCustomerOrDefault(Long id, Integer customerId) {
		return daoReportTemplate.findByIdAndCustomerOrDefault(id, customerId);
	}

	@Override
	public boolean isUseAuthorised(Long id, Integer customerId) {
		return daoReportTemplate.isUseAuthorised(id, customerId);
	}

	@Override
	public AnalysisType findTypeById(Long id) {
		return daoReportTemplate.findTypeById(id);
	}

}
