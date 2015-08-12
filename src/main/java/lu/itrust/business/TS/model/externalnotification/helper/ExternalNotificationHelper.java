package lu.itrust.business.TS.model.externalnotification.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.api.ApiExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;

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
		modelObj.setHalfLife(apiObj.getH());
		modelObj.setNumber(apiObj.getN());
		modelObj.setAssertiveness(apiObj.getA());
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
			apiObj.setH(obj.getHalfLife());
			apiObj.setN(obj.getNumber());
			apiObj.setA(obj.getAssertiveness());
			apiObj.setS(obj.getSeverity());
			apiList.add(apiObj);
		}
		return apiList;
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
