package lu.itrust.business.ts.model.externalnotification.helper;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.api.ApiExternalNotification;
import lu.itrust.business.ts.model.externalnotification.ExternalNotification;
import lu.itrust.business.ts.model.externalnotification.ExternalNotificationType;
import lu.itrust.business.expressions.StringExpressionHelper;

/**
 * Provides helper functionality for external notification instances.
 */
public class ExternalNotificationHelper {
	/**
	 * Creates a new database entity for the given external notification.
	 * @param apiObj The object which has been obtained via an API call.
	 * @param userName The user name of the reporting user.
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
		modelObj.setType(ExternalNotificationType.RELATIVE);
		modelObj.setSeverity(apiObj.getS());
		modelObj.setSourceUserName(userName);
		return modelObj;
	}
	
	/**
	 * Deduces the name of the dynamic parameter associated to an external notification.
	 * @param sourceUserName The user name of the reporting user.
	 * @param category The category of the external notification.
	 */
	public static String createParameterName(String sourceUserName, String category) {
		return StringExpressionHelper.makeValidVariable(String.format("%s_%s", sourceUserName, category));
	}
}
