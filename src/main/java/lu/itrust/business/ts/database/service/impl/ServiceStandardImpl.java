package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOStandard;
import lu.itrust.business.ts.database.service.ServiceStandard;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;

/**
 * ServiceStandardImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
@Transactional(readOnly = true)
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
	 * 	@see lu.itrust.business.ts.database.service.ServiceStandard#get(int)
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
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#findByLabel(java.lang.String)
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
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#findByLabelAndAnalysisOnlyFalse(java.lang.String)
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
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#getStandardByLabelAndVersion(java.lang.String,
	 *      int)
	 */
	@Override
	public Standard getStandardByLabelAndVersion(String label, int version) {
		return daoStandard.getStandardByLabelAndVersion(label, version);
	}

	/**
	 * existsByNameAndVersion: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#existsByLabelAndVersion(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean existsByLabelAndVersion(String label, int version) {
		return daoStandard.existsByLabelAndVersion(label, version);
	}

	/**
	 * getAll: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#getAll()
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
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#getAllFromAnalysis(java.lang.Integer)
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
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#getAllFromAnalysis(lu.itrust.business.ts.model.analysis.Analysis)
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
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#getAllNotInAnalysis(java.lang.Integer)
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
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#save(lu.itrust.business.ts.model.standard.Standard)
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
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#saveOrUpdate(lu.itrust.business.ts.model.standard.Standard)
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
	 * @see lu.itrust.business.ts.database.service.ServiceStandard#delete(lu.itrust.business.ts.model.standard.Standard)
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
	public Integer getNextVersionByLabelAndType(String label, StandardType standardType) {
		return daoStandard.getNextVersionByLabelAndType(label, standardType);
	}

	@Override
	public boolean existsByLabelVersionType(String label, Integer version, StandardType type) {
		return daoStandard.existsByLabelVersionType(label, version, type);
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
	public boolean existsByLabel(String name) {
		return daoStandard.existsByLabel(name);
	}

	@Override
	public boolean isLabelConflicted(String newName, String oldName) {
		return daoStandard.isLabelConflicted(newName, oldName);
	}

	@Override
	public boolean isNameConflicted(String newName, String oldName) {
		return daoStandard.isNameConflicted(newName,oldName);
	}

}