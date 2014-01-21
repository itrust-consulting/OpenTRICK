package lu.itrust.business.TS.actionplan;

/**
 * ActionPlanType: <br>
 * Represents the actionplan type by name.
 * 
 * @author itrust consulting s.�.rl. : EOM, BJA, SME
 * @version 0.1
 * @since 28 janv. 2013
 */
public class ActionPlanType {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** actionplantype id */
	private int id = -1;

	/** ActionplanType name */
	private ActionPlanMode name;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 * 
	 * @param name
	 */
	public ActionPlanType(ActionPlanMode mode) {
		this.id = mode.getValue();
		this.name = mode;
	}

	/**
	 * Constructor:<br>
	 */
	public ActionPlanType() {
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
	 * getName: <br>
	 * Returns the name field value.
	 * 
	 * @return The value of the name field
	 */
	public String getName() {
		return name.name();
	}
	
	public ActionPlanMode getActionPlanMode() {
		return this.name;
	}

	public void setActionPlanMode(ActionPlanMode mode) {
		this.name = mode;
	}
	
	/**
	 * setName: <br>
	 * Sets the Field "name" with a value.
	 * 
	 * @param name
	 *            The Value to set the name field
	 */
	public void setName(String name) {
		this.name = ActionPlanMode.getByName(name);
	}
}