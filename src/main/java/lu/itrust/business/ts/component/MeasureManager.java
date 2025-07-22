package lu.itrust.business.ts.component;

import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getOrCreateRow;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getWorksheetPart;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xlsx4j.sml.CTTable;
import org.xlsx4j.sml.CTTableColumn;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOAnalysisStandard;
import lu.itrust.business.ts.database.dao.DAOAssetType;
import lu.itrust.business.ts.database.dao.DAOAssetTypeValue;
import lu.itrust.business.ts.database.dao.DAOLanguage;
import lu.itrust.business.ts.database.dao.DAOMeasure;
import lu.itrust.business.ts.database.dao.DAOMeasureDescription;
import lu.itrust.business.ts.database.dao.DAOStandard;
import lu.itrust.business.ts.database.dao.impl.DAOHibernate;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.ts.form.ImportRRFForm;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.model.general.AssetTypeValue;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.general.helper.Utils;
import lu.itrust.business.ts.model.parameter.IMaturityParameter;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.MaturityParameter;
import lu.itrust.business.ts.model.parameter.impl.SimpleParameter;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.AssetStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.helper.Chapter;
import lu.itrust.business.ts.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.ts.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.ts.model.standard.measure.impl.NormalMeasure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;

/**
 * MeasureManager.java: <br>
 * Detailed description...
 * 
 * @author eomar itrust consulting s.a.rl.:
 * @version
 * @since May 26, 2013
 */
@Component
public class MeasureManager {

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private DAOAnalysisStandard daoAnalysisStandard;

	@Autowired
	private DAOMeasure daoMeasure;

	@Autowired
	private DAOAssetType daoAssetType;

	@Autowired
	private DAOAssetTypeValue daoAssetTypeValue;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private ServiceStorage serviceStorage;

	@Autowired
	private DAOStandard daoStandard;

	@Autowired
	private DAOLanguage daoLanguage;

	@Autowired
	private DAOMeasureDescription daoMeasureDescription;

	@Value("${app.settings.standard.template.path}")
	private String template;

	public static Standard getStandard(List<Standard> standards, String standardname) {
		for (Standard standard : standards)
			if (standard.is(standardname))
				return standard;
		return null;
	}

	public static Integer getStandardId(List<Standard> standards, String standardname) {
		for (Standard standard : standards)
			if (standard.is(standardname))
				return standard.getId();
		return null;
	}

	public static StandardType getStandardType(List<Standard> standards, String standardname) {
		for (Standard standard : standards)
			if (standard.is(standardname))
				return standard.getType();
		return null;
	}

	public static boolean isAnalysisOnlyStandard(List<Standard> standards, String standardname) {
		for (Standard standard : standards)
			if (standard.is(standardname))
				return standard.isAnalysisOnly();
		return false;
	}

	/**
	 * SplitByStandard: <br>
	 * Description
	 * 
	 * @param measures
	 * @return
	 */
	public static Map<String, List<Measure>> splitByStandard(List<Measure> measures) {
		Map<String, List<Measure>> mappingMeasures = new LinkedHashMap<>();
		Collections.sort(measures, new MeasureComparator());
		for (Measure measure : measures) {
			Standard standard = measure.getMeasureDescription().getStandard();
			List<Measure> measures2 = mappingMeasures.get(standard.getName());
			if (measures2 == null) {
				measures2 = new LinkedList<>();
				mappingMeasures.put(standard.getName(), measures2);
			}
			measures2.add(measure);
		}

		return mappingMeasures;
	}

	public static List<Measure> concatMeasure(List<AnalysisStandard> analysisStandards) {
		List<Measure> measures = new LinkedList<>();
		for (AnalysisStandard analysisStandard : analysisStandards)
			measures.addAll(analysisStandard.getMeasures());

		return measures;
	}

