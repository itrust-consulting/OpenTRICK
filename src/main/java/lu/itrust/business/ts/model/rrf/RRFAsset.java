/**
 * 
 */
package lu.itrust.business.ts.model.rrf;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an RRF (Risk, Response, and Feedback) asset.
 */
public class RRFAsset {
	
	private String label;
	private List<RRFMeasure> rrfMeasures = new ArrayList<RRFMeasure>();
	
	/**
	 * Constructs a new RRFAsset object.
	 */
	public RRFAsset() {
	}
	
	/**
	 * Constructs a new RRFAsset object with the specified label.
	 * 
	 * @param label the label of the RRFAsset
	 */
	public RRFAsset(String label) {
		this.label = label;
	}

	/**
	 * Returns the label of the RRFAsset.
	 * 
	 * @return the label of the RRFAsset
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label of the RRFAsset.
	 * 
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the list of RRFMeasures associated with the RRFAsset.
	 * 
	 * @return the list of RRFMeasures
	 */
	public List<RRFMeasure> getRrfMeasures() {
		return rrfMeasures;
	}

	/**
	 * Sets the list of RRFMeasures associated with the RRFAsset.
	 * 
	 * @param rrfMeasures the list of RRFMeasures to set
	 */
	public void setRrfMeasures(List<RRFMeasure> rrfMeasures) {
		this.rrfMeasures = rrfMeasures;
	}

}
