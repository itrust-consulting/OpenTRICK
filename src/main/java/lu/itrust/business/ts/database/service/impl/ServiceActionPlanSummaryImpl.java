package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOActionPlanSummary;
import lu.itrust.business.ts.database.service.ServiceActionPlanSummary;
import lu.itrust.business.ts.model.actionplan.ActionPlanType;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStage;
import lu.itrust.business.ts.model.analysis.Analysis;

/**
 * ServiceActionPlanSummaryImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Transactional(readOnly = true)
@Service
public class ServiceActionPlanSummaryImpl implements ServiceActionPlanSummary {

	@Autowired
	private DAOActionPlanSummary daoActionPlanSummary;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param idSummaryStage
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#get(int)
	 */
	@Override
	public SummaryStage get(Integer idSummaryStage)  {
		return daoActionPlanSummary.get(idSummaryStage);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param id
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public SummaryStage getFromAnalysisById(Integer id, Integer idAnalysis)  {
		return daoActionPlanSummary.getFromAnalysisById(id, idAnalysis);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param actionPlanSummaryId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanSummaryId)  {
		return daoActionPlanSummary.belongsToAnalysis(analysisId, actionPlanSummaryId);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#getAll()
	 */
	@Override
	public List<SummaryStage> getAll()  {
		return daoActionPlanSummary.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#getAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysis(Integer idAnalysis)  {
		return daoActionPlanSummary.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#getAllFromAnalysis(lu.itrust.business.ts.model.analysis.Analysis)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysis(Analysis analysis)  {
		return daoActionPlanSummary.getAllFromAnalysis(analysis);
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param actionPlanType
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#getFromAnalysisAndActionPlanType(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Integer idAnalysis, String actionPlanType)  {
		return daoActionPlanSummary.getAllFromAnalysisAndActionPlanType(idAnalysis, actionPlanType);
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param actionPlanType
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#getFromAnalysisAndActionPlanType(lu.itrust.business.ts.model.analysis.Analysis,
	 *      lu.itrust.business.ts.model.actionplan.ActionPlanType)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanType actionPlanType)  {
		return daoActionPlanSummary.getAllFromAnalysisAndActionPlanType(analysis, actionPlanType);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param summaryStage
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#save(lu.itrust.business.ts.model.actionplan.summary.SummaryStage)
	 */
	@Transactional
	@Override
	public void save(SummaryStage summaryStage)  {
		daoActionPlanSummary.save(summaryStage);

	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param summaryStage
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#saveOrUpdate(lu.itrust.business.ts.model.actionplan.summary.SummaryStage)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(SummaryStage summaryStage)  {
		daoActionPlanSummary.saveOrUpdate(summaryStage);

	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param summaryStage
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#delete(lu.itrust.business.ts.model.actionplan.summary.SummaryStage)
	 */
	@Transactional
	@Override
	public void delete(SummaryStage summaryStage)  {
		daoActionPlanSummary.delete(summaryStage);
	}

	/**
	 * deleteAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanSummary#deleteAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("deprecation")
	@Transactional
	@Override
	public void deleteAllFromAnalysis(Integer analysisID)  {
		daoActionPlanSummary.deleteAllFromAnalysis(analysisID);		
	}
}