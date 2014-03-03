package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.helper.ALE;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssessment;
import lu.itrust.business.service.ServiceAsset;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServiceScenario;

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
@RequestMapping("/Assessment")
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

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {

		// retrieve selected analysis
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;

		// add assessments to model
		model.addAttribute("assessments", serviceAssessment.loadAllFromAnalysisID(integer));
		return "analysis/components/assessment";
	}

	/**
	 * updateAssessment: <br>
	 * Description
	 * 
	 * @param session
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Update", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String updateAssessment(HttpSession session, Locale locale, Principal principal) {
		try {

			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute("selectedAnalysis");

			// check if analysis is not null
			if (integer == null)
				return new String("{\"error\":\"" + messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale) + "\" }");

			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);

			// check if analysis object is not null
			if (analysis == null)
				return new String("{\"error\":\"" + messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale) + "\" }");

			// update assessments of analysis
			assessmentManager.UpdateAssessment(analysis);
			
			// update
			serviceAnalysis.saveOrUpdate(analysis);

			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.update", null, "Assessments were successfully updated", locale) + "\"}");
		} catch (Exception e) {

			// return error
			e.printStackTrace();
			return new String("{\"error\":\"" + messageSource.getMessage("error.internal.assessment.generation", null, "An error occurred during the generation", locale) + "\"}");
		}
	}

	/**
	 * deleteAssessments: <br>
	 * Description
	 * 
	 * @param session
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Wipe", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String deleteAssessments(HttpSession session, Locale locale, Principal principal) {

		try {

			// retrieve analysis id
			Integer integer = (Integer) session.getAttribute("selectedAnalysis");

			// check if analysis id is not null
			if (integer == null)
				return new String("{\"error\":\"" + messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale) + "\" }");

			// load analysis object
			Analysis analysis = serviceAnalysis.get(integer);

			// check if analysis is not null
			if (analysis == null)
				return new String("{\"error\":\"" + messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale) + "\" }");

			// delete all assessments
			assessmentManager.WipeAssessment(analysis);

			// update
			serviceAnalysis.saveOrUpdate(analysis);

			
			// return success message
			return new String("{\"success\":\"" + messageSource.getMessage("success.assessment.wipe", null, "Assessments were successfully deleted", locale) + "\"}");
		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			return new String("{\"error\":\"" + messageSource.getMessage("error.internal.assessment.delete", null, "An error occurred during deletion", locale) + "\"}");
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
	@RequestMapping(value = "/Asset/{assetId}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String loadAssessmentsOfAsset(@PathVariable Integer assetId, Model model, HttpSession session, Principal principal) throws Exception {

		// get analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		// load analysis object
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		if (analysis == null)
			return null;

		// retrieve asset
		Asset asset = serviceAsset.get(assetId);
		if (!analysis.getAssets().contains(asset))
			return null;

		// load assessments by asset into model
		return assessmentByAsset(model, asset, serviceAssessment.findByAssetAndSelected(asset), idAnalysis);
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
	@RequestMapping(value = "/Asset/{assetId}/Update", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String updateAsset(@PathVariable int assetId, Model model, HttpSession session, Locale locale, Principal principal) {

		try {

			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			// retrieve asset by id
			Asset asset = serviceAsset.get(assetId);
			if (asset == null)
				return null;

			// retrieve extended paramters of analysis
			List<ExtendedParameter> parameters = serviceParameter.findExtendedByAnalysis(idAnalysis);

			// retrieve assessments of analysis
			List<Assessment> assessments = serviceAssessment.findByAssetAndSelected(asset);

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
			AssessmentManager.ComputeAlE(assessments, parameters);

			// update assessments
			serviceAssessment.saveOrUpdate(assessments);

			// add assessments of asset to model
			return assessmentByAsset(model, asset, assessments, idAnalysis);

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
	@RequestMapping(value = "/Scenario/{scenarioId}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String loadByScenario(@PathVariable Integer scenarioId, Model model, HttpSession session, Principal principal) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		// retrieve analysis object
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		if (analysis == null)
			return null;

		// retrieve scenario from given id
		Scenario scenario = serviceScenario.get(scenarioId);
		if (!analysis.getScenarios().contains(scenario))
			return null;

		// load all assessments by scenario to model
		return assessmentByScenario(model, scenario, serviceAssessment.findByScenarioAndSelected(scenario), idAnalysis);
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
	@RequestMapping(value = "/Scenario/{scenarioId}/Update", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String updateByScenario(@PathVariable int scenarioId, Model model, HttpSession session, Locale locale, Principal principal) {

		try {

			// load analysis id
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			// load scenario
			Scenario scenario = serviceScenario.get(scenarioId);
			if (scenario == null)
				return null;

			// load extended parameters
			List<ExtendedParameter> parameters = serviceParameter.findExtendedByAnalysis(idAnalysis);

			// load assessments
			List<Assessment> assessments = serviceAssessment.findByScenarioAndSelected(scenario);

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
			AssessmentManager.ComputeAlE(assessments, parameters);

			// update assessments
			serviceAssessment.saveOrUpdate(assessments);

			// load assessments of scenario to model
			return assessmentByScenario(model, scenario, assessments, idAnalysis);

		} catch (Exception e) {

			// return null on error
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * updateAcromyn: <br>
	 * Description
	 * 
	 * @param idParameter
	 * @param acronym
	 * @param session
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Update/Acronym/{idParameter}/{acronym}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String updateAcromyn(@PathVariable int idParameter, @PathVariable String acronym, HttpSession session, Locale locale, Principal principal) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

		// retrieve parameter id
		Parameter parameter = serviceParameter.findByIdAndAnalysis(idParameter, idAnalysis);
		if (parameter == null || !(parameter instanceof ExtendedParameter))
			return JsonMessage.Error(messageSource.getMessage("error.parameter.not_found", null, "Parameter cannot be found", locale));

		// load parameter by id
		ExtendedParameter extendedParameter = (ExtendedParameter) parameter;

		try {

			// retrieve assessments by acronym and analysis
			List<Assessment> assessments = serviceAssessment.findByAnalysisAndAcronym(idAnalysis, acronym);

			// parse assessments and update impact value to parameter acronym
			for (Assessment assessment : assessments) {
				if (acronym.equals(assessment.getImpactFin()))
					assessment.setImpactFin(extendedParameter.getAcronym());
				else if (acronym.equals(assessment.getImpactLeg()))
					assessment.setImpactLeg(extendedParameter.getAcronym());
				else if (acronym.equals(assessment.getImpactOp()))
					assessment.setImpactOp(extendedParameter.getAcronym());
				else if (acronym.equals(assessment.getImpactRep()))
					assessment.setImpactRep(extendedParameter.getAcronym());
				else if (acronym.equals(assessment.getLikelihood()))
					assessment.setLikelihood(extendedParameter.getAcronym());

				// update assessment
				serviceAssessment.saveOrUpdate(assessment);
			}
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.assessment.acronym.updated", new String[] { acronym, extendedParameter.getAcronym() },
					"Assessment acronym (" + acronym + ") was successfully updated with (" + extendedParameter.getAcronym() + ")", locale));
		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.assessment.acronym.updated", new String[] { acronym, extendedParameter.getAcronym() }, "Assessment acronym ("
					+ acronym + ") cannot be updated to (" + extendedParameter.getAcronym() + ")", locale));
		}

	}

	private Map<String, Double> generateAcronymValueMatching(int idAnalysis) {
		List<ExtendedParameter> extendedParameters = serviceParameter.findExtendedByAnalysis(idAnalysis);
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
	 * @return
	 */
	private String assessmentByAsset(Model model, Asset asset, List<Assessment> assessments, int idAnalysis) {
		ALE ale = new ALE(asset.getName(), 0);
		ALE aleo = new ALE(asset.getName(), 0);
		ALE alep = new ALE(asset.getName(), 0);
		model.addAttribute("ale", ale);
		model.addAttribute("aleo", aleo);
		model.addAttribute("alep", alep);
		model.addAttribute("asset", asset);
		model.addAttribute("parameters", generateAcronymValueMatching(idAnalysis));
		model.addAttribute("assessments", AssessmentManager.Sort(assessments, ale, alep, aleo));
		asset.setALE(ale.getValue());
		asset.setALEO(aleo.getValue());
		asset.setALEP(alep.getValue());
		serviceAsset.saveOrUpdate(asset);
		return "analysis/components/assessmentAsset";
	}

	/**
	 * assessmentByScenario: <br>
	 * Description
	 * 
	 * @param model
	 * @param scenario
	 * @param assessments
	 * @return
	 */
	private String assessmentByScenario(Model model, Scenario scenario, List<Assessment> assessments, int idAnalysis) {
		ALE ale = new ALE(scenario.getName(), 0);
		ALE aleo = new ALE(scenario.getName(), 0);
		ALE alep = new ALE(scenario.getName(), 0);
		model.addAttribute("ale", ale);
		model.addAttribute("aleo", aleo);
		model.addAttribute("alep", alep);
		model.addAttribute("scenario", scenario);
		model.addAttribute("parameters", generateAcronymValueMatching(idAnalysis));
		model.addAttribute("assessments", AssessmentManager.Sort(assessments, ale, alep, aleo));
		return "analysis/components/assessmentScenario";
	}
}