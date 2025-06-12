package lu.itrust.business.ts.asynchronousWorkers;

import static lu.itrust.business.ts.component.MeasureManager.update;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getBoolean;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getInt;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.isEmpty;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.util.StringUtils;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.TrickLogManager;
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
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.standard.AssetStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;

public class WorkerImportStandard extends WorkerImpl {

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

	private Standard newstandard;

	private MessageHandler messageHandler;

	public WorkerImportStandard(String filename) {
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

	@Override
	public void run() {
		Session session = null;
		Transaction transaction = null;
		try {
			synchronized (this) {
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId()))
					if (!getWorkersPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
				setCurrent(Thread.currentThread());
			}

			session = getSessionFactory().openSession();

			initialiseDAO(session);

			transaction = session.beginTransaction();

			importNewStandard();

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.commit.transcation", "Commit transaction", 95));

			transaction.commit();

			messageHandler = new MessageHandler("success.import.standard", "Standard was successfully imported", 100);

			messageHandler.setAsyncCallbacks(new AsyncCallback("reloadSection", "section_kb_standard"));
			getServiceTaskFeedback().send(getId(), messageHandler);
			/**
			 * Log
			 */
			String username = getServiceTaskFeedback().findUsernameById(this.getId());
			TrickLogManager.persist(LogType.ANALYSIS, "log.import.standard",
					String.format("Standard: %s, version: %d", newstandard.getName(), newstandard.getVersion()),
					username,
					LogAction.IMPORT, newstandard.getName(), String.valueOf(newstandard.getVersion()));
		} catch (TrickException e) {
			setError(e);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
			if (transaction != null && transaction.getStatus().canRollback())
				transaction.rollback();
		} catch (Exception e) {
			setError(e);
			getServiceTaskFeedback().send(getId(), new MessageHandler("error.import.norm",
					"Import of standard failed! Error message is: " + e.getMessage(), e));
			if (transaction != null && transaction.getStatus().canRollback())
				transaction.rollback();
		} finally {
			try {
				if (session != null && !session.isOpen())
					session.close();
			} catch (HibernateException e) {
				TrickLogManager.persist(e);
			}
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
			getServiceStorage().delete(getFilename());

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.asynchronousWorkers.Worker#isMatch(java.lang.String ,
	 * java.lang.Object)
	 */
	@Override
	public boolean isMatch(String express, Object... values) {
		try {
			String[] expressions = express.split("\\+");
			boolean match = values.length == expressions.length && values.length == 1;
			for (int i = 0; i < expressions.length && match; i++) {
				switch (expressions[i]) {
					case "class":
						match &= values[i].equals(getClass());
						break;
					default:
						match = false;
						break;
				}
			}
			return match;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * importNewStandard: <br>
	 * Description
	 * 
	 * @throws Exception
	 */
	public void importNewStandard() throws Exception {

		getServiceTaskFeedback().send(getId(),
				new MessageHandler("info.import.norm.from.excel", "Import new Standard from Excel template", 1));

		final DataFormatter formatter = new DataFormatter();

		final WorkbookPart workbookPart = SpreadsheetMLPackage.load(getServiceStorage().loadAsFile(getFilename()))
				.getWorkbookPart();

		getServiceTaskFeedback().send(getId(),
				new MessageHandler("info.import.norm.information", "Import standard information", 5));

		getStandard(workbookPart, formatter);

		if (newstandard != null) {
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.import.norm.measure", "Import measures", 10));
			getMeasures(workbookPart, formatter);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("success.import.norm", "Standard was been successfully imported", 95));
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
		this.newstandard = null;
		SheetData infoSheet = findSheet(workbookPart, "NormInfo");
		if (infoSheet == null)
			return;
		this.newstandard = loadStandard(infoSheet, formatter);
	}

	private Standard loadStandard(SheetData infoSheet, DataFormatter formatter) throws Exception {
		final TablePart tablePart = findTable(infoSheet, "TableNormInfo");
		if (tablePart == null)
			return null;
		final AddressRef addressRef = AddressRef.parse(tablePart.getContents().getRef());
		final Row data = infoSheet.getRow().get(addressRef.getEnd().getRow());
		final int labelIndex = data.getC().size() == 4 ? addressRef.getBegin().getCol()
				: addressRef.getBegin().getCol() + 1;
		final String label = getString(data.getC().get(labelIndex), formatter);
		final int version = getInt(data.getC().get(labelIndex + 1), formatter);
		final String name = data.getC().size() == 4 ? label
				: getString(data.getC().get(addressRef.getBegin().getCol()), formatter);
		if (isEmpty(label))
			throw new TrickException("error.standard.label.empty", "Standard name cannot be empty");
		if (isEmpty(name))
			throw new TrickException("error.standard.name.empty", "Standard display name cannot be empty");
		if (version == 0)
			throw new TrickException("error.standard.version.zero", "Standard version must be greather than 0");
		Standard standard = daoStandard.getStandardByLabelAndVersion(label, version);
		if (standard == null)
			standard = new Standard(name.trim(), label.trim(), version);
		else if (standard.isAnalysisOnly())
			throw new TrickException("error.standard.link.analysis", "Standard cannot be updated from knowledge base");

		return loadStandardData(standard, data, labelIndex, formatter);
	}

	private Standard loadStandardData(Standard standard, Row data, int startCol, DataFormatter formatter) {
		standard.setVersion(getInt(data.getC().get(startCol + 1), formatter));
		standard.setDescription(getString(data.getC().get(startCol + 2), formatter));
		standard.setComputable(getBoolean(data.getC().get(startCol + 3), formatter));
		if (standard.getId() > 0) {
			setUpdated(true);
			daoStandard.saveOrUpdate(standard);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.import.norm.safe.update",
							new Object[] { standard.getName(), standard.getVersion() },
							String.format("Updating of standard %s, version %d. No measure shall be waived.",
									standard.getName(), standard.getVersion()),
							10));
		} else {
			if (Constant.STANDARD_MATURITY.equalsIgnoreCase(standard.getName()))
				standard.setType(StandardType.MATURITY);
			else
				standard.setType(StandardType.NORMAL);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.import.norm", new Object[] { standard.getName(), standard.getVersion() },
							String.format("Import standard %s, version %d.", standard.getName(), standard.getVersion()),
							10));
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
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("error.import.norm.measure", null,
							"There was problem during import of measures. Please check measure content!"));
		TablePart tablePart = findTable(sheet, "TableNormData");
		if (tablePart == null)
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("error.import.norm.measure", null,
							"There was problem during import of measures. Please check measure content!"));
		loadMeasureDescription(sheet, AddressRef.parse(tablePart.getContents().getRef()), formatter);
	}

	private void loadMeasureDescription(SheetData sheet, AddressRef address, DataFormatter formatter) {
		Map<Integer, Language> languages = loadLanguages(sheet, address, formatter);
		if (languages.isEmpty())
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("error.import.norm.measure", null,
							"There was problem during import of measures. Please check measure content!"));

		final int begin = address.getBegin().getRow() + 1,
				end = Math.min(address.getEnd().getRow() + 1, sheet.getRow().size()),
				startIndex = getStartIndex(sheet, begin, formatter);

		final List<MeasureDescription> measureDescriptions = new LinkedList<>();

		for (int i = begin; i < end; i++) {
			final Row row = sheet.getRow().get(i);
			final String reference = getString(row.getC().get(startIndex), formatter);
			if (isEmpty(reference))
				continue;
			MeasureDescription measureDescription = daoMeasureDescription.getByReferenceAndStandard(reference,
					newstandard);

			if (measureDescription == null) {
				measureDescription = new MeasureDescription(reference, newstandard);
				if (isUpdated())
					measureDescriptions.add(measureDescription);
			}

			measureDescription.setComputable(getBoolean(row.getC().get(startIndex + 1), formatter));
			final int languageCount = (address.getEnd().getCol() - (startIndex + 1)) / 2;
			for (int j = 0; j < languageCount; j++) {
				Language language = languages.get(j);
				int domInd = j * 2 + (startIndex + 2), descInd = domInd + 1;
				MeasureDescriptionText descriptionText = daoMeasureDescriptionText
						.getForMeasureDescriptionAndLanguage(measureDescription.getId(), language.getId());
				if (descriptionText == null)
					measureDescription.getMeasureDescriptionTexts()
							.add(descriptionText = new MeasureDescriptionText(measureDescription, language));
				String domain = getString(row, domInd, formatter), description = getString(row, descInd, formatter);
				if (StringUtils.hasText(domain))
					descriptionText.setDomain(domain);
				if (StringUtils.hasText(description))
					descriptionText.setDescription(description);
			}
			daoMeasureDescription.saveOrUpdate(measureDescription);
		}

		if (!measureDescriptions.isEmpty()) {
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.synchronise.analyses.measure.collection",
							"Synchronising measure collection of knowledge base to analyses", 50));
			final int total = (int) daoAnalysisStandard.countByStandard(newstandard), size = 40,
					count = (total / size) + 1;
			for (int page = 1; page <= count; page++) {
				daoAnalysisStandard.findByStandard(page, size, newstandard).forEach(a -> {
					if (a instanceof MaturityStandard)
						update((MaturityStandard) a, measureDescriptions, daoAnalysisStandard, daoAnalysis);
					else if (a instanceof AssetStandard)
						update((AssetStandard) a, measureDescriptions, daoAnalysisStandard, daoAnalysis);
					else if (a instanceof NormalStandard)
						update((NormalStandard) a, measureDescriptions, daoAnalysisStandard, daoAnalysis, daoAssetType);
				});
			}
		}

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
		final int startIndex = hasLevel(row, formatter) ? 1 : 0,
				languageCount = (address.getEnd().getCol() - (1 + startIndex)) / 2;
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

	@Override
	public synchronized void start() {
		run();
	}

	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						if (getCurrent() == null)
							Thread.currentThread().interrupt();
						else
							getCurrent().interrupt();

						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			setError(e);
			TrickLogManager.persist(e);
		} finally {
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
			getServiceStorage().delete(getFilename());
		}
	}

	@Override
	public TaskName getName() {
		return TaskName.IMPORT_MEASURE_COLLECTION;
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
