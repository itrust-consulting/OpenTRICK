package lu.itrust.business.ts.helper;

import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.createRow;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.createWorkSheetPart;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getDouble;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.isEmpty;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.ObjectFactory;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.ts.asynchronousWorkers.WorkerExportRiskSheet;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceAssetType;
import lu.itrust.business.ts.database.service.ServiceAssetTypeValue;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.model.cssf.tools.CategoryConverter;
import lu.itrust.business.ts.model.general.AssetTypeValue;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.SecurityCriteria;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.AssetStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.ts.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.ts.model.standard.measure.impl.NormalMeasure;

public class RRFExportImport {

	/**
	 *
	 */
	private static final String RAW_ASSET_TYPES = "Asset Types";

	/**
	 *
	 */
	private static final String RAW_ASSETS = "Assets";

	private static final String F_SECTORIAL = "FSectorial";

	private static final String F_MEASURE = "FMeasure";

	private static final String REFERENCE = "Reference";

	private static final String RAW_SCENARIO = "Scenario";

	private static final int SCENARIO_RRF_DEFAULT_FIELD_COUNT = 12;

	private static final int MEASURE_RRF_DEFAULT_FIELD_COUNT = 12;

	private static final String RAW_SCENARIOS = "Scenario-RRF";

	private static final String RAW_PREVENTIVE = "Preventive";

	private static final String RAW_DETECTIVE = "Detective";

	private static final String RAW_LIMITATIVE = "Limitative";

	private static final String RAW_CORRECTIVE = "Corrective";

	private static final String RAW_INTENTIONAL = "Intentional";

	private static final String RAW_ACCIDENTAL = "Accidental";

	private static final String RAW_ENVIRONMENTAL = "Environmental";

	private static final String RAW_INTERNAL_THREAT = "Internal Threat";

	private static final String RAW_EXTERNAL_THREAT = "External Threat";

	private ServiceAssetTypeValue serviceAssetTypeValue;

	private ServiceAssetType serviceAssetType;

	private ServiceAnalysis serviceAnalysis;

	private MessageSource messageSource;

	public RRFExportImport(ServiceAssetType serviceAssetType, ServiceAnalysis serviceAnalysis,
			ServiceAssetTypeValue serviceAssetTypeValue,
			MessageSource messageSource) {

		this.serviceAssetTypeValue = serviceAssetTypeValue;
		this.serviceAssetType = serviceAssetType;
		this.serviceAnalysis = serviceAnalysis;
		this.messageSource = messageSource;
	}

