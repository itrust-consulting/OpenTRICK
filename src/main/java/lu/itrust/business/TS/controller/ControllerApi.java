package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeDynamicParameters;
import lu.itrust.business.TS.component.DynamicParameterComputer;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.api.ApiExpressionRequest;
import lu.itrust.business.TS.model.api.ApiExternalNotification;
import lu.itrust.business.TS.model.api.ApiNotifyRequest;
import lu.itrust.business.TS.model.api.ApiResult;
import lu.itrust.business.TS.model.externalnotification.ExternalNotificationOccurrence;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;
import lu.itrust.business.expressions.InvalidExpressionException;
import lu.itrust.business.expressions.StringExpressionParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Value("${app.settings.dynamicparameters.computationdelayseconds}")
	private Integer computationDelayInSeconds;

	@Autowired
	private DynamicParameterComputer dynamicParameterComputer;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	private WorkersPoolManager poolManager;

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
	public Object home(Principal principal) {
		if (principal == null)
			return new ApiResult(0, "<not logged in>");
		else
			return new ApiResult(0, principal.getName());
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
	public Object notify(HttpSession session, Principal principal, @RequestBody ApiNotifyRequest request) throws Exception {
		// For each of the given external notifications
		for (ApiExternalNotification apiObj : request.getData()) {
			// Create entity and insert it into database
			serviceExternalNotification.save(ExternalNotificationHelper.createEntityBasedOn(apiObj));
		}

		// Trigger execution of worker which computes dynamic parameters.
		// This method only schedules the task if it does not have been scheduled yet for the given user.
		WorkerComputeDynamicParameters.trigger(principal.getName(), computationDelayInSeconds, dynamicParameterComputer, taskScheduler, poolManager);

		// Success
		return new ApiResult(0);
	}

	/**
	 * Lists all the external notifications in the database.
	 * Used for debugging purposes; this method will be removed in the production version.
	 * THIS IS A DEBUG METHOD WHICH DOES NOT PERFORM ACCESS RIGHT VERIFICATION.
	 */
	@RequestMapping(value = "/list", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public Object list() throws Exception {
		// Fetch all entities from the database
		// and convert them to exportable API objects
		return ExternalNotificationHelper.convertList(serviceExternalNotification.getAll());		
	}
	
	/**
	 * Evaluates the given expression by plugging in the real-time values of the variables.
	 * Used for debugging purposes; this method will be removed in the production version.
	 * THIS IS A DEBUG METHOD WHICH DOES NOT PERFORM ACCESS RIGHT VERIFICATION.
	 */
	@RequestMapping(value = "/eval", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public Object eval(@RequestBody ApiExpressionRequest data) throws Exception {
		// Verify data passed to API
		if (data.getTimespan() <= 0)
			throw new TrickException("error.api.timespan_negative", "Timespan must be positive.");
		if (data.getUnitDuration() <= 0)
			throw new TrickException("error.api.unit_duration_negative", "Unit duration must be positive.");
		
		// Read request data
		final long maxTimestamp = java.time.Instant.now().getEpochSecond(); // now
		final long minTimestamp = maxTimestamp - data.getTimespan(); // some time ago
		final double unitDuration = data.getUnitDuration();
		final double timespanInUnits = data.getTimespan() / unitDuration;
		StringExpressionParser exprParser = new StringExpressionParser(data.getExpression());
		
		try {
			// Compute frequencies for all involved variables
			Collection<String> variablesInvolved = exprParser.getInvolvedVariables();
			Map<String, List<ExternalNotificationOccurrence>> occurrencesByCategory = serviceExternalNotification.getOccurrences(variablesInvolved, minTimestamp, maxTimestamp);
			Map<String, Double> variableValues = ExternalNotificationHelper.computeLikelihoods(occurrencesByCategory, timespanInUnits, new HashMap<>());

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
