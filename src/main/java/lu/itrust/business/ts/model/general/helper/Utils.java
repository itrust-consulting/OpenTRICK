/**
 * 
 */
package lu.itrust.business.ts.model.general.helper;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.util.StringUtils;

import lu.itrust.business.ts.constants.Constant;

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

	public static int parseInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static boolean isValidURL(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (MalformedURLException | URISyntaxException e) {
			return false;
		}
	}

}
