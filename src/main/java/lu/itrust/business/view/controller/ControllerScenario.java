package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.TS.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.component.MeasureManager;
import lu.itrust.business.component.ScenarioManager;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.component.helper.RRFFieldEditor;
import lu.itrust.business.component.helper.RRFFilter;
import lu.itrust.business.dao.hbm.DAOHibernate;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceScenario;
import lu.itrust.business.service.ServiceScenarioType;
import lu.itrust.business.validator.ScenarioValidator;
import lu.itrust.business.validator.field.ValidatorField;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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
	private ServiceMeasure serviceMeasure;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private AssessmentManager assessmentManager;

	@Autowired
	private ServiceDataValidation serviceDataValidation;


	
	/**
	 * select: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Select/{id}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
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
	@RequestMapping(value = "/Select", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
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
	@RequestMapping(value = "/Delete/{id}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
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

	@RequestMapping(value = "/{idScenario}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	Scenario get(@PathVariable int idScenario, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Scenario scenario = DAOHibernate.Initialise(serviceScenario.findByIdAndAnalysis(idScenario, idAnalysis));
		scenario.setScenarioType(DAOHibernate.Initialise(scenario.getScenarioType()));
		scenario.setAssetTypeValues(null);
		return scenario;
	}

	@RequestMapping(value = "/RRF", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String rrf(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<NormMeasure> normMeasures = serviceMeasure.findNormMeasureByAnalysisAndComputable(idAnalysis);
		List<Scenario> scenarios = serviceScenario.loadAllFromAnalysisID(idAnalysis);
		model.addAttribute("measures", MeasureManager.SplitByChapter(normMeasures));
		model.addAttribute("categories", CategoryConverter.JAVAKEYS);
		model.addAttribute("scenarios", ScenarioManager.SplitByType(scenarios));
		model.addAttribute("assetTypes", serviceAssetType.findByAnalysis(idAnalysis));
		Language language = serviceLanguage.findByAnalysis(idAnalysis);
		model.addAttribute("language", language == null ? locale.getISO3Language() : language.getAlpha3());
		return "analysis/components/widgets/rrfEditor";
	}

	@RequestMapping(value = "/RRF/Update", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String updateRRF(@RequestBody RRFFieldEditor fieldEditor, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Scenario scenario = serviceScenario.findByIdAndAnalysis(fieldEditor.getId(), idAnalysis);
		Field field = ControllerEditField.FindField(Scenario.class, fieldEditor.getFieldName());
		if (field == null)
			return null;
		field.setAccessible(true);
		field.set(scenario, fieldEditor.getValue());
		serviceScenario.saveOrUpdate(scenario);
		return chartGenerator.rrfByScenario(scenario, idAnalysis, locale, fieldEditor.getFilter());
	}

	@RequestMapping(value = "/RRF/{idScenario}/Load", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String load(@RequestBody RRFFilter filter, @PathVariable int idScenario, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Scenario scenario = serviceScenario.findByIdAndAnalysis(idScenario, idAnalysis);
		return chartGenerator.rrfByScenario(scenario, idAnalysis, locale, filter);
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
		model.addAttribute("assetTypes", serviceAssetType.loadAll());
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
		model.addAttribute("assetTypes", serviceAssetType.loadAll());
		return "analysis/components/widgets/scenarioForm";
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Delete/AssetTypeValueDuplication", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody
	String deleteDuplicationAssetTypeValue(HttpSession session, Principal principal, Locale locale) {
		try {
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			List<Scenario> scenarios = serviceScenario.loadAllFromAnalysisID(idAnalysis);
			customDelete.deleteDuplicationAssetTypeValue(scenarios);
			return JsonMessage.Success(messageSource.getMessage("success.delete.assettypevalue.duplication", null, "Duplication were successfully deleted", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.delete.assettypevalue.duplication", null, "Duplication cannot be deleted", locale));
		}

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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	Map<String, String> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) {

		// create errors list
		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			// get analysis id
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null) {
				errors.put("scenario", messageSource.getMessage("error.analysis.no_selected", null, "There is no analysis selected", locale));
				return errors;
			}

			// load analysis
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis == null) {
				errors.put("scenario", messageSource.getMessage("error.analysis.not_found", null, "Selected analysis cannot be found", locale));
				return errors;
			}

			Scenario scenario = new Scenario();

			List<AssetType> assetTypes = serviceAssetType.loadAll();

			buildScenario(errors, scenario, assetTypes, value, locale);

			if (!errors.isEmpty())
				// return error on failure
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

			return errors;

		} catch (Exception e) {
			errors.put("scenario", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return errors;
		}

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
	@RequestMapping(value = "/Chart/Ale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
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
	@RequestMapping(value = "/Chart/Type/Ale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
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
	private boolean buildScenario(Map<String, String> errors, Scenario scenario, List<AssetType> assetTypes, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			int idScenario = jsonNode.get("id").asInt();

			if (idScenario > 0)
				scenario.setId(idScenario);

			ValidatorField validator = serviceDataValidation.findByClass(Scenario.class);
			if (validator == null)
				serviceDataValidation.register(validator = new ScenarioValidator());

			String error = null;

			String name = jsonNode.get("name").asText();

			JsonNode node = jsonNode.get("scenarioType");
			ScenarioType scenarioType = serviceScenarioType.get(node.get("id").asInt());

			scenario.setDescription(jsonNode.get("description").asText());

			error = validator.validate(scenario, "name", name);
			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				scenario.setName(name);
				scenario.setSelected(jsonNode.get("selected").asBoolean());
			}

			error = validator.validate(scenario, "scenarioType", scenarioType);
			if (error != null)
				errors.put("scenarioType", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				scenario.setScenarioType(scenarioType);

				// set all categories to 0
				for (String key : CategoryConverter.JAVAKEYS)
					scenario.setCategoryValue(key, 0);

				// set category according to value of scenario type
				scenario.setCategoryValue(CategoryConverter.getTypeFromScenario(scenario), 1);
			}

			for (AssetType assetType : assetTypes) {

				AssetTypeValue atv = scenario.retrieveAssetTypeValue(assetType);

				int value = 0;
				if (jsonNode.get(assetType.getType()) != null)
					value = jsonNode.get(assetType.getType()).asInt();

				if (atv != null)
					atv.setValue(value);
				else
					scenario.addAssetTypeValue(new AssetTypeValue(assetType, value));
			}
			return true;

		} catch (Exception e) {

			errors.put("scenario", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		}

	}
}