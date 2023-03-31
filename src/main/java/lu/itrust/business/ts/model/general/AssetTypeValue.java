package lu.itrust.business.ts.model.general;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.asset.AssetType;

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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAssetTypeValue")
	private int id = 0;

	/** Name of the Asset Type */
	@ManyToOne
	@JoinColumn(name = "fiAssetType", nullable = false)
	@Access(AccessType.FIELD)
	private AssetType assetType = null;

	/** The Asset Type Value */
	@Column(name = "dtValue", nullable = false)
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
	 * @throws TrickException
	 */
	public AssetTypeValue(AssetType assetType, int value) throws TrickException {
		setAssetType(assetType);
		setValue(value);
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
		if (assetType == null || assetType.getName() == null)
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
	public AssetTypeValue clone() {
		try {
			return (AssetTypeValue) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new TrickException("error.clone.data", "Data cannot be copied");
		}

	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @throws TrickException
	 * 
	 * @see java.lang.Object#clone()
	 */
	public AssetTypeValue duplicate() {
		try {
			AssetTypeValue assetTypeValue = (AssetTypeValue) super.clone();
			assetTypeValue.id = 0;
			return assetTypeValue;
		} catch (CloneNotSupportedException e) {
			throw new TrickException("error.clone.data", "Data cannot be copied");
		}
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

	public boolean hasSameType(String type) {
		return assetType == null ? (type == null ? true : false) : assetType.isSame(type);
	}

}