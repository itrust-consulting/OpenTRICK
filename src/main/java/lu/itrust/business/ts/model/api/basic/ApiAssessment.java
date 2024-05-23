package lu.itrust.business.ts.model.api.basic;

import java.util.Map;
import java.util.stream.Collectors;

import lu.itrust.business.ts.model.assessment.Assessment;


/**
 * Represents an API assessment.
 */
public class ApiAssessment extends ApiAssessmentValue {

	private ApiAsset asset;
	private ApiScenario scenario;

	/**
	 * Default constructor.
	 */
	public ApiAssessment() {
	}

	/**
	 * Constructs an ApiAssessment object with the specified parameters.
	 *
	 * @param id        the assessment ID
	 * @param asset     the API asset
	 * @param scenario  the API scenario
	 * @param likelihood the likelihood value
	 * @param impacts   the map of impacts
	 */
	public ApiAssessment(Integer id, ApiAsset asset, ApiScenario scenario, double likelihood, Map<String, Object> impacts) {
		super(id, likelihood, impacts);
		this.asset = asset;
		this.scenario = scenario;
	}

	/**
	 * Creates an ApiAssessment object from the given Assessment object.
	 *
	 * @param assessment the Assessment object
	 * @return the created ApiAssessment object
	 */
	public static ApiAssessment create(Assessment assessment) {
		return new ApiAssessment(
			assessment.getId(),
			ApiAsset.create(assessment.getAsset()),
			ApiScenario.create(assessment.getScenario()),
			assessment.getLikelihoodReal(),
			assessment.getImpacts().stream().collect(Collectors.toMap(v -> v.getName(), v -> v.getReal())));
	}

	/**
	 * Gets the API asset.
	 *
	 * @return the API asset
	 */
	public ApiAsset getAsset() {
		return asset;
	}

	/**
	 * Sets the API asset.
	 *
	 * @param asset the API asset to set
	 */
	public void setAsset(ApiAsset asset) {
		this.asset = asset;
	}

	/**
	 * Gets the API scenario.
	 *
	 * @return the API scenario
	 */
	public ApiScenario getScenario() {
		return scenario;
	}

	/**
	 * Sets the API scenario.
	 *
	 * @param scenario the API scenario to set
	 */
	public void setScenario(ApiScenario scenario) {
		this.scenario = scenario;
	}
}
