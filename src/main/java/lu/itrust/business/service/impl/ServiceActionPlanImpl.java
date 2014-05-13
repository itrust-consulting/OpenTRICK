package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.dao.DAOActionPlan;
import lu.itrust.business.service.ServiceActionPlan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceActionPlanImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Feb 13, 2013
 */
@Service
public class ServiceActionPlanImpl implements ServiceActionPlan {

	@Autowired
	private DAOActionPlan daoActionPlan;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#get(int)
	 */
	@Override
	public ActionPlanEntry get(Integer id) throws Exception {
		return daoActionPlan.get(id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param actionPlanEntryId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanEntryId) throws Exception {
		return daoActionPlan.belongsToAnalysis(analysisId, actionPlanEntryId);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#getAll()
	 */
	@Override
	public List<ActionPlanEntry> getAll() throws Exception {
		return daoActionPlan.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#getAllFromAnalysis(int)
	 */
	@Override
	public List<ActionPlanEntry> getAllFromAnalysis(Integer id) throws Exception {
		return this.daoActionPlan.getAllFromAnalysis(id);
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @param mode
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#getFromAnalysisAndActionPlanType(int,
	 *      lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@Override
	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Integer analysisID, ActionPlanMode mode) throws Exception {
		return this.daoActionPlan.getFromAnalysisAndActionPlanType(analysisID, mode);
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param mode
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#getFromAnalysisAndActionPlanType(lu.itrust.business.TS.Analysis,
	 *      lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@Override
	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanMode mode) throws Exception {
		return this.daoActionPlan.getFromAnalysisAndActionPlanType(analysis, mode);
	}

	/**
	 * getMeasuresFromActionPlanAndAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @param mode
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#getMeasuresFromActionPlanAndAnalysis(int,
	 *      lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@Override
	public List<Measure> getMeasuresFromActionPlanAndAnalysis(Integer analysisID, ActionPlanMode mode) throws Exception {
		return daoActionPlan.getMeasuresFromActionPlanAndAnalysis(analysisID, mode);
	}

	/**
	 * getMeasuresFromActionPlanAndAnalysisAndNotToImplement: <br>
	 * Description
	 * 
	 * @param id
	 * @param apm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#getMeasuresFromActionPlanAndAnalysisAndNotToImplement(int,
	 *      lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@Override
	public List<Measure> getMeasuresFromActionPlanAndAnalysisAndNotToImplement(Integer id, ActionPlanMode apm) throws Exception {
		return daoActionPlan.getMeasuresFromActionPlanAndAnalysisAndNotToImplement(id, apm);
	}

	/**
	 * getDistinctActionPlanAssetsFromAnalysisAndOrderByALE: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(int)
	 */
	@Override
	public List<Asset> getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(Integer analysisID) throws Exception {
		return daoActionPlan.getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(analysisID);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param actionPlanEntry
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#save(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Transactional
	@Override
	public void save(ActionPlanEntry actionPlanEntry) throws Exception {
		daoActionPlan.save(actionPlanEntry);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param actionPlanEntry
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#saveOrUpdate(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) throws Exception {
		daoActionPlan.saveOrUpdate(actionPlanEntry);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param actionPlanEntry
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlan#delete(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Transactional
	@Override
	public void delete(ActionPlanEntry actionPlanEntry) throws Exception {
		daoActionPlan.delete(actionPlanEntry);
	}
}