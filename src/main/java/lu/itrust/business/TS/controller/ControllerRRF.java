package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.general.AssetTypeValue;
import lu.itrust.business.TS.data.general.Language;
import lu.itrust.business.TS.data.rrf.ImportRRFForm;
import lu.itrust.business.TS.data.scenario.Scenario;
import lu.itrust.business.TS.data.scenario.ScenarioType;
import lu.itrust.business.TS.data.scenario.helper.ScenarioManager;
import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.data.standard.Standard;
import lu.itrust.business.TS.data.standard.StandardType;
import lu.itrust.business.TS.data.standard.measure.AssetMeasure;
import lu.itrust.business.TS.data.standard.measure.Measure;
import lu.itrust.business.TS.data.standard.measure.NormalMeasure;
import lu.itrust.business.TS.data.standard.measure.helper.Chapter;
import lu.itrust.business.TS.data.standard.measure.helper.MeasureManager;
import lu.itrust.business.TS.database.dao.hbm.DAOHibernate;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceStandard;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Hibernate;
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public String rrf(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
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
				Double typeValue =
					normalMeasure.getMeasurePropertyList().getPreventive() + normalMeasure.getMeasurePropertyList().getDetective() + normalMeasure.getMeasurePropertyList().getLimitative()
						+ normalMeasure.getMeasurePropertyList().getCorrective();
				model.addAttribute("typeValue", round(typeValue, 1) == 1 ? true : false);
				model.addAttribute("assetTypes", normalMeasure.getAssetTypeValues());
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
				double typeValue =
					assetMeasure.getMeasurePropertyList().getPreventive() + assetMeasure.getMeasurePropertyList().getDetective() + assetMeasure.getMeasurePropertyList().getLimitative()
						+ assetMeasure.getMeasurePropertyList().getCorrective();
				model.addAttribute("typeValue", round(typeValue, 1) == 1 ? true : false);
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

	private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public String loadRRFScenario(@PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		Scenario scenario = DAOHibernate.Initialise(serviceScenario.getFromAnalysisById(idAnalysis, elementID));
		scenario.setScenarioType(DAOHibernate.Initialise(scenario.getScenarioType()));
		for (AssetTypeValue assetTypeValue : scenario.getAssetTypeValues())
			assetTypeValue.setAssetType(DAOHibernate.Initialise(assetTypeValue.getAssetType()));
		model.addAttribute("selectedScenario", scenario);
		double typeValue = scenario.getCorrective() + scenario.getDetective() + scenario.getPreventive() + scenario.getLimitative();
		model.addAttribute("typeValue", round(typeValue, 1) == 1 ? true : false);
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID,'Scenario', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String loadRRFScenarioChart(@RequestBody String requestBody, @PathVariable int elementID, Model model, HttpSession session, Principal principal, Locale locale)
			throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

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
		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Measure', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public String loadRRFMeasure(@PathVariable int standardID, @PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {

		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

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
			double typeValue =
				normalMeasure.getMeasurePropertyList().getPreventive() + normalMeasure.getMeasurePropertyList().getDetective() + normalMeasure.getMeasurePropertyList().getLimitative()
					+ normalMeasure.getMeasurePropertyList().getCorrective();
			model.addAttribute("typeValue", round(typeValue, 1) == 1 ? true : false);
			model.addAttribute("intentional", normalMeasure.getMeasurePropertyList().getIntentional());
			model.addAttribute("accidental", normalMeasure.getMeasurePropertyList().getAccidental());
			model.addAttribute("environmental", normalMeasure.getMeasurePropertyList().getEnvironmental());
			model.addAttribute("internalThreat", normalMeasure.getMeasurePropertyList().getInternalThreat());
			model.addAttribute("externalThreat", normalMeasure.getMeasurePropertyList().getExternalThreat());
			model.addAttribute("assetTypes", normalMeasure.getAssetTypeValues());

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
			double typeValue =
				assetMeasure.getMeasurePropertyList().getPreventive() + assetMeasure.getMeasurePropertyList().getDetective() + assetMeasure.getMeasurePropertyList().getLimitative()
					+ assetMeasure.getMeasurePropertyList().getCorrective();
			model.addAttribute("typeValue", round(typeValue, 1) == 1 ? true : false);
			model.addAttribute("intentional", assetMeasure.getMeasurePropertyList().getIntentional());
			model.addAttribute("accidental", assetMeasure.getMeasurePropertyList().getAccidental());
			model.addAttribute("environmental", assetMeasure.getMeasurePropertyList().getEnvironmental());
			model.addAttribute("internalThreat", assetMeasure.getMeasurePropertyList().getInternalThreat());
			model.addAttribute("externalThreat", assetMeasure.getMeasurePropertyList().getExternalThreat());
			model.addAttribute("assets", assetMeasure.getMeasureAssetValues());
		}

		Language language = serviceAnalysis.getLanguageOfAnalysis(idAnalysis);
		model.addAttribute("language", language.getAlpha3());

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Measure', #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String loadRRFStandardChart(@RequestBody String requestbody, @PathVariable int elementID, Model model, HttpSession session, Principal principal, Locale locale)
			throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
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

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Import", headers = "Accept=application/json;charset=UTF-8")
	public String importRRF(HttpSession session, Principal principal, Model model) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<Standard> standards = serviceStandard.getAllFromAnalysis(idAnalysis);
		List<Integer> idStandards = new ArrayList<Integer>(standards.size());
		for (Standard standard : standards) {
			if (!Constant.STANDARD_MATURITY.equalsIgnoreCase(standard.getLabel()))
				idStandards.add(standard.getId());
		}
		List<Analysis> profiles = serviceAnalysis.getAllProfileContainsStandard(standards);
		model.addAttribute("idStandards", idStandards);
		model.addAttribute("profiles", profiles);
		return "analyses/singleAnalysis/components/forms/importMeasureCharacteristics";

	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.data.analysis.rights.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Import/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody Object importRRFSave(@ModelAttribute ImportRRFForm rrfForm, HttpSession session, Principal principal, Locale locale) {
		try {
			if (rrfForm.getProfile() < 1)
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.no_profile", null, "No profile", locale));
			else if (rrfForm.getStandards() == null || rrfForm.getStandards().isEmpty())
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.norm", null, "No standard", locale));
			measureManager.importStandard((Integer) session.getAttribute("selectedAnalysis"), rrfForm);

			return JsonMessage.Success(messageSource.getMessage("success.import_rrf", null, "Measure characteristics has been successfully imported", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		}

	}
	
}
