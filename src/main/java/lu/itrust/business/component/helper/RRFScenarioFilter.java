/**
 * 
 */
package lu.itrust.business.component.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class RRFScenarioFilter extends ChartFilter {
	
	private List<Integer>  measures = new ArrayList<Integer>();

	/**
	 * @return the measures
	 */
	public List<Integer> getMeasures() {
		return measures;
	}

	/**
	 * @param measures the measures to set
	 */
	public void setMeasures(List<Integer> measures) {
		this.measures = measures;
	}

}
