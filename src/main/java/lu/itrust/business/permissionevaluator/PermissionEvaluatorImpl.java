package lu.itrust.business.permissionevaluator;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.security.Principal;

import org.checkerframework.checker.fenum.qual.SwingCompassDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceActionPlan;
import lu.itrust.business.ts.database.service.ServiceActionPlanSummary;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceAssessment;
import lu.itrust.business.ts.database.service.ServiceAsset;
import lu.itrust.business.ts.database.service.ServiceDynamicParameter;
import lu.itrust.business.ts.database.service.ServiceHistory;
import lu.itrust.business.ts.database.service.ServiceIlrSoaScaleParameter;
import lu.itrust.business.ts.database.service.ServiceImpactParameter;
import lu.itrust.business.ts.database.service.ServiceItemInformation;
import lu.itrust.business.ts.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.ts.database.service.ServiceMaturityParameter;
import lu.itrust.business.ts.database.service.ServiceMeasure;
import lu.itrust.business.ts.database.service.ServicePhase;
import lu.itrust.business.ts.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.ts.database.service.ServiceRiskInformation;
import lu.itrust.business.ts.database.service.ServiceRiskProfile;
import lu.itrust.business.ts.database.service.ServiceRiskRegister;
import lu.itrust.business.ts.database.service.ServiceScenario;
import lu.itrust.business.ts.database.service.ServiceSimpleParameter;
import lu.itrust.business.ts.database.service.ServiceStandard;
import lu.itrust.business.ts.database.service.ServiceTSSetting;
import lu.itrust.business.ts.database.service.ServiceUser;
import lu.itrust.business.ts.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.ts.exception.ResourceNotFoundException;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.general.OpenMode;
import lu.itrust.business.ts.model.general.TSSettingName;

/**
 * This class implements the PermissionEvaluator interface and provides the
 * implementation for evaluating permissions in the application.
 * It is responsible for checking if a user has permission to perform certain
 * actions on specific objects or resources.
 * The class uses various service dependencies to perform the permission checks.
 * 
 * The class provides methods for checking permissions based on different
 * parameters such as authentication, target domain object, target ID, etc.
 * It also provides methods for checking if a user is authorized to perform
 * certain actions on specific elements or resources.
 * 
 * The class also provides methods for checking if a user or owner is authorized
 * to perform certain actions on an analysis.
 * 
 * Note: This class is annotated with @Component to be recognized as a Spring
 * bean and can be injected into other components or services.
 * 
 * @author itrust consulting s.à.rl. :
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

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceTSSetting serviceTSSetting;

	@Autowired
	private ServiceIlrSoaScaleParameter serviceIlrSoaScaleParameter;

	/**
	 * This class is responsible for evaluating permissions.
	 * It implements the PermissionEvaluator interface.
	 */
	public PermissionEvaluatorImpl() {
	}

	public PermissionEvaluatorImpl(ServiceUser serviceUser, ServiceAnalysis serviceAnalysis,
			ServiceUserAnalysisRight serviceUserAnalysisRight) {
		this.serviceAnalysis = serviceAnalysis;
		this.serviceUserAnalysisRight = serviceUserAnalysisRight;
	}

	public void setServiceUserAnalysisRight(ServiceUserAnalysisRight serviceUserAnalysisRight) {
		this.serviceUserAnalysisRight = serviceUserAnalysisRight;
	}

	@Override
	public boolean hasPermission(Integer analysisId, Principal principal, AnalysisRight right) {
		try {
			if (analysisId == null || analysisId <= 0)
				throw new InvalidParameterException("Invalid analysis id!");
			else if (!serviceAnalysis.exists(analysisId))
				throw new ResourceNotFoundException("Analysis does not exist!");
			if (principal == null)
				return false;
			if (right == null)
				throw new InvalidParameterException("AnalysisRight cannot be null!");
			return serviceUserAnalysisRight.hasRightOrOwner(analysisId, principal.getName(), right);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	@Override
	public boolean userIsAuthorized(HttpSession session, Integer elementId, String className, Principal principal,
			AnalysisRight right) {
		return userIsAuthorized(isAuthorised(session, principal, right), elementId, className, principal, right);
	}

	@Override
	public boolean userIsAuthorized(HttpSession session, Principal principal, AnalysisRight right) {
		return userIsAuthorized(isAuthorised(session, principal, right), principal, right);
	}

	/**
	 * Checks if a user is authorized to perform a specific action on a given
	 * element in an analysis.
	 *
	 * @param analysisId the ID of the analysis
	 * @param elementId  the ID of the element
	 * @param className  the name of the class representing the element
	 * @param principal  the principal object representing the user
	 * @param right      the analysis right required to perform the action
	 * @return true if the user is authorized, false otherwise
	 */
	@Override
	public boolean userIsAuthorized(Integer analysisId, Integer elementId, String className, Principal principal,
			AnalysisRight right) {
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

				case "RiskAcceptanceParameter":
					if (!serviceRiskAcceptanceParameter.belongsToAnalysis(analysisId, elementId))
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

				case "Standard": {
					if (!serviceStandard.belongsToAnalysis(analysisId, elementId))
						return false;
					break;
				}

				case "IlrSoaScaleParameter": {
					if (!serviceIlrSoaScaleParameter.belongsToAnalysis(analysisId, elementId))
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
			if (analysisId == null || principal == null || right == null
					|| !(analysisId > 0 || serviceAnalysis.exists(analysisId)))
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
				throw new ResourceNotFoundException("Analysis does not exist!");
			if (principal == null)
				return false;
			if (right == null)
				throw new InvalidParameterException("AnalysisRight cannot be null!");
			return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right)
					|| serviceAnalysis.isAnalysisOwner(analysisId, principal.getName());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	private Integer isAuthorised(HttpSession session, Principal principal, AnalysisRight right) {
		//if (session == null || principal == null || right == null)
		//	return null;
		Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		OpenMode open = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		if (analysisId == null)
			return null;
		if (OpenMode.isReadOnly(open) && right != AnalysisRight.READ)
			return null;
		return analysisId;
	}

	@Override
	public boolean hasDeletePermission(Integer analysisId, Principal principal, Boolean isProfile) {
		try {
			if (analysisId == null || analysisId <= 0)
				throw new InvalidParameterException("Invalid analysis id!");
			else if (!serviceAnalysis.exists(analysisId))
				throw new ResourceNotFoundException("Analysis does not exist!");
			if (principal == null)
				return false;
			return serviceUserAnalysisRight.hasDeletePermission(analysisId, principal.getName(), isProfile);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	@Override
	public boolean hasManagementPermission(Integer analysisId, Principal principal) {
		try {
			if (analysisId == null || analysisId <= 0)
				throw new InvalidParameterException("Invalid analysis id!");
			else if (!serviceAnalysis.exists(analysisId))
				throw new ResourceNotFoundException("Analysis does not exist!");
			if (principal == null)
				return false;
			return serviceUserAnalysisRight.hasManagementPermission(analysisId, principal.getName());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

	@Override
	public boolean isAllowed(TSSettingName setting) {
		if (!serviceTSSetting.isAllowed(setting))
			throw new ResourceNotFoundException("Page does not exist");
		return true;
	}

	@Override
	public boolean isAllowed(TSSettingName setting, boolean defaultValue) {
		if (!serviceTSSetting.isAllowed(setting, defaultValue))
			throw new ResourceNotFoundException("Page does not exist");
		return true;
	}
}
