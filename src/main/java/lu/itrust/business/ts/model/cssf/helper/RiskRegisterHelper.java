/**
 * 
 */
package lu.itrust.business.ts.model.cssf.helper;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.cssf.EvaluationResult;

/**
 * Used into riskRegister JSP
 */
/**
 * The RiskRegisterHelper class represents a helper class for managing risk register data.
 */
public class RiskRegisterHelper {
	/** The Net Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult netEvaluation = null;
	
	/** The Raw Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult rawEvaluation = null;

	/** The Expected Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult expectedEvaluation = null;
	
	
	/**
	 * Constructs a new RiskRegisterHelper object.
	 * @throws TrickException if an error occurs during initialization.
	 */
	public RiskRegisterHelper() throws TrickException {
		rawEvaluation = new EvaluationResult(0, 0);
		expectedEvaluation = new EvaluationResult(0, 0);
		netEvaluation = new EvaluationResult(0, 0);
	}
	
	/**
	 * Gets the net evaluation data.
	 * @return the net evaluation data.
	 */
	public EvaluationResult getNetEvaluation() {
		return netEvaluation;
	}

	/**
	 * Sets the net evaluation data.
	 * @param netEvaluation the net evaluation data to set.
	 */
	public void setNetEvaluation(EvaluationResult netEvaluation) {
		this.netEvaluation = netEvaluation;
	}

	/**
	 * Gets the raw evaluation data.
	 * @return the raw evaluation data.
	 */
	public EvaluationResult getRawEvaluation() {
		return rawEvaluation;
	}

	/**
	 * Sets the raw evaluation data.
	 * @param rawEvaluation the raw evaluation data to set.
	 */
	public void setRawEvaluation(EvaluationResult rawEvaluation) {
		this.rawEvaluation = rawEvaluation;
	}

	/**
	 * Gets the expected evaluation data.
	 * @return the expected evaluation data.
	 */
	public EvaluationResult getExpectedEvaluation() {
		return expectedEvaluation;
	}

	/**
	 * Sets the expected evaluation data.
	 * @param expectedEvaluation the expected evaluation data to set.
	 */
	public void setExpectedEvaluation(EvaluationResult expectedEvaluation) {
		this.expectedEvaluation = expectedEvaluation;
	}
}
