package lu.itrust.business.ts.model.api.basic;

import java.util.Map;


/**
 * Represents an API assessment value.
 * This class extends the {@link ApiTrickObject} class.
 */
public class ApiAssessmentValue extends ApiTrickObject {

	private Object likelihood;
	private Map<String, Object> impacts;

	/**
	 * Default constructor.
	 */
	public ApiAssessmentValue() {
	}

	/**
	 * Constructs an instance of {@code ApiAssessmentValue} with the specified id, likelihood, and impacts.
	 *
	 * @param id        the id of the assessment value
	 * @param likelihood the likelihood of the assessment value
	 * @param impacts    the impacts of the assessment value
	 */
	public ApiAssessmentValue(Object id, Object likelihood, Map<String, Object> impacts) {
		super(id);
		this.likelihood = likelihood;
		this.impacts = impacts;
	}

	/**
	 * Returns the likelihood of the assessment value.
	 *
	 * @return the likelihood of the assessment value
	 */
	public Object getLikelihood() {
		return likelihood;
	}

	/**
	 * Sets the likelihood of the assessment value.
	 *
	 * @param likelihood the likelihood of the assessment value
	 */
	public void setLikelihood(double likelihood) {
		this.likelihood = likelihood;
	}

	/**
	 * Returns the impacts of the assessment value.
	 *
	 * @return the impacts of the assessment value
	 */
	public Map<String, Object> getImpacts() {
		return impacts;
	}
}
