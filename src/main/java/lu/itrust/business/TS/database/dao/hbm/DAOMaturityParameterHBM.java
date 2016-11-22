/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOMaturityParameter;
import lu.itrust.business.TS.model.parameter.impl.MaturityParameter;

/**
 * @author eomar
 *
 */
@Repository
public class DAOMaturityParameterHBM extends DAOHibernate implements DAOMaturityParameter {
	
	/**
	 * 
	 */
	public DAOMaturityParameterHBM() {
	}

	/**
	 * @param session
	 */
	public DAOMaturityParameterHBM(Session session) {
		super(session);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Integer save(MaturityParameter entity) {
		return (Integer) getSession().save(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public MaturityParameter merge(MaturityParameter entity) {
		return (MaturityParameter) getSession().merge(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.lang.Object)
	 */
	@Override
	public void saveOrUpdate(MaturityParameter entity) {
		getSession().saveOrUpdate(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Integer> save(List<MaturityParameter> entities) {
		return entities.stream().map(entity -> save(entity)).collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.Serializable)
	 */
	@Override
	public MaturityParameter findOne(Integer id) {
		return getSession().get(MaturityParameter.class, id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.Serializable, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MaturityParameter findOne(Integer id, Integer idAnalysis) {
		return (MaturityParameter) getSession()
			.createQuery("Select parameter From Analysis analysis inner join analysis.maturityParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id")
			.setParameter("id", id).setParameter("idAnalysis", idAnalysis).uniqueResultOptional().orElse(null);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#belongsToAnalysis(java.lang.Integer, java.io.Serializable)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer id) {
		return (boolean) getSession()
		.createQuery(
				"Select count(parameter) > 0 From Analysis analysis inner join analysis.maturityParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id")
		.setParameter("id", id).setParameter("idAnalysis", analysisId).getSingleResult();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#exists(java.io.Serializable)
	 */
	@Override
	public boolean exists(Integer id) {
		return (boolean) getSession().createQuery("Select count(*)>0 From MaturityParameter where id = :id").setParameter("id", id).getSingleResult();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MaturityParameter> findAll() {
		return getSession().createQuery("From MaturityParameter").getResultList();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MaturityParameter> findAll(List<Integer> ids) {
		return getSession().createQuery("From MaturityParameter where id in (:ids)").setParameterList("ids", ids).getResultList();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return (long) getSession().createQuery("Select count(*) From MaturityParameter").getSingleResult();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.io.Serializable)
	 */
	@Override
	public void delete(Integer id) {
		getSession().createQuery("Delete From MaturityParameter where id = :id").setParameter("id", id).executeUpdate();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Override
	public void delete(MaturityParameter entity) {
		getSession().delete(entity);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.util.List)
	 */
	@Override
	public void delete(List<? extends MaturityParameter> entities) {
		entities.forEach(parameter -> delete(parameter));
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		getSession().createQuery("Delete From MaturityParameter").executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaturityParameter> findByAnalysisId(Integer idAnalysis) {
		return getSession()
		.createQuery("Select parameter From Analysis analysis inner join analysis.maturityParameters as parameter where analysis.id = :idAnalysis").setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@Override
	public void saveOrUpdate(List<MaturityParameter> entities) {
		entities.stream().forEach(entity -> saveOrUpdate(entity));
	}

}
