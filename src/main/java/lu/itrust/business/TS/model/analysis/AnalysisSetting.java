/**
 * 
 */
package lu.itrust.business.TS.model.analysis;

/**
 * @author eomar
 *
 */
public enum AnalysisSetting {

	ALLOW_RISK_ESTIMATION_RAW_COLUMN("label.analysis.setting.allow_risk_estimation_raw_column", Boolean.class, false, AnalysisType.QUALITATIVE), 
	ALLOW_RISK_HIDDEN_COMMENT("label.analysis.setting.allow_hidden_comment", Boolean.class, false,null), 
	ALLOW_DYNAMIC_ANALYSIS("label.analysis.setting.allow_dynamic_analysis", Boolean.class, false, AnalysisType.QUANTITATIVE);

	private String code;

	private Class<?> type;

	private Object defaultValue;

	private AnalysisType analysisType;

	private AnalysisSetting(String code, Class<?> type, Object defaultValue, AnalysisType analysisType) {
		this.setCode(code);
		this.setType(type);
		this.setDefaultValue(defaultValue);
		setAnalysisType(analysisType);
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	protected void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	protected void setType(Class<?> type) {
		this.type = type;
	}

	/**
	 * @return the defaultValue
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	protected void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the analysisType
	 */
	public boolean isSupported(AnalysisType analysisType) {
		return this.analysisType == null || this.analysisType.equals(analysisType);
	}

	/**
	 * @param analysisType
	 *            the analysisType to set
	 */
	protected void setAnalysisType(AnalysisType analysisType) {
		this.analysisType = analysisType;
	}

}
