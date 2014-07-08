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
	public ActionPlanEntry get(Integer id) throws Exception {
		return (ActionPlanEntry) getSession().get(ActionPlanEntry.class, id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idActionPlanEntry
	 * @return
	 * @throws Exception
	 */
	@Override
	public ActionPlanEntry getFromAnalysisById(Integer idAnalysis, Integer idActionPlanEntry) throws Exception {
		String query = "Select actionplanentry From Analysis as analysis inner join analysis.actionPlans as actionplanentry where analysis.id = :idAnalysis and actionplanentry.id = :idActionPlanEntry";
		return (ActionPlanEntry) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idActionPlanEntry", idActionPlanEntry).uniqueResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId,Integer actionPlanEntryId) throws Exception {
		String query = "Select count(actionPlanEntry) From Analysis as analysis inner join analysis.actionPlans as actionPlanEntry where analysis.id = :analysisId and actionPlanEntry.id = :actionPlanEntryId";
		return ((Long) getSession().createQuery(query).setParameter("analysisId", analysisId).setParameter("actionPlanEntryId", actionPlanEntryId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#getAll()
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
	public List<ActionPlanEntry> getAllFromAnalysis(Integer id) throws Exception {
		String query = "Select actionplan From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID ORDER BY actionplan.id DESC";
		return (List<ActionPlanEntry>) getSession().createQuery(query).setParameter("analysisID", id).list();
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
	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Integer analysisID, ActionPlanMode mode) throws Exception {
		String query = "SELECT actionplans From Analysis As analysis INNER JOIN analysis.actionPlans As actionplans where analysis.id = :analysisID and actionplans.actionPlanType.name = :mode ORDER BY actionplan.actionPlanType.id ASC, actionplan.measure.phase.number ASC, actionplan.ROI ASC";
		return (List<ActionPlanEntry>) getSession().createQuery(query).setParameter("mode", mode).setParameter("analysisID", analysisID).list();
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
	 * getAllFromAsset: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#getAllFromAsset(lu.itrust.business.TS.Asset)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> getAllFromAsset(Asset asset) throws Exception {
		String query = "SELECT actionplans From Analysis As analysis INNER JOIN analysis.actionPlans As actionplans INNER JOIN actionplans.actionPlanAssets As actionPlanAssets where actionPlanAssets.asset = :asset";
		return (List<ActionPlanEntry>) getSession().createQuery(query).setParameter("asset", asset).list();
	}

	/**
	 * getMeasuresFromActionPlanAndAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#getMeasuresFromActionPlanAndAnalysis(int,
	 *      lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getMeasuresFromActionPlanAndAnalysis(Integer id, ActionPlanMode apm) throws Exception {
		String query = "Select actionplan.measure From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID and actionplan.actionPlanType.name = :apm";
		return (List<Measure>) getSession().createQuery(query).setParameter("analysisID", id).setParameter("apm", apm).list();
	}

	/**
	 * getMeasuresFromActionPlanAndAnalysisAndNotToImplement: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#getMeasuresFromActionPlanAndAnalysisAndNotToImplement(int,
	 *      lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getMeasuresFromActionPlanAndAnalysisAndNotToImplement(Integer id, ActionPlanMode apm) throws Exception {
		String query = "Select actionplan.measure From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID and actionplan.actionPlanType.name = :apm and actionplan.ROI <= 0.0";
		return (List<Measure>) getSession().createQuery(query).setParameter("analysisID", id).setParameter("apm", apm).list();
	}

	/**
	 * getDistinctActionPlanAssetsFromAnalysisAndOrderByALE: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(Integer analysisID) throws Exception {
		String query = "SELECT DISTINCT apa.asset FROM Analysis a INNER JOIN ActionPlanAsset apa WHERE a.id= :analysisID";
		return (List<Asset>) getSession().createQuery(query).setParameter("analysisID", analysisID).list();
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