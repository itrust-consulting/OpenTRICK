package lu.itrust.business.TS.data.actionplan.summary.helper;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.data.standard.measure.Measure;

/** SummaryStandardHelper.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version 
 * @since Aug 25, 2014
 */
public class SummaryStandardHelper {

	public AnalysisStandard standard = null;
	
	public List<Measure> measures = new ArrayList<Measure>();
	
	public double conformance = 0;
	
	public SummaryStandardHelper(AnalysisStandard standard) {
		this.standard = standard;
	}
	
}