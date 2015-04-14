package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import lu.itrust.business.TS.database.dao.DAOHistory;
import lu.itrust.business.TS.database.service.ServiceHistory;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.history.History;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceHistoryImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 22, 2013
 */
@Service
public class ServiceHistoryImpl implements ServiceHistory {

	@Autowired
	private DAOHistory daoHistory;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#get(java.lang.Integer)
	 */
	@Override
	public History get(Integer id) throws Exception {
		return daoHistory.get(id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idHistory
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public History getFromAnalysisById(Integer idAnalysis, Integer idHistory) throws Exception {
		return daoHistory.getFromAnalysisById(idAnalysis, idHistory);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param historyId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer historyId, Integer analysisId) throws Exception {
		return daoHistory.belongsToAnalysis(historyId, analysisId);
	}

	/**
	 * versionExistsByAnalysisIdAndAnalysisVersion: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param version
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#versionExistsByAnalysisIdAndAnalysisVersion(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	public boolean versionExistsInAnalysis(Integer analysisId, String version) throws Exception {
		return daoHistory.versionExistsInAnalysis(analysisId, version);
	}

	/**
	 * versionExistsForAnalysisByVersion: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param version
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#versionExistsForAnalysisByVersion(lu.itrust.business.TS.model.analysis.Analysis,
	 *      java.lang.String)
	 */
	@Override
	public boolean versionExistsInAnalysis(Analysis analysis, String version) throws Exception {
		return daoHistory.versionExistsInAnalysis(analysis, version);
	}

	/**
	 * getVersionsFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#getVersionsFromAnalysisId(int)
	 */
	@Override
	public List<String> getVersionsFromAnalysis(Integer analysisId) throws Exception {
		return daoHistory.getVersionsFromAnalysis(analysisId);
	}

	/**
	 * getAllHistories: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#getAllHistories()
	 */
	@Override
	public List<History> getAll() throws Exception {
		return daoHistory.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#getAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	public List<History> getAllFromAnalysis(Integer id) throws Exception {
		return daoHistory.getAllFromAnalysis(id);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#getAllFromAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public List<History> getAllFromAnalysis(Analysis analysis) throws Exception {
		return daoHistory.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllHistoriesFromAnalysisByAuthor: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param author
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#getAllHistoriesFromAnalysisByAuthor(lu.itrust.business.TS.model.analysis.Analysis,
	 *      java.lang.String)
	 */
	@Override
	public List<History> getAllFromAnalysisByAuthor(Analysis analysis, String author) throws Exception {
		return daoHistory.getAllFromAnalysisByAuthor(analysis, author);
	}

	/**
	 * getAllHistoriesFromAnalysisByVersion: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param version
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#getAllHistoriesFromAnalysisByVersion(lu.itrust.business.TS.model.analysis.Analysis,
	 *      java.lang.String)
	 */
	@Override
	public List<History> getAllFromAnalysisByVersion(Analysis analysis, String version) throws Exception {
		return daoHistory.getAllFromAnalysisByVersion(analysis, version);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param history
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#save(java.lang.Integer,
	 *      lu.itrust.business.TS.model.history.History)
	 */
	@Transactional
	@Override
	public void save(Integer analysisId, History history) throws Exception {
		daoHistory.save(analysisId, history);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param history
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#save(lu.itrust.business.TS.model.history.History)
	 */
	@Transactional
	@Override
	public void save(History history) throws Exception {
		daoHistory.save(history);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param history
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#saveOrUpdate(lu.itrust.business.TS.model.history.History)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(History history) throws Exception {
		daoHistory.saveOrUpdate(history);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param history
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceHistory#delete(lu.itrust.business.TS.model.history.History)
	 */
	@Transactional
	@Override
	public void delete(History history) throws Exception {
		daoHistory.delete(history);
	}
}