package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOScenario;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.scenario.ScenarioType;

/**
 * DAOScenarioImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 22 janv. 2013
 */
@Repository
public class DAOScenarioImpl extends DAOHibernate implements DAOScenario {

	/**
	 * Constructor: <br>
	 */
	public DAOScenarioImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOScenarioImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#get(int)
	 */
	@Override
	public Scenario get(Integer id)  {
		return (Scenario) getSession().get(Scenario.class, id);
	}

	/**
	 * getScenarioFromAnalysisByScenarioId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#getScenarioFromAnalysisByScenarioId(int,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Scenario getFromAnalysisById(Integer idAnalysis, Integer scenarioId)  {
		String query = "Select scenario From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :idAnalysis and scenario.id = :idScenario";
		return (Scenario) createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).setParameter("idScenario", scenarioId).uniqueResultOptional().orElse(null);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer scenarioId)  {
		String query = "Select count(scenario)>0 From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysisId and scenario.id = :scenarioId";
		return  (boolean) createQueryWithCache(query).setParameter("analysisId", analysisId).setParameter("scenarioId", scenarioId).getSingleResult();
	}

	/**
	 * getAllScenarios: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#getAllScenarios()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAll()  {
		return createQueryWithCache("From Scenario").getResultList();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAllFromAnalysis(Integer idAnalysis)  {
		String query = "Select scenario from Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysisId order by scenario.selected DESC, scenario.type asc, scenario.name asc";
		return createQueryWithCache(query).setParameter("analysisId", idAnalysis).getResultList();
	}

	/**
	 * getAllFromAnalysisIdAndSelected: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#getAllFromAnalysisIdAndSelected(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAllSelectedFromAnalysis(Integer idAnalysis)  {
		String query = "select scenario From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :idAnalysis and scenario.selected = true order by ";
		query += "scenario.type asc, scenario.name";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getAllFromAnalysisByScenarioTypeId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#getAllFromAnalysisByScenarioTypeId(lu.itrust.business.ts.model.analysis.Analysis,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAllFromAnalysisByType(Integer idAnalysis, ScenarioType scenarioType)  {
		String query = "Select scenario from Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysis and scenario.type = :scenariotype order by scenario.type ASC, scenario.name";
		return createQueryWithCache(query).setParameter("analysis", idAnalysis).setParameter("scenariotype", scenarioType).getResultList();
	}

	/**
	 * getAllSelectedFromAnalysisByType: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#getAllSelectedFromAnalysisByType(java.lang.Integer,
	 *      lu.itrust.business.ts.model.scenario.ScenarioType)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAllSelectedFromAnalysisByType(Integer idAnalysis, ScenarioType scenarioType)  {
		String query = "Select scenario from Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysis and scenario.type = :scenariotype and scenario.selected=true order by scenario.type ASC, scenario.name";
		return createQueryWithCache(query).setParameter("analysis", idAnalysis).setParameter("scenariotype", scenarioType).getResultList();
	}

	/**
	 * getAllScenariosFromAnalysisByScenarioIdList: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#getAllScenariosFromAnalysisByScenarioIdList(int,
	 *      java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAllFromAnalysisByIdList(Integer idAnalysis, List<Integer> scenarios)  {
		String query = "Select scenario From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :idAnalysis and scenario.id in :idScenarios order by ";
		query += "scenario.type asc, scenario.name asc";
		return createQueryWithCache(query).setParameter("idAnalysis", idAnalysis).setParameterList("idScenarios", scenarios).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#save(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@Override
	public void save(Scenario scenario)  {
		getSession().save(scenario);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#saveOrUpdate(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@Override
	public void saveOrUpdate(Scenario scenario)  {
		getSession().saveOrUpdate(scenario);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#merge(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@Override
	public Scenario merge(Scenario scenario)  {
		return (Scenario) getSession().merge(scenario);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#delete(lu.itrust.business.ts.model.scenario.Scenario)
	 */
	@Override
	public void delete(Scenario scenario)  {
		getSession().delete(scenario);
	}

	/**
	 * getAnalysisIdFromScenario: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#getAnalysisIdFromScenario(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Integer getAnalysisIdFromScenario(Integer scenarioId)  {
		return (Integer) createQueryWithCache("SELECT analysis.id From Analysis analysis join analysis.scenarios scenario where scenario.id = :scenarioId")
				.setParameter("scenarioId", scenarioId).uniqueResultOptional().orElse(0);
	}

	/**
	 * exist: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOScenario#exist(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	public boolean exist(Integer idAnalysis, String name)  {
		String query = "Select count(scenario)>0 From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysisId and scenario.name = :scenario";
		return (boolean) createQueryWithCache(query).setParameter("analysisId", idAnalysis).setParameter("scenario", name).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Scenario getByNameAndAnalysisId(String name, int analysisId) {
		return (Scenario) createQueryWithCache("Select scenario From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysisId and scenario.name = :scenario")
				.setParameter("analysisId", analysisId).setParameter("scenario", name).uniqueResultOptional().orElse(null);
	}

	@Override
	public boolean belongsToAnalysis(Integer analysisId, List<Integer> scenarioIds) {
		if(scenarioIds.isEmpty())
			return true;
		Long count = (Long) createQueryWithCache("Select count(scenario) From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysisid and scenario.id in (:scenarioIds)")
				.setParameter("analysisid", analysisId).setParameterList("scenarioIds", scenarioIds).getSingleResult();
		return count == scenarioIds.size();
	}
}