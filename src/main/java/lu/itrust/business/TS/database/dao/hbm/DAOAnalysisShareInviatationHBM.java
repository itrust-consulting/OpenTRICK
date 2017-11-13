/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOAnalysisShareInvitation;
import lu.itrust.business.TS.model.analysis.AnalysisShareInvitation;

/**
 * @author eomar
 *
 */
@Repository
public class DAOAnalysisShareInviatationHBM extends DAOHibernate implements DAOAnalysisShareInvitation {

	/**
	 * 
	 */
	public DAOAnalysisShareInviatationHBM() {
	}

	/**
	 * @param session
	 */
	public DAOAnalysisShareInviatationHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return getSession().createQuery("Select count(*) From AnalysisShareInvitation", Long.class).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.util.
	 * Collection)
	 */
	@Override
	public void delete(Collection<? extends AnalysisShareInvitation> entities) {
		entities.forEach(entity -> delete(entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(Long id) {
		AnalysisShareInvitation entity = findOne(id);
		if (entity != null)
			delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Override
	public void delete(AnalysisShareInvitation entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		getSession().createQuery("Delete From AnalysisShareInvitation").executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return getSession().createQuery("Select count(*) > 0 From AnalysisShareInvitation where id = :id", Boolean.class).setParameter("id", id).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<AnalysisShareInvitation> findAll() {
		return getSession().createQuery("From AnalysisShareInvitation", AnalysisShareInvitation.class).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<AnalysisShareInvitation> findAll(List<Long> ids) {
		return ids.isEmpty() ? Collections.emptyList()
				: getSession().createQuery("From AnalysisShareInvitation where id = :ids", AnalysisShareInvitation.class).setParameterList("ids", ids).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public AnalysisShareInvitation findOne(Long id) {
		return getSession().get(AnalysisShareInvitation.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public AnalysisShareInvitation merge(AnalysisShareInvitation entity) {
		return (AnalysisShareInvitation) getSession().merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Long> save(List<AnalysisShareInvitation> entities) {
		return entities.stream().map(entity -> save(entity)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Long save(AnalysisShareInvitation entity) {
		return (Long) getSession().save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.util.
	 * List)
	 */
	@Override
	public void saveOrUpdate(List<AnalysisShareInvitation> entities) {
		entities.forEach(entity -> saveOrUpdate(entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Override
	public void saveOrUpdate(AnalysisShareInvitation entity) {
		getSession().saveOrUpdate(entity);
	}

	@Override
	public List<AnalysisShareInvitation> findByAnalysisId(Integer idAnalysis) {
		return getSession().createQuery("From AnalysisShareInvitation where analysis.id = :idAnalysis order by email", AnalysisShareInvitation.class)
				.setParameter("idAnalysis", idAnalysis).list();
	}

	@Override
	public AnalysisShareInvitation findByEmailAndAnalysisId(String email, int analysisId) {
		return getSession().createQuery("From AnalysisShareInvitation where analysis.id = :idAnalysis and email = :email", AnalysisShareInvitation.class)
				.setParameter("idAnalysis", analysisId).setParameter("email", email).uniqueResult();
	}

	@Override
	public AnalysisShareInvitation findByToken(String token) {
		return getSession().createQuery("From AnalysisShareInvitation where token = :token", AnalysisShareInvitation.class).setParameter("token", token).uniqueResult();
	}

	@Override
	public boolean exists(String token) {
		return getSession().createQuery("Select count(*)>0 From AnalysisShareInvitation where token = :token", Boolean.class).setParameter("token", token).uniqueResult();
	}

}
