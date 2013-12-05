/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.component.ALE;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.JsonMessage;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssessment;
import lu.itrust.business.service.ServiceAsset;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServiceScenario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
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
@RequestMapping("/Assessment")
@Secured("ROLE_USER")
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

	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model, HttpSession session, Principal principal)
			throws Exception {
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;
		model.addAttribute("assessments",
				serviceAssessment.loadAllFromAnalysisID(integer));
		return "analysis/components/assessment";
	}

	@RequestMapping(value = "/Update", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String update(HttpSession session, Locale locale) {
		try {
			Integer integer = (Integer) session
					.getAttribute("selectedAnalysis");

			if (integer == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage(
								"error.analysis.no_selected", null,
								"No selected analysis", locale) + "\" }");
			Analysis analysis = serviceAnalysis.get(integer);
			if (analysis == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage("error.analysis.not_found",
								null, "Analysis cannot be found", locale)
						+ "\" }");

			assessmentManager.UpdateAssessment(analysis);

			return new String("{\"success\":\""
					+ messageSource.getMessage("success.assessment.update",
							null, "Assessments were successfully updated",
							locale) + "\"}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String("{\"error\":\""
				+ messageSource.getMessage(
						"error.internal.assessment.generation", null,
						"An error occurred during the generation", locale)
				+ "\"}");
	}

	@RequestMapping(value = "/Wipe", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String delete(HttpSession session, Locale locale) {
		try {
			Integer integer = (Integer) session
					.getAttribute("selectedAnalysis");

			if (integer == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage(
								"error.analysis.no_selected", null,
								"No selected analysis", locale) + "\" }");
			Analysis analysis = serviceAnalysis.get(integer);
			if (analysis == null)
				return new String("{\"error\":\""
						+ messageSource.getMessage("error.analysis.not_found",
								null, "Analysis cannot be found", locale)
						+ "\" }");

			assessmentManager.WipeAssessment(analysis);

			return new String("{\"success\":\""
					+ messageSource.getMessage("success.assessment.wipe", null,
							"Assessments were successfully deleted", locale)
					+ "\"}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String("{\"error\":\""
				+ messageSource.getMessage("error.internal.assessment.delete",
						null, "An error occurred during deletion", locale)
				+ "\"}");
	}

	@RequestMapping(value = "/Asset/{assetId}/Update", method = RequestMethod.GET, headers = "Accept=application/json")
	public String update(@PathVariable int assetId, Model model,
			HttpSession session, Locale locale) {

		try {
			Integer integer = (Integer) session
					.getAttribute("selectedAnalysis");

			if (integer == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.analysis.no_selected", null,
						"No selected analysis", locale));

			Asset asset = serviceAsset.get(assetId);
			if (asset == null)
				return null;

			List<ExtendedParameter> parameters = serviceParameter
					.findExtendedByAnalysis(integer);

			List<Assessment> assessments = serviceAssessment
					.findByAssetAndSelected(asset);

			for (Assessment assessment : assessments) {
				if (assessment.getImpactFin() == null
						|| assessment.getImpactFin().trim().isEmpty())
					assessment.setImpactFin("0");
				if (assessment.getImpactOp() == null
						|| assessment.getImpactOp().trim().isEmpty())
					assessment.setImpactOp("0");
				if (assessment.getImpactLeg() == null
						|| assessment.getImpactLeg().trim().isEmpty())
					assessment.setImpactLeg("0");
				if (assessment.getImpactRep() == null
						|| assessment.getImpactRep().trim().isEmpty())
					assessment.setImpactRep("0");
				if (assessment.getLikelihood() == null
						|| assessment.getLikelihood().trim().isEmpty())
					assessment.setLikelihood("0");
			}

			AssessmentManager.ComputeAlE(assessments, parameters);
			serviceAssessment.saveOrUpdate(assessments);

			return assessmentByAsset(model, asset, assessments);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@RequestMapping(value = "/Asset/{assetId}", method = RequestMethod.GET, headers = "Accept=application/json")
	public String loadByAsset(@PathVariable Integer assetId, Model model,
			HttpSession session) throws Exception {
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;
		Analysis analysis = serviceAnalysis.get(integer);
		if (analysis == null)
			return null;
		Asset asset = serviceAsset.get(assetId);
		if (!analysis.getAssets().contains(asset))
			return null;

		return assessmentByAsset(model, asset,
				serviceAssessment.findByAssetAndSelected(asset));
	}

	private String assessmentByAsset(Model model, Asset asset,
			List<Assessment> assessments) {
		ALE ale = new ALE(asset.getName(), 0);
		ALE aleo = new ALE(asset.getName(), 0);
		ALE alep = new ALE(asset.getName(), 0);
		model.addAttribute("ale", ale);
		model.addAttribute("aleo", aleo);
		model.addAttribute("alep", alep);
		model.addAttribute("asset", asset);
		model.addAttribute("assessments",
				AssessmentManager.Sort(assessments, ale, alep, aleo));
		return "analysis/components/assessmentAsset";
	}

	@RequestMapping(value = "/Scenario/{scenarioId}/Update", method = RequestMethod.GET, headers = "Accept=application/json")
	public String updateByScenario(@PathVariable int scenarioId, Model model,
			HttpSession session, Locale locale) {

		try {
			Integer integer = (Integer) session
					.getAttribute("selectedAnalysis");

			if (integer == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.analysis.no_selected", null,
						"No selected analysis", locale));

			Scenario scenario = serviceScenario.get(scenarioId);
			if (scenario == null)
				return null;

			List<ExtendedParameter> parameters = serviceParameter
					.findExtendedByAnalysis(integer);

			List<Assessment> assessments = serviceAssessment
					.findByScenarioAndSelected(scenario);

			for (Assessment assessment : assessments) {
				if (assessment.getImpactFin() == null
						|| assessment.getImpactFin().trim().isEmpty())
					assessment.setImpactFin("0");
				if (assessment.getImpactOp() == null
						|| assessment.getImpactOp().trim().isEmpty())
					assessment.setImpactOp("0");
				if (assessment.getImpactLeg() == null
						|| assessment.getImpactLeg().trim().isEmpty())
					assessment.setImpactLeg("0");
				if (assessment.getImpactRep() == null
						|| assessment.getImpactRep().trim().isEmpty())
					assessment.setImpactRep("0");
				if (assessment.getLikelihood() == null
						|| assessment.getLikelihood().trim().isEmpty())
					assessment.setLikelihood("0");
			}

			AssessmentManager.ComputeAlE(assessments, parameters);
			serviceAssessment.saveOrUpdate(assessments);
			return assessmentByScenario(model, scenario, assessments);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@RequestMapping(value = "/Scenario/{scenarioId}", method = RequestMethod.GET, headers = "Accept=application/json")
	public String loadByScenario(@PathVariable Integer scenarioId, Model model,
			HttpSession session) throws Exception {
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;
		Analysis analysis = serviceAnalysis.get(integer);
		if (analysis == null)
			return null;
		Scenario scenario = serviceScenario.get(scenarioId);
		if (!analysis.getScenarios().contains(scenario))
			return null;

		return assessmentByScenario(model, scenario,
				serviceAssessment.findByScenarioAndSelected(scenario));
	}

	private String assessmentByScenario(Model model, Scenario scenario,
			List<Assessment> assessments) {
		ALE ale = new ALE(scenario.getName(), 0);
		ALE aleo = new ALE(scenario.getName(), 0);
		ALE alep = new ALE(scenario.getName(), 0);
		model.addAttribute("ale", ale);
		model.addAttribute("aleo", aleo);
		model.addAttribute("alep", alep);
		model.addAttribute("scenario", scenario);
		model.addAttribute("assessments",
				AssessmentManager.Sort(assessments, ale, alep, aleo));
		return "analysis/components/assessmentScenario";
	}

}
