package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.PHASE_DEFAULT;
import static lu.itrust.business.ts.constants.Constant.ROLE_MIN_USER;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.ts.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.ts.component.Duplicator;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceAnalysisStandard;
import lu.itrust.business.ts.database.service.ServiceAssessment;
import lu.itrust.business.ts.database.service.ServiceAsset;
import lu.itrust.business.ts.database.service.ServiceAssetNode;
import lu.itrust.business.ts.database.service.ServiceCustomer;
import lu.itrust.business.ts.database.service.ServiceDataValidation;
import lu.itrust.business.ts.database.service.ServiceDynamicParameter;
import lu.itrust.business.ts.database.service.ServiceIlrSoaScaleParameter;
import lu.itrust.business.ts.database.service.ServiceImpactParameter;
import lu.itrust.business.ts.database.service.ServiceItemInformation;
import lu.itrust.business.ts.database.service.ServiceLanguage;
import lu.itrust.business.ts.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.ts.database.service.ServiceMaturityParameter;
import lu.itrust.business.ts.database.service.ServicePhase;
import lu.itrust.business.ts.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.ts.database.service.ServiceRiskInformation;
import lu.itrust.business.ts.database.service.ServiceRiskProfile;
import lu.itrust.business.ts.database.service.ServiceScaleType;
import lu.itrust.business.ts.database.service.ServiceScenario;
import lu.itrust.business.ts.database.service.ServiceSimpleDocument;
import lu.itrust.business.ts.database.service.ServiceSimpleParameter;
import lu.itrust.business.ts.database.service.ServiceUser;
import lu.itrust.business.ts.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.form.AnalysisForm;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.ts.model.analysis.helper.AnalysisStandardBaseInfo;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.general.document.impl.SimpleDocument;
import lu.itrust.business.ts.model.history.History;
import lu.itrust.business.ts.model.ilr.AssetEdge;
import lu.itrust.business.ts.model.ilr.AssetImpact;
import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.iteminformation.ItemInformation;
import lu.itrust.business.ts.model.parameter.ILevelParameter;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.ts.model.parameter.value.AbstractValue;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.parameter.value.impl.RealValue;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;
import lu.itrust.business.ts.model.scale.Scale;
import lu.itrust.business.ts.model.scale.ScaleType;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.usermanagement.User;
import lu.itrust.business.ts.validator.CustomAnalysisValidator;

/**
 * This class represents a controller for creating analysis in the application.
 * It handles the HTTP requests related to analysis creation and provides
 * necessary dependencies.
 * 
 * @author itrust consulting s.a.rl.:
 * @version
 * @since Oct 13, 2014
 */
