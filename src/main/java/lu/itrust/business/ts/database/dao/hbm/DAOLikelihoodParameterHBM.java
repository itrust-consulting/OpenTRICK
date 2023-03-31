/**
 * 
 */
package lu.itrust.business.ts.database.dao.hbm;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOLikelihoodParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;

/**
 * @author eomar
 *
 */
@Repository
public class DAOLikelihoodParameterHBM extends DAOHibernate implements DAOLikelihoodParameter {

	/**
	 * 
	 */
	public DAOLikelihoodParameterHBM() {
	}

	/**
	 * @param session
	 */
	public DAOLikelihoodParameterHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Integer save(LikelihoodParameter entity) {
		return (Integer) getSession().save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public LikelihoodParameter merge(LikelihoodParameter entity) {
		return (LikelihoodParameter) getSession().merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Override
	public void saveOrUpdate(LikelihoodParameter entity) {
		getSession().saveOrUpdate(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Integer> save(List<LikelihoodParameter> entities) {
		return entities.stream().map(entity -> save(entity)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public LikelihoodParameter findOne(Integer id) {
		return getSession().get(LikelihoodParameter.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
	 * Serializable, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LikelihoodParameter findOne(Integer id, Integer idAnalysis) {
		return (LikelihoodParameter) getSession()
				.createQuery("Select parameter From Analysis analysis inner join analysis.likelihoodParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id")
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
		return (boolean) getSession()
				.createQuery(
						"Select count(parameter) > 0 From Analysis analysis inner join analysis.likelihoodParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id")
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
		return (boolean) getSession().createQuery("Select count(*)>0 From LikelihoodParameter where id = :id").setParameter("id", id).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LikelihoodParameter> findAll() {
		return getSession().createQuery("From LikelihoodParameter").getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LikelihoodParameter> findAll(List<Integer> ids) {
		return getSession().createQuery("From LikelihoodParameter where id in (:ids) order by level").setParameterList("ids", ids).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return (long) getSession().createQuery("Select count(*) From LikelihoodParameter").getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(Integer id) {
		getSession().createQuery("Delete From LikelihoodParameter where id = :id").setParameter("id", id).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.
	 * Object)
	 */
	@Override
	public void delete(LikelihoodParameter entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.List)
	 */
	@Override
	public void delete(Collection<? extends LikelihoodParameter> entities) {
		entities.forEach(parameter -> delete(parameter));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		getSession().createQuery("Delete From LikelihoodParameter").executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLikelihoodParameter#
	 * findAcronymByAnalysisId(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAcronymByAnalysisId(Integer idAnalysis) {
		return getSession().createQuery("Select parameter.acronym From Analysis analysis inner join analysis.likelihoodParameters as parameter  where analysis.id = :idAnalysis")
				.setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LikelihoodParameter> findByAnalysisId(Integer idAnalysis) {
		return getSession()
				.createQuery(
						"Select parameter From Analysis analysis inner join analysis.likelihoodParameters as parameter  where analysis.id = :idAnalysis order by parameter.level asc")
				.setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@Override
	public void saveOrUpdate(List<LikelihoodParameter> entities) {
		entities.stream().forEach(entity -> saveOrUpdate(entity));
	}

	@Override
	public Integer findMaxLevelByIdAnalysis(Integer idAnalysis) {
		return getSession().createQuery("Select max(parameter.level) From Analysis analysis inner join analysis.likelihoodParameters as parameter  where analysis.id = :idAnalysis",
				Integer.class).setParameter("idAnalysis", idAnalysis).getSingleResult();
	}

}
