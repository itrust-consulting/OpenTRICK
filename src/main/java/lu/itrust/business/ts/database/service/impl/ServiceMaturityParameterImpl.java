/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOMaturityParameter;
import lu.itrust.business.ts.database.service.ServiceMaturityParameter;
import lu.itrust.business.ts.model.parameter.impl.MaturityParameter;

/**
 * @author eomar
 *
 */
@Transactional(readOnly = true)
@Service
public class ServiceMaturityParameterImpl implements ServiceMaturityParameter {

	@Autowired
	private DAOMaturityParameter daoMaturiyParameter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Integer save(MaturityParameter entity) {
		return daoMaturiyParameter.save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public MaturityParameter merge(MaturityParameter entity) {
		return daoMaturiyParameter.merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Override
	public void saveOrUpdate(MaturityParameter entity) {
		daoMaturiyParameter.saveOrUpdate(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Integer> save(List<MaturityParameter> entities) {
		return daoMaturiyParameter.save(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public MaturityParameter findOne(Integer id) {
		return daoMaturiyParameter.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable, java.lang.Integer)
	 */
	@Override
	public MaturityParameter findOne(Integer id, Integer idAnalysis) {
		return daoMaturiyParameter.findOne(id, idAnalysis);
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
		return daoMaturiyParameter.belongsToAnalysis(analysisId, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return daoMaturiyParameter.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<MaturityParameter> findAll() {
		return daoMaturiyParameter.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<MaturityParameter> findAll(List<Integer> ids) {
		return daoMaturiyParameter.findAll(ids);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoMaturiyParameter.count();
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
		daoMaturiyParameter.delete(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.
	 * Object)
	 */
	@Transactional
	@Override
	public void delete(MaturityParameter entity) {
		daoMaturiyParameter.delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.List)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends MaturityParameter> entities) {
		daoMaturiyParameter.delete(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoMaturiyParameter.deleteAll();
	}

	@Override
	public List<MaturityParameter> findByAnalysisId(Integer idAnalysis) {
		return daoMaturiyParameter.findByAnalysisId(idAnalysis);
	}

	@Transactional
	@Override
	public void saveOrUpdate(List<MaturityParameter> entities) {
		daoMaturiyParameter.saveOrUpdate(entities);
	}

}
