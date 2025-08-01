package lu.itrust.business.ts.database.dao.impl;

import java.sql.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOPhase;
import lu.itrust.business.ts.model.general.Phase;

/**
 * DAOPhaseImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
@Repository
public class DAOPhaseImpl extends DAOHibernate implements DAOPhase {

	/**
	 * Constructor: <br>
	 */
	public DAOPhaseImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOPhaseImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#get(int)
	 */
	@Override
	public Phase get(Integer id) {
		return (Phase) getSession().get(Phase.class, id);
	}

	/**
	 * getPhaseFromAnalysisByPhaseNumber: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#getPhaseFromAnalysisByPhaseNumber(int,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Phase getFromAnalysisByPhaseNumber(Integer IdAnalysis, Integer number) {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis and phase.number = :phaseNumber";
		return (Phase) createQueryWithCache(query).setParameter("idAnalysis", IdAnalysis).setParameter("phaseNumber", number).uniqueResultOptional().orElse(null);
	}

	/**
	 * getPhaseFromAnalysisIdByPhaseId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#getPhaseFromAnalysisIdByPhaseId(int,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Phase getFromAnalysisById(Integer idAnalysis, Integer idPhase) {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis and phase.id = :idPhase";
		return (Phase) createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).setParameter("idPhase", idPhase).uniqueResultOptional().orElse(null);
	}

	/**
	 * canBeDeleted: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#canBeDeleted(int)
	 */
	@Override
	public boolean canBeDeleted(Integer idPhase) {
		String query = "Select count(measure) = 0 from Measure as measure where measure.phase.id = :idPhase";
		return (boolean) createQueryWithCache(query, Boolean.class).setParameter("idPhase", idPhase).getSingleResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer phaseId) {
		String query = "Select count(phase) > 0 From Analysis as analysis inner join analysis.phases as phase where analysis.id = :analysisid and phase.id = :phaseId";
		return (boolean) createQueryWithCache(query).setParameter("analysisid", analysisId).setParameter("phaseId", phaseId).getSingleResult();
	}

	/**
	 * getAllPhases: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#getAllPhases()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> getAll() {
		return createQueryWithCache("from Phase phase order by phase.number asc, phase.beginDate asc").getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#getAllFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> getAllFromAnalysis(Integer idAnalysis) {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis order by phase.number asc";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getAllPhasesFromAnalysisByBeginDate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#getAllPhasesFromAnalysisByBeginDate(int,
	 *      java.sql.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> getAllFromAnalysisByBeginDate(Integer idAnalysis, Date beginDate) {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis and phase.beginDate = :beginDate order by phase.number";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).setParameter("beginDate", beginDate).getResultList();
	}

	/**
	 * getAllPhasesFromAnalysisByEndDate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#getAllPhasesFromAnalysisByEndDate(int,
	 *      java.sql.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> getAllFromAnalysisByEndDate(Integer idAnalysis, Date endDate) {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis and phase.endDate = :endDate order by phase.number";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).setParameter("endDate", endDate).getResultList();
	}

	/**
	 * getAllFromAnalysisActionPlan: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> getAllFromAnalysisActionPlan(Integer idAnalysis) {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis and phase.number in (Select DISTINCT actionplan.measure.phase.number From Analysis a inner join a.actionPlans actionplan where a.id = :idAnalysis)  order by phase.number";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#save(lu.itrust.business.ts.model.general.Phase)
	 */
	@Override
	public void save(Phase phase) {
		getSession().save(phase);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#saveOrUpdate(lu.itrust.business.ts.model.general.Phase)
	 */
	@Override
	public void saveOrUpdate(Phase phase) {
		getSession().saveOrUpdate(phase);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOPhase#delete(lu.itrust.business.ts.model.general.Phase)
	 */
	@Override
	public void delete(Phase phase) {
		getSession().delete(phase);
	}

	@Override
	public Phase findAllByIdAnalysis(Integer idAnalysis) {
		return createQueryWithCache("From Phase phase where analysis.id = :idAnalysis order by number desc", Phase.class).setParameter("idAnalysis", idAnalysis)
				.setMaxResults(1).uniqueResult();
	}
}