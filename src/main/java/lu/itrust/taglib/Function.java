/**
 * 
 */
package lu.itrust.taglib;

/**
 * @author eomar
 *
 */
public final class Function {

	public static boolean matches(String value, String regex){
		return value == null || regex == null? false : value.matches(regex);
	}
}
