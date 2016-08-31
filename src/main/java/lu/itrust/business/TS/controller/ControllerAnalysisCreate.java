package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

import lu.itrust.business.TS.component.Duplicator;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceItemInformation;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.database.service.ServiceRiskProfile;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.model.analysis.helper.AnalysisForm;
import lu.itrust.business.TS.model.analysis.helper.AnalysisStandardBaseInfo;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
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
@PreAuthorize(Constant.ROLE_MIN_USER)
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
	private ServiceParameter serviceParameter;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceRiskProfile serviceRiskProfile;

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private Duplicator duplicator;

	@RequestMapping(method = RequestMethod.GET)
	public String buildCustom(HttpSession session, Principal principal, Model model, Locale locale) throws Exception {

		// add languages
		model.addAttribute("languages", serviceLanguage.getAll());
		// add only customers of the current user
		model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));

		model.addAttribute("profiles", serviceAnalysis.getAllProfiles());
		// set author as the username

		User user = serviceUser.get(principal.getName());

		model.addAttribute("author", user.getFirstName() + " " + user.getLastName());

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

			int defaultProfileId = analysisForm.getProfile() < 1 ? serviceAnalysis.getDefaultProfileId() : analysisForm.getProfile();

			analysisForm.setDefaultProfile(defaultProfileId);

			if (analysisForm.getAsset() > 0 && !serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getAsset(), principal.getName(), AnalysisRight.EXPORT))
				errors.put("asset", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (analysisForm.getScenario() > 0 && !(analysisForm.getScenario() == defaultProfileId
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getScenario(), principal.getName(), AnalysisRight.EXPORT)))
				errors.put("scenario", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			validateStandards(analysisForm.getStandards(), errors, principal, defaultProfileId, locale);

			if (analysisForm.isAssessment() && (analysisForm.getScenario() < 1 || analysisForm.getScenario() != analysisForm.getAsset()))
				errors.put("assessment", messageSource.getMessage("error.analysis_custom.assessment.invalid", null, "Risk estimation cannot be selected", locale));

			if (analysisForm.getScope() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null, "No default profile, please select a profile", locale));
				errors.put("scope", messageSource.getMessage("error.analysis_custom.scope.empty", null, "No default profile, scope cannot be empty", locale));
			} else if (!(analysisForm.getScope() == defaultProfileId
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getScope(), principal.getName(), AnalysisRight.EXPORT)))
				errors.put("scope", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (analysisForm.getParameter() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null, "No default profile, please select a profile", locale));
				errors.put("parameter", messageSource.getMessage("error.analysis_custom.parameter.empty", null, "No default profile, parameter cannot be empty", locale));
			} else if (!(analysisForm.getParameter() == defaultProfileId
					|| serviceUserAnalysisRight.hasRightOrOwner(analysisForm.getParameter(), principal.getName(), AnalysisRight.EXPORT)))
				errors.put("parameter", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (analysisForm.getRiskInformation() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null, "No default profile, please select a profile", locale));
				errors.put("riskInformation",
						messageSource.getMessage("error.analysis_custom.risk_information.empty", null, "No default profile, risk information cannot be empty", locale));
			} else if (!(analysisForm.getRiskInformation() == defaultProfileId
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
			analysis.setCssf(analysisForm.isCssf());
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

			if (analysisForm.getScenario() > 1 && analysisForm.getScenario() != defaultProfileId) {
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

			if (analysisForm.getParameter() > 1 && analysisForm.getParameter() != defaultProfileId) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getParameter());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getParameter());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getParameter());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.parameters", new String[] { label, company, version },
						String.format("Parameters based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			if (analysisForm.getRiskInformation() > 1 && analysisForm.getRiskInformation() != defaultProfileId) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getRiskInformation());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getRiskInformation());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getRiskInformation());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.risk_information", new String[] { label, company, version },
						String.format("Risk information based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			if (analysisForm.getScope() > 1 && analysisForm.getScope() != defaultProfileId) {
				String company = serviceAnalysis.getCustomerNameFromId(analysisForm.getScope());
				String label = serviceAnalysis.getLabelFromId(analysisForm.getScope());
				String version = serviceAnalysis.getVersionOfAnalysis(analysisForm.getScope());
				baseAnalysis += "\n" + messageSource.getMessage("label.analysis_custom.origin.scope", new String[] { label, company, version },
						String.format("Scope based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			baseAnalysis = generateStandardLog(baseAnalysis, analysisForm, defaultProfileId, analysisLocale);

			history.setComment(history.getComment() + baseAnalysis);
			List<ItemInformation> itemInformations = serviceItemInformation.getAllFromAnalysis(analysisForm.getScope());
			for (ItemInformation itemInformation : itemInformations)
				analysis.addAnItemInformation(itemInformation.duplicate());

			List<RiskInformation> riskInformations = serviceRiskInformation.getAllFromAnalysis(analysisForm.getRiskInformation());
			for (RiskInformation riskInformation : riskInformations)
				analysis.addARiskInformation(riskInformation.duplicate());

			List<Parameter> parameters = serviceParameter.getAllFromAnalysis(analysisForm.getParameter());
			Map<String, Parameter> mappingParameters = new LinkedHashMap<String, Parameter>(parameters.size());
			for (Parameter parameter : parameters) {
				Parameter parameter2 = parameter.duplicate();
				analysis.addAParameter(parameter2);
				mappingParameters.put(parameter.getKey(), parameter2);
			}

			List<Asset> assets = serviceAsset.getAllFromAnalysis(analysisForm.getAsset());
			
			Map<Integer, Asset> mappingAssets = assets.isEmpty() ? null : new LinkedHashMap<Integer, Asset>(assets.size());

			for (Asset asset : assets) {
				Asset duplication = asset.duplicate();
				analysis.addAnAsset(duplication);
				mappingAssets.put(asset.getId(), duplication);
			}

			List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(analysisForm.getScenario());
			
			Map<Integer, Scenario> mappingScenarios = scenarios.isEmpty() || assets.isEmpty() ? null : new LinkedHashMap<Integer, Scenario>(scenarios.size());
			for (Scenario scenario : scenarios) {
				Scenario duplication = scenario.duplicate();
				analysis.addAScenario(duplication);
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
						analysis.addAnAssessment(duplication);
					}
				}

				if (analysisForm.isRiskProfile()) {
					List<RiskProfile> riskProfiles = serviceRiskProfile.getAllFromAnalysis(analysisForm.getScenario());
					for (RiskProfile riskProfile : riskProfiles)
						analysis.getRiskProfiles().add(riskProfile.duplicate(mappingAssets, mappingScenarios, mappingParameters));
				}
				
				assessmentAndRiskProfileManager.UpdateRiskDendencies(analysis, mappingParameters.entrySet().stream().filter(entry -> entry.getValue() instanceof ExtendedParameter)
						.collect(Collectors.toMap(entry -> ((ExtendedParameter) entry.getValue()).getAcronym(), entry -> (ExtendedParameter) entry.getValue())));
			}

			Map<Integer, Phase> mappingPhases;
			if (analysisForm.isPhase()) {
				List<Phase> phases = servicePhase.getAllFromAnalysis(analysisForm.getStandards().get(0).getIdAnalysis());
				mappingPhases = new LinkedHashMap<Integer, Phase>(phases.size());
				for (Phase phase : phases) {
					Phase phase1 = phase.duplicate(analysis);
					analysis.addPhase(phase1);
					mappingPhases.put(phase.getNumber(), phase1);
				}
			} else {
				mappingPhases = new LinkedHashMap<Integer, Phase>(2);
				Calendar calendar = Calendar.getInstance();
				Phase phase = new Phase(Constant.PHASE_DEFAULT);
				phase.setAnalysis(analysis);
				phase.setBeginDate(new java.sql.Date(calendar.getTimeInMillis()));
				calendar.add(Calendar.YEAR, 1);
				phase.setEndDate(new java.sql.Date(calendar.getTimeInMillis()));
				mappingPhases.put(Constant.PHASE_DEFAULT, phase);
				analysis.addPhase(phase);
			}

			if (analysisForm.getStandards().size() == 1) {
				AnalysisStandardBaseInfo standardBaseInfo = analysisForm.getStandards().get(0);
				if (standardBaseInfo.getIdAnalysis() == defaultProfileId && standardBaseInfo.getIdAnalysisStandard() < 1) {
					for (AnalysisStandard analysisStandard : serviceAnalysisStandard.getAllFromAnalysis(standardBaseInfo.getIdAnalysis()))
						analysis.addAnalysisStandard(duplicator.duplicateAnalysisStandard(analysisStandard, mappingPhases, mappingParameters, mappingAssets, false));
				} else
					analysis.addAnalysisStandard(duplicator.duplicateAnalysisStandard(serviceAnalysisStandard.get(standardBaseInfo.getIdAnalysisStandard()), mappingPhases,
							mappingParameters, mappingAssets, false));
			} else {
				for (AnalysisStandardBaseInfo standardBaseInfo : analysisForm.getStandards())
					analysis.addAnalysisStandard(duplicator.duplicateAnalysisStandard(serviceAnalysisStandard.get(standardBaseInfo.getIdAnalysisStandard()), mappingPhases,
							mappingParameters, mappingAssets, false));
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