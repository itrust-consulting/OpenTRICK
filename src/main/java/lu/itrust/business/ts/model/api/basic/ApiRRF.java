/**
 * 
 */
package lu.itrust.business.ts.model.api.basic;

import java.util.LinkedList;
import java.util.List;

/**
 * The `ApiRRF` class represents a Risk Response Factor (RRF) in the API model.
 * It contains information about the analysis ID, impact, probability, asset, scenario, and standards.
 */
public class ApiRRF {

	private int analysisId;

	private double impact;

	private double probability;

	private ApiAsset asset;

	private ApiNamable scenario;

	private List<ApiStandard> standards = new LinkedList<>();

	/**
	 * 
	 */
	public ApiRRF() {
	}

	/**
	 * @param analysisId
	 */
	public ApiRRF(int analysisId, double impact, double probability) {
		this.analysisId = analysisId;
		this.impact = impact;
		this.probability = probability;
	}

	/**
	 * @return the analysisId
	 */
	public int getAnalysisId() {
		return analysisId;
	}

	/**
	 * @param analysisId
	 *            the analysisId to set
	 */
	public void setAnalysisId(int analysisId) {
		this.analysisId = analysisId;
	}

	/**
	 * @return the impact
	 */
	public double getImpact() {
		return impact;
	}

	/**
	 * @param impact
	 *            the impact to set
	 */
	public void setImpact(double impact) {
		this.impact = impact;
	}

	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * @param probability
	 *            the probability to set
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

	/**
	 * @return the asset
	 */
	public ApiAsset getAsset() {
		return asset;
	}

	/**
	 * @param asset
	 *            the asset to set
	 */
	public void setAsset(ApiAsset asset) {
		this.asset = asset;
	}

	/**
	 * @return the scenario
	 */
	public ApiNamable getScenario() {
		return scenario;
	}

	/**
	 * @param scenario
	 *            the scenario to set
	 */
	public void setScenario(ApiNamable scenario) {
		this.scenario = scenario;
	}

	/**
	 * @return the standards
	 */
	public List<ApiStandard> getStandards() {
		return standards;
	}

	/**
	 * @param standards
	 *            the standards to set
	 */
	public void setStandards(List<ApiStandard> standards) {
		this.standards = standards;
	}
}
