package lu.itrust.business.TS.database.service.impl;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOPhase;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.model.general.Phase;

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
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#get(int)
	 */
	@Override
	public Phase get(Integer id)  {
		return daoPhase.get(id);
	}

	/**
	 * getPhaseFromAnalysisByPhaseNumber: <br>
	 * Description
	 * 
	 * @param number
	 * @param analysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getPhaseFromAnalysisByPhaseNumber(int, int)
	 */
	@Override
	public Phase getFromAnalysisByPhaseNumber(Integer analysis, Integer number)  {
		return daoPhase.getFromAnalysisByPhaseNumber(analysis, number);
	}

	/**
	 * getPhaseFromAnalysisIdByPhaseId: <br>
	 * Description
	 * 
	 * @param idPhase
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getPhaseFromAnalysisIdByPhaseId(int,
	 *      java.lang.Integer)
	 */
	@Override
	public Phase getFromAnalysisById(Integer idAnalysis, Integer idPhase)  {
		return daoPhase.getFromAnalysisById(idAnalysis, idPhase);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param phaseId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer phaseId)  {
		return daoPhase.belongsToAnalysis(analysisId, phaseId);
	}

	/**
	 * canBeDeleted: <br>
	 * Description
	 * 
	 * @param idPhase
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#canBeDeleted(int)
	 */
	@Override
	public boolean canBeDeleted(Integer idPhase)  {
		return daoPhase.canBeDeleted(idPhase);
	}

	/**
	 * getAllPhases: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getAllPhases()
	 */
	@Override
	public List<Phase> getAll()  {
		return daoPhase.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getAllFromAnalysis(int)
	 */
	@Override
	public List<Phase> getAllFromAnalysis(Integer analysis)  {
		return daoPhase.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllPhasesFromAnalysisByBeginDate: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param beginDate
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getAllPhasesFromAnalysisByBeginDate(int,
	 *      java.sql.Date)
	 */
	@Override
	public List<Phase> getAllFromAnalysisByBeginDate(Integer analysis, Date beginDate)  {
		return daoPhase.getAllFromAnalysisByBeginDate(analysis, beginDate);
	}

	/**
	 * getAllPhasesFromAnalysisByEndDate: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param endDate
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#getAllPhasesFromAnalysisByEndDate(int,
	 *      java.sql.Date)
	 */
	@Override
	public List<Phase> getAllFromAnalysisByEndDate(Integer analysis, Date endDate)  {
		return daoPhase.getAllFromAnalysisByEndDate(analysis, endDate);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param phase
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#save(lu.itrust.business.TS.model.general.Phase)
	 */
	@Transactional
	@Override
	public void save(Phase phase)  {
		daoPhase.save(phase);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param phase
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#saveOrUpdate(lu.itrust.business.TS.model.general.Phase)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Phase phase)  {
		daoPhase.saveOrUpdate(phase);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param phase
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServicePhase#delete(lu.itrust.business.TS.model.general.Phase)
	 */
	@Transactional
	@Override
	public void delete(Phase phase)  {
		daoPhase.delete(phase);
	}

	@Override
	public Phase findAllByIdAnalysis(Integer idAnalysis) {
		return daoPhase.findAllByIdAnalysis(idAnalysis);
	}
}