	/**
	 * extractMainChapter: <br>
	 * extract the main chapter
	 * 
	 * @param chapter
	 * @return
	 */
	public static String extractMainChapter(String chapter) {
		if ((chapter.toUpperCase().startsWith("A.")) || (chapter.toUpperCase().startsWith("M."))) {
			String[] chapters = chapter.split("[.]", 3);
			return chapters[0] + "." + chapters[1];
		} else {
			return (chapter.contains(".") ? chapter.split("[.]", 2)[0] : chapter);
		}

	}

	/**
	 * SplitByChapter: <br>
	 * Description
	 * 
	 * @param measures
	 * @return
	 */
	public static Map<Chapter, List<Measure>> splitByChapter(List<Measure> measures) {
		Map<Chapter, List<Measure>> chapters = new LinkedHashMap<>();
		Map<String, Chapter> chapterMapping = new LinkedHashMap<>();

		Comparator<Measure> cmp = new MeasureComparator();

		Map<String, List<Measure>> splittedmeasures = MeasureManager.splitByStandard(measures);

		List<Measure> allmeasures = new ArrayList<>();

		for (String key : splittedmeasures.keySet()) {
			List<Measure> tmpMeasures = splittedmeasures.get(key);
			Collections.sort(tmpMeasures, cmp);
			allmeasures.addAll(tmpMeasures);
		}

		for (Measure measure : allmeasures) {
			String reference = extractMainChapter(measure.getMeasureDescription().getReference());
			Standard standard = measure.getMeasureDescription().getStandard();
			Chapter chapter = chapterMapping.get(standard.getName() + "|-|" + reference);
			if (chapter == null)
				chapterMapping.put(standard.getName() + "|-|" + reference, chapter = new Chapter(standard, reference));
			List<Measure> measures2 = chapters.get(chapter);
			if (measures2 == null)
				chapters.put(chapter, measures2 = new ArrayList<>());
			measures2.add(measure);
		}

		List<Chapter> keys = chapters.entrySet().stream().filter(
				entry -> entry.getValue().stream().noneMatch(measure -> measure.getMeasureDescription().isComputable()))
				.map(Entry::getKey).collect(Collectors.toList());

		keys.forEach(chapters::remove);

		return chapters;
	}

	/**
	 * createNewMeasureForAllAnalyses: <br>
	 * Description
	 * 
	 * @param measureDescription
	 */
	@Transactional
	public void createNewMeasureForAllAnalyses(MeasureDescription measureDescription) throws Exception {
		final List<AnalysisStandard> analysisStandards = daoAnalysisStandard
				.getAllFromStandard(measureDescription.getStandard());
		for (AnalysisStandard analysisStandard : analysisStandards) {
			final Analysis analysis = daoAnalysis.getByAnalysisStandardId(analysisStandard.getId());
			Measure measure = null;
			Object implementationRate = null;
			if (analysisStandard instanceof NormalStandard) {
				measure = new NormalMeasure();
				List<AssetType> assetTypes = daoAssetType.getAll();
				List<AssetTypeValue> assetTypeValues = ((NormalMeasure) measure).getAssetTypeValues();
				for (AssetType assetType : assetTypes)
					assetTypeValues.add(new AssetTypeValue(assetType, 0));
				((NormalMeasure) measure).setMeasurePropertyList(new MeasureProperties());
				implementationRate = 0D;
			} else if (analysisStandard instanceof MaturityStandard) {
				measure = new MaturityMeasure();
				implementationRate = analysis.getSimpleParameters().stream().filter(
						p -> p.isMatch(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME) && p.getValue() == 0)
						.min((p1, p2) -> p1.getValue().compareTo(p2.getValue())).orElse(null);
			} else if (analysisStandard instanceof AssetStandard) {
				measure = new AssetMeasure();
				((AssetMeasure) measure).setMeasurePropertyList(new MeasureProperties());
				implementationRate = 0D;
				((AssetMeasure) measure).setMeasureAssetValues(new ArrayList<>());
			}
			Phase phase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
			if (phase == null) {
				phase = new Phase(Constant.PHASE_DEFAULT);
				phase.setAnalysis(analysis);
			}
			measure.setPhase(phase);
			measure.setImplementationRate(implementationRate);
			measure.setMeasureDescription(measureDescription);
			measure.setStatus(Constant.MEASURE_STATUS_APPLICABLE);
			analysisStandard.getMeasures().add(measure);
			daoAnalysisStandard.saveOrUpdate(analysisStandard);

			TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.add.measure",
					String.format("Analysis: %s, version: %s, target: Measure (%s) from: %s", analysis.getIdentifier(),
							analysis.getVersion(), measureDescription.getReference(),
							measureDescription.getStandard().getName()),
					"System", LogAction.ADD, analysis.getIdentifier(), analysis.getVersion(),
					measureDescription.getReference(), measureDescription.getStandard().getName());
		}

