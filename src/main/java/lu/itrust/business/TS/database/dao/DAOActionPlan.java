package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * DAOActionPlan.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOActionPlan {
	public ActionPlanEntry get(Integer id) throws Exception;

	public ActionPlanEntry getFromAnalysisById(Integer idAnalysis, Integer idActionPlanEntry) throws Exception;
	
	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanEntryId) throws Exception;

	public List<ActionPlanEntry> getAll() throws Exception;

	public List<ActionPlanEntry> getAllFromAnalysis(Integer id) throws Exception;

	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Integer analysisID, ActionPlanMode mode) throws Exception;

	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanMode mode) throws Exception;
	
	public List<ActionPlanEntry> getAllFromAsset(Asset asset) throws Exception;
	
	public List<Measure> getMeasuresFromActionPlanAndAnalysis(Integer analysisID, ActionPlanMode mode) throws Exception;

	public List<Measure> getMeasuresFromActionPlanAndAnalysisAndNotToImplement(Integer id, ActionPlanMode apm) throws Exception;

	public List<Asset> getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(Integer analysisID) throws Exception;

	public void save(ActionPlanEntry actionPlanEntry) throws Exception;

	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) throws Exception;

	public void delete(ActionPlanEntry actionPlanEntry) throws Exception;
	
	public void deleteAllFromAnalysis(Integer analysisID) throws Exception;
	
}