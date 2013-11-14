package lu.itrust.business.TS;

import javax.naming.directory.InvalidAttributesException;

/**
 * Asset: <br>
 * This class represents an asset and all its data
 * 
 * This class is used to store assets. Assets are also stored in assessments
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
public class Asset {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** Asset Identifier */
	private int id = -1;

	/** The Asset Name */
	private String name = "";

	/** The Asset Type Name */
	private AssetType assetType = null;

	/** The Asset Value */
	private double value = 0;

	/** The Asset Comment */
	private String comment = "";

	/** The Asset Hidden Comment */
	private String hiddenComment = "";

	/** The Flag to determine if the Asset is selected for calculations */
	private boolean selected = false;

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
	 * @param id
	 *            The value to set the "id" field
	 */
	public void setId(int id) {
		if (id < 1) {
			throw new IllegalArgumentException("error.asset.invalid.id");
		}
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
	 * @param name
	 *            The value to set the Asset "name" field
	 */
	public void setName(String name) {
		if (name == null)
			throw new IllegalArgumentException("error.asset.label_null");
		else if (name.trim().isEmpty())
			throw new IllegalArgumentException("error.asset.label_empty");
		this.name = name;
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
	 * @param assetType
	 *            The value to set the Asset "assetType" field
	 */
	public void setAssetType(AssetType assetType) {
		if (assetType == null)
			throw new IllegalArgumentException("error.asset.assettype_null");
		else if (assetType.getType() == null)
			throw new IllegalArgumentException(
					"error.asset.assettype.type_null");
		else if (assetType.getType().trim().isEmpty())
			throw new IllegalArgumentException(
					"error.asset.assettype.type_empty");
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
	 * @param value
	 *            The value to set the Asset "value" field
	 */
	public void setValue(double value) {
		if (value < 0) {
			throw new IllegalArgumentException("error.asset.value");
		}
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
	 * @param comment
	 *            The value to set the "comment" field
	 */
	public void setComment(String comment) {
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
	 * @param hideComment
	 *            The Value to set the hiddenComment field
	 */
	public void setHiddenComment(String hiddenComment) {
		this.hiddenComment = hiddenComment;
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
	 * @param selected
	 *            The value to set the "selected" field
	 * @throws InvalidAttributesException
	 *             if others fields are not initialized yet
	 */
	public void setSelected(boolean selected) throws InvalidAttributesException {
		if (name.trim().isEmpty())
			throw new InvalidAttributesException("error.asset.selected.name");
		if (value < 0)
			throw new InvalidAttributesException("error.asset.selected.value");
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
		result = prime * result
				+ ((assetType == null) ? 0 : assetType.hashCode());
		return result;
	}

	/**
	 * equals: This method checks if an object Asset equals another object
	 * Asset. Fields taken in concideration: ID, name, assetType.<br>
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
		if (getId() != other.getId()) {
			if (getId() > 0 && other.getId() > 0) {

				System.out.println(getId() + " - " + other.getId());
				return false;
			}
		}
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		}
		return name.equals(other.name);
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
}