package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createWorkSheetPart;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getDouble;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getSharedStrings;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

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

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.ObjectFactory;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STSheetState;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.JSTLFunctions;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.RFFMeasureFilter;
import lu.itrust.business.TS.component.RRFScenarioFilter;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.asset.helper.AssetTypeValueComparator;
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

	private static final String SCENARIOS = "scenarios";

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

	private static final String RAW_SCENARIO = "Scenario";

	private static final String DETECTIVE = "detective";

	private static final String PREVENTIVE = "preventive";

	private static final int SCENARIO_RRF_DEFAULT_FIELD_COUNT = 10;

	private static final int MEASURE_RRF_DEFAULT_FIELD_COUNT = 12;

	private static final String TS_INFO_FOR_IMPORT = "^!TS-InfO_fOr-ImpOrt!^";

	private static final String RAW_SCENARIOS = "Scenarios";

	private static final String RAW_PREVENTIVE = "Preventive";

	private static final String RAW_DETECTIVE = "Detective";

	private static final String RAW_LIMITATIVE = "Limitative";

	private static final String RAW_CORRECTIVE = "Corrective";

	private static final String RAW_INTENTIONAL = "Intentional";

	private static final String RAW_ACCIDENTAL = "Accidental";

	private static final String RAW_ENVIRONMENTAL = "Environmental";

	private static final String RAW_INTERNAL_THREAT = "Internal Threat";

	private static final String RAW_EXTERNAL_THREAT = "External Threat";

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
	@RequestMapping(method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String rrf(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<Measure> measures = serviceMeasure.getAllNotMaturityMeasuresFromAnalysisAndComputable(idAnalysis);
		List<Scenario> scenarios = serviceScenario.getAllSelectedFromAnalysis(idAnalysis);
		Map<Chapter, List<Measure>> splittedmeasures = MeasureManager.SplitByChapter(measures);
		if (!splittedmeasures.isEmpty() && splittedmeasures.entrySet().iterator().next().getValue().get(0) != null) {

			model.addAttribute("measures", splittedmeasures);
			model.addAttribute(SCENARIOS, ScenarioManager.SplitByType(scenarios));
			Measure measure = splittedmeasures.entrySet().iterator().next().getValue().get(0);
			AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);
			if (measure instanceof NormalMeasure) {
				NormalMeasure normalMeasure = (NormalMeasure) measure;
				model.addAttribute("standardid", normalMeasure.getAnalysisStandard().getStandard().getId());
				model.addAttribute("measureid", normalMeasure.getId());
				model.addAttribute("strength_measure", normalMeasure.getMeasurePropertyList().getFMeasure());
				model.addAttribute("strength_sectorial", normalMeasure.getMeasurePropertyList().getFSectoral());
				if (AnalysisType.isQualitative(type))
					model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getAllCategories());
				else
					model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getCIACategories());
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

				model.addAttribute("assetTypeValues", assetTypeValues);
			}
			if (measure instanceof AssetMeasure) {
				AssetMeasure assetMeasure = (AssetMeasure) measure;
				model.addAttribute("standardid", assetMeasure.getAnalysisStandard().getStandard().getId());
				model.addAttribute("measureid", assetMeasure.getId());
				model.addAttribute("strength_measure", assetMeasure.getMeasurePropertyList().getFMeasure());
				model.addAttribute("strength_sectorial", assetMeasure.getMeasurePropertyList().getFSectoral());
				if (AnalysisType.isQualitative(type))
					model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getAllCategories());
				else
					model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getCIACategories());
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
	@RequestMapping(value = "/Scenario/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadRRFScenario(@PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, elementID);
		scenario.getAssetTypeValues().sort(new AssetTypeValueComparator());
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
	@RequestMapping(value = "/Scenario/{elementID}/Chart", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID,'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object loadRRFScenarioChart(@RequestBody RFFMeasureFilter measureFilter, @PathVariable int elementID, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (measureFilter.getIdStandard() < 1 || StringUtils.isEmpty(measureFilter.getChapter()))
			return null;
		Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, elementID);
		scenario.getAssetTypeValues().sort(new AssetTypeValueComparator());
		List<AnalysisStandard> standards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);
		List<Measure> measures = new ArrayList<Measure>();
		for (AnalysisStandard standard : standards) {
			if (standard.getStandard().getId() == measureFilter.getIdStandard() && standard.getStandard().getType() != StandardType.MATURITY) {
				if (measureFilter.getIdMeasure() < 1 && standard.getStandard().getType() == StandardType.ASSET)
					return JsonMessage.Error(messageSource.getMessage("error.rrf.standard.standardtype_invalid", null,
							"This standard type permits only to see RRF by single measure (Select a single measure of this standard)", locale));

				for (Measure measure : standard.getMeasures()) {
					if (measureFilter.getIdMeasure() > 0) {
						if (measure.getId() == measureFilter.getIdMeasure()) {
							measures.add(measure);
							break;
						}
					} else if (!measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
							&& measure.getMeasureDescription().getReference().startsWith(measureFilter.getChapter() + ".") && measure.getMeasureDescription().isComputable())
						measures.add(measure);
				}
			}
		}
		return chartGenerator.rrfByScenario(scenario, idAnalysis, measures, locale);
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
	@RequestMapping(value = "/Standard/{standardID}/Measure/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String loadRRFMeasure(@PathVariable int standardID, @PathVariable int elementID, Model model, HttpSession session, Principal principal) throws Exception {

		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);

		if (measure.getAnalysisStandard().getStandard().getId() != standardID || measure.getAnalysisStandard().getStandard().getType() == StandardType.MATURITY)
			return null;

		AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);

		if (measure instanceof NormalMeasure) {

			NormalMeasure normalMeasure = (NormalMeasure) measure;
			model.addAttribute("strength_measure", normalMeasure.getMeasurePropertyList().getFMeasure());
			model.addAttribute("strength_sectorial", normalMeasure.getMeasurePropertyList().getFSectoral());
			if (AnalysisType.isQualitative(type))
				model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getAllCategories());
			else
				model.addAttribute("categories", normalMeasure.getMeasurePropertyList().getCIACategories());
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
			model.addAttribute("assetTypeValues", assetTypeValues);
		} else if (measure instanceof AssetMeasure) {
			AssetMeasure assetMeasure = (AssetMeasure) measure;
			model.addAttribute("standardid", assetMeasure.getAnalysisStandard().getStandard().getId());
			model.addAttribute("measureid", assetMeasure.getId());
			model.addAttribute("strength_measure", assetMeasure.getMeasurePropertyList().getFMeasure());
			model.addAttribute("strength_sectorial", assetMeasure.getMeasurePropertyList().getFSectoral());
			if (AnalysisType.isQualitative(type))
				model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getAllCategories());
			else
				model.addAttribute("categories", assetMeasure.getMeasurePropertyList().getCIACategories());
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
	public @ResponseBody Object loadRRFStandardChart(@RequestBody RRFScenarioFilter scenarioFilter, @PathVariable int elementID, Model model, HttpSession session,
			Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);

		// retrieve analysis id to compute
		if (scenarioFilter.getIdScenarioType() < 1)
			return null;

		ScenarioType scenariotype = ScenarioType.valueOf(scenarioFilter.getIdScenarioType());

		List<Scenario> scenarios = null;

		if (scenarioFilter.getIdScenario() > 0) {
			scenarios = new ArrayList<Scenario>();
			scenarios.add(serviceScenario.getFromAnalysisById(idAnalysis, scenarioFilter.getIdScenario()));
		} else
			scenarios = serviceScenario.getAllSelectedFromAnalysisByType(idAnalysis, scenariotype);

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
		return chartGenerator.rrfByMeasure(measure, idAnalysis, scenarios, customLocale != null ? customLocale : locale);
	}

	@RequestMapping(value = "/Measure/{idMeasure}/Update-child", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idMeasure, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String updateChildRRF(@PathVariable int idMeasure, @RequestBody LinkedList<Integer> idMeasureChilds, HttpSession session, Principal principal,
			Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

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
	@RequestMapping(value = "/Import", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String importRRF(HttpSession session, Principal principal, Model model) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<Standard> standards = serviceStandard.getAllFromAnalysis(idAnalysis);
		standards.removeIf(standard -> Constant.STANDARD_MATURITY.equalsIgnoreCase(standard.getLabel()));
		List<Analysis> analyses = serviceAnalysis.getAllProfileContainsStandard(standards, AnalysisType.QUANTITATIVE, AnalysisType.HYBRID);
		analyses.addAll(serviceAnalysis.getAllHasRightsAndContainsStandard(principal.getName(), AnalysisRight.highRightFrom(AnalysisRight.MODIFY), standards,
				AnalysisType.QUANTITATIVE, AnalysisType.HYBRID));
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
	@RequestMapping(value = "/Import/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object importRRFSave(@ModelAttribute ImportRRFForm rrfForm, HttpSession session, Principal principal, Locale locale) {
		try {
			if (rrfForm.getAnalysis() < 1)
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.no_analysis", null, "No analysis selected", locale));
			else if (rrfForm.getStandards() == null || rrfForm.getStandards().isEmpty())
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.norm", null, "No standard", locale));
			if (!(serviceAnalysis.isProfile(rrfForm.getAnalysis())
					|| serviceUserAnalysisRight.isUserAuthorized(rrfForm.getAnalysis(), principal.getName(), AnalysisRight.highRightFrom(AnalysisRight.MODIFY))))
				return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
			else if (!rrfForm.getStandards().stream().allMatch(idStandard -> serviceStandard.belongToAnalysis(idStandard, rrfForm.getAnalysis())))
				return JsonMessage.Error(messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
			measureManager.importStandard((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), rrfForm);
			return JsonMessage.Success(messageSource.getMessage("success.import_rrf", null, "Measure characteristics has been successfully imported", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
		}

	}

	@RequestMapping(value = "/Export/Raw/{idAnalysis}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportRawRFF(@PathVariable int idAnalysis, Model model, HttpServletResponse response, Principal principal, Locale locale) throws Exception {
		exportRawRRF(serviceAnalysis.get(idAnalysis), response, principal.getName(), locale);
	}

	private void exportRawRRF(Analysis analysis, HttpServletResponse response, String username, Locale locale) throws Exception {
		SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.createPackage();
		List<AssetType> assetTypes = serviceAssetType.getAll();
		writeAnalysisIdentifier(analysis, mlPackage);
		writeScenario(analysis.getScenarios(), mlPackage, locale);
		for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards())
			writeMeasure(analysis.isQualitative(), analysisStandard, assetTypes, mlPackage, locale);
		response.setContentType("xlsx");
		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"" + String.format("RAW RRF %s_V%s.xlsx", analysis.getLabel(), analysis.getVersion()) + "\"");
		mlPackage.save(response.getOutputStream());
		// Log
		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.export.raw.rrf",
				String.format("Analysis: %s, version: %s, type: Raw RRF", analysis.getIdentifier(), analysis.getVersion()), username, LogAction.EXPORT, analysis.getIdentifier(),
				analysis.getVersion());

	}

	@SuppressWarnings("unchecked")
	private void writeAssetMeasure(boolean cssf, AnalysisStandard analysisStandard, SpreadsheetMLPackage mlPackage, Locale locale) throws Exception {
		WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, analysisStandard.getStandard().getLabel());
		SheetData sheetData = worksheetPart.getContents().getSheetData();
		List<AssetMeasure> measures = (List<AssetMeasure>) analysisStandard.getExendedMeasures();
		List<Asset> assets = measures.stream().map(measure -> measure.getMeasureAssetValues()).flatMap(assetValues -> assetValues.stream()).map(assetValue -> assetValue.getAsset())
				.distinct().collect(Collectors.toList());
		Map<String, Integer> mappedValue = new LinkedHashMap<String, Integer>();
		String[] categories = cssf ? CategoryConverter.JAVAKEYS : CategoryConverter.TYPE_CIA_KEYS;
		int totalCol = MEASURE_RRF_DEFAULT_FIELD_COUNT + assets.size() + categories.length;
		Row row = createRow(sheetData);
		int colIndex = generateMeasureHeader(row, mappedValue, categories, totalCol);
		for (Asset asset : assets)
			setValue(row.getC().get(++colIndex), asset.getName());
		measures.stream().forEach(
				measure -> measure.getMeasureAssetValues().forEach(assetValue -> mappedValue.put(measure.getId() + "_" + assetValue.getAsset().getId(), assetValue.getValue())));
		for (AssetMeasure measure : measures) {
			row = createRow(sheetData);
			colIndex = writingMeasureData(row, totalCol, categories, mappedValue, measure.getMeasureDescription().getReference(), measure.getMeasurePropertyList());
			for (Asset asset : assets)
				setValue(row.getC().get(++colIndex), mappedValue.getOrDefault(measure.getId() + "_" + asset.getId(), 0));
		}
	}

	@SuppressWarnings("unchecked")
	private void writeNormalMeasure(boolean cssf, AnalysisStandard analysisStandard, List<AssetType> assetTypes, SpreadsheetMLPackage mlPackage, Locale locale) throws Exception {
		WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, analysisStandard.getStandard().getLabel());
		SheetData sheetData = worksheetPart.getContents().getSheetData();
		List<NormalMeasure> measures = (List<NormalMeasure>) analysisStandard.getExendedMeasures();
		Row row = createRow(sheetData);
		Map<String, Integer> mappedValue = new LinkedHashMap<String, Integer>();
		String[] categories = cssf ? CategoryConverter.JAVAKEYS : CategoryConverter.TYPE_CIA_KEYS;
		int totalCol = MEASURE_RRF_DEFAULT_FIELD_COUNT + assetTypes.size() + categories.length;
		int colIndex = generateMeasureHeader(row, mappedValue, categories, totalCol);
		for (AssetType assetType : assetTypes)
			setValue(row.getC().get(++colIndex), assetType.getName());
		measures.stream().forEach(measure -> measure.getAssetTypeValues()
				.forEach(assetypeValue -> mappedValue.put(measure.getId() + "_" + assetypeValue.getAssetType().getName(), assetypeValue.getValue())));
		for (NormalMeasure measure : measures) {
			row = createRow(sheetData);
			colIndex = writingMeasureData(row, totalCol, categories, mappedValue, measure.getMeasureDescription().getReference(), measure.getMeasurePropertyList());
			for (AssetType assetType : assetTypes)
				setValue(row.getC().get(++colIndex), mappedValue.getOrDefault(measure.getId() + "_" + assetType.getName(), 0));

		}
	}

	private int generateMeasureHeader(Row row, Map<String, Integer> mappedValue, String[] categories, int totalCol) {
		for (int i = 0; i < totalCol; i++)
			row.getC().add(Context.getsmlObjectFactory().createCell());
		int colIndex = 0;
		setValue(row.getC().get(colIndex), REFERENCE);
		setValue(row.getC().get(++colIndex), F_MEASURE);
		setValue(row.getC().get(++colIndex), F_SECTORIAL);
		setValue(row.getC().get(++colIndex), RAW_PREVENTIVE);
		setValue(row.getC().get(++colIndex), RAW_DETECTIVE);
		setValue(row.getC().get(++colIndex), RAW_LIMITATIVE);
		setValue(row.getC().get(++colIndex), RAW_CORRECTIVE);
		setValue(row.getC().get(++colIndex), RAW_INTENTIONAL);
		setValue(row.getC().get(++colIndex), RAW_ACCIDENTAL);
		setValue(row.getC().get(++colIndex), RAW_ENVIRONMENTAL);
		setValue(row.getC().get(++colIndex), RAW_INTERNAL_THREAT);
		setValue(row.getC().get(++colIndex), RAW_EXTERNAL_THREAT);
		for (String category : categories)
			setValue(row.getC().get(++colIndex), category);
		return colIndex;
	}

	private int writingMeasureData(Row row, int totalCol, String[] categories, Map<String, Integer> mappedValue, String reference, MeasureProperties properties) {
		for (int i = 0; i < totalCol; i++)
			row.getC().add(Context.getsmlObjectFactory().createCell());
		int colIndex = 0;
		setValue(row.getC().get(colIndex), reference);
		setValue(row.getC().get(++colIndex), properties.getFMeasure());
		setValue(row.getC().get(++colIndex), properties.getFSectoral());
		setValue(row.getC().get(++colIndex), properties.getPreventive());
		setValue(row.getC().get(++colIndex), properties.getDetective());
		setValue(row.getC().get(++colIndex), properties.getLimitative());
		setValue(row.getC().get(++colIndex), properties.getCorrective());
		setValue(row.getC().get(++colIndex), properties.getIntentional());
		setValue(row.getC().get(++colIndex), properties.getAccidental());
		setValue(row.getC().get(++colIndex), properties.getEnvironmental());
		setValue(row.getC().get(++colIndex), properties.getInternalThreat());
		setValue(row.getC().get(++colIndex), properties.getExternalThreat());
		for (String category : categories)
			setValue(row.getC().get(++colIndex), properties.getCategoryValue(category));
		return colIndex;
	}

	private void writeMeasure(boolean isCSSF, AnalysisStandard analysisStandard, List<AssetType> assetTypes, SpreadsheetMLPackage mlPackage, Locale locale) throws Exception {
		switch (analysisStandard.getStandard().getType()) {
		case ASSET:
			writeAssetMeasure(isCSSF, analysisStandard, mlPackage, locale);
			break;
		case NORMAL:
			writeNormalMeasure(isCSSF, analysisStandard, assetTypes, mlPackage, locale);
			break;
		default:
			break;
		}
	}

	private void writeScenario(List<Scenario> scenarios, SpreadsheetMLPackage mlPackage, Locale locale) throws Exception {
		if (scenarios.isEmpty())
			return;
		ObjectFactory factory = Context.getsmlObjectFactory();
		WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, RAW_SCENARIOS);
		SheetData scenarioSheet = worksheetPart.getContents().getSheetData();
		int colIndex = 0;
		Row row = factory.createRow();
		for (int i = 0; i < SCENARIO_RRF_DEFAULT_FIELD_COUNT; i++)
			row.getC().add(factory.createCell());
		scenarioSheet.getRow().add(row);
		setValue(row.getC().get(colIndex), RAW_SCENARIO);
		setValue(row.getC().get(++colIndex), RAW_PREVENTIVE);
		setValue(row.getC().get(++colIndex), RAW_DETECTIVE);
		setValue(row.getC().get(++colIndex), RAW_LIMITATIVE);
		setValue(row.getC().get(++colIndex), RAW_CORRECTIVE);
		setValue(row.getC().get(++colIndex), RAW_INTENTIONAL);
		setValue(row.getC().get(++colIndex), RAW_ACCIDENTAL);
		setValue(row.getC().get(++colIndex), RAW_ENVIRONMENTAL);
		setValue(row.getC().get(++colIndex), RAW_INTERNAL_THREAT);
		setValue(row.getC().get(++colIndex), RAW_EXTERNAL_THREAT);

		for (Scenario scenario : scenarios) {
			row = factory.createRow();
			for (int i = 0; i < SCENARIO_RRF_DEFAULT_FIELD_COUNT; i++)
				row.getC().add(factory.createCell());
			colIndex = 0;
			scenarioSheet.getRow().add(row);
			setValue(row.getC().get(colIndex), scenario.getName());
			setValue(row.getC().get(++colIndex), scenario.getPreventive());
			setValue(row.getC().get(++colIndex), scenario.getDetective());
			setValue(row.getC().get(++colIndex), scenario.getLimitative());
			setValue(row.getC().get(++colIndex), scenario.getCorrective());
			setValue(row.getC().get(++colIndex), scenario.getIntentional());
			setValue(row.getC().get(++colIndex), scenario.getAccidental());
			setValue(row.getC().get(++colIndex), scenario.getEnvironmental());
			setValue(row.getC().get(++colIndex), scenario.getInternalThreat());
			setValue(row.getC().get(++colIndex), scenario.getExternalThreat());
		}
	}

	private void writeAnalysisIdentifier(Analysis analysis, SpreadsheetMLPackage mlPackage) throws Exception {
		int index = mlPackage.getWorkbookPart().getContents().getSheets().getSheet().size() + 1;
		WorksheetPart worksheetPart = mlPackage.createWorksheetPart(new PartName(String.format("/xl/worksheets/sheet%d.xml", index)), TS_INFO_FOR_IMPORT, index);
		SheetData analysisSheet = worksheetPart.getContents().getSheetData();
		ObjectFactory factory = Context.getsmlObjectFactory();
		Row header = factory.createRow(), data = factory.createRow();
		for (int i = 0; i < 2; i++) {
			header.getC().add(factory.createCell());
			data.getC().add(factory.createCell());
		}
		analysisSheet.getRow().add(header);
		analysisSheet.getRow().add(data);
		setValue(header.getC().get(0), IDENTIFIER);
		setValue(data.getC().get(0), analysis.getIdentifier());
		setValue(header.getC().get(1), VERSION);
		setValue(data.getC().get(1), analysis.getVersion());
		mlPackage.getWorkbookPart().getContents().getSheets().getSheet().get(index - 1).setState(STSheetState.VERY_HIDDEN);
	}

	@RequestMapping(value = "/Form/Import/Raw/{idAnalysis}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String formImportRRF(@PathVariable int idAnalysis, Model model, Principal principal) {
		model.addAttribute("idAnalysis", idAnalysis);
		return "analyses/single/components/rrf/form/importRaw";
	}

	@RequestMapping(value = "/Import/Raw/{idAnalysis}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object importRRF(@PathVariable int idAnalysis, @RequestParam(value = "file") MultipartFile file, Principal principal, HttpServletRequest request,
			Locale locale) throws Exception {
		return importRawRRF(request.getServletContext().getRealPath("/WEB-INF/tmp/"), idAnalysis, file, principal.getName(), locale);
	}

	private Object importRawRRF(String tempPath, int idAnalysis, MultipartFile file, String username, Locale locale) throws Exception {
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(file.getInputStream());
			Map<String, String> sharedStrings = getSharedStrings(mlPackage.getWorkbookPart());
			loadAnalysisInfo(analysis, mlPackage.getWorkbookPart(), sharedStrings);
			loadScenarios(analysis.getScenarios(), mlPackage.getWorkbookPart(), sharedStrings);
			loadStandards(analysis.getAnalysisStandards(), mlPackage.getWorkbookPart(), sharedStrings);
			serviceAnalysis.saveOrUpdate(analysis); // Log
			TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.import.raw.rrf",
					String.format("Analysis: %s, version: %s, type: Raw RRF", analysis.getIdentifier(), analysis.getVersion()), username, LogAction.IMPORT,
					analysis.getIdentifier(), analysis.getVersion());
			return JsonMessage.Success(messageSource.getMessage("success.import.raw.rrf", null, "RRF was been successfully update from raw data", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
	}

	private void loadStandards(List<AnalysisStandard> analysisStandards, WorkbookPart workbookPart, Map<String, String> sharedStrings) throws Exception {
		for (AnalysisStandard analysisStandard : analysisStandards) {
			if (analysisStandard instanceof MaturityStandard)
				continue;
			SheetData sheet = findSheet(workbookPart, analysisStandard.getStandard().getLabel());
			if (sheet == null)
				continue;
			if (analysisStandard instanceof AssetStandard)
				loadStandard((AssetStandard) analysisStandard, sheet, sharedStrings);
			else if (analysisStandard instanceof NormalStandard)
				loadStandard((NormalStandard) analysisStandard, sheet, sharedStrings);
		}
	}

	private void loadStandard(AssetStandard analysisStandard, SheetData sheet, Map<String, String> sharedStrings) {

		if (sheet.getRow().isEmpty())
			throw new TrickException("error.import.raw.rrf.standard", "Standard cannot be loaded");
		Map<Integer, String> cellIndexToFieldName = new LinkedHashMap<Integer, String>();
		Row header = sheet.getRow().get(0);
		Integer index = mappingColumns(header, REFERENCE, cellIndexToFieldName, sharedStrings);
		if (index == null)
			throw new TrickException("error.import.raw.rrf.standard.reference", "Standard reference column cannot be found");
		Map<String, AssetMeasure> mappingMeasures = analysisStandard.getMeasures().stream()
				.collect(Collectors.toMap(measure -> measure.getMeasureDescription().getReference(), measure -> (AssetMeasure) measure));
		for (int i = 0; i < sheet.getRow().size(); i++) {
			Row row = sheet.getRow().get(i);
			String value = getString(row.getC().get(index), sharedStrings);
			if (value == null)
				continue;
			AssetMeasure measure = mappingMeasures.get(value);
			if (measure == null)
				continue;
			loadMeasureData(measure, row, index, cellIndexToFieldName);
		}
	}

	private void loadStandard(NormalStandard analysisStandard, SheetData sheet, Map<String, String> sharedStrings) {
		if (sheet.getRow().isEmpty())
			throw new TrickException("error.import.raw.rrf.standard", "Standard cannot be loaded");
		Map<Integer, String> cellIndexToFieldName = new LinkedHashMap<Integer, String>();
		Row header = sheet.getRow().get(0);
		Integer index = mappingColumns(header, REFERENCE, cellIndexToFieldName, sharedStrings);
		if (index == null)
			throw new TrickException("error.import.raw.rrf.standard.reference", "Standard reference column cannot be found");
		Map<String, NormalMeasure> mappingMeasures = analysisStandard.getMeasures().stream()
				.collect(Collectors.toMap(measure -> measure.getMeasureDescription().getReference(), measure -> (NormalMeasure) measure));
		for (int i = 0; i < sheet.getRow().size(); i++) {
			Row row = sheet.getRow().get(i);
			String value = getString(row.getC().get(index), sharedStrings);
			if (value == null)
				continue;
			NormalMeasure measure = mappingMeasures.get(value);
			if (measure == null)
				continue;
			loadMeasureData(measure, row, index, cellIndexToFieldName);
		}
	}

	private void loadMeasureData(NormalMeasure measure, Row row, Integer index, Map<Integer, String> cellIndexToFieldName) {
		Map<String, AssetTypeValue> assetValues = measure.getAssetTypeValues().stream()
				.collect(Collectors.toMap(assetValue -> assetValue.getAssetType().getName(), Function.identity()));
		MeasureProperties properties = measure.getMeasurePropertyList();
		for (int i = 0; i < row.getC().size(); i++) {
			if (i == index)
				continue;
			Cell cell = row.getC().get(i);
			String nameField = cellIndexToFieldName.get(i);
			if (nameField == null)
				continue;
			double value = getDouble(cell);
			if (assetValues.containsKey(nameField))
				assetValues.get(nameField).setValue((int) value);
			else
				updateMeasureProperties(properties, nameField, value);
		}
		measure.setMeasurePropertyList(properties);
	}

	private void loadMeasureData(AssetMeasure measure, Row row, Integer index, Map<Integer, String> cellIndexToFieldName) {
		Map<String, MeasureAssetValue> assetValues = measure.getMeasureAssetValues().stream()
				.collect(Collectors.toMap(assetValue -> assetValue.getAsset().getName(), Function.identity()));
		MeasureProperties properties = measure.getMeasurePropertyList();
		for (int i = 0; i < row.getC().size(); i++) {
			if (i == index)
				continue;
			Cell cell = row.getC().get(i);
			String nameField = cellIndexToFieldName.get(i);
			if (nameField == null)
				continue;
			double value = getDouble(cell);
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
			case RAW_EXTERNAL_THREAT:
				properties.setExternalThreat((int) value);
				break;
			case RAW_INTERNAL_THREAT:
				properties.setInternalThreat((int) value);
				break;
			case RAW_ENVIRONMENTAL:
				properties.setEnvironmental((int) value);
				break;
			case RAW_ACCIDENTAL:
				properties.setAccidental((int) value);
				break;
			case RAW_INTENTIONAL:
				properties.setIntentional((int) value);
				break;
			case RAW_CORRECTIVE:
				properties.setCorrective(value);
				break;
			case RAW_LIMITATIVE:
				properties.setLimitative(value);
				break;
			case RAW_DETECTIVE:
				properties.setDetective(value);
				break;
			case RAW_PREVENTIVE:
				properties.setPreventive(value);
				break;
			}
		}
	}

	private void loadScenarios(List<Scenario> scenarios, WorkbookPart workbookPart, Map<String, String> sharedStrings) throws Exception {
		SheetData sheet = findSheet(workbookPart, RAW_SCENARIOS);
		if (sheet == null || scenarios.isEmpty())
			return;
		if (sheet.getRow().isEmpty())
			throw new TrickException("error.import.raw.rrf.scenario", "Scenario cannot be loaded");
		Row header = sheet.getRow().get(0);
		Map<Integer, String> cellIndexToFieldName = new LinkedHashMap<Integer, String>();
		Integer nameIndex = mappingColumns(header, RAW_SCENARIO, cellIndexToFieldName, sharedStrings);
		if (nameIndex == null)
			throw new TrickException("error.import.raw.rrf.scenario.name", "Scenario name column cannot be found");
		Map<String, Scenario> scenarioMappings = scenarios.stream().collect(Collectors.toMap(Scenario::getName, Function.identity()));
		for (Row row : sheet.getRow()) {
			if (row.equals(header))
				continue;
			String key = getString(row.getC().get(nameIndex), sharedStrings);
			if (key == null)
				continue;
			Scenario scenario = scenarioMappings.get(key);
			if (scenario == null)
				continue;
			loadScenarioData(scenario, row, nameIndex, cellIndexToFieldName);
		}
	}

	private Integer mappingColumns(Row row, String identifier, Map<Integer, String> cellIndexToFieldName, Map<String, String> sharedStrings) {
		Integer identifierIndex = null;
		for (int i = 0; i < row.getC().size(); i++) {
			String value = getString(row.getC().get(i), sharedStrings);
			if (identifier.equalsIgnoreCase(value))
				identifierIndex = i;
			else
				cellIndexToFieldName.put(i, value);
		}
		return identifierIndex;
	}

	private void loadScenarioData(Scenario scenario, Row row, Integer nameIndex, Map<Integer, String> cellIndexToFieldName) {

		for (int i = 0; i < row.getC().size(); i++) {
			if (i == nameIndex)
				continue;
			String nameField = cellIndexToFieldName.get(i);
			if (nameField == null)
				continue;
			double value = getDouble(row.getC().get(i));
			switch (nameField) {
			case RAW_EXTERNAL_THREAT:
				scenario.setExternalThreat((int) value);
				break;
			case RAW_INTERNAL_THREAT:
				scenario.setInternalThreat((int) value);
				break;
			case RAW_ENVIRONMENTAL:
				scenario.setEnvironmental((int) value);
				break;
			case RAW_ACCIDENTAL:
				scenario.setAccidental((int) value);
				break;
			case RAW_INTENTIONAL:
				scenario.setIntentional((int) value);
				break;
			case RAW_CORRECTIVE:
				scenario.setCorrective(value);
				break;
			case RAW_LIMITATIVE:
				scenario.setLimitative(value);
				break;
			case RAW_DETECTIVE:
				scenario.setDetective(value);
				break;
			case RAW_PREVENTIVE:
				scenario.setPreventive(value);
				break;
			}
		}
	}

	private void loadAnalysisInfo(Analysis analysis, WorkbookPart workbookPart, Map<String, String> sharedStrings) throws Exception {
		SheetData sheet = findSheet(workbookPart, TS_INFO_FOR_IMPORT);
		if (sheet == null)
			throw new TrickException("error.import.raw.rrf.analysis.info", "Analysis information cannot be loaded");
		String identifier = null, version = null;
		Row row = sheet.getRow().get(0), data = sheet.getRow().get(1);
		if (row == null || data == null)
			throw new TrickException("error.import.raw.rrf.analysis.info", "Analysis information cannot be loaded");
		for (int i = 0; i < row.getC().size(); i++) {
			org.xlsx4j.sml.Cell cell = row.getC().get(i), cellData = data.getC().get(i);
			if (cellData == null)
				break;
			switch (getString(cell, sharedStrings)) {
			case IDENTIFIER:
				identifier = getString(cellData, sharedStrings);
				break;
			case VERSION:
				version = getString(cellData, sharedStrings);
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