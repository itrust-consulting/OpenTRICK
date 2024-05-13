package lu.itrust.business.ts.model.analysis.rights;

import java.util.LinkedList;
import java.util.List;

import lu.itrust.business.ts.exception.TrickException;

/**
 * The AnalysisRight enum represents the different rights that can be assigned to an analysis.
 */
public enum AnalysisRight {

	ALL(0), EXPORT(1), MODIFY(2), READ(3);

	/**
	 * Constructor for the AnalysisRight enum.
	 *
	 * @param value The value to set the AnalysisRight.
	 */
	private AnalysisRight(int value) {
		this.value = value;
	}

	/** The value of the AnalysisRight. */
	private int value = 0;

	/**
	 * Returns the AnalysisRight corresponding to the given value.
	 *
	 * @param value The value to search for.
	 * @return The AnalysisRight corresponding to the given value.
	 * @throws TrickException If the value is out of bounds.
	 */
	public static AnalysisRight valueOf(int value) throws TrickException {
		AnalysisRight[] values = values();
		if (value < 0 || value > values.length - 1)
			throw new TrickException("error.analysis_right.out_of_bounds", "Value should be between 0 and " + (values.length - 1), Integer.toString(values.length - 1));
		return values[value];
	}

	/**
	 * Returns the value of the AnalysisRight.
	 *
	 * @return The value of the AnalysisRight.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Checks if the given value is a valid AnalysisRight.
	 *
	 * @param value The value to check.
	 * @return True if the value is a valid AnalysisRight, false otherwise.
	 */
	public static boolean isValid(int value) {
		return value > -1 && value < values().length;
	}

	/**
	 * Returns a list of AnalysisRights that have a higher or equal ordinal value compared to the given right.
	 *
	 * @param right The AnalysisRight to compare against.
	 * @return A list of AnalysisRights with higher or equal ordinal value.
	 */
	public static List<AnalysisRight> highRightFrom(AnalysisRight right) {
		List<AnalysisRight> rights = new LinkedList<AnalysisRight>();
		if (right != null) {
			AnalysisRight[] values = values();
			for (int i = 0; i <= right.ordinal(); i++)
				rights.add(values[i]);
		}
		return rights;
	}

	/**
	 * Returns the lowercase string representation of the AnalysisRight.
	 *
	 * @return The lowercase string representation of the AnalysisRight.
	 */
	public String toLower() {
		return name().toLowerCase();
	}
}