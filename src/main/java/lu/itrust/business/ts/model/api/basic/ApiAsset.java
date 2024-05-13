/**
 * 
 */
package lu.itrust.business.ts.model.api.basic;

import lu.itrust.business.ts.model.asset.Asset;

/**
 * Represents an API asset.
 */
public class ApiAsset extends ApiNamable {

	private double value;
	private Integer assetTypeId;
	private String assetTypeName;
	private boolean selected;

	/**
	 * Default constructor for ApiAsset.
	 */
	public ApiAsset() {
	}

	/**
	 * Constructor for ApiAsset with specified parameters.
	 *
	 * @param id            the ID of the asset
	 * @param name          the name of the asset
	 * @param assetTypeId   the ID of the asset type
	 * @param assetTypeName the name of the asset type
	 * @param value         the value of the asset
	 * @param selected      indicates if the asset is selected
	 */
	public ApiAsset(Integer id, String name, Integer assetTypeId, String assetTypeName, double value, boolean selected) {
		super(id, name);
		this.value = value;
		this.assetTypeId = assetTypeId;
		this.assetTypeName = assetTypeName;
		this.selected = selected;
	}

	/**
	 * Creates an instance of ApiAsset from an Asset object.
	 *
	 * @param asset the Asset object to create ApiAsset from
	 * @return the created ApiAsset object
	 */
	public static ApiAsset create(Asset asset) {
		return new ApiAsset(asset.getId(), asset.getName(), asset.getAssetType().getId(), asset.getAssetType().getName(), asset.getValue(), asset.isSelected());
	}

	/**
	 * Gets the value of the asset.
	 *
	 * @return the value of the asset
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value of the asset.
	 *
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Gets the ID of the asset type.
	 *
	 * @return the ID of the asset type
	 */
	public Integer getAssetTypeId() {
		return assetTypeId;
	}

	/**
	 * Sets the ID of the asset type.
	 *
	 * @param assetTypeId the ID to set
	 */
	public void setAssetTypeId(Integer assetTypeId) {
		this.assetTypeId = assetTypeId;
	}

	/**
	 * Gets the name of the asset type.
	 *
	 * @return the name of the asset type
	 */
	public String getAssetTypeName() {
		return assetTypeName;
	}

	/**
	 * Sets the name of the asset type.
	 *
	 * @param assetTypeName the name to set
	 */
	public void setAssetTypeName(String assetTypeName) {
		this.assetTypeName = assetTypeName;
	}

	/**
	 * Checks if the asset is selected.
	 *
	 * @return true if the asset is selected, false otherwise
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the selected status of the asset.
	 *
	 * @param selected the selected status to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
