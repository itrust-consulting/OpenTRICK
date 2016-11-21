package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceDynamicParameter;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.parameter.ILevelParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * @author eom
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Assessment")
@Controller
public class ControllerAssessment {

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceDynamicParameter serviceDynamicParameter;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceScenario serviceScenario;

	/**
	 * loadAssessmentsOfAsset: <br>
	 * Description
	 * 
	 * @param assetId
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Asset/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadAssessmentsOfAsset(@PathVariable Integer elementID, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		// get analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		loadAssessmentData(model, locale, idAnalysis);
		// retrieve asset
		Asset asset = serviceAsset.get(elementID);
		// load assessments by asset into model
		return assessmentByAsset(model, asset, serviceAssessment.getAllSelectedFromAsset(asset), idAnalysis, true);
	}

	@RequestMapping(value = "/Asset/{idAsset}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadAssetAssessment(@PathVariable int idAsset, @RequestParam(value = "idScenario", defaultValue = "-1") int idScenario, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Asset asset = analysis.findAsset(idAsset);
		loadAssessmentData(model, locale, analysis);
		if (idScenario < 1) {
			List<Assessment> assessments = analysis.findSelectedAssessmentByAsset(idAsset);
			if (analysis.getType() == AnalysisType.QUANTITATIVE) {
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
			if (scenario == null)
				throw new AccessDeniedException(messageSource.getMessage("error.not_authorized", null, "Insufficient permissions!", locale));
			Assessment assessment = analysis.findAssessmentByAssetAndScenario(idAsset, idScenario);
			if (!assessment.isSelected())
				throw new ResourceNotFoundException(messageSource.getMessage("error.assessment.not_found", null, "Estimation cannot be found!", locale));
			model.addAttribute("scenario", scenario);
			loadAssessmentFormData(idScenario, idAsset, model, analysis, assessment);
		}
		model.addAttribute("asset", asset);
		return "analyses/single/components/estimation/asset/home";
	}

	/**
	 * loadByScenario: <br>
	 * Description
	 * 
	 * @param scenarioId
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Scenario/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadByScenario(@PathVariable Integer elementID, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		loadAssessmentData(model, locale, idAnalysis);
		Scenario scenario = serviceScenario.get(elementID);
		// load all assessments by scenario to model
		return assessmentByScenario(model, scenario, serviceAssessment.getAllFromScenario(scenario), idAnalysis, true);
	}

	@RequestMapping(value = "/Scenario/{idScenario}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadSceanrioAssessment(@PathVariable int idScenario, @RequestParam(value = "idAsset", defaultValue = "-1") int idAsset, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Scenario scenario = analysis.findScenario(idScenario);
		loadAssessmentData(model, locale, analysis);
		if (idAsset < 1) {
			List<Assessment> assessments = analysis.findSelectedAssessmentByScenario(idScenario);
			if (analysis.getType() == AnalysisType.QUANTITATIVE) {
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
			if (scenario == null)
				throw new AccessDeniedException(messageSource.getMessage("error.not_authorized", null, "Insufficient permissions!", locale));
			Assessment assessment = analysis.findAssessmentByAssetAndScenario(idAsset, idScenario);
			if (!assessment.isSelected())
				throw new ResourceNotFoundException(messageSource.getMessage("error.assessment.not_found", null, "Estimation cannot be found!", locale));
			model.addAttribute("asset", asset);
			loadAssessmentFormData(idScenario, idAsset, model, analysis, assessment);

		}
		model.addAttribute("scenario", scenario);
		return "analyses/single/components/estimation/scenario/home";

	}

	/**
	 * updateAssessment: <br>
	 * Description
	 * 
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Refresh", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String refreshAssessment(HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis is not null
			if (integer == null)
				return new String("{\"error\":\"" + messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale) + "\" }");

			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);

			// check if analysis object is not null
			if (analysis == null)
				return new String("{\"error\":\"" + messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale) + "\" }");
			// update assessments of analysis
			assessmentAndRiskProfileManager.WipeAssessment(analysis);
			assessmentAndRiskProfileManager.updateAssessment(analysis, null);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.refresh", null, "Assessments were successfully refreshed", locale) + "\"}");
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return new String("{\"error\":\"" + messageSource.getMessage("error.internal.assessment.generation", null, "An error occurred during the generation", locale) + "\"}");
		}
	}

	@RequestMapping(value = "/Update/ALE", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateAle(HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// check if analysis is not null
			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);
			// update assessments of analysis
			assessmentAndRiskProfileManager.updateAssetALE(analysis, null);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.ale.update", null, "Assessments ale were successfully updated", locale) + "\"}");
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return new String(
					"{\"error\":\"" + messageSource.getMessage("error.internal.assessment.ale.update", null, "Assessment ale update failed: an error occurred", locale) + "\"}");
		}
	}

	/**
	 * updateAssessment: <br>
	 * Description
	 * 
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateAssessment(HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);
			// update assessments of analysis
			assessmentAndRiskProfileManager.updateAssessment(analysis, null);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.update", null, "Assessments were successfully updated", locale) + "\"}");
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return new String("{\"error\":\"" + messageSource.getMessage("error.internal.assessment.generation", null, "An error occurred during the generation", locale) + "\"}");
		}
	}

	/**
	 * updateAsset: <br>
	 * Description
	 * 
	 * @param assetId
	 * @param model
	 * @param session
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Asset/{elementID}/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String updateAsset(@PathVariable int elementID, Model model, RedirectAttributes attributes, HttpSession session, Locale locale, Principal principal) {
		try {
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retrieve asset by id
			Asset asset = serviceAsset.get(elementID);
			// retrieve assessments of analysis
			List<Assessment> assessments = serviceAssessment.getAllSelectedFromAsset(asset);
			updateAssessments(idAnalysis, model, locale, assessments);
			return assessmentByAsset(model, asset, assessments, idAnalysis, false);
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return null
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error",
					messageSource.getMessage("error.internal.assessment.ale.update", null, "Assessment ale update failed: an error occurred", locale));
		}
		return "redirect:/Error";
	}

	/**
	 * updateByScenario: <br>
	 * Description
	 * 
	 * @param scenarioId
	 * @param model
	 * @param session
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Scenario/{elementID}/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String updateByScenario(@PathVariable int elementID, Model model, RedirectAttributes attributes, HttpSession session, Locale locale, Principal principal) {
		try {
			// load analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// load scenario
			Scenario scenario = serviceScenario.get(elementID);
			// retrieve parameters which are considered in the expression
			List<Assessment> assessments = serviceAssessment.getAllSelectedFromScenario(scenario);
			updateAssessments(idAnalysis, model, locale, assessments);
			return assessmentByScenario(model, scenario, assessments, idAnalysis, false);
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return null
			TrickLogManager.Persist(e);
			attributes.addFlashAttribute("error",
					messageSource.getMessage("error.internal.assessment.ale.update", null, "Assessment ale update failed: an error occurred", locale));
		}
		return "redirect:/Error";

	}

	private Comparator<? super Assessment> assessmentAssetComparator() {
		return (a1, a2) -> {
			int compare = Double.compare(a1.getALE(), a2.getALE());
			if (compare == 0) {
				compare = Double.compare(a1.getAsset().getValue(), a2.getAsset().getValue());
				if (compare == 0) {
					compare = a1.getAsset().getAssetType().getType().compareTo(a2.getAsset().getAssetType().getType());
					if (compare == 0)
						compare = a1.getAsset().getName().compareTo(a2.getAsset().getName());
				}
			}
			return compare;
		};
	}

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
	 * assessmentByAsset: <br>
	 * Description
	 * 
	 * @param model
	 * @param asset
	 * @param assessments
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	private String assessmentByAsset(Model model, Asset asset, List<Assessment> assessments, int idAnalysis, boolean sort) throws Exception {
		AnalysisType type = (AnalysisType) model.asMap().get("type");
		if (type == AnalysisType.QUANTITATIVE) {
			ALE ale = new ALE(asset.getName(), 0);
			ALE aleo = new ALE(asset.getName(), 0);
			ALE alep = new ALE(asset.getName(), 0);
			model.addAttribute("ale", ale);
			model.addAttribute("aleo", aleo);
			model.addAttribute("alep", alep);
			AssessmentAndRiskProfileManager.ComputeALE(assessments, ale, alep, aleo);
			asset.setALE(ale.getValue());
			asset.setALEO(aleo.getValue());
			asset.setALEP(alep.getValue());
		}
		if (sort)
			Collections.sort(assessments, assessmentScenarioComparator().reversed());
		model.addAttribute("asset", asset);
		model.addAttribute("assessments", assessments);
		serviceAsset.saveOrUpdate(asset);
		return "analyses/single/components/assessment/assets";
	}

	/**
	 * assessmentByScenario: <br>
	 * Description
	 * 
	 * @param model
	 * @param scenario
	 * @param assessments
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	private String assessmentByScenario(Model model, Scenario scenario, List<Assessment> assessments, int idAnalysis, boolean sort) throws Exception {
		AnalysisType type = (AnalysisType) model.asMap().get("type");
		if (type == AnalysisType.QUANTITATIVE) {
			ALE ale = new ALE(scenario.getName(), 0);
			ALE aleo = new ALE(scenario.getName(), 0);
			ALE alep = new ALE(scenario.getName(), 0);
			model.addAttribute("ale", ale);
			model.addAttribute("aleo", aleo);
			model.addAttribute("alep", alep);
			
			AssessmentAndRiskProfileManager.ComputeALE(assessments, ale, alep, aleo);
		}
		if (sort)
			Collections.sort(assessments, assessmentAssetComparator().reversed());
		model.addAttribute("scenario", scenario);
		model.addAttribute("assessments", assessments);
		return "analyses/single/components/assessment/scenarios";
	}

	private void loadAssessmentData(Model model, Locale locale, Analysis analysis) {
		model.addAttribute("valueFactory", new ValueFactory(analysis.getParameters()));
		model.addAttribute("impactTypes", analysis.getImpacts());
		model.addAttribute("type", analysis.getType());
		model.addAttribute("language", locale.getISO3Country());
		model.addAttribute("show_uncertainty", analysis.isUncertainty());
		model.addAttribute("langue", locale.getLanguage().toUpperCase());
	}

	private void loadAssessmentData(Model model, Locale locale, Integer idAnalysis) {
		AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);
		List<ILevelParameter> parameters = loadParameters(idAnalysis);
		if (type != AnalysisType.QUALITATIVE)
			model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));
		model.addAttribute("type", type);
		model.addAttribute("valueFactory", new ValueFactory(parameters));
		model.addAttribute("impactTypes", parameters.stream().filter(parameter -> parameter instanceof ImpactParameter).map(parameter -> ((ImpactParameter) parameter).getType())
				.distinct().collect(Collectors.toList()));
		model.addAttribute("langue", locale.getLanguage().toUpperCase());
	}

	private void loadAssessmentFormData(int idScenario, int idAsset, Model model, Analysis analysis, Assessment assessment) {
		ValueFactory factory = (ValueFactory) model.asMap().get("valueFactory");
		model.addAttribute("impacts", factory.getImpacts());
		model.addAttribute("assessment", assessment);
		model.addAttribute("probabilities", analysis.getLikelihoodParameters());
		model.addAttribute("dynamics", analysis.getLikelihoodParameters());
		if (analysis.getType() == AnalysisType.QUALITATIVE) {
			model.addAttribute("strategies", RiskStrategy.values());
			model.addAttribute("riskProfile", analysis.findRiskProfileByAssetAndScenario(idAsset, idScenario));
			model.addAttribute("computeNextImportance", factory.findImportance(assessment));
		}
	}

	private List<ILevelParameter> loadParameters(Integer idAnalysis) {
		List<ILevelParameter> parameters = new LinkedList<>(serviceImpactParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceLikelihoodParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceDynamicParameter.findByAnalysisId(idAnalysis));
		return parameters;
	}

	private void updateAssessments(Integer idAnalysis, Model model, Locale locale, List<Assessment> assessments) {
		List<ILevelParameter> parameters = loadParameters(idAnalysis);
		AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);
		ValueFactory factory = new ValueFactory(parameters);
		if (type != AnalysisType.QUALITATIVE)
			model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));
		model.addAttribute("type", type);
		model.addAttribute("langue", locale.getLanguage().toUpperCase());
		model.addAttribute("impactTypes", parameters.stream().filter(parameter -> parameter instanceof ImpactParameter).map(parameter -> ((ImpactParameter) parameter).getType())
				.distinct().collect(Collectors.toList()));
		model.addAttribute("valueFactory", factory);
		// parse assessments and initialise impact values to 0 if empty
		for (Assessment assessment : assessments) {
			if (assessment.getLikelihood() == null || assessment.getLikelihood().trim().isEmpty())
				assessment.setLikelihood("0");
			if (type == AnalysisType.QUALITATIVE)
				factory.getImpactNames().stream().filter(name -> !assessment.hasImpact(name)).forEach(name -> assessment.setImpact(factory.findValue(0, name)));
			else {
				factory.getImpactNames().stream().filter(name -> !assessment.hasImpact(name)).forEach(name -> assessment.setImpact(factory.findValue(0D, name)));
				// compute ALE
				AssessmentAndRiskProfileManager.ComputeAlE(assessment, factory, type);
			}
			// update assessments
			serviceAssessment.saveOrUpdate(assessment);
			// add assessments of asset to model
		}
	}
}