package lu.itrust.business.ts.component;

import static lu.itrust.business.ts.component.MeasureManager.update;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getBoolean;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getInt;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.isEmpty;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.colToIndex;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOAnalysisStandard;
import lu.itrust.business.ts.database.dao.DAOAssetType;
import lu.itrust.business.ts.database.dao.DAOLanguage;
import lu.itrust.business.ts.database.dao.DAOMeasureDescription;
import lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText;
import lu.itrust.business.ts.database.dao.DAOStandard;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisStandardHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOAssetTypeHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOLanguageHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOMeasureDescriptionHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOMeasureDescriptionTextHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOStandardHBM;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.AssetStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;

public class ImportCustomStandard {

	private boolean updated = false;

	private Pattern pattern = Pattern.compile("(Domain|Description)_(\\w{3})");

	private DAOStandard daoStandard;

	private DAOMeasureDescription daoMeasureDescription;

	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	private DAOAnalysisStandard daoAnalysisStandard;

	private DAOAnalysis daoAnalysis;

	private DAOAssetType daoAssetType;

	private DAOLanguage daoLanguage;

	private String filename;

	private Standard newStandard;

	private String standardName;

	private StandardType type;

	private Analysis analysis;

	public ImportCustomStandard(StandardType type, String name, String filename) {
		this.type = type;
		this.standardName = StringUtils.hasText(name) ? name.trim() : null;
		setFilename(filename);
	}

	public void initialiseDAO(Session session) {
		daoLanguage = new DAOLanguageHBM(session);
		daoStandard = new DAOStandardHBM(session);
		daoAnalysis = new DAOAnalysisHBM(session);
		daoAssetType = new DAOAssetTypeHBM(session);
		daoAnalysisStandard = new DAOAnalysisStandardHBM(session);
		daoMeasureDescription = new DAOMeasureDescriptionHBM(session);
		daoMeasureDescriptionText = new DAOMeasureDescriptionTextHBM(session);
	}

