package lu.itrust.business.TS.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAORiskInformation;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;

/**
 * ServiceRiskInformationImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
@Transactional(readOnly = true)
public class ServiceRiskInformationImpl implements ServiceRiskInformation {

	@Autowired
	private DAORiskInformation daoRiskInformation;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#get(int)
	 */
	@Override
	public RiskInformation get(Integer id)  {
		return daoRiskInformation.get(id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param id
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getFromAnalysisById(int, int)
	 */
	@Override
	public RiskInformation getFromAnalysisById(Integer idAnalysis, Integer id)  {
		return daoRiskInformation.getFromAnalysisById(idAnalysis, id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param riskinformationId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer riskinformationId)  {
		return daoRiskInformation.belongsToAnalysis(analysisId, riskinformationId);
	}

	/**
	 * getAllRiskInformation: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getAllRiskInformation()
	 */
	@Override
	public List<RiskInformation> getAll()  {
		return daoRiskInformation.getAll();
	}

	/**
	 * getAllByChapter: <br>
	 * Description
	 * 
	 * @param chapter
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getAllByChapter(java.lang.String)
	 */
	@Override
	public List<RiskInformation> getAllByChapter(String chapter)  {
		return daoRiskInformation.getAllByChapter(chapter);
	}

	/**
	 * getAllByCategory: <br>
	 * Description
	 * 
	 * @param category
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getAllByCategory(java.lang.String)
	 */
	@Override
	public List<RiskInformation> getAllByCategory(String category)  {
		return daoRiskInformation.getAllByCategory(category);
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getAllFromAnalysisId(int)
	 */
	@Override
	public List<RiskInformation> getAllFromAnalysis(Integer analysisID)  {
		return daoRiskInformation.getAllFromAnalysis(analysisID);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#getAllFromAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public List<RiskInformation> getAllFromAnalysis(Analysis analysis)  {
		return daoRiskInformation.getAllFromAnalysis(analysis);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param riskInformation
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#save(lu.itrust.business.TS.model.riskinformation.RiskInformation)
	 */
	@Transactional
	@Override
	public void save(RiskInformation riskInformation)  {
		daoRiskInformation.save(riskInformation);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param riskInformation
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#saveOrUpdate(lu.itrust.business.TS.model.riskinformation.RiskInformation)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(RiskInformation riskInformation)  {
		daoRiskInformation.saveOrUpdate(riskInformation);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param riskInformation
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRiskInformation#delete(lu.itrust.business.TS.model.riskinformation.RiskInformation)
	 */
	@Transactional
	@Override
	public void delete(RiskInformation riskInformation)  {
		daoRiskInformation.delete(riskInformation);
	}

	@Override
	public void delete(Collection<RiskInformation> riskInformations) {
		daoRiskInformation.delete(riskInformations);
		
	}

	@Override
	public List<RiskInformation> getAllByIdAnalysisAndCategory(Integer idAnalysis, String... types) {
		return daoRiskInformation.getAllByIdAnalysisAndCategories(idAnalysis,types);
	}

	@Override
	public List<RiskInformation> findByIdAnalysisAndCategory(Integer idAnalysis, String type) {
		return daoRiskInformation.findByIdAnalysisAndCategory(idAnalysis, type);
	}
}