package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.TS.StandardType;
import lu.itrust.business.dao.DAOStandard;
import lu.itrust.business.service.ServiceStandard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceStandardImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
@Transactional
public class ServiceStandardImpl implements ServiceStandard {

	@Autowired
	private DAOStandard daoStandard;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceStandard#get(int)
	 */
	@Override
	public Standard get(Integer idStandard) throws Exception {
		return daoStandard.get(idStandard);
	}

	/**
	 * getStandardByName: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#getStandardByName(java.lang.String)
	 */
	@Override
	public Standard getStandardByName(String standard) throws Exception {
		return daoStandard.getStandardByName(standard);
	}

	/**
	 * getStandardNotCustomByName: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#getStandardNotCustomByName(java.lang.String)
	 */
	@Override
	public Standard getStandardNotCustomByName(String standard) throws Exception {
		return daoStandard.getStandardNotCustomByName(standard);
	}

	/**
	 * getStandardByNameAndVersion: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#getStandardByNameAndVersion(java.lang.String, int)
	 */
	@Override
	public Standard getStandardByNameAndVersion(String label, int version) throws Exception {
		return daoStandard.getStandardByNameAndVersion(label, version);
	}

	/**
	 * existsByNameAndVersion: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#existsByNameAndVersion(java.lang.String, int)
	 */
	@Override
	public boolean existsByNameAndVersion(String label, int version) throws Exception {
		return daoStandard.existsByNameAndVersion(label, version);
	}

	/**
	 * getAll: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#getAll()
	 */
	@Override
	public List<Standard> getAll() throws Exception {
		return daoStandard.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	public List<Standard> getAllFromAnalysis(Integer analysisId) throws Exception {
		return daoStandard.getAllFromAnalysis(analysisId);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Standard> getAllFromAnalysis(Analysis analysis) throws Exception {
		return daoStandard.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllNotInAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#getAllNotInAnalysis(java.lang.Integer)
	 */
	@Override
	public List<Standard> getAllNotInAnalysis(Integer idAnalysis) throws Exception {
		return daoStandard.getAllNotInAnalysis(idAnalysis);
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#save(lu.itrust.business.TS.Standard)
	 */
	@Transactional
	@Override
	public void save(Standard standard) throws Exception {
		daoStandard.save(standard);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#saveOrUpdate(lu.itrust.business.TS.Standard)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Standard standard) throws Exception {
		daoStandard.saveOrUpdate(standard);
	}

	/**
	 * delete: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceStandard#delete(lu.itrust.business.TS.Standard)
	 */
	@Transactional
	@Override
	public void delete(Standard standard) throws Exception {
		daoStandard.delete(standard);
	}
	
	@Transactional
	@Override
	public List<Standard> getAllNotBoundToAnalysis() throws Exception {
		return daoStandard.getAllNotBoundToAnalysis();
	}

	@Transactional
	@Override
	public List<Standard> getAllAnalysisOnlyStandardsFromAnalysis(Integer analsisID) throws Exception {
		return daoStandard.getAllAnalysisOnlyStandardsFromAnalysis(analsisID);
	}

	@Transactional
	@Override
	public List<Standard> getAllFromAnalysisNotBound(Integer analysisId) throws Exception {
		return daoStandard.getAllFromAnalysisNotBound(analysisId);
	}

	@Override
	public Integer getBiggestVersionFromStandardByNameAndType(String label, StandardType standardType) throws Exception{
		return daoStandard.getBiggestVersionFromStandardByNameAndType(label, standardType);
	}
	
}