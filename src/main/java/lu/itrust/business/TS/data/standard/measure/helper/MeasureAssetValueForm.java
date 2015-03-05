/**
 * 
 */
package lu.itrust.business.TS.data.standard.measure.helper;

import lu.itrust.business.TS.data.standard.measure.MeasureAssetValue;

/**
 * @author eomar
 *
 */
public class MeasureAssetValueForm {
	
	private int id;
	
	private String name;
	
	private String type;
	
	private int value;

	/**
	 * 
	 */
	public MeasureAssetValueForm() {
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 */
	public MeasureAssetValueForm(int id, String name, String type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param value
	 */
	public MeasureAssetValueForm(int id, String name, String type, int value) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public MeasureAssetValueForm(MeasureAssetValue assetValue) {
		this.id = assetValue.getAsset().getId();
		this.name = assetValue.getAsset().getName();
		this.type = assetValue.getAsset().getAssetType().getType();
		this.value = assetValue.getValue();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "MeasureAssetValueForm [id=" + id + ", name=" + name + ", type=" + type + ", value=" + value + "]";
	}
}
