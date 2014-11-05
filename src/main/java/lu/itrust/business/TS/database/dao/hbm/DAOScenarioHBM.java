package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.data.scenario.Scenario;
import lu.itrust.business.TS.data.scenario.ScenarioType;
import lu.itrust.business.TS.database.dao.DAOScenario;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOScenarioHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 22 janv. 2013
 */
@Repository
public class DAOScenarioHBM extends DAOHibernate implements DAOScenario {

	/**
	 * Constructor: <br>
	 */
	public DAOScenarioHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOScenarioHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#get(int)
	 */
	@Override
	public Scenario get(Integer id) throws Exception {
		return (Scenario) getSession().get(Scenario.class, id);
	}

	/**
	 * getScenarioFromAnalysisByScenarioId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#getScenarioFromAnalysisByScenarioId(int, int)
	 */
	@Override
	public Scenario getFromAnalysisById(Integer idAnalysis, Integer scenarioId) throws Exception {
		String query = "Select scenario From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :idAnalysis and scenario.id = :idScenario";
		return (Scenario) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idScenario", scenarioId).uniqueResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer scenarioId) throws Exception {
		String query = "Select count(scenario) From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysisId and scenario.id = :scenarioId";
		return ((Long) getSession().createQuery(query).setParameter("analysisId", analysisId).setParameter("scenarioId", scenarioId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAllScenarios: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#getAllScenarios()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAll() throws Exception {
		return getSession().createQuery("From Scenario").list();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		String query =
			"Select scenario from Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysisId order by scenario.selected DESC, scenario.type.name asc, ";
		query += "scenario.name asc";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).list();
	}

	/**
	 * getAllFromAnalysisIdAndSelected: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#getAllFromAnalysisIdAndSelected(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAllSelectedFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "select scenario From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :idAnalysis and scenario.selected = true order by ";
		query += "scenario.scenarioType.name asc, scenario.name";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllFromAnalysisByScenarioTypeId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#getAllFromAnalysisByScenarioTypeId(lu.itrust.business.TS.data.analysis.Analysis,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAllFromAnalysisByType(Integer idAnalysis, ScenarioType scenarioType) throws Exception {
		String query =
			"Select scenario from Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysis and scenario.type = :scenariotype order by scenario.scenarioType.name ASC, scenario.name";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("scenariotype", scenarioType).list();
	}

	/**
	 * getAllScenariosFromAnalysisByScenarioIdList: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#getAllScenariosFromAnalysisByScenarioIdList(int,
	 *      java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> getAllFromAnalysisByIdList(Integer idAnalysis, List<Integer> scenarios) throws Exception {
		String query = "Select scenario From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :idAnalysis and scenario.id in :idScenarios order by ";
		query += "scenario.scenarioType.name asc, scenario.name asc";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameterList("idScenarios", scenarios).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#save(lu.itrust.business.TS.data.scenario.Scenario)
	 */
	@Override
	public void save(Scenario scenario) throws Exception {
		getSession().save(scenario);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#saveOrUpdate(lu.itrust.business.TS.data.scenario.Scenario)
	 */
	@Override
	public void saveOrUpdate(Scenario scenario) throws Exception {
		getSession().saveOrUpdate(scenario);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#merge(lu.itrust.business.TS.data.scenario.Scenario)
	 */
	@Override
	public Scenario merge(Scenario scenario) throws Exception {
		return (Scenario) getSession().merge(scenario);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#delete(lu.itrust.business.TS.data.scenario.Scenario)
	 */
	@Override
	public void delete(Scenario scenario) throws Exception {
		getSession().delete(scenario);
	}

	/**
	 * getAnalysisIdFromScenario: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#getAnalysisIdFromScenario(java.lang.Integer)
	 */
	@Override
	public Integer getAnalysisIdFromScenario(Integer scenarioId) throws Exception {
		return (Integer) getSession().createQuery("SELECT analysis.id From Analysis analysis join analysis.scenarios scenario where scenario.id = :scenarioId").setParameter("scenarioId", scenarioId)
				.uniqueResult();
	}

	/**
	 * exist: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOScenario#exist(java.lang.Integer, java.lang.String)
	 */
	@Override
	public boolean exist(Integer idAnalysis, String name) throws Exception {
		String query = "Select count(scenario) From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :analysisId and scenario.name = :scenario";
		return ((Long) getSession().createQuery(query).setParameter("analysisId", idAnalysis).setParameter("scenario", name).uniqueResult()).intValue() > 0;
	}
}