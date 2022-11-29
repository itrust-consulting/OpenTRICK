package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOAssessment;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * ServiceAssessmentImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Transactional(readOnly = true)
@Service
public class ServiceAssessmentImpl implements ServiceAssessment {

	@Autowired
	private DAOAssessment daoAssessment;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#get(int)
	 */
	@Override
	public Assessment get(Integer id)  {
		return daoAssessment.get(id);
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
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public Assessment getFromAnalysisById(Integer idAnalysis, Integer idAssessment)  {
		return daoAssessment.getFromAnalysisById(idAnalysis, idAssessment);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param assessmentId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer assessmentId)  {
		return daoAssessment.belongsToAnalysis(analysisId, assessmentId);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAll()
	 */
	@Override
	public List<Assessment> getAll()  {
		return daoAssessment.getAll();
	}

	/**
	 * getAllFromAnalysisID: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllFromAnalysisID(int)
	 */
	@Override
	public List<Assessment> getAllFromAnalysis(Integer idAnalysis)  {
		return daoAssessment.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * getAssessmentFromAnalysisAndImpactLikelihoodAcronym: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param acronym
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAssessmentFromAnalysisAndImpactLikelihoodAcronym(int,
	 *      java.lang.String)
	 */
	@Override
	public List<Assessment> getAllFromAnalysisAndImpactLikelihoodAcronym(Integer idAnalysis, String acronym)  {
		return daoAssessment.getAllFromAnalysisAndImpactLikelihoodAcronym(idAnalysis, acronym);
	}

	/**
	 * getAllFromAnalysisAndSelectedScenario: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllFromAnalysisAndSelectedScenario(java.lang.Integer)
	 */
	@Override
	public List<Assessment> getAllFromAnalysisAndSelectedScenario(Integer idAnalysis)  {
		return daoAssessment.getAllFromAnalysisAndSelectedScenario(idAnalysis);
	}

	/**
	 * getAllFromScenarioId: <br>
	 * Description
	 * 
	 * @param scenarioID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllFromScenarioId(int)
	 */
	@Override
	public List<Assessment> getAllFromScenario(Integer scenarioID)  {
		return daoAssessment.getAllFromScenario(scenarioID);
	}

	/**
	 * getAllFromScenario: <br>
	 * Description
	 * 
	 * @param scenario
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllFromScenario(lu.itrust.business.TS.model.scenario.Scenario)
	 */
	@Override
	public List<Assessment> getAllFromScenario(Scenario scenario)  {
		return daoAssessment.getAllFromScenario(scenario);
	}

	/**
	 * getAllSelectedAssessmentFromScenario: <br>
	 * Description
	 * 
	 * @param scenario
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllSelectedAssessmentFromScenario(lu.itrust.business.TS.model.scenario.Scenario)
	 */
	@Override
	public List<Assessment> getAllSelectedFromScenario(Scenario scenario)  {
		return daoAssessment.getAllSelectedFromScenario(scenario);
	}

	/**
	 * getAllUnselectedAssessmentFromScenario: <br>
	 * Description
	 * 
	 * @param scenario
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllUnselectedAssessmentFromScenario(lu.itrust.business.TS.model.scenario.Scenario)
	 */
	@Override
	public List<Assessment> getAllUnselectedFromScenario(Scenario scenario)  {
		return daoAssessment.getAllUnselectedFromScenario(scenario);
	}

	/**
	 * getAllFromAnalysisAndSelectedAsset: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllFromAnalysisAndSelectedAsset(java.lang.Integer)
	 */
	@Override
	public List<Assessment> getAllFromAnalysisAndSelectedAsset(Integer idAnalysis)  {
		return daoAssessment.getAllFromAnalysisAndSelectedAsset(idAnalysis);
	}

	/**
	 * getAllFromAssetId: <br>
	 * Description
	 * 
	 * @param assetID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllFromAssetId(int)
	 */
	@Override
	public List<Assessment> getAllFromAsset(Integer assetID)  {
		return daoAssessment.getAllFromAsset(assetID);
	}

	/**
	 * getAllFromAsset: <br>
	 * Description
	 * 
	 * @param asset
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllFromAsset(lu.itrust.business.TS.model.asset.Asset)
	 */
	@Override
	public List<Assessment> getAllFromAsset(Asset asset)  {
		return daoAssessment.getAllFromAsset(asset);
	}

	/**
	 * getAllSelectedAssessmentFromAsset: <br>
	 * Description
	 * 
	 * @param asset
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllSelectedAssessmentFromAsset(lu.itrust.business.TS.model.asset.Asset)
	 */
	@Override
	public List<Assessment> getAllSelectedFromAsset(Asset asset)  {
		return daoAssessment.getAllSelectedFromAsset(asset);
	}

	/**
	 * getAllUnSelectedAssessmentFromAsset: <br>
	 * Description
	 * 
	 * @param asset
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#getAllUnSelectedAssessmentFromAsset(lu.itrust.business.TS.model.asset.Asset)
	 */
	@Override
	public List<Assessment> getAllUnSelectedFromAsset(Asset asset)  {
		return daoAssessment.getAllUnSelectedFromAsset(asset);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param assessment
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#save(lu.itrust.business.TS.model.assessment.Assessment)
	 */
	@Transactional
	@Override
	public void save(Assessment assessment)  {
		daoAssessment.save(assessment);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param assessment
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#saveOrUpdate(lu.itrust.business.TS.model.assessment.Assessment)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Assessment assessment)  {
		daoAssessment.saveOrUpdate(assessment);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param assessments
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<Assessment> assessments)  {
		daoAssessment.saveOrUpdate(assessments);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param assessment
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssessment#delete(lu.itrust.business.TS.model.assessment.Assessment)
	 */
	@Transactional
	@Override
	public void delete(Assessment assessment)  {
		daoAssessment.delete(assessment);
	}

	@Override
	public List<Assessment> getAllFromAnalysisAndSelected(Integer idAnalysis) {
		return daoAssessment.getAllFromAnalysisAndSelected(idAnalysis);
	}

	@Override
	public Assessment getByAssetAndScenario(Asset asset, Scenario scenario) {
		return daoAssessment.getByAssetAndScenario(asset, scenario);
	}

	@Override
	public Assessment getByAssetAndScenario(int idAsset, int idScenario) {
		return daoAssessment.getByAssetAndScenario(idAsset, idScenario);
	}

	@Override
	public List<String> getDistinctOwnerByIdAnalysis(Integer analysisId) {
		return daoAssessment.getDistinctOwnerByIdAnalysis(analysisId);
	}

	@Transactional
	@Override
	public void delete(IValue impact) {
		daoAssessment.delete(impact);
	}
}