package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAONorm;
import lu.itrust.business.service.ServiceNorm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceNormImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
@Transactional
public class ServiceNormImpl implements ServiceNorm {

	@Autowired
	private DAONorm daoNorm;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param idNorm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#get(int)
	 */
	@Override
	public Norm get(Integer idNorm) throws Exception {
		return daoNorm.get(idNorm);
	}

	/**
	 * getNormByName: <br>
	 * Description
	 * 
	 * @param norm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#getNormByName(java.lang.String)
	 */
	@Override
	public Norm getNormByName(String norm) throws Exception {
		return daoNorm.getNormByName(norm);
	}

	/**
	 * getNormNotCustomByName: <br>
	 * Description
	 * 
	 * @param norm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#getNormNotCustomByName(java.lang.String)
	 */
	@Override
	public Norm getNormNotCustomByName(String norm) throws Exception {
		return daoNorm.getNormNotCustomByName(norm);
	}

	/**
	 * getNormByNameAndVersion: <br>
	 * Description
	 * 
	 * @param label
	 * @param version
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#getNormByNameAndVersion(java.lang.String, int)
	 */
	@Override
	public Norm getNormByNameAndVersion(String label, int version) throws Exception {
		return daoNorm.getNormByNameAndVersion(label, version);
	}

	/**
	 * existsByNameAndVersion: <br>
	 * Description
	 * 
	 * @param label
	 * @param version
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#existsByNameAndVersion(java.lang.String, int)
	 */
	@Override
	public boolean existsByNameAndVersion(String label, int version) throws Exception {
		return daoNorm.existsByNameAndVersion(label, version);
	}

	/**
	 * getAllNorms: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#getAllNorms()
	 */
	@Override
	public List<Norm> getAll() throws Exception {
		return daoNorm.getAll();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#getAllFromAnalysisId(int)
	 */
	@Override
	public List<Norm> getAllFromAnalysis(Integer analysisId) throws Exception {
		return daoNorm.getAllFromAnalysis(analysisId);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Norm> getAllFromAnalysis(Analysis analysis) throws Exception {
		return daoNorm.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllNormsNotInAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#getAllNormsNotInAnalysis(int)
	 */
	@Override
	public List<Norm> getAllNotInAnalysis(Integer idAnalysis) throws Exception {
		return daoNorm.getAllNotInAnalysis(idAnalysis);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param Norm
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#save(lu.itrust.business.TS.Norm)
	 */
	@Transactional
	@Override
	public void save(Norm Norm) throws Exception {
		daoNorm.save(Norm);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param Norm
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#saveOrUpdate(lu.itrust.business.TS.Norm)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Norm Norm) throws Exception {
		daoNorm.saveOrUpdate(Norm);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param Norm
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#delete(lu.itrust.business.TS.Norm)
	 */
	@Transactional
	@Override
	public void delete(Norm Norm) throws Exception {
		daoNorm.delete(Norm);
	}
}