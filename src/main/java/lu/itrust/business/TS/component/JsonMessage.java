/**
 * 
 */
package lu.itrust.business.TS.component;

import net.minidev.json.JSONObject;

/**
 * @author eom
 *
 */
public class JsonMessage {

	public static String Error(String message) {
		return Field("error", message);
	}

	public static String Info(String message) {
		return Field("info", message);
	}

	public static String Success(String message) {
		return Field("success", message);
	}

	public static String Warning(String message) {
		return Field("warning", message);
	}

	public static String Field(String fieldName, String value) {
		return String.format("{\"%s\": \"%s\"}", fieldName, JSONObject.escape(value));
	}
}
