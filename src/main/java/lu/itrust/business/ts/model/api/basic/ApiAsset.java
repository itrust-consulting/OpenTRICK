/**
 * 
 */
package lu.itrust.business.ts.model.api.basic;

import lu.itrust.business.ts.model.asset.Asset;

/**
 * @author eomar
 *
 */
public class ApiAsset extends ApiNamable {

	private double value;
	private Integer assetTypeId;
	private String assetTypeName;
	private boolean selected;
	
	/**
	 * 
	 */
	public ApiAsset() {
	}

	/**
	 * @param id
	 * @param name
	 * @param value
	 */
	public ApiAsset(Integer id, String name, Integer assetTypeId, String assetTypeName, double value, boolean selected) {
		super(id, name);
		this.value = value;
		this.assetTypeId = assetTypeId;
		this.assetTypeName = assetTypeName;
		this.selected = selected;
	}

	public static ApiAsset create(Asset asset) {
		return new ApiAsset(asset.getId(), asset.getName(), asset.getAssetType().getId(), asset.getAssetType().getName(), asset.getValue(), asset.isSelected());
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

	public Integer getAssetTypeId() {
		return assetTypeId;
	}

	public void setAssetTypeId(Integer assetTypeId) {
		this.assetTypeId = assetTypeId;
	}

	public String getAssetTypeName() {
		return assetTypeName;
	}

	public void setAssetTypeName(String assetTypeName) {
		this.assetTypeName = assetTypeName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
