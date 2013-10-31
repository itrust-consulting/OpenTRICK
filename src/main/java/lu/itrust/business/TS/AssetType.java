package lu.itrust.business.TS;

import lu.itrust.business.TS.tsconstant.Constant;

/**
 * AssetType: <br>
 * Represents the Asset type of an Asset.
 * 
 * @author itrust consulting s.à r.l. : OEM, BJA, SME
 * @version 0.1
 * @since 25 janv. 2013
 */
public class AssetType {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** AssetType id, unsaved value = -1 */
	private int id = -1;

	/** AssetType Type name */
	private String type = "";

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
	 * @param type
	 *            Type Name
	 */
	public AssetType(String type) {
		setType(type);
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
	 *            The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * getType: <br>
	 * Returns the type field value.
	 * 
	 * @return The value of the type field
	 */
	public String getType() {
		return type;
	}

	/**
	 * setType: <br>
	 * Sets the Field "type" with a value.
	 * 
	 * @param type
	 *            The Value to set the type field
	 */
	public void setType(String type) {
		if (type == null)
			throw new IllegalArgumentException("error.assettype.type_null");
		else if (!type.matches(Constant.REGEXP_VALID_ASSET_TYPE))
			throw new IllegalArgumentException("error.assettype.type_no_meet");
		this.type = type;
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
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
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (getType() == null) {
			if (other.getType() != null)
				return false;
		} else if (!getType().equals(other.getType())) {
			return false;
		}
		return true;
	}
}