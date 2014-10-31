package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisStandard;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.dao.DAOAnalysisStandard;
import lu.itrust.business.service.ServiceAnalysisStandard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceAnalysisStandardImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 24, 2013
 */
@Service
public class ServiceAnalysisStandardImpl implements ServiceAnalysisStandard {

	@Autowired
	private DAOAnalysisStandard daoAnalysisStandard;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisStandard#get(int)
	 */
	
	@Override
	public AnalysisStandard get(Integer id) throws Exception {
		return daoAnalysisStandard.get(id);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisStandard#getAll()
	 */
	
	@Override
	public List<AnalysisStandard> getAll() throws Exception {
		return daoAnalysisStandard.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	
	@Override
	public List<AnalysisStandard> getAllFromAnalysis(Integer analysisID) throws Exception {
		return this.daoAnalysisStandard.getAllFromAnalysis(analysisID);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	
	@Override
	public List<AnalysisStandard> getAllComputableFromAnalysis(Integer analysisID) throws Exception {
		return this.daoAnalysisStandard.getAllComputableFromAnalysis(analysisID);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisStandard#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	
	@Override
	public List<AnalysisStandard> getAllFromAnalysis(Analysis analysis) throws Exception {
		return daoAnalysisStandard.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllFromStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.service.ServiceAnalysisStandard#getAllFromStandard(lu.itrust.business.TS.Standard)
	 */
	
	@Override
	public List<AnalysisStandard> getAllFromStandard(Standard standard) throws Exception {
		return daoAnalysisStandard.getAllFromStandard(standard);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisStandard#save(lu.itrust.business.TS.AnalysisStandard)
	 */
	@Transactional
	@Override
	public void save(AnalysisStandard analysisStandard) throws Exception {
		daoAnalysisStandard.save(analysisStandard);

	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisStandard#saveOrUpdate(lu.itrust.business.TS.AnalysisStandard)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(AnalysisStandard analysisStandard) throws Exception {
		daoAnalysisStandard.saveOrUpdate(analysisStandard);

	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisStandard#delete(lu.itrust.business.TS.AnalysisStandard)
	 */
	@Transactional
	@Override
	public void delete(AnalysisStandard analysisStandard) throws Exception {
		daoAnalysisStandard.delete(analysisStandard);

	}

	@Transactional
	@Override
	public void deleteAllFromAnalysis(Integer analysisId) throws Exception {
		daoAnalysisStandard.deleteAllFromAnalysis(analysisId);
	}
}