/**
 * 
 */
package lu.itrust.business.component.helper;

/**
 * @author eom
 *
 */
public class ALE {
	
	private String assetName;
	
	private double value;
	
	/**
	 * @param assetName
	 * @param value
	 */
	public ALE(String assetName, double value) {
		this.assetName = assetName;
		this.value = value;
	}

	/**
	 * @return the assetName
	 */
	public String getAssetName() {
		return assetName;
	}

	/**
	 * @param assetName the assetName to set
	 */
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

}
