package lu.itrust.business.TS.model.api.basic;

import java.util.Map;

/**
 * @author smuller
 */
public class ApiAssessmentValue extends ApiTrickObject {

	private Object likelihood;
	private Map<String, Object> impacts;

	public ApiAssessmentValue() {
	}

	public ApiAssessmentValue(Object id, Object likelihood, Map<String, Object> impacts) {
		super(id);
		this.likelihood = likelihood;
		this.impacts = impacts;
	}

	public Object getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(double likelihood) {
		this.likelihood = likelihood;
	}

	public Map<String, Object> getImpacts() {
		return impacts;
	}
}
