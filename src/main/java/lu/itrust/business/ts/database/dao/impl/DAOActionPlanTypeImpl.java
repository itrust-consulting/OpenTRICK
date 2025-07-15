package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOActionPlanType;
import lu.itrust.business.ts.model.actionplan.ActionPlanType;

/**
 * DAOActionPlanTypeImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 7 f�vr. 2013
 */
@Repository
public class DAOActionPlanTypeImpl extends DAOHibernate implements DAOActionPlanType {

	/**
	 * Constructor: <br>
	 */
	public DAOActionPlanTypeImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOActionPlanTypeImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanType#get(int)
	 */
	@Override
	public ActionPlanType get(Integer id)  {
		return (ActionPlanType) getSession().get(ActionPlanType.class, id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanType#getByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ActionPlanType getByName(String name)  {
		return (ActionPlanType) createQueryWithCache("From ActionPlanType where name = :name").setParameter("name", name.trim()).uniqueResultOptional().orElse(null);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanType#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanType> getAll()  {
		return (List<ActionPlanType>) createQueryWithCache("From ActionPlanType").getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanType#save(lu.itrust.business.ts.model.actionplan.ActionPlanType)
	 */
	@Override
	public void save(ActionPlanType actionPlanType)  {
		getSession().save(actionPlanType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanType#saveOrUpdate(lu.itrust.business.ts.model.actionplan.ActionPlanType)
	 */
	@Override
	public void saveOrUpdate(ActionPlanType actionPlanType)  {
		getSession().saveOrUpdate(actionPlanType);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanType#merge(lu.itrust.business.ts.model.actionplan.ActionPlanType)
	 */
	@Override
	public void merge(ActionPlanType actionPlanType)  {
		getSession().merge(actionPlanType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOActionPlanType#delete(lu.itrust.business.ts.model.actionplan.ActionPlanType)
	 */
	@Override
	public void delete(ActionPlanType actionPlanType)  {
		getSession().delete(actionPlanType);
	}
}