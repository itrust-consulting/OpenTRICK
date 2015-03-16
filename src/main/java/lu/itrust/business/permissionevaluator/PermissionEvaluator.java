package lu.itrust.business.permissionevaluator;

import java.security.Principal;

import lu.itrust.business.TS.data.analysis.rights.AnalysisRight;

/** 
 * PermissionEvaluator.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 
 * @since Jan 16, 2014
 */
public interface PermissionEvaluator extends org.springframework.security.access.PermissionEvaluator {

	boolean userIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right) throws Exception;
	
	boolean userOrOwnerIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right) throws Exception;
	
	boolean userIsAuthorized(Integer analysisId, Integer elementId, String className, Principal principal, AnalysisRight right) throws Exception;
	
}
