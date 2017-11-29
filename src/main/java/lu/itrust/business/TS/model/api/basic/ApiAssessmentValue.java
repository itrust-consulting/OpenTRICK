package lu.itrust.business.TS.model.api.basic;

import java.util.Map;

/**
 * @author smuller
 */
public class ApiAssessmentValue extends ApiTrickObject {

	private double likelihood;
	private Map<String, Double> impacts;

	public ApiAssessmentValue() {
	}

	public ApiAssessmentValue(Object id, double likelihood, Map<String, Double> impacts) {
		super(id);
		this.likelihood = likelihood;
		this.impacts = impacts;
	}

	public double getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(double likelihood) {
		this.likelihood = likelihood;
	}

	public Map<String, Double> getImpacts() {
		return impacts;
	}
}
