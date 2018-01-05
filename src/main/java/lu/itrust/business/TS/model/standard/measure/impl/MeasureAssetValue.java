package lu.itrust.business.TS.model.standard.measure.impl;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.asset.Asset;

/**
 * AssetTypeValue: <br>
 * This class represents an AssetValue for AssetMeasure (1 measure -> 0..* Assets with asset values)
 * 
 * @author itrust consulting s.à r.l. - SME
 * @version 0.1
 * @since 2014/08/22
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MeasureAssetValue implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** assetTypeValue identifier, unsaved value = -1 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "idMeasureAssetValue")
	private int id = -1;

	/** The Asset */
	@ManyToOne
	@Access(AccessType.FIELD)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAsset", nullable = false)
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE})
	private Asset asset = null;

	/** The Asset Value */
	@Column(name = "dtValue", nullable = false)
	private int value = -1;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public MeasureAssetValue() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param asset
	 * @param value
	 */
	public MeasureAssetValue(Asset asset, int value) {
		this.asset = asset;
		this.value = value;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getAsset: <br>
	 * Description
	 * 
	 * @return
	 */
	public Asset getAsset() {
		return asset;
	}

	/**
	 * setAsset: <br>
	 * Description
	 * 
	 * @param asset
	 * @throws TrickException
	 */
	public void setAsset(Asset asset) throws TrickException {
		if (asset == null || asset.getAssetType() == null)
			throw new TrickException("error.assetvalue.assettype_null", "Asset cannot be empty");
		this.asset = asset;
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
	public MeasureAssetValue clone() throws CloneNotSupportedException {
		MeasureAssetValue assetValue =  (MeasureAssetValue) super.clone();
		assetValue.asset = this.asset.clone();
		return assetValue;
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @throws TrickException
	 * 
	 * @see java.lang.Object#clone()
	 */
	public MeasureAssetValue duplicate() throws CloneNotSupportedException, TrickException {
		MeasureAssetValue assetValue = (MeasureAssetValue) super.clone();
		assetValue.id = -1;
		assetValue.asset.setId(-1);
		return assetValue;
	}
	
	/**
	 * clone: <br>
	 * Description
	 * 
	 * @throws TrickException
	 * 
	 * @see java.lang.Object#clone()
	 */
	public MeasureAssetValue duplicate(Asset asset) throws CloneNotSupportedException {
		MeasureAssetValue assetValue = (MeasureAssetValue) super.clone();
		assetValue.id = -1;
		if(asset!=null)
			assetValue.asset = asset;
		return assetValue;
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
		result = prime * result + ((asset == null) ? 0 : asset.hashCode());
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
		MeasureAssetValue other = (MeasureAssetValue) obj;
		if (asset == null) {
			if (other.asset != null)
				return false;
		} else if (!asset.equals(other.asset))
			return false;
		if (id != other.id && (id != -1 || other.id == -1))
			return false;
		return true;
	}

}