/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAODynamicParameter;
import lu.itrust.business.ts.database.service.ServiceDynamicParameter;
import lu.itrust.business.ts.model.parameter.impl.DynamicParameter;

/**
 * @author eomar
 *
 */
@Transactional(readOnly = true)
@Service
public class ServiceDynamicParameterImpl implements ServiceDynamicParameter {

	@Autowired
	private DAODynamicParameter daoDynamicParameter;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Integer save(DynamicParameter entity) {
		return daoDynamicParameter.save(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public DynamicParameter merge(DynamicParameter entity) {
		return daoDynamicParameter.merge(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(DynamicParameter entity) {
		daoDynamicParameter.saveOrUpdate(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Integer> save(List<DynamicParameter> entities) {
		return daoDynamicParameter.save(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.Serializable)
	 */
	@Override
	public DynamicParameter findOne(Integer id) {
		return daoDynamicParameter.findOne(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.Serializable, java.lang.Integer)
	 */
	@Override
	public DynamicParameter findOne(Integer id, Integer idAnalysis) {
		return daoDynamicParameter.findOne(id, idAnalysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#belongsToAnalysis(java.lang.Integer, java.io.Serializable)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer id) {
		return daoDynamicParameter.belongsToAnalysis(analysisId, id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return daoDynamicParameter.exists(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<DynamicParameter> findAll() {
		return daoDynamicParameter.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<DynamicParameter> findAll(List<Integer> ids) {
		return daoDynamicParameter.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoDynamicParameter.count();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.Serializable)
	 */
	@Transactional
	@Override
	public void delete(Integer id) {
		daoDynamicParameter.delete(id);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(DynamicParameter entity) {
		daoDynamicParameter.delete(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.List)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends DynamicParameter> entities) {
		daoDynamicParameter.delete(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoDynamicParameter.deleteAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceDynamicParameter#findAcronymByAnalysisId(java.lang.Integer)
	 */
	@Override
	public List<String> findAcronymByAnalysisId(Integer idAnalysis) {
		return daoDynamicParameter.findAcronymByAnalysisId(idAnalysis);
	}

	@Override
	public List<DynamicParameter> findByAnalysisId(Integer idAnalysis) {
		return daoDynamicParameter.findByAnalysisId(idAnalysis);
	}

	@Transactional
	@Override
	public void saveOrUpdate(List<DynamicParameter> entities) {
		daoDynamicParameter.saveOrUpdate(entities);
	}

}
