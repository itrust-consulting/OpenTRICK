package lu.itrust.business.view.controller;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.AnalysisStandard;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.History;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.Duplicator;
import lu.itrust.business.component.helper.AnalysisBaseInfo;
import lu.itrust.business.component.helper.CustomAnalysisForm;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.exception.TrickException;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAnalysisStandard;
import lu.itrust.business.service.ServiceAssessment;
import lu.itrust.business.service.ServiceAsset;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceItemInformation;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServicePhase;
import lu.itrust.business.service.ServiceRiskInformation;
import lu.itrust.business.service.ServiceScenario;
import lu.itrust.business.service.ServiceStandard;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserAnalysisRight;
import lu.itrust.business.validator.CustomAnalysisValidator;

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
	private ServicePhase servicePhase;
	
	@Autowired
	private ServiceStandard serviceStandard;
	
	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;
	
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

		return "analysis/forms/buildAnalysis";

	}

	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody Object buildCustomSave(@ModelAttribute CustomAnalysisForm customAnalysisForm, Principal principal, Locale locale) throws Exception {
		try {
			if (!serviceDataValidation.isRegistred(CustomAnalysisForm.class))
				serviceDataValidation.register(new CustomAnalysisValidator());
			Map<String, String> errors = serviceDataValidation.validate(customAnalysisForm);
			for (String error : errors.keySet())
				errors.put(error, serviceDataValidation.ParseError(errors.get(error), messageSource, locale));
			int defaultProfileId = customAnalysisForm.getProfile() < 1 ? serviceAnalysis.getDefaultProfileId() : customAnalysisForm.getProfile();

			customAnalysisForm.setDefaultProfile(defaultProfileId);

			if (customAnalysisForm.getAsset() > 0 && !serviceUserAnalysisRight.isUserAuthorized(customAnalysisForm.getAsset(), principal.getName(), AnalysisRight.READ))
				errors.put("asset", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (customAnalysisForm.getScenario() > 0 && !serviceUserAnalysisRight.isUserAuthorized(customAnalysisForm.getScenario(), principal.getName(), AnalysisRight.READ))
				errors.put("scenario", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (customAnalysisForm.getStandard() > 0 && !serviceUserAnalysisRight.isUserAuthorized(customAnalysisForm.getStandard(), principal.getName(), AnalysisRight.READ))
				errors.put("standard", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (customAnalysisForm.isAssessment() && (customAnalysisForm.getScenario() < 1 || customAnalysisForm.getScenario() != customAnalysisForm.getAsset()))
				errors.put("assessment", messageSource.getMessage("error.analysis_custom.assessment.invalid", null, "Risk estimation cannot be selected", locale));

			if (customAnalysisForm.getScope() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null, "No default profile, please select a profile", locale));
				errors.put("scope", messageSource.getMessage("error.analysis_custom.scope.empty", null, "No default profile, scope cannot be empty", locale));
			} else if (!(customAnalysisForm.getScope() == defaultProfileId || serviceUserAnalysisRight.isUserAuthorized(customAnalysisForm.getScope(), principal.getName(), AnalysisRight.READ)))
				errors.put("scope", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (customAnalysisForm.getParameter() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null, "No default profile, please select a profile", locale));
				errors.put("parameter", messageSource.getMessage("error.analysis_custom.parameter.empty", null, "No default profile, parameter cannot be empty", locale));
			} else if (!(customAnalysisForm.getParameter() == defaultProfileId || serviceUserAnalysisRight.isUserAuthorized(customAnalysisForm.getParameter(), principal.getName(), AnalysisRight.READ)))
				errors.put("parameter", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			if (customAnalysisForm.getRiskInformation() < 1) {
				if (!errors.containsKey("profile"))
					errors.put("profile", messageSource.getMessage("error.analysis_custom.no_default_profile", null, "No default profile, please select a profile", locale));
				errors.put("riskInformation", messageSource.getMessage("error.analysis_custom.risk_information.empty", null, "No default profile, risk information cannot be empty", locale));
			} else if (!(customAnalysisForm.getRiskInformation() == defaultProfileId || serviceUserAnalysisRight.isUserAuthorized(customAnalysisForm.getRiskInformation(), principal.getName(),
					AnalysisRight.READ)))
				errors.put("riskInformation", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			Customer customer = null;
			Language language = null;

			if (customAnalysisForm.getLanguage() > 0) {
				language = serviceLanguage.get(customAnalysisForm.getLanguage());
				if (language == null)
					errors.put("language", messageSource.getMessage("error.language.not_found", null, "Language cannot be found", locale));
			}

			if (customAnalysisForm.getCustomer() > 0) {
				customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customAnalysisForm.getCustomer());
				if (customer == null)
					errors.put("customer", messageSource.getMessage("error.customer.not_found", null, "Customer cannot be found", locale));
				else if (!customer.isCanBeUsed())
					errors.put("customer", messageSource.getMessage("error.customer.invalid", null, "Customer cannot be used", locale));
			}

			if (!errors.isEmpty())
				return errors;

			History history = customAnalysisForm.generateHistory();
			String identifier = language.getAlpha3() + "_" + new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(history.getDate());
			Analysis analysis = new Analysis();
			analysis.setIdentifier(identifier);
			analysis.setCustomer(customer);
			analysis.setLanguage(language);
			analysis.addAHistory(history);
			analysis.setData(true);
			analysis.setLabel(history.getComment());
			analysis.setCreationDate((Timestamp) history.getDate());
			analysis.setVersion(customAnalysisForm.getVersion());
			analysis.setUncertainty(customAnalysisForm.isUncertainty());
			analysis.setCssf(customAnalysisForm.isCssf());
			analysis.setOwner(serviceUser.get(principal.getName()));
			analysis.addUserRight(analysis.getOwner(), AnalysisRight.ALL);
			String baseAnalysis = "";

			Locale analysisLocale = language.getAlpha3().equalsIgnoreCase("fra") ? Locale.FRANCE : Locale.ENGLISH;

			if (customAnalysisForm.getAsset() > 0) {
				String company = serviceAnalysis.getCustomerNameFromId(customAnalysisForm.getAsset());
				String label = serviceAnalysis.getLabelFromId(customAnalysisForm.getAsset());
				String version = serviceAnalysis.getVersionOfAnalysis(customAnalysisForm.getAsset());
				baseAnalysis +=
					"\n"
						+ messageSource.getMessage("label.analysis_custom.origin.assets", new String[] { label, company, version }, String.format("Assets based on: %s, customer: %s, version: %s",
								label, company, version), analysisLocale);
			}

			if (customAnalysisForm.getScenario() > 1 && customAnalysisForm.getScenario() != defaultProfileId) {
				String company = serviceAnalysis.getCustomerNameFromId(customAnalysisForm.getScenario());
				String label = serviceAnalysis.getLabelFromId(customAnalysisForm.getScenario());
				String version = serviceAnalysis.getVersionOfAnalysis(customAnalysisForm.getScenario());
				baseAnalysis +=
					"\n"
						+ messageSource.getMessage("label.analysis_custom.origin.scenarios", new String[] { label, company, version }, String.format(
								"Scenarios based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
				if (customAnalysisForm.isAssessment())
					baseAnalysis +=
						"\n"
							+ messageSource.getMessage("label.analysis_custom.origin.estimation", new String[] { label, company, version }, String.format(
									"Risk estimation based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			if (customAnalysisForm.getParameter() > 1 && customAnalysisForm.getParameter() != defaultProfileId) {
				String company = serviceAnalysis.getCustomerNameFromId(customAnalysisForm.getParameter());
				String label = serviceAnalysis.getLabelFromId(customAnalysisForm.getParameter());
				String version = serviceAnalysis.getVersionOfAnalysis(customAnalysisForm.getParameter());
				baseAnalysis +=
					"\n"
						+ messageSource.getMessage("label.analysis_custom.origin.parameters", new String[] { label, company, version }, String.format(
								"Parameters based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			if (customAnalysisForm.getRiskInformation() > 1 && customAnalysisForm.getRiskInformation() != defaultProfileId) {
				String company = serviceAnalysis.getCustomerNameFromId(customAnalysisForm.getRiskInformation());
				String label = serviceAnalysis.getLabelFromId(customAnalysisForm.getRiskInformation());
				String version = serviceAnalysis.getVersionOfAnalysis(customAnalysisForm.getRiskInformation());
				baseAnalysis +=
					"\n"
						+ messageSource.getMessage("label.analysis_custom.origin.risk_information", new String[] { label, company, version }, String.format(
								"Risk information based on: %s, customer: %s, version: %s", label, company, version), analysisLocale);
			}

			if (customAnalysisForm.getScope() > 1 && customAnalysisForm.getScope() != defaultProfileId) {
				String company = serviceAnalysis.getCustomerNameFromId(customAnalysisForm.getScope());
				String label = serviceAnalysis.getLabelFromId(customAnalysisForm.getScope());
				String version = serviceAnalysis.getVersionOfAnalysis(customAnalysisForm.getScope());
				baseAnalysis +=
					"\n"
						+ messageSource.getMessage("label.analysis_custom.origin.scope", new String[] { label, company, version }, String.format("Scope based on: %s, customer: %s, version: %s",
								label, company, version), analysisLocale);
			}

			if (customAnalysisForm.getStandard() > 1 && customAnalysisForm.getStandard() != defaultProfileId) {
				String company = serviceAnalysis.getCustomerNameFromId(customAnalysisForm.getStandard());
				String label = serviceAnalysis.getLabelFromId(customAnalysisForm.getStandard());
				String version = serviceAnalysis.getVersionOfAnalysis(customAnalysisForm.getStandard());
				baseAnalysis +=
					"\n"
						+ messageSource.getMessage("label.analysis_custom.origin.standard", new String[] { label, company, company, version }, String.format(
								"Standard based on: %s, customer: %s, version: %s", label, company, company, version), analysisLocale);

				if (customAnalysisForm.isPhase())
					baseAnalysis +=
						"\n"
							+ messageSource.getMessage("label.analysis_custom.origin.phase", new String[] { label, company, version }, String.format("Phase based on: %s, customer: %s, version: %s",
									label, company, version), analysisLocale);
			}

			history.setComment(history.getComment() + baseAnalysis);
			List<ItemInformation> itemInformations = serviceItemInformation.getAllFromAnalysis(customAnalysisForm.getScope());
			for (ItemInformation itemInformation : itemInformations)
				analysis.addAnItemInformation(itemInformation.duplicate());

			List<RiskInformation> riskInformations = serviceRiskInformation.getAllFromAnalysis(customAnalysisForm.getRiskInformation());
			for (RiskInformation riskInformation : riskInformations)
				analysis.addARiskInformation(riskInformation.duplicate());

			List<Parameter> parameters = serviceParameter.getAllFromAnalysis(customAnalysisForm.getParameter());
			Map<String, Parameter> mappingParameters = new LinkedHashMap<String, Parameter>(parameters.size());
			for (Parameter parameter : parameters) {
				Parameter parameter2 = parameter.duplicate();
				analysis.addAParameter(parameter2);
				mappingParameters.put(String.format(Duplicator.KEY_PARAMETER_FORMAT, parameter2.getType().getLabel(), parameter2.getDescription()), parameter2);
			}

			List<Asset> assets = serviceAsset.getAllFromAnalysis(customAnalysisForm.getAsset());

			Map<Integer, Asset> mappingAssets = customAnalysisForm.isAssessment() ? new LinkedHashMap<Integer, Asset>(assets.size()) : null;

			for (Asset asset : assets) {
				Asset duplication = asset.duplicate();
				analysis.addAnAsset(duplication);
				if (mappingAssets != null)
					mappingAssets.put(asset.getId(), duplication);
			}

			List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(customAnalysisForm.getScenario());
			Map<Integer, Scenario> mappingScenarios = customAnalysisForm.isAssessment() ? new LinkedHashMap<Integer, Scenario>(scenarios.size()) : null;
			for (Scenario scenario : scenarios) {
				Scenario duplication = scenario.duplicate();
				analysis.addAScenario(duplication);
				if (mappingScenarios != null)
					mappingScenarios.put(scenario.getId(), duplication);
			}

			if (customAnalysisForm.isAssessment()) {
				List<Assessment> assessments = serviceAssessment.getAllFromAnalysis(customAnalysisForm.getScenario());
				for (Assessment assessment : assessments) {
					Assessment duplication = assessment.duplicate();
					duplication.setScenario(mappingScenarios.get(assessment.getScenario().getId()));
					duplication.setAsset(mappingAssets.get(assessment.getAsset().getId()));
					analysis.addAnAssessment(duplication);
				}
			}

			Map<Integer, Phase> mappingPhases;
			List<Phase> phases = customAnalysisForm.isPhase() ? servicePhase.getAllFromAnalysis(customAnalysisForm.getStandard()) : null;

			if (phases != null) {
				mappingPhases = new LinkedHashMap<Integer, Phase>(phases.size());
				for (Phase phase : phases) {
					Phase phase1 = phase.duplicate();
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
			
			serviceAnalysis.save(analysis);
			
			List<AnalysisStandard> analysisStandards = serviceAnalysisStandard.getAllFromAnalysis(customAnalysisForm.getStandard());
			
			for (AnalysisStandard analysisStandard : analysisStandards)
				analysis.addAnalysisStandard(duplicator.duplicateAnalysisStandard(analysisStandard, mappingPhases, mappingParameters, false));
			serviceAnalysis.saveOrUpdate(analysis);

			return JsonMessage.Success(messageSource.getMessage("success.analysis_custom.create", null, "Your analysis has been successfully created", locale));
		} catch (TrickException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
	}

	@RequestMapping(value = "/Customer/{id}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomer(@PathVariable Integer id, Principal principal) {
		return serviceAnalysis.getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(id, principal.getName());
	}

	@RequestMapping(value = "/Customer/{id}/Identifier/{identifier}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomerAndIdentifier(@PathVariable Integer id, @PathVariable String identifier, Principal principal) {
		return serviceAnalysis.getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(id, principal.getName(), identifier);
	}
}