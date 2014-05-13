package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAOAnalysisNorm;
import lu.itrust.business.service.ServiceAnalysisNorm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceAnalysisNormImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 24, 2013
 */
@Service
public class ServiceAnalysisNormImpl implements ServiceAnalysisNorm {

	@Autowired
	private DAOAnalysisNorm daoAnalysisNorm;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#get(int)
	 */
	@Override
	public AnalysisNorm get(Integer id) throws Exception {
		return daoAnalysisNorm.get(id);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#getAll()
	 */
	@Override
	public List<AnalysisNorm> getAll() throws Exception {
		return daoAnalysisNorm.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#getAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	public List<AnalysisNorm> getAllFromAnalysis(Integer analysisID) throws Exception {
		return this.daoAnalysisNorm.getAllFromAnalysis(analysisID);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<AnalysisNorm> getAllFromAnalysis(Analysis analysis) throws Exception {
		return daoAnalysisNorm.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllAnalysisNormFromNorm: <br>
	 * Description
	 * 
	 * @param norm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#getAllAnalysisNormFromNorm(lu.itrust.business.TS.Norm)
	 */
	@Override
	public List<AnalysisNorm> getAllFromNorm(Norm norm) throws Exception {
		return daoAnalysisNorm.getAllFromNorm(norm);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param analysisNorm
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#save(lu.itrust.business.TS.AnalysisNorm)
	 */
	@Transactional
	@Override
	public void save(AnalysisNorm analysisNorm) throws Exception {
		daoAnalysisNorm.save(analysisNorm);

	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param analysisNorm
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#saveOrUpdate(lu.itrust.business.TS.AnalysisNorm)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(AnalysisNorm analysisNorm) throws Exception {
		daoAnalysisNorm.saveOrUpdate(analysisNorm);

	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param analysisNorm
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#delete(lu.itrust.business.TS.AnalysisNorm)
	 */
	@Transactional
	@Override
	public void delete(AnalysisNorm analysisNorm) throws Exception {
		daoAnalysisNorm.delete(analysisNorm);

	}
}