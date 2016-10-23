package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.assessment.helper.AssessmentAssetComparator;
import lu.itrust.business.TS.model.assessment.helper.AssessmentComparator;
import lu.itrust.business.TS.model.assessment.helper.AssessmentScenarioComparator;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.EvaluationResult;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.parameter.IAcronymParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.AbstractProbability;
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
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private ServiceParameter serviceParameter;

	@Autowired
	private MessageSource messageSource;

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
			assessmentAndRiskProfileManager.UpdateAssessment(analysis, null);
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

	@RequestMapping(value = "/Asset/{idAsset}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadAssetAssessment(@PathVariable int idAsset, @RequestParam(value = "idScenario", defaultValue = "-1") int idScenario, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Asset asset = analysis.findAsset(idAsset);
		model.addAttribute("valueFactory", new ValueFactory(analysis.getParameters()));
		if (idScenario < 1) {
			ALE ale = new ALE(asset.getName(), 0);
			ALE aleo = new ALE(asset.getName(), 0);
			ALE alep = new ALE(asset.getName(), 0);
			List<Assessment> assessments = analysis.findSelectedAssessmentByAsset(idAsset);
			AssessmentAndRiskProfileManager.ComputeALE(assessments, ale, alep, aleo);
			assessments.sort(new AssessmentScenarioComparator());
			model.addAttribute("ale", ale);
			model.addAttribute("aleo", aleo);
			model.addAttribute("alep", alep);
			// model.addAttribute("parameters", analysis.mapAcronymToValue());
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
		model.addAttribute("type", analysis.getType());
		model.addAttribute("language", locale.getISO3Country());
		model.addAttribute("show_uncertainty", analysis.isUncertainty());
		return "analyses/single/components/estimation/asset";
	}

	@RequestMapping(value = "/Scenario/{idScenario}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadSceanrioAssessment(@PathVariable int idScenario, @RequestParam(value = "idAsset", defaultValue = "-1") int idAsset, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		Scenario scenario = analysis.findScenario(idScenario);
		model.addAttribute("valueFactory", new ValueFactory(analysis.getParameters()));
		if (idAsset < 1) {
			ALE ale = new ALE(scenario.getName(), 0);
			ALE aleo = new ALE(scenario.getName(), 0);
			ALE alep = new ALE(scenario.getName(), 0);
			model.addAttribute("ale", ale);
			model.addAttribute("aleo", aleo);
			model.addAttribute("alep", alep);
			// model.addAttribute("parameters", analysis.mapAcronymToValue());
			List<Assessment> assessments = analysis.findSelectedAssessmentByScenario(idScenario);
			AssessmentAndRiskProfileManager.ComputeALE(assessments, ale, alep, aleo);
			assessments.sort(new AssessmentAssetComparator().reversed());
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
		model.addAttribute("type", analysis.getType());
		model.addAttribute("language", locale.getISO3Country());
		model.addAttribute("show_uncertainty", analysis.isUncertainty());
		return "analyses/single/components/estimation/scenario";

	}

	private void loadAssessmentFormData(int idScenario, int idAsset, Model model, Analysis analysis, Assessment assessment) {
		List<ImpactParameter> probabilities = new ArrayList<>(11), impacts = new ArrayList<>(11);
		analysis.groupExtended(probabilities, impacts);
		model.addAttribute("impacts", impacts);
		model.addAttribute("assessment", assessment);
		model.addAttribute("probabilities", probabilities);
		if (analysis.getType() == AnalysisType.QUALITATIVE) {
			model.addAttribute("strategies", RiskStrategy.values());
			model.addAttribute("riskProfile", analysis.findRiskProfileByAssetAndScenario(idAsset, idScenario));
			ValueFactory factory = (ValueFactory) model.asMap().get("valueFactory");
			RiskRegisterItem registerItem = analysis.findRiskRegisterByAssetAndScenario(idAsset, idScenario);
			model.addAttribute("computeNextImportance", factory.findImportance(assessment));
			if (registerItem != null) {
				int rawImpact = factory.findImpactLevelByMaxLevel(registerItem.getRawEvaluation().getImpact()),
						rawProbability = factory.findExpLevel(registerItem.getRawEvaluation().getProbability()),
						netProbability = factory.findExpLevel(registerItem.getNetEvaluation().getProbability()),
						expImpact = factory.findExpLevel(registerItem.getExpectedEvaluation().getImpact()),
						expProbability = factory.findExpLevel(registerItem.getExpectedEvaluation().getProbability()),
						netImpact = factory.findImpactLevelByMaxLevel(registerItem.getNetEvaluation().getImpact());
				model.addAttribute("rawModelling", new EvaluationResult(rawProbability, rawImpact));
				model.addAttribute("netModelling", new EvaluationResult(netProbability, netImpact));
				model.addAttribute("expModelling", new EvaluationResult(expProbability, expImpact));
				model.addAttribute("riskRegister", registerItem);
			}
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
			assessmentAndRiskProfileManager.UpdateAssessment(analysis, null);
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
			assessmentAndRiskProfileManager.UpdateAssetALE(analysis, null);
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
	public String loadAssessmentsOfAsset(@PathVariable Integer elementID, Model model, HttpSession session, Principal principal) throws Exception {
		// get analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));
		// retrieve asset
		Asset asset = serviceAsset.get(elementID);
		// load assessments by asset into model
		return assessmentByAsset(model, asset, serviceAssessment.getAllSelectedFromAsset(asset), idAnalysis, true);
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

			model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));

			// retrieve parameters which are considered in the expression
			// evaluation
			ValueFactory factory = new ValueFactory(serviceParameter.findAllAcronymParameterByAnalysisId(idAnalysis));

			AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);

			model.addAttribute("valueFactory", factory);

			// retrieve assessments of analysis
			List<Assessment> assessments = serviceAssessment.getAllSelectedFromAsset(asset);
			// parse assessments and initialise impact values to 0 if empty
			for (Assessment assessment : assessments) {
				if (assessment.getImpactFin() == null || assessment.getImpactFin().trim().isEmpty())
					assessment.setImpactFin("0");
				if (assessment.getImpactOp() == null || assessment.getImpactOp().trim().isEmpty())
					assessment.setImpactOp("0");
				if (assessment.getImpactLeg() == null || assessment.getImpactLeg().trim().isEmpty())
					assessment.setImpactLeg("0");
				if (assessment.getImpactRep() == null || assessment.getImpactRep().trim().isEmpty())
					assessment.setImpactRep("0");
				if (assessment.getLikelihood() == null || assessment.getLikelihood().trim().isEmpty())
					assessment.setLikelihood("0");
				// compute ALE
				AssessmentAndRiskProfileManager.ComputeAlE(assessment, factory, type);
				// update assessments
				serviceAssessment.saveOrUpdate(assessment);
				// add assessments of asset to model
			}
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
	public String loadByScenario(@PathVariable Integer elementID, Model model, HttpSession session, Principal principal) throws Exception {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// retrieve scenario from given id
		Scenario scenario = serviceScenario.get(elementID);
		// load all assessments by scenario to model
		return assessmentByScenario(model, scenario, serviceAssessment.getAllFromScenario(scenario), idAnalysis, true);
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
			if (scenario == null)
				return null;

			model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));

			// retrieve parameters which are considered in the expression
			// evaluation
			ValueFactory factory = new ValueFactory(serviceParameter.findAllAcronymParameterByAnalysisId(idAnalysis));

			model.addAttribute("valueFactory", factory);

			AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);

			// load assessments
			List<Assessment> assessments = serviceAssessment.getAllSelectedFromScenario(scenario);

			// parse assessments and initilaise illegal impact values
			for (Assessment assessment : assessments) {
				if (assessment.getImpactFin() == null || assessment.getImpactFin().trim().isEmpty())
					assessment.setImpactFin("0");
				if (assessment.getImpactOp() == null || assessment.getImpactOp().trim().isEmpty())
					assessment.setImpactOp("0");
				if (assessment.getImpactLeg() == null || assessment.getImpactLeg().trim().isEmpty())
					assessment.setImpactLeg("0");
				if (assessment.getImpactRep() == null || assessment.getImpactRep().trim().isEmpty())
					assessment.setImpactRep("0");
				if (assessment.getLikelihood() == null || assessment.getLikelihood().trim().isEmpty())
					assessment.setLikelihood("0");
				// compute ALE
				AssessmentAndRiskProfileManager.ComputeAlE(assessment, factory, type);
				// update assessments
				serviceAssessment.saveOrUpdate(assessments);
				// load assessments of scenario to model
			}

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

	/**
	 * generateAcronymValueMatching: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 */
	private Map<String, Double> generateKeyValueMatching(int idAnalysis) throws Exception {
		return serviceParameter.findAllAcronymParameterByAnalysisId(idAnalysis).stream().collect(Collectors.toMap(IAcronymParameter::getKey, IAcronymParameter::getValue));
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
		ALE ale = new ALE(asset.getName(), 0);
		ALE aleo = new ALE(asset.getName(), 0);
		ALE alep = new ALE(asset.getName(), 0);
		model.addAttribute("ale", ale);
		model.addAttribute("aleo", aleo);
		model.addAttribute("alep", alep);
		model.addAttribute("asset", asset);
		model.addAttribute("parameters", generateKeyValueMatching(idAnalysis));
		AssessmentAndRiskProfileManager.ComputeALE(assessments, ale, alep, aleo);
		if (sort)
			Collections.sort(assessments, new AssessmentComparator());
		model.addAttribute("assessments", assessments);
		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));
		model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));
		asset.setALE(ale.getValue());
		asset.setALEO(aleo.getValue());
		asset.setALEP(alep.getValue());
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
		ALE ale = new ALE(scenario.getName(), 0);
		ALE aleo = new ALE(scenario.getName(), 0);
		ALE alep = new ALE(scenario.getName(), 0);
		model.addAttribute("ale", ale);
		model.addAttribute("aleo", aleo);
		model.addAttribute("alep", alep);
		model.addAttribute("scenario", scenario);
		model.addAttribute("parameters", generateKeyValueMatching(idAnalysis));
		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));
		model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));
		AssessmentAndRiskProfileManager.ComputeALE(assessments, ale, alep, aleo);
		if (sort)
			Collections.sort(assessments, new AssessmentComparator());
		model.addAttribute("assessments", assessments);
		return "analyses/single/components/assessment/scenarios";
	}
}