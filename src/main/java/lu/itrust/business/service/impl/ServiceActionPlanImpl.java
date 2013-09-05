/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.dao.DAOActionPlan;
import lu.itrust.business.service.ServiceActionPlan;

/**
 * @author oensuifudine
 *
 */
@Service
public class ServiceActionPlanImpl implements ServiceActionPlan {

	@Autowired
	private DAOActionPlan daoActionPlan;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlan#get(int)
	 */
	@Override
	public ActionPlanEntry get(int id) throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlan.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlan#get(lu.itrust.business.TS.actionplan.ActionPlanType, lu.itrust.business.TS.Measure)
	 */
	@Override
	public ActionPlanEntry get(ActionPlanType actionPlanType, Measure measure)
			throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlan.get(actionPlanType, measure);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlan#findByActionPlanType(lu.itrust.business.TS.actionplan.ActionPlanType)
	 */
	@Override
	public List<ActionPlanEntry> findByActionPlanType(
			ActionPlanType actionPlanType) throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlan.findByActionPlanType(actionPlanType);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlan#findByAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<ActionPlanEntry> findByAnalysis(Analysis analysis)
			throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlan.findByAnalysis(analysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlan#loadAllFromAnalysis(int, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ActionPlanEntry> loadAllFromAnalysis(int identifier,
			String version, String creationDate) throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlan.loadAllFromAnalysis(identifier, version, creationDate);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlan#loadAll()
	 */
	@Override
	public List<ActionPlanEntry> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlan.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlan#save(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Transactional
	@Override
	public void save(ActionPlanEntry actionPlanEntry) throws Exception {
		daoActionPlan.save(actionPlanEntry);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlan#saveOrUpdate(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) throws Exception {
		daoActionPlan.saveOrUpdate(actionPlanEntry);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlan#delete(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Transactional
	@Override
	public void delete(ActionPlanEntry actionPlanEntry) throws Exception {
		daoActionPlan.delete(actionPlanEntry);

	}

	/**
	 * @param daoActionPlan the daoActionPlan to set
	 */
	public void setDaoActionPlan(DAOActionPlan daoActionPlan) {
		this.daoActionPlan = daoActionPlan;
	}
}
