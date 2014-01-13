/**
 * 
 */
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
 * @author eom
 * 
 */
@Service
public class ServiceAssessmentImpl implements ServiceAssessment {

	@Autowired
	private DAOAssessment daoAssessment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#get(int)
	 */
	@Override
	public Assessment get(int id) throws Exception {
		return daoAssessment.get(id);
	}
	
	@Override
	public List<Assessment> findByAnalysisAndAcronym(int idAnalysis,
			String acronym) {
		
		return daoAssessment.findByAnalysisAndAcronym(idAnalysis,acronym);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#loadAllFromScenario(lu.itrust
	 * .business.TS.Scenario)
	 */
	@Override
	public List<Assessment> loadAllFromScenario(Scenario scenario)
			throws Exception {
		return daoAssessment.loadAllFromScenario(scenario);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#loadAllFromScenarioId(int)
	 */
	@Override
	public List<Assessment> loadAllFromScenarioId(int scenarioID)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#loadAllFromAsset(lu.itrust
	 * .business.TS.Asset)
	 */
	@Override
	public List<Assessment> loadAllFromAsset(Asset asset) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#loadAllFromAssetId(int)
	 */
	@Override
	public List<Assessment> loadAllFromAssetId(int assetID) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#loadAllFromAnalysisID(int)
	 */
	@Override
	public List<Assessment> loadAllFromAnalysisID(int idAnalysis)
			throws Exception {
		return daoAssessment.loadAllFromAnalysisID(idAnalysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAssessment#loadAll()
	 */
	@Override
	public List<Assessment> loadAll() throws Exception {
		return daoAssessment.loadAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#save(lu.itrust.business.
	 * TS.Assessment)
	 */
	@Transactional
	@Override
	public void save(Assessment assessment) throws Exception {
		daoAssessment.save(assessment);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#saveOrUpdate(lu.itrust.business
	 * .TS.Assessment)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Assessment assessment) throws Exception {
		daoAssessment.saveOrUpdate(assessment);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#remove(lu.itrust.business
	 * .TS.Assessment)
	 */
	@Transactional
	@Override
	public void remove(Assessment assessment) throws Exception {
		daoAssessment.remove(assessment);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#saveOrUpdate(java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(List<Assessment> assessments) {
		daoAssessment.saveOrUpdate(assessments);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#findByAssetAndUnselected
	 * (lu.itrust.business.TS.Asset)
	 */
	@Override
	public List<Assessment> findByAssetAndUnselected(Asset asset) {
		return daoAssessment.findByAssetAndUnselected(asset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#findByAssetAndSelected(lu
	 * .itrust.business.TS.Asset)
	 */
	@Override
	public List<Assessment> findByAssetAndSelected(Asset asset) {
		return daoAssessment.findByAssetAndSelected(asset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#findByScenarioAndSelected
	 * (lu.itrust.business.TS.Scenario)
	 */
	@Override
	public List<Assessment> findByScenarioAndSelected(Scenario scenario) {
		return daoAssessment.findByScenarioAndSelected(scenario);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssessment#findByScenarioAndUnselected
	 * (lu.itrust.business.TS.Scenario)
	 */
	@Override
	public List<Assessment> findByScenarioAndUnselected(Scenario scenario) {
		return daoAssessment.findByScenarioAndUnselected(scenario);
	}

}
