package lu.itrust.business.ts.database.dao;

import java.util.List;

import lu.itrust.business.ts.model.cssf.RiskRegisterItem;

/**
 * DAORiskRegister.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAORiskRegister {
	public RiskRegisterItem get(Integer id);

	public boolean belongsToAnalysis(Integer analysisId, Integer riskregisterItemId);

	public List<RiskRegisterItem> getAllFromAnalysis(Integer analysisId);

	public void save(RiskRegisterItem riskRegisterItem);

	public void saveOrUpdate(RiskRegisterItem riskRegisterItem);

	public void delete(RiskRegisterItem riskRegisterItem);
	
	public void deleteAllFromAnalysis(Integer analysisID);

	public void delete(Integer integer);

	public RiskRegisterItem merge(RiskRegisterItem riskRegister);

	public RiskRegisterItem getByAssetIdAndScenarioId(int idAsset, int idScenario);
}