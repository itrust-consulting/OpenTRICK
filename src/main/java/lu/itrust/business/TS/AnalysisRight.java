package lu.itrust.business.TS;

/** 
 * AnalysisRight: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 
 * @since Jan 9, 2014
 */
public enum AnalysisRight {
	ALL(0), DELETE(1), CALCULATE_RISK_REGISTER(2),CALCULATE_ACTIONPLAN(3), MODIFY(4), EXPORT(5), READ(6);
	
	/**
	 * Constructor:<br>
	 * 
	 * @param value
	 *            The value to set the ActionPlanMode
	 */
	private AnalysisRight(int value) {
		this.value = value;
	}
	
	/** ActionPlanModeValue */
	private int value = 0;
	
	/**
	 * valueOf: <br>
	 * Description
	 * 
	 * @param value
	 * @return
	 */
	public static AnalysisRight valueOf(int value) {
		AnalysisRight[] values = values();
		if (value < 0 || value > values.length-1)
			throw new IllegalArgumentException("Value should be between 0 and " + (values.length-1));
		return values[value];
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
}