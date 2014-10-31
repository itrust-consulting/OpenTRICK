package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.OldScenarioType;
import lu.itrust.business.dao.DAOScenarioType;
import lu.itrust.business.service.ServiceScenarioType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceScenarioTypeImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
@Transactional
public class ServiceScenarioTypeImpl implements ServiceScenarioType {

	@Autowired
	private DAOScenarioType daoScenarioType;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceScenarioType#get(int)
	 */
	@Override
	public OldScenarioType get(Integer id) throws Exception {
		return daoScenarioType.get(id);
	}

	/**
	 * getByTypeName: <br>
	 * Description
	 * 
	 * @param scenarioTypeName
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceScenarioType#getByTypeName(java.lang.String)
	 */
	@Override
	public OldScenarioType getByName(String scenarioTypeName) throws Exception {
		return daoScenarioType.getByName(scenarioTypeName);
	}

	/**
	 * getAllScenarioTypes: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceScenarioType#getAllScenarioTypes()
	 */
	@Override
	public List<OldScenarioType> getAll() throws Exception {
		return daoScenarioType.getAll();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param scenarioType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceScenarioType#save(lu.itrust.business.TS.ScenarioType)
	 */
	@Transactional
	@Override
	public void save(OldScenarioType scenarioType) throws Exception {
		daoScenarioType.save(scenarioType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param scenarioType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceScenarioType#saveOrUpdate(lu.itrust.business.TS.ScenarioType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(OldScenarioType scenarioType) throws Exception {
		daoScenarioType.saveOrUpdate(scenarioType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param scenarioType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceScenarioType#delete(lu.itrust.business.TS.ScenarioType)
	 */
	@Transactional
	@Override
	public void delete(OldScenarioType scenarioType) throws Exception {
		daoScenarioType.delete(scenarioType);
	}
}