package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.dao.DAOActionPlanType;
import lu.itrust.business.service.ServiceActionPlanType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceActionPlanTypeImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Feb 7, 2013
 */
@Service
public class ServiceActionPlanTypeImpl implements ServiceActionPlanType {

	@Autowired
	private DAOActionPlanType daoActionPlanType;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlanType#get(int)
	 */
	@Override
	public ActionPlanType get(Integer id) throws Exception {
		return daoActionPlanType.get(id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlanType#getByName(java.lang.String)
	 */
	@Override
	public ActionPlanType getByName(String name) throws Exception {
		return daoActionPlanType.getByName(name);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlanType#getAll()
	 */
	@Override
	public List<ActionPlanType> getAll() throws Exception {
		return daoActionPlanType.getAll();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param actionPlanType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlanType#save(lu.itrust.business.TS.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void save(ActionPlanType actionPlanType) throws Exception {
		daoActionPlanType.save(actionPlanType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param actionPlanType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlanType#saveOrUpdate(lu.itrust.business.TS.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ActionPlanType actionPlanType) throws Exception {
		daoActionPlanType.saveOrUpdate(actionPlanType);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param actionPlanType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlanType#merge(lu.itrust.business.TS.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void merge(ActionPlanType actionPlanType) throws Exception {
		this.daoActionPlanType.merge(actionPlanType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param actionPlanType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlanType#delete(lu.itrust.business.TS.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void delete(ActionPlanType actionPlanType) throws Exception {
		daoActionPlanType.delete(actionPlanType);
	}
}