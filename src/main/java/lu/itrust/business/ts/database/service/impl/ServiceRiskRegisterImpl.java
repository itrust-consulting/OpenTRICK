package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAORiskRegister;
import lu.itrust.business.ts.database.service.ServiceRiskRegister;
import lu.itrust.business.ts.model.cssf.RiskRegisterItem;

/**
 * ServiceRiskRegisterImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
@Transactional(readOnly = true)
public class ServiceRiskRegisterImpl implements ServiceRiskRegister {

	@Autowired
	private DAORiskRegister daoRiskRegister;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceRiskRegister#get(int)
	 */
	@Override
	public RiskRegisterItem get(Integer id)  {
		return daoRiskRegister.get(id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * @param analysisId
	 * @param riskregisterItemId
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceRiskRegister#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId,Integer riskregisterItemId)  {
		return daoRiskRegister.belongsToAnalysis(analysisId,riskregisterItemId);
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceRiskRegister#getAllFromAnalysisId(java.lang.Integer)
	 */
	@Override
	public List<RiskRegisterItem> getAllFromAnalysis(Integer analysisID)  {
		return daoRiskRegister.getAllFromAnalysis(analysisID);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param riskRegisterItem
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceRiskRegister#save(lu.itrust.business.ts.model.cssf.RiskRegisterItem)
	 */
	@Transactional
	@Override
	public void save(RiskRegisterItem riskRegisterItem)  {
		daoRiskRegister.save(riskRegisterItem);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param riskRegisterItem
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceRiskRegister#saveOrUpdate(lu.itrust.business.ts.model.cssf.RiskRegisterItem)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(RiskRegisterItem riskRegisterItem)  {
		daoRiskRegister.saveOrUpdate(riskRegisterItem);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param riskRegisterItem
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceRiskRegister#delete(lu.itrust.business.ts.model.cssf.RiskRegisterItem)
	 */
	@Transactional
	@Override
	public void delete(RiskRegisterItem riskRegisterItem)  {
		daoRiskRegister.delete(riskRegisterItem);
	}

	@Transactional
	@Override
	public void deleteAllFromAnalysis(Integer analysisID)  {
		daoRiskRegister.deleteAllFromAnalysis(analysisID);

	}

	@Override
	public RiskRegisterItem getByAssetIdAndScenarioId(int idAsset, int idScenario) {
		return daoRiskRegister.getByAssetIdAndScenarioId(idAsset,idScenario);
	}
}