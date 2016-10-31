/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAODynamicParameter;
import lu.itrust.business.TS.database.service.ServiceDynamicParameter;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;

/**
 * @author eomar
 *
 */
@Service
public class ServiceDynamicParameterImpl implements ServiceDynamicParameter {

	@Autowired
	private DAODynamicParameter daoDynamicParameter;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Integer save(DynamicParameter entity) {
		return daoDynamicParameter.save(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public DynamicParameter merge(DynamicParameter entity) {
		return daoDynamicParameter.merge(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.lang.Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(DynamicParameter entity) {
		daoDynamicParameter.saveOrUpdate(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Integer> save(List<DynamicParameter> entities) {
		return daoDynamicParameter.save(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.Serializable)
	 */
	@Override
	public DynamicParameter findOne(Integer id) {
		return daoDynamicParameter.findOne(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.Serializable, java.lang.Integer)
	 */
	@Override
	public DynamicParameter findOne(Integer id, Integer idAnalysis) {
		return daoDynamicParameter.findOne(id, idAnalysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#belongsToAnalysis(java.lang.Integer, java.io.Serializable)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer id) {
		return daoDynamicParameter.belongsToAnalysis(analysisId, id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return daoDynamicParameter.exists(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<DynamicParameter> findAll() {
		return daoDynamicParameter.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<DynamicParameter> findAll(List<Integer> ids) {
		return daoDynamicParameter.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoDynamicParameter.count();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.io.Serializable)
	 */
	@Transactional
	@Override
	public void delete(Integer id) {
		daoDynamicParameter.delete(id);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(DynamicParameter entity) {
		daoDynamicParameter.delete(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.util.List)
	 */
	@Transactional
	@Override
	public void delete(List<? extends DynamicParameter> entities) {
		daoDynamicParameter.delete(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoDynamicParameter.deleteAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceDynamicParameter#findAcronymByAnalysisId(java.lang.Integer)
	 */
	@Override
	public List<String> findAcronymByAnalysisId(Integer idAnalysis) {
		return daoDynamicParameter.findAcronymByAnalysisId(idAnalysis);
	}

	@Override
	public List<DynamicParameter> findByAnalysisId(Integer idAnalysis) {
		return daoDynamicParameter.findByAnalysisId(idAnalysis);
	}

}
