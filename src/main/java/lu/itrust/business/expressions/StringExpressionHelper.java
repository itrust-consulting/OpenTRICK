package lu.itrust.business.expressions;

import java.util.regex.Pattern;

/**
 * Provides additional functionality related to string expressions.
 * 
 * @author (SMU), itrust consulting s.à r.l.
 * @since Jun 19, 2015
 */
public class StringExpressionHelper {
	private static Pattern RegexUnwantedCharacters = Pattern.compile("^[0-9.]+|[^a-zA-Z0-9_]+");

	/**
	 * Replaces all invalid characters in the given name.
	 * 
	 * @param potentialName
	 *            The potential name of a variable.
	 * @return Returns a valid variable name which can be used in an expression.
	 */
	public static String makeValidVariable(String potentialName) {
		potentialName = RegexUnwantedCharacters.matcher(potentialName).replaceAll("_");
		if (potentialName.length() == 0)
			return "_";
		else
			return potentialName;
	}
}