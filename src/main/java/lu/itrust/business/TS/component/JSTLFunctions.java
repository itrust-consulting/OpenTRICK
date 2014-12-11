package lu.itrust.business.TS.component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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

	/*public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;

		// ;

	}*/
	
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		DecimalFormat df2 = new DecimalFormat("###.##");
        
		BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return Double.valueOf(df2.format(bd.doubleValue()));

		/*
		 * long tmp = Math.round(value); return (double) tmp / factor;
		 */
	}

}
