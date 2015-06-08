package lu.itrust.business.TS.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.api.ApiExternalNotification;
import lu.itrust.business.TS.model.api.ApiResult;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ControllerApi.java: <br>
 *     This controller is responsible for accepting external notifications
 *     which serve as risk indicator (such as IDS alerts). From these the
 *     probabilities that certain events happen, are deduced and stored in
 *     variables ready to be used within the TRICK service user interface
 *     (asset/scenario estimation).
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 4, 2015
 */
@Controller
@RequestMapping("/Api")
@ResponseBody
public class ControllerApi {
	@Autowired
	private ServiceExternalNotification serviceExternalNotification;

	/**
	 * Method is called whenever an exception of type TrickException
	 * is thrown in this controller.
	 */
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	@ExceptionHandler(TrickException.class)
	private Object handleTrickException(Exception ex) {
		// Return the error message to the client (JSON).
		// -1 denotes general errors.
		return new ApiResult(-1, ex.getMessage());
	}

	/**
	 * Home of the API. Always returns success.
	 */
	@RequestMapping
	public Object home() {
		return new ApiResult(0);
	}
	
	/**
	 * Lists all the external notifications in the database.
	 * Used for debugging purposes; this method will be removed in the production version.
	 */
	@RequestMapping(value = "/list", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public Object list() throws Exception {
		// Fetch all entities from the database
		// and convert them to exportable API objects
		return ExternalNotificationHelper.convertList(serviceExternalNotification.getAll());		
	}
	
	/**
	 * This method is responsible for accepting external notifications
	 * which serve as risk indicator (such as IDS alerts). From these, the
	 * probabilities that certain events happen, are deduced and stored in
	 * variables ready to be used within the TRICK service user interface
	 * (asset/scenario estimation).
	 * @param data One or multiple notifications sent to TRICK Service.
	 * @return Returns an error code (0 = success).
	 * @throws Exception 
	 */
	@RequestMapping(value = "/notify", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	public Object notify(@RequestBody List<ApiExternalNotification> data) throws Exception {
		// For each of the given external notifications
		for (ApiExternalNotification apiObj : data) {
			// Create a new entity based on given object
			ExternalNotification newObj = ExternalNotificationHelper.createEntityBasedOn(apiObj);
			
			// Insert it into database
			serviceExternalNotification.save(newObj);
		}
		return 0;
	}
	
	@RequestMapping(value = "/test", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public Object test() throws Exception {
		String expr = "some_event";
		List<String> variablesInvolved = new ArrayList<String>();
		variablesInvolved.add(expr);
		
		final long timespan = 30 * 24 * 60 * 60; // 30 days
		final long maxTimestamp = java.time.Instant.now().getEpochSecond(); // now
		final long minTimestamp = maxTimestamp - timespan;
		final double secondsPerYear = 365 * 24 * 60 * 60;
		
		// Get all notifications from the past month
		Map<String, Double> count = serviceExternalNotification.getFrequencies(variablesInvolved, minTimestamp, maxTimestamp, secondsPerYear);
		return count;
	}
}
