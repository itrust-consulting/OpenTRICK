/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOSimpleParameter;
import lu.itrust.business.ts.model.parameter.impl.SimpleParameter;
import lu.itrust.business.ts.model.parameter.type.impl.ParameterType;

/**
 * @author eomar
 *
 */
@Service
@Transactional(readOnly = true)
public class ServiceSimpleParameterImpl implements lu.itrust.business.ts.database.service.ServiceSimpleParameter {

	@Autowired
	private DAOSimpleParameter daoSimpleParameter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Integer save(SimpleParameter entity) {
		return daoSimpleParameter.save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public  SimpleParameter merge(SimpleParameter entity) {
		return daoSimpleParameter.merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(SimpleParameter entity) {
		daoSimpleParameter.saveOrUpdate(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Integer> save(List<SimpleParameter> entities) {
		return daoSimpleParameter.save(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public SimpleParameter findOne(Integer id) {
		return daoSimpleParameter.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable, java.lang.Integer)
	 */
	@Override
	public SimpleParameter findOne(Integer id, Integer idAnalysis) {
		return daoSimpleParameter.findOne(id, idAnalysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#belongsToAnalysis(java.
	 * lang.Integer, java.io.Serializable)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer id) {
		return daoSimpleParameter.belongsToAnalysis(analysisId, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return daoSimpleParameter.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<SimpleParameter> findAll() {
		return daoSimpleParameter.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<SimpleParameter> findAll(List<Integer> ids) {
		return daoSimpleParameter.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoSimpleParameter.count();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Transactional
	@Override
	public void delete(Integer id) {
		daoSimpleParameter.delete(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.
	 * Object)
	 */
	@Transactional
	@Override
	public void delete(SimpleParameter entity) {
		daoSimpleParameter.delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.List)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends SimpleParameter> entities) {
		daoSimpleParameter.delete(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoSimpleParameter.deleteAll();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceSimpleParameter#
	 * findByTypeAndAnalysisId(java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<SimpleParameter> findByTypeAndAnalysisId(String type, Integer idAnalysis) {
		return daoSimpleParameter.findByTypeAndAnalysisId(type, idAnalysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceSimpleParameter#
	 * findByTypeAndAnalysisId(lu.itrust.business.ts.model.parameter.type.impl.
	 * ParameterType, java.lang.Integer)
	 */
	@Override
	public List<SimpleParameter> findByTypeAndAnalysisId(ParameterType type, Integer idAnalysis) {
		return daoSimpleParameter.findByTypeAndAnalysisId(type, idAnalysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceSimpleParameter#
	 * findByAnalysisIdAndDescription(java.lang.String, java.lang.String)
	 */
	@Override
	public SimpleParameter findByAnalysisIdAndDescription(Integer idAnalysis, String description) {
		return daoSimpleParameter.findByAnalysisIdAndDescription(idAnalysis, description);
	}

	@Override
	public List<SimpleParameter> findByAnalysisId(Integer idAnalysis) {
	
		return daoSimpleParameter.findByAnalysisId(idAnalysis);
	}

	@Override
	public SimpleParameter findByAnalysisIdAndTypeAndDescription(Integer idAnalysis, String type, String description) {
		return daoSimpleParameter.findByAnalysisIdAndTypeAndDescription(idAnalysis, type, description);
	}

	@Transactional
	@Override
	public void saveOrUpdate(List<SimpleParameter> entities) {
		daoSimpleParameter.saveOrUpdate(entities);
	}

}
