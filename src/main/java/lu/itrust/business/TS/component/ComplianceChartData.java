/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.Collections;
import java.util.List;

import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * @author eomar
 *
 */
public class ComplianceChartData {

	private String standard;

	private String analysisKey;

	private List<Measure> measures = Collections.emptyList();

	private List<AcronymParameter> analysisExpressionParameters = Collections.emptyList();

	/**
	 * 
	 */
	public ComplianceChartData() {
	}

	/**
	 * @param standard
	 * @param analysisKey
	 */
	public ComplianceChartData(String standard, String analysisKey) {
		this.standard = standard;
		this.analysisKey = analysisKey;
	}

	/**
	 * @param standard
	 * @param analysis
	 * @param measures
	 * @param analysisExpressionParameters The expression parameters of the associated analysis.
	 */
	public ComplianceChartData(String standard, String analysis, List<Measure> measures, List<AcronymParameter> analysisExpressionParameters) {
		this(standard, analysis);
		this.measures = measures;
		this.analysisExpressionParameters = analysisExpressionParameters;
	}

	/**
	 * @return the standard
	 */
	public String getStandard() {
		return standard;
	}

	/**
	 * @param standard
	 *            the standard to set
	 */
	public void setStandard(String standard) {
		this.standard = standard;
	}

	/**
	 * @return the analysis
	 */
	public String getAnalysisKey() {
		return analysisKey;
	}

	/**
	 * @param analysisKey A key which uniquely identifies this chart data in a compliance chart that compares the compliance of several analyses.
	 */
	public void setAnalysisKey(String analysisKey) {
		this.analysisKey = analysisKey;
	}

	/**
	 * @return the measures
	 */
	public List<Measure> getMeasures() {
		return measures;
	}

	/**
	 * @param measures
	 *            the measures to set
	 */
	public void setMeasures(List<Measure> measures) {
		this.measures = measures;
	}

	/**
	 * Gets the expression parameters of the associated analysis. Used to evaluate the implementation rate. 
	 */
	public List<AcronymParameter> getAnalysisExpressionParameters() {
		return analysisExpressionParameters;
	}

	/**
	 * Sets the expression parameters of the associated analysis. Used to evaluate the implementation rate. 
	 */
	public void setAnalysisExpressionParameters(List<AcronymParameter> analysisExpressionParameters) {
		this.analysisExpressionParameters = analysisExpressionParameters;
	}
}
