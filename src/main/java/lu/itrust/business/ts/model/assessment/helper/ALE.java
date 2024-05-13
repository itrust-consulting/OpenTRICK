/**
 * 
 */
package lu.itrust.business.ts.model.assessment.helper;

import java.util.Comparator;


/**
 * The ALE (Annual Loss Expectancy) class represents an asset's name and its corresponding value.
 */
public class ALE {

	private String assetName;

	private double value;

	/**
	 * Constructs a new ALE object with the specified asset name and value.
	 *
	 * @param assetName the name of the asset
	 * @param value the value of the asset
	 */
	public ALE(String assetName, double value) {
		this.assetName = assetName;
		this.value = value;
	}

	/**
	 * Returns the name of the asset.
	 *
	 * @return the asset name
	 */
	public String getAssetName() {
		return assetName;
	}

	/**
	 * Sets the name of the asset.
	 *
	 * @param assetName the asset name to set
	 */
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	/**
	 * Returns the value of the asset.
	 *
	 * @return the asset value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value of the asset.
	 *
	 * @param value the asset value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Returns a comparator for comparing ALE objects based on their values and asset names.
	 *
	 * @return a comparator for ALE objects
	 */
	public static Comparator<? super ALE> Comparator() {
		return (E1, E2) -> {
			int result = Double.compare(E1.getValue(), E2.getValue());
			return result == 0 ? E1.assetName.compareToIgnoreCase(E2.assetName) : result;
		};
	}

}
