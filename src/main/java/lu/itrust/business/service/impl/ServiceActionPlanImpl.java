/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.dao.DAOActionPlan;
import lu.itrust.business.service.ServiceActionPlan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		return daoActionPlan.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlan#loadAll()
	 */
	@Override
	public List<ActionPlanEntry> loadAll() throws Exception {
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

	@Override
	public List<ActionPlanEntry> loadByAnalysisActionPlanType(Analysis analysis, ActionPlanMode mode) throws Exception {
		return this.daoActionPlan.loadByAnalysisActionPlanType(analysis, mode);
	}

	@Override
	public List<ActionPlanEntry> loadByAnalysisActionPlanType(int analysisID, ActionPlanMode mode) throws Exception {
		return this.daoActionPlan.loadByAnalysisActionPlanType(analysisID, mode);
	}

	@Override
	public List<ActionPlanEntry> loadAllFromAnalysis(int id) throws Exception {
		return this.daoActionPlan.loadAllFromAnalysis(id);
	}
}
