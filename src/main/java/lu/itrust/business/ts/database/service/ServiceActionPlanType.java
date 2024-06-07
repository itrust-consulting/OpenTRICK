package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.actionplan.ActionPlanType;

/**
 * ServiceActionPlanType.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Feb 7, 2013
 */
public interface ServiceActionPlanType {
	public ActionPlanType get(Integer id);

	public ActionPlanType getByName(String name);

	public List<ActionPlanType> getAll();

	public void save(ActionPlanType actionPlanType);

	public void merge(ActionPlanType actionPlanType);

	public void saveOrUpdate(ActionPlanType actionPlanType);

	public void delete(ActionPlanType actionPlanType);
}