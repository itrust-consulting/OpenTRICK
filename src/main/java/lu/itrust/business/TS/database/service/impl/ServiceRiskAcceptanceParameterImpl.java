/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAORiskAcceptanceParameter;
import lu.itrust.business.TS.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
@Service
public class ServiceRiskAcceptanceParameterImpl implements ServiceRiskAcceptanceParameter {
	
	@Autowired
	private DAORiskAcceptanceParameter daoRiskAcceptanceParameter;

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Integer save(RiskAcceptanceParameter entity) {
		return daoRiskAcceptanceParameter.save(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public RiskAcceptanceParameter merge(RiskAcceptanceParameter entity) {
		return daoRiskAcceptanceParameter.merge(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.lang.Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(RiskAcceptanceParameter entity) {
		daoRiskAcceptanceParameter.saveOrUpdate(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<RiskAcceptanceParameter> entities) {
		daoRiskAcceptanceParameter.saveOrUpdate(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Integer> save(List<RiskAcceptanceParameter> entities) {
		return daoRiskAcceptanceParameter.save(entities);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.Serializable)
	 */
	@Override
	public RiskAcceptanceParameter findOne(Integer id) {
		return daoRiskAcceptanceParameter.findOne(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.Serializable, java.lang.Integer)
	 */
	@Override
	public RiskAcceptanceParameter findOne(Integer id, Integer idAnalysis) {
		return daoRiskAcceptanceParameter.findOne(id, idAnalysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#belongsToAnalysis(java.lang.Integer, java.io.Serializable)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer id) {
		return daoRiskAcceptanceParameter.belongsToAnalysis(analysisId, id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return daoRiskAcceptanceParameter.exists(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<RiskAcceptanceParameter> findAll() {
		return daoRiskAcceptanceParameter.findAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findByAnalysisId(java.lang.Integer)
	 */
	@Override
	public List<RiskAcceptanceParameter> findByAnalysisId(Integer idAnalysis) {
		return daoRiskAcceptanceParameter.findByAnalysisId(idAnalysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<RiskAcceptanceParameter> findAll(List<Integer> ids) {
		return daoRiskAcceptanceParameter.findAll(ids);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoRiskAcceptanceParameter.count();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.io.Serializable)
	 */
	@Transactional
	@Override
	public void delete(Integer id) {
		daoRiskAcceptanceParameter.delete(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(RiskAcceptanceParameter entity) {
		daoRiskAcceptanceParameter.delete(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.util.Collection)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends RiskAcceptanceParameter> entities) {
		daoRiskAcceptanceParameter.delete(entities);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoRiskAcceptanceParameter.deleteAll();
	}

}
