package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.dao.DAOActionPlan;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/** 
 * DAOActionPlanHBM.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.�.rl. :
 * @version 
 * @since Feb 5, 2013
 */
@Repository
public class DAOActionPlanHBM extends DAOHibernate implements DAOActionPlan {
	
	/**
	 * 
	 */
	public DAOActionPlanHBM() {
	}

	/**
	 * @param sessionFactory
	 */
	public DAOActionPlanHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#get(int)
	 */
	@Override
	public ActionPlanEntry get(int id) throws Exception {
		return (ActionPlanEntry) getSession().get(ActionPlanEntry.class, id);
	}

	/**
	 * get: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#get(lu.itrust.business.TS.actionplan.ActionPlanType, lu.itrust.business.TS.Measure)
	 */
	@Override
	public ActionPlanEntry get(ActionPlanType actionPlanType, Measure measure) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllFromActionPlanType: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#findByActionPlanType(lu.itrust.business.TS.actionplan.ActionPlanType)
	 */
	@Override
	public List<ActionPlanEntry> findByActionPlanType(ActionPlanType actionPlanType)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllFromAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#findByAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<ActionPlanEntry> findByAnalysis(Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllFromAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#loadAllFromAnalysis(int, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ActionPlanEntry> loadAllFromAnalysis(int identifier, String version,
			String creationDate) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAll: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#loadAll()
	 */
	@Override
	public List<ActionPlanEntry> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#save(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Override
	public void save(ActionPlanEntry actionPlanEntry) throws Exception {
		getSession().save(actionPlanEntry);

	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#saveOrUpdate(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Override
	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) throws Exception {
		getSession().saveOrUpdate(actionPlanEntry);

	}

	/**
	 * remove: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOActionPlan#delete(lu.itrust.business.TS.actionplan.ActionPlanEntry)
	 */
	@Override
	public void delete(ActionPlanEntry actionPlanEntry) throws Exception {
		getSession().delete(actionPlanEntry);

	}

	@Override
	public List<ActionPlanEntry> loadByAnalysisActionPlanType(Analysis analysis, ActionPlanMode mode) throws Exception {
		
		return analysis.getActionPlan(mode);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> loadByAnalysisActionPlanType(int analysisID, ActionPlanMode mode) throws Exception {
		
		return (List<ActionPlanEntry>) getSession().createQuery
		("SELECT actionplans From Analysis As analysis INNER JOIN analysis.actionPlans As actionplans where analysis.id = :analysisID and actionplans.actionPlanType.name = :mode ORDER BY actionplan.actionPlanType.id, actionplan.ROI")
		.setParameter("mode", mode)
		.setParameter("analysisID", analysisID)
		.list();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanEntry> loadAllFromAnalysis(int id) throws Exception {
		return (List<ActionPlanEntry>) getSession().createQuery("Select actionplan From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID ORDER BY actionplan.actionPlanType.id, actionplan.ROI").setParameter("analysisID", id).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> loadMeasuresFromAnalysisActionPlan(int id, ActionPlanMode apm) throws Exception {
		return (List<Measure>) getSession().createQuery
				("Select actionplan.measure From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID and actionplan.actionPlanType.name = :apm")
				.setParameter("analysisID", id)
				.setParameter("apm", apm)
				.list();
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> loadMeasuresFromAnalysisActionPlanNotToImplement(int id, ActionPlanMode apm) throws Exception {
		return (List<Measure>) getSession().createQuery
				("Select actionplan.measure From Analysis a inner join a.actionPlans actionplan where a.id = :analysisID and actionplan.actionPlanType.name = :apm and actionplan.ROI <= 0.0")
				.setParameter("analysisID", id)
				.setParameter("apm", apm)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> loadAssetsByAnalysisOrderByALE(int analysisID) throws Exception {

		return (List<Asset>) getSession().createQuery
				("SELECT DISTINCT apa.asset FROM Analysis a INNER JOIN ActionPlanAsset apa WHERE a.id= : analysisID").list();
		}
}
