package lu.itrust.business.TS.model.actionplan.summary.helper;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * SummaryStandardHelper.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Aug 25, 2014
 */
public class SummaryStandardHelper {

	public double conformance = 0;

	public int notCompliantMeasureCount = 0;

	public AnalysisStandard standard = null;

	public List<Measure> measures = new ArrayList<Measure>();

	public SummaryStandardHelper(AnalysisStandard standard) {
		this.standard = standard;
	}

}