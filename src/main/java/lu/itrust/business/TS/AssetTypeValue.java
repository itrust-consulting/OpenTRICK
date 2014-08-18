package lu.itrust.business.TS;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lu.itrust.business.exception.TrickException;

/**
 * AssetTypeValue: <br>
 * This class represents an AssetTypeValue and all its data. This class is used
 * to store AssetTypeValues of either Scenarios or Measures.
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
@Entity 
public class AssetTypeValue implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** assetTypeValue identifier, unsaved value = -1 */
	@Id @GeneratedValue 
	@Column(name="idAssetTypeValue")
	private int id = -1;

	/** Name of the Asset Type */
	@ManyToOne 
	@Column(name="fiAssetType")
	private AssetType assetType = null;

	/** The Asset Type Value */
	@Column(name="dtValue")
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
	public AssetTypeValue(AssetType assetType, int value) {
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
	 * @throws TrickException
	 */
	public void setAssetType(AssetType assetType) throws TrickException {
		if (assetType == null || assetType.getType() == null)
			throw new TrickException("error.assettypevalue.assettype_null", "Asset type value cannot be empty");
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
	 * @throws TrickException
	 */
	public void setValue(int value) throws TrickException {
		if ((value < -1) || (value > 101))
			throw new TrickException("error.asset.assettypevalue.value", "Asset type value: value should be between 0 and 100");
		this.value = value;
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public AssetTypeValue clone() throws CloneNotSupportedException {
		return (AssetTypeValue) super.clone();
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	public AssetTypeValue duplicate() throws CloneNotSupportedException {
		AssetTypeValue assetTypeValue = (AssetTypeValue) super.clone();
		assetTypeValue.id = -1;
		return assetTypeValue;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assetType == null) ? 0 : assetType.hashCode());
		result = prime * result + id;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssetTypeValue other = (AssetTypeValue) obj;
		if (assetType == null) {
			if (other.assetType != null)
				return false;
		} else if (!assetType.equals(other.assetType))
			return false;
		if (id != other.id && (id != -1 || other.id == -1))
			return false;
		return true;
	}

}