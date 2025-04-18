/**
 * 
 */
package lu.itrust.business.ts.model.rrf;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a filter for the RRFFilter class.
 * Extends the ChartFilter class.
 */
public class RRFFilter extends ChartFilter {
	
	private List<Integer> measures = new ArrayList<Integer>();
	
	private List<Integer> scenarios = new ArrayList<Integer>();

	/**
	 * Get the list of measures.
	 * 
	 * @return the measures
	 */
	public List<Integer> getMeasures() {
		return measures;
	}

	/**
	 * Set the list of measures.
	 * 
	 * @param measures the measures to set
	 */
	public void setMeasures(List<Integer> measures) {
		this.measures = measures;
	}

	/**
	 * Get the list of scenarios.
	 * 
	 * @return the scenarios
	 */
	public List<Integer> getScenarios() {
		return scenarios;
	}

	/**
	 * Set the list of scenarios.
	 * 
	 * @param scenarios the scenarios to set
	 */
	public void setScenarios(List<Integer> scenarios) {
		this.scenarios = scenarios;
	}

}
