package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOActionPlanSummary;
import lu.itrust.business.TS.database.service.ServiceActionPlanSummary;
import lu.itrust.business.TS.model.actionplan.ActionPlanType;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.Analysis;

/**
 * ServiceActionPlanSummaryImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#get(int)
	 */
	@Override
	public SummaryStage get(Integer idSummaryStage) throws Exception {
		return daoActionPlanSummary.get(idSummaryStage);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param id
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public SummaryStage getFromAnalysisById(Integer id, Integer idAnalysis) throws Exception {
		return daoActionPlanSummary.getFromAnalysisById(id, idAnalysis);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param actionPlanSummaryId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanSummaryId) throws Exception {
		return daoActionPlanSummary.belongsToAnalysis(analysisId, actionPlanSummaryId);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#getAll()
	 */
	@Override
	public List<SummaryStage> getAll() throws Exception {
		return daoActionPlanSummary.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#getAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		return daoActionPlanSummary.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#getAllFromAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysis(Analysis analysis) throws Exception {
		return daoActionPlanSummary.getAllFromAnalysis(analysis);
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param actionPlanType
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#getFromAnalysisAndActionPlanType(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Integer idAnalysis, String actionPlanType) throws Exception {
		return daoActionPlanSummary.getAllFromAnalysisAndActionPlanType(idAnalysis, actionPlanType);
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param actionPlanType
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#getFromAnalysisAndActionPlanType(lu.itrust.business.TS.model.analysis.Analysis,
	 *      lu.itrust.business.TS.model.actionplan.ActionPlanType)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanType actionPlanType) throws Exception {
		return daoActionPlanSummary.getAllFromAnalysisAndActionPlanType(analysis, actionPlanType);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param summaryStage
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#save(lu.itrust.business.TS.model.actionplan.summary.SummaryStage)
	 */
	@Transactional
	@Override
	public void save(SummaryStage summaryStage) throws Exception {
		daoActionPlanSummary.save(summaryStage);

	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param summaryStage
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#saveOrUpdate(lu.itrust.business.TS.model.actionplan.summary.SummaryStage)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(SummaryStage summaryStage) throws Exception {
		daoActionPlanSummary.saveOrUpdate(summaryStage);

	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param summaryStage
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#delete(lu.itrust.business.TS.model.actionplan.summary.SummaryStage)
	 */
	@Transactional
	@Override
	public void delete(SummaryStage summaryStage) throws Exception {
		daoActionPlanSummary.delete(summaryStage);
	}

	/**
	 * deleteAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceActionPlanSummary#deleteAllFromAnalysis(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void deleteAllFromAnalysis(Integer analysisID) throws Exception {
		daoActionPlanSummary.deleteAllFromAnalysis(analysisID);		
	}
}