package lu.itrust.business.TS.model.actionplan;

import lu.itrust.business.TS.exception.TrickException;

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
 * <li>QUALITATIVE (7) </li>
 * </ul>
 * 
 * @author EOM, BJA, SME
 * @version 0.1
 * @since 2012
 */
public enum ActionPlanMode {

	/** ActionPlanMode Value Constants */
	APN(1), APO(2), APP(3), APPN(4), APPO(5), APPP(6),APQ(7);

	/** ActionPlanModeValue */
	private int value = 1;

	private String[] NAMES = {"APN", "APO" , "APP", "APPN", "APPO", "APPP","APQ"};
	
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
	 * valueOf:<br>
	 * Retrieves:
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
	 * @param value
	 *            ActionPlanmode value from 1 to 6
	 * @return ActionplanMode
	 * @throws TrickException 
	 * @throws IllegalArgumentException
	 *             when value is not between 1 and 6
	 */
	public static ActionPlanMode valueOf(int value) throws TrickException {
		ActionPlanMode[] values = values();
		if (value < 1 || value > values.length)
			throw new TrickException("error.action_plan_mode.out_of_bound","Value should be between 1 and " + values.length, String.valueOf(1), String.valueOf(values.length));
		return values[value - 1];
	}

	public String getName() {
		return NAMES[this.value-1];
	}

	public static ActionPlanMode getByName(String name) {
		ActionPlanMode[] values = values();
		for (int i = 0; i < values.length;i++)
			if (values[i].getName().equals(name.trim()))
				return values[i];
		return null;
	}
}