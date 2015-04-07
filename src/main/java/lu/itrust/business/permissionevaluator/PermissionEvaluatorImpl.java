package lu.itrust.business.permissionevaluator;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lu.itrust.business.TS.component.GeneralComperator;
import lu.itrust.business.TS.data.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceActionPlanSummary;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceHistory;
import lu.itrust.business.TS.database.service.ServiceItemInformation;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.database.service.ServiceRiskRegister;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;

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
	private ServiceHistory serviceHistory;

	@Autowired
	private ServiceItemInformation serviceItemInformation;

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private ServiceParameter serviceParameter;

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceRiskInformation serviceRiskInformation;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceRiskRegister serviceRiskRegister;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	public PermissionEvaluatorImpl() {
	}

	public PermissionEvaluatorImpl(ServiceUser serviceUser, ServiceAnalysis serviceAnalysis, ServiceUserAnalysisRight serviceUserAnalysisRight) {
		this.serviceUser = serviceUser;
		this.serviceAnalysis = serviceAnalysis;
		this.serviceUserAnalysisRight = serviceUserAnalysisRight;
	}

	public void setServiceUser(ServiceUser serviceUser) {
		this.serviceUser = serviceUser;
	}

	public void setServiceUserAnalysisRight(ServiceUserAnalysisRight serviceUserAnalysisRight) {
		this.serviceUserAnalysisRight = serviceUserAnalysisRight;
	}

	@Override
	public boolean userIsAuthorized(Integer analysisId, Integer elementId, String className, Principal principal, AnalysisRight right){
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
			case "Assessment": {

				if (!serviceAssessment.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "Asset": {

				if (!serviceAsset.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "History": {

				if (!serviceHistory.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "ItemInformation": {

				if (!serviceItemInformation.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "Measure": {

				if (!serviceMeasure.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			case "Parameter": {

				if (!serviceParameter.belongsToAnalysis(analysisId, elementId))
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
			case "Scenario": {

				if (!serviceScenario.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}

			case "RiskRegister": {
				if (!serviceRiskRegister.belongsToAnalysis(analysisId, elementId))
					return false;
				break;
			}
			default:
				return false;
			}
			return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean userIsAuthorized(Integer analysisId, Principal principal, AnalysisRight right) {
		try {

			if (analysisId == null || analysisId <= 0)
				throw new InvalidParameterException("Invalid analysis id!");
			else if (!serviceAnalysis.exists(analysisId))
				throw new NotFoundException("Analysis does not exist!");

			if (principal == null)
				return false;

			if (right == null)
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
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean canCreateNewVersion(Integer analysisId, Principal principal, AnalysisRight right) {
		try {
			if (principal == null || analysisId == null || right == null || analysisId < 1)
				return false;
			String identifier = serviceAnalysis.getIdentifierByIdAnalysis(analysisId);
			List<String> versions = serviceAnalysis.getAllVersion(identifier);
			if (versions.size() > 1) {
				Comparator<String> comparator = new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return GeneralComperator.VersionComparator(o2, o1);
					}
				};
				Collections.sort(versions, comparator);
				Integer idLastVersion = serviceAnalysis.getIdFromIdentifierAndVersion(identifier, versions.get(0));
				if (idLastVersion != analysisId)
					return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right)
							&& serviceUserAnalysisRight.isUserAuthorized(idLastVersion, principal.getName(), AnalysisRight.ALL)
							|| serviceAnalysis.isAnalysisOwner(idLastVersion, principal.getName());
			}
			return serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), right);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
