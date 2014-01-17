package lu.itrust.business.permissionevaluator;

import java.io.Serializable;
import java.security.Principal;

import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.service.ServiceUserAnalysisRight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/** 
 * PermissionEvaluatorImpl.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 
 * @since Jan 16, 2014
 */
@Component("permissionEvaluator")
public class PermissionEvaluatorImpl implements PermissionEvaluator {

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;
	
	public PermissionEvaluatorImpl(){}
	
	/**
	 * userIsAuthorized: <br>
	 * Description
	 *
	 * @see lu.itrust.business.permissionevaluator.PermissionEvaluator#userIsAuthorized(int, java.lang.String, lu.itrust.business.TS.AnalysisRight)
	 */
	@Override
	public boolean userIsAuthorized(int analysisId, Principal principal, AnalysisRight right, Integer selectedAnalysis) throws Exception {
		
		//System.out.println("Selected analysis: " + selectedAnalysis);
		
		return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right);
	}

	@Override
	public boolean userIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right) throws Exception {
		
		//System.out.println("Selected analysis first: " + analysisId);

		
		return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		// TODO Auto-generated method stub
		return false;
	}

}
