package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.History;
import lu.itrust.business.dao.DAOHistory;
import lu.itrust.business.service.ServiceHistory;

/** 
 * ServiceHistoryImpl.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since Oct 22, 2013
 */
public class ServiceHistoryImpl implements ServiceHistory {

	@Autowired
	private DAOHistory daoHistory;
	
	/**
	 * setDAOHistory: <br>
	 * Description
	 * 
	 * @param daoHistory
	 */
	public void setDAOHistory(DAOHistory daoHistory) {
		this.daoHistory = daoHistory;
	}
	
	/**
	 * daoHistory: <br>
	 * Description
	 * 
	 * @return
	 */
	@Override
	public DAOHistory getDAOHistory() {
		return daoHistory;
	}
	
	/**
	 * get: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceHistory#get(java.lang.Integer)
	 */
	@Override
	public History get(Integer id) throws Exception {
		return daoHistory.get(id);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceHistory#getAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	public List<History> getAllFromAnalysis(Integer id) throws Exception {
		return daoHistory.loadAllFromAnalysis(id);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceHistory#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<History> getAllFromAnalysis(Analysis analysis) throws Exception {
		return daoHistory.loadAllFromAnalysis(analysis);
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceHistory#save(lu.itrust.business.TS.History)
	 */
	@Override
	public void save(History history) throws Exception {
		daoHistory.save(history);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceHistory#saveOrUpdate(lu.itrust.business.TS.History)
	 */
	@Override
	public void saveOrUpdate(History history) throws Exception {
		daoHistory.saveOrUpdate(history);
	}

	/**
	 * delete: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceHistory#delete(lu.itrust.business.TS.History)
	 */
	@Override
	public void delete(History history) throws Exception {
		daoHistory.remove(history);
	}

	/**
	 * versionExists: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceHistory#versionExists(lu.itrust.business.TS.Analysis, java.lang.String)
	 */
	@Override
	public boolean versionExists(Analysis analysis, String version) throws Exception {
		return daoHistory.versionExists(analysis, version);
	}

	/**
	 * versionExists: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceHistory#versionExists(java.lang.Integer, java.lang.String)
	 */
	@Override
	public boolean versionExists(Integer analysisId, String version) throws Exception {
		return daoHistory.versionExists(analysisId, version);
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceHistory#save(java.lang.Integer, lu.itrust.business.TS.History)
	 */
	@Override
	public void save(Integer analysisId, History history) throws Exception {
		daoHistory.save(analysisId, history);
	}
}
