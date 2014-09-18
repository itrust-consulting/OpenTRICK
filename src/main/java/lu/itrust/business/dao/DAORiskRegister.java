package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.cssf.RiskRegisterItem;

/**
 * DAORiskRegister.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAORiskRegister {
	public RiskRegisterItem get(Integer id) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer riskregisterItemId) throws Exception;

	public List<RiskRegisterItem> getAllFromAnalysis(Integer analysisId) throws Exception;

	public void save(RiskRegisterItem riskRegisterItem) throws Exception;

	public void saveOrUpdate(RiskRegisterItem riskRegisterItem) throws Exception;

	public void delete(RiskRegisterItem riskRegisterItem) throws Exception;
	
	public void deleteAllFromAnalysis(Integer analysisID) throws Exception;
}