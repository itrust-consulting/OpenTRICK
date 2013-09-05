package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanType;

/** 
 * DAOActionPlan.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAOActionPlan {
	
	public ActionPlanEntry get(int id) throws Exception;
	
	public ActionPlanEntry get(ActionPlanType actionPlanType, Measure measure) throws Exception;
	
	public List<ActionPlanEntry> findByActionPlanType(ActionPlanType actionPlanType) throws Exception;
	
	public List<ActionPlanEntry> findByAnalysis(Analysis analysis) throws Exception;
	public List<ActionPlanEntry> loadAllFromAnalysis(int identifier, String version, String creationDate) throws Exception;
	public List<ActionPlanEntry> loadAll() throws Exception;
	
	public void save(ActionPlanEntry actionPlanEntry) throws Exception;
	
	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) throws Exception;
	
	public void delete(ActionPlanEntry actionPlanEntry) throws Exception;
	
}