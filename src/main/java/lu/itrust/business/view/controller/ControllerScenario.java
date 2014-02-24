/**
 * 
 */
package lu.itrust.business.view.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.naming.directory.InvalidAttributesException;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.component.JsonMessage;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceScenario;
import lu.itrust.business.service.ServiceScenarioType;
import lu.itrust.business.view.model.FieldEditor;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerScenario.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl. :
 * @version
 * @since Feb 4, 2014
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Scenario")
public class ControllerScenario {

	@Autowired
	private ServiceScenarioType serviceScenarioType;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AssessmentManager assessmentManager;

	/**
	 * select: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Select/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String select(@PathVariable int id, Principal principal, Locale locale, HttpSession session) {

		try {

			// retrieve scenario
			Scenario scenario = serviceScenario.get(id);
			if (scenario == null)
				return JsonMessage.Error(messageSource.getMessage("error.scenario.not_found", null, "Scenario cannot be found", locale));

			// select or unselect scenario
			if (scenario.isSelected())
				assessmentManager.unSelectScenario(scenario);
			else
				assessmentManager.selectScenario(scenario);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.scenario.update.successfully", null, "Scenario was updated successfully", locale));
		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		}
	}

	/**
	 * selectMultiple: <br>
	 * Description
	 * 
	 * @param ids
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Select", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	List<String> selectMultiple(@RequestBody List<Integer> ids, Principal principal, Locale locale, HttpSession session) {

		// set error list
		List<String> errors = new LinkedList<String>();

		try {

			// parse each scenario id
			for (Integer id : ids) {

				// select elements
				select(id, principal, locale, session);

				// return success message
			}

			// return empty errors list (success)
			return errors;

		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			errors.add(JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale)));
			return errors;
		}
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/Delete/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	public @ResponseBody
	String delete(@PathVariable int id, Principal principal, Locale locale, HttpSession session) {
		try {

			// try to delete assessment with this scenario
			customDelete.deleteScenario(serviceScenario.get(id));

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.scenario.delete.successfully", null, "Scenario was deleted successfully", locale));
		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.scenario.delete.failed", null, "Scenario cannot be deleted", locale));
		}
	}

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

		// retrieve analysis id
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;
		// load all scenarios from analysis
		model.addAttribute("scenarios", serviceScenario.loadAllFromAnalysisID(integer));

		return "analysis/components/scenario";
	}
	
	@RequestMapping(value = "/RRF", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String rrf(Model model, HttpSession session, Principal principal) throws Exception{
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<Scenario> scenarios = serviceScenario.loadAllFromAnalysisID(idAnalysis);
		model.addAttribute("scenarios", scenarios);
		return "analysis/components/widgets/scenarioRRF";
	}
	
	
	@RequestMapping(value = "/RRF/{idScenario}/Update", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String updateRRF(@RequestBody FieldEditor fieldEditor, @PathVariable int idScenario, Model model, HttpSession session, Principal principal) throws Exception{
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Scenario scenario = serviceScenario.findByIdAndAnalysis(fieldEditor.getId(), idAnalysis);
		Field field = ControllerEditField.FindField(Scenario.class, fieldEditor.getFieldName());
		if(field == null)
			return null;
		field.setAccessible(true);
		field.set(scenario, fieldEditor.getValue());
		
		ControllerEditField.SetFieldData(field, scenario, fieldEditor, null);
		return chartGenerator.rrfByScenario(fieldEditor.getId(), idAnalysis);
	}

	/**
	 * add: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Add")
	public String add(Model model) throws Exception {
		model.addAttribute("scenariotypes", serviceScenarioType.loadAll());
		model.addAttribute("scenario", new Scenario(serviceAssetType.loadAll()));
		return "analysis/components/widgets/scenarioForm";
	}

	/**
	 * edit: <br>
	 * Description
	 * 
	 * @param id
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Edit/{id}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String edit(@PathVariable Integer id, Model model, HttpSession session, Principal principal) throws Exception {

		// add scenario types to model
		model.addAttribute("scenariotypes", serviceScenarioType.loadAll());

		// add scenario to model
		model.addAttribute("scenario", serviceScenario.get(id));

		return "analysis/components/widgets/scenarioForm";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) {

		// create errors list
		List<String[]> errors = new LinkedList<>();

		// get analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null) {
			errors.add(new String[] { "analysis", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale) });
			return errors;
		}

		try {

			// load analysis
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis == null) {
				errors.add(new String[] { "analysis", messageSource.getMessage("error.analysis.not_found", null, "Selected analysis cannot be found", locale) });
				return errors;
			}

			int idScenario = retrieveId(value);
			Scenario scenario = null;
			if (idScenario > 0) {
				scenario = serviceScenario.get(idScenario);
				if (scenario == null) {
					errors.add(new String[] { "scenario", messageSource.getMessage("error.scenario.not_found", null, "Scenario cannot be found", locale) });
					return errors;
				}
			} else
				scenario = new Scenario();

			List<AssetType> assetTypes = serviceAssetType.loadAll();

			if (!buildScenario(errors, scenario, assetTypes, value, locale))
				return errors;
			if (scenario.getId() < 1) {
				assessmentManager.build(scenario, idAnalysis);
			} else {
				serviceScenario.saveOrUpdate(scenario);
				if (scenario.isSelected())
					assessmentManager.selectScenario(scenario);
				else
					assessmentManager.unSelectScenario(scenario);
			}
		} catch (ConstraintViolationException e) {
			errors.add(new String[] { "assetType", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
		} catch (IllegalArgumentException e) {
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}

		catch (Exception e) {
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}
		return errors;
	}

	/**
	 * aleByAsset: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping("/Chart/Ale")
	public @ResponseBody
	String aleByAsset(HttpSession session, Model model, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		return chartGenerator.aleByScenario(idAnalysis, locale);
	}

	/**
	 * assetByALE: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping("/Chart/Type/Ale")
	public @ResponseBody
	String assetByALE(HttpSession session, Model model, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		return chartGenerator.aleByScenarioType(idAnalysis, locale);
	}

	/**
	 * buildScenario: <br>
	 * Description
	 * 
	 * @param errors
	 * @param scenario
	 * @param assetTypes
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildScenario(List<String[]> errors, Scenario scenario, List<AssetType> assetTypes, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			scenario.setName(jsonNode.get("name").asText());
			scenario.setSelected(jsonNode.get("selected").asBoolean());
			scenario.setDescription(jsonNode.get("description").asText());
			JsonNode node = jsonNode.get("scenarioType");
			ScenarioType scenarioType = serviceScenarioType.get(node.get("id").asInt());
			if (scenarioType == null) {
				errors.add(new String[] { "assetType", messageSource.getMessage("error.scenariotype.not_found", null, "Selected scenario type cannot be found", locale) });
				return false;
			}
			scenario.setScenarioType(scenarioType);
			for (AssetType assetType : assetTypes){
				
				AssetTypeValue atv = scenario.retrieveAssetTypeValue(assetType);
				
				int value = 0;
				if (jsonNode.get(assetType.getType())!=null)
					value = jsonNode.get(assetType.getType()).asInt();
				
				if (atv!=null)
					atv.setValue(value);
				else
					scenario.addAssetTypeValue(new AssetTypeValue(assetType, value));
			}
			return true;

		} catch (JsonProcessingException e) {
			errors.add(new String[] { "scenario", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (IOException e) {
			errors.add(new String[] { "scenario", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (InvalidAttributesException e) {
			errors.add(new String[] { "scenario", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			errors.add(new String[] { "scenario", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (Exception e) {

			errors.add(new String[] { "scenario", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * retrieveId: <br>
	 * Description
	 * 
	 * @param source
	 * @return
	 */
	private int retrieveId(String source) {

		try {

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			// return scenario id
			return jsonNode.get("id").asInt();
		} catch (Exception e) {

			// return illegal id
			e.printStackTrace();
			return -1;
		}
	}
}