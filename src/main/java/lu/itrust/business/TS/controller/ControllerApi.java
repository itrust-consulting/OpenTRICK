package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.text.MessageFormat;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeDynamicParameters;
import lu.itrust.business.TS.component.DynamicParameterComputer;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.api.ApiExternalNotification;
import lu.itrust.business.TS.model.api.ApiNotifyRequest;
import lu.itrust.business.TS.model.api.ApiParameterSetter;
import lu.itrust.business.TS.model.api.ApiResult;
import lu.itrust.business.TS.model.api.ApiSetParameterRequest;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;

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
	private Object handleTrickException(TrickException ex) {
		// Return the error message to the client (JSON).
		// -1 denotes general errors.
		return new ApiResult(-1, MessageFormat.format(ex.getMessage(), ex.getParameters()));
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
		String userName = principal.getName();

		for (ApiExternalNotification apiObj : request.getData())
			serviceExternalNotification.save(ExternalNotificationHelper.createEntityBasedOn(apiObj, userName));

		// Trigger execution of worker which computes dynamic parameters.
		// This method only schedules the task if it does not have been scheduled yet for the given user.
		WorkerComputeDynamicParameters.trigger(userName, computationDelayInSeconds, dynamicParameterComputer, taskScheduler, poolManager);

		// Success
		return new ApiResult(0);
	}
	
	/**
	 * This method is responsible for accepting requests that set the value of a
	 * dynamic parameter ready to be used within the TRICK service user interface
	 * (asset/scenario estimation).
	 * @param data One or multiple notifications sent to TRICK Service.
	 * @return Returns an error code (0 = success).
	 * @throws Exception 
	 */
	@RequestMapping(value = "/set", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	public Object set(HttpSession session, Principal principal, @RequestBody ApiSetParameterRequest request) throws Exception {
		String userName = principal.getName();

		for (ApiParameterSetter apiObj : request.getData())
			serviceExternalNotification.save(ExternalNotificationHelper.createEntityBasedOn(apiObj, userName));

		// Trigger execution of worker which computes dynamic parameters.
		// This method only schedules the task if it does not have been scheduled yet for the given user.
		//
		// Note that we cannot set the value directly because we do not know whether there are other parameter setters
		// or external notifications in the database which also impact the value of the parameter.
		WorkerComputeDynamicParameters.trigger(userName, computationDelayInSeconds, dynamicParameterComputer, taskScheduler, poolManager);

		// Success
		return new ApiResult(0);
	}
}
