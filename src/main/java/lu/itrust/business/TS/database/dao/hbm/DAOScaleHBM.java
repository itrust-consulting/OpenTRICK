/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;

import lu.itrust.business.TS.database.dao.DAOScale;
import lu.itrust.business.TS.model.scale.Scale;

/**
 * @author eomar
 *
 */
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
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOScale#findByName(java.lang.String)
	 */
	@Override
	public Scale findByName(String name) {
		return (Scale) getSession().createQuery("From Scale where name = :name").setString("name", name).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScale#findByAcronym(java.lang.
	 * String)
	 */
	@Override
	public Scale findByAcronym(String acronym) {
		return (Scale) getSession().createQuery("From Scale where acronym = :acronym").setString("acronym", acronym).uniqueResult();
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
		return (boolean) getSession().createQuery("Select count(*) From Scale where id = :id").setInteger("id", id).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOScale#hasName(java.lang.String)
	 */
	@Override
	public boolean hasName(String name) {
		return (boolean) getSession().createQuery("Select count(*) From Scale where name = :name").setString("name", name).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOScale#hasAcronym(java.lang.String)
	 */
	@Override
	public boolean hasAcronym(String acronym) {
		return (boolean) getSession().createQuery("Select count(*) From Scale where acronym = :acronym").setString("acronym", acronym).uniqueResult();
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
	public void delete(List<Scale> scales) {
		getSession().createQuery("Delete From Scale where Scale in :scales").setParameterList("scales", scales).executeUpdate();
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
