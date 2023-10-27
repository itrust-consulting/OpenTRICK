package lu.itrust.business.ts.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lu.itrust.business.ts.asynchronousWorkers.WorkerComputeDynamicParameters;
import lu.itrust.business.ts.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.ts.component.CustomDelete;
import lu.itrust.business.ts.component.DynamicParameterComputer;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceAssessment;
import lu.itrust.business.ts.database.service.ServiceAsset;
import lu.itrust.business.ts.database.service.ServiceAssetEdge;
import lu.itrust.business.ts.database.service.ServiceAssetType;
import lu.itrust.business.ts.database.service.ServiceCustomer;
import lu.itrust.business.ts.database.service.ServiceExternalNotification;
import lu.itrust.business.ts.database.service.ServiceIDS;
import lu.itrust.business.ts.database.service.ServiceImpactParameter;
import lu.itrust.business.ts.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.ts.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.ts.database.service.ServiceScenario;
import lu.itrust.business.ts.database.service.ServiceStandard;
import lu.itrust.business.ts.exception.ResourceNotFoundException;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.api.ApiExternalNotification;
import lu.itrust.business.ts.model.api.ApiNotifyRequest;
import lu.itrust.business.ts.model.api.ApiResult;
import lu.itrust.business.ts.model.api.basic.ApiAssessment;
import lu.itrust.business.ts.model.api.basic.ApiAssessmentValue;
import lu.itrust.business.ts.model.api.basic.ApiAsset;
import lu.itrust.business.ts.model.api.basic.ApiEdge;
import lu.itrust.business.ts.model.api.basic.ApiGraph;
import lu.itrust.business.ts.model.api.basic.ApiMeasure;
import lu.itrust.business.ts.model.api.basic.ApiNamable;
import lu.itrust.business.ts.model.api.basic.ApiNode;
import lu.itrust.business.ts.model.api.basic.ApiRRF;
import lu.itrust.business.ts.model.api.basic.ApiRiskAcceptance;
import lu.itrust.business.ts.model.api.basic.ApiRiskAcceptanceLevel;
import lu.itrust.business.ts.model.api.basic.ApiRiskLevel;
import lu.itrust.business.ts.model.api.basic.ApiScenario;
import lu.itrust.business.ts.model.api.basic.ApiStandard;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.externalnotification.helper.ExternalNotificationHelper;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.document.impl.SimpleDocument;
import lu.itrust.business.ts.model.general.document.impl.SimpleDocumentType;
import lu.itrust.business.ts.model.ilr.AssetEdge;
import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.ilr.Position;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.rrf.RRF;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.usermanagement.IDS;

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
@RestController
@RequestMapping(value = "/Api", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, produces = MediaType.APPLICATION_JSON_VALUE)
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
	private ServiceRiskAcceptanceParameter serviceRiskAcceptanceParameter;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceIDS serviceIDS;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private ServiceAssetEdge serviceAssetEdge;

	private Long maxUploadFileSize;

	@PostMapping("/data/analysis/{idAnalysis}/new-asset")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object createAnalysisAsset(@PathVariable final Integer idAnalysis,
			@RequestParam(name = "name") final String assetName,
			@RequestParam(name = "type") final String assetTypeName,
			@RequestParam(name = "selected", defaultValue = "false") final boolean selected, final Principal principal,
			final Locale locale) {
		try {
			if (serviceAnalysis.isProfile(idAnalysis))
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			final Asset asset = new Asset(assetName);

			asset.setSelected(selected);

			asset.setAssetType(serviceAssetType.getByName(assetTypeName));

			final Analysis analysis = serviceAnalysis.get(idAnalysis);

			analysis.add(asset);

			serviceAnalysis.saveOrUpdate(analysis);

			if (selected)
				assessmentAndRiskProfileManager.selectAsset(asset);
			else
				assessmentAndRiskProfileManager.unSelectAsset(asset);

			assessmentAndRiskProfileManager.build(asset, idAnalysis);

			return JsonMessage.SuccessWithId(asset.getId());
		} catch (final TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (final Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@DeleteMapping("/data/analysis/{idAnalysis}/assets/{idAsset}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object deleteAnalysisAsset(@PathVariable final Integer idAnalysis,
			@PathVariable final Integer idAsset,
			final Principal principal, final Locale locale) {
		try {
			customDelete.deleteAsset(idAsset, idAnalysis);
			return JsonMessage.Success(messageSource.getMessage("success.asset.delete.successfully", null,
					"Asset was deleted successfully", locale));
		} catch (final TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (final Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(
					messageSource.getMessage("error.asset.delete.failed", null, "Asset cannot be deleted", locale));
		}
	}

	@PostMapping("/data/analysis/{idAnalysis}/assets/{idAsset}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object editAnalysisAsset(@PathVariable final Integer idAnalysis,
			@PathVariable final Integer idAsset,
			@RequestParam(name = "name") final String assetName,
			@RequestParam(name = "type") final String assetTypeName,
			@RequestParam(name = "selected", defaultValue = "true") final boolean selected, final Principal principal,
			final Locale locale) {
		try {
			if (serviceAnalysis.isProfile(idAnalysis))
				throw new TrickException("error.action.not_authorise", "Action does not authorised");

			final Asset asset = serviceAsset.getFromAnalysisById(idAnalysis, idAsset);

			asset.setName(assetName);

			asset.setAssetType(serviceAssetType.getByName(assetTypeName));

			serviceAsset.saveOrUpdate(asset);

			if (selected)
				assessmentAndRiskProfileManager.selectAsset(asset);
			else
				assessmentAndRiskProfileManager.unSelectAsset(asset);

			assessmentAndRiskProfileManager.build(asset, idAnalysis);

			return JsonMessage.SuccessWithId(asset.getId());
		} catch (final TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (final Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@PostMapping("/data/analysis/{idAnalysis}/dependency")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object updateAssetDependencies(@PathVariable final Integer idAnalysis,
			@RequestBody final ApiGraph graph, final Principal principal,
			final Locale locale) {

		final String EDGE_KEY_NAME_FORMAT = "-P-!%d-C-%d!--";

		final Analysis analysis = serviceAnalysis.get(idAnalysis);

		final Map<Integer, AssetNode> nodeByAssetIds = analysis.getAssetNodes().stream()
				.collect(Collectors.toMap(e -> e.getAsset().getId(), Function.identity()));

		final Map<Integer, Asset> assetMap = analysis.getAssets().stream()
				.collect(Collectors.toMap(Asset::getId, Function.identity()));

		final Map<String, AssetEdge> oldEdges = analysis.getAssetNodes().stream()
				.flatMap(e -> e.getEdges().values().stream())
				.collect(Collectors.toMap(e -> String.format(EDGE_KEY_NAME_FORMAT, e.getParent().getAsset().getId(),
						e.getChild().getAsset().getId()), Function.identity()));

		final Map<String, AssetNode> nodes = graph.getNodes().values().stream()
				.map(e -> new Object[] { (String) e.getData().get(ApiNode.ID),
						this.createAssetNde(e, nodeByAssetIds, assetMap) })
				.filter(e -> !(e[0] == null || e[1] == null))
				.collect(Collectors.toMap(e -> (String) e[0],
						e -> (AssetNode) e[1]));

		for (final ApiEdge apiEdge : graph.getEdges()) {
			final AssetNode parent = nodes.get(apiEdge.getSource());
			final AssetNode child = nodes.get(apiEdge.getTarget());

			if (parent == null || child == null)
				continue;

			final AssetEdge edge = oldEdges.remove(String.format(EDGE_KEY_NAME_FORMAT, parent.getAsset().getId(),
					child.getAsset().getId()));

			if (edge == null)
				parent.getEdges().put(child, new AssetEdge(parent, child, apiEdge.getP()));
			else
				edge.setWeight(apiEdge.getP());
		}

		oldEdges.values().forEach(e -> e.getParent().getEdges().remove(e.getChild()));

		nodes.values().stream().filter(e -> e.getId() < 1).forEach(e -> analysis.getAssetNodes().add(e));

		serviceAnalysis.saveOrUpdate(analysis);

		serviceAssetEdge.delete(oldEdges.values());

		// Success
		return new ApiResult(0);
	}

	@PostMapping("/data/analysis/{idAnalysis}/dependency/{type}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object updateAssetDependenciesPng(@PathVariable final Integer idAnalysis,
			@PathVariable String type,
			@RequestParam(value = "file") final MultipartFile file, final Principal principal,
			final Locale locale) throws IOException {

		if (file.getSize() > maxUploadFileSize)
			return JsonMessage.Error(messageSource.getMessage("error.file.too.large",
					new Object[] { maxUploadFileSize }, "File is to large", locale));

		final Analysis analysis = serviceAnalysis.get(idAnalysis);

		final SimpleDocumentType documentType;

		switch (type.trim().toUpperCase()) {
			case "JSON":
				documentType = SimpleDocumentType.ASSET_DEPENDENCY_GRAPH_JSON;
				break;
			case "PNG":
				documentType = SimpleDocumentType.ASSET_DEPENDENCY_GRAPH_PNG;
				break;
			default:
				throw new IllegalArgumentException("error.document.type");
		}

		final SimpleDocument document = analysis.getDocuments().computeIfAbsent(documentType,
				k -> new SimpleDocument(documentType, file.getName(), file.getSize(), null));

		if (document.getId() > 0) {
			document.setName(file.getName());
			document.setLength(file.getSize());
			document.setCreated(new Timestamp(System.currentTimeMillis()));
		}

		document.setData(FileCopyUtils.copyToByteArray(file.getInputStream()));

		serviceAnalysis.saveOrUpdate(analysis);

		return new ApiResult(0);
	}

	@GetMapping("/data/analysis/{idAnalysis}/dependency/{type}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public void getSimpleDocument(@PathVariable final Integer idAnalysis, @PathVariable String type,
			HttpServletRequest request, HttpServletResponse response, final Principal principal,
			final Locale locale) throws IOException {

		// get user file by given file id and username

		final Analysis analysis = serviceAnalysis.get(idAnalysis);

		final SimpleDocumentType documentType;

		switch (type.trim().toUpperCase()) {
			case "JSON":
				documentType = SimpleDocumentType.ASSET_DEPENDENCY_GRAPH_JSON;
				break;
			case "PNG":
				documentType = SimpleDocumentType.ASSET_DEPENDENCY_GRAPH_PNG;
				break;
			default:
				throw new IllegalArgumentException("error.document.type");
		}

		final SimpleDocument document = analysis.getDocuments().get(documentType);

		if (document == null)
			throw new ResourceNotFoundException("error.document.not_found");

		// set response contenttype to document
		response.setContentType(type.trim().toLowerCase());

		// set response header with location of the filename
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + document.getName() + "\"");

		// set document file size as response size
		response.setContentLength((int) document.getLength());

		// return the document file (as copy) to the response outputstream ( whihc
		// creates on the
		// client side the document file)
		FileCopyUtils.copy(document.getData(), response.getOutputStream());
	}

	@GetMapping("/data/analysis/{idAnalysis}/dependency")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object getAssetDependencies(@PathVariable final Integer idAnalysis, final Principal principal,
			final Locale locale) {
		return new ApiGraph(serviceAnalysis.get(idAnalysis).getAssetNodes());
	}

	@GetMapping("/data/analysis/{idAnalysis}/assessments")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object getAnalysisAssessments(@PathVariable final Integer idAnalysis,
			final Principal principal,
			final Locale locale) {
		return serviceAnalysis.get(idAnalysis).getAssessments().stream().map(ApiAssessment::create)
				.collect(Collectors.toList());
	}

	/**
	 * Home of the API. Always returns success.
	 */
	@RequestMapping
	public Object home(final Principal principal) {
		if (principal == null)
			return new ApiResult(0, "<not logged in>");
		else
			return new ApiResult(0, principal.getName());
	}

	@GetMapping("/data/analysis/all")
	public @ResponseBody Object loadAnalyses(@RequestParam(name = "customerId") final Integer idCustomer,
			final Principal principal,
			final Locale locale) {
		if (idCustomer == null)
			throw new TrickException("error.custmer.null", "Customer id cannot be empty");
		return serviceAnalysis.getIdentifierAndNameByUserAndCustomer(principal.getName(), idCustomer).stream()
				.map(analysis -> new ApiNamable(analysis[0], analysis[1].toString()))
				.collect(Collectors.toList());
	}

	@GetMapping("/data/analysis/versions")
	public @ResponseBody Object loadAnalysesVersion(@RequestParam(name = "customerId") final Integer idCustomer,
			@RequestParam(name = "identifier") final String identifier,
			final Principal principal, final Locale locale) {
		if (idCustomer == null)
			throw new TrickException("error.custmer.null", "Customer id cannot be empty");
		if (identifier == null)
			throw new TrickException("error.analysis.identifier", "Identifier cannot be empty");
		final Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), idCustomer);
		if (customer == null)
			throw new TrickException("error.custmer.not_found", "Customer cannot be found");
		return serviceAnalysis
				.getIdAndVersionByIdentifierAndCustomerAndUsername(identifier, idCustomer, principal.getName()).stream()
				.map(version -> new ApiNamable(version[0], version[1].toString())).collect(Collectors.toList());
	}

	@GetMapping("/data/analysis/{idAnalysis}/assets")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object loadAnalysisAssets(@PathVariable("idAnalysis") final Integer idAnalysis,
			final Principal principal) {
		return serviceAsset.getAllFromAnalysis(idAnalysis).stream().map(ApiAsset::create)
				.collect(Collectors.toList());
	}

	@GetMapping("/data/analysis/{idAnalysis}/risk-acceptance")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object loadAnalysisRiskAcceptance(@PathVariable("idAnalysis") final Integer idAnalysis,
			final Principal principal) {
		final ApiRiskAcceptance apiObject = new ApiRiskAcceptance();
		apiObject.setAcceptanceLevels(serviceRiskAcceptanceParameter.findByAnalysisId(idAnalysis).stream()
				.map(ApiRiskAcceptanceLevel::create).collect(Collectors.toList()));
		apiObject.setImpactLevels(serviceImpactParameter.findByAnalysisId(idAnalysis).stream()
				.collect(Collectors.groupingBy(ImpactParameter::getTypeName,
						Collectors.mapping(ApiRiskLevel::create, Collectors.toList()))));
		apiObject.setLikelihoodLevels(serviceLikelihoodParameter.findByAnalysisId(idAnalysis).stream()
				.map(ApiRiskLevel::create).collect(Collectors.toList()));
		return apiObject;
	}

	@GetMapping("/data/analysis/{idAnalysis}/scenarios")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object loadAnalysisScenarios(@PathVariable("idAnalysis") final Integer idAnalysis,
			final Principal principal) {
		return serviceScenario.getAllFromAnalysis(idAnalysis).stream().map(scenario -> ApiScenario.create(scenario))
				.collect(Collectors.toList());
	}

	@GetMapping("/data/analysis/{idAnalysis}/standards")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object loadAnalysisStandards(@PathVariable("idAnalysis") final Integer idAnalysis,
			final Principal principal) {
		return serviceStandard.getAllFromAnalysis(idAnalysis).stream()
				.map(standard -> new ApiNamable(standard.getId(), standard.getName())).collect(Collectors.toList());
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
	@GetMapping("/data/load-rrf")
	public @ResponseBody Object loadRRF(@RequestParam(name = "analysisId") final Integer idAnalysis,
			@RequestParam(name = "assetId") final Integer idAsset,
			@RequestParam(name = "scenarioId") final Integer idScenario,
			@RequestParam(name = "standards") final String standard,
			final Principal principal, final HttpServletResponse response,
			final Locale locale) {
		final String[] standardNames = standard.split(",");
		if (standardNames.length == 0)
			throw new TrickException("error.standards.empty", "Standard cannot be empty");

		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (!analysis.isUserAuthorized(principal.getName(), AnalysisRight.EXPORT))
			throw new TrickException("error.403.message",
					"You do not have the necessary permissions to perform this action");
		final Map<String, AnalysisStandard> analysisStandards = analysis.getAnalysisStandards().values().stream()
				.collect(Collectors.toMap(analysisStandard -> analysisStandard.getStandard().getName(),
						Function.identity()));
		final Assessment assessment = analysis.getAssessments().stream()
				.filter(assessment1 -> assessment1.getAsset().getId() == idAsset
						&& assessment1.getScenario().getId() == idScenario)
				.findAny()
				.orElseThrow(() -> new TrickException("error.assessment.not_found", "Assessment cannot be found"));
		final ApiRRF apiRRF = new ApiRRF(idAnalysis, assessment.getImpactReal(), assessment.getLikelihoodReal());
		apiRRF.setScenario(new ApiScenario(assessment.getScenario().getId(), assessment.getScenario().getName(),
				assessment.getScenario().getType().getValue(),
				assessment.getScenario().getType().getName()));
		apiRRF.setAsset(new ApiAsset(assessment.getAsset().getId(), assessment.getAsset().getName(),
				assessment.getAsset().getAssetType().getId(),
				assessment.getAsset().getAssetType().getName(), assessment.getAsset().getValue(),
				assessment.getAsset().isSelected()));
		final IParameter rrfTuning = analysis.findParameter(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME,
				Constant.PARAMETER_MAX_RRF);
		final ValueFactory factory = new ValueFactory(analysis.getParameters());
		for (final String name : standardNames) {
			final AnalysisStandard analysisStandard = analysisStandards.get(name);
			if (analysisStandard == null)
				throw new TrickException("error.standard.not_found", "Standard cannot be found");
			final ApiStandard apiStandard = new ApiStandard(analysisStandard.getStandard().getId(),
					analysisStandard.getStandard().getName());
			analysisStandard.getMeasures().stream().filter(measure -> measure.getMeasureDescription().isComputable())
					.forEach(measure -> {
						apiStandard.getMeasures()
								.add(new ApiMeasure(measure.getId(),
										measure.getMeasureDescription()
												.getMeasureDescriptionTextByAlpha2(locale.getLanguage()).getDomain(),
										(int) measure.getImplementationRateValue(factory), measure.getCost(),
										RRF.calculateRRF(assessment, rrfTuning, measure)));
					});
			apiRRF.getStandards().add(apiStandard);
		}
		return apiRRF;

	}

	@GetMapping("/data/customers")
	public @ResponseBody Object loadUserCustomer(final Principal principal, final Locale locale) {
		return serviceCustomer.getAllNotProfileOfUser(principal.getName()).stream()
				.map(customer -> new ApiNamable(customer.getId(), customer.getOrganisation()))
				.collect(Collectors.toList());
	}

	/**
	 * This method is responsible for accepting external notifications which serve
	 * as risk indicator (such as IDS alerts). From these, the probabilities that
	 * certain events happen, are deduced and stored in variables ready to be used
	 * within the TRICK service user interface (asset/scenario estimation).
	 * 
	 * @param data One or multiple notifications sent to TRICK Service.
	 * @return Returns an error code (0 = success).
	 * @throws Exception
	 */
	@Transactional
	@PostMapping("/ids/notify")
	public Object notify(final HttpSession session, final Principal principal,
			@RequestBody final ApiNotifyRequest request) {

		final IDS ids = serviceIDS.get(principal.getName()).notifyAlert();

		for (final ApiExternalNotification apiObj : request.getData())
			serviceExternalNotification.save(ExternalNotificationHelper.createEntityBasedOn(apiObj, ids.getPrefix()));

		// Trigger execution of worker which computes dynamic parameters.
		// This method only schedules the task if it does not have been
		// scheduled yet for the given user.
		WorkerComputeDynamicParameters.trigger(ids.getPrefix(), computationDelayInSeconds, dynamicParameterComputer,
				scheduler);

		serviceIDS.saveOrUpdate(ids);

		// Success
		return new ApiResult(0);
	}

	@PostMapping("/data/analysis/{idAnalysis}/assessments/save")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).EXPORT)")
	public @ResponseBody Object saveAnalysisAssessments(@PathVariable final Integer idAnalysis,
			@RequestBody final ApiAssessmentValue assessmentValue, final Principal principal, final Locale locale) {
		try {
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			// Find assessment
			final Assessment assessment = analysis.getAssessments().stream()
					.filter(a -> assessmentValue.getId().equals(a.getId())).findAny().orElse(null);
			if (assessment == null)
				return null;

			final List<IValue> values = new LinkedList<>();

			final ValueFactory factory = new ValueFactory(analysis.getParameters());

			if (assessmentValue.getLikelihood() != null) {
				final IValue value = factory.findProb(assessmentValue.getLikelihood());
				// Update likelihood
				if (assessment.getLikelihood() == null)
					assessment.setLikelihood(value);
				else if (!assessment.getLikelihood().merge(value)) {
					values.add(assessment.getLikelihood());
					assessment.setLikelihood(value);
				}

				if (analysis.isQuantitative() && assessment.getLikelihood() != null)
					assessment.setLikelihoodReal(assessment.getLikelihood().getReal());

			}

			// Set new impacts
			for (final Entry<String, Object> entry : assessmentValue.getImpacts().entrySet()) {
				final IValue oldValue = assessment.getImpact(entry.getKey());
				final IValue newValue = factory.findValue(entry.getValue(), entry.getKey());
				if (oldValue == null) {
					if (newValue != null && factory.getImpactMapper().containsKey(entry.getKey()))
						assessment.setImpact(newValue);
				} else if (!oldValue.merge(newValue) && newValue != null) {
					assessment.setImpact(newValue);
				}
			}
			serviceAssessment.save(assessment);
			return JsonMessage.Success(messageSource.getMessage("success.assessment.refresh", null,
					"Assessments were successfully refreshed", locale));
		} catch (final TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (final Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(
					messageSource.getMessage("error.asset.delete.failed", null, "Asset cannot be deleted", locale));
		}
	}

	/**
	 * Method is called whenever an exception of type TrickException is thrown in
	 * this controller.
	 */
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	@ExceptionHandler(TrickException.class)
	private Object handleTrickException(final TrickException ex) {
		// Return the error message to the client (JSON).
		// -1 denotes general errors.
		return new ApiResult(-1, MessageFormat.format(ex.getMessage(), ex.getParameters()));
	}

	@Value("${spring.servlet.multipart.max-file-size}")
	public void setMaxUploadFileSize(String value) {
		this.maxUploadFileSize = DataSize.parse(value).toBytes();
	}

	private AssetNode createAssetNde(final ApiNode apiNode, final Map<Integer, AssetNode> nodes,
			final Map<Integer, Asset> assetMap) {

		final Integer idAsset = (Integer) apiNode.getData().get(ApiNode.TRICK_ID);
		if (idAsset == null)
			return null;
		final Asset asset = assetMap.get(idAsset);
		if (asset == null)
			return null;

		AssetNode assetNode = nodes.get(idAsset);
		if (assetNode == null)
			assetNode = new AssetNode(asset);

		if (!apiNode.getPosition().isEmpty()) {
			if (assetNode.getPosition() == null)
				assetNode.setPosition(new Position());

			assetNode.getPosition().setX(apiNode.getPosition().getOrDefault(ApiNode.POSITION_X,
					Math.random() * assetMap.size() * 20 - assetMap.size() * 10));
			assetNode.getPosition().setY(apiNode.getPosition().getOrDefault(ApiNode.POSITION_Y,
					Math.random() * assetMap.size() * 20 - assetMap.size() * 10));
		}

		return assetNode;
	}
}
