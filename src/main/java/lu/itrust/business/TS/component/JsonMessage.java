/**
 * 
 */
package lu.itrust.business.TS.component;

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
		return String.format("{\"%s\": \"%s\"}", fieldName, value);
	}
}
