package lu.itrust.business.TS.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOParameter;
import lu.itrust.business.TS.database.dao.DAOParameterType;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.parameter.ParameterType;

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
	
	/**
	 * Computes all dynamic parameters for all analyses for the given user.
	 * @param userName The name of the user to compute the dynamic parameters for.
	 */
	public void computeForAllAnalysesOfUser(String userName) throws Exception {
		// Fetch all analyses which the user can access
		List<Analysis> analyses = daoAnalysis.getFromUserNameAndNotEmpty("admin", AnalysisRight.highRightFrom(AnalysisRight.MODIFY));

		// Fetch the 'DYNAMIC' parameter type or create it, if if does not exist yet/anymore
		ParameterType dynamicParameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME);
		if (dynamicParameterType == null) {
			dynamicParameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME);
			dynamicParameterType.setId(Constant.PARAMETERTYPE_TYPE_DYNAMIC);
		}

		/**
		 * All frequencies within TRICK service are to be understood with respect to 1 year.
		 * 1 year is defined here to be 365 days, which is not entirely correct,
		 * but the value does not have to be that precise anyway.
		 */
		final double unitDuration = 86400 * 365;

		// Deduce dynamic parameter values for each analysis
		for (Analysis analysis : analyses) {
			// Get all parameters (in particular those which define the severity probabilities)
			Map<String, Double> allParameterValues = new HashMap<>();
			for (Parameter parameter : analysis.getParameters())
				allParameterValues.put(parameter.getDescription(), parameter.getValue());
			
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
			Map<String, Double> likelihoods = ExternalNotificationHelper.computeLikelihoods(serviceExternalNotification.getOccurrences(minTimestamp, maxTimestamp), timespanInUnits, allParameterValues);

			// Fetch instances of all (existing) dynamic parameters
			// and map them by their acronym
			Map<String, DynamicParameter> dynamicParameters = analysis.findDynamicParametersByAnalysisAsMap();
			
			// Make sure that there is a likelihood value for each parameter.
			// If there is none, it exactly means that the likelihood is zero.
			for (String acronym : dynamicParameters.keySet())
				likelihoods.putIfAbsent(acronym, 0.0);

			// Now every parameter has an associated likelihood value.
			// For each computed frequency:
			// - update existing dynamic parameters with the respective value in the frequencies collection; or
			// - create parameter if it does not exist.
			for (String acronym : likelihoods.keySet()) {
				DynamicParameter newParameter = dynamicParameters.get(acronym);
				if (newParameter == null) {
					newParameter = new DynamicParameter();
					newParameter.setAcronym(acronym);
					newParameter.setDescription("dynamic:" + acronym); // we won't need this, it just looks nice
					newParameter.setType(dynamicParameterType);
					analysis.getParameters().add(newParameter);
				}
				
				// TODO Consider using a minimum value?
				newParameter.setValue(likelihoods.get(acronym));
			}

			// Save everything
			daoAnalysis.saveOrUpdate(analysis);
		}
		
	}
}
