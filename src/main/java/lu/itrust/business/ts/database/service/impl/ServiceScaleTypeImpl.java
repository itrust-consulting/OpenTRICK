/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOScaleType;
import lu.itrust.business.ts.database.service.ServiceScaleType;
import lu.itrust.business.ts.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
@Service
@Transactional(readOnly = true)
public class ServiceScaleTypeImpl implements ServiceScaleType {

	@Autowired
	private DAOScaleType daoScaleType;
	
	@Override
	public ScaleType findOne(int id) {
		return daoScaleType.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.service.ServiceScaleType#findOne(java.lang
	 * .String)
	 */
	@Override
	public ScaleType findOne(String name) {
		return daoScaleType.findOne(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.service.ServiceScaleType#findByAcronym(
	 * java.lang.String)
	 */
	@Override
	public ScaleType findByAcronym(String acronym) {
		return daoScaleType.findByAcronym(acronym);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScaleType#findAll()
	 */
	@Override
	public List<ScaleType> findAll() {
		return daoScaleType.findAll();
	}
	

	@Override
	public boolean exists(int id) {
		return daoScaleType.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.service.ServiceScaleType#exists(java.lang.
	 * String)
	 */
	@Override
	public boolean exists(String name) {
		return daoScaleType.exists(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.service.ServiceScaleType#hasAcronym(java.
	 * lang.String)
	 */
	@Override
	public boolean hasAcronym(String acronym) {
		return daoScaleType.hasAcronym(acronym);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.service.ServiceScaleType#save(lu.itrust.
	 * business.ts.model.scale.ScaleType)
	 */
	@Transactional
	@Override
	public int save(ScaleType scaleType) {
		return daoScaleType.save(scaleType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.service.ServiceScaleType#saveOrUpdate(lu.
	 * itrust.business.ts.model.scale.ScaleType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ScaleType scaleType) {
		daoScaleType.saveOrUpdate(scaleType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.service.ServiceScaleType#delete(lu.itrust.
	 * business.ts.model.scale.ScaleType)
	 */
	@Transactional
	@Override
	public void delete(ScaleType scaleType) {
		daoScaleType.delete(scaleType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.service.ServiceScaleType#delete(java.util.
	 * List)
	 */
	@Transactional
	@Override
	public void delete(List<Integer> scaleTypes) {
		daoScaleType.delete(scaleTypes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceScaleType#deleteAll()
	 */
	@Transactional
	@Override
	public void deleteAll() {
		daoScaleType.deleteAll();
	}

	@Override
	public List<ScaleType> findAllFree() {
		return daoScaleType.findAllFree();
	}

	@Override
	public List<ScaleType> findAllExpect(String... names) {
		return daoScaleType.findAllExpect(names);
	}

	@Override
	public ScaleType findOneByAnalysisId(Integer analysisId) {
		return daoScaleType.findOneByAnalysisId(analysisId);
	}

	@Override
	public List<ScaleType> findFromAnalysis(Integer idAnalysis) {
		return daoScaleType.findFromAnalysis(idAnalysis);
	}

	@Override
	public ScaleType findOneQualitativeByAnalysisId(Integer idAnalysis) {
		return daoScaleType.findOneQualitativeByAnalysisId(idAnalysis);
	}
}
