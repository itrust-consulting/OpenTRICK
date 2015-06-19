package lu.itrust.business.expressions;


/**
 * Provides additional functionality related to string expressions.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 19, 2015
 */
public class StringExpressionHelper {
	/**
	 * Replaces all invalid characters in the given name.
	 * @param potentialName The potential name of a variable.
	 * @return Returns a valid variable name which can be used in an expression.
	 */
	public static String makeValidVariable(String potentialName) {
		potentialName.replaceAll("[^a-zA-Z0-9_]+", "_");
		potentialName.replaceAll("^[0-9.]+", "");
		if (potentialName.length() == 0)
			return "_";
		else
			return potentialName;
	}
}