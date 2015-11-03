package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOScenario;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;

/**
 * ServiceScenarioImpl.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.a.rl.
 * @version
 * @since May 13, 2014
 */
@Service
public class ServiceScenarioImpl implements ServiceScenario {

	@Autowired
	private DAOScenario daoScenario;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#get(int)
	 */
	@Override
	public Scenario get(Integer id) throws Exception {
		return daoScenario.get(id);
	}

	/**
	 * getScenarioFromAnalysisByScenarioId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#getScenarioFromAnalysisByScenarioId(int, int)
	 */
	@Override
	public Scenario getFromAnalysisById(Integer idAnalysis, Integer id) throws Exception {
		return daoScenario.getFromAnalysisById(idAnalysis, id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param scenarioId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer scenarioId) throws Exception {
		return daoScenario.belongsToAnalysis(analysisId, scenarioId);
	}

	/**
	 * exist: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#exist(java.lang.Integer, java.lang.String)
	 */
	@Override
	public boolean exist(Integer idAnalysis, String name) throws Exception {
		return daoScenario.exist(idAnalysis, name);
	}

	/**
	 * getAllScenarios: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#getAllScenarios()
	 */
	@Override
	public List<Scenario> getAll() throws Exception {
		return daoScenario.getAll();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#getAllFromAnalysisId(int)
	 */
	@Override
	public List<Scenario> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		return daoScenario.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * getAllFromAnalysisIdAndSelected: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#getAllFromAnalysisIdAndSelected(int)
	 */
	@Override
	public List<Scenario> getAllSelectedFromAnalysis(Integer idAnalysis) throws Exception {
		return daoScenario.getAllSelectedFromAnalysis(idAnalysis);
	}

	/**
	 * getAllFromAnalysisByScenarioTypeId: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param scenarioTypeID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#getAllFromAnalysisByScenarioTypeId(lu.itrust.business.TS.model.analysis.Analysis,
	 *      int)
	 */
	@Override
	public List<Scenario> getAllFromAnalysisByType(Integer idAnalysis, ScenarioType scenarioType) throws Exception {
		return daoScenario.getAllFromAnalysisByType(idAnalysis, scenarioType);
	}

	/**
	 * getAllFromAnalysisByScenarioTypeId: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param scenarioTypeID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#getAllFromAnalysisByScenarioTypeId(lu.itrust.business.TS.model.analysis.Analysis,
	 *      int)
	 */
	@Override
	public List<Scenario> getAllSelectedFromAnalysisByType(Integer idAnalysis, ScenarioType scenarioType) throws Exception {
		return daoScenario.getAllSelectedFromAnalysisByType(idAnalysis, scenarioType);
	}
	
	/**
	 * getAllScenariosFromAnalysisByScenarioIdList: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param scenarios
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#getAllScenariosFromAnalysisByScenarioIdList(int,
	 *      java.util.List)
	 */
	@Override
	public List<Scenario> getAllFromAnalysisByIdList(Integer idAnalysis, List<Integer> scenarios) throws Exception {
		return daoScenario.getAllFromAnalysisByIdList(idAnalysis, scenarios);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param scenario
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#saveOrUpdate(lu.itrust.business.TS.model.scenario.Scenario)
	 */
	@Transactional
	@Override
	public void save(Scenario scenario) throws Exception {
		daoScenario.save(scenario);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param scenario
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#saveOrUpdate(lu.itrust.business.TS.model.scenario.Scenario)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Scenario scenario) throws Exception {
		daoScenario.saveOrUpdate(scenario);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param scenario
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#merge(lu.itrust.business.TS.model.scenario.Scenario)
	 */
	@Transactional
	@Override
	public Scenario merge(Scenario scenario) throws Exception {
		return daoScenario.merge(scenario);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param scenario
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScenario#delete(lu.itrust.business.TS.model.scenario.Scenario)
	 */
	@Transactional
	@Override
	public void delete(Scenario scenario) throws Exception {
		daoScenario.delete(scenario);
	}

	@Override
	public Scenario getByNameAndAnalysisId(String name, int analysisId) {
		return daoScenario.getByNameAndAnalysisId(name,analysisId);
	}
}