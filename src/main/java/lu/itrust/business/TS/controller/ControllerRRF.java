package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.JSTLFunctions;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.hbm.DAOHibernate;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.rrf.ImportRRFForm;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.model.scenario.helper.ScenarioManager;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;
import lu.itrust.business.TS.model.standard.measure.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;
import lu.itrust.business.TS.model.standard.measure.helper.Chapter;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ControllerRRF.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Oct 16, 2014
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/RRF")
public class ControllerRRF {

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	/**
	 * rrf: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String rrf(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<Measure> measures = serviceMeasure.getAllNotMaturityMeasuresFromAnalysisAndComputable(idAnalysis);
		List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(idAnalysis);
		Map<Chapter, List<Measure>> splittedmeasures = MeasureManager.SplitByChapter(measures);
		if (!splittedmeasures.isEmpty() && splittedmeasures.entrySet().iterator().next().getValue().get(0) != null) {

			model.addAttribute("measures", splittedmeasures);
			model.addAttribute("scenarios", ScenarioManager.SplitByType(scenarios));

			Measure measure = null;

			measure = splittedmeasures.entrySet().iterator().next().getValue().get(0);

			if (measure instanceof NormalMeasure) {
				NormalMeasure normalMeasure = (NormalMeasure) measure;
				model.addAttribute("standardid", normalMeasure.getAnalysisStandard().getStandard().getId());
				model.addAttribute("measureid", normalMeasure.getId());
				model.addAttribute("strength_measure", normalMeasure.getMeasurePropertyList().getFMeasure());
				model.addAttribute("strength_sectorial", normalMeasure.getMeasurePropertyList().getFSectoral());
				if (serviceAnalysis.isAnalysisCssf(idAnalysis)) {
					model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getAllCategories());
				} else {
					model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getCIACategories());
				}
				model.addAttribute("preventive", normalMeasure.getMeasurePropertyList().getPreventive());
				model.addAttribute("detective", normalMeasure.getMeasurePropertyList().getDetective());
				model.addAttribute("limitative", normalMeasure.getMeasurePropertyList().getLimitative());
				model.addAttribute("corrective", normalMeasure.getMeasurePropertyList().getCorrective());
				model.addAttribute("intentional", normalMeasure.getMeasurePropertyList().getIntentional());
				model.addAttribute("accidental", normalMeasure.getMeasurePropertyList().getAccidental());
				model.addAttribute("environmental", normalMeasure.getMeasurePropertyList().getEnvironmental());
				model.addAttribute("internalThreat", normalMeasure.getMeasurePropertyList().getInternalThreat());
				model.addAttribute("externalThreat", normalMeasure.getMeasurePropertyList().getExternalThreat());
				List<AssetType> assetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);
				List<AssetTypeValue> assetTypeValues = normalMeasure.getAssetTypeValues();
				int size = assetTypeValues.size();
				for (AssetType assetType : assetTypes) {
					if (normalMeasure.getAssetTypeValueByAssetType(assetType) == null)
						normalMeasure.addAnAssetTypeValue(new AssetTypeValue(assetType, 0));
				}

				if (assetTypeValues.size() != size)
					serviceMeasure.saveOrUpdate(measure);

				model.addAttribute("assetTypes", assetTypeValues);
			}
			if (measure instanceof AssetMeasure) {
				AssetMeasure assetMeasure = (AssetMeasure) measure;
				model.addAttribute("standardid", assetMeasure.getAnalysisStandard().getStandard().getId());
				model.addAttribute("measureid", assetMeasure.getId());
				model.addAttribute("strength_measure", assetMeasure.getMeasurePropertyList().getFMeasure());
				model.addAttribute("strength_sectorial", assetMeasure.getMeasurePropertyList().getFSectoral());
				if (serviceAnalysis.isAnalysisCssf(idAnalysis)) {
					model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getAllCategories());
				} else {
					model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getCIACategories());
				}
				model.addAttribute("preventive", assetMeasure.getMeasurePropertyList().getPreventive());
				model.addAttribute("detective", assetMeasure.getMeasurePropertyList().getDetective());
				model.addAttribute("limitative", assetMeasure.getMeasurePropertyList().getLimitative());
				model.addAttribute("corrective", assetMeasure.getMeasurePropertyList().getCorrective());
				model.addAttribute("intentional", assetMeasure.getMeasurePropertyList().getIntentional());
				model.addAttribute("accidental", assetMeasure.getMeasurePropertyList().getAccidental());
				model.addAttribute("environmental", assetMeasure.getMeasurePropertyList().getEnvironmental());
				model.addAttribute("internalThreat", assetMeasure.getMeasurePropertyList().getInternalThreat());
				model.addAttribute("externalThreat", assetMeasure.getMeasurePropertyList().getExternalThreat());
				double typeValue = assetMeasure.getMeasurePropertyList().getPreventive() + assetMeasure.getMeasurePropertyList().getDetective()
						+ assetMeasure.getMeasurePropertyList().getLimitative() + assetMeasure.getMeasurePropertyList().getCorrective();
				model.addAttribute("typeValue", JSTLFunctions.round(typeValue, 1) == 1 ? true : false);
				model.addAttribute("assets", assetMeasure.getMeasureAssetValues());
			}

			Language language = serviceAnalysis.getLanguageOfAnalysis(idAnalysis);
			model.addAttribute("language", language.getAlpha3());
			model.addAttribute("notenoughdata", false);
		} else {
			model.addAttribute("notenoughdata", true);
		}

		return "analyses/singleAnalysis/components/forms/rrf/rrfEditor";
	}

	/***********************
	 * Scenarios
	 **********************/

