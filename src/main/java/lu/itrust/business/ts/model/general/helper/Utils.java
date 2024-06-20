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
 * The Utils class provides utility methods for various operations.
 */
public final class Utils {

	/**
	 * Checks if the given text has any content.
	 *
	 * @param text the text to check
	 * @return true if the text has content, false otherwise
	 */
	public static boolean hasText(String text) {
		return StringUtils.hasText(text);
	}

	/**
	 * Checks if the given text is empty.
	 *
	 * @param text the text to check
	 * @return true if the text is empty, false otherwise
	 */
	public static boolean isEmpty(String text) {
		return !StringUtils.hasText(text);
	}

	/**
	 * Cleans up the file name by replacing certain characters with underscores and removing spaces.
	 *
	 * @param text the file name to clean up
	 * @return the cleaned up file name
	 */
	public static String cleanUpFileName(String text) {
		return isEmpty(text) ? "" : text.replaceAll(Constant.CLEAN_UP_FILE_NAME, "_").replaceAll("[ ]", "").replaceAll("[_]{2,}", "");
	}

	/**
	 * Extracts the original filename from the given name by removing the timestamp control string.
	 *
	 * @param name the name to extract the original filename from
	 * @return the original filename
	 */
	public static String extractOrignalFilename(String name) {
		final int index = hasText(name) ? name.lastIndexOf(Constant.TS_GEN_TIME_CTRL) : -1;
		return index == -1 ? name : name.substring(0, index);
	}

	/**
	 * Parses the given value as an integer and returns the result. If the parsing fails, the default value is returned.
	 *
	 * @param value        the value to parse
	 * @param defaultValue the default value to return if parsing fails
	 * @return the parsed integer value or the default value if parsing fails
	 */
	public static int parseInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Checks if the given URL is valid.
	 *
	 * @param url the URL to check
	 * @return true if the URL is valid, false otherwise
	 */
	public static boolean isValidURL(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (MalformedURLException | URISyntaxException e) {
			return false;
		}
	}

}
