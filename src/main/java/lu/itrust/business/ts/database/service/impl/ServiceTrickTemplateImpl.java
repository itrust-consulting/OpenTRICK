/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOTrickTemplate;
import lu.itrust.business.ts.database.service.ServiceTrickTemplate;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplate;

/**
 * @author eomar
 *
 */
@Service
@Transactional(readOnly = true)
public class ServiceTrickTemplateImpl implements ServiceTrickTemplate {
	
	@Autowired
	private DAOTrickTemplate daoTrickTemplate;

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.template.TemplateReportTemplate#findByIdAndCustomer(long, int)
	 */
	@Override
	public TrickTemplate findByIdAndCustomer(long id, int customerId) {
		return daoTrickTemplate.findByIdAndCustomer(id, customerId);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.template.TemplateReportTemplate#findByCustomer(int)
	 */
	@Override
	public List<TrickTemplate> findByCustomer(int customerId) {
		return daoTrickTemplate.findByCustomer(customerId);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.template.TemplateReportTemplate#findByCustomerAndType(int, lu.itrust.business.ts.model.analysis.AnalysisType)
	 */
	@Override
	public List<TrickTemplate> findByCustomerAndType(int customerId, AnalysisType type) {
		return daoTrickTemplate.findByCustomerAndType(customerId, type);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoTrickTemplate.count();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.Collection)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends TrickTemplate> entities) {
		daoTrickTemplate.delete(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.Serializable)
	 */
	@Transactional
	@Override
	public void delete(Long id) {
		daoTrickTemplate.delete(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(TrickTemplate entity) {
		daoTrickTemplate.delete(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoTrickTemplate.deleteAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return daoTrickTemplate.exists(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<TrickTemplate> findAll() {
		return daoTrickTemplate.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<TrickTemplate> findAll(List<Long> ids) {
		return daoTrickTemplate.findAll(ids);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.Serializable)
	 */
	@Override
	public TrickTemplate findOne(Long id) {
		return daoTrickTemplate.findOne(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public TrickTemplate merge(TrickTemplate entity) {
		return daoTrickTemplate.merge(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Long> save(List<TrickTemplate> entities) {
		return daoTrickTemplate.save(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Long save(TrickTemplate entity) {
		return daoTrickTemplate.save(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<TrickTemplate> entities) {
		daoTrickTemplate.saveOrUpdate(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(TrickTemplate entity) {
		daoTrickTemplate.saveOrUpdate(entity);
	}

	@Override
	public List<TrickTemplate> findDefault() {
		return daoTrickTemplate.findDefault();
	}

	@Override
	public TrickTemplate findByIdAndCustomerOrDefault(Long id, Integer customerId) {
		return daoTrickTemplate.findByIdAndCustomerOrDefault(id, customerId);
	}

	@Override
	public boolean isUseAuthorised(Long id, Integer customerId) {
		return daoTrickTemplate.isUseAuthorised(id, customerId);
	}

	@Override
	public AnalysisType findTypeById(Long id) {
		return daoTrickTemplate.findTypeById(id);
	}

}
