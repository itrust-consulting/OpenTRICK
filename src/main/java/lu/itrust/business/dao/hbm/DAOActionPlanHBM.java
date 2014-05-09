package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.dao.DAOActionPlan;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOActionPlanHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 5, 2013
 */
@Repository
public class DAOActionPlanHBM extends DAOHibernate implements DAOActionPlan {

	/**
	 * Constructor: <br>
	 */
	public DAOActionPlanHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOActionPlanHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#get(int)
	 */
	@Override
	public ActionPlanEntry get(int id) throws Exception {
		return (ActionPlanEntry) getSession().get(ActionPlanEntry.class, id);
	}
	
	/**
	 * belongsToAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(int actionPlanEntryId, int analysisId){
		return ((Long) getSession().createQuery("Select count(actionPlanEntry) From Analysis as analysis inner join analysis.actionPlans as actionPlanEntry where analysis.id = :analysisId and actionPlanEntry.id = : actionPlanEntryid")
				.setInteger("analysisId", analysisId).setInteger("actionPlanEntryId", actionPlanEntryId).uniqueResult()).intValue() > 0;
	}

	
	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> getAll() throws Exception {
		return (List<ActionPlanEntry>) getSession().createQuery("From actionplans").list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#getAllFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> getAllFromAnalysis(int id) throws Exception {
		return (List<ActionPlanEntry>) getSession().createQuery("Select actionplan From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID ORDER BY actionplan.id DESC")
				.setParameter("analysisID", id).list();
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#getFromAnalysisAndActionPlanType(int,
	 *      lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(int analysisID, ActionPlanMode mode) throws Exception {
		return (List<ActionPlanEntry>) getSession()
				.createQuery(
						"SELECT actionplans From Analysis As analysis INNER JOIN analysis.actionPlans As actionplans where analysis.id = :analysisID and actionplans.actionPlanType.name = :mode ORDER BY actionplan.actionPlanType.id ASC, actionplan.measure.phase.number ASC, actionplan.ROI ASC")
				.setParameter("mode", mode).setParameter("analysisID", analysisID).list();
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#getFromAnalysisAndActionPlanType(lu.itrust.business.TS.Analysis,
	 *      lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@Override
	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanMode mode) throws Exception {
		return analysis.getActionPlan(mode);
	}

	/**
	 * getMeasuresFromActionPlanAndAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#getMeasuresFromActionPlanAndAnalysis(int, lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getMeasuresFromActionPlanAndAnalysis(int id, ActionPlanMode apm) throws Exception {
		return (List<Measure>) getSession().createQuery(
				"Select actionplan.measure From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID and actionplan.actionPlanType.name = :apm").setParameter(
				"analysisID", id).setParameter("apm", apm).list();
	}

	/**
	 * getMeasuresFromActionPlanAndAnalysisAndNotToImplement: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#getMeasuresFromActionPlanAndAnalysisAndNotToImplement(int, lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getMeasuresFromActionPlanAndAnalysisAndNotToImplement(int id, ActionPlanMode apm) throws Exception {
		return (List<Measure>) getSession().createQuery(
				"Select actionplan.measure From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID and actionplan.actionPlanType.name = :apm and actionplan.ROI <= 0.0")
				.setParameter("analysisID", id).setParameter("apm", apm).list();
	}

	/**
	 * getDistinctActionPlanAssetsFromAnalysisAndOrderByALE: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(int analysisID) throws Exception {
		return (List<Asset>) getSession().createQuery("SELECT DISTINCT apa.asset FROM Analysis a INNER JOIN ActionPlanAsset apa WHERE a.id= : analysisID").list();
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#save(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Override
	public void save(ActionPlanEntry actionPlanEntry) throws Exception {
		getSession().save(actionPlanEntry);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#saveOrUpdate(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Override
	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) throws Exception {
		getSession().saveOrUpdate(actionPlanEntry);
	}

	/**
	 * delete: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#delete(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Override
	public void delete(ActionPlanEntry actionPlanEntry) throws Exception {
		getSession().delete(actionPlanEntry);
	}
}