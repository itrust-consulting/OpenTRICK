/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOScaleEntry;
import lu.itrust.business.TS.database.service.ServiceScaleEntry;
import lu.itrust.business.TS.model.scale.ScaleEntry;

/**
 * @author eomar
 *
 */
@Service
public class ServiceScaleEntryImpl implements ServiceScaleEntry {

	@Autowired
	private DAOScaleEntry daoScaleEntry;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScaleEntry#findOne(int)
	 */
	@Override
	public ScaleEntry findOne(int id) {
		return daoScaleEntry.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScaleEntry#findAll()
	 */
	@Override
	public List<ScaleEntry> findAll() {
		return daoScaleEntry.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScaleEntry#exists(int)
	 */
	@Override
	public boolean exists(int id) {
		return daoScaleEntry.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScaleEntry#save(lu.itrust.
	 * business.TS.model.parameter.ScaleEntry)
	 */
	@Transactional
	@Override
	public int save(ScaleEntry scaleEntry) {
		return daoScaleEntry.save(scaleEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScaleEntry#saveOrUpdate(lu.
	 * itrust.business.TS.model.parameter.ScaleEntry)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ScaleEntry scaleEntry) {
		daoScaleEntry.saveOrUpdate(scaleEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScaleEntry#delete(lu.itrust
	 * .business.TS.model.parameter.ScaleEntry)
	 */
	@Transactional
	@Override
	public void delete(ScaleEntry scaleEntry) {
		daoScaleEntry.saveOrUpdate(scaleEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScaleEntry#delete(java.util
	 * .List)
	 */
	@Transactional
	@Override
	public void delete(List<Integer> scaleEntries) {
		daoScaleEntry.delete(scaleEntries);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScaleEntry#deleteAll()
	 */
	@Override
	public void deleteAll() {
		daoScaleEntry.deleteAll();
	}

}
