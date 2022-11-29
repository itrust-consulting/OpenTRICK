/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOAnalysisShareInvitation;
import lu.itrust.business.TS.database.service.ServiceAnalysisShareInvitation;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisShareInvitation;
import lu.itrust.business.TS.model.general.helper.InvitationFilter;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Transactional(readOnly = true)
@Service
public class ServiceAnalysisShareInvitationImpl implements ServiceAnalysisShareInvitation {

	@Autowired
	private DAOAnalysisShareInvitation daoAnalysisShareInviatation;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#count()
	 */
	@Override
	public long count() {
		return daoAnalysisShareInviatation.count();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.util.
	 * Collection)
	 */
	@Transactional
	@Override
	public void delete(Collection<? extends AnalysisShareInvitation> entities) {
		daoAnalysisShareInviatation.delete(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#delete(java.io.
	 * Serializable)
	 */
	@Transactional
	@Override
	public void delete(Long id) {
		daoAnalysisShareInviatation.delete(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(AnalysisShareInvitation entity) {
		daoAnalysisShareInviatation.delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoAnalysisShareInviatation.deleteAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Long id) {
		return daoAnalysisShareInviatation.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findAll()
	 */
	@Override
	public List<AnalysisShareInvitation> findAll() {
		return daoAnalysisShareInviatation.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#findAll(java.util.List)
	 */
	@Override
	public List<AnalysisShareInvitation> findAll(List<Long> ids) {
		return daoAnalysisShareInviatation.findAll(ids);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#findByAnalysisId(java.lang.
	 * Integer)
	 */
	@Override
	public List<AnalysisShareInvitation> findByAnalysisId(Integer idAnalysis) {
		return daoAnalysisShareInviatation.findByAnalysisId(idAnalysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public AnalysisShareInvitation findOne(Long id) {
		return daoAnalysisShareInviatation.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#merge(java.lang.Object)
	 */
	@Transactional
	@Override
	public AnalysisShareInvitation merge(AnalysisShareInvitation entity) {
		return daoAnalysisShareInviatation.merge(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.util.List)
	 */
	@Transactional
	@Override
	public List<Long> save(List<AnalysisShareInvitation> entities) {
		return daoAnalysisShareInviatation.save(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.TemplateDAOService#save(java.lang.Object)
	 */
	@Transactional
	@Override
	public Long save(AnalysisShareInvitation entity) {
		return daoAnalysisShareInviatation.save(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.util.
	 * List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<AnalysisShareInvitation> entities) {
		daoAnalysisShareInviatation.save(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.TemplateDAOService#saveOrUpdate(java.lang.
	 * Object)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(AnalysisShareInvitation entity) {
		daoAnalysisShareInviatation.saveOrUpdate(entity);
	}

	@Override
	public AnalysisShareInvitation findByEmailAndAnalysisId(String email, int analysisId) {
		return daoAnalysisShareInviatation.findByEmailAndAnalysisId(email, analysisId);
	}

	@Override
	public AnalysisShareInvitation findByToken(String token) {
		return daoAnalysisShareInviatation.findByToken(token);
	}

	@Override
	public boolean exists(String token) {
		return daoAnalysisShareInviatation.exists(token);
	}

	@Transactional
	@Override
	public void deleteByUser(User user) {
		daoAnalysisShareInviatation.deleteByUser(user);
	}

	@Transactional
	@Override
	public void deleteByAnalysis(Analysis analysis) {
		daoAnalysisShareInviatation.deleteByAnalysis(analysis);
	}

	@Override
	public List<AnalysisShareInvitation> findByEmail(String email) {
		return daoAnalysisShareInviatation.findByEmail(email);
	}

	@Override
	public long countByEmail(String email) {
		return daoAnalysisShareInviatation.countByEmail(email);
	}

	@Override
	public long countByUsername(String username) {
		return daoAnalysisShareInviatation.countByUsername(username);
	}

	@Override
	public List<AnalysisShareInvitation> findAllByUsernameAndFilterControl(String username, Integer page, InvitationFilter filter) {
		return daoAnalysisShareInviatation.findAllByUsernameAndFilterControl(username, page, filter);
	}

	@Override
	public AnalysisShareInvitation findByIdAndUsername(Long id, String username) {
		return daoAnalysisShareInviatation.findByIdAndUsername(id, username);
	}

	@Override
	public String findTokenByIdAndUsername(Long id, String username) {
		return daoAnalysisShareInviatation.findTokenByIdAndUsername(id, username);
	}

}
