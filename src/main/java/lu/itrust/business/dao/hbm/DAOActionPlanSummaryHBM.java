package lu.itrust.business.dao.hbm;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.dao.DAOActionPlanSummary;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOActionPlanSummaryHBM.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOActionPlanSummaryHBM extends DAOHibernate implements DAOActionPlanSummary {

	/**
	 * Constructor: <br>
	 */
	public DAOActionPlanSummaryHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOActionPlanSummaryHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#get(int)
	 */
	@Override
	public SummaryStage get(int idSummaryStage) throws Exception {
		return (SummaryStage) getSession().get(SummaryStage.class, idSummaryStage);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public SummaryStage getFromAnalysisById(Integer idAnalysis, Integer idSummaryStage) throws Exception {
		String query = "Select summary From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :idAnalysis and summary.id = :idSummaryStage";
		return (SummaryStage) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idSummaryStage", idSummaryStage).uniqueResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlan#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(int actionPlanSummaryId, int analysisId) throws Exception {
		String query = "Select count(summary) From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :analysisId and summary.id = : actionPlanSummaryId";
		return ((Long) getSession().createQuery(query).setParameter("analysisId", analysisId).setParameter("actionPlanSummaryId", actionPlanSummaryId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SummaryStage> getAll() throws Exception {
		return getSession().createQuery("From SummaryStage").list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#getAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SummaryStage> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select summary From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :idAnalysis order by summary.id";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysis(Analysis analysis) throws Exception {
		return analysis.getSummaries();
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#getFromAnalysisAndActionPlanType(java.lang.Integer,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SummaryStage> getFromAnalysisAndActionPlanType(Integer idAnalysis, String actionPlanType) throws Exception {
		String query = "Select summary From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :idAnalysis and summary.actionPlanType.name = :actionPlanType ";
		query += "order by summary.id";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("actionPlanType", ActionPlanMode.getByName(actionPlanType)).list();
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#getFromAnalysisAndActionPlanType(lu.itrust.business.TS.Analysis,
	 *      lu.itrust.business.TS.actionplan.ActionPlanType)
	 */
	@Override
	public List<SummaryStage> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanType actionPlanType) throws Exception {
		List<SummaryStage> sumStages = new ArrayList<SummaryStage>();
		for (SummaryStage stage : sumStages) {
			if (stage.getActionPlanType().equals(actionPlanType))
				sumStages.add(stage);
		}
		return sumStages;
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#save(lu.itrust.business.TS.actionplan.SummaryStage)
	 */
	@Override
	public void save(SummaryStage summaryStage) throws Exception {
		getSession().save(summaryStage);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#saveOrUpdate(lu.itrust.business.TS.actionplan.SummaryStage)
	 */
	@Override
	public void saveOrUpdate(SummaryStage summaryStage) throws Exception {
		getSession().saveOrUpdate(summaryStage);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanSummary#delete(lu.itrust.business.TS.actionplan.SummaryStage)
	 */
	@Override
	public void delete(SummaryStage summaryStage) throws Exception {
		getSession().delete(summaryStage);
	}
}