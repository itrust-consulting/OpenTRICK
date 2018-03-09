package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.PHASE_DEFAULT;
import static lu.itrust.business.TS.constants.Constant.ROLE_MIN_USER;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.Duplicator;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.controller.form.AnalysisForm;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceDynamicParameter;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.database.service.ServiceItemInformation;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.TS.database.service.ServiceMaturityParameter;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.database.service.ServiceRiskProfile;
import lu.itrust.business.TS.database.service.ServiceScaleType;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.model.analysis.helper.AnalysisStandardBaseInfo;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.parameter.ILevelParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.value.AbstractValue;
import lu.itrust.business.TS.model.parameter.value.impl.RealValue;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.scale.Scale;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.validator.CustomAnalysisValidator;

/**
 * ControllerAnalysisCreate.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Oct 13, 2014
 */
@PreAuthorize(ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/Build")
public class ControllerAnalysisCreate {

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceRiskInformation serviceRiskInformation;

	@Autowired
	private ServiceItemInformation serviceItemInformation;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceDynamicParameter serviceDynamicParameter;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceMaturityParameter serviceMaturityParameter;

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceRiskAcceptanceParameter serviceRiskAcceptanceParameter;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceRiskProfile serviceRiskProfile;

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private ServiceScaleType serviceScaleType;

	@Autowired
	private Duplicator duplicator;

	@RequestMapping(method = RequestMethod.GET)
	public String buildCustom(HttpSession session, Principal principal, Model model, Locale locale) throws Exception {

		// add languages
		model.addAttribute("languages", serviceLanguage.getAll());
		// add only customers of the current user
		model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));

		model.addAttribute("profiles", serviceAnalysis.getAllProfiles());

		model.addAttribute("impacts", serviceScaleType.findAllExpect(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME));
		// set author as the username

		User user = serviceUser.get(principal.getName());

		model.addAttribute("author", user.getFirstName() + " " + user.getLastName());

		model.addAttribute("types", AnalysisType.values());

		model.addAttribute("locale", locale.getLanguage().toUpperCase());

		return "analyses/all/forms/buildAnalysis";

	}

	@RequestMapping(value = "/Save", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
	public @ResponseBody Object buildCustomSave(@ModelAttribute AnalysisForm analysisForm, Principal principal, Locale locale) throws Exception {
		try {
			if (!serviceDataValidation.isRegistred(AnalysisForm.class))
				serviceDataValidation.register(new CustomAnalysisValidator());

			Map<String, String> errors = serviceDataValidation.validate(analysisForm);
			for (String error : errors.keySet())
				errors.put(error, serviceDataValidation.ParseError(errors.get(error), messageSource, locale));

			analysisForm.updateProfile();

			analysisForm.getImpacts().removeIf(id -> id < 1);

			if (analysisForm.getAsset() > 0 && !serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getAsset(), principal.getName(), AnalysisRight.EXPORT))
				errors.put("asset", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (analysisForm.getScenario() > 0 && !(analysisForm.getScenario() == analysisForm.getProfile()
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getScenario(), principal.getName(), AnalysisRight.EXPORT)))
				errors.put("scenario", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			validateStandards(analysisForm.getStandards(), errors, principal, analysisForm.getProfile(), locale);

			if (analysisForm.isAssessment()
					&& (analysisForm.getScenario() < 1 || analysisForm.getScenario() != analysisForm.getAsset() || analysisForm.getParameter() != analysisForm.getAsset()))
				errors.put("assessment", messageSource.getMessage("error.analysis_custom.assessment.invalid", null, "Risk estimation cannot be selected", locale));

			if (analysisForm.getScope() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null, "No default profile, please select a profile", locale));
				errors.put("scope", messageSource.getMessage("error.analysis_custom.scope.empty", null, "No default profile, scope cannot be empty", locale));
			} else if (!(analysisForm.getScope() == analysisForm.getProfile()
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getScope(), principal.getName(), AnalysisRight.EXPORT)))
				errors.put("scope", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (!analysisForm.getImpacts().isEmpty()) {
				if (analysisForm.getScale() == null)
					errors.put("scale.level", messageSource.getMessage("error.scale.level.empty", null, "Level cannot be empty", locale));
				else {
					if (analysisForm.getScale().getLevel() < 2)
						errors.put("scale.level", messageSource.getMessage("error.scale.level.bad_value", new Object[] { 2 }, "Level must be 2 or great", locale));
					if (analysisForm.getScale().getMaxValue() < 1)
						analysisForm.getScale().setMaxValue(300000);
				}
			}

			if (analysisForm.getParameter() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null, "No default profile, please select a profile", locale));
				errors.put("parameter", messageSource.getMessage("error.analysis_custom.parameter.empty", null, "No default profile, parameter cannot be empty", locale));
			} else if (!(analysisForm.getParameter() == analysisForm.getProfile()
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getParameter(), principal.getName(), AnalysisRight.EXPORT)))
				errors.put("parameter", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (analysisForm.getRiskInformation() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null, "No default profile, please select a profile", locale));
				errors.put("riskInformation",
						messageSource.getMessage("error.analysis_custom.risk_information.empty", null, "No default profile, risk information cannot be empty", locale));
			} else if (!(analysisForm.getRiskInformation() == analysisForm.getProfile()
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getRiskInformation(), principal.getName(), AnalysisRight.EXPORT)))
				errors.put("riskInformation", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			Customer customer = null;
			Language language = null;

			if (analysisForm.getLanguage() > 0) {
				language = serviceLanguage.get(analysisForm.getLanguage());
				if (language == null)
					errors.put("language", messageSource.getMessage("error.language.not_found", null, "Language cannot be found", locale));
			}

			if (analysisForm.getCustomer() > 0) {
				customer = serviceCustomer.getFromUsernameAndId(principal.getName(), analysisForm.getCustomer());
				if (customer == null)
					errors.put("customer", messageSource.getMessage("error.customer.not_found", null, "Customer cannot be found", locale));
				else if (!customer.isCanBeUsed())
					errors.put("customer", messageSource.getMessage("error.customer.invalid", null, "Customer cannot be used", locale));
				else if (serviceAnalysis.existsByNameAndCustomerId(analysisForm.getName(), analysisForm.getCustomer()))
					errors.put("name", messageSource.getMessage("error.analysis.name.in_used.for.customer", new Object[] { customer.getOrganisation() },
							String.format("Name cannot be used for %s", customer.getOrganisation()), locale));
			}

			if (!errors.isEmpty())
				return errors;

			History history = analysisForm.generateHistory();
			String identifier = language.getAlpha3() + "_" + new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(history.getDate());
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
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.assets", new String[] { label, company, version },
						String.format("Assets based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			if (analysisForm.getScenario() > 1 && analysisForm.getScenario() != analysisForm.getProfile()) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getScenario());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getScenario());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getScenario());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.scenarios", new String[] { label, company, version },
						String.format("Scenarios based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
				if (analysisForm.isAssessment())
					baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.estimation", new String[] { label, company, version },
							String.format("Risk estimation based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
				if (analysisForm.isRiskProfile())
					baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.risk_profile", new String[] { label, company, version },
							String.format("Risk profile based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			if (analysisForm.getParameter() > 1 && analysisForm.getParameter() != analysisForm.getProfile()) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getParameter());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getParameter());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getParameter());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.parameters", new String[] { label, company, version },
						String.format("Parameters based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			if (analysisForm.getRiskInformation() > 1 && analysisForm.getRiskInformation() != analysisForm.getProfile()) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getRiskInformation());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getRiskInformation());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getRiskInformation());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.risk_information", new String[] { label, company, version },
						String.format("Risk information based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			if (analysisForm.getScope() > 1 && analysisForm.getScope() != analysisForm.getProfile()) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getScope());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getScope());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getScope());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.scope", new String[] { label, company, version },
						String.format("Scope based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			baseAnalysis = generateStandardLog(baseAnalysis, analysisForm, analysisForm.getProfile(), analysisLocale);

			history.setComment(history.getComment() + baseAnalysis);
			List<ItemInformation> itemInformations = serviceItemInformation.getAllFromAnalysis(analysisForm.getScope());
			for (ItemInformation itemInformation : itemInformations)
				analysis.add(itemInformation.duplicate());

			List<RiskInformation> riskInformations = serviceRiskInformation.getAllFromAnalysis(analysisForm.getRiskInformation());
			for (RiskInformation riskInformation : riskInformations)
				analysis.add(riskInformation.duplicate());

			Map<String, IParameter> mappingParameters = serviceSimpleParameter.findByAnalysisId(analysisForm.getParameter()).stream().map(duplicateParameter(analysis))
					.collect(Collectors.toMap(IParameter::getKey, Function.identity()));

			mappingParameters.putAll(serviceMaturityParameter.findByAnalysisId(analysisForm.getParameter()).stream().map(duplicateParameter(analysis))
					.collect(Collectors.toMap(IParameter::getKey, Function.identity())));

			mappingParameters.putAll(serviceDynamicParameter.findByAnalysisId(analysisForm.getParameter()).stream().map(duplicateParameter(analysis))
					.collect(Collectors.toMap(IParameter::getKey, Function.identity())));

			if (analysisForm.getImpacts().isEmpty()) {
				mappingParameters.putAll(serviceRiskAcceptanceParameter.findByAnalysisId(analysisForm.getParameter()).stream().map(duplicateParameter(analysis))
						.collect(Collectors.toMap(IParameter::getKey, Function.identity())));
				mappingParameters.putAll(serviceLikelihoodParameter.findByAnalysisId(analysisForm.getParameter()).stream().map(duplicateParameter(analysis))
						.collect(Collectors.toMap(IParameter::getKey, Function.identity())));
				mappingParameters.putAll(serviceImpactParameter.findByAnalysisId(analysisForm.getParameter()).stream().map(duplicateParameter(analysis))
						.collect(Collectors.toMap(IParameter::getKey, Function.identity())));
			} else {
				analysisForm.getScale().setLevel(analysisForm.getScale().getLevel() + 1);
				generateLikelihoodParameters(analysis, mappingParameters, Constant.DEFAULT_LIKELIHOOD_MAX_VALUE, analysisForm.getScale().getLevel());
				analysisForm.getImpacts().forEach(generateImpactParameters(analysis, mappingParameters, analysisForm.getScale()));
			}

			if (analysisForm.getType().isQuantitative()
					&& !analysis.getImpactParameters().parallelStream().anyMatch(parameter -> parameter.isMatch(Constant.DEFAULT_IMPACT_NAME))) {
				ScaleType scaleType = serviceScaleType.findOne(Constant.DEFAULT_IMPACT_NAME);
				analysis.getImpactParameters().parallelStream().max((p1, p2) -> Integer.compare(p1.getLevel(), p2.getLevel()))
						.ifPresent(impact -> generateImpactParameters(analysis, mappingParameters, new Scale(scaleType, impact.getLevel() + 1, impact.getValue() * 0.001))
								.accept(scaleType.getId()));
			}
			
			analysis.updateType();

			List<Asset> assets = serviceAsset.getAllFromAnalysis(analysisForm.getAsset());
			Map<Integer, Asset> mappingAssets = assets.isEmpty() ? null : new LinkedHashMap<Integer, Asset>(assets.size());
			for (Asset asset : assets) {
				Asset duplication = asset.duplicate();
				analysis.add(duplication);
				mappingAssets.put(asset.getId(), duplication);
			}

			List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(analysisForm.getScenario());

			Map<Integer, Scenario> mappingScenarios = scenarios.isEmpty() || assets.isEmpty() ? null : new LinkedHashMap<Integer, Scenario>(scenarios.size());
			for (Scenario scenario : scenarios) {
				Scenario duplication = scenario.duplicate(mappingAssets);
				analysis.add(duplication);
				if (mappingScenarios != null)
					mappingScenarios.put(scenario.getId(), duplication);
			}

			if (!(mappingScenarios == null || mappingAssets == null)) {

				if (analysisForm.isAssessment()) {
					List<Assessment> assessments = serviceAssessment.getAllFromAnalysis(analysisForm.getScenario());
					for (Assessment assessment : assessments) {
						Assessment duplication = assessment.duplicate();
						duplication.setScenario(mappingScenarios.get(assessment.getScenario().getId()));
						duplication.setAsset(mappingAssets.get(assessment.getAsset().getId()));
						duplication.setImpacts(new LinkedList<>());
						assessment.getImpacts().forEach(impact -> {
							AbstractValue value = (AbstractValue) impact.duplicate();
							value.setParameter((ILevelParameter) mappingParameters.get(value.getParameter().getKey()));
							duplication.setImpact(value);
						});
						analysis.add(duplication);
					}

					if (analysis.isQuantitative() && !analysis.getAssessments().isEmpty()) {
						analysis.getImpactParameters().parallelStream().filter(impact -> impact.isMatch(Constant.DEFAULT_IMPACT_NAME) && impact.getLevel() == 0).findAny()
								.ifPresent(impact -> {
									ValueFactory factory = new ValueFactory(analysis.getParameters());
									analysis.getAssessments().parallelStream().forEach(assessment -> {
										if (!assessment.getImpacts().parallelStream().anyMatch(value -> value.getParameter().isMatch(Constant.DEFAULT_IMPACT_NAME))) {
											assessment.getImpacts().add(new RealValue(0d, impact));
											AssessmentAndRiskProfileManager.ComputeAlE(assessment, factory);
										}
									});
								});
					}
				}

			}

			Map<Integer, Phase> mappingPhases;
			if (analysisForm.isPhase()) {
				List<Phase> phases = servicePhase.getAllFromAnalysis(analysisForm.getStandards().get(0).getIdAnalysis());
				mappingPhases = new LinkedHashMap<Integer, Phase>(phases.size());
				for (Phase phase : phases) {
					Phase phase1 = phase.duplicate(analysis);
					analysis.add(phase1);
					mappingPhases.put(phase.getNumber(), phase1);
				}
			} else {
				mappingPhases = new LinkedHashMap<Integer, Phase>(1);
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
				if (standardBaseInfo.getIdAnalysis() == analysisForm.getProfile() && standardBaseInfo.getIdAnalysisStandard() < 1) {
					for (AnalysisStandard analysisStandard : serviceAnalysisStandard.getAllFromAnalysis(standardBaseInfo.getIdAnalysis()))
						analysis.add(duplicator.duplicateAnalysisStandard(analysisStandard, mappingPhases, mappingParameters, mappingAssets, false));
				} else
					analysis.add(duplicator.duplicateAnalysisStandard(serviceAnalysisStandard.get(standardBaseInfo.getIdAnalysisStandard()), mappingPhases, mappingParameters,
							mappingAssets, false));
			} else {
				for (AnalysisStandardBaseInfo standardBaseInfo : analysisForm.getStandards())
					analysis.add(duplicator.duplicateAnalysisStandard(serviceAnalysisStandard.get(standardBaseInfo.getIdAnalysisStandard()), mappingPhases, mappingParameters,
							mappingAssets, false));
			}

			if (!(mappingScenarios == null || mappingAssets == null)) {
				if (analysisForm.isRiskProfile()) {
					Map<String, Measure> measures = analysis.getAnalysisStandards().stream().flatMap(analysisStandard -> analysisStandard.getMeasures().stream())
							.collect(Collectors.toMap(Measure::getKeyName, Function.identity()));
					List<RiskProfile> riskProfiles = serviceRiskProfile.getAllFromAnalysis(analysisForm.getScenario());
					for (RiskProfile riskProfile : riskProfiles)
						analysis.getRiskProfiles().add(riskProfile.duplicate(mappingAssets, mappingScenarios, mappingParameters, measures));
				}
				AssessmentAndRiskProfileManager.UpdateRiskDendencies(analysis, null);
			}

			while (serviceAnalysis.countByIdentifier(analysis.getIdentifier()) > 1)
				analysis.setIdentifier(language.getAlpha3() + "_" + new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(history.generateDate()));

			serviceAnalysis.saveOrUpdate(analysis);

			return JsonMessage.Success(messageSource.getMessage("success.analysis_custom.create", null, "Your analysis has been successfully created", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.create.analysis", null, "An unknown error occurred while saving analysis", locale));
		}
	}

	private void generateLikelihoodParameters(Analysis analysis, Map<String, IParameter> mappingParameters, int maxValue, int maxlevel) {

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
						next = prev == null ? new LikelihoodParameter(level + 1, "p" + (level + 1), currentValue) : prev;
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

	private Consumer<? super Integer> generateImpactParameters(Analysis analysis, Map<String, IParameter> mappingParameters, Scale scale) {
		return id -> {
			ScaleType scaleType = serviceScaleType.findOne(id);
			List<ImpactParameter> impacts = new ArrayList<>(scale.getLevel());
			double currentValue = scale.getMaxValue() * 1000;
			if (scale.getLevel() % 2 == 0) {
				for (int level = scale.getLevel() - 1; level >= 0; level--) {
					if (impacts.isEmpty())
						impacts.add(new ImpactParameter(scaleType, level, scaleType.getAcronym() + level, currentValue));
					else
						impacts.add(new ImpactParameter(scaleType, level, scaleType.getAcronym() + level, currentValue *= 0.5));
				}
			} else {
				ImpactParameter prev = null;
				for (int level = scale.getLevel() - 2; level > 0; level -= 2) {
					ImpactParameter current = new ImpactParameter(scaleType, level, scaleType.getAcronym() + level),
							next = prev == null ? new ImpactParameter(scaleType, level + 1, scaleType.getAcronym() + (level + 1), currentValue) : prev;
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

	private Function<? super IParameter, ? extends IParameter> duplicateParameter(Analysis analysis) {
		return parameter -> {
			IParameter clone = parameter.duplicate();
			analysis.add(clone);
			return clone;
		};
	}

	private String generateStandardLog(String baseAnalysis, AnalysisForm analysisForm, int defaultProfileId, Locale analysisLocale) throws Exception {
		if (analysisForm.getStandards().size() == 1 && analysisForm.getStandards().get(0).getIdAnalysis() == defaultProfileId)
			return baseAnalysis;
		boolean isFirst = true;
		for (AnalysisStandardBaseInfo standardBaseInfo : analysisForm.getStandards()) {
			String company = serviceAnalysis.getCustomerNameFromId(standardBaseInfo.getIdAnalysis()), label = serviceAnalysis.getLabelFromId(standardBaseInfo.getIdAnalysis()),
					version = serviceAnalysis.getVersionOfAnalysis(standardBaseInfo.getIdAnalysis()),
					StandardName = serviceAnalysisStandard.getStandardNameById(standardBaseInfo.getIdAnalysisStandard());

			if (isFirst) {
				if (analysisForm.isPhase()) {
					baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.phase", new String[] { label, company, version },
							String.format("Phase based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
				}
				isFirst = false;
			}

			baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.standard", new Object[] { StandardName, label, company, version },
					String.format("Standard (%s) based on: %s, customer: %s, version: %s", StandardName, label, company, version), analysisLocale);

		}
		return baseAnalysis;
	}

	private void validateStandards(List<AnalysisStandardBaseInfo> standrads, Map<String, String> errors, Principal principal, int defaultProfileId, Locale locale) {
		if (standrads == null || standrads.isEmpty())
			errors.put("standards", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		else if (standrads.size() == 1) {
			AnalysisStandardBaseInfo analysisStandardBaseInfo = standrads.get(0);
			if (analysisStandardBaseInfo.getIdAnalysis() == defaultProfileId) {
				if (analysisStandardBaseInfo.getIdAnalysisStandard() > 0)
					validateStandards(analysisStandardBaseInfo, errors, principal, locale);
			} else if (!serviceUserAnalysisRight.hasRightOrOwner(analysisStandardBaseInfo.getIdAnalysis(), principal.getName(), AnalysisRight.EXPORT))
				errors.put("standards", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		} else
			standrads.forEach(analysisStandardBaseInfo -> validateStandards(analysisStandardBaseInfo, errors, principal, locale));
	}

	private void validateStandards(AnalysisStandardBaseInfo analysisStandardBaseInfo, Map<String, String> errors, Principal principal, Locale locale) {
		if (!serviceUserAnalysisRight.hasRightOrOwner(analysisStandardBaseInfo.getIdAnalysis(), principal.getName(), AnalysisRight.EXPORT))
			errors.put("standards", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		else if (analysisStandardBaseInfo.getIdAnalysisStandard() > 0) {
			if (!serviceAnalysisStandard.belongsToAnalysis(analysisStandardBaseInfo.getIdAnalysis(), analysisStandardBaseInfo.getIdAnalysisStandard()))
				errors.put("standards", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
		}
	}

	@RequestMapping(value = "/Customer/{id}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomer(@PathVariable Integer id, Principal principal) {
		return serviceAnalysis.getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(id, principal.getName(), AnalysisRight.highRightFrom(AnalysisRight.EXPORT));
	}

	@RequestMapping(value = "/Customer/{id}/Identifier/{identifier}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomerAndIdentifier(@PathVariable Integer id, @PathVariable String identifier, Principal principal) {
		return serviceAnalysis.getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(id, principal.getName(), identifier, AnalysisRight.highRightFrom(AnalysisRight.EXPORT));
	}
}