package lu.itrust.business.TS.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOParameter;
import lu.itrust.business.TS.database.dao.DAOParameterType;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.helper.AssessmentManager;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.parameter.ParameterType;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.expressions.StringExpressionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
	
	/**
	 * Computes all dynamic parameters for all analyses for the given user.
	 * @param userName The name of the user to compute the dynamic parameters for.
	 */
	public void computeForAllAnalysesOfUser(String userName) throws Exception {
		// Prefix all dynamic parameters with the user name
		final String prefix = userName + "_";
		
		// Fetch all analyses which the user can access
		List<Analysis> analyses = daoAnalysis.getFromUserNameAndNotEmpty(userName, AnalysisRight.highRightFrom(AnalysisRight.MODIFY));
		for (Analysis analysis : analyses) {
			this.computeForAnalysisAndSource(userName, prefix, analysis);
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
			this.computeForAnalysisAndSource(userName, userName + "_", analysis);
		}
	}

	/**
	 * Computes all dynamic parameters for the given analysis and the given user.
	 * @param userName The name of the user to compute the dynamic parameters for.
	 * @param prefix The prefix to be added in front of the name of each parameter.
	 * @param analysis The analysis for which parameters shall be recomputed.
	 */
	private void computeForAnalysisAndSource(String userName, String prefix, Analysis analysis) throws Exception {
		// Fetch the 'DYNAMIC' parameter type or create it, if if does not exist yet/anymore
		ParameterType dynamicParameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME);
		if (dynamicParameterType == null) {
			dynamicParameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME);
			dynamicParameterType.setId(Constant.PARAMETERTYPE_TYPE_DYNAMIC);
		}

		// Fetch the 'SEVERITY' parameter type or create it, if if does not exist yet/anymore
		ParameterType severityParameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_SEVERITY_NAME);
		if (severityParameterType == null) {
			severityParameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_SEVERITY_NAME);
			severityParameterType.setId(Constant.PARAMETERTYPE_TYPE_SEVERITY);
		}

		/**
		 * All frequencies within TRICK service are to be understood with respect to 1 year.
		 * 1 year is defined here to be 365 days, which is not entirely correct,
		 * but the value does not have to be that precise anyway.
		 */
		final double unitDuration = 86400 * 365;

		// Log
		TrickLogManager.Persist(
				LogType.ANALYSIS,
				"log.analysis.compute.dynamicparameters",
				String.format("Updating dynamic parameters for analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()),
				userName,
				LogAction.UPDATE,
				analysis.getIdentifier(), analysis.getVersion());
		
		// Get all parameters (in particular those which define the severity probabilities)
		Map<String, Double> allParameterValues = new HashMap<>();
		for (Parameter parameter : analysis.getParameters()) {
			allParameterValues.put(parameter.getDescription(), parameter.getValue());
		}
		Map<Integer, Double> severityProbabilities = new HashMap<>();
		for (ExtendedParameter parameter : analysis.getOrCreateSeverityParameters(severityParameterType)) {
			severityProbabilities.put(parameter.getLevel(), parameter.getValue());
		}
		
		/** The time span over which all notifications shall be considered in the computation of the dynamic parameter. */
		final long timespan = (long)(double)allParameterValues.getOrDefault(Constant.PARAMETER_DYNAMIC_PARAMETER_AGGREGATION_TIMESPAN, (double)Constant.DEFAULT_DYNAMIC_PARAMETER_AGGREGATION_TIMESPAN);

		/** The minimum timestamp for all notifications to consider. Points to NOW. */
		final long maxTimestamp = java.time.Instant.now().getEpochSecond();

		/** The maximum timestamp for all notifications to consider. */
		final long minTimestamp = maxTimestamp - timespan;
		
		/**
		 * The time span (in abstract units) which the notification occurrences have been taken from.
		 * Regarding abstract units: the returned likelihood values are to be understood as 'expected number of times an incident occurs in an abstract unit'.
		 * For instance, if the abstract time unit is 1 year, and notifications have been taken from 1 month, then 'timespanInUnits'
		 * should equal 1/12. The returned likelihood values represent the 'expected number of times per year'.
		 */
		final double timespanInUnits = timespan / unitDuration;
		
		// Compute likelihoods
		Map<String, Double> likelihoods = ExternalNotificationHelper.computeLikelihoods(serviceExternalNotification.getOccurrences(minTimestamp, maxTimestamp, userName), timespanInUnits, severityProbabilities);

		// Fetch instances of all (existing) dynamic parameters
		// and map them by their acronym
		Map<String, DynamicParameter> dynamicParameters = analysis.findDynamicParametersByAnalysisAsMap();
		
		// Make sure that there is a likelihood value for each parameter.
		// If there is none, it exactly means that the likelihood is zero.
		for (String key : dynamicParameters.keySet())
			if (key.startsWith(prefix))
				likelihoods.putIfAbsent(key.substring(prefix.length()), 0.0);

		// Now every parameter has an associated likelihood value.
		// For each computed frequency:
		// - update existing dynamic parameters with the respective value in the frequencies collection; or
		// - create parameter if it does not exist.
		for (String key : likelihoods.keySet()) {
			String parameterName = StringExpressionHelper.makeValidVariable(prefix + key);
			DynamicParameter parameter = dynamicParameters.get(parameterName);
			if (parameter == null) {
				parameter = new DynamicParameter();
				parameter.setAcronym(parameterName);
				// The description/label of dynamic parameters is never used within TRICK service,
				// we will set a value nevertheless to ease the work for a database maintainer. :-) 
				parameter.setDescription(String.format("dynamic:%s:%s", userName, key));
				parameter.setType(dynamicParameterType);
				analysis.getParameters().add(parameter);
			}

			// TODO Consider using a minimum value?
			parameter.setValue(likelihoods.get(key));
		}

		// Update assessment to reflect the new values of the dynamic parameters
		assessmentManager.UpdateAssessment(analysis);

		// Save everything
		daoAnalysis.saveOrUpdate(analysis);
	}
}
