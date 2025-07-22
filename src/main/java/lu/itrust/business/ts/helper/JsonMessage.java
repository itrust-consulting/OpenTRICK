/**
 * 
 */
package lu.itrust.business.ts.helper;

import java.util.Map;

import org.springframework.lang.NonNull;

import net.minidev.json.JSONObject;

/**
 * @author eom
 *
 */
public class JsonMessage {

	public static String error(String message) {
		return field("error", message);
	}

	public static String info(String message) {
		return field("info", message);
	}

	public static String success(String message) {
		return field("success", message);
	}

	public static String successWithId(Integer id) {
		return String.format("{\"success\": true, \"id\": %d}", id);
	}

	public static String warning(String message) {
		return field("warning", message);
	}

	public static String field(String fieldName, String value) {
		return String.format("{\"%s\": \"%s\"}", fieldName, JSONObject.escape(value));
	}

	public static void error(@NonNull Map<String, Object> messages, String text) {
		messages.put("error", text);
	}

	public static void success(@NonNull Map<String, Object> messages, String text) {
		messages.put("success", text);
	}
}
