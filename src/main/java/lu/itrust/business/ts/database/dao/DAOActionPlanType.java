package lu.itrust.business.ts.database.dao;

import java.util.List;

import lu.itrust.business.ts.model.actionplan.ActionPlanType;

/**
 * DAOActionPlanType.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl. :
 * @version
 * @since 7 feb. 2013
 */
public interface DAOActionPlanType {
	public ActionPlanType get(Integer id);

	public ActionPlanType getByName(String name);

	public List<ActionPlanType> getAll();

	public void save(ActionPlanType actionPlanType);

	public void merge(ActionPlanType actionPlanType);

	public void saveOrUpdate(ActionPlanType actionPlanType);

	public void delete(ActionPlanType actionPlanType);
}