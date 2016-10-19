/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOScale;
import lu.itrust.business.TS.database.service.ServiceScale;
import lu.itrust.business.TS.model.scale.Scale;

/**
 * @author eomar
 *
 */
@Service
public class ServiceScaleImpl implements ServiceScale {

	@Autowired
	private DAOScale daoScale;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScale#findOne(int)
	 */
	@Override
	public Scale findOne(int id) {
		return daoScale.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScale#findByName(java.lang.
	 * String)
	 */
	@Override
	public Scale findByName(String name) {
		return daoScale.findByName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScale#findByAcronym(java.
	 * lang.String)
	 */
	@Override
	public Scale findByAcronym(String acronym) {
		return daoScale.findByAcronym(acronym);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScale#findAll()
	 */
	@Override
	public List<Scale> findAll() {
		return daoScale.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScale#exists(int)
	 */
	@Override
	public boolean exists(int id) {
		return daoScale.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScale#hasName(java.lang.
	 * String)
	 */
	@Override
	public boolean hasName(String name) {
		return daoScale.hasName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScale#hasAcronym(java.lang.
	 * String)
	 */
	@Override
	public boolean hasAcronym(String acronym) {
		return daoScale.hasAcronym(acronym);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScale#save(lu.itrust.
	 * business.TS.model.parameter.Scale)
	 */
	@Transactional
	@Override
	public int save(Scale scale) {
		return daoScale.save(scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScale#saveOrUpdate(lu.
	 * itrust.business.TS.model.parameter.Scale)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Scale scale) {
		daoScale.saveOrUpdate(scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScale#delete(lu.itrust.
	 * business.TS.model.parameter.Scale)
	 */
	@Transactional
	@Override
	public void delete(Scale scale) {
		daoScale.delete(scale);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.service.ServiceScale#delete(java.util.
	 * List)
	 */
	@Transactional
	@Override
	public void delete(List<Scale> scales) {
		daoScale.delete(scales);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceScale#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoScale.deleteAll();
	}

}
