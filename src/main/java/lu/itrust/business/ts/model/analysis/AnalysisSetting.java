/**
 * 
 */
package lu.itrust.business.ts.model.analysis;

import java.util.Arrays;
import java.util.List;


/**
 * Represents the settings for analysis in the system.
 */
public enum AnalysisSetting {

	// ... enum constants and fields ...
	ALLOW_RISK_ESTIMATION_RAW_COLUMN("label.analysis.setting.allow_risk_estimation_raw_column", Boolean.class, true,
			AnalysisType.QUALITATIVE,
			AnalysisType.HYBRID),
	ALLOW_FULL_COST_RELATED_TO_MEASURE("label.analysis.setting.allow_full_cost_related_to_measure", Boolean.class,
			false),
	ALLOW_ILR_ANALYSIS("label.analysis.setting.allow_ilr_analysis", Boolean.class, false,AnalysisType.HYBRID,AnalysisType.QUALITATIVE),
	ALLOW_RISK_HIDDEN_COMMENT("label.analysis.setting.allow_hidden_comment", Boolean.class, true),
	ALLOW_DYNAMIC_ANALYSIS(
			"label.analysis.setting.allow_dynamic_analysis", Boolean.class, false, AnalysisType.QUANTITATIVE,
			AnalysisType.HYBRID),
	ALLOW_QUALITATIVE_IN_QUANTITATIVE_REPORT("label.analysis.setting.allow_qualitative_in_quantitative_report",
			Boolean.class,
			false, AnalysisType.HYBRID);

	/**
	 * The code associated with the analysis setting.
	 */
	private String code;

	/**
	 * The type of the analysis setting.
	 */
	private Class<?> type;

	/**
	 * The default value for the analysis setting.
	 */
	private Object defaultValue;

	/**
	 * The list of analysis types for the analysis setting.
	 */
	private List<AnalysisType> analysisTypes;

	/**
	 * Constructs an AnalysisSetting with the specified code, type, default value, and supported analysis types.
	 *
	 * @param code           the code for the analysis setting
	 * @param type           the type of the analysis setting
	 * @param defaultValue   the default value of the analysis setting
	 * @param analysisTypes  the supported analysis types for the setting
	 */
	private AnalysisSetting(String code, Class<?> type, Object defaultValue, AnalysisType... analysisTypes) {
		this.setCode(code);
		this.setType(type);
		this.setDefaultValue(defaultValue);
		setAnalysisTypes(Arrays.asList(analysisTypes));
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code  the code to set
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
	 * @param type  the type to set
	 *            
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
	 * @param defaultValue the defaultValue to set	 *                     
	 */
	protected void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the analysisType
	 */
	public boolean isSupported(AnalysisType analysisType) {
		return this.analysisTypes == null || this.analysisTypes.isEmpty() || this.analysisTypes.contains(analysisType);
	}

	/**
	 * @param analysisType the analysisTypes to set     
	 */
	protected void setAnalysisTypes(List<AnalysisType> analysisTypes) {
		this.analysisTypes = analysisTypes;
	}

}
