package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.actionplan.ActionPlanType;

/**
 * DAOActionPlanType.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 7 f�vr. 2013
 */
public interface DAOActionPlanType {

	public ActionPlanType get(int id) throws Exception;

	public ActionPlanType getByName(String name) throws Exception;

	public List<ActionPlanType> getAll() throws Exception;

	public void save(ActionPlanType actionPlanType) throws Exception;

	public void merge(ActionPlanType actionPlanType) throws Exception;

	public void saveOrUpdate(ActionPlanType actionPlanType) throws Exception;

	public void delete(ActionPlanType actionPlanType) throws Exception;
}