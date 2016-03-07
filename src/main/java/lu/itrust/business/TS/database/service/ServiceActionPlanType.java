package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.actionplan.ActionPlanType;

/**
 * ServiceActionPlanType.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Feb 7, 2013
 */
public interface ServiceActionPlanType {
	public ActionPlanType get(Integer id) ;

	public ActionPlanType getByName(String name) ;

	public List<ActionPlanType> getAll() ;

	public void save(ActionPlanType actionPlanType) ;

	public void merge(ActionPlanType actionPlanType) ;

	public void saveOrUpdate(ActionPlanType actionPlanType) ;

	public void delete(ActionPlanType actionPlanType) ;
}