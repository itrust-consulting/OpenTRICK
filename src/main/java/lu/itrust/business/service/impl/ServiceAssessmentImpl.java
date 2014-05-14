package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.dao.DAOAssessment;
import lu.itrust.business.service.ServiceAssessment;

/**
 * ServiceAssessmentImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#get(int)
	 */
	@Override
	public Assessment get(Integer id) throws Exception {
		return daoAssessment.get(id);
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
	 * @see lu.itrust.business.service.ServiceAssessment#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public Assessment getFromAnalysisById(Integer idAnalysis, Integer idAssessment) throws Exception {
		return daoAssessment.getFromAnalysisById(idAnalysis, idAssessment);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param assessmentId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer assessmentId, Integer analysisId) throws Exception {
		return daoAssessment.belongsToAnalysis(assessmentId, analysisId);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAll()
	 */
	@Override
	public List<Assessment> getAll() throws Exception {
		return daoAssessment.getAll();
	}

	/**
	 * getAllFromAnalysisID: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllFromAnalysisID(int)
	 */
	@Override
	public List<Assessment> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		return daoAssessment.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * getAssessmentFromAnalysisAndImpactLikelihoodAcronym: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param acronym
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAssessmentFromAnalysisAndImpactLikelihoodAcronym(int,
	 *      java.lang.String)
	 */
	@Override
	public List<Assessment> getAllFromAnalysisAndImpactLikelihoodAcronym(Integer idAnalysis, String acronym) throws Exception {
		return daoAssessment.getAllFromAnalysisAndImpactLikelihoodAcronym(idAnalysis, acronym);
	}

	/**
	 * getAllFromAnalysisAndSelectedScenario: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllFromAnalysisAndSelectedScenario(java.lang.Integer)
	 */
	@Override
	public List<Assessment> getAllFromAnalysisAndSelectedScenario(Integer idAnalysis) throws Exception {
		return daoAssessment.getAllFromAnalysisAndSelectedScenario(idAnalysis);
	}

	/**
	 * getAllFromScenarioId: <br>
	 * Description
	 * 
	 * @param scenarioID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllFromScenarioId(int)
	 */
	@Override
	public List<Assessment> getAllFromScenario(Integer scenarioID) throws Exception {
		return daoAssessment.getAllFromScenario(scenarioID);
	}

	/**
	 * getAllFromScenario: <br>
	 * Description
	 * 
	 * @param scenario
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllFromScenario(lu.itrust.business.TS.Scenario)
	 */
	@Override
	public List<Assessment> getAllFromScenario(Scenario scenario) throws Exception {
		return daoAssessment.getAllFromScenario(scenario);
	}

	/**
	 * getAllSelectedAssessmentFromScenario: <br>
	 * Description
	 * 
	 * @param scenario
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllSelectedAssessmentFromScenario(lu.itrust.business.TS.Scenario)
	 */
	@Override
	public List<Assessment> getAllSelectedFromScenario(Scenario scenario) throws Exception {
		return daoAssessment.getAllSelectedFromScenario(scenario);
	}

	/**
	 * getAllUnselectedAssessmentFromScenario: <br>
	 * Description
	 * 
	 * @param scenario
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllUnselectedAssessmentFromScenario(lu.itrust.business.TS.Scenario)
	 */
	@Override
	public List<Assessment> getAllUnselectedFromScenario(Scenario scenario) throws Exception {
		return daoAssessment.getAllUnselectedFromScenario(scenario);
	}

	/**
	 * getAllFromAnalysisAndSelectedAsset: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllFromAnalysisAndSelectedAsset(java.lang.Integer)
	 */
	@Override
	public List<Assessment> getAllFromAnalysisAndSelectedAsset(Integer idAnalysis) throws Exception {
		return daoAssessment.getAllFromAnalysisAndSelectedAsset(idAnalysis);
	}

	/**
	 * getAllFromAssetId: <br>
	 * Description
	 * 
	 * @param assetID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllFromAssetId(int)
	 */
	@Override
	public List<Assessment> getAllFromAsset(Integer assetID) throws Exception {
		return daoAssessment.getAllFromAsset(assetID);
	}

	/**
	 * getAllFromAsset: <br>
	 * Description
	 * 
	 * @param asset
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllFromAsset(lu.itrust.business.TS.Asset)
	 */
	@Override
	public List<Assessment> getAllFromAsset(Asset asset) throws Exception {
		return daoAssessment.getAllFromAsset(asset);
	}

	/**
	 * getAllSelectedAssessmentFromAsset: <br>
	 * Description
	 * 
	 * @param asset
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllSelectedAssessmentFromAsset(lu.itrust.business.TS.Asset)
	 */
	@Override
	public List<Assessment> getAllSelectedFromAsset(Asset asset) throws Exception {
		return daoAssessment.getAllSelectedFromAsset(asset);
	}

	/**
	 * getAllUnSelectedAssessmentFromAsset: <br>
	 * Description
	 * 
	 * @param asset
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#getAllUnSelectedAssessmentFromAsset(lu.itrust.business.TS.Asset)
	 */
	@Override
	public List<Assessment> getAllUnSelectedFromAsset(Asset asset) throws Exception {
		return daoAssessment.getAllUnSelectedFromAsset(asset);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param assessment
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#save(lu.itrust.business.TS.Assessment)
	 */
	@Transactional
	@Override
	public void save(Assessment assessment) throws Exception {
		daoAssessment.save(assessment);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param assessment
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#saveOrUpdate(lu.itrust.business.TS.Assessment)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Assessment assessment) throws Exception {
		daoAssessment.saveOrUpdate(assessment);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param assessments
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<Assessment> assessments) throws Exception {
		daoAssessment.saveOrUpdate(assessments);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param assessment
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#delete(lu.itrust.business.TS.Assessment)
	 */
	@Transactional
	@Override
	public void delete(Assessment assessment) throws Exception {
		daoAssessment.delete(assessment);
	}
}