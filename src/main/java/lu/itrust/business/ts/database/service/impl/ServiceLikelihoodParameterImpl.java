/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOLikelihoodParameter;
import lu.itrust.business.ts.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;

/**
 * @author eomar
 *
 */
@Transactional(readOnly = true)
@Service
public class ServiceLikelihoodParameterImpl implements ServiceLikelihoodParameter {

	@Autowired
	private DAOLikelihoodParameter daoLikelihoodParameter;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Integer save(LikelihoodParameter entity) {
		return daoLikelihoodParameter.save(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public LikelihoodParameter merge(LikelihoodParameter entity) {
		return daoLikelihoodParameter.merge(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(LikelihoodParameter entity) {
		daoLikelihoodParameter.saveOrUpdate(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Integer> save(List<LikelihoodParameter> entities) {
		return daoLikelihoodParameter.save(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.Serializable)
	 */
	@Override
	public LikelihoodParameter findOne(Integer id) {
		return daoLikelihoodParameter.findOne(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.Serializable, java.lang.Integer)
	 */
	@Override
	public LikelihoodParameter findOne(Integer id, Integer idAnalysis) {
		return daoLikelihoodParameter.findOne(id, idAnalysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#belongsToAnalysis(java.lang.Integer, java.io.Serializable)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer id) {
		return daoLikelihoodParameter.belongsToAnalysis(analysisId, id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return daoLikelihoodParameter.exists(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<LikelihoodParameter> findAll() {
		return daoLikelihoodParameter.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<LikelihoodParameter> findAll(List<Integer> ids) {
		return daoLikelihoodParameter.findAll(ids);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoLikelihoodParameter.count();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.Serializable)
	 */
	@Transactional
	@Override
	public void delete(Integer id) {
		daoLikelihoodParameter.delete(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(LikelihoodParameter entity) {
		daoLikelihoodParameter.delete(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.List)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends LikelihoodParameter> entities) {
		daoLikelihoodParameter.delete(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoLikelihoodParameter.deleteAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.database.service.ServiceLikelihoodParameter#findAcronymByAnalysisId(java.lang.Integer)
	 */
	@Override
	public List<String> findAcronymByAnalysisId(Integer idAnalysis) {
		return daoLikelihoodParameter.findAcronymByAnalysisId(idAnalysis);
	}

	@Override
	public List<LikelihoodParameter> findByAnalysisId(Integer idAnalysis) {
		return daoLikelihoodParameter.findByAnalysisId(idAnalysis);
	}

	@Transactional
	@Override
	public void saveOrUpdate(List<LikelihoodParameter> entities) {
		daoLikelihoodParameter.saveOrUpdate(entities);
	}

	@Override
	public Integer findMaxLevelByIdAnalysis(Integer idAnalysis) {
		return daoLikelihoodParameter.findMaxLevelByIdAnalysis(idAnalysis);
	}

}
