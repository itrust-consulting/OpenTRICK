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
	public ActionPlanEntry get(Integer id);

	public ActionPlanEntry getFromAnalysisById(Integer idAnalysis, Integer idActionPlanEntry);
	
	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanEntryId);

	public List<ActionPlanEntry> getAll();

	public List<ActionPlanEntry> getAllFromAnalysis(Integer id);

	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Integer analysisID, ActionPlanMode mode);

	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanMode mode);
	
	public List<ActionPlanEntry> getAllFromAsset(Asset asset);
	
	public List<Measure> getMeasuresFromActionPlanAndAnalysis(Integer analysisID, ActionPlanMode mode);

	public List<Measure> getMeasuresFromActionPlanAndAnalysisAndNotToImplement(Integer id, ActionPlanMode apm);

	public List<Asset> getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(Integer analysisID);

	public void save(ActionPlanEntry actionPlanEntry);

	public void saveOrUpdate(ActionPlanEntry actionPlanEntry);

	public void delete(ActionPlanEntry actionPlanEntry);
	
	public void deleteAllFromAnalysis(Integer analysisID);

}