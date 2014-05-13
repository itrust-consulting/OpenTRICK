package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.cssf.RiskRegisterItem;
import lu.itrust.business.dao.DAORiskRegister;
import lu.itrust.business.service.ServiceRiskRegister;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceRiskRegisterImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Transactional
@Service
public class ServiceRiskRegisterImpl implements ServiceRiskRegister {

	@Autowired
	private DAORiskRegister daoRiskRegister;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRiskRegister#get(int)
	 */
	@Override
	public RiskRegisterItem get(Integer id) throws Exception {
		return daoRiskRegister.get(id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param riskregisterItemId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRiskRegister#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer riskregisterItemId, Integer analysisId) throws Exception {
		return daoRiskRegister.belongsToAnalysis(riskregisterItemId, analysisId);
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRiskRegister#getAllFromAnalysisId(java.lang.Integer)
	 */
	@Override
	public List<RiskRegisterItem> getAllFromAnalysis(Integer analysisID) throws Exception {
		return daoRiskRegister.getAllFromAnalysis(analysisID);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param riskRegisterItem
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRiskRegister#save(lu.itrust.business.TS.cssf.RiskRegisterItem)
	 */
	@Transactional
	@Override
	public void save(RiskRegisterItem riskRegisterItem) throws Exception {
		daoRiskRegister.save(riskRegisterItem);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param riskRegisterItem
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRiskRegister#saveOrUpdate(lu.itrust.business.TS.cssf.RiskRegisterItem)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) throws Exception {
		daoRiskRegister.saveOrUpdate(riskRegisterItem);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param riskRegisterItem
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRiskRegister#delete(lu.itrust.business.TS.cssf.RiskRegisterItem)
	 */
	@Transactional
	@Override
	public void delete(RiskRegisterItem riskRegisterItem) throws Exception {
		daoRiskRegister.delete(riskRegisterItem);
	}
}