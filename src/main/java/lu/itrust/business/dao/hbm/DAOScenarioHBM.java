package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.dao.DAOScenario;

import org.hibernate.Query;

/**
 * DAOScenarioHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since 22 janv. 2013
 */
public class DAOScenarioHBM extends DAOHibernate implements DAOScenario {

	@Override
	public Scenario get(int id) throws Exception {
		return (Scenario) getSession().get(Scenario.class, id);
	}

	@Override
	public Scenario loadFromNameAnalysis(String scenarioName, Analysis analysis) throws Exception {
		Query query = getSession().createQuery("From Scenario where name = :name and analysis = :analysis");
		return (Scenario) query.uniqueResult();
	}

	/**
	 * loadAllFromScenarioType: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOScenario#loadAllFromScenarioType(lu.itrust.business.TS.ScenarioType, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Scenario> loadAllFromScenarioType(ScenarioType scenarioType, Analysis analysis)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllFromScenarioTypeID: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOScenario#loadAllFromScenarioTypeID(int, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Scenario> loadAllFromScenarioTypeID(int scenarioTypeID, Analysis analysis)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllFromAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOScenario#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Scenario> loadAllFromAnalysis(Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllFromAnalysisID: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOScenario#loadAllFromAnalysisID(int)
	 */
	@Override
	public List<Scenario> loadAllFromAnalysisID(int idAnalysis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllFromAnalysisIdentifierAndVersion: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOScenario#loadAllFromAnalysisIdentifierAndVersion(int, int, int)
	 */
	@Override
	public List<Scenario> loadAllFromAnalysisIdentifierAndVersion(int idAnalysis, int identifier,
			int version) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAll: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOScenario#loadAll()
	 */
	@Override
	public List<Scenario> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
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

}
