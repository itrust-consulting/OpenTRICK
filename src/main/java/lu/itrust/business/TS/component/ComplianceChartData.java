/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.Collections;
import java.util.List;

import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * @author eomar
 *
 */
public class ComplianceChartData {

	private String standard;

	private String analysis;

	private List<Measure> measures = Collections.emptyList();

	/**
	 * 
	 */
	public ComplianceChartData() {
	}

	/**
	 * @param standard
	 * @param analysis
	 */
	public ComplianceChartData(String standard, String analysis) {
		this.standard = standard;
		this.analysis = analysis;
	}

	/**
	 * @param standard
	 * @param analysis
	 * @param measures
	 */
	public ComplianceChartData(String standard, String analysis, List<Measure> measures) {
		this(standard, analysis);
		this.measures = measures;
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
	public String getAnalysis() {
		return analysis;
	}

	/**
	 * @param analysis
	 *            the analysis to set
	 */
	public void setAnalysis(String analysis) {
		this.analysis = analysis;
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
}
