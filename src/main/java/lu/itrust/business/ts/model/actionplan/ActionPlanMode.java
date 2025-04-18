package lu.itrust.business.ts.model.actionplan;

import lu.itrust.business.ts.exception.TrickException;

/**
 * Represents the different modes of an action plan.
 *  <b>There are 7 modes:</b>
 * <ul>
 * <li>NORMAL (1)</li>
 * <li>OPTIMISTIC (2)</li>
 * <li>PESSIMISTIC (3)</li>
 * <li>PHASE_NORMAL (4)</li>
 * <li>PHASE_OPTIMISTIC (5)</li>
 * <li>PHASE_PESSIMISTIC (6)</li>
 * <li>QUALITATIVE (7) </li>
 * </ul>
 */
public enum ActionPlanMode {

	/** ActionPlanMode Value Constants */
	APN(1), APO(2), APP(3), APPN(4), APPO(5), APPP(6), APQ(7);

	/** ActionPlanModeValue */
	private int value = 1;

	private String[] NAMES = {"APN", "APO" , "APP", "APPN", "APPO", "APPP","APQ"};

	/**
	 * Constructor:<br>
	 * Creates a new ActionPlanMode with the specified value.
	 *
	 * @param value The value to set the ActionPlanMode.
	 */
	private ActionPlanMode(int value) {
		this.value = value;
	}

	/**
	 * getValue: <br>
	 * Returns the value of the ActionPlanMode.
	 *
	 * @return The value of the ActionPlanMode.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * valueOf:<br>
	 * Retrieves: The ActionPlanMode based on the specified value.
	 * <ul>
	 * <li>NORMAL (1)</li>
	 * <li>OPTIMISTIC (2)</li>
	 * <li>PESSIMISTIC (3)</li>
	 * <li>PHASE_NORMAL (4)</li>
	 * <li>PHASE_OPTIMISTIC (5)</li>
	 * <li>PHASE_PESSIMISTIC (6)</li>
	 * <li>QUALITATIVE (7)</li>
	 * </ul>
	 * 
	 *
	 * @param value The ActionPlanMode value from 1 to 7.
	 * @return The ActionPlanMode.
	 * @throws TrickException If the value is not between 1 and 7.
	 *  */
	public static ActionPlanMode valueOf(int value) throws TrickException {
		ActionPlanMode[] values = values();
		if (value < 1 || value > values.length)
			throw new TrickException("error.action_plan_mode.out_of_bound","Value should be between 1 and " + values.length, String.valueOf(1), String.valueOf(values.length));
		return values[value - 1];
	}

	/**
	 * getName: <br>
	 * Returns the name of the ActionPlanMode.
	 *
	 * @return The name of the ActionPlanMode.
	 */
	public String getName() {
		return NAMES[this.value-1];
	}

	/**
	 * getByName: <br>
	 * Retrieves the ActionPlanMode based on the specified name.
	 *
	 * @param name The name of the ActionPlanMode.
	 * @return The ActionPlanMode with the specified name, or null if not found.
	 */
	public static ActionPlanMode getByName(String name) {
		ActionPlanMode[] values = values();
		for (int i = 0; i < values.length;i++)
			if (values[i].getName().equals(name.trim()))
				return values[i];
		return null;
	}
}