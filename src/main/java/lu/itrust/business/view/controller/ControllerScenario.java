package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.TS.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.dao.hbm.DAOHibernate;
import lu.itrust.business.exception.TrickException;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssessment;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceScenario;
import lu.itrust.business.service.ServiceScenarioType;
import lu.itrust.business.service.ServiceUser;
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
@RequestMapping("/Analysis/Scenario")
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
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceUser serviceUser;

	/**
	 * select: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Select/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String select(@PathVariable int elementID, Principal principal, Locale locale, HttpSession session) throws Exception {

		try {

			Integer analysis = (Integer) session.getAttribute("selectedAnalysis");

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysis).getAlpha3().substring(0, 2));

			// retrieve scenario
			Scenario scenario = serviceScenario.get(elementID);
			if (scenario == null)
				return JsonMessage.Error(messageSource.getMessage("error.scenario.not_found", null, "Scenario cannot be found", customLocale != null ? customLocale : locale));

			// select or unselect scenario
			if (scenario.isSelected())
				assessmentManager.unSelectScenario(scenario);
			else
				assessmentManager.selectScenario(scenario);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.scenario.update.successfully", null, "Scenario was updated successfully", customLocale != null ? customLocale : locale));
		} catch (Exception e) {

			Integer analysis = (Integer) session.getAttribute("selectedAnalysis");

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysis).getAlpha3().substring(0, 2));
			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), customLocale != null ? customLocale : locale));
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
	 * @throws Exception
	 */
	@RequestMapping(value = "/Select", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody List<String> selectMultiple(@RequestBody List<Integer> ids, Principal principal, Locale locale, HttpSession session) throws Exception {

		// set error list
		List<String> errors = new LinkedList<String>();

		try {

			for (Integer id : ids) {

				Integer analysisId = (Integer) session.getAttribute("selectedAnalysis");

				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().substring(0, 2));

				if (!serviceScenario.belongsToAnalysis(analysisId, id)) {
					errors.add(JsonMessage.Error(messageSource.getMessage("label.unauthorized_scenario", null, "One of the scenarios does not belong to this analysis!", customLocale != null
						? customLocale : locale)));
					return errors;
				}
			}

			// parse each scenario id
			for (Integer id : ids) {
				Integer analysisId = (Integer) session.getAttribute("selectedAnalysis");

				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().substring(0, 2));
				// select elements
				select(id, principal, customLocale != null ? customLocale : locale, session);

				// return success message
			}

			// return empty errors list (success)
			return errors;

		} catch (Exception e) {
			Integer analysisId = (Integer) session.getAttribute("selectedAnalysis");

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().substring(0, 2));
			// return error message
			e.printStackTrace();
			errors.add(JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), customLocale != null ? customLocale : locale)));
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
	 * @throws Exception
	 */
	@RequestMapping(value = "/Delete/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	public @ResponseBody String delete(@PathVariable int elementID, Principal principal, Locale locale, HttpSession session) throws Exception {
		try {

			Integer analysisId = (Integer) session.getAttribute("selectedAnalysis");

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().substring(0, 2));

			// try to delete assessment with this scenario
			customDelete.deleteScenario(serviceScenario.get(elementID));
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.scenario.delete.successfully", null, "Scenario was deleted successfully", customLocale != null ? customLocale : locale));
		} catch (Exception e) {

			Integer analysisId = (Integer) session.getAttribute("selectedAnalysis");

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().substring(0, 2));

			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.scenario.delete.failed", null, "Scenario cannot be deleted", customLocale != null ? customLocale : locale));
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
		List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(integer);
		List<Assessment> assessments = serviceAssessment.getAllFromAnalysisAndSelected(integer);
		model.addAttribute("scenarios", scenarios);
		model.addAttribute("scenarioALE", AssessmentManager.ComputeScenarioALE(scenarios, assessments));
		model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(integer));
		return "analyses/singleAnalysis/components/scenario/scenario";
	}

	@RequestMapping(value = "/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody Scenario get(@PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Scenario scenario = DAOHibernate.Initialise(serviceScenario.getFromAnalysisById(idAnalysis, elementID));
		scenario.setScenarioType(DAOHibernate.Initialise(scenario.getScenarioType()));
		for (AssetTypeValue assetTypeValue : scenario.getAssetTypeValues())
			assetTypeValue.setAssetType(DAOHibernate.Initialise(assetTypeValue.getAssetType()));
		return scenario;
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String add(Model model, HttpSession session, Principal principal) throws Exception {
		model.addAttribute("scenariotypes", serviceScenarioType.getAll());
		model.addAttribute("assetTypes", serviceAssetType.getAll());
		return "analyses/singleAnalysis/components/scenario/manageScenario";
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
	@RequestMapping("/Edit/{elementID}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String edit(@PathVariable Integer elementID, Model model, HttpSession session, Principal principal) throws Exception {

		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		if (idAnalysis == null)
			return null;

		// add scenario types to model
		model.addAttribute("scenariotypes", serviceScenarioType.getAll());

		// add scenario to model
		model.addAttribute("scenario", serviceScenario.getFromAnalysisById(idAnalysis, elementID));
		model.addAttribute("assetTypes", serviceAssetType.getAll());
		return "analyses/singleAnalysis/components/scenario/manageScenario";
	}

	@RequestMapping(value = "/Delete/AssetTypeValueDuplication", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String deleteDuplicationAssetTypeValue(HttpSession session, Principal principal, Locale locale) throws Exception {
		try {
			Integer analysisId = (Integer) session.getAttribute("selectedAnalysis");

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().substring(0, 2));
			List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(analysisId);
			customDelete.deleteDuplicationAssetTypeValue(scenarios);
			return JsonMessage.Success(messageSource.getMessage("success.delete.assettypevalue.duplication", null, "Duplication were successfully deleted", customLocale != null ? customLocale
				: locale));
		} catch (Exception e) {
			Integer analysisId = (Integer) session.getAttribute("selectedAnalysis");

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().substring(0, 2));
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.delete.assettypevalue.duplication", null, "Duplication cannot be deleted", customLocale != null ? customLocale : locale));
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
	 * @throws Exception
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) throws Exception {

		// create errors list
		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			// get analysis id
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

			if (idAnalysis == null) {
				errors.put("scenario", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
				return errors;
			}

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

			Scenario scenario = null;

			List<AssetType> assetTypes = serviceAssetType.getAll();

			scenario = buildScenario(errors, assetTypes, value, customLocale != null ? customLocale : locale);

			if (!errors.isEmpty())
				return errors;

			if (scenario.getId() > 0) {

				if (!serviceScenario.belongsToAnalysis(idAnalysis, scenario.getId())) {
					errors.put("scenario", messageSource.getMessage("error.scenario.not_belongs_to_analysis", null, "Scenario does not belong to analysis", customLocale != null ? customLocale
						: locale));
					return errors;
				}

				serviceScenario.saveOrUpdate(scenario);

			} else {
				if (serviceScenario.exist(idAnalysis, scenario.getName())) {

					errors.put("name", messageSource.getMessage("error.scenario.duplicate", new String[] { scenario.getName() }, String.format("Scenario (%s) already exists", scenario.getName()),
							customLocale != null ? customLocale : locale));
					return errors;
				} else {
					Analysis analysis = serviceAnalysis.get(idAnalysis);
					analysis.addAScenario(scenario);
					serviceAnalysis.saveOrUpdate(analysis);
				}

			}

			if (scenario.isSelected())
				assessmentManager.selectScenario(scenario);
			else
				assessmentManager.unSelectScenario(scenario);

			assessmentManager.build(scenario, idAnalysis);

			return errors;

		} catch (TrickException e) {
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis != null) {
				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
				errors.put("scenario", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
			} else
				errors.put("scenario", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			e.printStackTrace();
			return errors;
		} catch (Exception e) {
			Integer analysisId = (Integer) session.getAttribute("selectedAnalysis");

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(analysisId).getAlpha3().substring(0, 2));
			errors.put("scenario", messageSource.getMessage(e.getMessage(), null, e.getMessage(), customLocale != null ? customLocale : locale));
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
	 * @throws Exception
	 */
	@RequestMapping(value = "/Chart/Ale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public @ResponseBody String aleByAsset(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {

		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

		if (idAnalysis == null)
			return null;

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

		return chartGenerator.aleByScenario(idAnalysis, customLocale != null ? customLocale : locale);
	}

	/**
	 * assetByALE: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Chart/Type/Ale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public @ResponseBody String assetByALE(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));

		return chartGenerator.aleByScenarioType(idAnalysis, customLocale != null ? customLocale : locale);
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
	private Scenario buildScenario(Map<String, String> errors, List<AssetType> assetTypes, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			int idScenario = jsonNode.get("id").asInt();

			Scenario returnvalue = null;

			if (idScenario > 0) {
				returnvalue = serviceScenario.get(idScenario);
			} else {
				returnvalue = new Scenario();
			}

			ValidatorField validator = serviceDataValidation.findByClass(Scenario.class);
			if (validator == null)
				serviceDataValidation.register(validator = new ScenarioValidator());

			String error = null;

			String name = jsonNode.get("name").asText();

			JsonNode node = jsonNode.get("scenarioType");
			ScenarioType scenarioType = serviceScenarioType.get(node.get("id").asInt());

			String description = jsonNode.get("description").asText();

			error = validator.validate(returnvalue, "name", name);
			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				returnvalue.setName(name);
				returnvalue.setSelected(jsonNode.get("selected").asBoolean());
			}

			error = validator.validate(returnvalue, "description", description);
			if (error != null)
				errors.put("description", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				returnvalue.setDescription(description);
			}

			error = validator.validate(returnvalue, "scenarioType", scenarioType);
			if (error != null)
				errors.put("scenarioType", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				returnvalue.setScenarioType(scenarioType);

				// set all categories to 0
				for (String key : CategoryConverter.JAVAKEYS)
					returnvalue.setCategoryValue(key, 0);

				// set category according to value of scenario type
				returnvalue.setCategoryValue(CategoryConverter.getTypeFromScenario(returnvalue), 4);
			}

			for (AssetType assetType : assetTypes) {

				AssetTypeValue atv = null;

				int value = 0;
				if (jsonNode.get(assetType.getType()) != null)
					value = jsonNode.get(assetType.getType()).asInt();
				atv = returnvalue.retrieveAssetTypeValue(assetType);

				if (atv != null)
					atv.setValue(value);
				else
					returnvalue.addAssetTypeValue(new AssetTypeValue(assetType, value));

			}
			return returnvalue;

		} catch (TrickException e) {
			// return error message
			errors.put("scenario", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			e.printStackTrace();
		} catch (Exception e) {

			errors.put("scenario", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
		}

		return null;

	}
}