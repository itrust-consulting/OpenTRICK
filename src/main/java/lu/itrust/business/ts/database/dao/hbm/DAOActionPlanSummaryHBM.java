package lu.itrust.business.ts.database.dao.hbm;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOActionPlanSummary;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.actionplan.ActionPlanType;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStage;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStandardConformance;
import lu.itrust.business.ts.model.analysis.Analysis;

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
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#get(int)
	 */
	@Override
	public SummaryStage get(Integer idSummaryStage) {
		return (SummaryStage) getSession().get(SummaryStage.class, idSummaryStage);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SummaryStage getFromAnalysisById(Integer idAnalysis, Integer idSummaryStage)  {
		String query = "Select summary From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :idAnalysis and summary.id = :idSummaryStage";
		return (SummaryStage) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idSummaryStage", idSummaryStage).uniqueResultOptional().orElse(null);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlan#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanSummaryId)  {
		String query = "Select count(summary)>0 From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :analysisId and summary.id = :actionPlanSummaryId";
		return (boolean) getSession().createQuery(query).setParameter("analysisId", analysisId).setParameter("actionPlanSummaryId", actionPlanSummaryId).getSingleResult();
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SummaryStage> getAll()  {
		return getSession().createQuery("From SummaryStage").getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#getAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SummaryStage> getAllFromAnalysis(Integer idAnalysis)  {
		String query = "Select summary From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :idAnalysis order by summary.id";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#getAllFromAnalysis(lu.itrust.business.ts.model.analysis.Analysis)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysis(Analysis analysis)  {
		return analysis.getSummaries();
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#getFromAnalysisAndActionPlanType(java.lang.Integer,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Integer idAnalysis, String actionPlanType)  {
		String query = "Select summary From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :idAnalysis and summary.actionPlanType.name = :actionPlanType order by summary.id";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("actionPlanType", ActionPlanMode.getByName(actionPlanType)).getResultList();
	}

	/**
	 * getFromAnalysisAndActionPlanType: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#getFromAnalysisAndActionPlanType(lu.itrust.business.ts.model.analysis.Analysis,
	 *      lu.itrust.business.ts.model.actionplan.ActionPlanType)
	 */
	@Override
	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanType actionPlanType)  {
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
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#save(lu.itrust.business.ts.model.actionplan.summary.SummaryStage)
	 */
	@Override
	public void save(SummaryStage summaryStage)  {
		getSession().save(summaryStage);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#saveOrUpdate(lu.itrust.business.ts.model.actionplan.summary.SummaryStage)
	 */
	@Override
	public void saveOrUpdate(SummaryStage summaryStage)  {
		getSession().saveOrUpdate(summaryStage);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#delete(lu.itrust.business.ts.model.actionplan.summary.SummaryStage)
	 */
	@Override
	public void delete(SummaryStage summaryStage)  {
		getSession().delete(summaryStage);
	}

	/**
	 * deleteAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanSummary#deleteAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteAllFromAnalysis(Integer analysisID)  {
		String query = "Select summary From Analysis as analysis inner join analysis.summaries as summary where analysis.id = :idAnalysis";
		List<SummaryStage> summaries = (List<SummaryStage>) getSession().createQuery(query).setParameter("idAnalysis", analysisID).getResultList();
		for(SummaryStage summary : summaries){
			for(SummaryStandardConformance conformance : summary.getConformances())
				getSession().delete(conformance);
			getSession().delete(summary);
		}
	}
}