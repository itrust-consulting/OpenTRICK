package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOStandard;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;

/**
 * ServiceStandardImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
public class ServiceStandardImpl implements ServiceStandard {

	@Autowired
	private DAOStandard daoStandard;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @return
	 * @
	 * 
	 * 	@see lu.itrust.business.TS.database.service.ServiceStandard#get(int)
	 */
	@Override
	public Standard get(Integer idStandard) {
		return daoStandard.get(idStandard);
	}

	/**
	 * getStandardByName: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#findByLabel(java.lang.String)
	 */
	@Override
	public List<Standard> findByLabel(String standard) {
		return daoStandard.findByLabel(standard);
	}

	/**
	 * getStandardNotCustomByName: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#findByLabelAndAnalysisOnlyFalse(java.lang.String)
	 */
	@Override
	public List<Standard> findByLabelAndAnalysisOnlyFalse(String standard) {
		return daoStandard.findByLabelAndAnalysisOnlyFalse(standard);
	}

	/**
	 * getStandardByNameAndVersion: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#getStandardByNameAndVersion(java.lang.String,
	 *      int)
	 */
	@Override
	public Standard getStandardByNameAndVersion(String label, int version) {
		return daoStandard.getStandardByNameAndVersion(label, version);
	}

	/**
	 * existsByNameAndVersion: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#existsByNameAndVersion(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean existsByNameAndVersion(String label, int version) {
		return daoStandard.existsByNameAndVersion(label, version);
	}

	/**
	 * getAll: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#getAll()
	 */
	@Override
	public List<Standard> getAll() {
		return daoStandard.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	public List<Standard> getAllFromAnalysis(Integer analysisId) {
		return daoStandard.getAllFromAnalysis(analysisId);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#getAllFromAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public List<Standard> getAllFromAnalysis(Analysis analysis) {
		return daoStandard.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllNotInAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#getAllNotInAnalysis(java.lang.Integer)
	 */
	@Override
	public List<Standard> getAllNotInAnalysis(Integer idAnalysis) {
		return daoStandard.getAllNotInAnalysis(idAnalysis);
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#save(lu.itrust.business.TS.model.standard.Standard)
	 */
	@Transactional
	@Override
	public void save(Standard standard) {
		daoStandard.save(standard);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#saveOrUpdate(lu.itrust.business.TS.model.standard.Standard)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Standard standard) {
		daoStandard.saveOrUpdate(standard);
	}

	/**
	 * delete: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceStandard#delete(lu.itrust.business.TS.model.standard.Standard)
	 */
	@Transactional
	@Override
	public void delete(Standard standard) {
		daoStandard.delete(standard);
	}

	@Override
	public List<Standard> getAllNotBoundToAnalysis() {
		return daoStandard.getAllNotBoundToAnalysis();
	}

	@Override
	public List<Standard> getAllAnalysisOnlyStandardsFromAnalysis(Integer analsisID) {
		return daoStandard.getAllAnalysisOnlyStandardsFromAnalysis(analsisID);
	}

	@Override
	public List<Standard> getAllFromAnalysisNotBound(Integer analysisId) {
		return daoStandard.getAllFromAnalysisNotBound(analysisId);
	}

	@Override
	public Integer getNextVersionByNameAndType(String label, StandardType standardType) {
		return daoStandard.getNextVersionByNameAndType(label, standardType);
	}

	@Override
	public boolean existsByNameVersionType(String label, Integer version, StandardType type) {
		return daoStandard.existsByNameVersionType(label, version, type);
	}

	@Override
	public boolean belongsToAnalysis(int idAnalysis, Integer idStandard) {
		return daoStandard.belongsToAnalysis(idAnalysis, idStandard);
	}

	@Override
	public boolean isUsed(Standard standard) {
		return daoStandard.isUsed(standard);
	}

	@Override
	public List<Standard> getAllNotInAnalysisAndNotMaturity(Integer idAnalysis) {
		return daoStandard.getAllNotInAnalysisAndNotMaturity(idAnalysis);
	}

	@Override
	public boolean existsByName(String name) {
		return daoStandard.existsByName(name);
	}

	@Override
	public boolean isConflicted(String newName, String oldName) {
		return daoStandard.isConflicted(newName, oldName);
	}

}