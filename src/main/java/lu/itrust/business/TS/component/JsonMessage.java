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
		return "{\"error\":\"" + message + "\"}";
	}

	public static String Success(String message) {
		return "{\"success\":\"" + message + "\"}";
	}

	public static String Field(String fieldName, String value) {
		return String.format("{\"%s\": \"%s\"}", fieldName, JSONObject.escape(value) );
	}
}
