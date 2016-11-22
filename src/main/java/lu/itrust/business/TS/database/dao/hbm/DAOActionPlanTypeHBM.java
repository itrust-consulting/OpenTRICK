package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOActionPlanType;
import lu.itrust.business.TS.model.actionplan.ActionPlanType;

/**
 * DAOActionPlanTypeHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 7 f�vr. 2013
 */
@Repository
public class DAOActionPlanTypeHBM extends DAOHibernate implements DAOActionPlanType {

	/**
	 * Constructor: <br>
	 */
	public DAOActionPlanTypeHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOActionPlanTypeHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#get(int)
	 */
	@Override
	public ActionPlanType get(Integer id)  {
		return (ActionPlanType) getSession().get(ActionPlanType.class, id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#getByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ActionPlanType getByName(String name)  {
		return (ActionPlanType) getSession().createQuery("From ActionPlanType where name = :name").setParameter("name", name.trim()).uniqueResultOptional().orElse(null);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanType> getAll()  {
		return (List<ActionPlanType>) getSession().createQuery("From ActionPlanType").getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#save(lu.itrust.business.TS.model.actionplan.ActionPlanType)
	 */
	@Override
	public void save(ActionPlanType actionPlanType)  {
		getSession().save(actionPlanType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#saveOrUpdate(lu.itrust.business.TS.model.actionplan.ActionPlanType)
	 */
	@Override
	public void saveOrUpdate(ActionPlanType actionPlanType)  {
		getSession().saveOrUpdate(actionPlanType);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#merge(lu.itrust.business.TS.model.actionplan.ActionPlanType)
	 */
	@Override
	public void merge(ActionPlanType actionPlanType)  {
		getSession().merge(actionPlanType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#delete(lu.itrust.business.TS.model.actionplan.ActionPlanType)
	 */
	@Override
	public void delete(ActionPlanType actionPlanType)  {
		getSession().delete(actionPlanType);
	}
}