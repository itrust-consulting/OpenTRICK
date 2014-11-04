package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.data.actionplan.ActionPlanAsset;
import lu.itrust.business.TS.data.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.data.actionplan.ActionPlanMode;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.asset.Asset;
import lu.itrust.business.TS.data.standard.measure.Measure;
import lu.itrust.business.TS.database.dao.DAOActionPlan;

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
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#get(int)
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
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#belongsToAnalysis(int, int)
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
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#getAll()
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
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#getAllFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> getAllFromAnalysis(Integer id) throws Exception {
		String query = "Select actionplan From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID ORDER BY actionplan.actionPlanType.name ASC, actionplan.position ASC, actionplan.totalALE DESC";
		return (List<ActionPlanEntry>) getSession().createQuery(query).setParameter("analysisID", id).list();
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#getFromAnalysisAndActionPlanType(int,
	 *      lu.itrust.business.TS.data.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Integer analysisID, ActionPlanMode mode) throws Exception {
		String query = "SELECT actionplans From Analysis As analysis INNER JOIN analysis.actionPlans As actionplan where analysis.id = :analysisID and actionplan.actionPlanType.name = :mode ORDER BY actionplan.position ASC, actionplan.totalALE DESC";
		return (List<ActionPlanEntry>) getSession().createQuery(query).setParameter("mode", mode).setParameter("analysisID", analysisID).list();
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#getFromAnalysisAndActionPlanType(lu.itrust.business.TS.data.analysis.Analysis,
	 *      lu.itrust.business.TS.data.actionplan.ActionPlanMode)
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
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#getAllFromAsset(lu.itrust.business.TS.data.asset.Asset)
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
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#getMeasuresFromActionPlanAndAnalysis(int,
	 *      lu.itrust.business.TS.data.actionplan.ActionPlanMode)
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
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#getMeasuresFromActionPlanAndAnalysisAndNotToImplement(int,
	 *      lu.itrust.business.TS.data.actionplan.ActionPlanMode)
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
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(int)
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
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#save(lu.itrust.business.TS.data.actionplan.ActionPlanEntry)
	 */
	@Override
	public void save(ActionPlanEntry actionPlanEntry) throws Exception {
		getSession().save(actionPlanEntry);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#saveOrUpdate(lu.itrust.business.TS.data.actionplan.ActionPlanEntry)
	 */
	@Override
	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) throws Exception {
		getSession().saveOrUpdate(actionPlanEntry);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#delete(lu.itrust.business.TS.data.actionplan.ActionPlanEntry)
	 */
	@Override
	public void delete(ActionPlanEntry actionPlanEntry) throws Exception {
		getSession().delete(actionPlanEntry);
	}

	/**
	 * deleteAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlan#deleteAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void deleteAllFromAnalysis(Integer analysisID) throws Exception {
		String query = "Select actionplans FROM Analysis analysis INNER JOIN analysis.actionPlans actionplans WHERE analysis.id= :analysisID";
		 
		List<ActionPlanEntry> actionplans = (List<ActionPlanEntry>) getSession().createQuery(query).setParameter("analysisID", analysisID).list();
		 for(ActionPlanEntry entry : actionplans) {
			 List<ActionPlanAsset> assets = entry.getActionPlanAssets();
			 for(ActionPlanAsset asset : assets)
				 getSession().delete(asset);
			 getSession().delete(entry);
		 }
		
	}
}