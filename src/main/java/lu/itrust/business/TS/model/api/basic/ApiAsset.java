/**
 * 
 */
package lu.itrust.business.TS.model.api.basic;

/**
 * @author eomar
 *
 */
public class ApiAsset extends ApiNamable {

	private double value;
	private Integer assetTypeId;
	private String assetTypeName;
	
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
	public ApiAsset(Integer id, String name, Integer assetTypeId, String assetTypeName, double value) {
		super(id, name);
		this.value = value;
		this.assetTypeId = assetTypeId;
		this.assetTypeName = assetTypeName;
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

}
