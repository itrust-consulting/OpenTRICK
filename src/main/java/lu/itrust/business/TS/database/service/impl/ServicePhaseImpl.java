package lu.itrust.business.TS.database.service.impl;

import java.sql.Date;
import java.util.List;

import lu.itrust.business.TS.data.basic.Phase;
import lu.itrust.business.TS.database.dao.DAOPhase;
import lu.itrust.business.TS.database.service.ServicePhase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServicePhaseImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
public class ServicePhaseImpl implements ServicePhase {

	@Autowired
	private DAOPhase daoPhase;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#get(int)
	 */
	@Override
	public Phase get(Integer id) throws Exception {
		return daoPhase.get(id);
	}

	/**
	 * getPhaseFromAnalysisByPhaseNumber: <br>
	 * Description
	 * 
	 * @param number
	 * @param analysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getPhaseFromAnalysisByPhaseNumber(int, int)
	 */
	@Override
	public Phase getFromAnalysisByPhaseNumber(Integer analysis, Integer number) throws Exception {
		return daoPhase.getFromAnalysisByPhaseNumber(analysis, number);
	}

	/**
	 * getPhaseFromAnalysisIdByPhaseId: <br>
	 * Description
	 * 
	 * @param idPhase
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getPhaseFromAnalysisIdByPhaseId(int,
	 *      java.lang.Integer)
	 */
	@Override
	public Phase getFromAnalysisById(Integer idAnalysis, Integer idPhase) throws Exception {
		return daoPhase.getFromAnalysisById(idAnalysis, idPhase);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param phaseId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer phaseId) throws Exception {
		return daoPhase.belongsToAnalysis(analysisId, phaseId);
	}

	/**
	 * canBeDeleted: <br>
	 * Description
	 * 
	 * @param idPhase
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#canBeDeleted(int)
	 */
	@Override
	public boolean canBeDeleted(Integer idPhase) throws Exception {
		return daoPhase.canBeDeleted(idPhase);
	}

	/**
	 * getAllPhases: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getAllPhases()
	 */
	@Override
	public List<Phase> getAll() throws Exception {
		return daoPhase.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getAllFromAnalysis(int)
	 */
	@Override
	public List<Phase> getAllFromAnalysis(Integer analysis) throws Exception {
		return daoPhase.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllPhasesFromAnalysisByBeginDate: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param beginDate
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getAllPhasesFromAnalysisByBeginDate(int,
	 *      java.sql.Date)
	 */
	@Override
	public List<Phase> getAllFromAnalysisByBeginDate(Integer analysis, Date beginDate) throws Exception {
		return daoPhase.getAllFromAnalysisByBeginDate(analysis, beginDate);
	}

	/**
	 * getAllPhasesFromAnalysisByEndDate: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getAllPhasesFromAnalysisByEndDate(int,
	 *      java.sql.Date)
	 */
	@Override
	public List<Phase> getAllFromAnalysisByEndDate(Integer analysis, Date endDate) throws Exception {
		return daoPhase.getAllFromAnalysisByEndDate(analysis, endDate);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param phase
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#save(lu.itrust.business.TS.data.basic.Phase)
	 */
	@Transactional
	@Override
	public void save(Phase phase) throws Exception {
		daoPhase.save(phase);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param phase
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#saveOrUpdate(lu.itrust.business.TS.data.basic.Phase)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Phase phase) throws Exception {
		daoPhase.saveOrUpdate(phase);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param phase
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#delete(lu.itrust.business.TS.data.basic.Phase)
	 */
	@Transactional
	@Override
	public void delete(Phase phase) throws Exception {
		daoPhase.delete(phase);
	}
}