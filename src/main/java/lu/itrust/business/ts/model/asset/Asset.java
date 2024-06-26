package lu.itrust.business.ts.model.asset;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lu.itrust.business.ts.exception.TrickException;

/**
 * Asset: <br>
 * This class represents an asset and all its data
 * 
 * This class is used to store assets. Assets are also stored in assessments
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "dtLabel" }))
public class Asset implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** Asset Identifier */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAsset")
	private int id = 0;

	/** The Asset Name */
	@Column(name = "dtLabel", nullable = false)
	private String name = "";

	/** The Asset Type Name */
	@ManyToOne
	@JoinColumn(name = "fiAssetType", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Access(AccessType.FIELD)
	private AssetType assetType = null;

	/** The Asset Value */
	@Column(name = "dtValue", nullable = false)
	private double value = 0;

	/** The Asset Comment */
	@Column(name = "dtComment", nullable = false, length = 16777216)
	private String comment = "";

	/** The Asset Hidden Comment */
	@Column(name = "dtHiddenComment", nullable = false, length = 16777216)
	private String hiddenComment = "";

	/** The Asset related name */
	@Column(name = "dtRelatedName", nullable = false, length = 16777216)
	private String relatedName = "";

	/** The Flag to determine if the Asset is selected for calculations */
	@Column(name = "dtSelected", nullable = false)
	private boolean selected = false;

	/** The Annual Loss Expectancy - Pessimistic */
	@Column(name = "dtALEP", nullable = false)
	private double ALEP = 0;

	/** The Annual Loss Expectancy - Normal */
	@Column(name = "dtALE", nullable = false)
	private double ALE = 0;

	/** The Annual Loss Expectancy - Optimistic */
	@Column(name = "dtALEO", nullable = false)
	private double ALEO = 0;

	public Asset() {
	}

	public Asset(String name) {
		setName(name);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getId: <br>
	 * Returns the "id" field of the asset (Asset ID)
	 * 
	 * @return The Asset ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the "id" field of the asset with a value
	 * 
	 * @param id The value to set the "id" field
	 * @throws TrickException
	 */
	public void setId(int id) throws TrickException {
		if (id < 1)
			throw new TrickException("error.asset.invalid.id", "Asset id should be greater than 0");
		this.id = id;
	}

	/**
	 * getName: <br>
	 * Returns the "name" field of the asset (Asset Name)
	 * 
	 * @return The Name of the asset
	 */
	public String getName() {
		return name;
	}

	/**
	 * setName: <br>
	 * Sets the "name" field of the asset with a value
	 * 
	 * @param name The value to set the Asset "name" field
	 * @throws TrickException
	 */
	public void setName(String name) throws TrickException {
		if (name == null || name.trim().isEmpty())
			throw new TrickException("error.asset.label_null", "Asset name cannot be empty");
		this.name = name.trim();
	}

	/**
	 * getType: <br>
	 * Returns "assetType" field of the asset
	 * 
	 * @return The Asset Type Name
	 */
	public AssetType getAssetType() {
		return assetType;
	}

	/**
	 * setType: <br>
	 * Sets the "assetType" field of the asset with a value
	 * 
	 * @param assetType The value to set the Asset "assetType" field
	 * @throws TrickException
	 */
	public void setAssetType(AssetType assetType) throws TrickException {
		if (assetType == null || assetType.getName() == null || assetType.getName().trim().isEmpty())
			throw new TrickException("error.asset.assettype_null", "Asset type cannot be empty");
		this.assetType = assetType;
	}

	/**
	 * getValue: <br>
	 * Returns "value" field of the asset
	 * 
	 * @return The Asset Value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * setValue: <br>
	 * Sets the "value" field of the asset with a value
	 * 
	 * @param value The value to set the Asset "value" field
	 * @throws TrickException
	 */
	public void setValue(double value) throws TrickException {
		if (value < 0)
			throw new TrickException("error.asset.value", "Asset value cannot be negative");
		this.value = value;
	}

	/**
	 * getComment: <br>
	 * Returns "comment" field of the asset
	 * 
	 * @return The Comment about the asset
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * setComment: <br>
	 * Sets the "comment" field of the asset with a value
	 * 
	 * @param comment The value to set the "comment" field
	 */
	public void setComment(String comment) {
		if (comment == null)
			this.comment = "";
		else
			this.comment = comment;
	}

	/**
	 * getHiddenComment: <br>
	 * Returns the hiddenComment field value.
	 * 
	 * @return The value of the hiddenComment field
	 */
	public String getHiddenComment() {
		return hiddenComment;
	}

	/**
	 * setHiddenComment: <br>
	 * Sets the Field "hiddenComment" with a value.
	 * 
	 * @param hideComment The Value to set the hiddenComment field
	 */
	public void setHiddenComment(String hiddenComment) {
		if (hiddenComment == null)
			this.hiddenComment = "";
		else
			this.hiddenComment = hiddenComment;
	}

	/**
	 * Related name
	 * @return
	 */
	public String getRelatedName() {
		return relatedName;
	}

	/**
	 * Related name
	 * @param relatedName
	 */
	public void setRelatedName(String relatedName) {
		this.relatedName = (relatedName == null? "": relatedName).trim();
	}

	/**
	 * isSelected: <br>
	 * Returns "selected" field of the asset
	 * 
	 * @return The selected flag
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * setSelected: <br>
	 * Sets the "selected" field of the asset with a value
	 * 
	 * @param selected The value to set the "selected" field
	 * @throws InvalidAttributesException if others fields are not initialized yet
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * hashCode: <br>
	 * Used inside equals method. <br>
	 * <br>
	 * <b>NOTE:</b> This Method is auto generated
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((assetType == null) ? 0 : assetType.hashCode());
		return result;
	}

	/**
	 * equals: This method checks if an object Asset equals another object Asset.
	 * Fields taken in concideration: ID, name, assetType.<br>
	 * <br>
	 * <b>NOTE:</b> This Method is auto generated
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Asset)) {
			return false;
		}
		Asset other = (Asset) obj;
		if (getId() > 0 && other.getId() > 0)
			return getId() == other.getId();
		if (getName() == null && other.getName() != null)
			return false;
		return name.equalsIgnoreCase(other.name);
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Asset clone() {
		try {
			return (Asset) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new TrickException("error.clone.asset", "Asset cannot be copied");
		}
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Asset duplicate() {
		try {
			Asset asset = (Asset) super.clone();
			asset.id = 0;
			return asset;
		} catch (CloneNotSupportedException e) {
			throw new TrickException("error.clone.asset", "Asset cannot be copied");
		}
	}

	/**
	 * getALEP: <br>
	 * Description
	 * 
	 * @return
	 */
	public double getALEP() {
		return ALEP;
	}

	/**
	 * setALEP: <br>
	 * Description
	 * 
	 * @param aLEP
	 */
	public void setALEP(double aLEP) {
		ALEP = aLEP;
	}

	/**
	 * getALE: <br>
	 * Description
	 * 
	 * @return
	 */
	public double getALE() {
		return ALE;
	}

	/**
	 * setALE: <br>
	 * Description
	 * 
	 * @param aLE
	 */
	public void setALE(double aLE) {
		ALE = aLE;
	}

	/**
	 * getALEO: <br>
	 * Description
	 * 
	 * @return
	 */
	public double getALEO() {
		return ALEO;
	}

	/**
	 * setALEO: <br>
	 * Description
	 * 
	 * @param aLEO
	 */
	public void setALEO(double aLEO) {
		ALEO = aLEO;
	}

	/**
	 * toString: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Asset [id=" + id + ", name=" + name + ", assetType=" + assetType + ", value=" + value + ", comment="
				+ comment + ", hiddenComment=" + hiddenComment + ", selected="
				+ selected + ", ALEP=" + ALEP + ", ALE=" + ALE + ", ALEO=" + ALEO + "]";
	}
}
