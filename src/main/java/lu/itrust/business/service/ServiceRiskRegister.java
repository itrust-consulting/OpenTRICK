package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.cssf.RiskRegisterItem;

/**
 * ServiceRiskRegister.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceRiskRegister {
	public RiskRegisterItem get(Integer id) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer riskregisterItemId) throws Exception;

	public List<RiskRegisterItem> getAllFromAnalysis(Integer analysisId) throws Exception;

	public void save(RiskRegisterItem riskRegisterItem) throws Exception;

	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) throws Exception;

	public void delete(RiskRegisterItem riskRegisterItem) throws Exception;
	
	public void deleteAllFromAnalysis(Integer analysisID) throws Exception;
}