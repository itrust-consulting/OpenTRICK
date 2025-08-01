/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOScaleType;
import lu.itrust.business.ts.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
@Repository
public class DAOScaleTypeImpl extends DAOHibernate implements DAOScaleType {

	/**
	 * 
	 */
	public DAOScaleTypeImpl() {
	}

	/**
	 * @param session
	 */
	public DAOScaleTypeImpl(Session session) {
		super(session);
	}

	@Override
	public ScaleType findOne(int id) {
		return getSession().get(ScaleType.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOScaleType#findOne(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ScaleType findOne(String name) {
		return (ScaleType) createQueryWithCache("From ScaleType where name = :name").setParameter("name", name).uniqueResultOptional().orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOScaleType#findByAcronym(java.lang.
	 * String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ScaleType findByAcronym(String acronym) {
		return (ScaleType) createQueryWithCache("From ScaleType where acronym = :acronym").setParameter("acronym", acronym).uniqueResultOptional().orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScaleType#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ScaleType> findAll() {
		return createQueryWithCache("From ScaleType").getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOScaleType#exists(java.lang.String)
	 */
	@Override
	public boolean exists(int id) {
		return (boolean) createQueryWithCache("Select count(*)> 0 From ScaleType where id = :id").setParameter("id", id).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOScaleType#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String name) {
		return (boolean) createQueryWithCache("Select count(*)> 0 From ScaleType where name = :name").setParameter("name", name).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOScaleType#hasAcronym(java.lang.
	 * String)
	 */
	@Override
	public boolean hasAcronym(String acronym) {
		return (boolean) createQueryWithCache("Select count(*)> 0 From ScaleType where acronym = :acronym").setParameter("acronym", acronym).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOScaleType#save(lu.itrust.business.
	 * TS.model.scale.ScaleType)
	 */
	@Override
	public int save(ScaleType scaleType) {
		return (int) getSession().save(scaleType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOScaleType#saveOrUpdate(lu.itrust.
	 * business.ts.model.scale.ScaleType)
	 */
	@Override
	public void saveOrUpdate(ScaleType scaleType) {
		getSession().saveOrUpdate(scaleType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOScaleType#delete(lu.itrust.business
	 * .ts.model.scale.ScaleType)
	 */
	@Override
	public void delete(ScaleType scaleType) {
		getSession().delete(scaleType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOScaleType#delete(java.util.List)
	 */
	@Override
	public void delete(List<Integer> scaleTypes) {
		createQuery("Delete From ScaleType where id in :scaleTypes").setParameterList("scaleTypes", scaleTypes).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScaleType#deleteAll()
	 */
	@Override
	public void deleteAll() {
		createQuery("Delete From ScaleType").executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ScaleType> findAllFree() {
		return createQuery("From ScaleType where id not in (Select type.id From Scale) order by name").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ScaleType> findAllExpect(String[] names) {
		return createQueryWithCache("From ScaleType where name not in (:names) order by name").setParameterList("names", names).getResultList();
	}

	@Override
	public ScaleType findOneByAnalysisId(Integer analysisId) {
		return createQueryWithCache("Select distinct parameter.type From Analysis analysis inner join analysis.impactParameters as parameter where analysis.id = :idAnalysis",
				ScaleType.class).setParameter("idAnalysis", analysisId).setMaxResults(1).uniqueResultOptional().orElse(null);
	}

	@Override
	public List<ScaleType> findFromAnalysis(Integer idAnalysis) {
		return createQueryWithCache("Select distinct parameter.type From Analysis analysis inner join analysis.impactParameters as parameter where analysis.id = :idAnalysis",
				ScaleType.class).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@Override
	public ScaleType findOneQualitativeByAnalysisId(Integer idAnalysis) {
		return createQueryWithCache(
						"Select distinct parameter.type From Analysis analysis inner join analysis.impactParameters as parameter where analysis.id = :idAnalysis and parameter.type.name <> :quantitative",
						ScaleType.class)
				.setParameter("idAnalysis", idAnalysis).setParameter("quantitative", Constant.DEFAULT_IMPACT_NAME).setMaxResults(1).uniqueResultOptional().orElse(null);
	}

}
