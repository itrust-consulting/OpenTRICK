package lu.itrust.business.permissionevaluator;

import java.io.Serializable;
import java.security.Principal;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserAnalysisRight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
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
	
	@Autowired
	private ServiceUser serviceUser;
	
	public PermissionEvaluatorImpl(){
	}
	
	public PermissionEvaluatorImpl(ServiceUser serviceUser, ServiceUserAnalysisRight serviceUserAnalysisRight){
		this.serviceUser=serviceUser;
		this.serviceUserAnalysisRight=serviceUserAnalysisRight;
	}
	
	public void setServiceUser(ServiceUser serviceUser) {
		this.serviceUser=serviceUser;
	}
	
	public void setServiceUserAnalysisRight(ServiceUserAnalysisRight serviceUserAnalysisRight) {
		this.serviceUserAnalysisRight=serviceUserAnalysisRight;
	}
	
	/**
	 * userIsAuthorized: <br>
	 * Description
	 *
	 * @see lu.itrust.business.permissionevaluator.PermissionEvaluator#userIsAuthorized(int, java.lang.String, lu.itrust.business.TS.AnalysisRight)
	 */
	@Override
	public boolean userIsAuthorized(int analysisId, Principal principal, AnalysisRight right, Integer selectedAnalysis) throws Exception {
			
		return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right);
	}

	@Override
	public boolean userIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right) throws Exception {
		
		//System.out.println("Preauthorize analysis id: " + analysisId);
		
		if (serviceUser.get(principal.getName()).hasRole(RoleType.ROLE_ADMIN)) {
			return true;
		}
		
		return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right);
	}

	@Override
	public boolean userIsAuthorized(HttpSession session, Principal principal, AnalysisRight right) throws Exception {
		
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");
		
		//System.out.println("Preauthorize analysis from selected id: " + selected);
		
		if (selected == null) {
			throw new NotFoundException("No analysis selected!");
		}
		
		return serviceUserAnalysisRight.isUserAuthorized(selected, principal.getName(), right);
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
