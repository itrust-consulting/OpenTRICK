/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.dao.DAOScenarioType;
import lu.itrust.business.service.ServiceScenarioType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author oensuifudine
 * 
 */
@Service
@Transactional
public class ServiceScenarioTypeImpl implements ServiceScenarioType {

	@Autowired
	private DAOScenarioType daoScenarioType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceScenarioType#get(int)
	 */
	@Override
	public ScenarioType get(int id) throws Exception {
		// TODO Auto-generated method stub
		return daoScenarioType.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceScenarioType#get(java.lang.String)
	 */
	@Override
	public ScenarioType get(String scenarioTypeName) throws Exception {
		// TODO Auto-generated method stub
		return daoScenarioType.get(scenarioTypeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceScenarioType#loadAll()
	 */
	@Override
	public List<ScenarioType> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return daoScenarioType.loadAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceScenarioType#save(lu.itrust.business
	 * .TS.ScenarioType)
	 */
	@Transactional
	@Override
	public void save(ScenarioType scenarioType) throws Exception {
		daoScenarioType.save(scenarioType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceScenarioType#saveOrUpdate(lu.itrust
	 * .business.TS.ScenarioType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ScenarioType scenarioType) throws Exception {
		daoScenarioType.saveOrUpdate(scenarioType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceScenarioType#delete(lu.itrust.business
	 * .TS.ScenarioType)
	 */
	@Transactional
	@Override
	public void delete(ScenarioType scenarioType) throws Exception {
		daoScenarioType.delete(scenarioType);

	}

	public DAOScenarioType getDaoScenarioType() {
		return daoScenarioType;
	}

	public void setDaoScenarioType(DAOScenarioType daoScenarioType) {
		this.daoScenarioType = daoScenarioType;
	}

}
