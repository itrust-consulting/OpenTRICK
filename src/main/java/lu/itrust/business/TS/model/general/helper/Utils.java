/**
 * 
 */
package lu.itrust.business.TS.model.general.helper;

import org.springframework.util.StringUtils;

/**
 * @author eomar
 *
 */
public final class Utils {

	public static boolean hasText(String text) {
		return StringUtils.hasText(text);
	}
	
	public static boolean isEmpty(String text) {
		return  StringUtils.isEmpty(text);
	}

}
