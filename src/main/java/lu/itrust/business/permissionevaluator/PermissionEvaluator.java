package lu.itrust.business.permissionevaluator;

import java.security.Principal;

import jakarta.servlet.http.HttpSession;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.general.TSSettingName;
/**
 * PermissionEvaluator.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à r.l
 * @version
 * @since Jan 16, 2014
 */
public interface PermissionEvaluator{

	/**
	 * Checks if the specified principal has delete permission for the given analysis ID.
	 *
	 * @param analysisId the ID of the analysis
	 * @param principal the principal representing the user
	 * @param isProfile a flag indicating whether the analysis is a profile
	 * @return true if the principal has delete permission, false otherwise
	 */
	boolean hasDeletePermission(Integer analysisId, Principal principal, Boolean isProfile);
	
	/**
	 * Checks if the specified principal has management permission for the given analysis ID.
	 *
	 * @param analysisId the ID of the analysis
	 * @param principal the principal representing the user
	 * @return true if the principal has management permission, false otherwise
	 */
	boolean hasManagementPermission(Integer analysisId, Principal principal);

	/**
	 * Checks if the user is authorized to perform a specific analysis right on an element.
	 *
	 * @param session the HttpSession object representing the user's session
	 * @param elementId the ID of the element to be analyzed
	 * @param className the name of the class representing the element
	 * @param principal the Principal object representing the user's identity
	 * @param right the AnalysisRight enum representing the right to be checked
	 * @return true if the user is authorized, false otherwise
	 */
	boolean userIsAuthorized(HttpSession session, Integer elementId, String className, Principal principal, AnalysisRight right);

	/**
	 * Checks if the user is authorized to perform the specified analysis right.
	 *
	 * @param session   the HttpSession object representing the user's session
	 * @param principal the Principal object representing the user's identity
	 * @param right     the AnalysisRight object representing the analysis right to be checked
	 * @return true if the user is authorized, false otherwise
	 */
	boolean userIsAuthorized(HttpSession session, Principal principal, AnalysisRight right);

	/**
	 * Determines whether the user is authorized to perform the specified analysis right on the given analysis.
	 *
	 * @param analysisId the ID of the analysis
	 * @param principal the principal representing the user
	 * @param right the analysis right to be checked
	 * @return true if the user is authorized, false otherwise
	 */
	boolean userIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right);

	/**
	 * Checks if the user or owner is authorized to perform the specified analysis right.
	 *
	 * @param analysisId the ID of the analysis
	 * @param principal the principal representing the user
	 * @param right the analysis right to be checked
	 * @return true if the user or owner is authorized, false otherwise
	 */
	boolean userOrOwnerIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right);

	/**
	 * Determines whether the user is authorized to perform a specific action on an analysis element.
	 *
	 * @param analysisId   the ID of the analysis
	 * @param elementId    the ID of the element
	 * @param className    the name of the class
	 * @param principal    the principal representing the user
	 * @param right        the analysis right to check
	 * @return true if the user is authorized, false otherwise
	 */
	boolean userIsAuthorized(Integer analysisId, Integer elementId, String className, Principal principal, AnalysisRight right);
	
	/**
	 * Checks if the specified principal has the given analysis right for the specified analysis ID.
	 *
	 * @param analysisId the ID of the analysis
	 * @param principal the principal representing the user
	 * @param right the analysis right to check
	 * @return true if the principal has the specified analysis right, false otherwise
	 */
	boolean hasPermission(Integer analysisId, Principal principal, AnalysisRight right);
	
	/**
	 * Checks if the specified setting is allowed.
	 *
	 * @param setting the name of the setting to check
	 * @return true if the setting is allowed, false otherwise
	 */
	boolean isAllowed(TSSettingName setting);
	
	/**
	 * Checks if the specified setting is allowed.
	 *
	 * @param setting the TSSettingName to check
	 * @param defaultValue the default value to return if the setting is not found
	 * @return true if the setting is allowed, false otherwise
	 */
	boolean isAllowed(TSSettingName setting, boolean defaultValue);
}
