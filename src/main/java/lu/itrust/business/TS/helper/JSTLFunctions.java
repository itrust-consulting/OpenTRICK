package lu.itrust.business.TS.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Function.java: <br>
 * Detailed description...
 *
 * @author eomar itrust consulting s.a.rl.:
 * @version
 * @since Nov 4, 2014
 */
public final class JSTLFunctions {

	/**
	 * matches: <br>
	 * Description
	 * 
	 * @param value
	 * @param regex
	 * @return
	 */
	public static boolean matches(String value, String regex) {
		return value == null || regex == null ? false : value.matches(regex);
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}
