package lu.itrust.business.TS.model.externalnotification.helper;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.api.ApiExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.parameter.DynamicParameterScope;

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
	public static ExternalNotification createEntityBasedOn(ApiExternalNotification apiObj, DynamicParameterScope objScope) throws TrickException {
		ExternalNotification modelObj = new ExternalNotification();
		// Copy all properties from API object to a new entity
		modelObj.setCategory(apiObj.getC());
		modelObj.setScope(objScope);
		modelObj.setTimestamp(apiObj.getT());
		modelObj.setNumber(apiObj.getN());
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
			apiObj.setS(obj.getScope().getLabel());
			apiObj.setT(obj.getTimestamp());
			apiObj.setN(obj.getNumber());
			apiList.add(apiObj);
		}
		return apiList;
	}
}
