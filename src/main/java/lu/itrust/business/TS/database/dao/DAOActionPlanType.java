package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.data.actionplan.ActionPlanType;

/**
 * DAOActionPlanType.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl. :
 * @version
 * @since 7 feb. 2013
 */
public interface DAOActionPlanType {
	public ActionPlanType get(Integer id) throws Exception;

	public ActionPlanType getByName(String name) throws Exception;

	public List<ActionPlanType> getAll() throws Exception;

	public void save(ActionPlanType actionPlanType) throws Exception;

	public void merge(ActionPlanType actionPlanType) throws Exception;

	public void saveOrUpdate(ActionPlanType actionPlanType) throws Exception;

	public void delete(ActionPlanType actionPlanType) throws Exception;
}