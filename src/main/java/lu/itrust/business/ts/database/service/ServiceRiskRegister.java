package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.cssf.RiskRegisterItem;

/**
 * ServiceRiskRegister.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceRiskRegister {
	public RiskRegisterItem get(Integer id);

	public boolean belongsToAnalysis(Integer analysisId, Integer riskregisterItemId);

	public List<RiskRegisterItem> getAllFromAnalysis(Integer analysisId);

	public void save(RiskRegisterItem riskRegisterItem);

	public void saveOrUpdate(RiskRegisterItem riskRegisterItem);

	public void delete(RiskRegisterItem riskRegisterItem);
	
	public void deleteAllFromAnalysis(Integer analysisID);

	public RiskRegisterItem getByAssetIdAndScenarioId(int idAsset, int idScenario);
}