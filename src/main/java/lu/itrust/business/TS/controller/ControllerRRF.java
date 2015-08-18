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
import java.util.stream.Collectors;

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

		return "analyses/single/components/forms/rrf/rrfEditor";
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
		return "analyses/single/components/forms/rrf/scenarioRRF";
	}

	@RequestMapping(value = "/Export/Raw/{idAnalysis}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).EXPORT)")
	public void exportRawRFF(@PathVariable int idAnalysis, Model model, HttpServletResponse response, Principal principal) throws Exception {
		Analysis analysis = serviceAnalysis.get(idAnalysis);
		exportRawRRF(analysis, response, principal.getName());
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
		row.getCell(colIndex).setCellValue("Reference");
		row.getCell(++colIndex).setCellValue("FMeasure");
		row.getCell(++colIndex).setCellValue("FSectorial");
		row.getCell(++colIndex).setCellValue("preventive");
		row.getCell(++colIndex).setCellValue("detective");
		row.getCell(++colIndex).setCellValue("limitative");
		row.getCell(++colIndex).setCellValue("corrective");
		row.getCell(++colIndex).setCellValue("intentional");
		row.getCell(++colIndex).setCellValue("accidental");
		row.getCell(++colIndex).setCellValue("environmental");
		row.getCell(++colIndex).setCellValue("internalThreat");
		row.getCell(++colIndex).setCellValue("externalThreat");
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
		row.getCell(colIndex).setCellValue("name");
		row.getCell(++colIndex).setCellValue("preventive");
		row.getCell(++colIndex).setCellValue("detective");
		row.getCell(++colIndex).setCellValue("limitative");
		row.getCell(++colIndex).setCellValue("corrective");
		row.getCell(++colIndex).setCellValue("intentional");
		row.getCell(++colIndex).setCellValue("accidental");
		row.getCell(++colIndex).setCellValue("environmental");
		row.getCell(++colIndex).setCellValue("internalThreat");
		row.getCell(++colIndex).setCellValue("externalThreat");

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
		header.getCell(0).setCellValue("identifier");
		data.getCell(0).setCellValue(analysis.getIdentifier());
		header.getCell(1).setCellValue("version");
		data.getCell(1).setCellValue(analysis.getVersion());
		workbook.setSheetHidden(workbook.getSheetIndex(TS_INFO_FOR_IMPORT), XSSFWorkbook.SHEET_STATE_VERY_HIDDEN);
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

		return "analyses/single/components/forms/rrf/measureRRF";
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
		return "analyses/single/components/forms/importMeasureCharacteristics";

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
