/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.dao.DAOAssessment;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * @author eom
 * 
 */
@Repository
public class DAOAssessmentHBM extends DAOHibernate implements DAOAssessment {

	/**
	 * 
	 */
	public DAOAssessmentHBM() {
	}

	/**
	 * @param session
	 */
	public DAOAssessmentHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#get(int)
	 */
	@Override
	public Assessment get(int id) throws Exception {
		return (Assessment) getSession().get(Assessment.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAssessment#loadAllFromScenario(lu.itrust.business
	 * .TS.Scenario)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> loadAllFromScenario(Scenario scenario)
			throws Exception {
		return  getSession().createQuery(
				"From Assessment where scenario = :scenario").setParameter(
				"scenario", scenario).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#loadAllFromScenarioId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> loadAllFromScenarioId(int scenarioID)
			throws Exception {
		return getSession().createQuery(
				"From Assessment where scenario.id = :scenarioId")
				.setParameter("scenarioId", scenarioID).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAssessment#loadAllFromAsset(lu.itrust.business
	 * .TS.Asset)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> loadAllFromAsset(Asset asset) throws Exception {
		return getSession().createQuery(
				"From Assessment where asset = :asset").setParameter(
				"asset", asset).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#loadAllFromAssetId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> loadAllFromAssetId(int assetID) throws Exception {
		return  getSession().createQuery(
				"From Assessment where asset.id = :assetID").setParameter(
				"assetID", assetID).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#loadAllFromAnalysisID(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> loadAllFromAnalysisID(int idAnalysis)
			throws Exception {
		return getSession()
				.createQuery(
						"Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis")
				.setParameter("idAnalysis", idAnalysis).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> loadAll() throws Exception {
		return  getSession().createQuery("From Assessment")
				.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAssessment#save(lu.itrust.business.TS.Assessment
	 * )
	 */
	@Override
	public void save(Assessment assessment) throws Exception {
		getSession().save(assessment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAssessment#saveOrUpdate(lu.itrust.business.
	 * TS.Assessment)
	 */
	@Override
	public void saveOrUpdate(Assessment assessment) throws Exception {
		getSession().saveOrUpdate(assessment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAssessment#remove(lu.itrust.business.TS.Assessment
	 * )
	 */
	@Override
	public void remove(Assessment assessment) throws Exception {
		getSession().delete(assessment);

	}

	@Override
	public void saveOrUpdate(List<Assessment> assessments) {
		for (Assessment assessment : assessments)
			getSession().saveOrUpdate(assessment);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> findByAssetAndUnselected(Asset asset) {
		return getSession().createQuery(
				"From Assessment where asset = :asset and selected = false").setParameter(
				"asset", asset).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> findByAssetAndSelected(Asset asset) {
		return getSession().createQuery(
				"From Assessment where asset = :asset and selected = true").setParameter(
				"asset", asset).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> findByScenarioAndSelected(Scenario scenario) {
		return getSession().createQuery(
				"From Assessment where scenario = :scenario and selected = true").setParameter(
				"scenario", scenario).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> findByScenarioAndUnselected(Scenario scenario) {
		return getSession().createQuery(
				"From Assessment where scenario = :scenario and selected = false").setParameter(
				"scenario", scenario).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> findByAnalysisAndSelectedScenario(Integer idAnalysis) {
		return getSession()
		.createQuery(
				"Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and assessment.scenario.selected = true")
		.setParameter("idAnalysis", idAnalysis).list();
	}

}
