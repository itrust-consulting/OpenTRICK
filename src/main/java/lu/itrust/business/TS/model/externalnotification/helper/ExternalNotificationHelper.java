package lu.itrust.business.TS.model.externalnotification.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.api.ApiExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotificationOccurrence;

/**
 * Provides helper functionality for external notification instances.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
public class ExternalNotificationHelper {
	/**
	 * Creates a new database entity for the given external notification.
	 * @param apiObj The object which has been obtained via an API call.
	 * @param objScope The database entity representing the notification scope specified in the 'apiObj' parameter.
	 * This is necessary because 'apiObj' only specified the _label_ of the scope, not the full object.
	 * The caller of this method must assure that 'objScope' is really the right object - the value of apiObj.getScope() is silently ignored.
	 * @return Returns the created entity.
	 * @throws TrickException
	 */
	public static ExternalNotification createEntityBasedOn(ApiExternalNotification apiObj, String userName) throws TrickException {
		ExternalNotification modelObj = new ExternalNotification();
		// Copy all properties from API object to a new entity
		modelObj.setCategory(apiObj.getC());
		modelObj.setTimestamp(apiObj.getT());
		modelObj.setNumber(apiObj.getN());
		modelObj.setSeverity(apiObj.getS());
		modelObj.setSourceUserName(userName);
		return modelObj;
	}

	/**
	 * Converts a list of ExternalNotification entities to an list of exportable API objects. 
	 * @param list The list of database entities.
	 * @return Returns a list of API objects.
	 */
	public static List<ApiExternalNotification> convertList(List<ExternalNotification> list) {
		ArrayList<ApiExternalNotification> apiList = new ArrayList<ApiExternalNotification>();
		for (ExternalNotification obj : list) {
			ApiExternalNotification apiObj = new ApiExternalNotification();
			// Copy all relevant properties from entity to API object
			// We silently omit the unique identifier here
			apiObj.setC(obj.getCategory());
			apiObj.setT(obj.getTimestamp());
			apiObj.setN(obj.getNumber());
			apiObj.setS(obj.getSeverity());
			apiList.add(apiObj);
		}
		return apiList;
	}
	
	/**
	 * Computes, for each category, the likelihood that an incident of such a category occurs.
	 * @param occurrencesByCategory The list of notification occurrences, grouped by category, over a certain time period.
	 * @param timespanInUnits The time span (in abstract units) which the notification occurrences have been taken from.
	 * Regarding abstract units: the returned likelihood values are to be understood as 'expected number of times an incident occurs in an abstract unit'.
	 * For instance, if the abstract time unit is 1 year, and notifications have been taken from 1 month, then 'timespanInUnits'
	 * should equal 1/12. The returned likelihood values represent the 'expected number of times per year'.
	 * @param severityProbabilities A map containing at least the parameters defining the severity probability for all possible levels.
	 * The keys of the map correspond to the severity level. 
	 * @return A map assigning a likelihood value to each incident category. The likelihood values are with respect to the abstract
	 * time unit (see description of 'timespanInUnits' parameter). 
	 */
	public static Map<String, Double> computeLikelihoods(Map<String, List<ExternalNotificationOccurrence>> occurrencesByCategory, double timespanInUnits, Map<Integer, Double> severityProbabilities) {
		Map<String, Double> likelihoods = new HashMap<>(occurrencesByCategory.size());
		for (String key : occurrencesByCategory.keySet()) {
			// Iterate over all occurrence objects and compute overall likelihood.
			// The sum is weighted by the probability (0 <= p <= 1) of occurrence, associated to the severity. 
			double likelihood = 0.0;
			for (ExternalNotificationOccurrence occurrence : occurrencesByCategory.get(key))
				likelihood += occurrence.getOccurrence() / timespanInUnits * getSeverityProbability(occurrence.getSeverity(), severityProbabilities);
			likelihoods.put(key, likelihood);
		}
		return likelihoods;
	}

	/**
	 * Gets the probability that an incident of the given severity occurs, given that a respective anomaly has been detected.
	 * Indeed, an anomaly/intrusion of low severity has a much lower chance to have any impact.
	 * @param level The severity level of the incident. Must be in the range [EXTERNAL_NOTIFICATION_MIN_SEVERITY, EXTERNAL_NOTIFICATION_MAX_SEVERITY].
	 * @param severityProbabilities A map containing at least the parameters defining the severity probability for all possible levels.
	 * The keys of the map correspond to the severity level. 
	 * @return Returns a probability value in the range [0.0, 1.0].
	 */
	public static double getSeverityProbability(int level, Map<Integer, Double> severityProbabilities) {
		if (level < Constant.EXTERNAL_NOTIFICATION_MIN_SEVERITY) return 0.0;
		if (level > Constant.EXTERNAL_NOTIFICATION_MAX_SEVERITY) return 1.0;

		// Find the corresponding parameter
		Double parameterValue = severityProbabilities.get(level);
		if (parameterValue != null)
			return parameterValue;

		// If it cannot be found, use the default value.
		return getDefaultSeverityProbability(level);
	}

	/**
	 * Gets the default severity probability value to initialize the respective parameter with.
	 * @param level The severity level of the incident. Must be in the range [EXTERNAL_NOTIFICATION_MIN_SEVERITY, EXTERNAL_NOTIFICATION_MAX_SEVERITY].
	 * @return Returns a probability value in the range [0.0, 1.0].
	 */
	public static double getDefaultSeverityProbability(int level) {
		if (level < Constant.EXTERNAL_NOTIFICATION_MIN_SEVERITY) return 0.0;
		if (level > Constant.EXTERNAL_NOTIFICATION_MAX_SEVERITY) return 1.0;

		// Use an exponential formula to deduce a probability
		double prob = Math.exp((level - Constant.EXTERNAL_NOTIFICATION_MAX_SEVERITY) * Math.log(2));
		// Round to 4 decimals
		return Math.round(prob * 10000.0) / 10000.0;
	}
}
