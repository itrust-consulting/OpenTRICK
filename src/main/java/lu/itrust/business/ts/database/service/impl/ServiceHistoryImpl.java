package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOHistory;
import lu.itrust.business.ts.database.service.ServiceHistory;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.history.History;

/**
 * ServiceHistoryImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 22, 2013
 */
@Transactional(readOnly = true)
@Service
public class ServiceHistoryImpl implements ServiceHistory {

	@Autowired
	private DAOHistory daoHistory;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#get(java.lang.Integer)
	 */
	@Override
	public History get(Integer id)  {
		return daoHistory.get(id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idHistory
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public History getFromAnalysisById(Integer idAnalysis, Integer idHistory)  {
		return daoHistory.getFromAnalysisById(idAnalysis, idHistory);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param historyId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer historyId, Integer analysisId)  {
		return daoHistory.belongsToAnalysis(historyId, analysisId);
	}

	/**
	 * versionExistsByAnalysisIdAndAnalysisVersion: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param version
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#versionExistsByAnalysisIdAndAnalysisVersion(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	public boolean versionExistsInAnalysis(Integer analysisId, String version)  {
		return daoHistory.versionExistsInAnalysis(analysisId, version);
	}

	/**
	 * versionExistsForAnalysisByVersion: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param version
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#versionExistsForAnalysisByVersion(lu.itrust.business.ts.model.analysis.Analysis,
	 *      java.lang.String)
	 */
	@Override
	public boolean versionExistsInAnalysis(Analysis analysis, String version)  {
		return daoHistory.versionExistsInAnalysis(analysis, version);
	}

	/**
	 * getVersionsFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#getVersionsFromAnalysisId(int)
	 */
	@Override
	public List<String> getVersionsFromAnalysis(Integer analysisId)  {
		return daoHistory.getVersionsFromAnalysis(analysisId);
	}

	/**
	 * getAllHistories: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#getAllHistories()
	 */
	@Override
	public List<History> getAll()  {
		return daoHistory.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#getAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	public List<History> getAllFromAnalysis(Integer id)  {
		return daoHistory.getAllFromAnalysis(id);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#getAllFromAnalysis(lu.itrust.business.ts.model.analysis.Analysis)
	 */
	@Override
	public List<History> getAllFromAnalysis(Analysis analysis)  {
		return daoHistory.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllHistoriesFromAnalysisByAuthor: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param author
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#getAllHistoriesFromAnalysisByAuthor(lu.itrust.business.ts.model.analysis.Analysis,
	 *      java.lang.String)
	 */
	@Override
	public List<History> getAllFromAnalysisByAuthor(Analysis analysis, String author)  {
		return daoHistory.getAllFromAnalysisByAuthor(analysis, author);
	}

	/**
	 * getAllHistoriesFromAnalysisByVersion: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param version
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#getAllHistoriesFromAnalysisByVersion(lu.itrust.business.ts.model.analysis.Analysis,
	 *      java.lang.String)
	 */
	@Override
	public List<History> getAllFromAnalysisByVersion(Analysis analysis, String version)  {
		return daoHistory.getAllFromAnalysisByVersion(analysis, version);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param history
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#save(java.lang.Integer,
	 *      lu.itrust.business.ts.model.history.History)
	 */
	@Transactional
	@Override
	public void save(Integer analysisId, History history)  {
		daoHistory.save(analysisId, history);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param history
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#save(lu.itrust.business.ts.model.history.History)
	 */
	@Transactional
	@Override
	public void save(History history)  {
		daoHistory.save(history);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param history
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#saveOrUpdate(lu.itrust.business.ts.model.history.History)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(History history)  {
		daoHistory.saveOrUpdate(history);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param history
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceHistory#delete(lu.itrust.business.ts.model.history.History)
	 */
	@Transactional
	@Override
	public void delete(History history)  {
		daoHistory.delete(history);
	}
}