	/**
	 * loadRRFScenario: <br>
	 * Description
	 * 
	 * @param elementID
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Scenario/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadRRFScenario(@PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Scenario scenario = DAOHibernate.Initialise(serviceScenario.getFromAnalysisById(idAnalysis, elementID));
		for (AssetTypeValue assetTypeValue : scenario.getAssetTypeValues())
			assetTypeValue.setAssetType(DAOHibernate.Initialise(assetTypeValue.getAssetType()));
		model.addAttribute("selectedScenario", scenario);
		double typeValue = scenario.getCorrective() + scenario.getDetective() + scenario.getPreventive() + scenario.getLimitative();
		model.addAttribute("typeValue", JSTLFunctions.round(typeValue, 1) == 1 ? true : false);
		return "analyses/singleAnalysis/components/forms/rrf/scenarioRRF";
	}

	/**
	 * loadRRFScenarioChart: <br>
	 * Description
	 * 
	 * @param filter
	 * @param elementID
	 * @param model
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Scenario/{elementID}/Chart", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID,'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String loadRRFScenarioChart(@RequestBody String requestBody, @PathVariable int elementID, Model model, HttpSession session, Principal principal,
			Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(requestBody);

		// retrieve analysis id to compute
		Integer standardid = jsonNode.get("idStandard") == null ? null : jsonNode.get("idStandard").asInt();
		if (standardid == null)
			return null;

		String chapter = jsonNode.get("chapter") == null ? null : jsonNode.get("chapter").asText();
		if (chapter == null)
			return null;

		Integer measureid = jsonNode.get("idMeasure") == null ? null : jsonNode.get("idMeasure").asInt();

		Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, elementID);
		List<AnalysisStandard> standards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);
		List<Measure> measures = new ArrayList<Measure>();
		for (AnalysisStandard standard : standards)
			if (standard.getStandard().getId() == standardid && standard.getStandard().getType() != StandardType.MATURITY) {
				if (measureid == null && standard.getStandard().getType() == StandardType.ASSET)
					return JsonMessage.Error(messageSource.getMessage("error.rrf.standard.standardtype_invalid", null,
							"This standard type permits only to see RRF by single measure (Select a single measure of this standard)", locale));

				for (Measure measure : standard.getMeasures())
					if (measureid != null) {
						if (measure.getId() == measureid) {
							measures.add(measure);
							break;
						}
					} else {
						if (measure.getMeasureDescription().getReference().startsWith(chapter + ".") && measure.getMeasureDescription().isComputable())
							measures.add(measure);
					}
			}
		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
		return chartGenerator.rrfByScenario(scenario, idAnalysis, measures, customLocale != null ? customLocale : locale);
	}

	/***************
	 * Measures
	 ***************/

