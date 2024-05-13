package lu.itrust.business.ts.model.asset;

import java.io.Serializable;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;

/**
 * AssetType: <br>
 * Represents the Asset name of an Asset.
 * 
 */

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AssetType implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** AssetType id, unsaved value = -1 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAssetType")
	private int id = 0;

	/** AssetType Type name */
	@Column(name = "dtLabel", nullable = false, unique = true)
	private String name = "";

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Default constructor.
	 */
	public AssetType() {
	}

	/**
	 * Constructor with name parameter.
	 *
	 * @param name The type name of the asset.
	 * @throws TrickException If the asset name is null or does not meet the required format.
	 */
	public AssetType(String name) throws TrickException {
		setName(name);
	}

	/**
	 * Constructor with id and name parameters.
	 *
	 * @param id   The id of the asset type.
	 * @param name The type name of the asset.
	 */
	public AssetType(int id, String name) {
		setId(id);
		setName(name);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * Returns the id of the asset type.
	 *
	 * @return The id of the asset type.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of the asset type.
	 *
	 * @param id The id to set for the asset type.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the name of the asset type.
	 *
	 * @return The name of the asset type.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the asset type.
	 *
	 * @param name The name to set for the asset type.
	 * @throws TrickException If the asset name is null or does not meet the required format.
	 */
	public void setName(String name) throws TrickException {
		if (name == null)
			throw new TrickException("error.assettype.type_null", "Asset name cannot be empty");
		else if (!name.trim().matches(Constant.REGEXP_VALID_ASSET_TYPE))
			throw new TrickException("error.assettype.type_no_meet", "Asset name: wrong name");
		this.name = name.trim();
	}

	/**
	 * Clones the asset type.
	 *
	 * @return A cloned instance of the asset type.
	 * @throws CloneNotSupportedException If cloning is not supported for the asset type.
	 */
	@Override
	public AssetType clone() throws CloneNotSupportedException {
		return (AssetType) super.clone();
	}

	/**
	 * Generates the hash code for the asset type.
	 *
	 * @return The hash code for the asset type.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Checks if the asset type is equal to another object.
	 *
	 * @param obj The object to compare with.
	 * @return True if the asset type is equal to the other object, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AssetType))
			return false;
		AssetType other = (AssetType) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if the asset type has the same name as the given name.
	 *
	 * @param name The name to compare with.
	 * @return True if the asset type has the same name as the given name, false otherwise.
	 */
	public boolean isSame(String name) {
		return this.name == null ? name == null : this.name.equalsIgnoreCase(name);
	}
}