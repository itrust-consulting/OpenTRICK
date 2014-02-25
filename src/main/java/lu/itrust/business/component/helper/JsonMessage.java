/**
 * 
 */
package lu.itrust.business.component.helper;

/**
 * @author eom
 *
 */
public class JsonMessage {
	
	public static String Error(String message){
		return "{\"error\":\""+message+"\"}";
	}
	
	public static String Success(String message){
		return "{\"success\":\""+message+"\"}";
	}
}
