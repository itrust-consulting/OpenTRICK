package lu.itrust.business.ts.model.api.basic;

import java.util.List;
import java.util.Map;

/**
 * Represents the risk acceptance configuration for an API.
 */
public class ApiRiskAcceptance {
	private List<ApiRiskLevel> likelihoodLevels;
	private Map<String, List<ApiRiskLevel>> impactLevels;
	private List<ApiRiskAcceptanceLevel> acceptanceLevels;

	/**
	 * Gets the likelihood levels for the risk acceptance.
	 *
	 * @return The likelihood levels.
	 */
	public List<ApiRiskLevel> getLikelihoodLevels() {
		return likelihoodLevels;
	}

	/**
	 * Sets the likelihood levels for the risk acceptance.
	 *
	 * @param likelihoodLevels The likelihood levels to set.
	 */
	public void setLikelihoodLevels(List<ApiRiskLevel> likelihoodLevels) {
		this.likelihoodLevels = likelihoodLevels;
	}

	/**
	 * Gets the impact levels for the risk acceptance.
	 *
	 * @return The impact levels.
	 */
	public Map<String, List<ApiRiskLevel>> getImpactLevels() {
		return impactLevels;
	}

	/**
	 * Sets the impact levels for the risk acceptance.
	 *
	 * @param impactLevels The impact levels to set.
	 */
	public void setImpactLevels(Map<String, List<ApiRiskLevel>> impactLevels) {
		this.impactLevels = impactLevels;
	}

	/**
	 * Gets the acceptance levels for the risk acceptance.
	 *
	 * @return The acceptance levels.
	 */
	public List<ApiRiskAcceptanceLevel> getAcceptanceLevels() {
		return acceptanceLevels;
	}

	/**
	 * Sets the acceptance levels for the risk acceptance.
	 *
	 * @param acceptanceLevels The acceptance levels to set.
	 */
	public void setAcceptanceLevels(List<ApiRiskAcceptanceLevel> acceptanceLevels) {
		this.acceptanceLevels = acceptanceLevels;
	}
}
