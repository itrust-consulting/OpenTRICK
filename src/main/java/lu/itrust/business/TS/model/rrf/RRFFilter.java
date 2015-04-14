/**
 * 
 */
package lu.itrust.business.TS.model.rrf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class RRFFilter extends ChartFilter {
	
	private List<Integer>  measures = new ArrayList<Integer>();
	
	private List<Integer> scenarios = new ArrayList<Integer>();

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

	/**
	 * @return the scenarios
	 */
	public List<Integer> getScenarios() {
		return scenarios;
	}

	/**
	 * @param scenarios the scenarios to set
	 */
	public void setScenarios(List<Integer> scenarios) {
		this.scenarios = scenarios;
	}

}
