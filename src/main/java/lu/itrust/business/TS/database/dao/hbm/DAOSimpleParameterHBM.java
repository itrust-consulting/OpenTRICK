/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOSimpleParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.parameter.type.impl.ParameterType;

/**
 * @author eomar
 *
 */
@Repository
public class DAOSimpleParameterHBM extends DAOHibernate implements DAOSimpleParameter {

	/**
	 * 
	 */
	public DAOSimpleParameterHBM() {
	}

	/**
	 * @param session
	 */
	public DAOSimpleParameterHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Integer save(SimpleParameter entity) {
		return (Integer) getSession().save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public SimpleParameter merge(SimpleParameter entity) {
		return (SimpleParameter) getSession().merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Override
	public void saveOrUpdate(SimpleParameter entity) {
		getSession().saveOrUpdate(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Integer> save(List<SimpleParameter> entities) {
		return entities.stream().map(entity -> save(entity)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public SimpleParameter findOne(Integer id) {
		return getSession().get(SimpleParameter.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.
	 * Serializable, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SimpleParameter findOne(Integer id, Integer idAnalysis) {
		return (SimpleParameter) getSession()
				.createQuery("Select parameter From Analysis analysis inner join analysis.simpleParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id")
				.setParameter("id", id).setParameter("idAnalysis", idAnalysis).uniqueResultOptional().orElse(null);
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
		return (boolean) getSession()
				.createQuery(
						"Select count(parameter) > 0 From Analysis analysis inner join analysis.simpleParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id")
				.setParameter("id", id).setParameter("idAnalysis", analysisId).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return (boolean) getSession().createQuery("Select count(*)>0 From SimpleParameter where id = :id").setParameter("id", id).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SimpleParameter> findAll() {
		return getSession().createQuery("From SimpleParameter").getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#findAll(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SimpleParameter> findAll(List<Integer> ids) {
		return getSession().createQuery("From SimpleParameter where id in (:ids)").setParameterList("ids", ids).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return (long) getSession().createQuery("Select count(*) From SimpleParameter").getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(Integer id) {
		getSession().createQuery("Delete From ImpactParameter where id = :id").setParameter("id", id).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.lang.
	 * Object)
	 */
	@Override
	public void delete(SimpleParameter entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#delete(java.util.List)
	 */
	@Override
	public void delete(Collection<? extends SimpleParameter> entities) {
		entities.forEach(parameter -> delete(parameter));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		getSession().createQuery("Delete From ImpactParameter").executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOSimpleParameter#
	 * findByTypeAndAnalysisId(java.lang.String, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SimpleParameter> findByTypeAndAnalysisId(String type, Integer idAnalysis) {
		return getSession()
				.createQuery(
						"Select parameter From Analysis analysis inner join analysis.simpleParameters as parameter  where analysis.id = :idAnalysis and parameter.type.name = :type")
				.setParameter("type", type).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOSimpleParameter#
	 * findByTypeAndAnalysisId(lu.itrust.business.TS.model.parameter.type.impl.
	 * ParameterType, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SimpleParameter> findByTypeAndAnalysisId(ParameterType type, Integer idAnalysis) {
		return getSession()
				.createQuery(
						"Select parameter From Analysis analysis inner join analysis.simpleParameters as parameter  where analysis.id = :idAnalysis and parameter.type.name = :type")
				.setParameter("type", type).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOSimpleParameter#
	 * findByAnalysisIdAndDescription(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SimpleParameter findByAnalysisIdAndDescription(Integer idAnalysis, String description) {
		return (SimpleParameter) getSession()
				.createQuery(
						"Select parameter From Analysis analysis inner join analysis.simpleParameters as parameter  where analysis.id = :idAnalysis and parameter.description = :description")
				.setParameter("description", description).setParameter("idAnalysis", idAnalysis).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SimpleParameter> findByAnalysisId(Integer idAnalysis) {
		return getSession()
		.createQuery("Select parameter From Analysis analysis inner join analysis.simpleParameters as parameter where analysis.id = :idAnalysis").setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleParameter findByAnalysisIdAndTypeAndDescription(Integer idAnalysis, String type, String description) {
		return (SimpleParameter) getSession()
				.createQuery(
						"Select parameter From Analysis analysis inner join analysis.simpleParameters as parameter  where analysis.id = :idAnalysis and parameter.type.name = :type and parameter.description = :description").setParameter("type", type)
			.setParameter("description", description).setParameter("idAnalysis", idAnalysis).uniqueResultOptional().orElse(null);
	}

	@Override
	public void saveOrUpdate(List<SimpleParameter> entities) {
		entities.stream().forEach(entity -> saveOrUpdate(entity));
	}

}
