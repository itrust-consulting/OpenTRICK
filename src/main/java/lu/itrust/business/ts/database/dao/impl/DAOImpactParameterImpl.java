/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOImpactParameter;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
@Repository
public class DAOImpactParameterImpl extends DAOHibernate implements DAOImpactParameter {

	/**
	 * 
	 */
	public DAOImpactParameterImpl() {
	}

	/**
	 * @param session
	 */
	public DAOImpactParameterImpl(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Integer save(ImpactParameter entity) {
		return (Integer) getSession().save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Integer> save(List<ImpactParameter> entities) {
		return entities.stream().map(entity -> save(entity)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public ImpactParameter findOne(Integer id) {
		return getSession().get(ImpactParameter.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ImpactParameter findOne(Integer id, Integer idAnalysis) {
		return (ImpactParameter) createQueryWithCache("Select parameter From Analysis analysis inner join analysis.impactParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id")
				.setParameter("id", id).setParameter("idAnalysis", idAnalysis).uniqueResultOptional().orElse(null);
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
		return (boolean) createQueryWithCache(
						"Select count(parameter) > 0 From Analysis analysis inner join analysis.impactParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id")
				.setParameter("id", id).setParameter("idAnalysis", analysisId).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return (boolean) createQueryWithCache("Select count(*)>0 From ImpactParameter where id = :id").setParameter("id", id).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ImpactParameter> findAll() {
		return createQueryWithCache("From ImpactParameter").getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ImpactParameter> findAll(List<Integer> ids) {
		return createQueryWithCache("From ImpactParameter where id in (:ids) order by type, level").setParameterList("ids", ids).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return (long) createQueryWithCache("Select count(*) From ImpactParameter").getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(Integer id) {
		createQueryWithCache("Delete From ImpactParameter where id = :id").setParameter("id", id).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.
	 * Object)
	 */
	@Override
	public void delete(ImpactParameter entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.List)
	 */
	@Override
	public void delete(Collection<? extends ImpactParameter> entities) {
		entities.forEach(this::delete);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		createQueryWithCache("Delete From ImpactParameter").executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOImpactParameter#
	 * findByTypeAndAnalysisId(java.lang.String, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ImpactParameter> findByTypeAndAnalysisId(String type, Integer idAnalysis) {
		return createQueryWithCache(
						"Select parameter From Analysis analysis inner join analysis.impactParameters as parameter  where analysis.id = :idAnalysis and parameter.type.name = :type order by parameter.level")
				.setParameter("type", type).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOImpactParameter#
	 * findByTypeAndAnalysisId(lu.itrust.business.ts.model.scale.ScaleType,
	 * java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ImpactParameter> findByTypeAndAnalysisId(ScaleType type, Integer idAnalysis) {
		return createQueryWithCache(
						"Select parameter From Analysis analysis inner join analysis.impactParameters as parameter  where analysis.id = :idAnalysis and parameter.type = :type order by parameter.level")
				.setParameter("type", type).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@Override
	public ImpactParameter merge(ImpactParameter entity) {
		return (ImpactParameter) getSession().merge(entity);
	}

	@Override
	public void saveOrUpdate(ImpactParameter entity) {
		getSession().saveOrUpdate(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findByAnalysisId(java.
	 * lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ImpactParameter> findByAnalysisId(Integer idAnalysis) {
		return createQueryWithCache("Select parameter From Analysis analysis inner join analysis.impactParameters as parameter  where analysis.id = :idAnalysis order by parameter.level")
				.setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@Override
	public void saveOrUpdate(List<ImpactParameter> entities) {
		entities.stream().forEach(entity -> saveOrUpdate(entity));
	}

	@Override
	public List<String> findAcronymByTypeAndAnalysisId(String type, Integer idAnalysis) {
		return createQueryWithCache(
				"Select parameter.acronym From Analysis analysis inner join analysis.impactParameters as parameter  where analysis.id = :idAnalysis and parameter.type.name = :type",
				String.class).setParameter("type", type).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@Override
	public List<ImpactParameter> findByIdAnalysisAndLevel(Integer idAnalysis, Integer level) {
		return createQueryWithCache(
				"Select parameter From Analysis analysis inner join analysis.impactParameters as parameter  where analysis.id = :idAnalysis and parameter.level = :level",
				ImpactParameter.class).setParameter("level", level).setParameter("idAnalysis", idAnalysis).getResultList();
	}

}
