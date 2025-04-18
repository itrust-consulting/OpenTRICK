package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOScenario;
import lu.itrust.business.ts.database.service.ServiceScenario;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.scenario.ScenarioType;

/**
 * ServiceScenarioImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since May 13, 2014
 */
@Service
@Transactional(readOnly = true)
public class ServiceScenarioImpl implements ServiceScenario {

	@Autowired
	private DAOScenario daoScenario;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#get(int)
	 */
	@Override
	public Scenario get(Integer id)  {
		return daoScenario.get(id);
	}

	/**
	 * getScenarioFromAnalysisByScenarioId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#getScenarioFromAnalysisByScenarioId(int, int)
	 */
	@Override
	public Scenario getFromAnalysisById(Integer idAnalysis, Integer id)  {
		return daoScenario.getFromAnalysisById(idAnalysis, id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param scenarioId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer scenarioId)  {
		return daoScenario.belongsToAnalysis(analysisId, scenarioId);
	}

	/**
	 * exist: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#exist(java.lang.Integer, java.lang.String)
	 */
	@Override
	public boolean exist(Integer idAnalysis, String name)  {
		return daoScenario.exist(idAnalysis, name);
	}

	/**
	 * getAllScenarios: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#getAllScenarios()
	 */
	@Override
	public List<Scenario> getAll()  {
		return daoScenario.getAll();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#getAllFromAnalysisId(int)
	 */
	@Override
	public List<Scenario> getAllFromAnalysis(Integer idAnalysis)  {
		return daoScenario.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * getAllFromAnalysisIdAndSelected: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#getAllFromAnalysisIdAndSelected(int)
	 */
	@Override
	public List<Scenario> getAllSelectedFromAnalysis(Integer idAnalysis)  {
		return daoScenario.getAllSelectedFromAnalysis(idAnalysis);
	}

	/**
	 * getAllFromAnalysisByScenarioTypeId: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param scenarioTypeID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#getAllFromAnalysisByScenarioTypeId(lu.itrust.business.ts.model.analysis.Analysis,
	 *      int)
	 */
	@Override
	public List<Scenario> getAllFromAnalysisByType(Integer idAnalysis, ScenarioType scenarioType)  {
		return daoScenario.getAllFromAnalysisByType(idAnalysis, scenarioType);
	}

	/**
	 * getAllFromAnalysisByScenarioTypeId: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param scenarioTypeID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#getAllFromAnalysisByScenarioTypeId(lu.itrust.business.ts.model.analysis.Analysis,
	 *      int)
	 */
	@Override
	public List<Scenario> getAllSelectedFromAnalysisByType(Integer idAnalysis, ScenarioType scenarioType)  {
		return daoScenario.getAllSelectedFromAnalysisByType(idAnalysis, scenarioType);
	}
	
	/**
	 * getAllScenariosFromAnalysisByScenarioIdList: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param scenarios
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#getAllScenariosFromAnalysisByScenarioIdList(int,
	 *      java.util.List)
	 */
	@Override
	public List<Scenario> getAllFromAnalysisByIdList(Integer idAnalysis, List<Integer> scenarios)  {
		return daoScenario.getAllFromAnalysisByIdList(idAnalysis, scenarios);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param scenario
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#saveOrUpdate(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@Transactional
	@Override
	public void save(Scenario scenario)  {
		daoScenario.save(scenario);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param scenario
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#saveOrUpdate(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Scenario scenario)  {
		daoScenario.saveOrUpdate(scenario);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param scenario
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#merge(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@Transactional
	@Override
	public Scenario merge(Scenario scenario)  {
		return daoScenario.merge(scenario);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param scenario
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScenario#delete(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@Transactional
	@Override
	public void delete(Scenario scenario)  {
		daoScenario.delete(scenario);
	}

	@Override
	public Scenario getByNameAndAnalysisId(String name, int analysisId) {
		return daoScenario.getByNameAndAnalysisId(name,analysisId);
	}

	@Override
	public boolean belongsToAnalysis(Integer analysisId, List<Integer> scenarioIds) {
		return daoScenario.belongsToAnalysis(analysisId, scenarioIds);
	}
}