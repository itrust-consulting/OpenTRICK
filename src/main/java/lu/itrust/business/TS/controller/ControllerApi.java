package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.text.MessageFormat;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeDynamicParameters;
import lu.itrust.business.TS.component.DynamicParameterComputer;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.api.ApiExternalNotification;
import lu.itrust.business.TS.model.api.ApiNotifyRequest;
import lu.itrust.business.TS.model.api.ApiParameterSetter;
import lu.itrust.business.TS.model.api.ApiResult;
import lu.itrust.business.TS.model.api.ApiSetParameterRequest;
import lu.itrust.business.TS.model.api.model.ApiAsset;
import lu.itrust.business.TS.model.api.model.ApiMeasure;
import lu.itrust.business.TS.model.api.model.ApiNamable;
import lu.itrust.business.TS.model.api.model.ApiRRF;
import lu.itrust.business.TS.model.api.model.ApiStandard;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.rrf.RRF;
import lu.itrust.business.TS.model.standard.AnalysisStandard;

/**
 * ControllerApi.java: <br>
 * This controller is responsible for accepting external notifications which
 * serve as risk indicator (such as IDS alerts). From these the probabilities
 * that certain events happen, are deduced and stored in variables ready to be
 * used within the TRICK service user interface (asset/scenario estimation).
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 4, 2015
 */
@Controller
@RequestMapping("/Api")
@ResponseBody
public class ControllerApi {
	@Autowired
	private ServiceExternalNotification serviceExternalNotification;

	@Value("${app.settings.dynamicparameters.computationdelayseconds}")
	private Integer computationDelayInSeconds;

	@Autowired
	private DynamicParameterComputer dynamicParameterComputer;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	private WorkersPoolManager poolManager;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	/**
	 * Method is called whenever an exception of type TrickException is thrown
	 * in this controller.
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
	 * This method is responsible for accepting external notifications which
	 * serve as risk indicator (such as IDS alerts). From these, the
	 * probabilities that certain events happen, are deduced and stored in
	 * variables ready to be used within the TRICK service user interface
	 * (asset/scenario estimation).
	 * 
	 * @param data
	 *            One or multiple notifications sent to TRICK Service.
	 * @return Returns an error code (0 = success).
	 * @throws Exception
	 */
	@RequestMapping(value = "/notify", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	public Object notify(HttpSession session, Principal principal, @RequestBody ApiNotifyRequest request) throws Exception {
		String userName = principal.getName();

		for (ApiExternalNotification apiObj : request.getData())
			serviceExternalNotification.save(ExternalNotificationHelper.createEntityBasedOn(apiObj, userName));

		// Trigger execution of worker which computes dynamic parameters.
		// This method only schedules the task if it does not have been
		// scheduled yet for the given user.
		WorkerComputeDynamicParameters.trigger(userName, computationDelayInSeconds, dynamicParameterComputer, taskScheduler, poolManager);

		// Success
		return new ApiResult(0);
	}

	/**
	 * This method is responsible for accepting requests that set the value of a
	 * dynamic parameter ready to be used within the TRICK service user
	 * interface (asset/scenario estimation).
	 * 
	 * @param data
	 *            One or multiple notifications sent to TRICK Service.
	 * @return Returns an error code (0 = success).
	 * @throws Exception
	 */
	@RequestMapping(value = "/set", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	public Object set(HttpSession session, Principal principal, @RequestBody ApiSetParameterRequest request) throws Exception {
		String userName = principal.getName();

		for (ApiParameterSetter apiObj : request.getData())
			serviceExternalNotification.save(ExternalNotificationHelper.createEntityBasedOn(apiObj, userName));

		// Trigger execution of worker which computes dynamic parameters.
		// This method only schedules the task if it does not have been
		// scheduled yet for the given user.
		//
		// Note that we cannot set the value directly because we do not know
		// whether there are other parameter setters
		// or external notifications in the database which also impact the value
		// of the parameter.
		WorkerComputeDynamicParameters.trigger(userName, computationDelayInSeconds, dynamicParameterComputer, taskScheduler, poolManager);

		// Success
		return new ApiResult(0);
	}

	/**
	 * Load RRF for a set of measure
	 * 
	 * @param idAnalysis
	 * @param idAsset
	 * @param idScenario
	 * @param standardNames
	 * @param principal
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/load-rrf", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	public void loadRRF(@RequestParam(name = "analysisId") Integer idAnalysis, @RequestParam(name = "assetId") Integer idAsset,
			@RequestParam(name = "scenarioId") Integer idScenario, @RequestParam(name = "standards") String standard, Principal principal, HttpServletResponse response)
			throws Exception {
		String[] standardNames = standard.split(",");
		if (standardNames.length == 0)
			throw new TrickException("error.standards.empty", "Standard cannot be empty");

		Analysis analysis = serviceAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (!analysis.isUserAuthorized(principal.getName(), AnalysisRight.EXPORT))
			throw new TrickException("error.403.access.denied", "You do not have the necessary permissions to perform this action");
		Map<String, AnalysisStandard> analysisStandards = analysis.getAnalysisStandards().stream()
				.collect(Collectors.toMap(analysisStandard -> analysisStandard.getStandard().getLabel(), Function.identity()));
		Assessment assessment = analysis.getAssessments().stream()
				.filter(assessment1 -> assessment1.getAsset().getId() == idAsset && assessment1.getScenario().getId() == idScenario).findAny()
				.orElseThrow(() -> new TrickException("error.assessment.not_found", "Assessment cannot be found"));
		ApiRRF apiRRF = new ApiRRF(idAnalysis, assessment.getImpactReal(), assessment.getLikelihoodReal());
		apiRRF.setScenario(new ApiNamable(assessment.getScenario().getId(), assessment.getScenario().getName()));
		apiRRF.setAsset(new ApiAsset(assessment.getAsset().getId(), assessment.getAsset().getName(), assessment.getAsset().getValue()));
		Parameter rrfTuning = analysis.findParameterByTypeAndDescription(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
		Map<String, Double> dynamicParameters = analysis.getParameters().stream().filter(parameter -> (parameter instanceof DynamicParameter))
				.map(parameter -> (DynamicParameter) parameter).collect(Collectors.toMap(DynamicParameter::getAcronym, DynamicParameter::getValue));
		for (String name : standardNames) {
			AnalysisStandard analysisStandard = analysisStandards.get(name);
			if (analysisStandard == null)
				throw new TrickException("error.standard.not_found", "Standard cannot be found");
			ApiStandard apiStandard = new ApiStandard(analysisStandard.getStandard().getId(), analysisStandard.getStandard().getLabel());
			analysisStandard.getMeasures().stream().filter(measure -> measure.getMeasureDescription().isComputable()).forEach(measure -> {
				apiStandard.getMeasures().add(new ApiMeasure(measure.getId(), measure.getMeasureDescription().getReference(),
						(int) measure.getImplementationRateValue(dynamicParameters), measure.getCost(), RRF.calculateRRF(assessment, rrfTuning, measure)));
			});
			apiRRF.getStandards().add(apiStandard);
		}
		response.setContentType("json");
		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"rrf.json\"");
		new ObjectMapper().writeValue(response.getOutputStream(), apiRRF);

	}
}