	public Map<String, String> importStandard(int analysisId, String username, Locale locale) {
		final Map<String, String> messages = new HashMap<>();
		Transaction transaction = null;
		Session session = null;
		try {

			session = getSessionFactory().openSession();

			initialiseDAO(session);

			transaction = session.beginTransaction();

			analysis = daoAnalysis.findByIdAndEager(analysisId);

			if (analysis == null)
				throw new TrickException("error.analysis.not_found", "Analysis cannot be found");

			importNewStandard();

			transaction.commit();

			if (isUpdated())
				messages.put("success", getMessageSource().getMessage("success.update.norm", null,
						"The standard has been updated", locale));
			else
				messages.put("success", getMessageSource().getMessage("success.import.norm", null,
						"The new standard has been imported", locale));

			/**
			 * Log
			 */

			TrickLogManager.persist(LogType.ANALYSIS, "log.import.standard",
					String.format("Standard: %s, version: %d", newStandard.getName(), newStandard.getVersion()),
					username,
					LogAction.IMPORT, newStandard.getName(), String.valueOf(newStandard.getVersion()));
		} catch (TrickException e) {
			messages.put("error",
					getMessageSource().getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			if (transaction != null && transaction.getStatus().canRollback())
				session.getTransaction().rollback();
			TrickLogManager.persist(e);
		} catch (Exception e) {
			messages.put("error", getMessageSource().getMessage("error.import.norm", null,
					"Import of standard failed! Error message is: " + e.getMessage(), locale));
			if (transaction != null && transaction.getStatus().canRollback())
				transaction.rollback();
			TrickLogManager.persist(e);
		} finally {
			try {
				if (session != null && !session.isOpen())
					session.close();
			} catch (HibernateException e) {
				TrickLogManager.persist(e);
			}

			getServiceStorage().delete(getFilename());

		}
		return messages;

	}

	private ServiceStorage getServiceStorage() {
		return InstanceManager.getServiceStorage();
	}

	private SessionFactory getSessionFactory() {
		return InstanceManager.getSessionFactory();
	}

	private MessageSource getMessageSource() {
		return InstanceManager.getMessageSource();
	}

	/**
	 * importNewStandard: <br>
	 * Description
	 * 
	 * @throws Exception
	 */
	public void importNewStandard() throws Exception {

		final DataFormatter formatter = new DataFormatter();

		final WorkbookPart workbookPart = SpreadsheetMLPackage.load(getServiceStorage().loadAsFile(getFilename()))
				.getWorkbookPart();

		getStandard(workbookPart, formatter);

		if (newStandard != null) {
			getMeasures(workbookPart, formatter);
		} else
			throw new TrickException("error.import.norm.malformedExcelFile",
					"The Excel file containing Standard to import is malformed. Please check its content!");
	}

	/**
	 * getStandard: <br>
	 * This function browse sheet (NormInfo) and table (TableNormInfo) of the Excel
	 * <br/>
	 * workbook and get information of the Standard to import
	 * 
	 * @param sharedStrings
	 * 
	 */
	public void getStandard(WorkbookPart workbookPart, DataFormatter formatter) throws Exception {
		this.newStandard = null;
		SheetData infoSheet = findSheet(workbookPart, "NormInfo");
		if (infoSheet == null)
			return;
		this.newStandard = loadStandard(infoSheet, formatter);
	}

	/**
	 * @param infoSheet
	 * @param formatter
	 * @return
	 * @throws Exception
	 */
	private Standard loadStandard(SheetData infoSheet, DataFormatter formatter) throws Exception {
		final TablePart tablePart = findTable(infoSheet, "TableNormInfo");
		if (tablePart == null)
			return null;
		final AddressRef addressRef = AddressRef.parse(tablePart.getContents().getRef());
		final Row data = infoSheet.getRow().get(addressRef.getEnd().getRow());
		final int labelIndex = data.getC().size() == 4 ? addressRef.getBegin().getCol()
				: addressRef.getBegin().getCol() + 1;
		final String label = getString(data.getC().get(labelIndex), formatter).strip();
		final int version = getInt(data.getC().get(labelIndex + 1), formatter);

		final String name = (!StringUtils.hasText(this.standardName) ? (data.getC().size() == 4 ? label
				: getString(data.getC().get(addressRef.getBegin().getCol()), formatter)) : this.standardName).strip();

		if (isEmpty(label))
			throw new TrickException("error.standard.label.empty", "Standard internal name cannot be empty");
		if (Constant.STANDARD_MATURITY.equalsIgnoreCase(label))
			throw new TrickException("error.maturity.name.reserve", "Maturity name is reserved");
		if (isEmpty(name))
			throw new TrickException("error.standard.name.empty", "Standard name cannot be empty");
		if (Constant.STANDARD_MATURITY.equalsIgnoreCase(name))
			throw new TrickException("error.maturity.name.reserve", "Maturity name is reserved");

		Standard standard = analysis.findStandardByName(name);
		if (standard == null) {
			standard = analysis.findStandardByLabel(label);
			if (standard == null) {
				standard = new Standard(name, label, daoStandard.getNextVersion(label));
			}
		}

		if (standard.getId() > 1 && !standard.isAnalysisOnly()) {
			if (name.equalsIgnoreCase(standard.getName()))
				throw new TrickException("error.standard.name.duplicated",
						"The name cannot be duplicated into a risk analysis");
			else {
				final String myLabel = String.format("%s - %s", label, version);
				standard = analysis.findStandardByLabel(myLabel);
				if (standard == null) {
					standard = new Standard(name, myLabel, daoStandard.getNextVersion(label));
				} else if (!standard.isAnalysisOnly()) {
					throw new TrickException("error.standard.internal.name.duplicated",
							"The internal name cannot be duplicated into a risk analysis");
				}
			}

		}
		standard.setAnalysisOnly(true);
		return loadStandardData(standard, data, labelIndex, formatter);
	}

	private Standard loadStandardData(Standard standard, Row data, int startCol, DataFormatter formatter) {
		standard.setDescription(getString(data.getC().get(startCol + 2), formatter));
		standard.setComputable(getBoolean(data.getC().get(startCol + 3), formatter));
		if (standard.getId() > 0) {
			setUpdated(true);
			daoStandard.saveOrUpdate(standard);
		} else {
			standard.setType(type);
			daoStandard.save(standard);
			setUpdated(false);
		}
		return standard;
	}

	/**
	 * getMeasures: <br>
	 * This function browse sheet (NormData) and table (TableNormData) of the Excel
	 * <br/>
	 * workbook and get information of the measures to import
	 * 
	 * @param sharedStrings
	 * @param workbookPart
	 * 
	 */
	public void getMeasures(WorkbookPart workbookPart, DataFormatter formatter) throws Exception {
		SheetData sheet = findSheet(workbookPart, "NormData");
		if (sheet == null)
			throw new TrickException("error.import.norm.measure", null,
					"There was problem during import of measures. Please check measure content!");
		TablePart tablePart = findTable(sheet, "TableNormData");
		if (tablePart == null)
			throw new TrickException("error.import.norm.measure", null,
					"There was problem during import of measures. Please check measure content!");
		loadMeasureDescription(sheet, AddressRef.parse(tablePart.getContents().getRef()), formatter);
	}

	private void loadMeasureDescription(SheetData sheet, AddressRef address, DataFormatter formatter) {
		Map<Integer, Language> languages = loadLanguages(sheet, address, formatter);
		if (languages.isEmpty())
			throw new TrickException("error.import.norm.measure", null,
					"There was problem during import of measures. Please check measure content!");

		final int begin = address.getBegin().getRow() + 1,
				end = Math.min(address.getEnd().getRow() + 1, sheet.getRow().size()),
				startIndex = getStartIndex(sheet, begin, formatter);

		final Map<String, List<String>> assetsByRef = new HashMap<>();

		final int assetIndex = type.equals(StandardType.ASSET) ? findAssetIndex(sheet, formatter) : -1;

		final List<MeasureDescription> measureDescriptions = new LinkedList<>();

		for (int i = begin; i < end; i++) {
			final Row row = sheet.getRow().get(i);
			final String reference = getString(row.getC().get(startIndex), formatter);
			if (isEmpty(reference))
				continue;
			MeasureDescription measureDescription = daoMeasureDescription.getByReferenceAndStandard(reference,
					newStandard);
			if (measureDescription == null)
				measureDescriptions.add(measureDescription = new MeasureDescription(reference, newStandard));

			measureDescription.setComputable(getBoolean(row.getC().get(startIndex + 1), formatter));
			final int languageCount = (address.getEnd().getCol() - (startIndex + 1)) / 2;
			for (int j = 0; j < languageCount; j++) {
				Language language = languages.get(j);
				int domInd = j * 2 + (startIndex + 2);
				int descInd = domInd + 1;
				MeasureDescriptionText descriptionText = daoMeasureDescriptionText
						.getForMeasureDescriptionAndLanguage(measureDescription.getId(), language.getId());
				if (descriptionText == null)
					measureDescription.getMeasureDescriptionTexts()
							.add(descriptionText = new MeasureDescriptionText(measureDescription, language));
				String domain = getString(row, domInd, formatter);
				String description = getString(row, descInd, formatter);
				if (StringUtils.hasText(domain))
					descriptionText.setDomain(domain.trim());
				if (StringUtils.hasText(description))
					descriptionText.setDescription(description.trim());
				if (assetIndex > -1) {
					String asset = getString(row, assetIndex, formatter);
					if (StringUtils.hasText(asset))
						assetsByRef.put(reference, Arrays.asList(asset.trim().split(";")).stream().map(String::trim)
								.collect(Collectors.toList()));
				}

			}
			daoMeasureDescription.saveOrUpdate(measureDescription);
		}

		if (!measureDescriptions.isEmpty()) {
			if (!isUpdated()) {
				switch (newStandard.getType()) {
					case ASSET:
						analysis.add(new AssetStandard(newStandard));
						break;
					case MATURITY:
						analysis.add(new MaturityStandard(newStandard));
						break;
					case NORMAL:
						analysis.add(new NormalStandard(newStandard));
						break;
					default:
						break;

				}
				daoAnalysis.saveOrUpdate(analysis);
			}
			AnalysisStandard a = analysis.getAnalysisStandards().get(newStandard.getName());
			if (a != null) {
				if (a instanceof MaturityStandard)
					update((MaturityStandard) a, this.analysis, measureDescriptions, daoAnalysisStandard);
				else if (a instanceof AssetStandard)
					update((AssetStandard) a, this.analysis, measureDescriptions, daoAnalysisStandard);
				else if (a instanceof NormalStandard)
					update((NormalStandard) a, this.analysis, measureDescriptions, daoAnalysisStandard, daoAssetType);
			}

		}

		if (!assetsByRef.isEmpty()) {
			final AnalysisStandard assetStandard = analysis.getAnalysisStandards().get(newStandard.getName());

			final Map<String, Asset> assetByName = analysis.getAssets().stream()
					.collect(Collectors.toMap(a -> a.getName().toUpperCase(), Function.identity()));

			for (Measure measure : assetStandard.getMeasures()) {
				if (measure instanceof AssetMeasure) {
					assetsByRef.getOrDefault(measure.getMeasureDescription().getReference(), Collections.emptyList())
							.stream().map(String::trim).map(String::toUpperCase)
							.map(assetByName::get).filter(Objects::nonNull)
							.filter(a -> ((AssetMeasure) measure).getMeasureAssetValueByAsset(a) == null)
							.forEach(a -> ((AssetMeasure) measure).addAnMeasureAssetValue(new MeasureAssetValue(a, 0)));
				}
			}
			daoAnalysis.saveOrUpdate(analysis);
		}

	}

	private int findAssetIndex(SheetData sheet, DataFormatter formatter) {
		if (sheet.getRow().isEmpty())
			return -1;
		final Row firstRow = sheet.getRow().get(0);
		for (int i = 0; i < firstRow.getC().size(); i++) {
			final String name = getString(firstRow.getC().get(i), formatter);
			if ("Assets".equalsIgnoreCase(name)) {
				return colToIndex(firstRow.getC().get(i).getR(), i);
			}
		}
		return -1;

	}

	private int getStartIndex(final SheetData sheet, final int begin, final DataFormatter formatter) {
		if (sheet.getRow().size() < begin || begin < 0)
			return 0;
		return hasLevel(sheet.getRow().get(begin - 1), formatter) ? 1 : 0;
	}

	private boolean hasColumn(final String name, final Row row, final int cellIndex, final DataFormatter formatter) {
		return name.equalsIgnoreCase(getString(row, cellIndex, formatter));
	}

	private boolean hasLevel(final Row row, final DataFormatter formatter) {
		return hasColumn("Level", row, 0, formatter);
	}

	private Map<Integer, Language> loadLanguages(SheetData sheet, AddressRef address, DataFormatter formatter) {
		final Map<Integer, Language> languages = new HashMap<>();
		final Row row = sheet.getRow().get(address.getBegin().getRow());
		final int startIndex = hasLevel(row, formatter) ? 1 : 0;
		final int languageCount = (address.getEnd().getCol() - (1 + startIndex)) / 2;
		for (int i = 0; i < languageCount; i++) {
			String domain = getString(row.getC().get(i * 2 + (2 + startIndex)), formatter);
			if (isEmpty(domain))
				throw new TrickException("error.standard.bad.table.header", "Please check for table header");
			Matcher matcher = pattern.matcher(domain);
			if (!matcher.find())
				throw new TrickException("error.standard.bad.table.header", "Please check for table header");
			String alpha3 = matcher.group(2);
			Language language = daoLanguage.getByAlpha3(alpha3);
			if (language == null)
				daoLanguage.save(language = new Language(alpha3.toUpperCase(), alpha3, alpha3));
			languages.put(i, language);
		}
		return languages;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