@PreAuthorize(ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/Build")
public class ControllerCreation {

	@Autowired
	private Duplicator duplicator;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceDynamicParameter serviceDynamicParameter;

	@Autowired
	private ServiceIlrSoaScaleParameter serviceIlrSoaScaleParameter;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceItemInformation serviceItemInformation;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceMaturityParameter serviceMaturityParameter;

	@Autowired
	private ServiceAssetNode serviceAssetNode;

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceRiskAcceptanceParameter serviceRiskAcceptanceParameter;

	@Autowired
	private ServiceRiskInformation serviceRiskInformation;

	@Autowired
	private ServiceRiskProfile serviceRiskProfile;

	@Autowired
	private ServiceScaleType serviceScaleType;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceSimpleDocument serviceSimpleDocument;

	/**
	 * Builds a custom analysis.
	 *
	 * @param session   the HttpSession object
	 * @param principal the Principal object representing the currently
	 *                  authenticated user
	 * @param model     the Model object used to pass data to the view
	 * @param locale    the Locale object representing the current locale
	 * @return the view name for the custom analysis form
	 * @throws Exception if an error occurs during the build process
	 */
	@GetMapping
	public String buildCustom(HttpSession session, Principal principal, Model model, Locale locale) throws Exception {

		// add languages
		model.addAttribute("languages", serviceLanguage.getAll());
		// add only customers of the current user
		model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));

		model.addAttribute("profiles", serviceAnalysis.getAllProfiles());

		model.addAttribute("impacts", serviceScaleType.findAllExpect(Constant.PARAMETER_TYPE_IMPACT_NAME));
		// set author as the username

		User user = serviceUser.get(principal.getName());

		model.addAttribute("author", user.getFirstName() + " " + user.getLastName());

		model.addAttribute("types", AnalysisType.values());

		model.addAttribute("locale", locale.getLanguage().toUpperCase());

		return "jsp/analyses/all/forms/buildAnalysis";

	}

	/**
	 * This method builds a custom save operation for the analysis form.
	 * 
	 * @param analysisForm The analysis form object.
	 * @param principal    The principal object representing the currently
	 *                     authenticated user.
	 * @param locale       The locale object representing the user's preferred
	 *                     language.
	 * @return An object representing the result of the save operation. If there are
	 *         validation errors, a map of errors is returned. Otherwise, an empty
	 *         object is returned.
	 * @throws Exception If an exception occurs during the save operation.
	 */
	@PostMapping(value = "/Save", consumes = "application/x-www-form-urlencoded;charset=UTF-8", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object buildCustomSave(@ModelAttribute AnalysisForm analysisForm, Principal principal,
			Locale locale) throws Exception {
		try {
			if (!serviceDataValidation.isRegistred(AnalysisForm.class))
				serviceDataValidation.register(new CustomAnalysisValidator());

			final Map<String, String> errors = serviceDataValidation.validate(analysisForm);
			for (Entry<String, String> error : errors.entrySet())
				errors.put(error.getKey(), serviceDataValidation.ParseError(error.getValue(), messageSource, locale));

			analysisForm.updateProfile();

			analysisForm.getImpacts().removeIf(id -> id < 1);

			if (analysisForm.getAsset() > 0 && !serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getAsset(),
					principal.getName(), AnalysisRight.EXPORT))
				errors.put("asset",
						messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (analysisForm.getScenario() > 0 && !(analysisForm.getScenario() == analysisForm.getProfile()
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getScenario(), principal.getName(),
							AnalysisRight.EXPORT)))
				errors.put("scenario",
						messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			validateStandards(analysisForm.getStandards(), errors, principal, analysisForm.getProfile(), locale);

			if (analysisForm.isAssessment()
					&& (analysisForm.getScenario() < 1 || analysisForm.getScenario() != analysisForm.getAsset()
							|| analysisForm.getParameter() != analysisForm.getAsset()))
				errors.put("assessment", messageSource.getMessage("error.analysis_custom.assessment.invalid", null,
						"Risk estimation cannot be selected", locale));

			if (analysisForm.getScope() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null,
							"No default profile, please select a profile", locale));
				errors.put("scope", messageSource.getMessage("error.analysis_custom.scope.empty", null,
						"No default profile, scope cannot be empty", locale));
			} else if (!(analysisForm.getScope() == analysisForm.getProfile()
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getScope(), principal.getName(),
							AnalysisRight.EXPORT)))
				errors.put("scope",
						messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (!analysisForm.getImpacts().isEmpty()) {
				if (analysisForm.getScale() == null)
					errors.put("scale.level",
							messageSource.getMessage("error.scale.level.empty", null, "Level cannot be empty", locale));
				else {
					if (analysisForm.getScale().getLevel() < 2)
						errors.put("scale.level", messageSource.getMessage("error.scale.level.bad_value",
								new Object[] { 2 }, "Level must be 2 or great", locale));
					if (analysisForm.getScale().getMaxValue() < 1)
						analysisForm.getScale().setMaxValue(300000);
				}
			}

			if (analysisForm.getParameter() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null,
							"No default profile, please select a profile", locale));
				errors.put("parameter", messageSource.getMessage("error.analysis_custom.parameter.empty", null,
						"No default profile, parameter cannot be empty", locale));
			} else if (!(analysisForm.getParameter() == analysisForm.getProfile()
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getParameter(), principal.getName(),
							AnalysisRight.EXPORT)))
				errors.put("parameter",
						messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (analysisForm.getRiskInformation() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null,
							"No default profile, please select a profile", locale));
				errors.put("riskInformation",
						messageSource.getMessage("error.analysis_custom.risk_information.empty", null,
								"No default profile, risk information cannot be empty", locale));
			} else if (!(analysisForm.getRiskInformation() == analysisForm.getProfile()
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getRiskInformation(), principal.getName(),
							AnalysisRight.EXPORT)))
				errors.put("riskInformation",
						messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			Customer customer = null;
			Language language = null;

			if (analysisForm.getLanguage() > 0) {
				language = serviceLanguage.get(analysisForm.getLanguage());
				if (language == null)
					errors.put("language", messageSource.getMessage("error.language.not_found", null,
							"Language cannot be found", locale));
			}

			if (analysisForm.getCustomer() > 0) {
				customer = serviceCustomer.getFromUsernameAndId(principal.getName(), analysisForm.getCustomer());
				if (customer == null)
					errors.put("customer", messageSource.getMessage("error.customer.not_found", null,
							"Customer cannot be found", locale));
				else if (!customer.isCanBeUsed())
					errors.put("customer", messageSource.getMessage("error.customer.invalid", null,
							"Customer cannot be used", locale));
				else if (serviceAnalysis.existsByNameAndCustomerId(analysisForm.getName(), analysisForm.getCustomer()))
					errors.put("name",
							messageSource.getMessage("error.analysis.name.in_used.for.customer",
									new Object[] { customer.getOrganisation() },
									String.format("Name cannot be used for %s", customer.getOrganisation()), locale));
			}

			if (!errors.isEmpty())
				return errors;

			History history = analysisForm.generateHistory();
			String identifier = language.getAlpha3() + "_"
					+ new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(history.getDate());
			Analysis analysis = new Analysis();
			analysis.setIdentifier(identifier);
			analysis.setCustomer(customer);
			analysis.setLanguage(language);
			analysis.addAHistory(history);
			analysis.setData(true);
			analysis.setLabel(analysisForm.getName());
			analysis.setCreationDate((Timestamp) history.getDate());
			analysis.setVersion(analysisForm.getVersion());
			analysis.setUncertainty(analysisForm.isUncertainty());
			analysis.setType(analysisForm.getType());
			analysis.setOwner(serviceUser.get(principal.getName()));
			analysis.addUserRight(analysis.getOwner(), AnalysisRight.ALL);
			String baseAnalysis = "";
			Locale analysisLocale = new Locale(language.getAlpha2());
			if (analysisForm.getAsset() > 0) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getAsset());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getAsset());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getAsset());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.assets",
						new String[] { label, company, version },
						String.format("Assets based on: %s, customer: %s, version: %s", label, company, version),
						analysisLocale);

				if (analysisForm.isAssetDependancy()) {
					baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.asset_dependancies",
							new String[] { label, company, version },
							String.format("Asset dependencies based on: %s, customer: %s, version: %s", label, company,
									version),
							analysisLocale);
				}
			}

			if (analysisForm.getScenario() > 1 && analysisForm.getScenario() != analysisForm.getProfile()) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getScenario());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getScenario());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getScenario());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.scenarios",
						new String[] { label, company, version },
						String.format("Scenarios based on: %s, customer: %s, version: %s", label, company, version),
						analysisLocale);
				if (analysisForm.isAssessment())
					baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.estimation",
							new String[] { label, company, version },
							String.format("Risk estimation based on: %s, customer: %s, version: %s", label, company,
									version),
							analysisLocale);
				if (analysisForm.isRiskProfile())
					baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.risk_profile",
							new String[] { label, company, version },
							String.format("Risk profile based on: %s, customer: %s, version: %s", label, company,
									version),
							analysisLocale);
			}

			if (analysisForm.getParameter() > 1 && analysisForm.getParameter() != analysisForm.getProfile()) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getParameter());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getParameter());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getParameter());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.parameters",
						new String[] { label, company, version },
						String.format("Parameters based on: %s, customer: %s, version: %s", label, company, version),
						analysisLocale);
			}

			if (analysisForm.getRiskInformation() > 1
					&& analysisForm.getRiskInformation() != analysisForm.getProfile()) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getRiskInformation());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getRiskInformation());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getRiskInformation());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.risk_information",
						new String[] { label, company, version },
						String.format("Risk information based on: %s, customer: %s, version: %s", label, company,
								version),
						analysisLocale);
			}

			if (analysisForm.getScope() > 1 && analysisForm.getScope() != analysisForm.getProfile()) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getScope());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getScope());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getScope());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.scope",
						new String[] { label, company, version },
						String.format("Scope based on: %s, customer: %s, version: %s", label, company, version),
						analysisLocale);
			}

			baseAnalysis = generateStandardLog(baseAnalysis, analysisForm, analysisForm.getProfile(), analysisLocale);

			history.setComment(history.getComment() + baseAnalysis);
			List<ItemInformation> itemInformations = serviceItemInformation.getAllFromAnalysis(analysisForm.getScope());
			for (ItemInformation itemInformation : itemInformations)
				analysis.add(itemInformation.duplicate());

			List<RiskInformation> riskInformations = serviceRiskInformation
					.getAllFromAnalysis(analysisForm.getRiskInformation());
			for (RiskInformation riskInformation : riskInformations)
				analysis.add(riskInformation.duplicate());

			analysis.setSettings(serviceAnalysis.getSettingsByIdAnalysis(analysisForm.getParameter()));

			Map<String, IParameter> mappingParameters = serviceSimpleParameter
					.findByAnalysisId(analysisForm.getParameter()).stream().map(duplicateParameter(analysis))
					.collect(Collectors.toMap(IParameter::getKey, Function.identity()));

			mappingParameters.putAll(serviceMaturityParameter.findByAnalysisId(analysisForm.getParameter()).stream()
					.map(duplicateParameter(analysis))
					.collect(Collectors.toMap(IParameter::getKey, Function.identity())));

			mappingParameters.putAll(serviceDynamicParameter.findByAnalysisId(analysisForm.getParameter()).stream()
					.map(duplicateParameter(analysis))
					.collect(Collectors.toMap(IParameter::getKey, Function.identity())));

			mappingParameters.putAll(serviceIlrSoaScaleParameter.findByAnalysisId(analysisForm.getParameter()).stream()
					.map(duplicateParameter(analysis))
					.collect(Collectors.toMap(IParameter::getKey, Function.identity())));

			if (analysisForm.getImpacts().isEmpty()) {
				mappingParameters.putAll(serviceRiskAcceptanceParameter.findByAnalysisId(analysisForm.getParameter())
						.stream().map(duplicateParameter(analysis))
						.collect(Collectors.toMap(IParameter::getKey, Function.identity())));
				mappingParameters.putAll(serviceLikelihoodParameter.findByAnalysisId(analysisForm.getParameter())
						.stream().map(duplicateParameter(analysis))
						.collect(Collectors.toMap(IParameter::getKey, Function.identity())));
				mappingParameters.putAll(serviceImpactParameter.findByAnalysisId(analysisForm.getParameter()).stream()
						.map(duplicateParameter(analysis))
						.collect(Collectors.toMap(IParameter::getKey, Function.identity())));
			} else {
				analysisForm.getScale().setLevel(analysisForm.getScale().getLevel() + 1);
				generateLikelihoodParameters(analysis, mappingParameters, Constant.DEFAULT_LIKELIHOOD_MAX_VALUE,
						analysisForm.getScale().getLevel());
				analysisForm.getImpacts()
						.forEach(generateImpactParameters(analysis, mappingParameters, analysisForm.getScale()));
			}

			if (analysisForm.getType().isQuantitative()
					&& analysis.getImpactParameters().stream()
							.noneMatch(parameter -> parameter.isMatch(Constant.DEFAULT_IMPACT_NAME))) {
				ScaleType scaleType = serviceScaleType.findOne(Constant.DEFAULT_IMPACT_NAME);
				analysis.getImpactParameters().stream().max((p1, p2) -> Integer.compare(p1.getLevel(), p2.getLevel()))
						.ifPresent(impact -> generateImpactParameters(analysis, mappingParameters,
								new Scale(scaleType, impact.getLevel() + 1, impact.getValue() * 0.001))
								.accept(scaleType.getId()));
			}

			analysis.updateType();

			final List<Asset> assets = serviceAsset.getAllFromAnalysis(analysisForm.getAsset());
			final Map<Integer, Asset> mappingAssets = assets.isEmpty() ? Collections.emptyMap()
					: new LinkedHashMap<>(assets.size());
			for (Asset asset : assets) {
				Asset duplication = asset.duplicate();
				analysis.add(duplication);
				mappingAssets.put(asset.getId(), duplication);
			}

			// add asset dependencies
			if (analysisForm.isAssetDependancy() && !assets.isEmpty()) {
				final Map<Long, AssetNode> nodes = new HashMap<>();
				final Map<Long, AssetImpact> assetImpacts = new HashMap<>();
				final Set<ScaleType> scales = new HashSet<>();

				for (AssetNode node : serviceAssetNode.findByAnalysisId(analysisForm.getAsset())) {
					final AssetImpact impact = assetImpacts.computeIfAbsent(node.getImpact().getId(),
							k -> node.getImpact().duplicate(mappingAssets.get(node.getAsset().getId())));

					final AssetNode myNode = node.duplicate(impact);
					analysis.getAssetNodes().add(myNode);
					nodes.put(node.getId(), myNode);
				}

				analysis.getAssetNodes().stream().filter(e -> !e.getEdges().isEmpty())
						.forEach(e -> e.setEdges(e.getEdges().values().stream()
								.map(b -> b.duplicate(e,
										nodes.get(b.getChild().getId())))
								.collect(Collectors.toMap(AssetEdge::getChild, Function.identity())))

						);

				assetImpacts.values().stream().forEach(e -> {
					scales.addAll(e.getAvailabilityImpacts().keySet());
					scales.addAll(e.getConfidentialityImpacts().keySet());
					scales.addAll(e.getIntegrityImpacts().keySet());
				});

				analysis.setIlrImpactTypes(scales.stream()
						.sorted((e1, e2) -> NaturalOrderComparator.compareTo(e1.getDisplayName(), e2.getDisplayName()))
						.toList());

				analysis.setDocuments(serviceSimpleDocument.findByAnalysisId(analysisForm.getAsset()).stream()
						.map(SimpleDocument::new)
						.collect(Collectors.toMap(SimpleDocument::getType, Function.identity(), (e1, e2) -> e1)));
			}

			final List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(analysisForm.getScenario());

			final Map<Integer, Scenario> mappingScenarios = scenarios.isEmpty() ? Collections.emptyMap()
					: new LinkedHashMap<>(scenarios.size());
			for (Scenario scenario : scenarios) {
				Scenario duplication = scenario.duplicate(mappingAssets);
				analysis.add(duplication);
				if (mappingScenarios != null)
					mappingScenarios.put(scenario.getId(), duplication);
			}

			if (!(mappingScenarios == null || mappingAssets == null) && analysisForm.isAssessment()) {

				List<Assessment> assessments = serviceAssessment.getAllFromAnalysis(analysisForm.getScenario());
				for (Assessment assessment : assessments) {
					Assessment duplication = assessment.duplicate();
					duplication.setScenario(mappingScenarios.get(assessment.getScenario().getId()));
					duplication.setAsset(mappingAssets.get(assessment.getAsset().getId()));
					duplication.setImpacts(new LinkedList<>());
					assessment.getImpacts().forEach(impact -> {
						IValue value = impact.duplicate();
						if (value instanceof AbstractValue)
							((AbstractValue) value).setParameter((ILevelParameter) mappingParameters
									.get(((AbstractValue) value).getParameter().getKey()));
						duplication.setImpact(value);
					});

					if (assessment.getLikelihood() != null) {
						IValue value = assessment.getLikelihood().duplicate();
						if (value instanceof AbstractValue)
							((AbstractValue) value).setParameter((ILevelParameter) mappingParameters
									.get(((AbstractValue) value).getParameter().getKey()));
						duplication.setLikelihood(value);
					}

					analysis.add(duplication);
				}

				if (analysis.isQuantitative() && !analysis.getAssessments().isEmpty()) {
					analysis.getImpactParameters().stream()
							.filter(p -> p.isMatch(Constant.DEFAULT_IMPACT_NAME) && p.getLevel() == 0).findAny()
							.ifPresent(p -> {
								analysis.getAssessments().parallelStream().forEach(assessment -> {
									if (!assessment.getImpacts().stream().anyMatch(
											value -> value.getName().equals(Constant.DEFAULT_IMPACT_NAME))) {
										assessment.getImpacts().add(new RealValue(0d, p));
										AssessmentAndRiskProfileManager.ComputeAlE(assessment);
									}
								});
							});
				}

			}

			Map<Integer, Phase> mappingPhases;
			if (analysisForm.isPhase()) {
				List<Phase> phases = servicePhase
						.getAllFromAnalysis(analysisForm.getStandards().get(0).getIdAnalysis());
				mappingPhases = new LinkedHashMap<>(phases.size());
				for (Phase phase : phases) {
					Phase phase1 = phase.duplicate(analysis);
					analysis.add(phase1);
					mappingPhases.put(phase.getNumber(), phase1);
				}
			} else {
				mappingPhases = new LinkedHashMap<>(1);
				Calendar calendar = Calendar.getInstance();
				Phase phase = new Phase(PHASE_DEFAULT);
				phase.setAnalysis(analysis);
				phase.setBeginDate(new java.sql.Date(calendar.getTimeInMillis()));
				calendar.add(Calendar.YEAR, 1);
				phase.setEndDate(new java.sql.Date(calendar.getTimeInMillis()));
				mappingPhases.put(PHASE_DEFAULT, phase);
				analysis.add(phase);
			}

			if (analysisForm.getStandards().size() == 1) {
				AnalysisStandardBaseInfo standardBaseInfo = analysisForm.getStandards().get(0);
				if (standardBaseInfo.getIdAnalysis() == analysisForm.getProfile()
						&& standardBaseInfo.getIdAnalysisStandard() < 1) {
					for (AnalysisStandard analysisStandard : serviceAnalysisStandard
							.getAllFromAnalysis(standardBaseInfo.getIdAnalysis()))
						analysis.add(duplicator.duplicateAnalysisStandard(analysisStandard, mappingPhases,
								mappingParameters, mappingAssets, false));
				} else
					analysis.add(duplicator.duplicateAnalysisStandard(
							serviceAnalysisStandard.get(standardBaseInfo.getIdAnalysisStandard()), mappingPhases,
							mappingParameters,
							mappingAssets, false));
			} else {
				for (AnalysisStandardBaseInfo standardBaseInfo : analysisForm.getStandards())
					analysis.add(duplicator.duplicateAnalysisStandard(
							serviceAnalysisStandard.get(standardBaseInfo.getIdAnalysisStandard()), mappingPhases,
							mappingParameters,
							mappingAssets, false));
			}

			if (!(mappingScenarios == null || mappingAssets == null)) {
				if (analysisForm.isRiskProfile()) {
					final Map<String, Measure> measures = analysis.getAnalysisStandards().values().stream()
							.flatMap(analysisStandard -> analysisStandard.getMeasures().stream())
							.collect(Collectors.toMap(Measure::getKeyName, Function.identity()));
					final List<RiskProfile> riskProfiles = serviceRiskProfile
							.getAllFromAnalysis(analysisForm.getScenario());
					for (RiskProfile riskProfile : riskProfiles)
						analysis.getRiskProfiles().add(
								riskProfile.duplicate(mappingAssets, mappingScenarios, mappingParameters, measures));
				}
				AssessmentAndRiskProfileManager.updateRiskDendencies(analysis, null);
			}

			while (serviceAnalysis.countByIdentifier(analysis.getIdentifier()) > 1)
				analysis.setIdentifier(language.getAlpha3() + "_"
						+ new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(history.generateDate()));

			serviceAnalysis.saveOrUpdate(analysis);

			return JsonMessage.success(messageSource.getMessage("success.analysis_custom.create", null,
					"Your analysis has been successfully created", locale));
		} catch (TrickException e) {
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage("error.unknown.create.analysis", null,
					"An unknown error occurred while saving analysis", locale));
		}
	}

	/**
	 * Generates likelihood parameters for the analysis.
	 *
	 * @param analysis          The analysis object.
	 * @param mappingParameters The mapping of parameter names to parameter objects.
	 * @param maxValue          The maximum value for the likelihood parameters.
	 * @param maxlevel          The maximum level for the likelihood parameters.
	 */
	private void generateLikelihoodParameters(Analysis analysis, Map<String, IParameter> mappingParameters,
			int maxValue, int maxlevel) {

		double currentValue = maxValue < 0 ? 12 : maxValue;

		List<LikelihoodParameter> likelihoodParameters = new ArrayList<>(maxlevel);

		if (maxlevel % 2 == 0) {
			for (int level = maxlevel - 1; level >= 0; level--) {
				if (likelihoodParameters.isEmpty())
					likelihoodParameters.add(new LikelihoodParameter(level, "p" + level, currentValue));
				else
					likelihoodParameters.add(new LikelihoodParameter(level, "p" + level, currentValue *= 0.5));
			}
		} else {
			LikelihoodParameter prev = null;
			for (int level = maxlevel - 2; level > 0; level -= 2) {
				LikelihoodParameter current = new LikelihoodParameter(level, "p" + level),
						next = prev == null ? new LikelihoodParameter(level + 1, "p" + (level + 1), currentValue)
								: prev;
				if (prev == null)
					likelihoodParameters.add(next);
				prev = new LikelihoodParameter(level - 1, "p" + (level - 1));
				prev.setValue(currentValue *= 0.5);
				likelihoodParameters.add(current);
				likelihoodParameters.add(prev);
				current.setValue(Math.sqrt(next.getValue() * prev.getValue()));
			}
		}

		LikelihoodParameter.ComputeScales(likelihoodParameters);
		likelihoodParameters.forEach(parameter -> {
			analysis.add(parameter);
			mappingParameters.put(parameter.getKey(), parameter);
		});
	}

	/**
	 * Generates impact parameters based on the given analysis, mapping parameters,
	 * and scale.
	 * 
	 * @param analysis          The analysis object.
	 * @param mappingParameters The mapping parameters map.
	 * @param scale             The scale object.
	 * @return A consumer that generates impact parameters.
	 */
	private Consumer<? super Integer> generateImpactParameters(Analysis analysis,
			Map<String, IParameter> mappingParameters, Scale scale) {
		return id -> {
			ScaleType scaleType = serviceScaleType.findOne(id);
			List<ImpactParameter> impacts = new ArrayList<>(scale.getLevel());
			double currentValue = scale.getMaxValue() * 1000;
			if (scale.getLevel() % 2 == 0) {
				for (int level = scale.getLevel() - 1; level >= 0; level--) {
					if (impacts.isEmpty())
						impacts.add(
								new ImpactParameter(scaleType, level, scaleType.getAcronym() + level, currentValue));
					else
						impacts.add(new ImpactParameter(scaleType, level, scaleType.getAcronym() + level,
								currentValue *= 0.5));
				}
			} else {
				ImpactParameter prev = null;
				for (int level = scale.getLevel() - 2; level > 0; level -= 2) {
					ImpactParameter current = new ImpactParameter(scaleType, level, scaleType.getAcronym() + level),
							next = prev == null
									? new ImpactParameter(scaleType, level + 1, scaleType.getAcronym() + (level + 1),
											currentValue)
									: prev;
					if (prev == null)
						impacts.add(next);
					prev = new ImpactParameter(scaleType, level - 1, scaleType.getAcronym() + (level - 1));
					prev.setValue(currentValue *= 0.5);
					impacts.add(current);
					impacts.add(prev);
					current.setValue(Math.sqrt(next.getValue() * prev.getValue()));
				}
			}
			ImpactParameter.ComputeScales(impacts);
			impacts.forEach(parameter -> {
				analysis.add(parameter);
				mappingParameters.put(parameter.getKey(), parameter);
			});
		};
	}

	/**
	 * Returns a function that duplicates the given parameter and adds it to the
	 * analysis.
	 *
	 * @param analysis the analysis to add the duplicated parameter to
	 * @return a function that duplicates the parameter and adds it to the analysis
	 */
	private Function<? super IParameter, ? extends IParameter> duplicateParameter(Analysis analysis) {
		return parameter -> {
			IParameter clone = parameter.duplicate();
			analysis.add(clone);
			return clone;
		};
	}

	/**
	 * Generates a standard log based on the given parameters.
	 *
	 * @param baseAnalysis     the base analysis string
	 * @param analysisForm     the analysis form object
	 * @param defaultProfileId the default profile ID
	 * @param analysisLocale   the analysis locale
	 * @return the generated standard log string
	 * @throws Exception if an error occurs during the generation process
	 */
	private String generateStandardLog(String baseAnalysis, AnalysisForm analysisForm, int defaultProfileId,
			Locale analysisLocale) throws Exception {
		if (analysisForm.getStandards().size() == 1
				&& analysisForm.getStandards().get(0).getIdAnalysis() == defaultProfileId)
			return baseAnalysis;
		boolean isFirst = true;
		for (AnalysisStandardBaseInfo standardBaseInfo : analysisForm.getStandards()) {
			String company = serviceAnalysis.getCustomerNameFromId(standardBaseInfo.getIdAnalysis()),
					label = serviceAnalysis.getLabelFromId(standardBaseInfo.getIdAnalysis()),
					version = serviceAnalysis.getVersionOfAnalysis(standardBaseInfo.getIdAnalysis()),
					StandardName = serviceAnalysisStandard
							.getStandardNameById(standardBaseInfo.getIdAnalysisStandard());

			if (isFirst) {
				if (analysisForm.isPhase()) {
					baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.phase",
							new String[] { label, company, version },
							String.format("Phase based on: %s, customer: %s, version: %s", label, company, version),
							analysisLocale);
				}
				isFirst = false;
			}

			baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.standard",
					new Object[] { StandardName, label, company, version },
					String.format("Standard (%s) based on: %s, customer: %s, version: %s", StandardName, label, company,
							version),
					analysisLocale);

		}
		return baseAnalysis;
	}

	/**
	 * Validates the standards for analysis.
	 * 
	 * @param standrads        The list of analysis standard base information.
	 * @param errors           The map to store any validation errors.
	 * @param principal        The principal object representing the current user.
	 * @param defaultProfileId The default profile ID.
	 * @param locale           The locale for error messages.
	 */
	private void validateStandards(List<AnalysisStandardBaseInfo> standrads, Map<String, String> errors,
			Principal principal, int defaultProfileId, Locale locale) {
		if (standrads == null || standrads.isEmpty())
			errors.put("standards",
					messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		else if (standrads.size() == 1) {
			AnalysisStandardBaseInfo analysisStandardBaseInfo = standrads.get(0);
			if (analysisStandardBaseInfo.getIdAnalysis() == defaultProfileId) {
				if (analysisStandardBaseInfo.getIdAnalysisStandard() > 0)
					validateStandards(analysisStandardBaseInfo, errors, principal, locale);
			} else if (!serviceUserAnalysisRight.hasRightOrOwner(analysisStandardBaseInfo.getIdAnalysis(),
					principal.getName(), AnalysisRight.EXPORT))
				errors.put("standards",
						messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		} else
			standrads.forEach(
					analysisStandardBaseInfo -> validateStandards(analysisStandardBaseInfo, errors, principal, locale));
	}

	/**
	 * Validates the standards for the given analysis standard base information.
	 * 
	 * @param analysisStandardBaseInfo The analysis standard base information to
	 *                                 validate.
	 * @param errors                   A map to store any validation errors.
	 * @param principal                The principal object representing the current
	 *                                 user.
	 * @param locale                   The locale to use for error messages.
	 */
	private void validateStandards(AnalysisStandardBaseInfo analysisStandardBaseInfo, Map<String, String> errors,
			Principal principal, Locale locale) {
		if (!serviceUserAnalysisRight.hasRightOrOwner(analysisStandardBaseInfo.getIdAnalysis(), principal.getName(),
				AnalysisRight.EXPORT))
			errors.put("standards",
					messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		else if (analysisStandardBaseInfo.getIdAnalysisStandard() > 0) {
			if (!serviceAnalysisStandard.belongsToAnalysis(analysisStandardBaseInfo.getIdAnalysis(),
					analysisStandardBaseInfo.getIdAnalysisStandard()))
				errors.put("standards",
						messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		}
	}

	/**
	 * Retrieves a list of AnalysisBaseInfo objects based on the provided customer
	 * ID and user principal.
	 *
	 * @param id        The ID of the customer.
	 * @param principal The user principal.
	 * @return A list of AnalysisBaseInfo objects.
	 */
	@GetMapping(value = "/Customer/{id}", headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomer(@PathVariable Integer id, Principal principal) {
		return serviceAnalysis.getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(id, principal.getName(),
				AnalysisRight.highRightFrom(AnalysisRight.EXPORT));
	}

	/**
	 * Retrieves a list of AnalysisBaseInfo objects based on the provided customer
	 * ID, identifier, and user principal.
	 * 
	 * @param id         The customer ID.
	 * @param identifier The identifier.
	 * @param principal  The user principal.
	 * @return A list of AnalysisBaseInfo objects.
	 */
	@GetMapping(value = "/Customer/{id}/Identifier/{identifier}", headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomerAndIdentifier(@PathVariable Integer id,
			@PathVariable String identifier, Principal principal) {
		return serviceAnalysis.getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(id, principal.getName(),
				identifier, AnalysisRight.highRightFrom(AnalysisRight.EXPORT));
	}
}