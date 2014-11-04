package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.data.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.data.actionplan.ActionPlanMode;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.asset.Asset;
import lu.itrust.business.TS.data.standard.measure.Measure;

/**
 * ServiceActionPlan.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Feb 13, 2013
 */
public interface ServiceActionPlan {
	public ActionPlanEntry get(Integer id) throws Exception;

	public ActionPlanEntry getFromAnalysisById(Integer idAnalysis, Integer id) throws Exception;
	
	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanEntryId) throws Exception;

	public List<ActionPlanEntry> getAll() throws Exception;

	public List<ActionPlanEntry> getAllFromAnalysis(Integer id) throws Exception;

	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Integer analysisID, ActionPlanMode mode) throws Exception;

	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanMode mode) throws Exception;

	public List<Measure> getMeasuresFromActionPlanAndAnalysis(Integer analysisID, ActionPlanMode mode) throws Exception;

	public List<Measure> getMeasuresFromActionPlanAndAnalysisAndNotToImplement(Integer id, ActionPlanMode apm) throws Exception;

	public List<Asset> getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(Integer analysisID) throws Exception;

	public void save(ActionPlanEntry actionPlanEntry) throws Exception;

	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) throws Exception;

	public void delete(ActionPlanEntry actionPlanEntry) throws Exception;
	
	public void deleteAllFromAnalysis(Integer analysisID) throws Exception;

}