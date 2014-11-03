package lu.itrust.business.TS.database.dao.hbm;

import java.sql.Date;
import java.util.List;

import lu.itrust.business.TS.data.basic.Phase;
import lu.itrust.business.TS.database.dao.DAOPhase;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOPhaseHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
@Repository
public class DAOPhaseHBM extends DAOHibernate implements DAOPhase {

	/**
	 * Constructor: <br>
	 */
	public DAOPhaseHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOPhaseHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#get(int)
	 */
	@Override
	public Phase get(Integer id) throws Exception {
		return (Phase) getSession().get(Phase.class, id);
	}

	/**
	 * getPhaseFromAnalysisByPhaseNumber: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#getPhaseFromAnalysisByPhaseNumber(int, int)
	 */
	@Override
	public Phase getFromAnalysisByPhaseNumber(Integer IdAnalysis, Integer number) throws Exception {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis and phase.number = :phaseNumber";
		return (Phase) getSession().createQuery(query).setParameter("idAnalysis", IdAnalysis).setParameter("phaseNumber", number).uniqueResult();
	}

	/**
	 * getPhaseFromAnalysisIdByPhaseId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#getPhaseFromAnalysisIdByPhaseId(int, java.lang.Integer)
	 */
	@Override
	public Phase getFromAnalysisById(Integer idAnalysis, Integer idPhase) throws Exception {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis and phase.id = :idPhase";
		return (Phase) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idPhase", idPhase).uniqueResult();
	}

	/**
	 * canBeDeleted: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#canBeDeleted(int)
	 */
	@Override
	public boolean canBeDeleted(Integer idPhase) throws Exception {
		String query = "Select count(measure) from Measure as measure where measure.phase.id = :idPhase";
		return ((Long) getSession().createQuery(query).setParameter("idPhase", idPhase).uniqueResult()).intValue() == 0;
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#belongsToAnalysis(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer phaseId) throws Exception {
		String query = "Select count(phase) From Analysis as analysis inner join analysis.phases as phase where analysis.id = :analysisid and phase.id = :phaseId";
		return ((Long) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("phaseId", phaseId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAllPhases: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#getAllPhases()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> getAll() throws Exception {
		return getSession().createQuery("from Phase phase order by phase.number asc, phase.beginDate asc").list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#getAllFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis order by phase.number asc";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllPhasesFromAnalysisByBeginDate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#getAllPhasesFromAnalysisByBeginDate(int, java.sql.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> getAllFromAnalysisByBeginDate(Integer idAnalysis, Date beginDate) throws Exception {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis and phase.beginDate = :beginDate order by phase.number";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("beginDate", beginDate).list();
	}

	/**
	 * getAllPhasesFromAnalysisByEndDate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#getAllPhasesFromAnalysisByEndDate(int, java.sql.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> getAllFromAnalysisByEndDate(Integer idAnalysis, Date endDate) throws Exception {
		String query = "Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis and phase.endDate = :endDate order by phase.number";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("endDate", endDate).list();
	}

	/**
	 * getAllFromAnalysisActionPlan: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> getAllFromAnalysisActionPlan(Integer idAnalysis) throws Exception {
		String query =
			"Select phase from Analysis as analysis inner join analysis.phases as phase where analysis.id = :idAnalysis and phase.number in (Select DISTINCT actionplan.measure.phase.number From Analysis a inner join a.actionPlans actionplan where a.id = :idAnalysis)  order by phase.number";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#save(lu.itrust.business.TS.data.basic.Phase)
	 */
	@Override
	public void save(Phase phase) throws Exception {
		getSession().save(phase);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#saveOrUpdate(lu.itrust.business.TS.data.basic.Phase)
	 */
	@Override
	public void saveOrUpdate(Phase phase) throws Exception {
		getSession().saveOrUpdate(phase);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOPhase#delete(lu.itrust.business.TS.data.basic.Phase)
	 */
	@Override
	public void delete(Phase phase) throws Exception {
		getSession().delete(phase);
	}
}