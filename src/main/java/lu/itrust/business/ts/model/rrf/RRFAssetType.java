/**
 * 
 */
package lu.itrust.business.ts.model.rrf;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an asset type in the RRF (Risk and Reporting Framework).
 */
public class RRFAssetType {
	
	private String label;
	private List<RRFMeasure> rrfMeasures = new ArrayList<RRFMeasure>();
	
	/**
	 * Default constructor for the RRFAssetType class.
	 */
	public RRFAssetType() {
	}
	
	/**
	 * Constructor for the RRFAssetType class with a label.
	 * 
	 * @param label the label of the asset type
	 */
	public RRFAssetType(String label) {
		this.label = label;
	}

	/**
	 * Returns the label of the asset type.
	 * 
	 * @return the label of the asset type
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label of the asset type.
	 * 
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the list of RRF measures associated with the asset type.
	 * 
	 * @return the list of RRF measures
	 */
	public List<RRFMeasure> getRrfMeasures() {
		return rrfMeasures;
	}

	/**
	 * Sets the list of RRF measures associated with the asset type.
	 * 
	 * @param rrfMeasures the list of RRF measures to set
	 */
	public void setRrfMeasures(List<RRFMeasure> rrfMeasures) {
		this.rrfMeasures = rrfMeasures;
	}

}
