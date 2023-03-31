/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAORiskProfile;
import lu.itrust.business.ts.database.service.ServiceRiskProfile;
import lu.itrust.business.ts.model.cssf.RiskProfile;

/**
 * @author eomar
 *
 */
@Service
@Transactional(readOnly = true)
public class ServiceProfileImpl implements ServiceRiskProfile {

	@Autowired
	private DAORiskProfile daoRiskProfile;

	@Override
	public RiskProfile get(Integer id) {
		return daoRiskProfile.get(id);
	}

	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer idRiskProfile) {
		return daoRiskProfile.belongsToAnalysis(analysisId, idRiskProfile);
	}

	@Override
	public List<RiskProfile> getAllFromAnalysis(Integer analysisId) {
		return daoRiskProfile.getAllFromAnalysis(analysisId);
	}

	@Transactional
	@Override
	public void save(RiskProfile riskProfile) {
		daoRiskProfile.save(riskProfile);
	}

	@Transactional
	@Override
	public void saveOrUpdate(RiskProfile riskProfile) {
		daoRiskProfile.saveOrUpdate(riskProfile);
	}

	@Transactional
	@Override
	public void delete(RiskProfile riskProfile) {
		daoRiskProfile.delete(riskProfile);
	}

	@Transactional
	@Override
	public void deleteAllFromAnalysis(Integer analysisID) {
		daoRiskProfile.deleteAllFromAnalysis(analysisID);
	}

	@Transactional
	@Override
	public void delete(Integer idRiskProfile) {
		daoRiskProfile.delete(idRiskProfile);
	}

	@Transactional
	@Override
	public RiskProfile merge(RiskProfile riskProfile) {
		return daoRiskProfile.merge(riskProfile);
	}

	@Override
	public RiskProfile getByAssetAndScanrio(int idAsset, int idScenario) {
		return daoRiskProfile.getByAssetAndScanrio(idAsset, idScenario);
	}

	@Override
	public RiskProfile getFromAnalysisById(int idAnalysis, int idRiskProfile) {
		return daoRiskProfile.getFromAnalysisById(idAnalysis,idRiskProfile);
	}

	@Override
	public boolean isUsed(String identifier, Integer idAnalysis) {
		return daoRiskProfile.isUsed(identifier,idAnalysis );
	}

}
