package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.assessment.helper.AssessmentComparator;
import lu.itrust.business.TS.model.assessment.helper.AssessmentManager;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.scenario.Scenario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	private AssessmentManager assessmentManager;

	@Autowired
	private ServiceParameter serviceParameter;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceLanguage serviceLanguage;

	/**
	 * updateAssessment: <br>
	 * Description
	 * 
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Refresh", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String refreshAssessment(HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());

			// check if analysis is not null
			if (integer == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", customLocale != null ? customLocale : locale) + "\" }");

			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);

			// check if analysis object is not null
			if (analysis == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", customLocale != null ? customLocale : locale) + "\" }");
			// update assessments of analysis
			assessmentManager.WipeAssessment(analysis);
			assessmentManager.UpdateAssessment(analysis);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\""
					+ messageSource.getMessage("success.assessment.refresh", null, "Assessments were successfully refreshed", customLocale != null ? customLocale : locale) + "\"}");
		} catch (TrickException e) {
			e.printStackTrace();
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
		} catch (Exception e) {
			// return error
			e.printStackTrace();
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());
			return new String("{\"error\":\""
					+ messageSource.getMessage("error.internal.assessment.generation", null, "An error occurred during the generation", customLocale != null ? customLocale
							: locale) + "\"}");
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
	@RequestMapping(value = "/Update", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateAssessment(HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());
			// check if analysis is not null
			if (integer == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", customLocale != null ? customLocale : locale) + "\" }");

			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);

			// check if analysis object is not null
			if (analysis == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", customLocale != null ? customLocale : locale) + "\" }");
			// update assessments of analysis
			assessmentManager.UpdateAssessment(analysis);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\""
					+ messageSource.getMessage("success.assessment.update", null, "Assessments were successfully updated", customLocale != null ? customLocale : locale) + "\"}");
		} catch (TrickException e) {
			e.printStackTrace();
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
		} catch (Exception e) {
			// return error
			e.printStackTrace();
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());
			return new String("{\"error\":\""
					+ messageSource.getMessage("error.internal.assessment.generation", null, "An error occurred during the generation", customLocale != null ? customLocale
							: locale) + "\"}");
		}
	}

	@RequestMapping(value = "/Update/ALE", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateAle(HttpSession session, Locale locale, Principal principal) throws Exception {
		try {

			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());
			// check if analysis is not null
			if (integer == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", customLocale != null ? customLocale : locale) + "\" }");

			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);

			// check if analysis object is not null
			if (analysis == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", customLocale != null ? customLocale : locale) + "\" }");
			// update assessments of analysis
			assessmentManager.UpdateAssetALE(analysis);
			// update
			serviceAnalysis.saveOrUpdate(analysis);
			// return success message
			return new String("{\"success\":\""
					+ messageSource.getMessage("success.assessment.ale.update", null, "Assessments ale were successfully updated", customLocale != null ? customLocale : locale)
					+ "\"}");
		} catch (Exception e) {

			// return error
			e.printStackTrace();
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());
			return new String("{\"error\":\""
					+ messageSource.getMessage("error.internal.assessment.ale.update", null, "Assessment ale update failed: an error occurred", customLocale != null ? customLocale
							: locale) + "\"}");
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
	@RequestMapping(value = "/Asset/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadAssessmentsOfAsset(@PathVariable Integer elementID, Model model, HttpSession session, Principal principal) throws Exception {

		// get analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return null;

		// load analysis object
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		if (analysis == null)
			return null;

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
	@RequestMapping(value = "/Asset/{elementID}/Update", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String updateAsset(@PathVariable int elementID, Model model, HttpSession session, Locale locale, Principal principal) {

		try {

			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			// retrieve asset by id
			Asset asset = serviceAsset.get(elementID);
			if (asset == null)
				return null;

			model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));

			// retrieve parameters which are considered in the expression evaluation
			List<AcronymParameter> expressionParameters = serviceParameter.getAllExpressionParametersFromAnalysis(idAnalysis);

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
			}

			// compute ALE
			AssessmentManager.ComputeAlE(assessments, expressionParameters);

			// update assessments
			serviceAssessment.saveOrUpdate(assessments);

			// add assessments of asset to model
			return assessmentByAsset(model, asset, assessments, idAnalysis, false);

		} catch (Exception e) {

			// return null
			e.printStackTrace();
			return null;
		}
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
	@RequestMapping(value = "/Scenario/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadByScenario(@PathVariable Integer elementID, Model model, HttpSession session, Principal principal) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return null;

		// retrieve analysis object
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		if (analysis == null)
			return null;

		model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));

		// retrieve scenario from given id
		Scenario scenario = serviceScenario.get(elementID);

		// load all assessments by scenario to model
		return assessmentByScenario(model, scenario, serviceAssessment.getAllSelectedFromScenario(scenario), idAnalysis, true);
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
	@RequestMapping(value = "/Scenario/{elementID}/Update", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String updateByScenario(@PathVariable int elementID, Model model, HttpSession session, Locale locale, Principal principal) {

		try {

			// load analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			// load scenario
			Scenario scenario = serviceScenario.get(elementID);
			if (scenario == null)
				return null;

			model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));

			// retrieve parameters which are considered in the expression evaluation
			List<AcronymParameter> expressionParameters = serviceParameter.getAllExpressionParametersFromAnalysis(idAnalysis);

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
			}

			// compute ALE
			AssessmentManager.ComputeAlE(assessments, expressionParameters);

			// update assessments
			serviceAssessment.saveOrUpdate(assessments);

			// load assessments of scenario to model
			return assessmentByScenario(model, scenario, assessments, idAnalysis, false);

		} catch (Exception e) {

			// return null on error
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * generateAcronymValueMatching: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 */
	private Map<String, Double> generateAcronymValueMatching(int idAnalysis) throws Exception {
		List<ExtendedParameter> extendedParameters = serviceParameter.getAllExtendedFromAnalysis(idAnalysis);
		Map<String, Double> matchingParameters = new LinkedHashMap<String, Double>(extendedParameters.size());
		for (ExtendedParameter extendedParameter : extendedParameters)
			matchingParameters.put(extendedParameter.getAcronym(), extendedParameter.getValue());
		return matchingParameters;

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
		model.addAttribute("parameters", generateAcronymValueMatching(idAnalysis));
		AssessmentManager.ComputeALE(assessments, ale, alep, aleo);
		if (sort)
			Collections.sort(assessments, new AssessmentComparator());
		model.addAttribute("assessments", assessments);
		model.addAttribute("show_cssf", serviceAnalysis.isAnalysisCssf(idAnalysis));
		model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));
		asset.setALE(ale.getValue());
		asset.setALEO(aleo.getValue());
		asset.setALEP(alep.getValue());
		serviceAsset.saveOrUpdate(asset);
		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis).getAlpha2());
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
		model.addAttribute("parameters", generateAcronymValueMatching(idAnalysis));
		model.addAttribute("show_cssf", serviceAnalysis.isAnalysisCssf(idAnalysis));
		model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(idAnalysis));
		AssessmentManager.ComputeALE(assessments, ale, alep, aleo);
		if (sort)
			Collections.sort(assessments, new AssessmentComparator());
		model.addAttribute("assessments", assessments);
		model.addAttribute("language", serviceLanguage.getFromAnalysis(idAnalysis).getAlpha2());
		return "analyses/single/components/assessment/scenarios";
	}
}