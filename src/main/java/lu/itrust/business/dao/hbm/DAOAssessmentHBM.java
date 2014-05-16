package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.dao.DAOAssessment;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOAssessmentHBM.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOAssessmentHBM extends DAOHibernate implements DAOAssessment {

	/**
	 * Constructor: <br>
	 */
	public DAOAssessmentHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOAssessmentHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#get(int)
	 */
	@Override
	public Assessment get(Integer id) throws Exception {
		return (Assessment) getSession().get(Assessment.class, id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 *
	 * @param idAnalysis
	 * @param idAssessment
	 * @return
	 * @throws Exception
	 *
	 * @see lu.itrust.business.dao.DAOAssessment#getFromAnalysisById(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Assessment getFromAnalysisById(Integer idAnalysis, Integer idAssessment) throws Exception{
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and assessment.id = :idAssessment";
		return (Assessment) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idAssessment", idAssessment).uniqueResult();
	}
	
	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	public boolean belongsToAnalysis(Integer analysisId, Integer assessmentId) throws Exception {
		String query = "Select count(assessment) From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :analysisId and assessment.id = :assessmentId";
		return ((Long) getSession().createQuery(query).setParameter("analysisId", analysisId).setParameter("assessmentId", assessmentId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAll() throws Exception {
		return getSession().createQuery("From Assessment").list();
	}

	/**
	 * getAllFromAnalysisID: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllFromAnalysisID(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAssessmentFromAnalysisAndImpactLikelihoodAcronym: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAssessmentFromAnalysisAndImpactLikelihoodAcronym(int,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAnalysisAndImpactLikelihoodAcronym(Integer idAnalysis, String acronym) throws Exception {
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and (assessment.impactRep = :acronym or ";
		query += "assessment.impactOp = :acronym or assessment.impactLeg = :acronym or assessment.impactFin = :acronym or assessment.likelihood = :acronym)";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("acronym", acronym).list();
	}

	/**
	 * getAllFromAnalysisAndSelectedScenario: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllFromAnalysisAndSelectedScenario(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAnalysisAndSelectedScenario(Integer idAnalysis) throws Exception {
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and assessment.scenario.selected = true";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllFromScenarioId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllFromScenarioId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromScenario(Integer scenarioID) throws Exception {
		return getSession().createQuery("From Assessment where scenario.id = :scenarioId").setParameter("scenarioId", scenarioID).list();
	}

	/**
	 * getAllFromScenario: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllFromScenario(lu.itrust.business.TS.Scenario)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromScenario(Scenario scenario) throws Exception {
		return getSession().createQuery("From Assessment where scenario = :scenario").setParameter("scenario", scenario).list();
	}

	/**
	 * getAllSelectedAssessmentFromScenario: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllSelectedAssessmentFromScenario(lu.itrust.business.TS.Scenario)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllSelectedFromScenario(Scenario scenario) throws Exception {
		return getSession().createQuery("From Assessment where scenario = :scenario and selected = true").setParameter("scenario", scenario).list();
	}

	/**
	 * getAllUnselectedAssessmentFromScenario: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllUnselectedAssessmentFromScenario(lu.itrust.business.TS.Scenario)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllUnselectedFromScenario(Scenario scenario) throws Exception {
		return getSession().createQuery("From Assessment where scenario = :scenario and selected = false").setParameter("scenario", scenario).list();
	}

	/**
	 * getAllFromAnalysisAndSelectedAsset: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllFromAnalysisAndSelectedAsset(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAnalysisAndSelectedAsset(Integer idAnalysis) throws Exception {
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and assessment.asset.selected = true";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllFromAssetId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllFromAssetId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAsset(Integer assetID) throws Exception {
		return getSession().createQuery("From Assessment where asset.id = :assetID").setParameter("assetID", assetID).list();
	}

	/**
	 * getAllFromAsset: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllFromAsset(lu.itrust.business.TS.Asset)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAsset(Asset asset) throws Exception {
		return getSession().createQuery("From Assessment where asset = :asset").setParameter("asset", asset).list();
	}

	/**
	 * getAllSelectedAssessmentFromAsset: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllSelectedAssessmentFromAsset(lu.itrust.business.TS.Asset)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllSelectedFromAsset(Asset asset) throws Exception {
		return getSession().createQuery("From Assessment where asset = :asset and selected = true").setParameter("asset", asset).list();
	}

	/**
	 * getAllUnSelectedAssessmentFromAsset: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#getAllUnSelectedAssessmentFromAsset(lu.itrust.business.TS.Asset)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllUnSelectedFromAsset(Asset asset) throws Exception {
		return getSession().createQuery("From Assessment where asset = :asset and selected = false").setParameter("asset", asset).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#save(lu.itrust.business.TS.Assessment)
	 */
	@Override
	public void save(Assessment assessment) throws Exception {
		getSession().save(assessment);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#saveOrUpdate(lu.itrust.business.TS.Assessment)
	 */
	@Override
	public void saveOrUpdate(Assessment assessment) throws Exception {
		getSession().saveOrUpdate(assessment);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#saveOrUpdate(java.util.List)
	 */
	@Override
	public void saveOrUpdate(List<Assessment> assessments) throws Exception {
		for (Assessment assessment : assessments)
			getSession().saveOrUpdate(assessment);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssessment#delete(lu.itrust.business.TS.Assessment)
	 */
	@Override
	public void delete(Assessment assessment) throws Exception {
		getSession().delete(assessment);
	}
}