package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.ts.constants.Constant.OPEN_MODE;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.ts.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.ts.component.ChartGenerator;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceAnalysisStandard;
import lu.itrust.business.ts.database.service.ServiceDynamicParameter;
import lu.itrust.business.ts.database.service.ServiceMeasure;
import lu.itrust.business.ts.database.service.ServiceRiskProfile;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.FieldValue;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.helper.chartJS.item.ColorBound;
import lu.itrust.business.ts.helper.chartJS.model.Chart;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.assessment.helper.ALE;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.cssf.RiskStrategy;
import lu.itrust.business.ts.model.general.OpenMode;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.DynamicParameter;
import lu.itrust.business.ts.model.rrf.RRF;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.standard.AssetStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;

/**
 * The ControllerAssessment class is a controller that handles requests related
 * to assessment and risk profiles in the analysis module.
 * It provides methods for loading asset assessments, scenario assessments,
 * managing risk profile measures, computing risk profile measures, and
 * refreshing assessments.
 * This controller is responsible for handling HTTP requests and returning the
 * appropriate response.
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Assessment")
@Controller
public class ControllerAssessment {

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private ServiceDynamicParameter serviceDynamicParameter;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceRiskProfile serviceRiskProfile;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private ServiceMeasure serviceMeasure;

	/**
	 * Loads the asset assessment for the given asset ID and scenario ID.
	 * 
	 * @param idAsset    The ID of the asset.
	 * @param idScenario The ID of the scenario.
	 * @param model      The model object for the view.
	 * @param session    The HttpSession object.
	 * @param principal  The Principal object representing the currently
	 *                   authenticated user.
	 * @param locale     The Locale object representing the user's preferred
	 *                   language.
	 * @return The name of the view to render.
	 * @throws Exception if an error occurs during the loading process.
	 */
	@RequestMapping(value = "/Asset/{idAsset}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String loadAssetAssessment(@PathVariable int idAsset,
			@RequestParam(value = "idScenario", defaultValue = "0") int idScenario, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Asset asset = analysis.findAsset(idAsset);
		loadAssessmentData(model, locale, analysis);
		if (idScenario < 1) {
			List<Assessment> assessments = analysis.findSelectedAssessmentByAsset(idAsset);
			if (analysis.isQuantitative()) {
				ALE ale = new ALE(asset.getName(), 0);
				ALE aleo = new ALE(asset.getName(), 0);
				ALE alep = new ALE(asset.getName(), 0);
				model.addAttribute("ale", ale);
				model.addAttribute("aleo", aleo);
				model.addAttribute("alep", alep);
				AssessmentAndRiskProfileManager.ComputeALE(assessments, ale, alep, aleo);
			}
			assessments.sort(assessmentScenarioComparator().reversed());
			model.addAttribute("assessments", assessments);
		} else {
			Scenario scenario = analysis.findScenario(idScenario);
			if (scenario != null) {
				Assessment assessment = analysis.findAssessmentByAssetAndScenario(idAsset, idScenario);
				if (assessment != null && assessment.isSelected())
					loadAssessmentFormData(idScenario, idAsset, model, analysis, assessment);
				model.addAttribute("scenario", scenario);
			}
		}

		model.addAttribute("asset", asset);
		loadAnalysisSettings(model, analysis);
		model.addAttribute("isEditable", !OpenMode.isReadOnly((OpenMode) session.getAttribute(OPEN_MODE)));
		return "jsp/analyses/single/components/risk-estimation/asset/home";
	}

	/**
	 * Loads the scenario assessment and returns a String representing the view
	 * name.
	 * 
	 * @param idScenario the ID of the scenario
	 * @param idAsset    the ID of the asset (default value is 0)
	 * @param model      the model object for the view
	 * @param session    the HttpSession object
	 * @param principal  the Principal object representing the currently
	 *                   authenticated user
	 * @param locale     the Locale object representing the user's locale
	 * @return a String representing the view name
	 * @throws Exception if an error occurs during the loading of the assessment
	 */
	@RequestMapping(value = "/Scenario/{idScenario}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String loadSceanrioAssessment(@PathVariable int idScenario,
			@RequestParam(value = "idAsset", defaultValue = "0") int idAsset, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Scenario scenario = analysis.findScenario(idScenario);
		loadAssessmentData(model, locale, analysis);
		if (idAsset < 1) {
			List<Assessment> assessments = analysis.findSelectedAssessmentByScenario(idScenario);
			if (analysis.isQuantitative()) {
				ALE ale = new ALE(scenario.getName(), 0);
				ALE aleo = new ALE(scenario.getName(), 0);
				ALE alep = new ALE(scenario.getName(), 0);
				model.addAttribute("ale", ale);
				model.addAttribute("aleo", aleo);
				model.addAttribute("alep", alep);
				AssessmentAndRiskProfileManager.ComputeALE(assessments, ale, alep, aleo);
			}
			assessments.sort(assessmentAssetComparator().reversed());
			model.addAttribute("assessments", assessments);
		} else {
			Asset asset = analysis.findAsset(idAsset);
			if (asset != null) {
				Assessment assessment = analysis.findAssessmentByAssetAndScenario(idAsset, idScenario);
				if (assessment != null && assessment.isSelected())
					loadAssessmentFormData(idScenario, idAsset, model, analysis, assessment);
				model.addAttribute("asset", asset);
			}
		}
		loadAnalysisSettings(model, analysis);
		model.addAttribute("scenario", scenario);
		model.addAttribute("isEditable", !OpenMode.isReadOnly((OpenMode) session.getAttribute(OPEN_MODE)));
		return "jsp/analyses/single/components/risk-estimation/scenario/home";

	}

	/**
	 * Retrieves the risk profile measure for a given asset and scenario.
	 * 
	 * @param idAsset    the ID of the asset
	 * @param idScenario the ID of the scenario
	 * @param model      the model object for the view
	 * @param session    the HTTP session
	 * @param principal  the principal object representing the currently
	 *                   authenticated user
	 * @param locale     the locale for the request
	 * @return the view name for the risk profile measure form
	 */
	@RequestMapping(value = "/RiskProfile/Manage-measure", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY) and "
			+ "@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manageRiskProfileMeasure(@RequestParam(name = "idAsset") Integer idAsset,
			@RequestParam(name = "idScenario") Integer idScenario, Model model, HttpSession session,
			Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		RiskProfile riskProfile = serviceRiskProfile.getByAssetAndScanrio(idAsset, idScenario);
		if (riskProfile == null)
			return null;
		final List<DynamicParameter> dynamicParameters = serviceDynamicParameter.findByAnalysisId(idAnalysis);
		model.addAttribute("riskProfile", riskProfile);
		model.addAttribute("valueFactory", new ValueFactory(dynamicParameters));
		model.addAttribute("standards", serviceAnalysisStandard.findStandardByAnalysisIdAndTypeIn(idAnalysis,
				NormalStandard.class, AssetStandard.class));
		return "jsp/analyses/single/components/risk-estimation/form/measure";
	}

	/**
	 * Computes the risk profile measure.
	 *
	 * @param model     the model object
	 * @param session   the HttpSession object
	 * @param principal the Principal object
	 * @param locale    the Locale object
	 * @return an Object representing the computed risk profile measure
	 */
	@PostMapping(value = "/RiskProfile/Compute-measure", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object computeRiskProfileMeasure(Model model, HttpSession session, Principal principal,
			Locale locale) {

		try {

			final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			final Analysis analysis = serviceAnalysis.get(idAnalysis);

			final IParameter maxRRFParameter = analysis.findSimpleParameter(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME,
					Constant.PARAMETER_MAX_RRF);

			final double rrfThreshold = analysis.findParameter(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME,
					Constant.ILR_RRF_THRESHOLD, 5d) * 0.01;

			final int mandatoryPhase = (int) analysis.findParameter(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME,
					Constant.MANDATORY_PHASE, 1d);

			final Map<String, RiskProfile> riskProfiles = analysis.getRiskProfiles().stream()
					.filter(RiskProfile::isSelected)
					.collect(Collectors.toMap(RiskProfile::getKey, Function.identity()));

			final List<Assessment> assessments = analysis.getAssessments().stream()
					.filter(Assessment::isSelected).collect(Collectors.toList());

			final ValueFactory factory = new ValueFactory(analysis.getParameters());

			analysis.getRiskProfiles().forEach(e -> e.getMeasures().clear());

			analysis.getAnalysisStandards().values().stream()
					.filter(e -> !e.getStandard().is(Constant.STANDARD_MATURITY))
					.flatMap(e -> e.getMeasures().stream())
					.filter(e -> !e.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
							&& e.getMeasureDescription().isComputable()
							&& e.getImplementationRateValue(factory) < 100
							&& e.getPhase().getNumber() <= mandatoryPhase)
					.filter(AbstractNormalMeasure.class::isInstance).forEach(e -> assessments.stream().forEach(ass -> {
						final double rrf = RRF.calculateRRF(ass, maxRRFParameter, e);
						if (rrf >= rrfThreshold) {
							final RiskProfile riskProfile = riskProfiles
									.get(RiskProfile.key(ass.getAsset(), ass.getScenario()));

							if (!(riskProfile == null || RiskStrategy.ACCEPT.equals(riskProfile.getRiskStrategy()))) {
								riskProfile.getMeasures().add(e);
							}

						}
					})

					);

			serviceAnalysis.saveOrUpdate(analysis);
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
		return JsonMessage.Success(messageSource.getMessage("success.compute.risk_profile_measure", null,
				"Risk Estimation measures had been computed successfully", locale));

	}

	/**
	 * Refreshes the assessment and returns a success or error message as a JSON
	 * string.
	 *
	 * @param session   the HttpSession object
	 * @param locale    the Locale object
	 * @param principal the Principal object
	 * @return a JSON string containing a success or error message
	 * @throws Exception if an error occurs during the refresh process
	 */
	@RequestMapping(value = "/Refresh", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String refreshAssessment(HttpSession session, Locale locale, Principal principal)
			throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis is not null
			if (integer == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale)
						+ "\" }");

			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);

			// check if analysis object is not null
			if (analysis == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale)
						+ "\" }");
			// update assessments of analysis
			assessmentAndRiskProfileManager.WipeAssessment(analysis);
			assessmentAndRiskProfileManager.updateAssessment(analysis, null);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.refresh", null,
					"Assessments were successfully refreshed", locale) + "\"}");
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return new String(
					"{\"error\":\"" + messageSource.getMessage("error.internal.message.assessment.generation", null,
							"An error occurred during the generation", locale) + "\"}");
		}
	}

	/**
	 * Represents a chart object used for visualizing data.
	 */
	@RequestMapping(value = "/Chart/Risk-evolution-heat-map", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Chart riskEvolutionHeatMapChart(HttpSession session, Principal principal, Locale locale) {
		return chartGenerator.generateRiskEvolutionHeatMap((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS),
				locale);
	}

	/**
	 * Represents a chart object.
	 */
	@RequestMapping(value = "/Chart/Risk-heat-map", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Chart riskHeatMapChart(HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return chartGenerator.generateRiskHeatMap(idAnalysis, locale);
	}

	/**
	 * Saves the risk profile measure.
	 *
	 * @param measureIds the list of measure IDs
	 * @param idAsset    the asset ID
	 * @param idScenario the scenario ID
	 * @param session    the HttpSession object
	 * @param principal  the Principal object
	 * @param locale     the Locale object
	 * @return the success message if the risk profile is saved successfully,
	 *         otherwise an error message
	 */
	@RequestMapping(value = "/RiskProfile/Update/Measure", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY) and "
			+ "@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String saveRiskProfileMeasure(@RequestBody List<Integer> measureIds,
			@RequestParam(name = "idAsset") Integer idAsset,
			@RequestParam(name = "idScenario") Integer idScenario, HttpSession session, Principal principal,
			Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		RiskProfile riskProfile = serviceRiskProfile.getByAssetAndScanrio(idAsset, idScenario);
		if (riskProfile == null)
			return JsonMessage.Error(messageSource.getMessage("error.risk_profile.not_found", null,
					"Risk profile cannot be found", locale));
		Map<Integer, Measure> measures = serviceMeasure.getByIdAnalysisAndIds(idAnalysis, measureIds).stream()
				.collect(Collectors.toMap(Measure::getId, Function.identity()));
		riskProfile.getMeasures().removeIf(measure -> !measures.containsKey(measure.getId()));
		riskProfile.getMeasures().forEach(measure -> measures.remove(measure.getId()));
		riskProfile.getMeasures().addAll(measures.values());
		serviceRiskProfile.saveOrUpdate(riskProfile);
		return JsonMessage.Success(messageSource.getMessage("success.save.risk_profile", null,
				"Risk profile has been successfully save", locale));
	}

	/**
	 * Updates the Asset Loss Expectancy (ALE) for the analysis and returns a
	 * success message.
	 *
	 * @param session   the HttpSession object
	 * @param locale    the Locale object
	 * @param principal the Principal object
	 * @return a JSON string representing the success message
	 * @throws Exception if an error occurs during the update process
	 */
	@RequestMapping(value = "/Update/ALE", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateAle(HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// check if analysis is not null
			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);
			// update assessments of analysis
			AssessmentAndRiskProfileManager.UpdateAssetALE(analysis, null);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.ale.update", null,
					"Assessments ale were successfully updated", locale) + "\"}");
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return new String(
					"{\"error\":\"" + messageSource.getMessage("error.internal.assessment.ale.update", null,
							"Assessment ale update failed: an error occurred", locale) + "\"}");
		}
	}

	/**
	 * Updates the assessment of an analysis and returns a success or error message.
	 *
	 * @param session   the HttpSession object
	 * @param locale    the Locale object
	 * @param principal the Principal object
	 * @return a JSON string containing a success or error message
	 * @throws Exception if an error occurs during the update process
	 */
	@RequestMapping(value = "/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateAssessment(HttpSession session, Locale locale, Principal principal)
			throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);
			// update assessments of analysis
			assessmentAndRiskProfileManager.updateAndSave(analysis, null);
			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.update", null,
					"Assessments were successfully updated", locale) + "\"}");
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return new String(
					"{\"error\":\"" + messageSource.getMessage("error.internal.message.assessment.generation", null,
							"An error occurred during the generation", locale) + "\"}");
		}
	}

	/**
	 * Returns a comparator for sorting assessments based on asset properties.
	 * The assessments are sorted in ascending order of ALE (Annual Loss
	 * Expectancy).
	 * If two assessments have the same ALE, they are further sorted based on the
	 * value of the asset.
	 * If the assets have the same value, they are further sorted based on the name
	 * of the asset type.
	 * If the asset types have the same name, they are further sorted based on the
	 * name of the asset.
	 *
	 * @return a comparator for sorting assessments based on asset properties
	 */
	private Comparator<? super Assessment> assessmentAssetComparator() {
		return (a1, a2) -> {
			int compare = Double.compare(a1.getALE(), a2.getALE());
			if (compare == 0) {
				compare = Double.compare(a1.getAsset().getValue(), a2.getAsset().getValue());
				if (compare == 0) {
					compare = a1.getAsset().getAssetType().getName().compareTo(a2.getAsset().getAssetType().getName());
					if (compare == 0)
						compare = a1.getAsset().getName().compareTo(a2.getAsset().getName());
				}
			}
			return compare;
		};
	}

	/**
	 * Returns a comparator for sorting assessments based on their ALE (Annual Loss
	 * Expectancy) values,
	 * scenario type names, and scenario names.
	 *
	 * @return a comparator for sorting assessments
	 */
	private Comparator<? super Assessment> assessmentScenarioComparator() {
		return (a1, a2) -> {
			int compare = Double.compare(a1.getALE(), a2.getALE());
			if (compare == 0) {
				compare = a1.getScenario().getType().getName().compareTo(a2.getScenario().getType().getName());
				if (compare == 0)
					compare = a1.getScenario().getName().compareTo(a2.getScenario().getName());
			}
			return compare;
		};
	}

	/**
	 * Loads the analysis settings into the model.
	 * 
	 * @param model    the model to which the settings will be added
	 * @param analysis the analysis object containing the settings
	 */
	private void loadAnalysisSettings(Model model, Analysis analysis) {
		final AnalysisSetting rawSetting = AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN;
		final AnalysisSetting hiddenCommentSetting = AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT;
		model.addAttribute("showHiddenComment", analysis.findSetting(hiddenCommentSetting));
		model.addAttribute("showRawColumn", analysis.findSetting(rawSetting));
		model.addAttribute("showDynamicAnalysis", analysis.findSetting(AnalysisSetting.ALLOW_DYNAMIC_ANALYSIS));
	}

	/**
	 * Loads the assessment data into the model.
	 *
	 * @param model    the model to which the assessment data will be added
	 * @param locale   the locale used for language-specific data
	 * @param analysis the analysis object containing the assessment data
	 */
	private void loadAssessmentData(Model model, Locale locale, Analysis analysis) {
		model.addAttribute("valueFactory", new ValueFactory(analysis.getParameters()));
		model.addAttribute("impactTypes", analysis.findImpacts());
		model.addAttribute("type", analysis.getType());
		model.addAttribute("language", locale.getISO3Country());
		model.addAttribute("show_uncertainty", analysis.isUncertainty());
		model.addAttribute("langue", locale.getLanguage().toUpperCase());
	}

	/**
	 * Loads the assessment form data for a given scenario and asset.
	 * 
	 * @param idScenario the ID of the scenario
	 * @param idAsset    the ID of the asset
	 * @param model      the model object to add attributes to
	 * @param analysis   the analysis object containing likelihood and dynamic
	 *                   parameters
	 * @param assessment the assessment object to be loaded
	 */
	private void loadAssessmentFormData(int idScenario, int idAsset, Model model, Analysis analysis,
			Assessment assessment) {
		ValueFactory factory = (ValueFactory) model.asMap().get("valueFactory");
		model.addAttribute("impacts", factory.getImpacts());
		model.addAttribute("assessment", assessment);
		model.addAttribute("probabilities", analysis.getLikelihoodParameters());
		model.addAttribute("dynamics", analysis.getDynamicParameters());

		if (analysis.isQualitative()) {

			final boolean isILR = Analysis.findSetting(AnalysisSetting.ALLOW_ILR_ANALYSIS,
			analysis.getSettings().get(AnalysisSetting.ALLOW_ILR_ANALYSIS.name()));

			model.addAttribute("isILR", isILR);

		

			RiskProfile riskProfile = analysis.findRiskProfileByAssetAndScenario(idAsset, idScenario);
			model.addAttribute("strategies", RiskStrategy.values());
			model.addAttribute("riskProfile", riskProfile);
			List<ColorBound> colorBounds = ChartGenerator.GenerateColorBounds(analysis.getRiskAcceptanceParameters());

			Integer netImportance = factory.findImportance(assessment);
			model.addAttribute("computedNetImportance", colorBounds.stream().filter(v -> v.isAccepted(netImportance))
					.map(v -> new FieldValue("importance", netImportance, v.getLabel(), null, v.getColor())).findAny()
					.orElse(new FieldValue("importance", netImportance)));

			Integer expImportance = riskProfile.getComputedExpImportance();

			model.addAttribute("computedExpImportance", colorBounds.stream().filter(v -> v.isAccepted(expImportance))
					.map(v -> new FieldValue("importance", expImportance, v.getLabel(), null, v.getColor())).findAny()
					.orElse(new FieldValue("importance", expImportance)));

			if (analysis.findSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN).equals(true)) {
				Integer rawImportance = riskProfile.getComputedRawImportance();
				model.addAttribute("computedRawImportance",
						colorBounds.stream().filter(v -> v.isAccepted(rawImportance))
								.map(v -> new FieldValue("importance", rawImportance, v.getLabel(), null, v.getColor()))
								.findAny().orElse(new FieldValue("importance", rawImportance)));
			}

			if (analysis.isQuantitative())
				model.addAttribute("riskRegister", analysis.findRiskRegisterByAssetAndScenario(idAsset, idScenario));

			if(isILR){
				//final int [] ilrImportance = {-1,-1};

				//final int nextIlrImportance = riskProfile.getRawProbaImpact() == null || riskProfile.getRawProbaImpact().getProbability() == null ? -1 : riskProfile.getRawProbaImpact().getProbability().getIlrLevel() * assessment.getVulnerability() * 
			}
		}
	}

}