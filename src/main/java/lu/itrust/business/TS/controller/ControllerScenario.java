package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.asset.helper.AssetTypeValueComparator;
import lu.itrust.business.TS.model.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
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
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

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
			return JsonMessage.Error(messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
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
			errors.add(JsonMessage.Error(messageSource.getMessage("error.internal", null, "Internal error occurred", locale)));
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
		OpenMode open = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		// load all scenarios from analysis
		List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(integer);
		List<Assessment> assessments = serviceAssessment.getAllFromAnalysisAndSelected(integer);
		model.addAttribute("scenarios", scenarios);
		model.addAttribute("scenarioALE", AssessmentAndRiskProfileManager.ComputeScenarioALE(scenarios, assessments));
		model.addAttribute("isEditable", OpenMode.isReadOnly(open) && serviceUserAnalysisRight.isUserAuthorized(integer, principal.getName(), AnalysisRight.MODIFY));
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
	public String add(Model model, HttpSession session, Principal principal) throws Exception {
		Integer analysisID = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (serviceAnalysis.isAnalysisCssf(analysisID))
			model.addAttribute("scenariotypes", ScenarioType.getAll());
		else
			model.addAttribute("scenariotypes", ScenarioType.getAllCIA());
		model.addAttribute("assetTypes", serviceAssetType.getAll());
		return "analyses/single/components/scenario/manageScenario";
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
	public String edit(@PathVariable Integer elementID, Model model, HttpSession session, Principal principal) throws Exception {

		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (serviceAnalysis.isAnalysisCssf(idAnalysis))
			model.addAttribute("scenariotypes", ScenarioType.getAll());
		else
			model.addAttribute("scenariotypes", ScenarioType.getAllCIA());
		// add scenario to model
		Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, elementID);
		scenario.getAssetTypeValues().sort(new AssetTypeValueComparator());
		model.addAttribute("scenario", scenario);
		model.addAttribute("assetTypes", serviceAssetType.getAll());
		return "analyses/single/components/scenario/manageScenario";
	}

	@RequestMapping(value = "/Delete/AssetTypeValueDuplication", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String deleteDuplicationAssetTypeValue(HttpSession session, Principal principal, Locale locale) throws Exception {
		try {
			Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(analysisId);
			customDelete.deleteDuplicationAssetTypeValue(scenarios);
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
	public @ResponseBody Map<String, String> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) throws Exception {

		// create errors list
		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {
			// get analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Analysis analysis = serviceAnalysis.get(idAnalysis);

			List<AssetType> assetTypes = serviceAssetType.getAll();

			Scenario scenario = buildScenario(errors, assetTypes, value, locale, analysis.isCssf());

			if (!errors.isEmpty())
				return errors;

			if (scenario.getId() > 0) {

				if (!serviceScenario.belongsToAnalysis(idAnalysis, scenario.getId())) {
					errors.put("scenario", messageSource.getMessage("error.scenario.not_belongs_to_analysis", null, "Scenario does not belong to analysis", locale));
					return errors;
				}

				serviceScenario.saveOrUpdate(scenario);

				if (scenario.isSelected())
					assessmentAndRiskProfileManager.selectScenario(scenario);
				else
					assessmentAndRiskProfileManager.unSelectScenario(scenario);

			} else {
				if (serviceScenario.exist(idAnalysis, scenario.getName())) {
					errors.put("name", messageSource.getMessage("error.scenario.duplicate", new String[] { scenario.getName() },
							String.format("Scenario (%s) already exists", scenario.getName()), locale));
					return errors;
				}
			}

			assessmentAndRiskProfileManager.buildOnly(scenario, analysis);

			serviceAnalysis.saveOrUpdate(analysis);

			return errors;

		} catch (TrickException e) {
			errors.put("scenario", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return errors;
		} catch (Exception e) {
			errors.put("scenario", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
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
	@RequestMapping(value = "/Chart/Ale", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String aleByAsset(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {

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
	public @ResponseBody String assetByALE(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
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
	private Scenario buildScenario(Map<String, String> errors, List<AssetType> assetTypes, String source, Locale locale, boolean cssf) {
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
			ScenarioType scenarioType = null;

			try {

				Integer i = node.get("id").asInt();

				if (i == -1)
					throw new TrickException("error.scenario_type.not_selected", "You need to select a scenario type");

				scenarioType = ScenarioType.valueOf(i);

				returnvalue.setType(scenarioType);

				// set category according to value of scenario type
				returnvalue.setCategoryValue(CategoryConverter.getTypeFromScenarioType(scenarioType.getName()), 1);

			} catch (TrickException e) {
				errors.put("scenarioType", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			}
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

			returnvalue.setAccidental(jsonNode.get("accidental").asInt());
			
			returnvalue.setEnvironmental(jsonNode.get("environmental").asInt());
			
			returnvalue.setExternalThreat(jsonNode.get("externalThreat").asInt());
			
			returnvalue.setInternalThreat(jsonNode.get("internalThreat").asInt());
			
			returnvalue.setIntentional(jsonNode.get("intentional").asInt());
			
			returnvalue.setDetective(jsonNode.get("detective").asDouble());

			returnvalue.setCorrective(jsonNode.get("corrective").asDouble());
			
			returnvalue.setLimitative(jsonNode.get("limitative").asDouble());
			
			returnvalue.setPreventive(jsonNode.get("preventive").asDouble());
			
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
			if(!returnvalue.hasThreatSource())
				throw new TrickException("error.scenario.threat.source", "Please define a threat source.");
			if(!returnvalue.hasControlCharacteristics())
				throw new TrickException("error.scenario.control.characteristic", "Sum of the control characteristics must be equal to 1.");
			return returnvalue;

		} catch (TrickException e) {
			// return error message
			errors.put("scenario", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			errors.put("scenario", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}

		return null;

	}
}