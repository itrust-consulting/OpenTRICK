package lu.itrust.business.permissionevaluator;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.security.Principal;

import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAsset;
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
	
	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAsset serviceAsset;
	
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

	@Override
	public boolean userIsAuthorized(Integer analysisId, Integer elementId, String className, Principal principal, AnalysisRight right) throws Exception {
		
		try {
		
			if (analysisId == null || analysisId <= 0)
				throw new InvalidParameterException("Invalid analysis id!");
			else
				if (!serviceAnalysis.exist(analysisId))
					throw new NotFoundException("Analysis does not exist!");
			
			if(className == null || className.isEmpty())
				throw new InvalidParameterException("Invalid class name!");
			
			if (elementId == null || elementId <= 0)
				throw new InvalidParameterException("Invalid element id selected!");
						
			if(principal == null)
				throw new InvalidParameterException("Principal cannot be null!");
			
			if(right == null)
				throw new InvalidParameterException("AnalysisRight cannot be null!");
			
			switch (className) {
				case "Asset":{
					
					if(!serviceAsset.belongsToAnalysis(elementId, analysisId))
							return false;
					break;
				}
			}
			
			
		return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean userIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right) throws Exception {
		
		try {
		
			if (analysisId == null || analysisId <= 0)
				throw new InvalidParameterException("Invalid analysis id!");
			else
				if (!serviceAnalysis.exist(analysisId))
					throw new NotFoundException("Analysis does not exist!");
						
			if(principal == null)
				throw new InvalidParameterException("Principal cannot be null!");
			
			if(right == null)
				throw new InvalidParameterException("AnalysisRight cannot be null!");
			
		return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		return false;
	}
}
