/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.dao.DAOScenario;
import lu.itrust.business.service.ServiceScenario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author eom
 *
 */
@Service
public class ServiceScenarioImpl implements ServiceScenario {

	@Autowired
	private DAOScenario daoScenario;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceScenario#get(int)
	 */
	@Override
	public Scenario get(int id) throws Exception {
		return daoScenario.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceScenario#loadFromNameAnalysis(java.lang.String, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public Scenario loadFromNameAnalysis(String scenarioName, Analysis analysis)
			throws Exception {
		return daoScenario.loadFromNameAnalysis(scenarioName, analysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceScenario#loadAllFromAnalysisID(int)
	 */
	@Override
	public List<Scenario> loadAllFromAnalysisID(int idAnalysis)
			throws Exception {
		return daoScenario.findByAnalysis(idAnalysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceScenario#loadAll()
	 */
	@Override
	public List<Scenario> loadAll() throws Exception {
		return daoScenario.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceScenario#save(lu.itrust.business.TS.Scenario)
	 */
	@Transactional
	@Override
	public void save(Scenario scenario) throws Exception {
		daoScenario.save(scenario);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceScenario#saveOrUpdate(lu.itrust.business.TS.Scenario)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Scenario scenario) throws Exception {
		daoScenario.saveOrUpdate(scenario);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceScenario#remove(lu.itrust.business.TS.Scenario)
	 */
	@Transactional
	@Override
	public void remove(Scenario scenario) throws Exception {
		daoScenario.remove(scenario);
	}

	@Transactional
	@Override
	public Scenario merge(Scenario scenario) {
		return daoScenario.merge(scenario);
	}

	@Override
	public List<Scenario> findByAnalysisAndSelected(int idAnalysis) {
		return daoScenario.findByAnalysisAndSelected(idAnalysis);
	}

	@Override
	public Scenario findByIdAndAnalysis(int id, int idAnalysis) {
		return daoScenario.findByIdAndAnalysis(id, idAnalysis);
	}

}
