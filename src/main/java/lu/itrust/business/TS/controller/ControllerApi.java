package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeDynamicParameters;
import lu.itrust.business.TS.component.DynamicParameterComputer;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.database.service.ServiceIDS;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.api.ApiExternalNotification;
import lu.itrust.business.TS.model.api.ApiNotifyRequest;
import lu.itrust.business.TS.model.api.ApiParameterSetter;
import lu.itrust.business.TS.model.api.ApiResult;
import lu.itrust.business.TS.model.api.ApiSetParameterRequest;
import lu.itrust.business.TS.model.api.basic.ApiAsset;
import lu.itrust.business.TS.model.api.basic.ApiMeasure;
import lu.itrust.business.TS.model.api.basic.ApiNamable;
import lu.itrust.business.TS.model.api.basic.ApiRRF;
import lu.itrust.business.TS.model.api.basic.ApiStandard;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.rrf.RRF;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.usermanagement.IDS;

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
	private ThreadPoolTaskScheduler scheduler;

	@Autowired
	private WorkersPoolManager poolManager;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceIDS serviceIDS;

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
	@Transactional
	@RequestMapping(value = "/ids/notify", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	public Object notify(HttpSession session, Principal principal, @RequestBody ApiNotifyRequest request) throws Exception {

		IDS ids = serviceIDS.get(principal.getName()).notifyAlert();

		for (ApiExternalNotification apiObj : request.getData())
			serviceExternalNotification.save(ExternalNotificationHelper.createEntityBasedOn(apiObj, ids.getPrefix()));

		// Trigger execution of worker which computes dynamic parameters.
		// This method only schedules the task if it does not have been
		// scheduled yet for the given user.
		WorkerComputeDynamicParameters.trigger(ids.getPrefix(), computationDelayInSeconds, dynamicParameterComputer, scheduler, poolManager);

		serviceIDS.saveOrUpdate(ids);

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
	@Transactional
	@RequestMapping(value = "/ids/set", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	public Object set(HttpSession session, Principal principal, @RequestBody ApiSetParameterRequest request) throws Exception {

		IDS ids = serviceIDS.get(principal.getName()).notifyUpdate();

		for (ApiParameterSetter apiObj : request.getData())
			serviceExternalNotification.save(ExternalNotificationHelper.createEntityBasedOn(apiObj, ids.getPrefix()));

		// Trigger execution of worker which computes dynamic parameters.
		// This method only schedules the task if it does not have been
		// scheduled yet for the given user.
		//
		// Note that we cannot set the value directly because we do not know
		// whether there are other parameter setters
		// or external notifications in the database which also impact the value
		// of the parameter.
		WorkerComputeDynamicParameters.trigger(ids.getPrefix(), computationDelayInSeconds, dynamicParameterComputer, scheduler, poolManager);

		serviceIDS.saveOrUpdate(ids);
		// Success
		return new ApiResult(0);
	}

	@RequestMapping(value = "/data/customers", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	public @ResponseBody Object loadUserCustomer(Principal principal, Locale locale) throws Exception {
		return serviceCustomer.getAllNotProfileOfUser(principal.getName()).stream().map(customer -> new ApiNamable(customer.getId(), customer.getOrganisation()))
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/data/analysis/all", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	public @ResponseBody Object loadAnalyses(@RequestParam(name = "customerId") Integer idCustomer, Principal principal, Locale locale) {
		if (idCustomer == null)
			throw new TrickException("error.custmer.null", "Customer id cannot be empty");
		return serviceAnalysis.getIdentifierAndNameByUserAndCustomer(principal.getName(), idCustomer).stream().map(analysis -> new ApiNamable(analysis[0], analysis[1].toString()))
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/data/analysis/versions", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	public @ResponseBody Object loadAnalysesVersion(@RequestParam(name = "customerId") Integer idCustomer, @RequestParam(name = "identifier") String identifier,
			Principal principal, Locale locale) {
		if (idCustomer == null)
			throw new TrickException("error.custmer.null", "Customer id cannot be empty");
		if (identifier == null)
			throw new TrickException("error.analysis.identifier", "Identifier cannot be empty");
		Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), idCustomer);
		if (customer == null)
			throw new TrickException("error.custmer.not_found", "Customer cannot be found");
		return serviceAnalysis.getIdAndVersionByIdentifierAndCustomerAndUsername(identifier, idCustomer, principal.getName()).stream()
				.map(version -> new ApiNamable(version[0], version[1].toString())).collect(Collectors.toList());
	}

	@RequestMapping(value = "/data/analysis/{idAnalysis}/assets", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object loadAnalysisAssets(@PathVariable("idAnalysis") Integer idAnalysis, Principal principal) throws Exception {
		return serviceAsset.getAllFromAnalysis(idAnalysis).stream().map(asset -> new ApiAsset(asset.getId(), asset.getName(), asset.getValue())).collect(Collectors.toList());
	}

	@RequestMapping(value = "/data/analysis/{idAnalysis}/scenarios", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object loadAnalysisScenarios(@PathVariable("idAnalysis") Integer idAnalysis, Principal principal) throws Exception {
		return serviceScenario.getAllFromAnalysis(idAnalysis).stream().map(scenario -> new ApiNamable(scenario.getId(), scenario.getName())).collect(Collectors.toList());
	}

	@RequestMapping(value = "/data/analysis/{idAnalysis}/standards", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object loadAnalysisStandards(@PathVariable("idAnalysis") Integer idAnalysis, Principal principal) throws Exception {
		return serviceStandard.getAllFromAnalysis(idAnalysis).stream().map(standard -> new ApiNamable(standard.getId(), standard.getLabel())).collect(Collectors.toList());
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
	@RequestMapping(value = "/data/load-rrf", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	public @ResponseBody Object loadRRF(@RequestParam(name = "analysisId") Integer idAnalysis, @RequestParam(name = "assetId") Integer idAsset,
			@RequestParam(name = "scenarioId") Integer idScenario, @RequestParam(name = "standards") String standard, Principal principal, HttpServletResponse response,
			Locale locale) throws Exception {
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
		IParameter rrfTuning = analysis.findParameter(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
		ValueFactory factory = new ValueFactory(analysis.getParameters());
		for (String name : standardNames) {
			AnalysisStandard analysisStandard = analysisStandards.get(name);
			if (analysisStandard == null)
				throw new TrickException("error.standard.not_found", "Standard cannot be found");
			ApiStandard apiStandard = new ApiStandard(analysisStandard.getStandard().getId(), analysisStandard.getStandard().getLabel());
			analysisStandard.getMeasures().stream().filter(measure -> measure.getMeasureDescription().isComputable()).forEach(measure -> {
				apiStandard.getMeasures().add(new ApiMeasure(measure.getId(), measure.getMeasureDescription().getMeasureDescriptionTextByAlpha2(locale.getLanguage()).getDomain(),
						(int) measure.getImplementationRateValue(factory), measure.getCost(), RRF.calculateRRF(assessment, rrfTuning, measure)));
			});
			apiRRF.getStandards().add(apiStandard);
		}
		return apiRRF;

	}
}
