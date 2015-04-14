/**
 * 
 */
package lu.itrust.business.TS.model.cssf.helper;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.cssf.EvaluationResult;

/**
 * @author eomar
 *
 */
public class RiskRegisterHelper {
	
	/** The Expected Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult netEvaluation = null;
	
	/** The Raw Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult rawEvaluation = null;

	/** The Net Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult expectedImportance = null;
	
	
	/**
	 * Constructors:<br>
	 * 
	 * @throws TrickException
	 */
	public RiskRegisterHelper() throws TrickException {
		rawEvaluation = new EvaluationResult(0, 0);
		expectedImportance = new EvaluationResult(0, 0);
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

	public EvaluationResult getExpectedImportance() {
		return expectedImportance;
	}

	public void setExpectedImportance(EvaluationResult expectedImportance) {
		this.expectedImportance = expectedImportance;
	}
	
	

}
