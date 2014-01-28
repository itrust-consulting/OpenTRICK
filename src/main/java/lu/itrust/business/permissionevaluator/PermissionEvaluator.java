package lu.itrust.business.permissionevaluator;

import java.security.Principal;

import lu.itrust.business.TS.AnalysisRight;

/** 
 * PermissionEvaluator.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 
 * @since Jan 16, 2014
 */
public interface PermissionEvaluator extends org.springframework.security.access.PermissionEvaluator {

	public boolean userIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right) throws Exception;
	
}
