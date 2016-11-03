/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOImpactParameter;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
@Service
public class ServiceImpactParameterImpl implements ServiceImpactParameter {

	@Autowired
	private DAOImpactParameter daoImpactParameter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public ImpactParameter findOne(Integer id) {
		return daoImpactParameter.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#belongsToAnalysis(java.
	 * lang.Integer, java.io.Serializable)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer id) {
		return daoImpactParameter.belongsToAnalysis(analysisId, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return daoImpactParameter.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<ImpactParameter> findAll() {
		return daoImpactParameter.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<ImpactParameter> findAll(List<Integer> ids) {
		return daoImpactParameter.findAll(ids);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoImpactParameter.count();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Transactional
	@Override
	public void delete(Integer id) {
		daoImpactParameter.delete(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.lang.
	 * Object)
	 */
	@Transactional
	@Override
	public void delete(ImpactParameter entity) {
		daoImpactParameter.delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#delete(java.util.List)
	 */
	@Transactional
	@Override
	public void delete(List<? extends ImpactParameter> entities) {
		daoImpactParameter.delete(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoImpactParameter.deleteAll();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceImpactParameter#
	 * findByTypeAndAnalysisId(java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<ImpactParameter> findByTypeAndAnalysisId(String type, Integer idAnalysis) {
		return daoImpactParameter.findByTypeAndAnalysisId(type, idAnalysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceImpactParameter#
	 * findByTypeAndAnalysisId(lu.itrust.business.TS.model.scale.ScaleType,
	 * java.lang.Integer)
	 */
	@Override
	public List<ImpactParameter> findByTypeAndAnalysisId(ScaleType type, Integer idAnalysis) {
		return daoImpactParameter.findByTypeAndAnalysisId(type, idAnalysis);
	}

	@Override
	public ImpactParameter findOne(Integer id, Integer idAnalysis) {
		return daoImpactParameter.findOne(id, idAnalysis);
	}

	@Transactional
	@Override
	public Integer save(ImpactParameter entity) {
		return daoImpactParameter.save(entity);
	}

	@Transactional
	@Override
	public ImpactParameter merge(ImpactParameter entity) {
		return daoImpactParameter.merge(entity);
	}

	@Transactional
	@Override
	public void saveOrUpdate(ImpactParameter entity) {
		daoImpactParameter.saveOrUpdate(entity);
	}

	@Transactional
	@Override
	public List<Integer> save(List<ImpactParameter> entities) {
		return daoImpactParameter.save(entities);
	}

	@Override
	public List<ImpactParameter> findByAnalysisId(Integer idAnalysis) {
		return daoImpactParameter.findByAnalysisId(idAnalysis);
	}

	@Transactional
	@Override
	public void saveOrUpdate(List<ImpactParameter> entities) {
		daoImpactParameter.saveOrUpdate(entities);
	}

	@Override
	public List<String> findAcronymByTypeAndAnalysisId(String type, Integer idAnalysis) {
		return daoImpactParameter.findAcronymByTypeAndAnalysisId(type,idAnalysis);
	}

}
