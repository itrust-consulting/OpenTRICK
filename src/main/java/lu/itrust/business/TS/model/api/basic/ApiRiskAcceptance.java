package lu.itrust.business.TS.model.api.basic;

import java.util.List;
import java.util.Map;

public class ApiRiskAcceptance {
	private List<ApiRiskLevel> likelihoodLevels;
	private Map<String, List<ApiRiskLevel>> impactLevels; 
	private List<ApiRiskAcceptanceLevel> acceptanceLevels;

	public List<ApiRiskLevel> getLikelihoodLevels() {
		return likelihoodLevels;
	}
	public void setLikelihoodLevels(List<ApiRiskLevel> likelihoodLevels) {
		this.likelihoodLevels = likelihoodLevels;
	}
	public Map<String, List<ApiRiskLevel>> getImpactLevels() {
		return impactLevels;
	}
	public void setImpactLevels(Map<String, List<ApiRiskLevel>> impactLevels) {
		this.impactLevels = impactLevels;
	}
	public List<ApiRiskAcceptanceLevel> getAcceptanceLevels() {
		return acceptanceLevels;
	}
	public void setAcceptanceLevels(List<ApiRiskAcceptanceLevel> acceptanceLevels) {
		this.acceptanceLevels = acceptanceLevels;
	}
}
