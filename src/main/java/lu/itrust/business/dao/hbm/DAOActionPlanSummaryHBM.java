/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.dao.DAOActionPlanSummary;

/**
 * @author oensuifudine
 *
 */
public class DAOActionPlanSummaryHBM extends DAOHibernate implements
		DAOActionPlanSummary {

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#get(long, lu.itrust.business.TS.actionplan.ActionPlanType, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public SummaryStage get(int idSummaryStage) throws Exception {
		return (SummaryStage) getSession().get(SummaryStage.class, idSummaryStage);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#loadAllFromType(lu.itrust.business.TS.actionplan.ActionPlanType, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<SummaryStage> loadAllFromType(ActionPlanType actionPlanType,
			Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<SummaryStage> loadAllFromAnalysis(Analysis analysis)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#loadAllFromAnalysisIdentifierVersionCreationDate(int, java.lang.String, java.lang.String)
	 */
	@Override
	public List<SummaryStage> loadAllFromAnalysisIdentifierVersionCreationDate(
			int identifier, String version, String creationDate)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SummaryStage> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return getSession().createQuery("From SummaryStage").list();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#save(lu.itrust.business.TS.actionplan.SummaryStage)
	 */
	@Override
	public void save(SummaryStage summaryStage) throws Exception {
		getSession().save(summaryStage);
		
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#saveOrUpdate(lu.itrust.business.TS.actionplan.SummaryStage)
	 */
	@Override
	public void saveOrUpdate(SummaryStage summaryStage) throws Exception {
		getSession().saveOrUpdate(summaryStage);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#remove(lu.itrust.business.TS.actionplan.SummaryStage)
	 */
	@Override
	public void remove(SummaryStage summaryStage) throws Exception {
		getSession().delete(summaryStage);
	}

}
