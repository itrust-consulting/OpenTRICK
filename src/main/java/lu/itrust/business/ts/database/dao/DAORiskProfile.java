/**
 * 
 */
package lu.itrust.business.ts.database.dao;

import java.util.List;

import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.standard.measure.Measure;

/**
 * @author eomar
 *
 */
public interface DAORiskProfile {
	
	 RiskProfile get(Integer id);

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

	boolean isUsed(String identifier, Integer idAnalysis);

	List<RiskProfile> findByIdAnalysisAndContainsMeasure(Integer idAnalysis, Measure measure);

	void resetRiskIdByIds(List<Integer> ids);

}
