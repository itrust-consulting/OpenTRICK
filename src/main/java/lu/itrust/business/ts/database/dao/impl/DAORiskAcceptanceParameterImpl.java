/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAORiskAcceptanceParameter;
import lu.itrust.business.ts.model.parameter.impl.RiskAcceptanceParameter;

/**
 * @author eomar
 *
 */
@Repository
public class DAORiskAcceptanceParameterImpl extends DAOHibernate implements DAORiskAcceptanceParameter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Integer save(RiskAcceptanceParameter entity) {
		return (Integer) getSession().save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public RiskAcceptanceParameter merge(RiskAcceptanceParameter entity) {
		return (RiskAcceptanceParameter) getSession().merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Override
	public void saveOrUpdate(RiskAcceptanceParameter entity) {
		getSession().saveOrUpdate(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.util.
	 * List)
	 */
	@Override
	public void saveOrUpdate(List<RiskAcceptanceParameter> entities) {
		entities.forEach(entity -> saveOrUpdate(entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Integer> save(List<RiskAcceptanceParameter> entities) {
		return entities.stream().map(entity -> save(entity)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public RiskAcceptanceParameter findOne(Integer id) {
		return getSession().get(RiskAcceptanceParameter.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable, java.lang.Integer)
	 */
	@Override
	public RiskAcceptanceParameter findOne(Integer id, Integer idAnalysis) {
		return createQueryWithCache(
				"Select parameter From Analysis as analysis inner join analysis.riskAcceptanceParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id",
				RiskAcceptanceParameter.class).setParameter("idAnalysis", idAnalysis).setParameter("id", id).uniqueResultOptional().orElse(null);
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
		return createQueryWithCache(
				"Select count(parameter)>0 From Analysis as analysis inner join analysis.riskAcceptanceParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id",
				Boolean.class).setParameter("idAnalysis", analysisId).setParameter("id", id).uniqueResultOptional().orElse(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return createQueryWithCache("Select count(parameter)>0 From RiskAcceptanceParameter as parameter where parameter.id = :id", Boolean.class).setParameter("id", id)
				.uniqueResultOptional().orElse(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<RiskAcceptanceParameter> findAll() {
		return createQueryWithCache("From RiskAcceptanceParameter", RiskAcceptanceParameter.class).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findByAnalysisId(java.
	 * lang.Integer)
	 */
	@Override
	public List<RiskAcceptanceParameter> findByAnalysisId(Integer idAnalysis) {
		return createQueryWithCache(
				"Select parameter From Analysis as analysis inner join analysis.riskAcceptanceParameters as parameter where analysis.id = :idAnalysis order by parameter.value,parameter.color, parameter.label, parameter.description",
				RiskAcceptanceParameter.class).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<RiskAcceptanceParameter> findAll(List<Integer> ids) {
		if (ids.isEmpty())
			return Collections.emptyList();
		return createQueryWithCache("From RiskAcceptanceParameter where id in (ids) order by value,color, label, description", RiskAcceptanceParameter.class)
				.setParameterList("ids", ids).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return createQueryWithCache("Select count(*) From RiskAcceptanceParameter", Long.class).uniqueResultOptional().orElse(0L);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(Integer id) {
		createQueryWithCache("Delete From RiskAcceptanceParameter where id = :id").setParameter("id", id).executeUpdate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.
	 * Object)
	 */
	@Override
	public void delete(RiskAcceptanceParameter entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.
	 * Collection)
	 */
	@Override
	public void delete(Collection<? extends RiskAcceptanceParameter> entities) {
		entities.forEach(entity -> delete(entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		createQueryWithCache("Delete From SimpleParameter").executeUpdate();

	}

	@Override
	public boolean existsByAnalysisId(Integer analysisId) {
		return createQueryWithCache(
				"Select count(parameter) > 0 From Analysis as analysis inner join analysis.riskAcceptanceParameters as parameter where analysis.id = :idAnalysis",
				Boolean.class).setParameter("idAnalysis", analysisId).uniqueResult();
	}

}
