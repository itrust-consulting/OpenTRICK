package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.dao.DAOActionPlanType;

import org.hibernate.Query;

/**
 * DAOActionPlanTypeHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 7 f�vr. 2013
 */
public class DAOActionPlanTypeHBM extends DAOHibernate implements
		DAOActionPlanType {

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanType#get(int)
	 */
	@Override
	public ActionPlanType get(int id) throws Exception {

		return (ActionPlanType) getSession().get(ActionPlanType.class, id);
	}

	/**
	 * 
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanType#get(java.lang.String)
	 */
	@Override
	public ActionPlanType get(String name) throws Exception {
		Query query = getSession().createQuery(
				"From ActionPlanType where name = :name");
		query.setString("name", name);
		return (ActionPlanType) query.uniqueResult();

	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanType#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanType> loadAll() throws Exception {
		return (List<ActionPlanType>) getSession().createQuery(
				"From ActionPlanType").list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanType#save(lu.itrust.business.TS.actionplan.ActionPlanType)
	 */
	@Override
	public void save(ActionPlanType actionPlanType) throws Exception {
		getSession().save(actionPlanType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanType#saveOrUpdate(lu.itrust.business.TS.actionplan.ActionPlanType)
	 */
	@Override
	public void saveOrUpdate(ActionPlanType actionPlanType) throws Exception {
		getSession().saveOrUpdate(actionPlanType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOActionPlanType#delete(lu.itrust.business.TS.actionplan.ActionPlanType)
	 */
	@Override
	public void delete(ActionPlanType actionPlanType) throws Exception {
		getSession().delete(actionPlanType);
	}

	@Override
	public void merge(ActionPlanType actionPlanType) throws Exception {
		getSession().merge(actionPlanType);
	}

}
