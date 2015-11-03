package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Query;
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
	public ActionPlanType get(Integer id) throws Exception {
		return (ActionPlanType) getSession().get(ActionPlanType.class, id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#getByName(java.lang.String)
	 */
	@Override
	public ActionPlanType getByName(String name) throws Exception {
		Query query = getSession().createQuery("From ActionPlanType where name = :name").setParameter("name", name.trim());
		return (ActionPlanType) query.uniqueResult();
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ActionPlanType> getAll() throws Exception {
		return (List<ActionPlanType>) getSession().createQuery("From ActionPlanType").list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#save(lu.itrust.business.TS.model.actionplan.ActionPlanType)
	 */
	@Override
	public void save(ActionPlanType actionPlanType) throws Exception {
		getSession().save(actionPlanType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#saveOrUpdate(lu.itrust.business.TS.model.actionplan.ActionPlanType)
	 */
	@Override
	public void saveOrUpdate(ActionPlanType actionPlanType) throws Exception {
		getSession().saveOrUpdate(actionPlanType);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#merge(lu.itrust.business.TS.model.actionplan.ActionPlanType)
	 */
	@Override
	public void merge(ActionPlanType actionPlanType) throws Exception {
		getSession().merge(actionPlanType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOActionPlanType#delete(lu.itrust.business.TS.model.actionplan.ActionPlanType)
	 */
	@Override
	public void delete(ActionPlanType actionPlanType) throws Exception {
		getSession().delete(actionPlanType);
	}
}