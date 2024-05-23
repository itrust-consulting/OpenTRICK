/**
 * 
 */
package lu.itrust.business.ts.model.analysis;

import java.io.Serializable;


/**
 * Represents the type of analysis.
 */
public enum AnalysisType implements Serializable {
	QUANTITATIVE, HYBRID, QUALITATIVE;

	/**
	 * Checks if the analysis type is hybrid.
	 *
	 * @return true if the analysis type is hybrid, false otherwise.
	 */
	public boolean isHybrid() {
		return HYBRID == this;
	}

	/**
	 * Checks if the analysis type is qualitative.
	 *
	 * @return true if the analysis type is qualitative, false otherwise.
	 */
	public boolean isQualitative() {
		return this == QUALITATIVE || isHybrid();
	}

	/**
	 * Checks if the analysis type is quantitative.
	 *
	 * @return true if the analysis type is quantitative, false otherwise.
	 */
	public boolean isQuantitative() {
		return QUANTITATIVE == this || isHybrid();
	}

	/**
	 * Checks if the given analysis type is hybrid.
	 *
	 * @param type the analysis type to check
	 * @return true if the analysis type is hybrid, false otherwise.
	 */
	public static boolean isHybrid(AnalysisType type) {
		return HYBRID == type;
	}

	/**
	 * Checks if the given analysis type is qualitative.
	 *
	 * @param type the analysis type to check
	 * @return true if the analysis type is qualitative, false otherwise.
	 */
	public static boolean isQualitative(AnalysisType type) {
		return QUALITATIVE == type || isHybrid(type);
	}

	/**
	 * Checks if the given analysis type is quantitative.
	 *
	 * @param type the analysis type to check
	 * @return true if the analysis type is quantitative, false otherwise.
	 */
	public static boolean isQuantitative(AnalysisType type) {
		return QUANTITATIVE == type || isHybrid(type);
	}
}
