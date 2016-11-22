/**
 * 
 */
package lu.itrust.business.TS.model.cssf.helper;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.cssf.EvaluationResult;

/**
 * @author eomar
 * Used into riskRegister JSP
 */
public class RiskRegisterHelper {
	/** The Net Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult netEvaluation = null;
	
	/** The Raw Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult rawEvaluation = null;

	/** The Expected Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult expectedEvaluation = null;
	
	
	/**
	 * Constructors:<br>
	 * 
	 * @throws TrickException
	 */
	public RiskRegisterHelper() throws TrickException {
		rawEvaluation = new EvaluationResult(0, 0);
		expectedEvaluation = new EvaluationResult(0, 0);
		netEvaluation = new EvaluationResult(0, 0);
	}
	
	public EvaluationResult getNetEvaluation() {
		return netEvaluation;
	}

	public void setNetEvaluation(EvaluationResult netEvaluation) {
		this.netEvaluation = netEvaluation;
	}

	public EvaluationResult getRawEvaluation() {
		return rawEvaluation;
	}

	public void setRawEvaluation(EvaluationResult rawEvaluation) {
		this.rawEvaluation = rawEvaluation;
	}

	public EvaluationResult getExpectedEvaluation() {
		return expectedEvaluation;
	}

	public void setExpectedEvaluation(EvaluationResult expectedEvaluation) {
		this.expectedEvaluation = expectedEvaluation;
	}
	
	

}
