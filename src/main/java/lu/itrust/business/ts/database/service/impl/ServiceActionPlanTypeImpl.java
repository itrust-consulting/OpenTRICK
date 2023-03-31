package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOActionPlanType;
import lu.itrust.business.ts.database.service.ServiceActionPlanType;
import lu.itrust.business.ts.model.actionplan.ActionPlanType;

/**
 * ServiceActionPlanTypeImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Feb 7, 2013
 */
@Transactional(readOnly = true)
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
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanType#get(int)
	 */
	@Override
	public ActionPlanType get(Integer id)  {
		return daoActionPlanType.get(id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanType#getByName(java.lang.String)
	 */
	@Override
	public ActionPlanType getByName(String name)  {
		return daoActionPlanType.getByName(name);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanType#getAll()
	 */
	@Override
	public List<ActionPlanType> getAll()  {
		return daoActionPlanType.getAll();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param actionPlanType
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanType#save(lu.itrust.business.ts.model.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void save(ActionPlanType actionPlanType)  {
		daoActionPlanType.save(actionPlanType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param actionPlanType
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanType#saveOrUpdate(lu.itrust.business.ts.model.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ActionPlanType actionPlanType)  {
		daoActionPlanType.saveOrUpdate(actionPlanType);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param actionPlanType
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanType#merge(lu.itrust.business.ts.model.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void merge(ActionPlanType actionPlanType)  {
		this.daoActionPlanType.merge(actionPlanType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param actionPlanType
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceActionPlanType#delete(lu.itrust.business.ts.model.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void delete(ActionPlanType actionPlanType)  {
		daoActionPlanType.delete(actionPlanType);
	}
}