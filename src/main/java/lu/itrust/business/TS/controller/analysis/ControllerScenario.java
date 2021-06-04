package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceAssetTypeValue;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.validator.ScenarioValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

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
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceAssetTypeValue serviceAssetTypeValue;

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
	 * @throws Exception
	 */
	@RequestMapping(value = "/Select/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String select(@PathVariable int elementID, Principal principal, Locale locale, HttpSession session) throws Exception {
		try {
			// retrieve scenario
			Scenario scenario = serviceScenario.get(elementID);
			if (scenario == null)
				return JsonMessage.Error(messageSource.getMessage("error.scenario.not_found", null, "Scenario cannot be found", locale));
			// select or unselect scenario
			if (scenario.isSelected())
				assessmentAndRiskProfileManager.unSelectScenario(scenario);
			else
				assessmentAndRiskProfileManager.selectScenario(scenario);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.scenario.update.successfully", null, "Scenario was updated successfully", locale));
		} catch (Exception e) {
			// return error message
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
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
	@RequestMapping(value = "/Select", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody List<String> selectMultiple(@RequestBody List<Integer> ids, Principal principal, Locale locale, HttpSession session) throws Exception {

		// set error list
		List<String> errors = new LinkedList<String>();

		try {
			Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (!serviceScenario.belongsToAnalysis(analysisId, ids)) {
				errors.add(JsonMessage.Error(messageSource.getMessage("label.unauthorized_scenario", null, "One of the scenarios does not belong to this analysis!", locale)));
				return errors;
			}
			assessmentAndRiskProfileManager.toggledScenarios(ids);
			// return empty errors list (success)
			return errors;

		} catch (Exception e) {
			// return error message
			TrickLogManager.Persist(e);
			errors.add(JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale)));
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
	@RequestMapping(value = "/Delete/{idScenario}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String delete(@PathVariable int idScenario, Principal principal, Locale locale, HttpSession session) throws Exception {
		try {
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// try to delete assessment with this scenario
			customDelete.deleteScenario(idScenario, idAnalysis);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.scenario.delete.successfully", null, "Scenario was deleted successfully", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error message
			TrickLogManager.Persist(e);
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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {
		// retrieve analysis id
		Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// load all scenarios from analysis
		List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(integer);
		List<Assessment> assessments = serviceAssessment.getAllFromAnalysisAndSelected(integer);
		model.addAttribute("scenarios", scenarios);
		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(integer));
		model.addAttribute("scenarioALE", AssessmentAndRiskProfileManager.ComputeScenarioALE(scenarios, assessments));
		model.addAttribute("isEditable", !OpenMode.isReadOnly((OpenMode) session.getAttribute(Constant.OPEN_MODE)));
		model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(integer));
		model.addAttribute("isProfile", serviceAnalysis.isProfile(integer));
		return "analyses/single/components/scenario/scenario";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String add(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		return loadFormData(null, model, (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), locale);
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String edit(@PathVariable Integer elementID, Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return loadFormData(serviceScenario.getFromAnalysisById(idAnalysis, elementID), model, idAnalysis, locale);
	}

	@RequestMapping(value = "/Delete/AssetTypeValueDuplication", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String deleteDuplicationAssetTypeValue(HttpSession session, Principal principal, Locale locale) throws Exception {
		try {
			customDelete.deleteDuplicationAssetTypeValue((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			return JsonMessage.Success(messageSource.getMessage("success.delete.assettypevalue.duplication", null, "Duplication were successfully deleted", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
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
	 * @throws Exception
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) throws Exception {

		// create errors list
		Map<String, Object> results = new LinkedHashMap<>(), errors = new HashMap<>();

		try {

			results.put("errors", errors);
			// get analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Analysis analysis = serviceAnalysis.get(idAnalysis);

			Map<Integer, AssetType> assetTypes = serviceAssetType.getAll().stream().collect(Collectors.toMap(AssetType::getId, Function.identity()));

			Scenario scenario = buildScenario(idAnalysis, errors, assetTypes, value, locale, analysis.isQuantitative());

			if (!errors.isEmpty())
				return results;
			if (scenario.getId() > 0) {
				if (!serviceScenario.belongsToAnalysis(idAnalysis, scenario.getId())) {
					errors.put("scenario", messageSource.getMessage("error.scenario.not_belongs_to_analysis", null, "Scenario does not belong to analysis", locale));
					return results;
				}

				List<AssetTypeValue> assetTypeValues = new LinkedList<>();

				if (scenario.isAssetLinked()) {
					assetTypeValues.addAll(scenario.getAssetTypeValues());
					scenario.getAssetTypeValues().clear();
				}

				serviceScenario.saveOrUpdate(scenario);

				if (!assetTypeValues.isEmpty())
					serviceAssetTypeValue.delete(assetTypeValues);

				if (scenario.isSelected())
					assessmentAndRiskProfileManager.selectScenario(scenario);
				else
					assessmentAndRiskProfileManager.unSelectScenario(scenario);

			}
			assessmentAndRiskProfileManager.buildOnly(scenario, analysis);
			serviceAnalysis.saveOrUpdate(analysis);
			results.put("id", scenario.getId());
		} catch (TrickException e) {
			errors.put("scenario", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			errors.put("scenario", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}
		return results;

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
	@RequestMapping(value = "/Chart/Ale", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object aleByAsset(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {

		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

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
	 * @throws Exception
	 */
	@RequestMapping(value = "/Chart/Type/Ale", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object assetByALE(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return chartGenerator.aleByScenarioType(idAnalysis, locale);
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
	@RequestMapping(value = "/Chart/Risk", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object riskByAsset(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return chartGenerator.riskByScenario(idAnalysis, locale);
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
	@RequestMapping(value = "/Chart/Type/Risk", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object riskByAssetType(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return chartGenerator.riskByScenarioType(idAnalysis, locale);
	}

	/**
	 * buildScenario: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * 
	 * @param errors
	 * @param scenario
	 * @param assetTypes
	 * @param source
	 * @param locale
	 * @return
	 */
	private Scenario buildScenario(Integer idAnalysis, Map<String, Object> errors, Map<Integer, AssetType> assetTypes, String source, Locale locale, boolean isQuantitative) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			JsonNode jsonNode = mapper.readTree(source);

			int idScenario = jsonNode.get("id").asInt(-1);

			Scenario scenario = null;

			if (idScenario > 0) {
				scenario = serviceScenario.get(idScenario);
			} else {
				scenario = new Scenario();
			}

			ValidatorField validator = serviceDataValidation.findByClass(Scenario.class);
			if (validator == null)
				serviceDataValidation.register(validator = new ScenarioValidator());

			String error = null;

			String name = jsonNode.get("name").asText("").trim();

			JsonNode node = jsonNode.get("scenarioType");
			ScenarioType scenarioType = null;

			try {
				Integer i = node.get("id").asInt(-1);
				if (i == -1)
					throw new TrickException("error.scenario_type.not_selected", "You need to select a scenario type");
				scenarioType = ScenarioType.valueOf(i);
				if (scenario.getType() != scenarioType) {
					scenario.setType(scenarioType);
					for (String category : ScenarioType.JAVAKEYS)
						scenario.setCategoryValue(category, 0);
				}
			} catch (TrickException e) {
				errors.put("scenarioType", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			}

			String description = jsonNode.get("description").asText("").trim();

			error = validator.validate(scenario, "name", name);
			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else if ((scenario.getId() > 0 && !scenario.getName().trim().equalsIgnoreCase(name.trim()) || scenario.getId() < 0) && serviceScenario.exist(idAnalysis, name))
				errors.put("name", messageSource.getMessage("error.scenario.duplicate", new String[] { scenario.getName() },
						String.format("Scenario (%s) already exists", scenario.getName()), locale));
			else
				scenario.setName(name);

			scenario.setSelected(jsonNode.get("selected").asBoolean());
			scenario.setAssetLinked(jsonNode.get("assetLinked").asBoolean());

			error = validator.validate(scenario, "description", description);
			if (error != null)
				errors.put("description", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				scenario.setDescription(description);

			JsonNode assetTypesValues = jsonNode.get("assetTypeValues");

			if (assetTypesValues == null)
				throw new TrickException("error.scenario.asset_types.empty", "Asset types cannot be found");

			scenario.getAssetTypeValues().forEach(assetTypeValue -> assetTypeValue.setValue(0));

			for (JsonNode assetTypeNoe : assetTypesValues)
				scenario.addApplicable(assetTypes.get(assetTypeNoe.asInt(-1)));

			scenario.getLinkedAssets().clear();

			JsonNode assetValues = jsonNode.get("assetValues");

			if (assetValues == null)
				throw new TrickException("error.scenario.assets.empty", "Asset values cannot be found");

			for (JsonNode assetNode : assetValues)
				scenario.addApplicable(serviceAsset.get(assetNode.asInt(-1)));

			if (isQuantitative) {
				scenario.setAccidental(jsonNode.get("accidental").asInt());

				scenario.setEnvironmental(jsonNode.get("environmental").asInt());

				scenario.setExternalThreat(jsonNode.get("externalThreat").asInt());

				scenario.setInternalThreat(jsonNode.get("internalThreat").asInt());

				scenario.setIntentional(jsonNode.get("intentional").asInt());

				scenario.setDetective(jsonNode.get("detective").asDouble());

				scenario.setCorrective(jsonNode.get("corrective").asDouble());

				scenario.setLimitative(jsonNode.get("limitative").asDouble());

				scenario.setPreventive(jsonNode.get("preventive").asDouble());

				if (!scenario.hasThreatSource())
					throw new TrickException("error.scenario.threat.source", "Please define a threat source.");
				if (!scenario.hasControlCharacteristics())
					throw new TrickException("error.scenario.control.characteristic", "Sum of the control characteristics must be equal to 1.");
			}

			return scenario;

		} catch (TrickException e) {
			errors.put("scenario", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			errors.put("scenario", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}

		return null;

	}

	private String loadFormData(Scenario scenario, Model model, Integer idAnalysis, Locale locale) {
		AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);
		if (AnalysisType.isQualitative(type))
			model.addAttribute("scenariotypes", ScenarioType.getAll());
		else
			model.addAttribute("scenariotypes", ScenarioType.getAllCIA());
		List<AssetType> assetTypes = serviceAssetType.getAll();
		assetTypes.sort((a1, a2) -> NaturalOrderComparator.compareTo(messageSource.getMessage("label.asset_type." + a1.getName().toLowerCase(), null, locale),
				messageSource.getMessage("label.asset_type." + a2.getName().toLowerCase(), null, locale)));
		List<Asset> assets = serviceAsset.getAllFromAnalysis(idAnalysis);
		assets.sort((a1, a2) -> NaturalOrderComparator.compareTo(a1.getName(), a2.getName()));
		Map<Object, Integer> assetTypeValues = assetTypes.stream().collect(Collectors.toMap(Function.identity(), assetType -> 0)),
				assetValues = assets.stream().collect(Collectors.toMap(Function.identity(), assetType -> 0));
		if (!(scenario == null || scenario.getId() < 1)) {
			if (scenario.isAssetLinked())
				scenario.getLinkedAssets().forEach(asset -> assetValues.put(asset, 1));
			else
				scenario.getAssetTypeValues().forEach(assetTypeValue -> assetTypeValues.put(assetTypeValue.getAssetType(), assetTypeValue.getValue()));
			model.addAttribute("scenario", scenario);
		}
		model.addAttribute("type", type);
		model.addAttribute("assetTypeValues", assetTypeValues);
		model.addAttribute("assetValues", assetValues);
		return "analyses/single/components/scenario/form";
	}
}