package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOAssessment;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.scenario.Scenario;

/**
 * DAOAssessmentImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOAssessmentImpl extends DAOHibernate implements DAOAssessment {

	/**
	 * Constructor: <br>
	 */
	public DAOAssessmentImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOAssessmentImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#get(int)
	 */
	@Override
	public Assessment get(Integer id) {
		return (Assessment) getSession().get(Assessment.class, id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 *
	 * @param idAnalysis
	 * @param idAssessment
	 * @return
	 * @
	 *
	 *   @see
	 *   lu.itrust.business.ts.database.dao.DAOAssessment#getFromAnalysisById(java.lang.Integer,
	 *   java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Assessment getFromAnalysisById(Integer idAnalysis, Integer idAssessment) {
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and assessment.id = :idAssessment";
		return (Assessment) createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).setParameter("idAssessment", idAssessment).uniqueResultOptional().orElse(null);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	public boolean belongsToAnalysis(Integer analysisId, Integer assessmentId) {
		String query = "Select count(assessment) > 0 From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :analysisId and assessment.id = :assessmentId";
		return (boolean) createQueryWithCache(query).setParameter("analysisId", analysisId).setParameter("assessmentId", assessmentId).getSingleResult();
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAll() {
		return createQueryWithCache("From Assessment").getResultList();
	}

	/**
	 * getAllFromAnalysisID: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllFromAnalysisID(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAnalysis(Integer idAnalysis) {
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getAssessmentFromAnalysisAndImpactLikelihoodAcronym: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAssessmentFromAnalysisAndImpactLikelihoodAcronym(int,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAnalysisAndImpactLikelihoodAcronym(Integer idAnalysis, String acronym) {
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and (assessment.impactRep = :acronym or ";
		query += "assessment.impactOp = :acronym or assessment.impactLeg = :acronym or assessment.impactFin = :acronym or assessment.likelihood = :acronym)";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).setParameter("acronym", acronym).getResultList();
	}

	/**
	 * getAllFromAnalysisAndSelectedScenario: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllFromAnalysisAndSelectedScenario(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAnalysisAndSelectedScenario(Integer idAnalysis) {
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and assessment.scenario.selected = true";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getAllFromScenarioId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllFromScenarioId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromScenario(Integer scenarioID) {
		return createQueryWithCache("From Assessment where scenario.id = :scenarioId").setParameter("scenarioId", scenarioID).getResultList();
	}

	/**
	 * getAllFromScenario: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllFromScenario(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromScenario(Scenario scenario) {
		return createQueryWithCache("From Assessment where scenario = :scenario").setParameter("scenario", scenario).getResultList();
	}

	/**
	 * getAllSelectedAssessmentFromScenario: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllSelectedAssessmentFromScenario(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllSelectedFromScenario(Scenario scenario) {
		return createQueryWithCache("From Assessment where scenario = :scenario and selected = true").setParameter("scenario", scenario).getResultList();
	}

	/**
	 * getAllUnselectedAssessmentFromScenario: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllUnselectedAssessmentFromScenario(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllUnselectedFromScenario(Scenario scenario) {
		return createQueryWithCache("From Assessment where scenario = :scenario and selected = false").setParameter("scenario", scenario).getResultList();
	}

	/**
	 * getAllFromAnalysisAndSelectedAsset: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllFromAnalysisAndSelectedAsset(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAnalysisAndSelectedAsset(Integer idAnalysis) {
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and assessment.asset.selected = true";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getAllFromAssetId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllFromAssetId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAsset(Integer assetID) {
		return createQueryWithCache("From Assessment where asset.id = :assetID").setParameter("assetID", assetID).getResultList();
	}

	/**
	 * getAllFromAsset: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllFromAsset(lu.itrust.business.ts.model.asset.Asset)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAsset(Asset asset) {
		return createQueryWithCache("From Assessment where asset = :asset").setParameter("asset", asset).getResultList();
	}

	/**
	 * getAllSelectedAssessmentFromAsset: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllSelectedAssessmentFromAsset(lu.itrust.business.ts.model.asset.Asset)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllSelectedFromAsset(Asset asset) {
		return createQueryWithCache("From Assessment where asset = :asset and selected = true").setParameter("asset", asset).getResultList();
	}

	/**
	 * getAllUnSelectedAssessmentFromAsset: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#getAllUnSelectedAssessmentFromAsset(lu.itrust.business.ts.model.asset.Asset)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllUnSelectedFromAsset(Asset asset) {
		return createQueryWithCache("From Assessment where asset = :asset and selected = false").setParameter("asset", asset).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#save(lu.itrust.business.ts.model.assessment.Assessment)
	 */
	@Override
	public void save(Assessment assessment) {
		getSession().save(assessment);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#saveOrUpdate(lu.itrust.business.ts.model.assessment.Assessment)
	 */
	@Override
	public void saveOrUpdate(Assessment assessment) {
		getSession().saveOrUpdate(assessment);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#saveOrUpdate(java.util.List)
	 */
	@Override
	public void saveOrUpdate(List<Assessment> assessments) {
		for (Assessment assessment : assessments)
			getSession().saveOrUpdate(assessment);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssessment#delete(lu.itrust.business.ts.model.assessment.Assessment)
	 */
	@Override
	public void delete(Assessment assessment) {
		getSession().delete(assessment);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Assessment> getAllFromAnalysisAndSelected(Integer idAnalysis) {
		String query = "Select assessment From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and assessment.selected = true";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Assessment getByAssetAndScenario(Asset asset, Scenario scenario) {
		return (Assessment) createQueryWithCache("From Assessment  where scenario = :scenario and asset = :asset").setParameter("asset", asset)
				.setParameter("scenario", scenario).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Assessment getByAssetAndScenario(int idAsset, int idScenario) {
		return (Assessment) createQueryWithCache("From Assessment  where scenario.id = :idScenario and asset.id = :idAsset").setParameter("idAsset", idAsset)
				.setParameter("idScenario", idScenario).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDistinctOwnerByIdAnalysis(Integer analysisId) {
		return createQueryWithCache(
				"Select distinct assessment.owner From Analysis as analysis inner join analysis.assessments as assessment where analysis.id = :idAnalysis and assessment.selected = true and assessment.owner<>''")
				.setParameter("idAnalysis", analysisId).getResultList();
	}

	@Override
	public void delete(IValue impact) {
		getSession().delete(impact);
	}

}