package lu.itrust.business.TS.data.scenario;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;

/** OldScenarioType.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à .r.l. : EOM, BJA, SME
 * @version 0.1
 * @since 23 janv. 2013
 */
@Entity
@Table(name="ScenarioType")
public class OldScenarioType {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** id unsaved value = -1 */
	@Id
	@GeneratedValue
	@Column(name = "idScenarioType")
	private int id = -1;

	/** scenario type name */
	@Column(name = "dtLabel", unique = true, nullable = false)
	private String name = "";

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public OldScenarioType() {
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param type
	 *            The Scenario Type Name
	 * @throws TrickException
	 */
	public OldScenarioType(String type) throws TrickException {
		setName(type);
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
	public String getName() {
		return name;
	}

	/**
	 * setType: <br>
	 * Sets the Field "type" with a value.
	 * 
	 * @param type
	 *            The Value to set the type field
	 * @throws TrickException
	 */
	public void setName(String type) throws TrickException {
		if (type == null || !type.trim().matches(Constant.REGEXP_VALID_SCENARIO_TYPE))
			throw new TrickException("error.scenario_type.invalid", "Type is invalid!");
		this.name = type.trim();
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
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}
}