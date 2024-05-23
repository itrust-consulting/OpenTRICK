package lu.itrust.business.ts.model.actionplan.summary.helper;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.Measure;


/**
 * The SummaryStandardHelper class represents a helper class for summary standard operations.
 */
public class SummaryStandardHelper {

	public double conformance = 0;

	public int notCompliantMeasureCount = 0;

	public AnalysisStandard standard = null;

	public List<Measure> measures = new ArrayList<Measure>();

	/**
	 * Constructs a SummaryStandardHelper object with the specified analysis standard.
	 *
	 * @param standard the analysis standard to be associated with the helper
	 */
	public SummaryStandardHelper(AnalysisStandard standard) {
		this.standard = standard;
	}

}