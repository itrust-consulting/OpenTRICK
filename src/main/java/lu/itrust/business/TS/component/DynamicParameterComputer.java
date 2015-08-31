package lu.itrust.business.TS.component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOParameter;
import lu.itrust.business.TS.database.dao.DAOParameterType;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOParameterHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOParameterTypeHBM;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.database.service.impl.ServiceExternalNotificationImpl;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.helper.AssessmentManager;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.ParameterType;
import lu.itrust.business.TS.usermanagement.RoleType;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Component which allows to compute the values of dynamic parameters.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 2015
 */
@Component
@Transactional
public class DynamicParameterComputer {
	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private DAOParameter daoParameter;

	@Autowired
	private DAOParameterType daoParameterType;

	@Autowired
	private ServiceExternalNotification serviceExternalNotification;

	@Autowired
	private AssessmentManager assessmentManager;

	public DynamicParameterComputer() {
	}

	public DynamicParameterComputer(Session session, AssessmentManager assessmentManager) {
		this.daoAnalysis = new DAOAnalysisHBM(session);
		this.daoParameter = new DAOParameterHBM(session);
		this.daoParameterType = new DAOParameterTypeHBM(session);
		this.serviceExternalNotification = new ServiceExternalNotificationImpl(session);
		this.assessmentManager = assessmentManager;
	}

	/**
	 * Computes all dynamic parameters for all analyses for the given user.
	 * @param userName The name of the user to compute the dynamic parameters for.
	 */
	public void computeForAllAnalysesOfUser(String userName) throws Exception {
		// Fetch all analyses which the user can access
		List<Analysis> analyses = daoAnalysis.getFromUserNameAndNotEmpty(userName, AnalysisRight.highRightFrom(AnalysisRight.MODIFY));
		for (Analysis analysis : analyses) {
			this.computeForAnalysisAndSource(userName, analysis);
		}
	}

	/**
	 * Computes all dynamic parameters for the given analysis.
	 * @param analysis The analysis for which parameters shall be recomputed.
	 */
	public void computeForAnalysis(Analysis analysis) throws Exception {
		// Find all external sources (i.e. users) which provide dynamic parameters
		List<String> userNames = analysis.getUserRights().stream()
				.map(userRight -> userRight.getUser())
				.filter(user -> user.hasRole(RoleType.ROLE_IDS))
				.map(user -> user.getLogin())
				.collect(Collectors.toList());
		
		// Compute dynamic parameters
		for (String userName : userNames) {
			this.computeForAnalysisAndSource(userName, analysis);
		}
	}

	/**
	 * Computes all dynamic parameters for the given analysis and the given user.
	 * @param userName The name of the user to compute the dynamic parameters for.
	 * @param analysis The analysis for which parameters shall be recomputed.
	 */
	private void computeForAnalysisAndSource(String userName, Analysis analysis) throws Exception {
		// Fetch the 'DYNAMIC' parameter type or create it, if if does not exist yet/anymore
		ParameterType dynamicParameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME);
		if (dynamicParameterType == null) {
			dynamicParameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME);
			dynamicParameterType.setId(Constant.PARAMETERTYPE_TYPE_DYNAMIC);
		}

		// Log
		TrickLogManager.Persist(
				LogType.ANALYSIS,
				"log.analysis.compute.dynamicparameters",
				String.format("Updating dynamic parameters for analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()),
				userName,
				LogAction.UPDATE,
				analysis.getIdentifier(), analysis.getVersion());
		
		// Get parameters
		final double minimumProbability = Math.max(0.0, analysis.getParameter("p0"));

		/** The maximum timestamp for all notifications to consider. Points to NOW. */
		final long now = java.time.Instant.now().getEpochSecond();

		// Compute likelihoods
		Map<String, Double> likelihoods = serviceExternalNotification.computeProbabilitiesAtTime(now, userName, minimumProbability);

		// Fetch instances of all (existing) dynamic parameters
		// and map them by their acronym
		Map<String, DynamicParameter> dynamicParameters = analysis.findDynamicParametersByAnalysisAsMap();

		// Now every parameter has an associated likelihood value.
		// For each computed frequency:
		// - update existing dynamic parameters with the respective value in the frequencies collection; or
		// - create parameter if it does not exist.
		for (String parameterName : likelihoods.keySet()) {
			DynamicParameter parameter = dynamicParameters.get(parameterName);
			if (parameter == null) {
				parameter = new DynamicParameter();
				parameter.setAcronym(parameterName);
				// The description/label of dynamic parameters is never used within TRICK service,
				// we will set a value nevertheless to ease the work for a database maintainer. :-) 
				parameter.setDescription(String.format("dynamic:%s", parameterName));
				parameter.setType(dynamicParameterType);
				analysis.getParameters().add(parameter);
			}
			
			// Remove entry from parameter map so that we know it has been handled
			dynamicParameters.remove(parameterName);	

			// Set new parameter value to computed likelihood.
			// If the latter is not set, it exactly means that the likelihood is zero.
			// NB: the user is free to enforce a minimum value by using the max() function in his formula
			parameter.setValue(likelihoods.getOrDefault(parameterName, 0.0));
		}
		
		// Remove all parameters which are no longer needed
		// (these are all parameters which have not been removed from 'dynamicParameters')
		for (DynamicParameter dynamicParameter : dynamicParameters.values())
			daoParameter.delete(dynamicParameter);

		// Update assessment to reflect the new values of the dynamic parameters
		assessmentManager.UpdateAssessment(analysis);

		// Save everything
		daoAnalysis.saveOrUpdate(analysis);
	}
}
