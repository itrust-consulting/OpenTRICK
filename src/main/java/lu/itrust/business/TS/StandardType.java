package lu.itrust.business.TS;

import lu.itrust.business.exception.TrickException;

/**
 * StandardType: <br>
 * <li>NORMAL(1)</li>
 * <li>MATURITY(2)</li>
 * <li>ASSET(3)</li>
 * @author smenghi itrust consulting s.a.rl.:
 * @version 
 * @since Oct 6, 2014
 */
public enum StandardType {

	/** ActionPlanMode Value Constants */
	NORMAL(1), MATURITY(2), ASSET(3);

	/** ActionPlanModeValue */
	private int value = 1;

	private String[] NAMES = {"NORMAL", "MATURITY" , "ASSET"};
	
	/**
	 * Constructor:<br>
	 * 
	 * @param value
	 *            The value to set the ActionPlanMode
	 */
	private StandardType(int value) {
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
	 * valueOf: <br>
	 * Description
	 * 
	 * @param value
	 * @return
	 * @throws TrickException
	 */
	public static StandardType valueOf(int value) throws TrickException {
		StandardType[] values = values();
		if (value < 1 || value > values.length)
			throw new TrickException("error.standard_type.out_of_bound","Value should be between 1 and " + values.length, String.valueOf(1), String.valueOf(values.length));
		return values[value - 1];
	}

	public String getName() {
		return NAMES[this.value-1];
	}

	public static StandardType getByName(String name) {
		StandardType[] values = values();
		for (int i = 0; i < values.length;i++)
			if (values[i].getName().equals(name.trim()))
				return values[i];
		return null;
	}
}