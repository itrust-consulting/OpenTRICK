/**
 * 
 */
package lu.itrust.business.TS.data.rrf;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author eomar
 *
 */
public class RRFAsset {
	
	private String label;
	
	private List<RRFMeasure> rrfMeasures = new ArrayList<RRFMeasure>();
	
	/**
	 * 
	 */
	public RRFAsset() {
	}
	
	/**
	 * @param label
	 */
	public RRFAsset(String label) {
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the rrfMeasures
	 */
	public List<RRFMeasure> getRrfMeasures() {
		return rrfMeasures;
	}

	/**
	 * @param rrfMeasures the rrfMeasures to set
	 */
	public void setRrfMeasures(List<RRFMeasure> rrfMeasures) {
		this.rrfMeasures = rrfMeasures;
	}

}
