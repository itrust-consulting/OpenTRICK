/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.cssf.RiskProfile;

/**
 * @author eomar
 *
 */
public interface DAORiskProfile {
	
	 RiskProfile get(Integer id) ;

	 boolean belongsToAnalysis(Integer analysisId, Integer idRiskProfile);

	 List<RiskProfile> getAllFromAnalysis(Integer analysisId);

	 void save(RiskProfile riskProfile);

	 void saveOrUpdate(RiskProfile riskProfile);

	 void delete(RiskProfile riskProfile);
	
	 void deleteAllFromAnalysis(Integer analysisID);

	 void delete(Integer idRiskProfile);

	 RiskProfile merge(RiskProfile riskProfile);

	RiskProfile getByAssetAndScanrio(int idAsset, int idScenario);

	RiskProfile getFromAnalysisById(int idAnalysis, int idRiskProfile);

}
