package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.History;
import lu.itrust.business.dao.DAOHistory;
import lu.itrust.business.service.ServiceHistory;

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
	 * @see lu.itrust.business.service.ServiceHistory#get(java.lang.Integer)
	 */
	@Override
	public History get(Integer id) throws Exception {
		return daoHistory.get(id);
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
	 * @see lu.itrust.business.service.ServiceHistory#belongsToAnalysis(java.lang.Integer,
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
	 * @see lu.itrust.business.service.ServiceHistory#versionExistsByAnalysisIdAndAnalysisVersion(java.lang.Integer,
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
	 * @see lu.itrust.business.service.ServiceHistory#versionExistsForAnalysisByVersion(lu.itrust.business.TS.Analysis,
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
	 * @see lu.itrust.business.service.ServiceHistory#getVersionsFromAnalysisId(int)
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
	 * @see lu.itrust.business.service.ServiceHistory#getAllHistories()
	 */
	@Override
	public List<History> getAll() throws Exception {
		return daoHistory.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceHistory#getAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	public List<History> getAllFromAnalysis(Integer id) throws Exception {
		return daoHistory.getAllFromAnalysis(id);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceHistory#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
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
	 * @see lu.itrust.business.service.ServiceHistory#getAllHistoriesFromAnalysisByAuthor(lu.itrust.business.TS.Analysis,
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
	 * @see lu.itrust.business.service.ServiceHistory#getAllHistoriesFromAnalysisByVersion(lu.itrust.business.TS.Analysis,
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
	 * @see lu.itrust.business.service.ServiceHistory#save(java.lang.Integer,
	 *      lu.itrust.business.TS.History)
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
	 * @see lu.itrust.business.service.ServiceHistory#save(lu.itrust.business.TS.History)
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
	 * @see lu.itrust.business.service.ServiceHistory#saveOrUpdate(lu.itrust.business.TS.History)
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
	 * @see lu.itrust.business.service.ServiceHistory#delete(lu.itrust.business.TS.History)
	 */
	@Transactional
	@Override
	public void delete(History history) throws Exception {
		daoHistory.delete(history);
	}
}