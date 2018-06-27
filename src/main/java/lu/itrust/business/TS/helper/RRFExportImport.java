package lu.itrust.business.TS.helper;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createWorkSheetPart;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getDouble;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.isEmpty;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.ObjectFactory;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STSheetState;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.AssetStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.impl.NormalMeasure;

public class RRFExportImport {

	private static final String F_SECTORIAL = "FSectorial";

	private static final String F_MEASURE = "FMeasure";

	private static final String REFERENCE = "Reference";

	private static final String VERSION = "version";

	private static final String IDENTIFIER = "identifier";
	
	private static final String RAW_SCENARIO = "Scenario";
	
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
	
	private ServiceAssetType serviceAssetType;
	
	private ServiceAnalysis serviceAnalysis;
	
	private MessageSource messageSource;
	
	public RRFExportImport(ServiceAssetType serviceAssetType, ServiceAnalysis serviceAnalysis, MessageSource messageSource) {
		this.serviceAssetType = serviceAssetType;
		this.serviceAnalysis = serviceAnalysis;
		this.messageSource = messageSource;
	}

	public void exportRawRRF(Analysis analysis, HttpServletResponse response, String username, Locale locale) throws Exception {
		SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.createPackage();
		List<AssetType> assetTypes = serviceAssetType.getAll();
		//writeAnalysisIdentifier(analysis, mlPackage);
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
	
	public Object importRawRRF(String tempPath, int idAnalysis, MultipartFile file, String username, Locale locale) throws Exception {
		try {
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(file.getInputStream());
			final DataFormatter formatter = new DataFormatter();
			//loadAnalysisInfo(analysis, mlPackage.getWorkbookPart(), formatter);
			loadScenarios(analysis.getScenarios(), mlPackage.getWorkbookPart(), formatter);
			loadStandards(analysis.getAnalysisStandards(), mlPackage.getWorkbookPart(), formatter);
			serviceAnalysis.saveOrUpdate(analysis); // Log
			TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.import.raw.rrf",
					String.format("Analysis: %s, version: %s, type: Raw RRF", analysis.getIdentifier(), analysis.getVersion()), username, LogAction.IMPORT,
					analysis.getIdentifier(), analysis.getVersion());
			return JsonMessage.Success(messageSource.getMessage("success.import.raw.rrf", null, "RRF was been successfully update from raw data", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
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
	
	@Deprecated
	protected void loadAnalysisInfo(Analysis analysis, WorkbookPart workbookPart, DataFormatter formatter) throws Exception {
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
			switch (getString(cell, formatter)) {
			case IDENTIFIER:
				identifier = getString(cellData, formatter);
				break;
			case VERSION:
				version = getString(cellData, formatter);
				break;
			}
			if (!(isEmpty(identifier) || isEmpty(version)))
				break;
		}
		if (isEmpty(identifier) || isEmpty(version))
			throw new TrickException("error.import.raw.rrf.analysis.info", "Analysis information cannot be loaded");
		else if (!(analysis.getIdentifier().equals(identifier) && analysis.getVersion().equals(version)))
			throw new TrickException("error.import.raw.rrf.bad.analysis", String.format("Please try again with this analysis: %s version: %s", identifier, version), identifier,
					version);
	}
	
	private void loadMeasureData(AssetMeasure measure, Row row, Integer index, Map<Integer, String> columnMapper, DataFormatter formatter) {
		Map<String, MeasureAssetValue> assetValues = measure.getMeasureAssetValues().stream()
				.collect(Collectors.toMap(assetValue -> assetValue.getAsset().getName(), Function.identity()));
		MeasureProperties properties = measure.getMeasurePropertyList();
		for (int i = 0; i < row.getC().size(); i++) {
			if (i == index)
				continue;
			Cell cell = row.getC().get(i);
			String nameField = columnMapper.get(i);
			if (nameField == null)
				continue;
			double value = getDouble(cell, formatter);
			if (assetValues.containsKey(nameField))
				assetValues.get(nameField).setValue((int) value);
			else
				updateMeasureProperties(properties, nameField, value);
		}
		measure.setMeasurePropertyList(properties);
	}

	private void loadMeasureData(NormalMeasure measure, Row row, Integer index, Map<Integer, String> columnMapper, DataFormatter formatter) {
		Map<String, AssetTypeValue> assetValues = measure.getAssetTypeValues().stream()
				.collect(Collectors.toMap(assetValue -> assetValue.getAssetType().getName(), Function.identity()));
		MeasureProperties properties = measure.getMeasurePropertyList();
		for (int i = 0; i < row.getC().size(); i++) {
			if (i == index)
				continue;
			Cell cell = row.getC().get(i);
			String nameField = columnMapper.get(i);
			if (nameField == null)
				continue;
			double value = getDouble(cell, formatter);
			if (assetValues.containsKey(nameField))
				assetValues.get(nameField).setValue((int) value);
			else
				updateMeasureProperties(properties, nameField, value);
		}
		measure.setMeasurePropertyList(properties);
	}

	private void loadScenarioData(Scenario scenario, Row row, Integer nameIndex, Map<Integer, String> columnMapper, DataFormatter formatter) {

		for (int i = 0; i < row.getC().size(); i++) {
			if (i == nameIndex)
				continue;
			String nameField = columnMapper.get(i);
			if (nameField == null)
				continue;
			double value = getDouble(row.getC().get(i), formatter);
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

	private void loadScenarios(List<Scenario> scenarios, WorkbookPart workbookPart, DataFormatter formatter) throws Exception {
		SheetData sheet = findSheet(workbookPart, RAW_SCENARIOS);
		if (sheet == null || scenarios.isEmpty())
			return;
		if (sheet.getRow().isEmpty())
			throw new TrickException("error.import.raw.rrf.scenario", "Scenario cannot be loaded");
		Row header = sheet.getRow().get(0);
		Map<Integer, String> columnMapper = new LinkedHashMap<Integer, String>();
		Integer nameIndex = mappingColumns(header, RAW_SCENARIO, columnMapper, formatter);
		if (nameIndex == null)
			throw new TrickException("error.import.raw.rrf.scenario.name", "Scenario name column cannot be found");
		Map<String, Scenario> scenarioMappings = scenarios.stream().collect(Collectors.toMap(Scenario::getName, Function.identity()));
		for (Row row : sheet.getRow()) {
			if (row.equals(header))
				continue;
			String key = getString(row.getC().get(nameIndex), formatter);
			if (isEmpty(key))
				continue;
			Scenario scenario = scenarioMappings.get(key);
			if (scenario == null)
				continue;
			loadScenarioData(scenario, row, nameIndex, columnMapper, formatter);
		}
	}

	private void loadStandard(AssetStandard analysisStandard, SheetData sheet, DataFormatter formatter) {

		if (sheet.getRow().isEmpty())
			throw new TrickException("error.import.raw.rrf.standard", "Standard cannot be loaded");
		Map<Integer, String> columnMapper = new LinkedHashMap<Integer, String>();
		Row header = sheet.getRow().get(0);
		Integer index = mappingColumns(header, REFERENCE, columnMapper, formatter);
		if (index == null)
			throw new TrickException("error.import.raw.rrf.standard.reference", "Standard reference column cannot be found");
		Map<String, AssetMeasure> mappingMeasures = analysisStandard.getMeasures().stream()
				.collect(Collectors.toMap(measure -> measure.getMeasureDescription().getReference(), measure -> (AssetMeasure) measure));
		for (int i = 0; i < sheet.getRow().size(); i++) {
			Row row = sheet.getRow().get(i);
			String value = getString(row.getC().get(index), formatter);
			if (isEmpty(value))
				continue;
			AssetMeasure measure = mappingMeasures.get(value);
			if (measure == null)
				continue;
			loadMeasureData(measure, row, index, columnMapper, formatter);
		}
	}

	private void loadStandard(NormalStandard analysisStandard, SheetData sheet, DataFormatter formatter) {
		if (sheet.getRow().isEmpty())
			throw new TrickException("error.import.raw.rrf.standard", "Standard cannot be loaded");
		Map<Integer, String> columnMapper = new LinkedHashMap<Integer, String>();
		Row header = sheet.getRow().get(0);
		Integer index = mappingColumns(header, REFERENCE, columnMapper, formatter);
		if (index == null)
			throw new TrickException("error.import.raw.rrf.standard.reference", "Standard reference column cannot be found");
		Map<String, NormalMeasure> mappingMeasures = analysisStandard.getMeasures().stream()
				.collect(Collectors.toMap(measure -> measure.getMeasureDescription().getReference(), measure -> (NormalMeasure) measure));
		for (int i = 0; i < sheet.getRow().size(); i++) {
			Row row = sheet.getRow().get(i);
			String value = getString(row.getC().get(index), formatter);
			if (isEmpty(value))
				continue;
			NormalMeasure measure = mappingMeasures.get(value);
			if (measure == null)
				continue;
			loadMeasureData(measure, row, index, columnMapper, formatter);
		}
	}

	private void loadStandards(List<AnalysisStandard> analysisStandards, WorkbookPart workbookPart, DataFormatter formatter) throws Exception {
		for (AnalysisStandard analysisStandard : analysisStandards) {
			if (analysisStandard instanceof MaturityStandard)
				continue;
			SheetData sheet = findSheet(workbookPart, analysisStandard.getStandard().getLabel());
			if (sheet == null)
				continue;
			if (analysisStandard instanceof AssetStandard)
				loadStandard((AssetStandard) analysisStandard, sheet, formatter);
			else if (analysisStandard instanceof NormalStandard)
				loadStandard((NormalStandard) analysisStandard, sheet, formatter);
		}
	}

	private Integer mappingColumns(Row row, String identifier, Map<Integer, String> cellIndexToFieldName, DataFormatter formatter) {
		Integer identifierIndex = null;
		for (int i = 0; i < row.getC().size(); i++) {
			String value = getString(row.getC().get(i), formatter);
			if (identifier.equalsIgnoreCase(value))
				identifierIndex = i;
			else
				cellIndexToFieldName.put(i, value);
		}
		return identifierIndex;
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

	@Deprecated
	protected void writeAnalysisIdentifier(Analysis analysis, SpreadsheetMLPackage mlPackage) throws Exception {
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

}
