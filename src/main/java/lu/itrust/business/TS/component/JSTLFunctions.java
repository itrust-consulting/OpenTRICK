package lu.itrust.business.TS.component;

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
	public static boolean matches(String value, String regex){
		return value == null || regex == null? false : value.matches(regex);
	}
	
}
