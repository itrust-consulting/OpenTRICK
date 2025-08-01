/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOAnalysisShareInvitation;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisShareInvitation;
import lu.itrust.business.ts.model.general.helper.InvitationFilter;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
@Repository
public class DAOAnalysisShareInviatationImpl extends DAOHibernate implements DAOAnalysisShareInvitation {

	/**
	 * 
	 */
	public DAOAnalysisShareInviatationImpl() {
	}

	/**
	 * @param session
	 */
	public DAOAnalysisShareInviatationImpl(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return createQueryWithCache("Select count(*) From AnalysisShareInvitation", Long.class).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.util.
	 * Collection)
	 */
	@Override
	public void delete(Collection<? extends AnalysisShareInvitation> entities) {
		entities.forEach(entity -> delete(entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#delete(java.io.
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
	 * lu.itrust.business.ts.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Override
	public void delete(AnalysisShareInvitation entity) {
		getSession().delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#deleteAll()
	 */
	@Override
	public void deleteAll() {
		createQuery("Delete From AnalysisShareInvitation").executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return createQueryWithCache("Select count(*) > 0 From AnalysisShareInvitation where id = :id", Boolean.class).setParameter("id", id).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<AnalysisShareInvitation> findAll() {
		return createQueryWithCache("From AnalysisShareInvitation", AnalysisShareInvitation.class).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<AnalysisShareInvitation> findAll(List<Long> ids) {
		return ids.isEmpty() ? Collections.emptyList()
				: createQueryWithCache("From AnalysisShareInvitation where id = :ids", AnalysisShareInvitation.class).setParameterList("ids", ids).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#findOne(java.io.
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
	 * lu.itrust.business.ts.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Override
	public AnalysisShareInvitation merge(AnalysisShareInvitation entity) {
		return (AnalysisShareInvitation) getSession().merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.util.List)
	 */
	@Override
	public List<Long> save(List<AnalysisShareInvitation> entities) {
		return entities.stream().map(entity -> save(entity)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Override
	public Long save(AnalysisShareInvitation entity) {
		return (Long) getSession().save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.util.
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
	 * lu.itrust.business.ts.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Override
	public void saveOrUpdate(AnalysisShareInvitation entity) {
		getSession().saveOrUpdate(entity);
	}

	@Override
	public List<AnalysisShareInvitation> findByAnalysisId(Integer idAnalysis) {
		return createQueryWithCache("From AnalysisShareInvitation where analysis.id = :idAnalysis order by email", AnalysisShareInvitation.class)
				.setParameter("idAnalysis", idAnalysis).list();
	}

	@Override
	public AnalysisShareInvitation findByEmailAndAnalysisId(String email, int analysisId) {
		return createQueryWithCache("From AnalysisShareInvitation where analysis.id = :idAnalysis and email = :email", AnalysisShareInvitation.class)
				.setParameter("idAnalysis", analysisId).setParameter("email", email).uniqueResult();
	}

	@Override
	public AnalysisShareInvitation findByToken(String token) {
		return createQueryWithCache("From AnalysisShareInvitation where token = :token", AnalysisShareInvitation.class).setParameter("token", token).uniqueResult();
	}

	@Override
	public boolean exists(String token) {
		return createQueryWithCache("Select count(*)>0 From AnalysisShareInvitation where token = :token", Boolean.class).setParameter("token", token).uniqueResult();
	}

	@Override
	public void deleteByUser(User user) {
		createQueryWithCache("Delete From AnalysisShareInvitation where host = :user").setParameter("user", user).executeUpdate();
	}

	@Override
	public void deleteByAnalysis(Analysis analysis) {
		createQueryWithCache("Delete From AnalysisShareInvitation where analysis = :analysis").setParameter("analysis", analysis).executeUpdate();
	}

	@Override
	public List<AnalysisShareInvitation> findByEmail(String email) {
		return createQueryWithCache("From AnalysisShareInvitation where email = :email", AnalysisShareInvitation.class).setParameter("email", email).list();
	}

	@Override
	public long countByEmail(String email) {
		return createQueryWithCache("Select count(*) From AnalysisShareInvitation where email = :email", Long.class).setParameter("email", email).uniqueResult();
	}

	@Override
	public long countByUsername(String username) {
		return createQueryWithCache("Select count(invitation) From AnalysisShareInvitation as invitation, User as user where invitation.email = user.email and user.login = :username",
						Long.class)
				.setParameter("username", username).uniqueResult();
	}

	@Override
	public List<AnalysisShareInvitation> findAllByUsernameAndFilterControl(String username, Integer page, InvitationFilter filter) {
		if (!filter.validate())
			return Collections.emptyList();
		String query = String.format(
				"Select invitation From AnalysisShareInvitation as invitation, User as user where invitation.email = user.email and user.login = :username order by invitation.%s %s",
				filter.getSort(), filter.getDirection());
		return createQueryWithCache(query, AnalysisShareInvitation.class).setParameter("username", username).setFirstResult((page - 1) * filter.getSize())
				.setMaxResults(filter.getSize()).getResultList();

	}

	@Override
	public AnalysisShareInvitation findByIdAndUsername(Long id, String username) {
		return createQueryWithCache(
				"Select invitation From AnalysisShareInvitation as invitation, User as user where invitation.id = :id and invitation.email = user.email and user.login = :username",
				AnalysisShareInvitation.class).setParameter("id", id).setParameter("username", username).uniqueResult();
	}

	@Override
	public String findTokenByIdAndUsername(Long id, String username) {
		return createQueryWithCache(
				"Select invitation.token From AnalysisShareInvitation as invitation, User as user where invitation.id = :id and invitation.email = user.email and user.login = :username",
				String.class).setParameter("id", id).setParameter("username", username).uniqueResult();
	}

}