		if (measureDescription.getId() < 1)
			daoMeasureDescription.saveOrUpdate(measureDescription);
	}

	@Transactional
	public void importRRFFromStandard(Integer idAnalysis, ImportRRFForm rrfForm) throws Exception {
		for (Integer idStandard : rrfForm.getStandards()) {
			Map<String, Measure> profileMeasures = daoMeasure.mappingAllFromAnalysisAndStandard(rrfForm.getAnalysis(),
					idStandard);
			List<Measure> measures = daoMeasure.getAllFromAnalysisAndStandard(idAnalysis, idStandard);
			for (Measure measure : measures) {
				NormalMeasure normalMeasure = (NormalMeasure) profileMeasures
						.get(measure.getMeasureDescription().getReference());
				if (normalMeasure == null)
					continue;
				// Force hibernate to initialise data
				((NormalMeasure) measure).setMeasurePropertyList(
						DAOHibernate.initialise(((NormalMeasure) measure).getMeasurePropertyList()));
				normalMeasure.setMeasurePropertyList(DAOHibernate.initialise(normalMeasure.getMeasurePropertyList()));
				normalMeasure.copyMeasureCharacteristicsTo(((NormalMeasure) measure));
				daoMeasure.saveOrUpdate(measure);
			}
		}
	}

	@Transactional
	public void patchMeasureAssetypeValueDuplicated() throws Exception {
		int count = daoMeasure.countNormalMeasure();
		int pageSize = 30;
		if (count == 0)
			return;
		else if (count < pageSize)
			count = pageSize;
		int pageCount = (int) Math.ceil(count / (double) pageSize) + 1;
		for (int pageIndex = 1; pageIndex < pageCount; pageIndex++) {
			for (NormalMeasure normalMeasure : daoMeasure.getAllNormalMeasure(pageIndex, pageSize))
				removeDuplicationAssetypeValue(normalMeasure);
		}
	}

	@Transactional
	public void removeDuplicationAssetypeValue(NormalMeasure measure) throws Exception {
		if (measure.getAssetTypeValues() == null || measure.getAssetTypeValues().isEmpty())
			return;
		Map<Integer, AssetTypeValue> indexAsssetType = new LinkedHashMap<>();
		Iterator<AssetTypeValue> iterator = measure.getAssetTypeValues().iterator();
		boolean saveRequired = false;
		while (iterator.hasNext()) {
			AssetTypeValue typeValue = iterator.next();
			if (indexAsssetType.containsKey(typeValue.getAssetType().getId())) {
				AssetTypeValue typeValue2 = indexAsssetType.get(typeValue.getAssetType().getId());
				if (typeValue.getValue() > typeValue2.getValue())
					typeValue2.setValue(typeValue.getValue());
				iterator.remove();
				daoAssetTypeValue.delete(typeValue);
				saveRequired = true;
			} else
				indexAsssetType.put(typeValue.getAssetType().getId(), typeValue);
		}
		indexAsssetType.clear();
		if (saveRequired)
			daoMeasure.saveOrUpdate(measure);
	}

	@Transactional
	public void removeStandardFromAnalysis(Integer idAnalysis, int idStandard) throws Exception {
		final Analysis analysis = daoAnalysis.findByIdAndEager(idAnalysis);

		final AnalysisStandard analysisStandard = analysis.findAnalysisStandardByStandardId(idStandard);

		analysis.removeAnalysisStandard(analysisStandard);

		customDelete.deleteAnalysisActionPlan(analysis);

		analysis.getRiskProfiles().forEach(riskProfile -> riskProfile.getMeasures()
				.removeIf(measure -> measure.getMeasureDescription().getStandard()
						.equals(analysisStandard.getStandard())));

		final Standard standard = analysisStandard.getStandard();

		daoAnalysisStandard.delete(analysisStandard);

		daoAnalysis.saveOrUpdate(analysis);

		final List<AnalysisStandard> astandards = daoAnalysisStandard.getAllFromStandard(standard);

		if (standard.isAnalysisOnly() && (astandards == null || astandards.isEmpty()))
			customDelete.deleteStandard(standard);
	}

	@Transactional(readOnly = true)
	public boolean exportStandard(int standardId,  HttpServletResponse response,
			String username) throws TrickException, Docx4JException, IOException {
		final Standard standard = daoStandard.get(standardId);

		if (standard == null)
			return true;
		final File workFile = serviceStorage.createTmpFile();
		try {

			serviceStorage.copy(template, workFile.getName());

			SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(workFile);

			WorkbookPart workbook = mlPackage.getWorkbookPart();

			SheetData sheet = findSheet(workbook, "NormInfo");

			TablePart tablePart = findTable(sheet, "TableNormInfo");
			tablePart.getContents().getRef();
			AddressRef address = AddressRef.parse(tablePart.getContents().getRef());
			int row = address.getBegin().getRow() + 1;
			int nameCol = address.getBegin().getCol();
			int labelCol = nameCol + 1;
			int versionCol = labelCol + 1;
			int descCol = versionCol + 1;
			// standard name
			Cell cell = sheet.getRow().get(row).getC().get(nameCol);
			setValue(cell, standard.getName());
			// standard label
			cell = sheet.getRow().get(row).getC().get(labelCol);
			setValue(cell, standard.getLabel());
			// standard version
			cell = sheet.getRow().get(row).getC().get(versionCol);
			setValue(cell, standard.getVersion());
			// standard description
			cell = sheet.getRow().get(row).getC().get(descCol);
			setValue(cell, standard.getDescription());
			// standard computable
			cell = sheet.getRow().get(row).getC().get(address.getEnd().getCol());
			setValue(cell, standard.isComputable());

			/**
			 * Measures
			 */

			sheet = findSheet(workbook, "NormData");
			final List<Language> languages = daoLanguage.getAll();
			final List<MeasureDescription> measuredescriptions = daoMeasureDescription
					.getAllByStandard(standard.getId());

			measuredescriptions
					.sort((m1, m2) -> NaturalOrderComparator.compareTo(m1.getReference(), m2.getReference()));

			int index = 0;
			int referenceCol = 0;
			int computableCol = 1;
			int colSize = (languages.size() + 1) * 2;

			sheet.getRow().clear();
			Row sheetRow = getOrCreateRow(sheet, 0, colSize);
			setValue(sheetRow.getC().get(index++), "Reference");
			setValue(sheetRow.getC().get(index++), "Computable");
			tablePart = findTable(sheet, "TableNormData");
			address = AddressRef.parse(tablePart.getContents().getRef());
			CTTable table = tablePart.getContents();
			while (table.getTableColumns().getTableColumn().size() > index)
				table.getTableColumns().getTableColumn().remove(index);

			for (Language language : languages) {
				var columnDomain = new CTTableColumn();
				var columnDesc = new CTTableColumn();
				table.getTableColumns().getTableColumn().add(columnDomain);
				table.getTableColumns().getTableColumn().add(columnDesc);
				columnDomain.setName("Domain_" + language.getAlpha3());
				columnDesc.setName("Description_" + language.getAlpha3());
				setValue(sheetRow.getC().get(index++), columnDomain.getName());
				setValue(sheetRow.getC().get(index++), columnDesc.getName());
			}

			for (int i = 0; i < colSize; i++)
				table.getTableColumns().getTableColumn().get(i).setId(i + 1l);

			address.getEnd().setCol(colSize - 1);
			address.getEnd().setRow(measuredescriptions.size());

			table.setRef(address.toString());

			if (table.getAutoFilter() != null)
				table.getAutoFilter().setRef(table.getRef());

			final WorksheetPart worksheetPart = getWorksheetPart(sheet);

			if (worksheetPart.getContents().getDimension() != null)
				worksheetPart.getContents().getDimension().setRef(table.getRef());

			row = 1;

			for (MeasureDescription measuredescription : measuredescriptions) {

				sheetRow = getOrCreateRow(sheet, row++, colSize);

				setValue(sheetRow.getC().get(referenceCol), measuredescription.getReference());

				setValue(sheetRow.getC().get(computableCol), measuredescription.isComputable());

				int domainCol = computableCol + 1;

				for (Language language : languages) {
					MeasureDescriptionText measureDescriptionText = measuredescription.findByLanguage(language);
					if (measureDescriptionText != null) {
						setValue(sheetRow.getC().get(domainCol), measureDescriptionText.getDomain());
						setValue(sheetRow.getC().get(domainCol + 1), measureDescriptionText.getDescription());
					}
					domainCol += 2;
				}
			}

			final String filename = String.format(Constant.ITR_FILE_NAMING,
					"R5xx_STA_TSE",
					"KB",
					Utils.cleanUpFileName(standard.getLabel()), "MeasureCollection", standard.getVersion(),
					"xlsx");

			response.setContentType("xlsx");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + filename + "\"");
			mlPackage.save(response.getOutputStream());
			/**
			 * Log
			 */
			TrickLogManager.persist(LogLevel.WARNING, LogType.KNOWLEDGE_BASE, "log.export.standard",
					String.format("Standard: %s, version: %d", standard.getLabel(), standard.getVersion()),
					username, LogAction.EXPORT, standard.getLabel(),
					String.valueOf(standard.getVersion()));

			return false; // no error
		} finally {
			serviceStorage.delete(workFile.getName());
		}

	}

	public static Measure create(StandardType standardType) throws TrickException {
		if (standardType == null)
			throw new TrickException("error.create.measure.invalid.standard",
					"Measure cannot be created: invalid standard");
		Measure measure = null;
		switch (standardType) {
			case ASSET:
				measure = new AssetMeasure();
				break;
			case NORMAL:
				measure = new NormalMeasure();
				break;
			case MATURITY:
				measure = new MaturityMeasure();
				break;
			default:
				throw new TrickException("error.create.measure.invalid.standard.type",
						"Measure cannot be created: invalid standard type");
		}
		return measure;
	}

	/**
	 * Compute Maturity-based Effectiveness Rate for 27002
	 * 
	 * @param measures27002
	 * @param maturityMeasures
	 * @param parameters       : require Maturity parameters +
	 *                         {@link Constant#PARAMETERTYPE_TYPE_MAX_EFF_NAME}
	 * @param reference:       mapped by reference
	 * @return Map<27002 Reference or id, efficiency implementation Rate>
	 */
	public static Map<Object, Double> computeMaturiyEfficiencyRate(List<Measure> measures27002,
			List<Measure> maturityMeasures, List<IParameter> parameters, boolean reference,
			ValueFactory factory) {
		Map<Object, Double> effectiveImpelementationRate = new HashMap<>();
		if (measures27002 == null || maturityMeasures == null || parameters == null)
			return effectiveImpelementationRate;
		Map<String, Double> maturities = computeMaturityByChapter(maturityMeasures, parameters, factory);
		if (maturities.isEmpty())
			return effectiveImpelementationRate;
		for (Measure measure : measures27002) {
			if (measure.getMeasureDescription().isComputable()) {
				Double maturity = maturities.get(measure.getMeasureDescription().getReference().split("[.]", 2)[0]);
				if (maturity != null)
					effectiveImpelementationRate.put(
							reference ? measure.getMeasureDescription().getReference() : measure.getId(),
							measure.getImplementationRateValue(factory) * maturity * 0.01);
			}
		}
		return effectiveImpelementationRate;
	}

	/**
	 * Compute Maturity by Chapter
	 * 
	 * @param maturityMeasures
	 * @param parameters       : MaturityParameter +
	 *                         {@link Constant#PARAMETERTYPE_TYPE_MAX_EFF_NAME}
	 * @return Map<Chapter, Maturity rate>
	 */
	public static Map<String, Double> computeMaturityByChapter(List<Measure> maturityMeasures,
			List<IParameter> parameters, ValueFactory factory) {
		Map<String, Double> maturities = new HashMap<>();
		Map<String, IMaturityParameter> mappedMaturityParameters = new HashMap<>(23);
		Map<String, IParameter> efficiencyRates = new HashMap<>(6);
		parameters.forEach(parameter -> {
			if (parameter instanceof IMaturityParameter)
				mappedMaturityParameters.put(parameter.getDescription(), (MaturityParameter) parameter);
			else if ((parameter instanceof SimpleParameter)
					&& parameter.isMatch(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME))
				efficiencyRates.put(parameter.getDescription(), parameter);
		});

		Map<String, boolean[]> smls = new HashMap<>();
		for (Measure measure : maturityMeasures) {
			if (!(measure instanceof MaturityMeasure))
				continue;
			String[] chapters = measure.getMeasureDescription().getReference().replace("M.", "").split("[.]");
			boolean[] complainces = smls.computeIfAbsent(chapters[0],
					k -> new boolean[] { true, true, true, true, true });
			if (!measure.getMeasureDescription().isComputable() || chapters.length < 2)
				continue;
			MeasureDescriptionText descriptionText = measure.getMeasureDescription().findByAlph3("eng");
			if (descriptionText == null || !mappedMaturityParameters.containsKey(descriptionText.getDomain()))
				continue;
			IMaturityParameter maturityMeasure = mappedMaturityParameters.get(descriptionText.getDomain());
			double implementation = measure.getImplementationRateValue(factory) * 0.01;

			switch (chapters[1]) {
				case "1":
					complainces[0] &= implementation >= maturityMeasure.getSMLLevel1();
					break;
				case "2":
					complainces[1] &= implementation >= maturityMeasure.getSMLLevel2();
					break;
				case "3":
					complainces[2] &= implementation >= maturityMeasure.getSMLLevel3();
					break;
				case "4":
					complainces[3] &= implementation >= maturityMeasure.getSMLLevel4();
					break;
				case "5":
					complainces[4] &= implementation >= maturityMeasure.getSMLLevel5();
					break;
				default:
					break;
			}
		}

		smls.forEach((chapter, complainces) -> {
			String sml = "SML0";
			for (int i = 0; i < complainces.length; i++) {
				if (complainces[i])
					sml = "SML" + (i + 1);
				else
					break;
			}
			IParameter parameter = efficiencyRates.get(sml);
			if (parameter != null)
				maturities.put(chapter, parameter.getValue().doubleValue());
		});
		return maturities;
	}

	public static void update(NormalStandard normalStandard, Collection<MeasureDescription> measureDescriptions,
			DAOAnalysisStandard daoAnalysisStandard, DAOAnalysis daoAnalysis,
			DAOAssetType daoAssetType) {

		update(normalStandard, daoAnalysis.getByAnalysisStandardId(normalStandard.getId()), measureDescriptions,
				daoAnalysisStandard, daoAssetType);
	}

	public static void update(NormalStandard normalStandard, Analysis analysis,
			Collection<MeasureDescription> measureDescriptions,
			DAOAnalysisStandard daoAnalysisStandard,
			DAOAssetType daoAssetType) {
		final List<AssetType> assetTypes = daoAssetType.getAll();
		Phase phase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
		if (phase == null) {
			phase = new Phase(Constant.PHASE_DEFAULT);
			phase.setAnalysis(analysis);
		}
		for (MeasureDescription measureDescription : measureDescriptions) {
			NormalMeasure measure = new NormalMeasure(measureDescription);
			List<AssetTypeValue> assetTypeValues = measure.getAssetTypeValues();
			for (AssetType assetType : assetTypes)
				assetTypeValues.add(new AssetTypeValue(assetType, 0));
			measure.setStatus(Constant.MEASURE_STATUS_APPLICABLE);
			measure.setPhase(phase);
			measure.setImplementationRate(0);
			normalStandard.getMeasures().add(measure);
			TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.add.measure",
					String.format("Analysis: %s, version: %s, target: Measure (%s) from: %s", analysis.getIdentifier(),
							analysis.getVersion(), measureDescription.getReference(),
							measureDescription.getStandard().getName()),
					"System", LogAction.ADD, analysis.getIdentifier(), analysis.getVersion(),
					measureDescription.getReference(), measureDescription.getStandard().getName());
		}
		daoAnalysisStandard.saveOrUpdate(normalStandard);

	}

	public static void update(AssetStandard assetStandard, Collection<MeasureDescription> measureDescriptions,
			DAOAnalysisStandard daoAnalysisStandard, DAOAnalysis daoAnalysis) {
		update(assetStandard, daoAnalysis.getByAnalysisStandardId(assetStandard.getId()), measureDescriptions,
				daoAnalysisStandard);
	}

	public static void update(AssetStandard assetStandard, Analysis analysis,
			Collection<MeasureDescription> measureDescriptions, DAOAnalysisStandard daoAnalysisStandard) {

		Phase phase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
		if (phase == null) {
			phase = new Phase(Constant.PHASE_DEFAULT);
			phase.setAnalysis(analysis);
		}

		for (MeasureDescription measureDescription : measureDescriptions) {
			AssetMeasure measure = new AssetMeasure(measureDescription);
			measure.setStatus(Constant.MEASURE_STATUS_APPLICABLE);
			measure.setPhase(phase);
			measure.setImplementationRate(0);
			assetStandard.getMeasures().add(measure);

			TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.add.measure",
					String.format("Analysis: %s, version: %s, target: Measure (%s) from: %s", analysis.getIdentifier(),
							analysis.getVersion(), measureDescription.getReference(),
							measureDescription.getStandard().getName()),
					"System", LogAction.ADD, analysis.getIdentifier(), analysis.getVersion(),
					measureDescription.getReference(), measureDescription.getStandard().getName());
		}
		daoAnalysisStandard.saveOrUpdate(assetStandard);
	}

	public static void update(MaturityStandard maturityStandard, Collection<MeasureDescription> measureDescriptions,
			DAOAnalysisStandard daoAnalysisStandard,
			DAOAnalysis daoAnalysis) {
		update(maturityStandard, daoAnalysis.getByAnalysisStandardId(maturityStandard.getId()), measureDescriptions,
				daoAnalysisStandard);
	}

	public static void update(MaturityStandard maturityStandard, Analysis analysis,
			Collection<MeasureDescription> measureDescriptions,
			DAOAnalysisStandard daoAnalysisStandard) {

		Phase phase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
		if (phase == null) {
			phase = new Phase(Constant.PHASE_DEFAULT);
			phase.setAnalysis(analysis);
		}

		final SimpleParameter implementationRate = analysis.getSimpleParameters().stream()
				.filter(p -> p.isMatch(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME) && p.getValue() == 0)
				.min((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
				.orElse(null);
		for (MeasureDescription measureDescription : measureDescriptions) {
			MaturityMeasure measure = new MaturityMeasure(measureDescription);
			measure.setStatus(Constant.MEASURE_STATUS_APPLICABLE);
			measure.setPhase(phase);
			measure.setImplementationRate(implementationRate);
			maturityStandard.getMeasures().add(measure);

			TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.add.measure",
					String.format("Analysis: %s, version: %s, target: Measure (%s) from: %s", analysis.getIdentifier(),
							analysis.getVersion(), measureDescription.getReference(),
							measureDescription.getStandard().getName()),
					"System", LogAction.ADD, analysis.getIdentifier(), analysis.getVersion(),
					measureDescription.getReference(), measureDescription.getStandard().getName());
		}
		daoAnalysisStandard.saveOrUpdate(maturityStandard);
	}

}