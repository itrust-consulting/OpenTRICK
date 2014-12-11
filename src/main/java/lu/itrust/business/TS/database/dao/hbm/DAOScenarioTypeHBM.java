package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.data.scenario.OldScenarioType;
import lu.itrust.business.TS.database.dao.DAOScenarioType;

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
	 * Constructor: <br>
	 */
	public DAOScenarioTypeHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOScenarioTypeHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenarioType#get(int)
	 */
	@Override
	public OldScenarioType get(Integer id) throws Exception {
		return (OldScenarioType) getSession().get(OldScenarioType.class, id);
	}

	/**
	 * getByTypeName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenarioType#getByTypeName(java.lang.String)
	 */
	@Override
	public OldScenarioType getByName(String scenarioTypeName) throws Exception {
		return (OldScenarioType) getSession().createQuery("From ScenarioType where name = :type").setString("type", scenarioTypeName).uniqueResult();
	}

	/**
	 * getAllScenarioTypes: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenarioType#getAllScenarioTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<OldScenarioType> getAll() throws Exception {
		return (List<OldScenarioType>) getSession().createQuery("From OldScenarioType").list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenarioType#save(lu.itrust.business.TS.data.basic.ScenarioType)
	 */
	@Override
	public void save(OldScenarioType scenarioType) throws Exception {
		getSession().save(scenarioType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenarioType#saveOrUpdate(lu.itrust.business.TS.data.basic.ScenarioType)
	 */
	@Override
	public void saveOrUpdate(OldScenarioType scenarioType) throws Exception {
		getSession().saveOrUpdate(scenarioType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScenarioType#delete(lu.itrust.business.TS.data.basic.ScenarioType)
	 */
	@Override
	public void delete(OldScenarioType scenarioType) throws Exception {
		getSession().delete(scenarioType);
	}
}