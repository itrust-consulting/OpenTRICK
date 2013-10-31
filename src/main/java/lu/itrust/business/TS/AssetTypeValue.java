package lu.itrust.business.TS;

/**
 * AssetTypeValue: <br>
 * This class represents an AssetTypeValue and all its data. This class is used to store
 * AssetTypeValues of either Scenarios or Measures.
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
public class AssetTypeValue {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** assetTypeValue identifier, unsaved value = -1 */
	private int id = -1;
	
	/** Name of the Asset Type */
	private AssetType assetType = null;

	/** The Asset Type Value */
	private int value = -1;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public AssetTypeValue() {
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param assetType
	 *            The Asset Type Object
	 * @param value
	 *            The Value to set
	 */
	public AssetTypeValue( AssetType assetType, int value) {
		this.assetType = assetType;
		this.value = value;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getAssetType: <br>
	 * Returns the "assetType" field value
	 * 
	 * @return The Name of the Asset Type
	 */
	public AssetType getAssetType() {
		return assetType;
	}

	/**
	 * setAssetType: <br>
	 * Sets the "assetType" field with a value
	 * 
	 * @param assetType
	 *            The value to set the "assetType" field
	 */
	public void setAssetType(AssetType assetType) {
		if (assetType == null)
			throw new IllegalArgumentException(
					"error.assettypevalue.assettype_null");
		else if (assetType.getType() == null)
			throw new IllegalArgumentException(
					"error.asset.assettypevalue.type_null");
		else if (assetType.getType().trim().isEmpty())
			throw new IllegalArgumentException(
					"error.asset.assettypevalue.type_empty");
		this.assetType = assetType;
	}

	/**
	 * getValue: <br>
	 * Returns the "value" field value
	 * 
	 * @return The Asset Type Value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * setValue: <br>
	 * Sets the "value" field with a value
	 * 
	 * @param value
	 *            The value to set the "value" field
	 */
	public void setValue(int value) {
		if ((value < -1) || (value > 101)) {
			throw new IllegalArgumentException("error.asset.assettypevalue.value");
		}
		this.value = value;
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		AssetTypeValue assetTypeValue = (AssetTypeValue) super.clone();
		assetTypeValue.assetType = (AssetType) assetType.clone();
		return super.clone();
	}

	/**
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id
	 *            The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}
}