	/**
	 * loadRRFMeasure: <br>
	 * Description
	 * 
	 * @param standardID
	 * @param elementID
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Standard/{standardID}/Measure/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadRRFMeasure(@PathVariable int standardID, @PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {

		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);

		if (measure.getAnalysisStandard().getStandard().getId() != standardID || measure.getAnalysisStandard().getStandard().getType() == StandardType.MATURITY)
			return null;

		if (measure instanceof NormalMeasure) {

			NormalMeasure normalMeasure = (NormalMeasure) measure;
			model.addAttribute("strength_measure", normalMeasure.getMeasurePropertyList().getFMeasure());
			model.addAttribute("strength_sectorial", normalMeasure.getMeasurePropertyList().getFSectoral());
			if (serviceAnalysis.isAnalysisCssf(idAnalysis)) {
				model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getAllCategories());
			} else {
				model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getCIACategories());
			}
			model.addAttribute("preventive", normalMeasure.getMeasurePropertyList().getPreventive());
			model.addAttribute("detective", normalMeasure.getMeasurePropertyList().getDetective());
			model.addAttribute("limitative", normalMeasure.getMeasurePropertyList().getLimitative());
			model.addAttribute("corrective", normalMeasure.getMeasurePropertyList().getCorrective());
			model.addAttribute("intentional", normalMeasure.getMeasurePropertyList().getIntentional());
			model.addAttribute("accidental", normalMeasure.getMeasurePropertyList().getAccidental());
			model.addAttribute("environmental", normalMeasure.getMeasurePropertyList().getEnvironmental());
			model.addAttribute("internalThreat", normalMeasure.getMeasurePropertyList().getInternalThreat());
			model.addAttribute("externalThreat", normalMeasure.getMeasurePropertyList().getExternalThreat());
			List<AssetType> assetTypes = serviceAssetType.getAllFromAnalysis(idAnalysis);
			List<AssetTypeValue> assetTypeValues = normalMeasure.getAssetTypeValues();
			int size = assetTypeValues.size();
			for (AssetType assetType : assetTypes) {
				if (normalMeasure.getAssetTypeValueByAssetType(assetType) == null)
					normalMeasure.addAnAssetTypeValue(new AssetTypeValue(assetType, 0));
			}

			if (assetTypeValues.size() != size)
				serviceMeasure.saveOrUpdate(measure);

			model.addAttribute("assetTypes", assetTypeValues);

		} else if (measure instanceof AssetMeasure) {
			AssetMeasure assetMeasure = (AssetMeasure) measure;
			model.addAttribute("standardid", assetMeasure.getAnalysisStandard().getStandard().getId());
			model.addAttribute("measureid", assetMeasure.getId());
			model.addAttribute("strength_measure", assetMeasure.getMeasurePropertyList().getFMeasure());
			model.addAttribute("strength_sectorial", assetMeasure.getMeasurePropertyList().getFSectoral());
			if (serviceAnalysis.isAnalysisCssf(idAnalysis)) {
				model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getAllCategories());
			} else {
				model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getCIACategories());
			}
			model.addAttribute("preventive", assetMeasure.getMeasurePropertyList().getPreventive());
			model.addAttribute("detective", assetMeasure.getMeasurePropertyList().getDetective());
			model.addAttribute("limitative", assetMeasure.getMeasurePropertyList().getLimitative());
			model.addAttribute("corrective", assetMeasure.getMeasurePropertyList().getCorrective());
			model.addAttribute("intentional", assetMeasure.getMeasurePropertyList().getIntentional());
			model.addAttribute("accidental", assetMeasure.getMeasurePropertyList().getAccidental());
			model.addAttribute("environmental", assetMeasure.getMeasurePropertyList().getEnvironmental());
			model.addAttribute("internalThreat", assetMeasure.getMeasurePropertyList().getInternalThreat());
			model.addAttribute("externalThreat", assetMeasure.getMeasurePropertyList().getExternalThreat());
			model.addAttribute("assets", assetMeasure.getMeasureAssetValues());
		}

		Language language = serviceAnalysis.getLanguageOfAnalysis(idAnalysis);
		model.addAttribute("language", language.getAlpha2());

		return "analyses/singleAnalysis/components/forms/rrf/measureRRF";
	}

	/**
	 * loadRRFStandardChart: <br>
	 * Description
	 * 
	 * @param filter
	 * @param elementID
	 * @param model
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Measure/{elementID}/Chart", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String loadRRFStandardChart(@RequestBody String requestbody, @PathVariable int elementID, Model model, HttpSession session, Principal principal,
			Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(requestbody);

		// retrieve analysis id to compute
		Integer scenariotypeid = jsonNode.get("scenariotype").asInt();
		if (scenariotypeid == null)
			return null;
		ScenarioType scenariotype = ScenarioType.valueOf(scenariotypeid);

		Integer scenarioid = null;

		if (jsonNode.get("scenario") != null)
			scenarioid = jsonNode.get("scenario").asInt();

		List<Scenario> scenarios = null;

		if (scenarioid != null) {
			scenarios = new ArrayList<Scenario>();
			scenarios.add(serviceScenario.getFromAnalysisById(idAnalysis, scenarioid));
		} else
			scenarios = serviceScenario.getAllSelectedFromAnalysisByType(idAnalysis, scenariotype);

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
		return chartGenerator.rrfByMeasure(measure, idAnalysis, scenarios, customLocale != null ? customLocale : locale);
	}

	/**
	 * importRRF: <br>
	 * Description
	 * 
	 * @param session
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Import", headers = "Accept=application/json;charset=UTF-8")
	public String importRRF(HttpSession session, Principal principal, Model model) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<Standard> standards = serviceStandard.getAllFromAnalysis(idAnalysis);
		standards.removeIf(standard -> Constant.STANDARD_MATURITY.equalsIgnoreCase(standard.getLabel()));
		List<Analysis> analyses = serviceAnalysis.getAllProfileContainsStandard(standards);
		analyses.addAll(serviceAnalysis.getAllHasRightsAndContainsStandard(principal.getName(), AnalysisRight.highRightFrom(AnalysisRight.MODIFY), standards));
		analyses.removeIf(analysis -> analysis.getId() == idAnalysis);
		Collections.sort(analyses, new AnalysisComparator());
		List<Customer> customers = new ArrayList<Customer>();
		analyses.stream().map(analysis -> analysis.getCustomer()).distinct().forEach(customer -> customers.add(customer));
		model.addAttribute("standards", standards);
		model.addAttribute("customers", customers);
		model.addAttribute("analyses", analyses);
		return "analyses/singleAnalysis/components/forms/importMeasureCharacteristics";

	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Import/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody Object importRRFSave(@ModelAttribute ImportRRFForm rrfForm, HttpSession session, Principal principal, Locale locale) {
		try {
			if (rrfForm.getAnalysis() < 1)
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.no_analysis", null, "No analysis selected", locale));
			else if (rrfForm.getStandards() == null || rrfForm.getStandards().isEmpty())
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.norm", null, "No standard", locale));
			if (!(serviceAnalysis.isProfile(rrfForm.getAnalysis()) || serviceUserAnalysisRight.isUserAuthorized(rrfForm.getAnalysis(), principal.getName(),
					AnalysisRight.highRightFrom(AnalysisRight.MODIFY))))
				return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
			else if (!rrfForm.getStandards().stream().allMatch(idStandard -> serviceStandard.belongToAnalysis(idStandard, rrfForm.getAnalysis())))
				return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
			measureManager.importStandard((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), rrfForm);
			return JsonMessage.Success(messageSource.getMessage("success.import_rrf", null, "Measure characteristics has been successfully imported", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}

	}

}
