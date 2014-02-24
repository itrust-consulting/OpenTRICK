package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.dao.DAOScenario;

import org.hibernate.Query;
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
	 * 
	 */
	public DAOScenarioHBM() {
	}

	/**
	 * @param sessionFactory
	 */
	public DAOScenarioHBM(Session session) {
		super(session);
	}

	@Override
	public Scenario get(int id) throws Exception {
		return (Scenario) getSession().get(Scenario.class, id);
	}

	@Override
	public Scenario loadFromNameAnalysis(String scenarioName, Analysis analysis)
			throws Exception {
		Query query = getSession().createQuery(
				"From Scenario where name = :name and analysis = :analysis");
		return (Scenario) query.uniqueResult();
	}

	/**
	 * loadAllFromScenarioTypeID: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOScenario#loadAllFromScenarioTypeID(int,
	 *      lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> loadAllFromScenarioTypeID(int scenarioTypeID,
			Analysis analysis) throws Exception {
		return getSession()
				.createQuery(
						"Select scenario "
								+ "from Analysis as analysis inner join analysis.scenarios as scenario "
								+ "where analysis = :analysis and scenario.type.id = :scenariotypeId")
				.setParameter("analysis", analysis)
				.setParameter("scenariotypeId", scenarioTypeID).list();
	}

	/**
	 * loadAllFromAnalysisID: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOScenario#loadAllFromAnalysisID(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> loadAllFromAnalysisID(int idAnalysis)
			throws Exception {
		return getSession()
				.createQuery(
						"Select scenario "
								+ "from Analysis as analysis inner join analysis.scenarios as scenario "
								+ "where analysis.id = :analysisId order by scenario.scenarioType.name asc, scenario.name asc")
				.setParameter("analysisId", idAnalysis).list();
	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOScenario#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> loadAll() throws Exception {
		return getSession().createQuery("From Scenario").list();
	}

	@Override
	public void save(Scenario scenario) throws Exception {
		getSession().save(scenario);
	}

	@Override
	public void saveOrUpdate(Scenario scenario) throws Exception {
		getSession().saveOrUpdate(scenario);
	}

	@Override
	public void remove(Scenario scenario) throws Exception {
		getSession().delete(scenario);
	}

	@Override
	public Scenario merge(Scenario scenario) {
		return (Scenario) getSession().merge(scenario);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Scenario> findByAnalysisAndSelected(int idAnalysis) {
		return getSession()
				.createQuery(
						"select scenario From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :idAnalysis and scenario.selected = true order by scenario.name asc")
				.setInteger("idAnalysis", idAnalysis).list();
	}

	@Override
	public Scenario findByIdAndAnalysis(int id, int idAnalysis) {
		return (Scenario) getSession().createQuery("Select scenario From Analysis as analysis inner join analysis.scenarios as scenario where analysis.id = :idAnalysis and scenario.id = :idScenario").setInteger("idAnalysis", idAnalysis).setInteger("idScenario", id).uniqueResult();
	}

}
