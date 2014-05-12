package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;

/**
 * DAOActionPlan.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOActionPlan {

	public ActionPlanEntry get(int id) throws Exception;

	public boolean belongsToAnalysis(int actionPlanEntryId, int analysisId) throws Exception;

	public List<ActionPlanEntry> getAll() throws Exception;

	public List<ActionPlanEntry> getAllFromAnalysis(int id) throws Exception;

	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(int analysisID, ActionPlanMode mode) throws Exception;

	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanMode mode) throws Exception;

	public List<Measure> getMeasuresFromActionPlanAndAnalysis(int analysisID, ActionPlanMode mode) throws Exception;

	public List<Measure> getMeasuresFromActionPlanAndAnalysisAndNotToImplement(int id, ActionPlanMode apm) throws Exception;

	public List<Asset> getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(int analysisID) throws Exception;

	public void save(ActionPlanEntry actionPlanEntry) throws Exception;

	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) throws Exception;

	public void delete(ActionPlanEntry actionPlanEntry) throws Exception;

}