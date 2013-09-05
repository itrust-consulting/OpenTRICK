package lu.itrust.business.TS;

import lu.itrust.business.TS.tsconstant.Constant;

/**
 * ScenarioType: <br>
 * Represents the Scenario Type with a Name.
 * 
 * @author itrust consulting s.à .r.l. : EOM, BJA, SME
 * @version 0.1
 * @since 23 janv. 2013
 */
public class ScenarioType {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** id unsaved value = -1 */
	private int id = -1;

	/** scenario type name */
	private String type = "";

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public ScenarioType() {
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param type
	 *            The Scenario Type Name
	 */
	public ScenarioType(String type) {
		System.out.println(type);
		if (type == null || !type.matches(Constant.REGEXP_VALID_SCENARIO_TYPE)) 
			throw new IllegalArgumentException("Scenario Type is not valid!");
		this.type = type;
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
	public String getTypeName() {
		return type;
	}

	/**
	 * setType: <br>
	 * Sets the Field "type" with a value.
	 * 
	 * @param type
	 *            The Value to set the type field
	 */
	public void setTypeName(String type) {
		if (type == null || !type.matches(Constant.REGEXP_VALID_SCENARIO_TYPE)) 
			throw new IllegalArgumentException("Scenario Type is not valid!");
		this.type = type;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ScenarioType)) {
			return false;
		}
		ScenarioType other = (ScenarioType) obj;
		if (getTypeName() == null) {
			if (other.getTypeName() != null) {
				return false;
			}
		} else if (!getTypeName().equals(other.getTypeName())) {
			return false;
		}
		return true;
	}
}