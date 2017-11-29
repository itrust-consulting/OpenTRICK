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
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lu.itrust.business.TS.asynchronousWorkers.WorkerComputeDynamicParameters;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.DynamicParameterComputer;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceAssetType;
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
import lu.itrust.business.TS.model.api.basic.ApiAssessment;
import lu.itrust.business.TS.model.api.basic.ApiAssessmentValue;
import lu.itrust.business.TS.model.api.basic.ApiAsset;
import lu.itrust.business.TS.model.api.basic.ApiMeasure;
import lu.itrust.business.TS.model.api.basic.ApiNamable;
import lu.itrust.business.TS.model.api.basic.ApiRRF;
import lu.itrust.business.TS.model.api.basic.ApiScenario;
import lu.itrust.business.TS.model.api.basic.ApiStandard;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.externalnotification.helper.ExternalNotificationHelper;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.impl.LevelValue;
import lu.itrust.business.TS.model.parameter.value.impl.RealValue;
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
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceIDS serviceIDS;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

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

	@CrossOrigin
	@RequestMapping(value = "/data/customers", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	public @ResponseBody Object loadUserCustomer(Principal principal, Locale locale) throws Exception {
		return serviceCustomer.getAllNotProfileOfUser(principal.getName()).stream().map(customer -> new ApiNamable(customer.getId(), customer.getOrganisation()))
				.collect(Collectors.toList());
	}

	@CrossOrigin
	@RequestMapping(value = "/data/analysis/all", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	public @ResponseBody Object loadAnalyses(@RequestParam(name = "customerId") Integer idCustomer, Principal principal, Locale locale) {
		if (idCustomer == null)
			throw new TrickException("error.custmer.null", "Customer id cannot be empty");
		return serviceAnalysis.getIdentifierAndNameByUserAndCustomer(principal.getName(), idCustomer).stream().map(analysis -> new ApiNamable(analysis[0], analysis[1].toString()))
				.collect(Collectors.toList());
	}

	@CrossOrigin
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

	@CrossOrigin
	@RequestMapping(value = "/data/analysis/{idAnalysis}/assets", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object loadAnalysisAssets(@PathVariable("idAnalysis") Integer idAnalysis, Principal principal) throws Exception {
		return serviceAsset.getAllFromAnalysis(idAnalysis).stream().map(asset -> ApiAsset.create(asset)).collect(Collectors.toList());
	}

	@CrossOrigin
	@RequestMapping(value = "/data/analysis/{idAnalysis}/scenarios", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object loadAnalysisScenarios(@PathVariable("idAnalysis") Integer idAnalysis, Principal principal) throws Exception {
		return serviceScenario.getAllFromAnalysis(idAnalysis).stream().map(scenario -> ApiScenario.create(scenario)).collect(Collectors.toList());
	}

	@CrossOrigin
	@RequestMapping(value = "/data/analysis/{idAnalysis}/standards", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object loadAnalysisStandards(@PathVariable("idAnalysis") Integer idAnalysis, Principal principal) throws Exception {
		return serviceStandard.getAllFromAnalysis(idAnalysis).stream().map(standard -> new ApiNamable(standard.getId(), standard.getLabel())).collect(Collectors.toList());
	}

	@CrossOrigin
	@RequestMapping(value = "/data/analysis/{idAnalysis}/new-asset", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object createAnalysisAsset(@PathVariable Integer idAnalysis, @RequestParam(name = "name") String assetName, @RequestParam(name = "type") String assetTypeName, Principal principal, Locale locale) throws Exception {
		try {
			if (serviceAnalysis.isProfile(idAnalysis))
				throw new TrickException("error.action.not_authorise", "Action does not authorised");
			Asset asset = new Asset();
			asset.setName(assetName);
			asset.setAssetType(serviceAssetType.getByName(assetTypeName));
			asset.setSelected(false);

			Analysis analysis = serviceAnalysis.get(idAnalysis);
			analysis.add(asset);
			serviceAnalysis.saveOrUpdate(analysis);

			assessmentAndRiskProfileManager.unSelectAsset(asset);
			assessmentAndRiskProfileManager.build(asset, idAnalysis);

			return JsonMessage.SuccessWithId(asset.getId());
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/data/analysis/{idAnalysis}/assets/{idAsset}", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object editAnalysisAsset(@PathVariable Integer idAnalysis, @PathVariable Integer idAsset, @RequestParam(name = "name") String assetName, @RequestParam(name = "type") String assetTypeName, Principal principal, Locale locale) throws Exception {
		try {
			if (serviceAnalysis.isProfile(idAnalysis))
				throw new TrickException("error.action.not_authorise", "Action does not authorised");
			Asset asset = serviceAsset.getFromAnalysisById(idAnalysis, idAsset);
			asset.setName(assetName);
			asset.setAssetType(serviceAssetType.getByName(assetTypeName));

			serviceAsset.saveOrUpdate(asset);

			assessmentAndRiskProfileManager.build(asset, idAnalysis);

			return JsonMessage.SuccessWithId(asset.getId());
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/data/analysis/{idAnalysis}/assets/{idAsset}", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.DELETE)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object deleteAnalysisAsset(@PathVariable Integer idAnalysis, @PathVariable Integer idAsset, Principal principal, Locale locale) throws Exception {
		try {
			customDelete.deleteAsset(idAsset, idAnalysis);
			return JsonMessage.Success(messageSource.getMessage("success.asset.delete.successfully", null, "Asset was deleted successfully", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.asset.delete.failed", null, "Asset cannot be deleted", locale));
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/data/analysis/{idAnalysis}/assessments", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.GET)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object getAnalysisAssessments(@PathVariable Integer idAnalysis, Principal principal, Locale locale) throws Exception {
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		return analysis.getAssessments().stream().map(assessment -> ApiAssessment.create(assessment)).collect(Collectors.toList());
	}

	@CrossOrigin
	@RequestMapping(value = "/data/analysis/{idAnalysis}/assessments/save", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object saveAnalysisAssessments(@PathVariable Integer idAnalysis, @RequestBody ApiAssessmentValue assessmentValue, Principal principal, Locale locale) throws Exception {
		try {
			// Find assessment
			Assessment assessment = serviceAssessment.getFromAnalysisById(idAnalysis, (Integer)assessmentValue.getId());
			// Set new likelihood
			assessment.setLikelihood(Double.toString(assessmentValue.getLikelihood()));
			assessment.setLikelihoodReal(assessmentValue.getLikelihood());
			// Set new impacts
			for (IValue currentValue : assessment.getImpacts()) {
				final Double newValue = assessmentValue.getImpacts().get(currentValue.getName());
				// Only update impact if a new value has been provided for it
				if (newValue != null) {
					if (currentValue instanceof RealValue)
						((RealValue)currentValue).setReal(newValue);
					else if (currentValue instanceof LevelValue)
						((LevelValue)currentValue).setLevel((int)Math.round(newValue));
					// silently ignore all other value types
				}
			}
			serviceAssessment.save(assessment);
			return JsonMessage.Success(messageSource.getMessage("success.assessment.refresh", null, "Assessments were successfully refreshed", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.asset.delete.failed", null, "Asset cannot be deleted", locale));
		}
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
	@CrossOrigin
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
		apiRRF.setScenario(new ApiScenario(assessment.getScenario().getId(), assessment.getScenario().getName(), assessment.getScenario().getType().getValue(), assessment.getScenario().getType().getName()));
		apiRRF.setAsset(new ApiAsset(assessment.getAsset().getId(), assessment.getAsset().getName(), assessment.getAsset().getAssetType().getId(), assessment.getAsset().getAssetType().getName(), assessment.getAsset().getValue()));
		IParameter rrfTuning = analysis.findParameterByTypeAndDescription(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
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
