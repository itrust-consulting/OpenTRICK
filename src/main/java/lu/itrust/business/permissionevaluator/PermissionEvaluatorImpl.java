package lu.itrust.business.permissionevaluator;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceActionPlanSummary;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceDynamicParameter;
import lu.itrust.business.TS.database.service.ServiceHistory;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.database.service.ServiceItemInformation;
import lu.itrust.business.TS.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.TS.database.service.ServiceMaturityParameter;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.database.service.ServiceRiskProfile;
import lu.itrust.business.TS.database.service.ServiceRiskRegister;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.OpenMode;

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
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceActionPlanSummary serviceActionPlanSummary;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceDynamicParameter serviceDynamicParameter;

	@Autowired
	private ServiceHistory serviceHistory;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceItemInformation serviceItemInformation;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceMaturityParameter serviceMaturityParameter;

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private ServicePhase servicePhase;
	
	@Autowired
	private ServiceRiskAcceptanceParameter serviceRiskAcceptanceParameter;
	
	@Autowired
	private ServiceRiskInformation serviceRiskInformation;

	@Autowired
	private ServiceRiskProfile serviceRiskProfile;

	@Autowired
	private ServiceRiskRegister serviceRiskRegister;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	public PermissionEvaluatorImpl() {
	}

	public PermissionEvaluatorImpl(ServiceUser serviceUser, ServiceAnalysis serviceAnalysis, ServiceUserAnalysisRight serviceUserAnalysisRight) {
		this.serviceAnalysis = serviceAnalysis;
		this.serviceUserAnalysisRight = serviceUserAnalysisRight;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		return false;
	}

	public void setServiceUser(ServiceUser serviceUser) {
	}

	public void setServiceUserAnalysisRight(ServiceUserAnalysisRight serviceUserAnalysisRight) {
		this.serviceUserAnalysisRight = serviceUserAnalysisRight;
	}

	@Override
	public boolean userCanCreateVersion(Integer analysisId, Principal principal, AnalysisRight right) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean userIsAuthorized(HttpSession session, Integer elementId, String className, Principal principal, AnalysisRight right) {
		return userIsAuthorized(isAuthorised(session, principal, right), elementId, className, principal, right);
	}

	@Override
	public boolean userIsAuthorized(HttpSession session, Principal principal, AnalysisRight right) {
		return userIsAuthorized(isAuthorised(session, principal, right), principal, right);
	}


	@Override
	public boolean userIsAuthorized(Integer analysisId, Integer elementId, String className, Principal principal, AnalysisRight right) {
		try {

			if (analysisId == null || analysisId <= 0)
				throw new InvalidParameterException("Invalid analysis id!");
			else if (!serviceAnalysis.exists(analysisId))
				throw new NotFoundException("Analysis does not exist!");

			if (className == null || className.isEmpty())
				throw new InvalidParameterException("Invalid class name!");

			if (elementId == null || elementId <= 0)
				throw new InvalidParameterException("Invalid element id selected!");

			if (principal == null)
				throw new InvalidParameterException("Principal cannot be null!");

			if (right == null)
				throw new InvalidParameterException("AnalysisRight cannot be null!");

			switch (className) {

			case "Scenario": {
				if (!serviceScenario.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "Asset": {
				if (!serviceAsset.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "Assessment": {
				if (!serviceAssessment.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "Measure": {
				if (!serviceMeasure.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "ItemInformation": {
				if (!serviceItemInformation.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "Phase": {
				if (!servicePhase.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "RiskInformation": {
				if (!serviceRiskInformation.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "RiskProfile": {
				if (!serviceRiskProfile.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "SimpleParameter": {
				if (!serviceSimpleParameter.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}

			case "LikelihoodParameter": {
				if (!serviceLikelihoodParameter.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}

			case "MaturityParameter": {
				if (!serviceMaturityParameter.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}

			case "ImpactParameter": {
				if (!serviceImpactParameter.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}

			case "ActionPlanEntry": {
				if (!serviceActionPlan.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "ActionPlanSummary": {
				if (!serviceActionPlanSummary.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			
			case "RiskAcceptanceParameter" :
				if(!serviceRiskAcceptanceParameter.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			case "History": {
				if (!serviceHistory.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			
			case "RiskRegister": {
				if (!serviceRiskRegister.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}

			case "DynamicParameter": {
				if (!serviceDynamicParameter.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}

			default:
				return false;
			}
			return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	@Override
	public boolean userIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right) {
		try {
			if (analysisId == null || principal == null || right == null || !(analysisId > 0 || serviceAnalysis.exists(analysisId)))
				return false;
			return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	@Override
	public boolean userOrOwnerIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right) {
		try {
			if (analysisId == null || analysisId <= 0)
				throw new InvalidParameterException("Invalid analysis id!");
			else if (!serviceAnalysis.exists(analysisId))
				throw new NotFoundException("Analysis does not exist!");
			if (principal == null)
				return false;
			if (right == null)
				throw new InvalidParameterException("AnalysisRight cannot be null!");
			return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right) || serviceAnalysis.isAnalysisOwner(analysisId, principal.getName());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	private Integer isAuthorised(HttpSession session, Principal principal, AnalysisRight right) {
		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		OpenMode open = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		if (analysisId == null || principal == null || right == null)
			return null;
		if (OpenMode.isReadOnly(open) && right != AnalysisRight.READ)
			return null;
		return analysisId;
	}
}
