/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAODynamicParameter;
import lu.itrust.business.ts.model.parameter.impl.DynamicParameter;

/**
 * @author eomar
 *
 */
@Repository
public class DAODynamicParameterImpl extends DAOHibernate implements DAODynamicParameter {

	/**
	 * 
	 */
	public DAODynamicParameterImpl() {
	}

	/**
	 * @param session
	 */
	public DAODynamicParameterImpl(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Integer save(DynamicParameter entity) {
		return (Integer) getSession().save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public DynamicParameter merge(DynamicParameter entity) {
		return (DynamicParameter) getSession().merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Override
	public void saveOrUpdate(DynamicParameter entity) {
		getSession().saveOrUpdate(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Integer> save(List<DynamicParameter> entities) {
		return entities.stream().map(entity -> save(entity)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public DynamicParameter findOne(Integer id) {
		return getSession().get(DynamicParameter.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DynamicParameter findOne(Integer id, Integer idAnalysis) {
		return (DynamicParameter) createQueryWithCache("Select parameter From Analysis analysis inner join analysis.dynamicParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id")
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
						"Select count(parameter) > 0 From Analysis analysis inner join analysis.dynamicParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id")
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
		return (boolean) createQueryWithCache("Select count(*)>0 From DynamicParameter where id = :id").setParameter("id", id).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DynamicParameter> findAll() {
		return createQueryWithCache("From DynamicParameter").getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DynamicParameter> findAll(List<Integer> ids) {
		return createQueryWithCache("From DynamicParameter where id in (:ids)").setParameterList("ids", ids).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return (long) createQueryWithCache("Select count(*) From DynamicParameter").getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(Integer id) {
		createQueryWithCache("Delete From DynamicParameter where id = :id").setParameter("id", id).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.
	 * Object)
	 */
	@Override
	public void delete(DynamicParameter entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.List)
	 */
	@Override
	public void delete(Collection<? extends DynamicParameter> entities) {
		entities.forEach(parameter -> delete(parameter));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		createQueryWithCache("Delete From DynamicParameter").executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAODynamicParameter#
	 * findAcronymByAnalysisId(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAcronymByAnalysisId(Integer idAnalysis) {
		return createQueryWithCache("Select parameter.acronym From Analysis analysis inner join analysis.dynamicParameters as parameter  where analysis.id = :idAnalysis")
				.setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DynamicParameter> findByAnalysisId(Integer idAnalysis) {
		return createQueryWithCache("Select parameter From Analysis analysis inner join analysis.dynamicParameters as parameter where analysis.id = :idAnalysis")
				.setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@Override
	public void saveOrUpdate(List<DynamicParameter> entities) {
		 entities.stream().forEach(entity -> saveOrUpdate(entity));
	}

}
