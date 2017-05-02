/**
 * 
 */
package lu.itrust.business.TS.model.analysis;

/**
 * @author eomar
 *
 */
public enum AnalysisType {
	HYBRID, QUALITATIVE, QUANTITATIVE;

	public boolean isHybrid() {
		return HYBRID == this;
	}

	public boolean isQualitative() {
		return this == QUALITATIVE;
	}

	public boolean isQuantitative() {
		return QUANTITATIVE == this || isHybrid();
	}

	public static boolean isHybrid(AnalysisType type) {
		return HYBRID == type;
	}

	public static boolean isQualitative(AnalysisType type) {
		return QUALITATIVE == type || isHybrid(type);
	}

	public static boolean isQuantitative(AnalysisType type) {
		return QUANTITATIVE == type || isHybrid(type);
	}
	
}
