package lu.itrust.business.dao.hbm;

import java.sql.Date;
import java.util.List;

import lu.itrust.business.TS.Phase;
import lu.itrust.business.dao.DAOPhase;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOPhaseHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Feb 1, 2013
 */
@Repository
public class DAOPhaseHBM extends DAOHibernate implements DAOPhase {

	/**
	 * 
	 */
	public DAOPhaseHBM() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sessionFactory
	 */
	public DAOPhaseHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOPhase#get(int)
	 */
	@Override
	public Phase get(int id) throws Exception {
		return (Phase) getSession().get(Phase.class, id);
	}

	/**
	 * loadFromPhaseNumberAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOPhase#loadFromPhaseNumberAnalysis(int,
	 *      lu.itrust.business.TS.Analysis)
	 */
	@Override
	public Phase loadFromPhaseNumberAnalysis(int number, int IdAnalysis)
			throws Exception {
		return (Phase) getSession()
				.createQuery(
						"Select phase from Analysis as analysis inner join analysis.usedPhases as phase where analysis.id = :idAnalysis and phase.number=:phaseNumber")
				.setParameter("idAnalysis", IdAnalysis)
				.setParameter("phaseNumber", number).uniqueResult();
	}

	/**
	 * loadByBeginDate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOPhase#loadByBeginDate(java.sql.Date,
	 *      lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> loadByBeginDate(Date beginDate, int idAnalysis)
			throws Exception {
		return getSession()
				.createQuery(
						"Select phase from Analysis as analysis inner join analysis.usedPhases as phase where analysis.id = :idAnalysis and phase.beginDate = :beginDate order by phase.number")
				.setParameter("idAnalysis", idAnalysis)
				.setParameter("beginDate", beginDate).list();
	}

	/**
	 * loadByEndDate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOPhase#loadByEndDate(java.sql.Date,
	 *      lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> loadByEndDate(Date endDate, int idAnalysis)
			throws Exception {
		return getSession()
		.createQuery(
				"Select phase from Analysis as analysis inner join analysis.usedPhases as phase where analysis.id = :idAnalysis and phase.endDate = :endDate order by phase.number")
		.setParameter("idAnalysis", idAnalysis)
		.setParameter("endDate", endDate).list();
	}

	/**
	 * loadAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOPhase#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Phase> loadAllFromAnalysis(int idAnalysis) throws Exception {
		return getSession()
		.createQuery(
				"Select phase from Analysis as analysis inner join analysis.usedPhases as phase where analysis.id = :idAnalysis order by phase.number asc")
		.setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOPhase#loadAll()
	 */
	@Override
	public List<Phase> loadAll() throws Exception {
		return null;
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOPhase#save(lu.itrust.business.TS.Phase)
	 */
	@Override
	public void save(Phase phase) throws Exception {
		getSession().save(phase);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOPhase#saveOrUpdate(lu.itrust.business.TS.Phase)
	 */
	@Override
	public void saveOrUpdate(Phase phase) throws Exception {
		getSession().saveOrUpdate(phase);

	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOPhase#remove(lu.itrust.business.TS.Phase)
	 */
	@Override
	public void remove(Phase phase) throws Exception {
		getSession().delete(phase);

	}

}
