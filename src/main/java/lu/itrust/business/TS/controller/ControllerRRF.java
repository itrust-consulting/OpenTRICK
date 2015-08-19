package lu.itrust.business.TS.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.JSTLFunctions;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.hbm.DAOHibernate;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.rrf.ImportRRFForm;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.model.scenario.helper.ScenarioManager;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.AssetStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;
import lu.itrust.business.TS.model.standard.measure.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;
import lu.itrust.business.TS.model.standard.measure.helper.Chapter;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureManager;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

	private static final String F_SECTORIAL = "FSectorial";

	private static final String F_MEASURE = "FMeasure";

	private static final String REFERENCE = "Reference";

	private static final String VERSION = "version";

	private static final String IDENTIFIER = "identifier";

	private static final String EXTERNAL_THREAT = "externalThreat";

	private static final String INTERNAL_THREAT = "internalThreat";

	private static final String ENVIRONMENTAL = "environmental";

	private static final String ACCIDENTAL = "accidental";

	private static final String INTENTIONAL = "intentional";

	private static final String CORRECTIVE = "corrective";

	private static final String LIMITATIVE = "limitative";

	private static final String NAME = "name";

	private static final String DETECTIVE = "detective";

	private static final String PREVENTIVE = "preventive";

	private static final int SCENARIO_RRF_DEFAULT_FIELD_COUNT = 20;

	private static final int MEASURE_RRF_DEFAULT_FIELD_COUNT = 12;

	private static final String TS_INFO_FOR_IMPORT = "^!TS-InfO_fOr-ImpOrt!^";

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

			Measure measure = splittedmeasures.entrySet().iterator().next().getValue().get(0);

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
				model.addAttribute(PREVENTIVE, normalMeasure.getMeasurePropertyList().getPreventive());
				model.addAttribute(DETECTIVE, normalMeasure.getMeasurePropertyList().getDetective());
				model.addAttribute(LIMITATIVE, normalMeasure.getMeasurePropertyList().getLimitative());
				model.addAttribute(CORRECTIVE, normalMeasure.getMeasurePropertyList().getCorrective());
				model.addAttribute(INTENTIONAL, normalMeasure.getMeasurePropertyList().getIntentional());
				model.addAttribute(ACCIDENTAL, normalMeasure.getMeasurePropertyList().getAccidental());
				model.addAttribute(ENVIRONMENTAL, normalMeasure.getMeasurePropertyList().getEnvironmental());
				model.addAttribute(INTERNAL_THREAT, normalMeasure.getMeasurePropertyList().getInternalThreat());
				model.addAttribute(EXTERNAL_THREAT, normalMeasure.getMeasurePropertyList().getExternalThreat());
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
				model.addAttribute(PREVENTIVE, assetMeasure.getMeasurePropertyList().getPreventive());
				model.addAttribute(DETECTIVE, assetMeasure.getMeasurePropertyList().getDetective());
				model.addAttribute(LIMITATIVE, assetMeasure.getMeasurePropertyList().getLimitative());
				model.addAttribute(CORRECTIVE, assetMeasure.getMeasurePropertyList().getCorrective());
				model.addAttribute(INTENTIONAL, assetMeasure.getMeasurePropertyList().getIntentional());
				model.addAttribute(ACCIDENTAL, assetMeasure.getMeasurePropertyList().getAccidental());
				model.addAttribute(ENVIRONMENTAL, assetMeasure.getMeasurePropertyList().getEnvironmental());
				model.addAttribute(INTERNAL_THREAT, assetMeasure.getMeasurePropertyList().getInternalThreat());
				model.addAttribute(EXTERNAL_THREAT, assetMeasure.getMeasurePropertyList().getExternalThreat());
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

		return "analyses/single/components/rrf/editor/home";
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
		return "analyses/single/components/rrf/editor/scenario";
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
			model.addAttribute(PREVENTIVE, normalMeasure.getMeasurePropertyList().getPreventive());
			model.addAttribute(DETECTIVE, normalMeasure.getMeasurePropertyList().getDetective());
			model.addAttribute(LIMITATIVE, normalMeasure.getMeasurePropertyList().getLimitative());
			model.addAttribute(CORRECTIVE, normalMeasure.getMeasurePropertyList().getCorrective());
			model.addAttribute(INTENTIONAL, normalMeasure.getMeasurePropertyList().getIntentional());
			model.addAttribute(ACCIDENTAL, normalMeasure.getMeasurePropertyList().getAccidental());
			model.addAttribute(ENVIRONMENTAL, normalMeasure.getMeasurePropertyList().getEnvironmental());
			model.addAttribute(INTERNAL_THREAT, normalMeasure.getMeasurePropertyList().getInternalThreat());
			model.addAttribute(EXTERNAL_THREAT, normalMeasure.getMeasurePropertyList().getExternalThreat());
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
			model.addAttribute(PREVENTIVE, assetMeasure.getMeasurePropertyList().getPreventive());
			model.addAttribute(DETECTIVE, assetMeasure.getMeasurePropertyList().getDetective());
			model.addAttribute(LIMITATIVE, assetMeasure.getMeasurePropertyList().getLimitative());
			model.addAttribute(CORRECTIVE, assetMeasure.getMeasurePropertyList().getCorrective());
			model.addAttribute(INTENTIONAL, assetMeasure.getMeasurePropertyList().getIntentional());
			model.addAttribute(ACCIDENTAL, assetMeasure.getMeasurePropertyList().getAccidental());
			model.addAttribute(ENVIRONMENTAL, assetMeasure.getMeasurePropertyList().getEnvironmental());
			model.addAttribute(INTERNAL_THREAT, assetMeasure.getMeasurePropertyList().getInternalThreat());
			model.addAttribute(EXTERNAL_THREAT, assetMeasure.getMeasurePropertyList().getExternalThreat());
			model.addAttribute("assets", assetMeasure.getMeasureAssetValues());
		}

		Language language = serviceAnalysis.getLanguageOfAnalysis(idAnalysis);
		model.addAttribute("language", language.getAlpha2());

		return "analyses/single/components/rrf/editor/measure";
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

	@RequestMapping(value = "/Measure/{idMeasure}/Update-child", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateChildRRF(@PathVariable int idMeasure, @RequestBody LinkedList<Integer> idMeasureChilds, HttpSession session, Principal principal,
			Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		String language = (String) session.getAttribute(Constant.SELECTED_ANALYSIS_LANGUAGE);
		if (language != null)
			locale = new Locale(language);
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, idMeasure);
		List<Measure> measures = new ArrayList<Measure>(idMeasureChilds.size());
		for (Integer idChild : idMeasureChilds) {
			Measure child = serviceMeasure.getFromAnalysisById(idAnalysis, idChild);
			if (child == null)
				return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
			else {
				measures.add(measure);
				if (measure instanceof NormalMeasure) {
					NormalMeasure parentMeasure = (NormalMeasure) measure, childMeasure = (NormalMeasure) child;
					parentMeasure.getMeasurePropertyList().copyTo(childMeasure.getMeasurePropertyList());
					Map<AssetType, AssetTypeValue> assetTypeValues = new LinkedHashMap<AssetType, AssetTypeValue>(childMeasure.getAssetTypeValues().size());
					childMeasure.getAssetTypeValues().forEach(assetValue -> assetTypeValues.put(assetValue.getAssetType(), assetValue));
					for (AssetTypeValue assetTypeValue : parentMeasure.getAssetTypeValues()) {
						AssetTypeValue typeValue = assetTypeValues.get(assetTypeValue.getAssetType());
						if (typeValue == null)
							childMeasure.addAnAssetTypeValue(new AssetTypeValue(assetTypeValue.getAssetType(), assetTypeValue.getValue()));
						else
							typeValue.setValue(assetTypeValue.getValue());
					}
				} else if (measure instanceof AssetMeasure) {
					AssetMeasure parentMeasure = (AssetMeasure) measure, childMeasure = (AssetMeasure) child;
					parentMeasure.getMeasurePropertyList().copyTo(childMeasure.getMeasurePropertyList());
					Map<Asset, MeasureAssetValue> measureAssetValues = new LinkedHashMap<Asset, MeasureAssetValue>(childMeasure.getMeasureAssetValues().size());
					childMeasure.getMeasureAssetValues().forEach(assetValue -> measureAssetValues.put(assetValue.getAsset(), assetValue));
					for (MeasureAssetValue assetValue : parentMeasure.getMeasureAssetValues()) {
						MeasureAssetValue measureAssetValue = measureAssetValues.get(assetValue.getAsset());
						if (measureAssetValue == null)
							childMeasure.addAnMeasureAssetValue(new MeasureAssetValue(assetValue.getAsset(), assetValue.getValue()));
						else
							measureAssetValue.setValue(assetValue.getValue());
					}
				} else
					continue;
				serviceMeasure.saveOrUpdate(child);
			}
		}
		return JsonMessage.Success(messageSource.getMessage("success.import_rrf", null, "Measure characteristics has been successfully imported", locale));

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
		return "analyses/single/components/rrf/form/importMeasure";

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

	@RequestMapping(value = "/Export/Raw/{idAnalysis}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportRawRFF(@PathVariable int idAnalysis, Model model, HttpServletResponse response, Principal principal) throws Exception {
		exportRawRRF(serviceAnalysis.get(idAnalysis), response, principal.getName());
	}

	private void exportRawRRF(Analysis analysis, HttpServletResponse response, String username) throws Exception {
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook();
			Locale locale = new Locale(analysis.getLanguage().getAlpha2());
			List<AssetType> assetTypes = serviceAssetType.getAll();
			writeAnalysisIdentifier(analysis, workbook);
			writeScenario(analysis.getScenarios(), assetTypes, workbook, locale);
			for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards())
				writeMeasure(analysis.isCssf(), analysisStandard, assetTypes, workbook, locale);
			response.setContentType("xlsx");
			// set response header with location of the filename
			response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("RAW RRF %s_V%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");
			workbook.write(response.getOutputStream());
			// Log
			TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.raw.action_plan",
					String.format("Analysis: %s, version: %s, type: Raw action plan", analysis.getIdentifier(), analysis.getVersion()), username, LogAction.EXPORT,
					analysis.getIdentifier(), analysis.getVersion());
		} finally {
			try {
				if (workbook != null)
					workbook.close();
			} catch (IOException e) {
				System.err.println("Close document: " + e.getMessage());
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void writeAssetMeasure(boolean cssf, AnalysisStandard analysisStandard, XSSFWorkbook workbook, Locale locale) {
		XSSFSheet sheet = workbook.createSheet(analysisStandard.getStandard().getLabel());
		List<AssetMeasure> measures = (List<AssetMeasure>) analysisStandard.getExendedMeasures();
		List<Asset> assets = measures.stream().map(measure -> measure.getMeasureAssetValues()).flatMap(assetValues -> assetValues.stream())
				.map(assetValue -> assetValue.getAsset()).distinct().collect(Collectors.toList());
		Map<String, Integer> mappedValue = new LinkedHashMap<String, Integer>();
		String[] categories = cssf ? CategoryConverter.JAVAKEYS : CategoryConverter.TYPE_CIA_KEYS;
		int totalCol = MEASURE_RRF_DEFAULT_FIELD_COUNT + assets.size() + categories.length, rowIndex = 0;
		XSSFRow row = sheet.getRow(0);
		if (row == null)
			row = sheet.createRow(0);
		int colIndex = generateMeasureHeader(row, mappedValue, categories, totalCol);
		for (Asset asset : assets)
			row.getCell(++colIndex).setCellValue(asset.getName());
		measures.stream().forEach(
				measure -> measure.getMeasureAssetValues().forEach(assetValue -> mappedValue.put(measure.getId() + "_" + assetValue.getAsset().getId(), assetValue.getValue())));
		for (AssetMeasure measure : measures) {
			row = sheet.getRow(++rowIndex);
			if (row == null)
				row = sheet.createRow(rowIndex);
			colIndex = writingMeasureData(row, totalCol, categories, mappedValue, measure.getMeasureDescription().getReference(), measure.getMeasurePropertyList());
			for (Asset asset : assets)
				row.getCell(++colIndex).setCellValue(mappedValue.getOrDefault(measure.getId() + "_" + asset.getId(), 0));
		}
	}

	@SuppressWarnings("unchecked")
	private void writeNormalMeasure(boolean cssf, AnalysisStandard analysisStandard, List<AssetType> assetTypes, XSSFWorkbook workbook, Locale locale) {
		XSSFSheet sheet = workbook.createSheet(analysisStandard.getStandard().getLabel());
		List<NormalMeasure> measures = (List<NormalMeasure>) analysisStandard.getExendedMeasures();
		XSSFRow row = sheet.getRow(0);
		if (row == null)
			row = sheet.createRow(0);
		Map<String, Integer> mappedValue = new LinkedHashMap<String, Integer>();
		String[] categories = cssf ? CategoryConverter.JAVAKEYS : CategoryConverter.TYPE_CIA_KEYS;
		int totalCol = MEASURE_RRF_DEFAULT_FIELD_COUNT + assetTypes.size() + categories.length, rowIndex = 0;
		int colIndex = generateMeasureHeader(row, mappedValue, categories, totalCol);
		for (AssetType assetType : assetTypes)
			row.getCell(++colIndex).setCellValue(assetType.getType());
		measures.stream().forEach(
				measure -> measure.getAssetTypeValues().forEach(
						assetypeValue -> mappedValue.put(measure.getId() + "_" + assetypeValue.getAssetType().getType(), assetypeValue.getValue())));
		for (NormalMeasure measure : measures) {
			row = sheet.getRow(++rowIndex);
			if (row == null)
				row = sheet.createRow(rowIndex);
			colIndex = writingMeasureData(row, totalCol, categories, mappedValue, measure.getMeasureDescription().getReference(), measure.getMeasurePropertyList());
			for (AssetType assetType : assetTypes)
				row.getCell(++colIndex).setCellValue(mappedValue.getOrDefault(measure.getId() + "_" + assetType.getType(), 0));

		}
	}

	private int generateMeasureHeader(XSSFRow row, Map<String, Integer> mappedValue, String[] categories, int totalCol) {
		for (int i = 0; i < totalCol; i++) {
			if (row.getCell(i) == null)
				row.createCell(i);
		}
		int colIndex = 0;
		row.getCell(colIndex).setCellValue(REFERENCE);
		row.getCell(++colIndex).setCellValue(F_MEASURE);
		row.getCell(++colIndex).setCellValue(F_SECTORIAL);
		row.getCell(++colIndex).setCellValue(PREVENTIVE);
		row.getCell(++colIndex).setCellValue(DETECTIVE);
		row.getCell(++colIndex).setCellValue(LIMITATIVE);
		row.getCell(++colIndex).setCellValue(CORRECTIVE);
		row.getCell(++colIndex).setCellValue(INTENTIONAL);
		row.getCell(++colIndex).setCellValue(ACCIDENTAL);
		row.getCell(++colIndex).setCellValue(ENVIRONMENTAL);
		row.getCell(++colIndex).setCellValue(INTERNAL_THREAT);
		row.getCell(++colIndex).setCellValue(EXTERNAL_THREAT);
		for (String category : categories)
			row.getCell(++colIndex).setCellValue(category);
		return colIndex;
	}

	private int writingMeasureData(XSSFRow row, int totalCol, String[] categories, Map<String, Integer> mappedValue, String reference, MeasureProperties properties) {
		for (int i = 0; i < totalCol; i++) {
			XSSFCell cell = row.getCell(i);
			if (cell == null)
				row.createCell(i, i < 1 ? Cell.CELL_TYPE_STRING : Cell.CELL_TYPE_NUMERIC);
		}
		int colIndex = 0;
		row.getCell(colIndex).setCellValue(reference);
		row.getCell(++colIndex).setCellValue(properties.getFMeasure());
		row.getCell(++colIndex).setCellValue(properties.getFSectoral());
		row.getCell(++colIndex).setCellValue(properties.getPreventive());
		row.getCell(++colIndex).setCellValue(properties.getDetective());
		row.getCell(++colIndex).setCellValue(properties.getLimitative());
		row.getCell(++colIndex).setCellValue(properties.getCorrective());
		row.getCell(++colIndex).setCellValue(properties.getIntentional());
		row.getCell(++colIndex).setCellValue(properties.getAccidental());
		row.getCell(++colIndex).setCellValue(properties.getEnvironmental());
		row.getCell(++colIndex).setCellValue(properties.getInternalThreat());
		row.getCell(++colIndex).setCellValue(properties.getExternalThreat());
		for (String category : categories)
			row.getCell(++colIndex).setCellValue(properties.getCategoryValue(category));
		return colIndex;
	}

	private void writeMeasure(boolean isCSSF, AnalysisStandard analysisStandard, List<AssetType> assetTypes, XSSFWorkbook workbook, Locale locale) {
		switch (analysisStandard.getStandard().getType()) {
		case ASSET:
			writeAssetMeasure(isCSSF, analysisStandard, workbook, locale);
			break;
		case NORMAL:
			writeNormalMeasure(isCSSF, analysisStandard, assetTypes, workbook, locale);
			break;
		default:
			break;
		}
	}

	private void writeScenario(List<Scenario> scenarios, List<AssetType> assetTypes, XSSFWorkbook workbook, Locale locale) {
		if (scenarios.isEmpty())
			return;
		XSSFSheet scenarioSheet = workbook.createSheet(messageSource.getMessage("label.scenario", null, "Scenario", locale));
		int colIndex = 0, rowIndex = 0;
		XSSFRow row = scenarioSheet.getRow(rowIndex);
		if (row == null)
			row = scenarioSheet.createRow(rowIndex);
		for (int i = 0; i < SCENARIO_RRF_DEFAULT_FIELD_COUNT; i++) {
			XSSFCell cell = row.getCell(i);
			if (cell == null)
				row.createCell(i);
		}
		row.getCell(colIndex).setCellValue(NAME);
		row.getCell(++colIndex).setCellValue(PREVENTIVE);
		row.getCell(++colIndex).setCellValue(DETECTIVE);
		row.getCell(++colIndex).setCellValue(LIMITATIVE);
		row.getCell(++colIndex).setCellValue(CORRECTIVE);
		row.getCell(++colIndex).setCellValue(INTENTIONAL);
		row.getCell(++colIndex).setCellValue(ACCIDENTAL);
		row.getCell(++colIndex).setCellValue(ENVIRONMENTAL);
		row.getCell(++colIndex).setCellValue(INTERNAL_THREAT);
		row.getCell(++colIndex).setCellValue(EXTERNAL_THREAT);

		for (AssetType assetType : assetTypes)
			row.getCell(++colIndex).setCellValue(assetType.getType());
		Map<String, Integer> mappedValue = new LinkedHashMap<String, Integer>();
		scenarios.stream().forEach(
				scenario -> scenario.getAssetTypeValues().forEach(
						assetypeValue -> mappedValue.put(scenario.getId() + "_" + assetypeValue.getAssetType().getType(), assetypeValue.getValue())));
		for (Scenario scenario : scenarios) {
			row = scenarioSheet.getRow(++rowIndex);
			if (row == null)
				row = scenarioSheet.createRow(rowIndex);
			for (int i = 0; i < SCENARIO_RRF_DEFAULT_FIELD_COUNT; i++) {
				XSSFCell cell = row.getCell(i);
				if (cell == null)
					row.createCell(i, i < 1 ? Cell.CELL_TYPE_STRING : Cell.CELL_TYPE_NUMERIC);
			}
			colIndex = 0;
			row.getCell(colIndex).setCellValue(scenario.getName());
			row.getCell(++colIndex).setCellValue(scenario.getPreventive());
			row.getCell(++colIndex).setCellValue(scenario.getDetective());
			row.getCell(++colIndex).setCellValue(scenario.getLimitative());
			row.getCell(++colIndex).setCellValue(scenario.getCorrective());
			row.getCell(++colIndex).setCellValue(scenario.getIntentional());
			row.getCell(++colIndex).setCellValue(scenario.getAccidental());
			row.getCell(++colIndex).setCellValue(scenario.getEnvironmental());
			row.getCell(++colIndex).setCellValue(scenario.getInternalThreat());
			row.getCell(++colIndex).setCellValue(scenario.getExternalThreat());
			for (AssetType assetType : assetTypes)
				row.getCell(++colIndex).setCellValue(mappedValue.getOrDefault(scenario.getId() + "_" + assetType.getType(), 0));
		}
	}

	private void writeAnalysisIdentifier(Analysis analysis, XSSFWorkbook workbook) {
		XSSFSheet analysisSheet = workbook.createSheet(TS_INFO_FOR_IMPORT);
		XSSFRow header = analysisSheet.getRow(0), data = analysisSheet.getRow(1);
		if (header == null)
			header = analysisSheet.createRow(0);
		if (data == null)
			data = analysisSheet.createRow(1);
		for (int i = 0; i < 2; i++) {
			if (header.getCell(i) == null)
				header.createCell(i);
			if (data.getCell(i) == null)
				data.createCell(i);
		}
		header.getCell(0).setCellValue(IDENTIFIER);
		data.getCell(0).setCellValue(analysis.getIdentifier());
		header.getCell(1).setCellValue(VERSION);
		data.getCell(1).setCellValue(analysis.getVersion());
		workbook.setSheetHidden(workbook.getSheetIndex(TS_INFO_FOR_IMPORT), XSSFWorkbook.SHEET_STATE_VERY_HIDDEN);
	}

	@RequestMapping(value = "/Form/Import/Raw/{idAnalysis}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String formImportRRF(@PathVariable int idAnalysis, Model model, Principal principal) {
		model.addAttribute("idAnalysis", idAnalysis);
		return "analyses/single/components/rrf/form/importRaw";
	}

	@RequestMapping(value = "/Import/Raw/{idAnalysis}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object importRRF(@PathVariable int idAnalysis, @RequestParam(value = "file") MultipartFile file, Principal principal, HttpServletRequest request,
			Locale locale) throws Exception {
		return importRawRRF(request.getServletContext().getRealPath("/WEB-INF/tmp/"), idAnalysis, file, principal.getName(), locale);
	}

	private Object importRawRRF(String tempPath, int idAnalysis, MultipartFile file, String username, Locale locale) throws Exception {
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			locale = new Locale(analysis.getLanguage().getAlpha2());
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			loadAnalysisInfo(analysis, workbook);
			loadScenarios(analysis.getScenarios(), workbook);
			loadStandards(analysis.getAnalysisStandards(), workbook);
			serviceAnalysis.saveOrUpdate(analysis);
			return JsonMessage.Success(messageSource.getMessage("success.import.raw.rrf", null, "RRF was been successfully update from raw data", locale));
		} catch (TrickException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
	}

	private void loadStandards(List<AnalysisStandard> analysisStandards, XSSFWorkbook workbook) {
		for (AnalysisStandard analysisStandard : analysisStandards) {
			if (analysisStandard instanceof MaturityStandard)
				continue;
			XSSFSheet sheet = workbook.getSheet(analysisStandard.getStandard().getLabel());
			if (sheet == null)
				continue;
			if (analysisStandard instanceof AssetStandard)
				loadStandard((AssetStandard) analysisStandard, sheet);
			else if (analysisStandard instanceof NormalStandard)
				loadStandard((NormalStandard) analysisStandard, sheet);
		}
	}

	private void loadStandard(AssetStandard analysisStandard, XSSFSheet sheet) {
		XSSFRow header = sheet.getRow(0);
		if (header == null)
			throw new TrickException("error.import.raw.rrf.standard", "Standard cannot be loaded");
		Map<Integer, String> cellIndexToFieldName = new LinkedHashMap<Integer, String>();
		Integer referenceIndex = mappingColumns(header, REFERENCE, cellIndexToFieldName);
		if (referenceIndex == null)
			throw new TrickException("error.import.raw.rrf.standard.reference", "Standard reference column cannot be found");
		Map<String, AssetMeasure> mappingMeasures = analysisStandard.getMeasures().stream()
				.collect(Collectors.toMap(measure -> measure.getMeasureDescription().getReference(), measure -> (AssetMeasure) measure));
		for (Row data : sheet) {
			if (data.getRowNum() == 0)
				continue;
			Cell cell = data.getCell(referenceIndex);
			if (cell == null)
				continue;
			AssetMeasure measure = mappingMeasures.get(cell.getStringCellValue());
			if (measure == null)
				continue;
			loadMeasureData(measure, data, referenceIndex, cellIndexToFieldName);
		}
	}

	private void loadStandard(NormalStandard analysisStandard, XSSFSheet sheet) {
		XSSFRow header = sheet.getRow(0);
		if (header == null)
			throw new TrickException("error.import.raw.rrf.standard", "Standard cannot be loaded");
		Map<Integer, String> cellIndexToFieldName = new LinkedHashMap<Integer, String>();
		Integer referenceIndex = mappingColumns(header, REFERENCE, cellIndexToFieldName);
		if (referenceIndex == null)
			throw new TrickException("error.import.raw.rrf.standard.reference", "Standard reference column cannot be found");
		Map<String, NormalMeasure> mappingMeasures = analysisStandard.getMeasures().stream()
				.collect(Collectors.toMap(measure -> measure.getMeasureDescription().getReference(), measure -> (NormalMeasure) measure));
		for (Row data : sheet) {
			if (data.getRowNum() == 0)
				continue;
			Cell cell = data.getCell(referenceIndex);
			if (cell == null)
				continue;
			NormalMeasure measure = mappingMeasures.get(cell.getStringCellValue());
			if (measure == null)
				continue;
			loadMeasureData(measure, data, referenceIndex, cellIndexToFieldName);
		}
	}

	private void loadMeasureData(NormalMeasure measure, Row data, Integer referenceIndex, Map<Integer, String> cellIndexToFieldName) {
		Map<String, AssetTypeValue> assetValues = measure.getAssetTypeValues().stream()
				.collect(Collectors.toMap(assetValue -> assetValue.getAssetType().getType(), Function.identity()));
		MeasureProperties properties = measure.getMeasurePropertyList();
		for (Cell cell : data) {
			if (cell.getColumnIndex() == referenceIndex)
				continue;
			String nameField = cellIndexToFieldName.get(cell.getColumnIndex());
			if (nameField == null)
				continue;
			double value = cell.getNumericCellValue();
			if (assetValues.containsKey(nameField))
				assetValues.get(nameField).setValue((int) value);
			else
				updateMeasureProperties(properties, nameField, value);
		}
		measure.setMeasurePropertyList(properties);
	}

	private void loadMeasureData(AssetMeasure measure, Row data, Integer referenceIndex, Map<Integer, String> cellIndexToFieldName) {
		Map<String, MeasureAssetValue> assetValues = measure.getMeasureAssetValues().stream()
				.collect(Collectors.toMap(assetValue -> assetValue.getAsset().getName(), Function.identity()));
		MeasureProperties properties = measure.getMeasurePropertyList();
		for (Cell cell : data) {
			if (cell.getColumnIndex() == referenceIndex)
				continue;
			String nameField = cellIndexToFieldName.get(cell.getColumnIndex());
			if (nameField == null)
				continue;
			double value = cell.getNumericCellValue();
			if (assetValues.containsKey(nameField))
				assetValues.get(nameField).setValue((int) value);
			else
				updateMeasureProperties(properties, nameField, value);
		}
		measure.setMeasurePropertyList(properties);
	}

	private void updateMeasureProperties(MeasureProperties properties, String nameField, double value) {

		if (MeasureProperties.isCategoryKey(nameField))
			properties.setCategoryValue(nameField, (int) value);
		else {
			switch (nameField) {
			case F_MEASURE:
				properties.setFMeasure((int) value);
				break;
			case F_SECTORIAL:
				properties.setFSectoral((int) value);
				break;
			case EXTERNAL_THREAT:
				properties.setExternalThreat((int) value);
				break;
			case INTERNAL_THREAT:
				properties.setInternalThreat((int) value);
				break;
			case ENVIRONMENTAL:
				properties.setEnvironmental((int) value);
				break;
			case ACCIDENTAL:
				properties.setAccidental((int) value);
				break;
			case INTENTIONAL:
				properties.setIntentional((int) value);
				break;
			case CORRECTIVE:
				properties.setCorrective(value);
				break;
			case LIMITATIVE:
				properties.setLimitative(value);
				break;
			case DETECTIVE:
				properties.setDetective(value);
				break;
			case PREVENTIVE:
				properties.setPreventive(value);
				break;
			}
		}
	}

	private void loadScenarios(List<Scenario> scenarios, XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.getSheet("scenario");
		if (sheet == null || scenarios.isEmpty())
			return;
		XSSFRow header = sheet.getRow(0);
		if (header == null)
			throw new TrickException("error.import.raw.rrf.scenario", "Scenario cannot be loaded");
		Map<Integer, String> cellIndexToFieldName = new LinkedHashMap<Integer, String>();
		Integer nameIndex = mappingColumns(header, NAME, cellIndexToFieldName);
		if (nameIndex == null)
			throw new TrickException("error.import.raw.rrf.scenario.name", "Scenario name column cannot be found");
		Map<String, Scenario> scenarioMappings = scenarios.stream().collect(Collectors.toMap(Scenario::getName, Function.identity()));
		for (Row data : sheet) {
			if (data.getRowNum() == 0)
				continue;
			Cell cell = data.getCell(nameIndex);
			if (cell == null)
				continue;
			Scenario scenario = scenarioMappings.get(cell.getStringCellValue());
			if (scenario == null)
				continue;
			loadScenarioData(scenario, data, nameIndex, cellIndexToFieldName);
		}
	}

	private Integer mappingColumns(XSSFRow header, String identifier, Map<Integer, String> cellIndexToFieldName) {
		Integer identifierIndex = null;
		for (Cell cell : header) {
			if (identifier.equalsIgnoreCase(cell.getStringCellValue()))
				identifierIndex = cell.getColumnIndex();
			else
				cellIndexToFieldName.put(cell.getColumnIndex(), cell.getStringCellValue());
		}
		return identifierIndex;
	}

	private void loadScenarioData(Scenario scenario, Row data, Integer nameIndex, Map<Integer, String> cellIndexToFieldName) {
		Map<String, AssetTypeValue> assetTypeValues = scenario.getAssetTypeValues().stream()
				.collect(Collectors.toMap(assetTypeValue -> assetTypeValue.getAssetType().getType(), Function.identity()));
		for (Cell cell : data) {
			if (cell.getColumnIndex() == nameIndex)
				continue;
			String nameField = cellIndexToFieldName.get(cell.getColumnIndex());
			if (nameField == null)
				continue;
			double value = cell.getNumericCellValue();
			if (assetTypeValues.containsKey(nameField))
				assetTypeValues.get(nameField).setValue((int) value);
			else {
				switch (nameField) {
				case EXTERNAL_THREAT:
					scenario.setExternalThreat((int) value);
					break;
				case INTERNAL_THREAT:
					scenario.setInternalThreat((int) value);
					break;
				case ENVIRONMENTAL:
					scenario.setEnvironmental((int) value);
					break;
				case ACCIDENTAL:
					scenario.setAccidental((int) value);
					break;
				case INTENTIONAL:
					scenario.setIntentional((int) value);
					break;
				case CORRECTIVE:
					scenario.setCorrective(value);
					break;
				case LIMITATIVE:
					scenario.setLimitative(value);
					break;
				case DETECTIVE:
					scenario.setDetective(value);
					break;
				case PREVENTIVE:
					scenario.setPreventive(value);
					break;
				}
			}
		}
	}

	private void loadAnalysisInfo(Analysis analysis, XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.getSheet(TS_INFO_FOR_IMPORT);
		if (sheet == null)
			throw new TrickException("error.import.raw.rrf.analysis.info", "Analysis information cannot be loaded");
		String identifier = null, version = null;
		XSSFRow row = sheet.getRow(0), data = sheet.getRow(1);
		if (row == null || data == null)
			throw new TrickException("error.import.raw.rrf.analysis.info", "Analysis information cannot be loaded");
		for (Cell cell : row) {
			XSSFCell cellData = data.getCell(cell.getColumnIndex());
			if (cellData == null)
				break;
			switch (cell.getStringCellValue()) {
			case IDENTIFIER:
				identifier = cellData.getStringCellValue();
				break;
			case VERSION:
				version = cellData.getStringCellValue();
				break;
			}
			if (!(identifier == null || version == null))
				break;
		}
		if (identifier == null || version == null)
			throw new TrickException("error.import.raw.rrf.analysis.info", "Analysis information cannot be loaded");
		else if (!(analysis.getIdentifier().equals(identifier) && analysis.getVersion().equals(version)))
			throw new TrickException("error.import.raw.rrf.bad.analysis", String.format("Please try again with this analysis: %s version: %s", identifier, version), identifier,
					version);
	}

}
