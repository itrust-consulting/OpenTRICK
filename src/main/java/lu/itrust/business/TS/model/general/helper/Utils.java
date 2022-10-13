/**
 * 
 */
package lu.itrust.business.TS.model.general.helper;

import org.springframework.util.StringUtils;

import lu.itrust.business.TS.constants.Constant;

/**
 * @author eomar
 *
 */
public final class Utils {

	public static boolean hasText(String text) {
		return StringUtils.hasText(text);
	}

	public static boolean isEmpty(String text) {
		return !StringUtils.hasText(text);
	}

	public static String cleanUpFileName(String text) {
		return isEmpty(text) ? ""
				: text.replaceAll(Constant.CLEAN_UP_FILE_NAME, "_").replaceAll("[ ]", "").replaceAll("[_]{2,}", "");
	}

	public static String extractOrignalFilename(String name) {
		final int index = hasText(name) ? name.lastIndexOf(Constant.TS_GEN_TIME_CTRL) : -1;
		return index == -1 ? name : name.substring(0, index);
	}

}
