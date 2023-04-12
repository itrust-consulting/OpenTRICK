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
 * @author itrust consulting s.à r.l. : OEM, BJA, SME
 * @version 0.1
 * @since 25 janv. 2013
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
	 * Constructor:<br>
	 */
	public AssetType() {
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param name
	 *             Type Name
	 * @throws TrickException
	 */
	public AssetType(String type) throws TrickException {
		setName(type);
	}

	public AssetType(int id, String name) {
		setId(id);
		setName(name);
	}
	

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	

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
	 *           The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * getType: <br>
	 * Returns the name field value.
	 * 
	 * @return The value of the name field
	 */
	public String getName() {
		return name;
	}

	/**
	 * setType: <br>
	 * Sets the Field "name" with a value.
	 * 
	 * @param name
	 *             The Value to set the name field
	 * @throws TrickException
	 */
	public void setName(String name) throws TrickException {
		if (name == null)
			throw new TrickException("error.assettype.type_null", "Asset name cannot be empty");
		else if (!name.trim().matches(Constant.REGEXP_VALID_ASSET_TYPE))
			throw new TrickException("error.assettype.type_no_meet", "Asset name: wrong name");
		this.name = name.trim();
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public AssetType clone() throws CloneNotSupportedException {
		return (AssetType) super.clone();
	}

	/**
	 * hashCode: <br>
	 * Description
	 * 
	 * @see java.lang.Object#hashCode()
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
	 * equals: <br>
	 * Description
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
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

	public boolean isSame(String name) {
		return this.name == null ? name == null : this.name.equalsIgnoreCase(name);
	}
}