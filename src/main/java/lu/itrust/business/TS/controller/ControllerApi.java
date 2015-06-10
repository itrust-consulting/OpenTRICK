package lu.itrust.business.TS.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.api.ApiExpression;
import lu.itrust.business.TS.model.api.ApiExternalNotification;
import lu.itrust.business.TS.model.api.ApiResult;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;
import lu.itrust.business.TS.model.parameter.DynamicParameterScope;
import lu.itrust.business.expressions.InvalidExpressionException;
import lu.itrust.business.expressions.StringExpressionParser;

import org.hibernate.NonUniqueResultException;
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

	@Autowired
	private ServiceParameter serviceParameter;

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
			// Determine the notification scope
			DynamicParameterScope scope;
			try {
				scope = serviceParameter.getDynamicParameterScopeByLabel(apiObj.getS());
			}
			catch (NonUniqueResultException ex) {
				throw new TrickException("error.api.unknwon_notification_scope", "Unknown notification scope: {0}", new Object[] { apiObj.getS() });
			}

			// Create a new entity based on given object
			ExternalNotification newObj = ExternalNotificationHelper.createEntityBasedOn(apiObj, scope);
			
			// Insert it into database
			serviceExternalNotification.save(newObj);
		}
		return 0;
	}
	
	/**
	 * Evaluates the given expression by plugging in the real-time values of the variables.
	 */
	@RequestMapping(value = "/eval", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public Object eval(@RequestBody ApiExpression data) throws Exception {
		// Verify data passed to API
		if (data.getTimespan() <= 0)
			throw new TrickException("error.api.timespan_negative", "Timespan must be positive.");
		if (data.getUnitDuration() <= 0)
			throw new TrickException("error.api.unit_duration_negative", "Unit duration must be positive.");
		
		// Read request data
		final long maxTimestamp = java.time.Instant.now().getEpochSecond(); // now
		final long minTimestamp = maxTimestamp - data.getTimespan(); // some time ago
		final long unitDuration = data.getUnitDuration();
		StringExpressionParser exprParser = new StringExpressionParser(data.getExpression());
		
		try {
			// Compute frequencies for all involved variables
			Collection<String> variablesInvolved = exprParser.getInvolvedVariables();
			Map<String, Double> variableValues = serviceExternalNotification.getFrequencies(variablesInvolved, minTimestamp, maxTimestamp, unitDuration);

			/*
			System.out.println("variablesInvolved:");
			for (String k:variablesInvolved)
				System.out.println("- " + k);
			System.out.println("end.");

			System.out.println("variableValues:");
			for (String k:variableValues.keySet())
				System.out.println("- " + k + " => " + variableValues.get(k));
			System.out.println("end.");
			//*/
			
			// Evaluate expression itself
			double value = exprParser.evaluate(variableValues);
			// Do something with it
			return value;
		}
		catch (InvalidExpressionException ex) {
			throw new TrickException("error.api.invalid_expression", "Invalid expression: syntax error.");
		}
	}
}