	public void exportRawRRF(Analysis analysis, File file, Consumer<SpreadsheetMLPackage> callback)
			throws Exception {
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(file);
		final List<AssetType> assetTypes = serviceAssetType.getAll();
		writeScenario(analysis.getScenarios(), mlPackage);
		for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards().values())
			writeMeasure(analysis.isQualitative(), analysisStandard, assetTypes, mlPackage);
		callback.accept(mlPackage);
	}

	public Object importRawRRF(final int idAnalysis, final MultipartFile file, final String username,
			final Locale locale) throws Exception {
		try {
			final DataFormatter formatter = new DataFormatter();
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(file.getInputStream());
			loadScenarios(analysis.getScenarios(), analysis.getAssets(), mlPackage.getWorkbookPart(), formatter);
			loadStandards(analysis, mlPackage.getWorkbookPart(), formatter);
			serviceAnalysis.saveOrUpdate(analysis); // Log
			TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.import.raw.rrf",
					String.format("Analysis: %s, version: %s, type: Raw RRF", analysis.getIdentifier(),
							analysis.getVersion()),
					username, LogAction.IMPORT,
					analysis.getIdentifier(), analysis.getVersion());
			return JsonMessage.success(messageSource.getMessage("success.import.raw.rrf", null,
					"RRF was been successfully update from raw data", locale));
		} catch (TrickException e) {
			return JsonMessage.error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
	}

	private int generateMeasureHeader(Row row, String[] categories, int totalCol) {
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

	private void loadMeasureData(AssetMeasure measure,final Map<String,Asset> assets, Row row, Integer index, Map<Integer, String> columnMapper,
			DataFormatter formatter) {
		Map<String, MeasureAssetValue> measureAssetValues = measure.getMeasureAssetValues().stream()
				.collect(Collectors.toMap(assetValue -> assetValue.getAsset().getName(), Function.identity()));
		MeasureProperties properties = measure.getMeasurePropertyList();
		for (int i = 0; i < row.getC().size(); i++) {
			if (i == index)
				continue;
			final String nameField = columnMapper.get(i);
			if (nameField != null) {
				double value = getDouble(row, i, formatter);
				if (measureAssetValues.containsKey(nameField))
					measureAssetValues.get(nameField).setValue((int) value);
				else{
					final var asset = assets.get(nameField);
					if(asset == null)
						updateMeasureProperties(properties, nameField, value);
					else {
						final var assetValue = new MeasureAssetValue(asset, (int) value);
						measureAssetValues.put(asset.getName(), assetValue);
						measure.addAnMeasureAssetValue(assetValue);
					}
				}
			}
		}
		measure.setMeasurePropertyList(properties);
	}

	private void loadMeasureData(NormalMeasure measure,final Map<String,AssetType> assetTypes, Row row, Integer index, Map<Integer, String> columnMapper,
			DataFormatter formatter) {
		Map<String, AssetTypeValue> assetValues = measure.getAssetTypeValues().stream()
				.collect(Collectors.toMap(assetValue -> assetValue.getAssetType().getName(), Function.identity()));
		MeasureProperties properties = measure.getMeasurePropertyList();
		for (int i = 0; i < row.getC().size(); i++) {
			if (i == index)
				continue;
			String nameField = columnMapper.get(i);
			if (nameField != null) {
				double value = getDouble(row, i, formatter);
				if (assetValues.containsKey(nameField))
					assetValues.get(nameField).setValue((int) value);
				else{
					final var assetType = assetTypes.get(nameField);
					if(assetType == null)
						updateMeasureProperties(properties, nameField, value);
					else {
						final var assetValue = new AssetTypeValue(assetType, (int) value);
						assetValues.put(assetType.getName(), assetValue);
						measure.addAnAssetTypeValue(assetValue);
					}
				}
			}
		}
		measure.setMeasurePropertyList(properties);
	}

	private void loadScenarioData(Scenario scenario, Row row, Integer nameIndex, Map<Integer, String> columnMapper,
			Map<String, Asset> assetMappings, Map<String, AssetType> assetTypeMappings,
			List<AssetTypeValue> toDeletedsTypeValues, DataFormatter formatter) {

		for (int i = 0; i < row.getC().size(); i++) {
			final String nameField = columnMapper.get(i);
			if (i == nameIndex || nameField == null)
				continue;
			if (nameField.equalsIgnoreCase(RAW_ASSETS)) {
				if (scenario.isAssetLinked()) {
					final List<String> assetNames = Arrays
							.stream(getString(row, i, formatter, "").trim().toLowerCase().split(";"))
							.map(String::trim).distinct().collect(Collectors.toList());
					scenario.getLinkedAssets().removeIf(e -> !assetNames.contains(e.getName().toLowerCase()));
					assetNames.stream().map(assetMappings::get).filter(Objects::nonNull)
							.forEach(scenario::addApplicable);
				}
			} else if (nameField.equalsIgnoreCase(RAW_ASSET_TYPES)) {
				if (!scenario.isAssetLinked()) {

					final List<String> assetTypeNames = Arrays
							.stream(getString(row, i, formatter, "").trim().toLowerCase().split(";"))
							.map(String::trim).distinct().collect(Collectors.toList());

					scenario.getAssetTypeValues()
							.removeIf(e -> !assetTypeNames.contains(e.getAssetType().getName().toLowerCase())
									&& toDeletedsTypeValues.add(e));
					assetTypeNames.stream().map(assetTypeMappings::get).filter(Objects::nonNull)
							.forEach(scenario::addApplicable);
				}

			} else {
				double value = getDouble(row, i, formatter);
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
					default:
						break;
				}
			}
		}
	}

	private void loadScenarios(List<Scenario> scenarios, List<Asset> assets, WorkbookPart workbookPart,
			DataFormatter formatter)
			throws Exception {
		SheetData sheet = findSheet(workbookPart, RAW_SCENARIOS);
		if (sheet == null || scenarios.isEmpty())
			return;
		if (sheet.getRow().isEmpty())
			throw new TrickException("error.import.raw.rrf.scenario", "Scenario cannot be loaded");
		Row header = sheet.getRow().get(0);
		Map<Integer, String> columnMapper = new LinkedHashMap<>();

		Integer nameIndex = mappingColumns(header, RAW_SCENARIO, columnMapper, formatter);
		if (nameIndex == null)
			throw new TrickException("error.import.raw.rrf.scenario.name", "Scenario name column cannot be found");

		final List<AssetTypeValue> toDeletedsTypeValues = new ArrayList<>();

		Map<String, Scenario> scenarioMappings = scenarios.stream()
				.collect(Collectors.toMap(Scenario::getName, Function.identity()));

		Map<String, Asset> assetMappings = assets.stream()
				.collect(Collectors.toMap(e -> e.getName().toLowerCase(), Function.identity()));

		Map<String, AssetType> assetTypeMappings = serviceAssetType.getAll().stream()
				.collect(Collectors.toMap(e -> e.getName().toLowerCase(), Function.identity()));

		for (Row row : sheet.getRow()) {
			if (!row.equals(header)) {

				String key = getString(row, nameIndex, formatter);
				if (isEmpty(key))
					continue;
				Scenario scenario = scenarioMappings.get(key);
				if (scenario == null)
					scenario = scenarioMappings.get(key.trim());
				if (scenario != null)
					loadScenarioData(scenario, row, nameIndex, columnMapper, assetMappings, assetTypeMappings,
							toDeletedsTypeValues, formatter);
			}
		}

		toDeletedsTypeValues.stream().filter(e -> e.getId() > 0).forEach(e -> serviceAssetTypeValue.delete(e));
	}

	private void loadStandard(AssetStandard analysisStandard, final Map<String,Asset> assets, SheetData sheet, DataFormatter formatter) {

		if (sheet.getRow().isEmpty())
			throw new TrickException("error.import.raw.rrf.standard", "Standard cannot be loaded");
		Map<Integer, String> columnMapper = new LinkedHashMap<>();
		Row header = sheet.getRow().get(0);
		Integer index = mappingColumns(header, REFERENCE, columnMapper, formatter);
		if (index == null)
			throw new TrickException("error.import.raw.rrf.standard.reference",
					"Standard reference column cannot be found");
		Map<String, AssetMeasure> mappingMeasures = analysisStandard.getMeasures().stream()
				.collect(Collectors.toMap(measure -> measure.getMeasureDescription().getReference(),
						AssetMeasure.class::cast));
		for (int i = 0; i < sheet.getRow().size(); i++) {
			Row row = sheet.getRow().get(i);
			String value = getString(row, index, formatter);
			if (isEmpty(value))
				continue;
			AssetMeasure measure = mappingMeasures.get(value);
			if (measure != null)
				loadMeasureData(measure,assets, row, index, columnMapper, formatter);
		}
	}

	private void loadStandard(NormalStandard analysisStandard, final Map<String,AssetType> assetTypes, SheetData sheet, DataFormatter formatter) {
		if (sheet.getRow().isEmpty())
			throw new TrickException("error.import.raw.rrf.standard", "Standard cannot be loaded");
		final Map<Integer, String> columnMapper = new LinkedHashMap<>();
		final Row header = sheet.getRow().get(0);
		final Integer index = mappingColumns(header, REFERENCE, columnMapper, formatter);
		if (index == null)
			throw new TrickException("error.import.raw.rrf.standard.reference",
					"Standard reference column cannot be found");

		final Map<String, NormalMeasure> mappingMeasures = analysisStandard.getMeasures().stream()
				.collect(Collectors.toMap(measure -> measure.getMeasureDescription().getReference(),
						NormalMeasure.class::cast));
		for (int i = 0; i < sheet.getRow().size(); i++) {
			final Row row = sheet.getRow().get(i);
			final String value = getString(row, index, formatter);
			if (isEmpty(value))
				continue;
			final NormalMeasure measure = mappingMeasures.get(value);
			if (measure != null)
				loadMeasureData(measure,assetTypes, row, index, columnMapper, formatter);
		}
	}

	private void loadStandards(Analysis analysis, WorkbookPart workbookPart,
			DataFormatter formatter) throws Docx4JException{
		final Map<String,AssetType> assetTypes = serviceAssetType.getAll().stream()
				.collect(Collectors.toMap(assetType -> assetType.getName(), Function.identity()));

		final Map<String, Asset> assets = analysis.getAssets().stream()
				.collect(Collectors.toMap(asset -> asset.getName(), Function.identity()));
		
		for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards().values()) {
			if (analysisStandard instanceof MaturityStandard)
				continue;
			final SheetData sheet = findSheet(workbookPart, analysisStandard.getStandard().getName());
			if (sheet != null) {
				if (analysisStandard instanceof AssetStandard assetStandard)
					loadStandard(assetStandard, assets, sheet, formatter);
				else if (analysisStandard instanceof NormalStandard normalStandard)
					loadStandard(normalStandard,assetTypes, sheet, formatter);
			}
		}
	}

	private Integer mappingColumns(Row row, String identifier, Map<Integer, String> cellIndexToFieldName,
			DataFormatter formatter) {
		Integer identifierIndex = null;
		for (int i = 0; i < row.getC().size(); i++) {
			String value = getString(row, i, formatter);
			if (identifier.equalsIgnoreCase(value))
				identifierIndex = i;
			else
				cellIndexToFieldName.put(i, value);
		}
		return identifierIndex;
	}

	private void updateMeasureProperties(MeasureProperties properties, String nameField, double value) {

		if (SecurityCriteria.isCategoryKey(nameField))
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
				default:
					break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void writeAssetMeasure(boolean cssf, AnalysisStandard analysisStandard, SpreadsheetMLPackage mlPackage)
			throws Exception {
		final WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, analysisStandard.getStandard().getName());
		final SheetData sheetData = worksheetPart.getContents().getSheetData();
		final List<AssetMeasure> measures = (List<AssetMeasure>) analysisStandard.getExendedMeasures();
		final List<Asset> assets = measures.stream().map(AssetMeasure::getMeasureAssetValues)
				.flatMap(Collection::stream)
				.map(MeasureAssetValue::getAsset).distinct()
				.sorted((a1, a2) -> NaturalOrderComparator.compareTo(a1.getName(), a2.getName()))
				.collect(Collectors.toList());
		final Map<String, Integer> mappedValue = new LinkedHashMap<>();
		final String[] categories = cssf ? CategoryConverter.JAVAKEYS : CategoryConverter.TYPE_CIA_KEYS;
		final int totalCol = MEASURE_RRF_DEFAULT_FIELD_COUNT + assets.size() + categories.length;
		Row row = createRow(sheetData);
		int colIndex = generateMeasureHeader(row, categories, totalCol);
		measures.sort(new MeasureComparator());
		for (Asset asset : assets)
			setValue(row, ++colIndex, asset.getName());
		measures.stream().forEach(
				measure -> measure.getMeasureAssetValues().forEach(assetValue -> mappedValue
						.put(measure.getId() + "_" + assetValue.getAsset().getId(), assetValue.getValue())));
		for (AssetMeasure measure : measures) {
			row = createRow(sheetData);
			colIndex = writingMeasureData(row, totalCol, categories,
					measure.getMeasureDescription().getReference(), measure.getMeasurePropertyList());
			for (Asset asset : assets)
				setValue(row, ++colIndex,
						mappedValue.getOrDefault(measure.getId() + "_" + asset.getId(), 0));
		}
	}

	private void writeMeasure(boolean isCSSF, AnalysisStandard analysisStandard, List<AssetType> assetTypes,
			SpreadsheetMLPackage mlPackage) throws Exception {
		switch (analysisStandard.getStandard().getType()) {
			case ASSET:
				writeAssetMeasure(isCSSF, analysisStandard, mlPackage);
				break;
			case NORMAL:
				writeNormalMeasure(isCSSF, analysisStandard, assetTypes, mlPackage);
				break;
			default:
				break;
		}
	}

	@SuppressWarnings("unchecked")
	private void writeNormalMeasure(boolean cssf, AnalysisStandard analysisStandard, List<AssetType> assetTypes,
			SpreadsheetMLPackage mlPackage) throws Exception {
		final WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, analysisStandard.getStandard().getName());
		final SheetData sheetData = worksheetPart.getContents().getSheetData();
		final List<NormalMeasure> measures = (List<NormalMeasure>) analysisStandard.getExendedMeasures();
		final Map<String, Integer> mappedValue = new LinkedHashMap<>();
		final String[] categories = cssf ? CategoryConverter.JAVAKEYS : CategoryConverter.TYPE_CIA_KEYS;
		final int totalCol = MEASURE_RRF_DEFAULT_FIELD_COUNT + assetTypes.size() + categories.length;

		measures.sort(new MeasureComparator());

		Row row = createRow(sheetData);
		int colIndex = generateMeasureHeader(row, categories, totalCol);
		for (AssetType assetType : assetTypes)
			setValue(row, ++colIndex, assetType.getName());
		measures.stream().forEach(measure -> measure.getAssetTypeValues()
				.forEach(assetypeValue -> mappedValue.put(
						measure.getId() + "_" + assetypeValue.getAssetType().getName(), assetypeValue.getValue())));
		for (NormalMeasure measure : measures) {
			row = createRow(sheetData);
			colIndex = writingMeasureData(row, totalCol, categories,
					measure.getMeasureDescription().getReference(), measure.getMeasurePropertyList());
			for (AssetType assetType : assetTypes)
				setValue(row, ++colIndex,
						mappedValue.getOrDefault(measure.getId() + "_" + assetType.getName(), 0));

		}

		ExcelHelper.applyHeaderAndFooter(WorkerExportRiskSheet.EXCEL_HEADER_FOOTER_SHEET_NAME,
				analysisStandard.getStandard().getName(), mlPackage);
	}

	private void writeScenario(List<Scenario> scenarios, SpreadsheetMLPackage mlPackage)
			throws Exception {
		if (scenarios.isEmpty())
			return;
		final ObjectFactory factory = Context.getsmlObjectFactory();
		final WorksheetPart worksheetPart = createWorkSheetPart(mlPackage, RAW_SCENARIOS);
		final SheetData scenarioSheet = worksheetPart.getContents().getSheetData();
		int colIndex = 0;
		Row row = factory.createRow();
		for (int i = 0; i < SCENARIO_RRF_DEFAULT_FIELD_COUNT; i++)
			row.getC().add(factory.createCell());
		scenarioSheet.getRow().add(row);
		setValue(row, colIndex, RAW_SCENARIO);
		setValue(row, ++colIndex, RAW_PREVENTIVE);
		setValue(row, ++colIndex, RAW_DETECTIVE);
		setValue(row, ++colIndex, RAW_LIMITATIVE);
		setValue(row, ++colIndex, RAW_CORRECTIVE);
		setValue(row, ++colIndex, RAW_INTENTIONAL);
		setValue(row, ++colIndex, RAW_ACCIDENTAL);
		setValue(row, ++colIndex, RAW_ENVIRONMENTAL);
		setValue(row, ++colIndex, RAW_INTERNAL_THREAT);
		setValue(row, ++colIndex, RAW_EXTERNAL_THREAT);
		setValue(row, ++colIndex, RAW_ASSETS);
		setValue(row, ++colIndex, RAW_ASSET_TYPES);

		for (Scenario scenario : scenarios) {
			row = factory.createRow();
			for (int i = 0; i < SCENARIO_RRF_DEFAULT_FIELD_COUNT; i++)
				row.getC().add(factory.createCell());
			colIndex = 0;
			scenarioSheet.getRow().add(row);
			setValue(row, colIndex, scenario.getName());
			setValue(row, ++colIndex, scenario.getPreventive());
			setValue(row, ++colIndex, scenario.getDetective());
			setValue(row, ++colIndex, scenario.getLimitative());
			setValue(row, ++colIndex, scenario.getCorrective());
			setValue(row, ++colIndex, scenario.getIntentional());
			setValue(row, ++colIndex, scenario.getAccidental());
			setValue(row, ++colIndex, scenario.getEnvironmental());
			setValue(row, ++colIndex, scenario.getInternalThreat());
			setValue(row, ++colIndex, scenario.getExternalThreat());
			if (scenario.isAssetLinked()) {
				setValue(row, ++colIndex,
						scenario.getLinkedAssets().stream()
								.map(Asset::getName).distinct().sorted().collect(Collectors.joining(";")));
				setValue(row, ++colIndex, "");
			} else {
				setValue(row, ++colIndex, "");
				setValue(row, ++colIndex,
						scenario.getAssetTypeValues().stream().filter(a -> a.getValue() > 0)
								.map(a -> a.getAssetType().getName()).distinct().sorted()
								.collect(Collectors.joining(";")));
			}
		}
		ExcelHelper.applyHeaderAndFooter(WorkerExportRiskSheet.EXCEL_HEADER_FOOTER_SHEET_NAME, RAW_SCENARIOS,
				mlPackage);
	}

	private int writingMeasureData(Row row, int totalCol, String[] categories,
			String reference, MeasureProperties properties) {
		for (int i = 0; i < totalCol; i++)
			row.getC().add(Context.getsmlObjectFactory().createCell());
		int colIndex = 0;
		setValue(row, colIndex, reference);
		setValue(row, ++colIndex, properties.getFMeasure());
		setValue(row, ++colIndex, properties.getFSectoral());
		setValue(row, ++colIndex, properties.getPreventive());
		setValue(row, ++colIndex, properties.getDetective());
		setValue(row, ++colIndex, properties.getLimitative());
		setValue(row, ++colIndex, properties.getCorrective());
		setValue(row, ++colIndex, properties.getIntentional());
		setValue(row, ++colIndex, properties.getAccidental());
		setValue(row, ++colIndex, properties.getEnvironmental());
		setValue(row, ++colIndex, properties.getInternalThreat());
		setValue(row, ++colIndex, properties.getExternalThreat());
		for (String category : categories)
			setValue(row, ++colIndex, properties.getCategoryValue(category));
		return colIndex;
	}

}
