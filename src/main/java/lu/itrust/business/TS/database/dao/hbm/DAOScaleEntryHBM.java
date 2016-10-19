/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOScaleEntry;
import lu.itrust.business.TS.model.scale.ScaleEntry;

/**
 * @author eomar
 *
 */
@Repository
public class DAOScaleEntryHBM extends DAOHibernate implements DAOScaleEntry {

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScaleEntry#findOne(int)
	 */
	@Override
	public ScaleEntry findOne(int id) {
		return getSession().get(ScaleEntry.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScaleEntry#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ScaleEntry> findAll() {
		return getSession().createCriteria(ScaleEntry.class).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScaleEntry#exists(int)
	 */
	@Override
	public boolean exists(int id) {
		return (boolean) getSession().createQuery("Select count(*)>0 From ScaleEntry where id = :id").setInteger("id", id).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOScaleEntry#save(lu.itrust.business.
	 * TS.model.parameter.ScaleEntry)
	 */
	@Override
	public int save(ScaleEntry scaleEntry) {
		return (int) getSession().save(scaleEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOScaleEntry#saveOrUpdate(lu.itrust.
	 * business.TS.model.parameter.ScaleEntry)
	 */
	@Override
	public void saveOrUpdate(ScaleEntry scaleEntry) {
		getSession().saveOrUpdate(scaleEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScaleEntry#delete(lu.itrust.
	 * business.TS.model.parameter.ScaleEntry)
	 */
	@Override
	public void delete(ScaleEntry scaleEntry) {
		getSession().delete(scaleEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAOScaleEntry#delete(java.util.List)
	 */
	@Override
	public void delete(List<ScaleEntry> scaleEntries) {
		getSession().createQuery("delete From ScaleEntry where ScaleEntry in :entries").setParameterList("entries", scaleEntries).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOScaleEntry#deleteAll()
	 */
	@Override
	public void deleteAll() {
		getSession().createQuery("delete From ScaleEntry").executeUpdate();
	}

}
