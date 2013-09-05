/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.dao.DAOActionPlanSummary;
import lu.itrust.business.service.ServiceActionPlanSummary;

/**
 * @author oensuifudine
 *
 */
@Service
public class ServiceActionPlanSummaryImpl implements ServiceActionPlanSummary {

	@Autowired
	private DAOActionPlanSummary daoActionPlanSummary;
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlanSummary#get(long, lu.itrust.business.TS.actionplan.ActionPlanType, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public SummaryStage get(int idSummaryStage) throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlanSummary.get(idSummaryStage);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlanSummary#loadAllFromType(lu.itrust.business.TS.actionplan.ActionPlanType, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<SummaryStage> loadAllFromType(ActionPlanType actionPlanType,
			Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlanSummary.loadAllFromType(actionPlanType, analysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlanSummary#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<SummaryStage> loadAllFromAnalysis(Analysis analysis)
			throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlanSummary.loadAllFromAnalysis(analysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlanSummary#loadAllFromAnalysisIdentifierVersionCreationDate(int, java.lang.String, java.lang.String)
	 */
	@Override
	public List<SummaryStage> loadAllFromAnalysisIdentifierVersionCreationDate(
			int identifier, String version, String creationDate)
			throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlanSummary.loadAllFromAnalysisIdentifierVersionCreationDate(identifier, version, creationDate);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlanSummary#loadAll()
	 */
	@Override
	public List<SummaryStage> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return daoActionPlanSummary.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlanSummary#save(lu.itrust.business.TS.actionplan.SummaryStage)
	 */
	@Transactional
	@Override
	public void save(SummaryStage summaryStage) throws Exception {
		daoActionPlanSummary.save(summaryStage);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlanSummary#saveOrUpdate(lu.itrust.business.TS.actionplan.SummaryStage)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(SummaryStage summaryStage) throws Exception {
		daoActionPlanSummary.saveOrUpdate(summaryStage);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceActionPlanSummary#remove(lu.itrust.business.TS.actionplan.SummaryStage)
	 */
	@Transactional
	@Override
	public void remove(SummaryStage summaryStage) throws Exception {
		daoActionPlanSummary.remove(summaryStage);

	}

	/**
	 * @param daoActionPlanSummary the daoActionPlanSummary to set
	 */
	public void setDaoActionPlanSummary(DAOActionPlanSummary daoActionPlanSummary) {
		this.daoActionPlanSummary = daoActionPlanSummary;
	}

}
