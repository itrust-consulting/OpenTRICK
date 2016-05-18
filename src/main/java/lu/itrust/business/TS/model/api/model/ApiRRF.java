/**
 * 
 */
package lu.itrust.business.TS.model.api.model;

import java.util.Collections;
import java.util.List;

/**
 * @author eomar
 *
 */
public class ApiRRF {
	
	private int analysisId;
	
	private double impact;
	
	private double probability;
	
	private ApiAsset asset;
	
	private ApiNamable scenario;
	
	private List<ApiStandard> standards = Collections.emptyList();

	/**
	 * @return the analysisId
	 */
	public int getAnalysisId() {
		return analysisId;
	}

	/**
	 * @param analysisId the analysisId to set
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
	 * @param impact the impact to set
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
	 * @param probability the probability to set
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
	 * @param asset the asset to set
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
	 * @param scenario the scenario to set
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
	 * @param standards the standards to set
	 */
	public void setStandards(List<ApiStandard> standards) {
		this.standards = standards;
	}
}
