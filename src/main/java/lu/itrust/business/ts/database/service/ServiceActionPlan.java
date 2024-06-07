package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.actionplan.ActionPlanEntry;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.standard.measure.Measure;

/**
 * ServiceActionPlan.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Feb 13, 2013
 */
public interface ServiceActionPlan {
	public ActionPlanEntry get(Integer id);

	public ActionPlanEntry getFromAnalysisById(Integer idAnalysis, Integer id);
	
	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanEntryId);

	public List<ActionPlanEntry> getAll();

	public List<ActionPlanEntry> getAllFromAnalysis(Integer id);

	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Integer analysisID, ActionPlanMode mode);

	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanMode mode);

	public List<Measure> getMeasuresFromActionPlanAndAnalysis(Integer analysisID, ActionPlanMode mode);

	public List<Measure> getMeasuresFromActionPlanAndAnalysisAndNotToImplement(Integer id, ActionPlanMode apm);

	public List<Asset> getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(Integer analysisID);

	public void save(ActionPlanEntry actionPlanEntry);

	public void saveOrUpdate(ActionPlanEntry actionPlanEntry);

	public void delete(ActionPlanEntry actionPlanEntry);
	
	@Deprecated
	public void deleteAllFromAnalysis(Integer analysisID);

}