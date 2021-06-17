package lu.itrust.business.TS.model.api.basic;

import java.util.Map;
import java.util.stream.Collectors;

import lu.itrust.business.TS.model.assessment.Assessment;

/**
 * @author smuller
 */
public class ApiAssessment extends ApiAssessmentValue {

	private ApiAsset asset;
	private ApiScenario scenario;

	public ApiAssessment() {
	}

	public ApiAssessment(Integer id, ApiAsset asset, ApiScenario scenario, double likelihood, Map<String, Object> impacts) {
		super(id, likelihood, impacts);
		this.asset = asset;
		this.scenario = scenario;
	}

	public static ApiAssessment create(Assessment assessment) {
		return new ApiAssessment(
			assessment.getId(),
			ApiAsset.create(assessment.getAsset()),
			ApiScenario.create(assessment.getScenario()),
			assessment.getLikelihoodReal(),
			assessment.getImpacts().stream().collect(Collectors.toMap(v -> v.getName(), v -> v.getReal())));
	}

	public ApiAsset getAsset() {
		return asset;
	}

	public void setAsset(ApiAsset asset) {
		this.asset = asset;
	}

	public ApiScenario getScenario() {
		return scenario;
	}

	public void setScenario(ApiScenario scenario) {
		this.scenario = scenario;
	}
}
