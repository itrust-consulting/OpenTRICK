/**
 * 
 */
package lu.itrust.business.ts.form;

import java.util.List;

/**
 * @author eomar
 *
 */
public class ImportRRFForm {

	private int analysis;
	
	private List<Integer> standards;
	
	/**
	 * 
	 */
	public ImportRRFForm() {
	}

	public int getAnalysis() {
		return analysis;
	}

	public void setAnalysis(int analysis) {
		this.analysis = analysis;
	}

	public List<Integer> getStandards() {
		return standards;
	}

	public void setStandards(List<Integer> standards) {
		this.standards = standards;
	}
}
