package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.riskinformation.RiskInformation;
import lu.itrust.business.TS.database.dao.DAORiskInformation;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;

/**
 * ServiceRiskInformationImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
public class ServiceRiskInformationImpl implements ServiceRiskInformation {

	@Autowired
	private DAORiskInformation daoRiskInformation;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#get(int)
	 */
	@Override
	public RiskInformation get(Integer id) throws Exception {
		return daoRiskInformation.get(id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param id
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getFromAnalysisById(int, int)
	 */
	@Override
	public RiskInformation getFromAnalysisById(Integer idAnalysis, Integer id) throws Exception {
		return daoRiskInformation.getFromAnalysisById(idAnalysis, id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param riskinformationId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer riskinformationId) throws Exception {
		return daoRiskInformation.belongsToAnalysis(analysisId, riskinformationId);
	}

	/**
	 * getAllRiskInformation: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getAllRiskInformation()
	 */
	@Override
	public List<RiskInformation> getAll() throws Exception {
		return daoRiskInformation.getAll();
	}

	/**
	 * getAllByChapter: <br>
	 * Description
	 * 
	 * @param chapter
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getAllByChapter(java.lang.String)
	 */
	@Override
	public List<RiskInformation> getAllByChapter(String chapter) throws Exception {
		return daoRiskInformation.getAllByChapter(chapter);
	}

	/**
	 * getAllByCategory: <br>
	 * Description
	 * 
	 * @param category
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getAllByCategory(java.lang.String)
	 */
	@Override
	public List<RiskInformation> getAllByCategory(String category) throws Exception {
		return daoRiskInformation.getAllByCategory(category);
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getAllFromAnalysisId(int)
	 */
	@Override
	public List<RiskInformation> getAllFromAnalysis(Integer analysisID) throws Exception {
		return daoRiskInformation.getAllFromAnalysis(analysisID);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getAllFromAnalysis(lu.itrust.business.TS.data.analysis.Analysis)
	 */
	@Override
	public List<RiskInformation> getAllFromAnalysis(Analysis analysis) throws Exception {
		return daoRiskInformation.getAllFromAnalysis(analysis);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param riskInformation
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#save(lu.itrust.business.TS.data.riskinformation.RiskInformation)
	 */
	@Transactional
	@Override
	public void save(RiskInformation riskInformation) throws Exception {
		daoRiskInformation.save(riskInformation);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param riskInformation
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#saveOrUpdate(lu.itrust.business.TS.data.riskinformation.RiskInformation)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(RiskInformation riskInformation) throws Exception {
		daoRiskInformation.saveOrUpdate(riskInformation);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param riskInformation
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#delete(lu.itrust.business.TS.data.riskinformation.RiskInformation)
	 */
	@Transactional
	@Override
	public void delete(RiskInformation riskInformation) throws Exception {
		daoRiskInformation.delete(riskInformation);
	}
}