/**
 * 
 */
package lu.itrust.business.ts.helper;

import java.util.Collections;
import java.util.List;

import lu.itrust.business.ts.model.parameter.IAcronymParameter;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.standard.measure.Measure;

/**
 * @author eomar
 *
 */
public class ComplianceChartData {

	private String standard;

	private String analysisKey;

	private List<Measure> measures = Collections.emptyList();

	private ValueFactory factory = new ValueFactory(Collections.emptyList());

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
	 * @param analysisKey
	 * @param measures
	 * @param factory
	 */
	public ComplianceChartData(String standard, String analysis, List<Measure> measures, ValueFactory factory) {
		this(standard, analysis);
		this.measures = measures;
		this.factory = factory;
	}

	/**
	 * @param standard
	 * @param analysis
	 * @param measures
	 * @param analysisExpressionParameters
	 *            The expression parameters of the associated analysis.
	 */
	public ComplianceChartData(String standard, String analysis, List<Measure> measures, List<IAcronymParameter> analysisExpressionParameters) {
		this(standard, analysis, measures, new ValueFactory(analysisExpressionParameters));
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
	 * @param analysisKey
	 *            A key which uniquely identifies this chart data in a
	 *            compliance chart that compares the compliance of several
	 *            analyses.
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
	 * @return the factory
	 */
	public ValueFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory
	 *            the factory to set
	 */
	public void setFactory(ValueFactory factory) {
		this.factory = factory;
	}
}
