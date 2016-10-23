/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOScale;
import lu.itrust.business.TS.model.scale.Scale;

/**
 * @author eomar
 *
 */
@Repository
public class DAOScaleHBM extends DAOHibernate implements DAOScale {

	/**
	 * 
	 */
	public DAOScaleHBM() {
	}

	/**
	 * @param session
	 */
	public DAOScaleHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScale#findOne(int)
	 */
	@Override
	public Scale findOne(int id) {
		return getSession().get(Scale.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScale#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Scale> findAll() {
		return getSession().createCriteria(Scale.class).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScale#exists(int)
	 */
	@Override
	public boolean exists(int id) {
		return (boolean) getSession().createQuery("Select count(*)>0 From Scale where id = :id").setInteger("id", id).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOScale#save(lu.itrust.business.TS.
	 * model.parameter.Scale)
	 */
	@Override
	public int save(Scale scale) {
		return (int) getSession().save(scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScale#saveOrUpdate(lu.itrust.
	 * business.TS.model.parameter.Scale)
	 */
	@Override
	public void saveOrUpdate(Scale scale) {
		getSession().saveOrUpdate(scale);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOScale#delete(lu.itrust.business.TS.
	 * model.parameter.Scale)
	 */
	@Override
	public void delete(Scale scale) {
		getSession().delete(scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScale#delete(java.util.List)
	 */
	@Override
	public void delete(List<Integer> scales) {
		getSession().createQuery("Delete From Scale where id in :scales").setParameterList("scales", scales).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScale#deleteAll()
	 */
	@Override
	public void deleteAll() {
		getSession().createQuery("Delete From Scale").executeUpdate();
	}
}
