package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.dao.DAOScenarioType;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOScenarioTypeHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 31 janv. 2013
 */
@Repository
public class DAOScenarioTypeHBM extends DAOHibernate implements DAOScenarioType {

	/**
	 * 
	 */
	public DAOScenarioTypeHBM() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sessionFactory
	 */
	public DAOScenarioTypeHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOScenarioType#get(int)
	 */
	@Override
	public ScenarioType get(int id) throws Exception {

		return (ScenarioType) getSession().get(ScenarioType.class, id);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOScenarioType#get(java.lang.String)
	 */
	@Override
	public ScenarioType get(String scenarioTypeName) throws Exception {
		return (ScenarioType) getSession().createQuery("From ScenarioType where name = :type").setString("type", scenarioTypeName).uniqueResult();

	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOScenarioType#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ScenarioType> loadAll() throws Exception {

		return (List<ScenarioType>) getSession().createQuery("From ScenarioType").list();

	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOScenarioType#save(lu.itrust.business.TS.ScenarioType)
	 */
	@Override
	public void save(ScenarioType scenarioType) throws Exception {
		getSession().save(scenarioType);

	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOScenarioType#saveOrUpdate(lu.itrust.business.TS.ScenarioType)
	 */
	@Override
	public void saveOrUpdate(ScenarioType scenarioType) throws Exception {
		getSession().saveOrUpdate(scenarioType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOScenarioType#delete(lu.itrust.business.TS.ScenarioType)
	 */
	@Override
	public void delete(ScenarioType scenarioType) throws Exception {
		getSession().delete(scenarioType);
	}

}
