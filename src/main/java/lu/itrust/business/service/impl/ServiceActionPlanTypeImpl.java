/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.dao.DAOActionPlanType;
import lu.itrust.business.service.ServiceActionPlanType;

/**
 * @author oensuifudine
 * 
 */
@Service
public class ServiceActionPlanTypeImpl implements ServiceActionPlanType {

	@Autowired
	private DAOActionPlanType daoActionPlanType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlanType#get(int)
	 */
	@Override
	public ActionPlanType get(int id) throws Exception {
		return daoActionPlanType.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceActionPlanType#get(java.lang.String)
	 */
	@Override
	public ActionPlanType get(String name) throws Exception {
		return daoActionPlanType.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceActionPlanType#loadAll()
	 */
	@Override
	public List<ActionPlanType> loadAll() throws Exception {
		return daoActionPlanType.loadAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceActionPlanType#save(lu.itrust.business
	 * .TS.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void save(ActionPlanType actionPlanType) throws Exception {
		daoActionPlanType.save(actionPlanType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceActionPlanType#saveOrUpdate(lu.itrust
	 * .business.TS.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ActionPlanType actionPlanType) throws Exception {
		daoActionPlanType.saveOrUpdate(actionPlanType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceActionPlanType#delete(lu.itrust.business
	 * .TS.actionplan.ActionPlanType)
	 */
	@Transactional
	@Override
	public void delete(ActionPlanType actionPlanType) throws Exception {
		daoActionPlanType.delete(actionPlanType);

	}

	/**
	 * @return the daoActionPlanType
	 */
	public DAOActionPlanType getDaoActionPlanType() {
		return daoActionPlanType;
	}

	/**
	 * @param daoActionPlanType
	 *            the daoActionPlanType to set
	 */
	public void setDaoActionPlanType(DAOActionPlanType daoActionPlanType) {
		this.daoActionPlanType = daoActionPlanType;
	}

	@Transactional
	@Override
	public void merge(ActionPlanType actionPlanType) throws Exception {
		this.daoActionPlanType.merge(actionPlanType);
	}

}
