package lu.itrust.business.permissionevaluator;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;

/**
 * PermissionEvaluator.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Jan 16, 2014
 */
public interface PermissionEvaluator extends org.springframework.security.access.PermissionEvaluator {

	boolean userIsAuthorized(HttpSession session, Integer elementId, String className, Principal principal, AnalysisRight right);

	boolean userIsAuthorized(HttpSession session, Principal principal, AnalysisRight right);

	boolean userIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right);

	//boolean userOrOwnerIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right);

	boolean userIsAuthorized(Integer analysisId, Integer elementId, String className, Principal principal, AnalysisRight right);
	
	boolean userCanCreateVersion(Integer analysisId, Principal principal, AnalysisRight right);

}
