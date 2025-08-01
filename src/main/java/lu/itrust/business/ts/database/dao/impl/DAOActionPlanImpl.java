package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOActionPlan;
import lu.itrust.business.ts.model.actionplan.ActionPlanAsset;
import lu.itrust.business.ts.model.actionplan.ActionPlanEntry;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.standard.measure.Measure;

/**
 * DAOActionPlanHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 5, 2013
 */
@Repository
public class DAOActionPlanImpl extends DAOHibernate implements DAOActionPlan {

	/**
	 * Constructor: <br>
	 */
	public DAOActionPlanImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOActionPlanImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#get(int)
	 */
	@Override
	public ActionPlanEntry get(Integer id) {
		return (ActionPlanEntry) getSession().get(ActionPlanEntry.class, id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idActionPlanEntry
	 * @return
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ActionPlanEntry getFromAnalysisById(Integer idAnalysis, Integer idActionPlanEntry) {
		String query = "Select actionplanentry From Analysis as analysis inner join analysis.actionPlans as actionplanentry where analysis.id = :idAnalysis and actionplanentry.id = :idActionPlanEntry";
		return (ActionPlanEntry) createQueryWithCache(query)
				.setParameter("idAnalysis", idAnalysis)
				.setParameter("idActionPlanEntry", idActionPlanEntry).uniqueResultOptional().orElse(null);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#belongsToAnalysis(int,
	 *      int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanEntryId) {
		String query = "Select count(actionPlanEntry) > 0 From Analysis as analysis inner join analysis.actionPlans as actionPlanEntry where analysis.id = :analysisId and actionPlanEntry.id = :actionPlanEntryId";
		return (boolean) createQueryWithCache(query)
				.setParameter("analysisId", analysisId)
				.setParameter("actionPlanEntryId", actionPlanEntryId).getSingleResult();
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> getAll() {
		return (List<ActionPlanEntry>) createQueryWithCache("From actionplans")
				.setHint("org.hibernate.cacheable", true).getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#getAllFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> getAllFromAnalysis(Integer id) {
		String query = "Select actionplan From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID ORDER BY actionplan.actionPlanType.name ASC, actionplan.position ASC, actionplan.totalALE DESC";
		return (List<ActionPlanEntry>) createQueryWithCache(query)
				.setParameter("analysisID", id).getResultList();
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#getFromAnalysisAndActionPlanType(int,
	 *      lu.itrust.business.ts.model.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Integer analysisID, ActionPlanMode mode) {
		String query = "SELECT actionplans From Analysis As analysis INNER JOIN analysis.actionPlans As actionplan where analysis.id = :analysisID and actionplan.actionPlanType.name = :mode ORDER BY actionplan.position ASC, actionplan.totalALE DESC";
		return (List<ActionPlanEntry>) createQueryWithCache(query)
				.setParameter("mode", mode)
				.setParameter("analysisID", analysisID).getResultList();
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#getFromAnalysisAndActionPlanType(lu.itrust.business.ts.model.analysis.Analysis,
	 *      lu.itrust.business.ts.model.actionplan.ActionPlanMode)
	 */
	@Override
	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanMode mode) {
		return analysis.findActionPlan(mode);
	}

	/**
	 * getAllFromAsset: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#getAllFromAsset(lu.itrust.business.ts.model.asset.Asset)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> getAllFromAsset(Asset asset) {
		String query = "SELECT actionplans From Analysis As analysis INNER JOIN analysis.actionPlans As actionplans INNER JOIN actionplans.actionPlanAssets As actionPlanAssets where actionPlanAssets.asset = :asset";
		return (List<ActionPlanEntry>) createQueryWithCache(query)
				.setParameter("asset", asset).getResultList();
	}

	/**
	 * getMeasuresFromActionPlanAndAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#getMeasuresFromActionPlanAndAnalysis(int,
	 *      lu.itrust.business.ts.model.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getMeasuresFromActionPlanAndAnalysis(Integer id, ActionPlanMode apm) {
		String query = "Select actionplan.measure From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID and actionplan.actionPlanType.name = :apm";
		return (List<Measure>) createQueryWithCache(query)
				.setParameter("analysisID", id).setParameter("apm", apm)
				.getResultList();
	}

	/**
	 * getMeasuresFromActionPlanAndAnalysisAndNotToImplement: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#getMeasuresFromActionPlanAndAnalysisAndNotToImplement(int,
	 *      lu.itrust.business.ts.model.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getMeasuresFromActionPlanAndAnalysisAndNotToImplement(Integer id, ActionPlanMode apm) {
		String query = "Select actionplan.measure From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID and actionplan.actionPlanType.name = :apm and actionplan.ROI <= 0.0";
		return (List<Measure>) createQueryWithCache(query)
				.setParameter("analysisID", id).setParameter("apm", apm)
				.getResultList();
	}

	/**
	 * getDistinctActionPlanAssetsFromAnalysisAndOrderByALE: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(Integer analysisID) {
		String query = "SELECT DISTINCT apa.asset FROM Analysis a INNER JOIN ActionPlanAsset apa WHERE a.id= :analysisID";
		return (List<Asset>) createQueryWithCache(query)
				.setParameter("analysisID", analysisID).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#save(lu.itrust.business.ts.model.actionplan.ActionPlanEntry)
	 */
	@Override
	public void save(ActionPlanEntry actionPlanEntry) {
		getSession().save(actionPlanEntry);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#saveOrUpdate(lu.itrust.business.ts.model.actionplan.ActionPlanEntry)
	 */
	@Override
	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) {
		getSession().saveOrUpdate(actionPlanEntry);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#delete(lu.itrust.business.ts.model.actionplan.ActionPlanEntry)
	 */
	@Override
	public void delete(ActionPlanEntry actionPlanEntry) {
		getSession().delete(actionPlanEntry);
	}

	/**
	 * deleteAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#deleteAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void deleteAllFromAnalysis(Integer analysisID) {
		String query = "Select actionplans FROM Analysis analysis INNER JOIN analysis.actionPlans actionplans WHERE analysis.id= :analysisID";

		List<ActionPlanEntry> actionplans = (List<ActionPlanEntry>) createQueryWithCache(query)
				.setHint("org.hibernate.cacheable", true)
				.setParameter("analysisID", analysisID).getResultList();
		for (ActionPlanEntry entry : actionplans) {
			List<ActionPlanAsset> assets = entry.getActionPlanAssets();
			for (ActionPlanAsset asset : assets)
				getSession().delete(asset);
			getSession().delete(entry);
		}

	}
}