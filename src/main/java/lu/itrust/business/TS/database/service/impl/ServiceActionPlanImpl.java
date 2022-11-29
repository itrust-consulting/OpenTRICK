package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOActionPlan;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * ServiceActionPlanImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Feb 13, 2013
 */
@Transactional(readOnly = true)
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
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#get(int)
	 */
	@Override
	public ActionPlanEntry get(Integer id)  {
		return daoActionPlan.get(id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#getFromAnalysisById(java.lang.Integer, java.lang.Integer)
	 */
	public ActionPlanEntry getFromAnalysisById(Integer idAnalysis, Integer id)  {
		return daoActionPlan.getFromAnalysisById(idAnalysis, id);	
	}

	
	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param actionPlanEntryId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanEntryId)  {
		return daoActionPlan.belongsToAnalysis(analysisId, actionPlanEntryId);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#getAll()
	 */
	@Override
	public List<ActionPlanEntry> getAll()  {
		return daoActionPlan.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#getAllFromAnalysis(int)
	 */
	@Override
	public List<ActionPlanEntry> getAllFromAnalysis(Integer id)  {
		return this.daoActionPlan.getAllFromAnalysis(id);
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @param mode
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#getFromAnalysisAndActionPlanType(int,
	 *      lu.itrust.business.TS.model.actionplan.ActionPlanMode)
	 */
	@Override
	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Integer analysisID, ActionPlanMode mode)  {
		return this.daoActionPlan.getFromAnalysisAndActionPlanType(analysisID, mode);
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param mode
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#getFromAnalysisAndActionPlanType(lu.itrust.business.TS.model.analysis.Analysis,
	 *      lu.itrust.business.TS.model.actionplan.ActionPlanMode)
	 */
	@Override
	public List<ActionPlanEntry> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanMode mode)  {
		return this.daoActionPlan.getFromAnalysisAndActionPlanType(analysis, mode);
	}

	/**
	 * getMeasuresFromActionPlanAndAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @param mode
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#getMeasuresFromActionPlanAndAnalysis(int,
	 *      lu.itrust.business.TS.model.actionplan.ActionPlanMode)
	 */
	@Override
	public List<Measure> getMeasuresFromActionPlanAndAnalysis(Integer analysisID, ActionPlanMode mode)  {
		return daoActionPlan.getMeasuresFromActionPlanAndAnalysis(analysisID, mode);
	}

	/**
	 * getMeasuresFromActionPlanAndAnalysisAndNotToImplement: <br>
	 * Description
	 * 
	 * @param id
	 * @param apm
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#getMeasuresFromActionPlanAndAnalysisAndNotToImplement(int,
	 *      lu.itrust.business.TS.model.actionplan.ActionPlanMode)
	 */
	@Override
	public List<Measure> getMeasuresFromActionPlanAndAnalysisAndNotToImplement(Integer id, ActionPlanMode apm)  {
		return daoActionPlan.getMeasuresFromActionPlanAndAnalysisAndNotToImplement(id, apm);
	}

	/**
	 * getDistinctActionPlanAssetsFromAnalysisAndOrderByALE: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(int)
	 */
	@Override
	public List<Asset> getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(Integer analysisID)  {
		return daoActionPlan.getDistinctActionPlanAssetsFromAnalysisAndOrderByALE(analysisID);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param actionPlanEntry
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#save(lu.itrust.business.TS.model.actionplan.ActionPlanEntry)
	 */
	@Transactional
	@Override
	public void save(ActionPlanEntry actionPlanEntry)  {
		daoActionPlan.save(actionPlanEntry);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param actionPlanEntry
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#saveOrUpdate(lu.itrust.business.TS.model.actionplan.ActionPlanEntry)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ActionPlanEntry actionPlanEntry)  {
		daoActionPlan.saveOrUpdate(actionPlanEntry);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param actionPlanEntry
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#delete(lu.itrust.business.TS.model.actionplan.ActionPlanEntry)
	 */
	@Transactional
	@Override
	public void delete(ActionPlanEntry actionPlanEntry)  {
		daoActionPlan.delete(actionPlanEntry);
	}

	/**
	 * deleteAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlan#deleteAllFromAnalysis(java.lang.Integer)
	 */
	@Deprecated
	@Transactional
	@Override
	public void deleteAllFromAnalysis(Integer analysisID)  {
		daoActionPlan.deleteAllFromAnalysis(analysisID);
	}
}