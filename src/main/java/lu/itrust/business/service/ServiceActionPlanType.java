/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.actionplan.ActionPlanType;

/**
 * @author oensuifudine
 * 
 */
public interface ServiceActionPlanType {
	
	public ActionPlanType get(int id) throws Exception;

	public ActionPlanType get(String name) throws Exception;

	public List<ActionPlanType> loadAll() throws Exception;

	public void save(ActionPlanType actionPlanType) throws Exception;
	
	public void merge(ActionPlanType actionPlanType) throws Exception;
	
	public void saveOrUpdate(ActionPlanType actionPlanType) throws Exception;

	public void delete(ActionPlanType actionPlanType) throws Exception;

}
