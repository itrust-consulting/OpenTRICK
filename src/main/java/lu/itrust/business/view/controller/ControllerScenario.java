/**
 * 
 */
package lu.itrust.business.view.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.naming.directory.InvalidAttributesException;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceScenario;
import lu.itrust.business.service.ServiceScenarioType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eom
 * 
 */
@Controller
@Secured("ROLE_USER")
@RequestMapping("/Scenario")
public class ControllerScenario {

	@Autowired
	private ServiceScenarioType serviceScenarioType;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AssessmentManager assessmentManager;

	private boolean buildAsset(List<String[]> errors, Scenario scenario,
			String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			if (id > 0)
				scenario.setId(jsonNode.get("id").asInt());
			scenario.setName(jsonNode.get("name").asText());
			scenario.setSelected(jsonNode.get("selected").asBoolean());
			scenario.setDescription(jsonNode.get("description").asText());
			JsonNode node = jsonNode.get("assetType");
			ScenarioType scenarioType = serviceScenarioType.get(node.get("id")
					.asInt());
			if (scenarioType == null) {
				errors.add(new String[] {
						"assetType",
						messageSource.getMessage(
								"error.scenariotype.not_found", null,
								"Selected scenario type cannot be found",
								locale) });
				return false;
			}
			scenario.setType(scenarioType);
			return true;

		} catch (JsonProcessingException e) {
			errors.add(new String[] {
					"scenario",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		} catch (IOException e) {
			errors.add(new String[] {
					"scenario",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		} catch (InvalidAttributesException e) {
			errors.add(new String[] {
					"scenario",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			errors.add(new String[] {
					"scenario",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		} catch (Exception e) {

			errors.add(new String[] {
					"scenario",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		}
		return false;
	}

	@RequestMapping(value = "/Select/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String[] select(@PathVariable int id, Principal principal, Locale locale) {
		try {
			Scenario scenario = serviceScenario.get(id);
			if (scenario == null)
				return new String[] {
						"error",
						messageSource.getMessage("error.scenario.not_found",
								null, "Scenario cannot be found", locale) };
			if (scenario.isSelected())
				assessmentManager.unSelectScenario(scenario);
			else
				assessmentManager.selectScenario(scenario);
			return new String[] {
					"error",
					messageSource.getMessage(
							"success.scenario.update.successfully", null,
							"Scenario was updated successfully", locale) };
		} catch (Exception e) {
			e.printStackTrace();
			return new String[] {
					"error",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) };

		}
	}

	@RequestMapping(value = "/Delete/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String[] delete(@PathVariable int id, Principal principal, Locale locale) {
		try {
			customDelete.deleteScenario(serviceScenario.get(id));
			return new String[] {
					"success",
					messageSource.getMessage(
							"success.scenario.delete.successfully", null,
							"Scenario was deleted successfully", locale) };
		} catch (Exception e) {
			e.printStackTrace();
			return new String[] {
					"error",
					messageSource.getMessage("error.scenario.delete.failed",
							null, "Scenario cannot be deleted", locale) };
		}
	}

	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model, HttpSession session, Principal principal)
			throws Exception {
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;
		model.addAttribute("scenarios",
				serviceScenario.loadAllFromAnalysisID(integer));
		return "analysis/components/scenario";
	}

	@RequestMapping("/Edit/{id}")
	public String edit(@PathVariable Integer id, Model model) throws Exception {
		model.addAttribute("scenariotypes", serviceScenarioType.loadAll());
		model.addAttribute("scenario", serviceScenario.get(id));
		return "analysis/components/widgets/scenarioForm";
	}

	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, HttpSession session,
			Principal principal, Locale locale) {
		List<String[]> errors = new LinkedList<>();
		try {
			Integer idAnalysis = (Integer) session
					.getAttribute("selectedAnalysis");
			if (idAnalysis == null) {
				errors.add(new String[] {
						"analysis",
						messageSource.getMessage("error.analysis.no_selected",
								null, "There is no selected analysis", locale) });
				return errors;
			}
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis == null) {
				errors.add(new String[] {
						"analysis",
						messageSource.getMessage("error.analysis.not_found",
								null, "Selected analysis cannot be found",
								locale) });
				return errors;
			}

			Scenario scenario = new Scenario();
			if (!buildAsset(errors, scenario, value, locale))
				return errors;
			if (scenario.getId() < 1) {
				assessmentManager.build(scenario, idAnalysis);
			} else {
				serviceScenario.merge(scenario);
				if (scenario.isSelected())
					assessmentManager.selectScenario(scenario);
				else
					assessmentManager.unSelectScenario(scenario);
			}
		} catch (ConstraintViolationException e) {
			errors.add(new String[] {
					"assetType",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
		} catch (IllegalArgumentException e) {
			errors.add(new String[] {
					"asset",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		}

		catch (Exception e) {
			errors.add(new String[] {
					"asset",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		}
		return errors;
	}

}
