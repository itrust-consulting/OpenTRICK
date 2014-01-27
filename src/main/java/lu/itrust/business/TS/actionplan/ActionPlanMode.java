package lu.itrust.business.TS.actionplan;

/**
 * ActionPlanMode <br>
 * <b>There are 6 modes:</b>
 * <ul>
 * <li>NORMAL (1)</li>
 * <li>OPTIMISTIC (2)</li>
 * <li>PESSIMISTIC (3)</li>
 * <li>PHASE_NORMAL (4)</li>
 * <li>PHASE_OPTIMISTIC (5)</li>
 * <li>PHASE_PESSIMISTIC (6)</li>
 * </ul>
 * 
 * @author EOM, BJA, SME
 * @version 0.1
 * @since 2012
 */
public enum ActionPlanMode {

	/** ActionPlanMode Value Constants */
	NORMAL(1), OPTIMISTIC(2), PESSIMISTIC(3), PHASE_NORMAL(4), PHASE_OPTIMISTIC(5), PHASE_PESSIMISTIC(
			6);

	/** ActionPlanModeValue */
	private int value = 1;

	/** List of ActionPlanMode Names */
	private static String[] NAMES = { "APN", "APO", "APP", "APPN", "APPO", "APPP" };

	/**
	 * Constructor:<br>
	 * 
	 * @param value
	 *            The value to set the ActionPlanMode
	 */
	private ActionPlanMode(int value) {
		this.value = value;
	}

	/**
	 * getValue: <br>
	 * Returns the Value of the ActionPlanMode
	 * 
	 * @return The Value of the ActionPlanMode
	 */
	public int getValue() {
		return value;
	}

	/**
	 * getName: <br>
	 * Returns the name of the actionplan mode: "APN", "APO", "APP", "APPN", "APPO", "APPP"
	 * 
	 * @return The Name of this ActionPlanMode object
	 */
	public String getName() {
		return NAMES[value - 1];
	}

	/**
	 * valueOf:<br>
	 * Retrieves:
	 * <ul>
	 * <li>NORMAL (1)</li>
	 * <li>OPTIMISTIC (2)</li>
	 * <li>PESSIMISTIC (3)</li>
	 * <li>PHASE_NORMAL (4)</li>
	 * <li>PHASE_OPTIMISTIC (5)</li>
	 * <li>PHASE_PESSIMISTIC (6)</li>
	 * </ul>
	 * 
	 * @param value
	 *            ActionPlanmode value from 1 to 6
	 * @return ActionplanMode
	 * @throws IllegalArgumentException
	 *             when value is not between 1 and 6
	 */
	public static ActionPlanMode valueOf(int value) {
		ActionPlanMode[] values = values();
		if (value < 1 || value > values.length)
			throw new IllegalArgumentException("Value should be between 1 and " + values.length);
		return values[value - 1];
	}

	/**
	 * getName: <br>
	 * Returns the Name of a given actionplanmode (1 to 6)
	 * 
	 * @param value
	 *            The value from 1 to 6 of actionplanmode
	 * @return The Name of the actionplanmode
	 */
	public static String getName(int value) {
		if (value < 1 || value > NAMES.length)
			throw new IllegalArgumentException("Value should be between 1 and " + NAMES.length);
		return NAMES[value - 1];
	}

	/**
	 * getIndex: <br>
	 * Returns the index of the actionplanmode given as name
	 * 
	 * @param name
	 *            The name of the actionplanmode
	 * @return The index of the actionplanmode (1 to 6)
	 */
	public static int getIndex(String name) {
		for (int i = 0; i < NAMES.length; i++)
			if (NAMES[i].equalsIgnoreCase(name))
				return i + 1;
		throw new IllegalArgumentException("Name should be APN, APO, APP, APPN, APPO or APPP");
	}
	
	public static ActionPlanMode getByName(String name) {
		for (int i = 0; i < NAMES.length; i++)
			if (NAMES[i].equalsIgnoreCase(name))
				return ActionPlanMode.valueOf(i+1);
		throw new IllegalArgumentException("Name should be APN, APO, APP, APPN, APPO or APPP");
	}
	
}