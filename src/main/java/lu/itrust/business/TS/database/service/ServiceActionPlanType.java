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
	public ActionPlanType get(Integer id) throws Exception;

	public ActionPlanType getByName(String name) throws Exception;

	public List<ActionPlanType> getAll() throws Exception;

	public void save(ActionPlanType actionPlanType) throws Exception;

	public void merge(ActionPlanType actionPlanType) throws Exception;

	public void saveOrUpdate(ActionPlanType actionPlanType) throws Exception;

	public void delete(ActionPlanType actionPlanType) throws Exception;
}