package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;

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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysisStandard#get(int)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysisStandard#getAll()
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysisStandard#getAllFromAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysisStandard#getAllFromStandard(lu.itrust.business.TS.model.standard.Standard)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysisStandard#save(lu.itrust.business.TS.model.standard.AnalysisStandard)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysisStandard#saveOrUpdate(lu.itrust.business.TS.model.standard.AnalysisStandard)
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
	 * @see lu.itrust.business.TS.database.service.ServiceAnalysisStandard#delete(lu.itrust.business.TS.model.standard.AnalysisStandard)
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

	@Override
	public AnalysisStandard getFromAnalysisIdAndStandardId(Integer analysisId, int standardId) {
		return daoAnalysisStandard.getFromAnalysisIdAndStandardId(analysisId,standardId );
	}

	@Override
	public boolean belongsToAnalysis(Integer idAnalysis, int id) {
		return daoAnalysisStandard.belongsToAnalysis(idAnalysis, id);
	}

	@Override
	public Standard getStandardById(int idAnalysisStandard) {
		return daoAnalysisStandard.getStandardById(idAnalysisStandard);
	}

	@Override
	public String getStandardNameById(int idAnalysisStandard) {
		return daoAnalysisStandard.getStandardNameById(idAnalysisStandard);
	}
}