package lu.itrust.business.TS.asynchronousWorkers;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.*;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getBoolean;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getInt;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getString;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.util.StringUtils;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOLanguage;
import lu.itrust.business.TS.database.dao.DAOMeasureDescription;
import lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText;
import lu.itrust.business.TS.database.dao.DAOStandard;
import lu.itrust.business.TS.database.dao.hbm.DAOLanguageHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOMeasureDescriptionHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOMeasureDescriptionTextHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOStandardHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

public class WorkerImportStandard implements Worker {

	private String id = String.valueOf(System.nanoTime());

	private Date started = null;

	private Date finished = null;

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private Pattern pattern = Pattern.compile("(Domain|Description)_(\\w{3})");

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	private WorkersPoolManager poolManager;

	private DAOStandard daoStandard;

	private DAOMeasureDescription daoMeasureDescription;

	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	private DAOLanguage daoLanguage;

	private File importFile;

	private Standard newstandard;

	private MessageHandler messageHandler;

	private Thread current;

	public WorkerImportStandard(ServiceTaskFeedback serviceTaskFeedback, SessionFactory sessionFactory, WorkersPoolManager poolManager, File importFile) {
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.sessionFactory = sessionFactory;
		this.poolManager = poolManager;
		this.importFile = importFile;
	}

	public void initialiseDAO(Session session) {
		daoLanguage = new DAOLanguageHBM(session);
		daoStandard = new DAOStandardHBM(session);
		daoMeasureDescription = new DAOMeasureDescriptionHBM(session);
		daoMeasureDescriptionText = new DAOMeasureDescriptionTextHBM(session);
	}

	@Override
	public void run() {
		Session session = null;
		Transaction transaction = null;
		try {
			synchronized (this) {
				if (poolManager != null && !poolManager.exist(getId()))
					if (!poolManager.add(this))
						return;
				if (canceled || working)
					return;
				working = true;
				started = new Timestamp(System.currentTimeMillis());
				setCurrent(Thread.currentThread());
			}

			session = sessionFactory.openSession();

			initialiseDAO(session);

			transaction = session.beginTransaction();

			importNewStandard();

			serviceTaskFeedback.send(id, new MessageHandler("info.commit.transcation", "Commit transaction", 95));

			transaction.commit();

			messageHandler = new MessageHandler("success.import.standard", "Standard was successfully imported", 100);

			messageHandler.setAsyncCallbacks(new AsyncCallback("reloadSection", "section_kb_standard"));
			serviceTaskFeedback.send(id, messageHandler);
			/**
			 * Log
			 */
			String username = serviceTaskFeedback.findUsernameById(this.getId());
			TrickLogManager.Persist(LogType.ANALYSIS, "log.import.standard", String.format("Standard: %s, version: %d", newstandard.getLabel(), newstandard.getVersion()), username,
					LogAction.IMPORT, newstandard.getLabel(), String.valueOf(newstandard.getVersion()));
		} catch (TrickException e) {
			serviceTaskFeedback.send(id, new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), this.error = e));
			TrickLogManager.Persist(e);
			if (transaction != null && transaction.getStatus().canRollback())
				session.getTransaction().rollback();
		} catch (Exception e) {
			serviceTaskFeedback.send(id, new MessageHandler("error.import.norm", "Import of standard failed! Error message is: " + e.getMessage(), this.error = e));
			TrickLogManager.Persist(e);
			if (transaction != null && transaction.getStatus().canRollback())
				session.getTransaction().rollback();
		} finally {
			try {
				if (session != null && !session.isOpen())
					session.close();
			} catch (HibernateException e) {
				TrickLogManager.Persist(e);
			}
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						working = false;
						finished = new Timestamp(System.currentTimeMillis());
					}
				}
			}
			if (importFile != null && importFile.exists() && !importFile.delete())
				importFile.deleteOnExit();

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.asynchronousWorkers.Worker#isMatch(java.lang.String ,
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

		serviceTaskFeedback.send(id, new MessageHandler("info.import.norm.from.excel", "Import new Standard from Excel template", 1));

		final WorkbookPart workbookPart = SpreadsheetMLPackage.load(importFile).getWorkbookPart();
		
		final DataFormatter formatter = new DataFormatter();

		serviceTaskFeedback.send(id, new MessageHandler("info.import.norm.information", "Import standard information", 5));

		getStandard(workbookPart, formatter);

		if (newstandard != null) {
			serviceTaskFeedback.send(id, new MessageHandler("info.import.norm.measure", "Import measures", 10));
			getMeasures(workbookPart, formatter);
			serviceTaskFeedback.send(id, new MessageHandler("success.import.norm", "Standard was been successfully imported", 95));
		} else
			throw new TrickException("error.import.norm.malformedExcelFile", "The Excel file containing Standard to import is malformed. Please check its content!");
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
	public void getStandard(WorkbookPart workbookPart,DataFormatter formatter) throws Exception {
		this.newstandard = null;
		SheetData infoSheet = findSheet(workbookPart, "NormInfo");
		if (infoSheet == null)
			return;
		this.newstandard = loadStandard(infoSheet, formatter);
	}

	private Standard loadStandard(SheetData infoSheet, DataFormatter formatter) throws Exception {
		TablePart tablePart = findTable(infoSheet.getWorksheetPart(), "TableNormInfo");
		if (tablePart == null)
			return null;
		AddressRef addressRef = AddressRef.parse(tablePart.getContents().getRef());
		Row data = infoSheet.getRow().get(addressRef.getEnd().getRow());
		String name = getString(data.getC().get(addressRef.getBegin().getCol()), formatter);
		int version = getInt(data.getC().get(addressRef.getBegin().getCol() + 1), formatter);
		if (isEmpty(name))
			throw new TrickException("error.standard.name.empty", "Standard name cannot be empty");
		if (version == 0)
			throw new TrickException("error.standard.version.zero", "Standard version must be greather than 0");
		Standard standard = daoStandard.getStandardByNameAndVersion(name, version);
		if (standard == null)
			standard = new Standard(name.trim(), version);
		return loadStandardData(standard, data, addressRef.getBegin().getCol(), formatter);
	}

	private Standard loadStandardData(Standard standard, Row data, int startCol, DataFormatter formatter) {
		standard.setVersion(getInt(data.getC().get(startCol + 1), formatter));
		standard.setDescription(getString(data.getC().get(startCol + 2), formatter));
		standard.setComputable(getBoolean(data.getC().get(startCol + 3), formatter));
		if (standard.getId() > 0) {
			daoStandard.saveOrUpdate(standard);
			serviceTaskFeedback.send(id, new MessageHandler("info.import.norm.safe.update", new Object[] { standard.getLabel(), standard.getVersion() },
					String.format("Updating of standard %s, version %d. No measure shall be waived.", standard.getLabel(), standard.getVersion()), 10));
		} else {
			if (Constant.STANDARD_MATURITY.equalsIgnoreCase(standard.getLabel()))
				standard.setType(StandardType.MATURITY);
			else
				standard.setType(StandardType.NORMAL);
			serviceTaskFeedback.send(id, new MessageHandler("info.import.norm", new Object[] { standard.getLabel(), standard.getVersion() },
					String.format("Import standard %s, version %d.", standard.getLabel(), standard.getVersion()), 10));
			daoStandard.save(standard);
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
			serviceTaskFeedback.send(id, new MessageHandler("error.import.norm.measure", null, "There was problem during import of measures. Please check measure content!"));
		TablePart tablePart = findTable(sheet.getWorksheetPart(), "TableNormData");
		if (tablePart == null)
			serviceTaskFeedback.send(id, new MessageHandler("error.import.norm.measure", null, "There was problem during import of measures. Please check measure content!"));
		loadMeasureDescription(sheet, AddressRef.parse(tablePart.getContents().getRef()), formatter);
	}

	private void loadMeasureDescription(SheetData sheet, AddressRef address, DataFormatter formatter) {
		Map<Integer, Language> languages = loadLanguages(sheet, address, formatter);
		if (languages.isEmpty())
			serviceTaskFeedback.send(id, new MessageHandler("error.import.norm.measure", null, "There was problem during import of measures. Please check measure content!"));

		final int begin = address.getBegin().getRow() + 1, end = Math.min(address.getEnd().getRow() + 1, sheet.getRow().size()),
				startIndex = getStartIndex(sheet, begin, formatter);
		
		

		for (int i = begin; i < end; i++) {
			final Row row = sheet.getRow().get(i);
			final String reference = getString(row.getC().get(startIndex), formatter);
			MeasureDescription measureDescription = daoMeasureDescription.getByReferenceAndStandard(reference, newstandard);
			if (measureDescription == null)
				measureDescription = new MeasureDescription(reference, newstandard);
			measureDescription.setComputable(getBoolean(row.getC().get(startIndex + 1), formatter));

			final int languageCount = (address.getEnd().getCol() - (startIndex + 1)) / 2;

			for (int j = 0; j < languageCount; j++) {
				Language language = languages.get(j);
				int domInd = j * 2 + (startIndex + 2), descInd = domInd + 1;
				MeasureDescriptionText descriptionText = daoMeasureDescriptionText.getForMeasureDescriptionAndLanguage(measureDescription.getId(), language.getId());
				if (descriptionText == null)
					measureDescription.getMeasureDescriptionTexts().add(descriptionText = new MeasureDescriptionText(measureDescription, language));
				String domain = getString(row, domInd, formatter), description = getString(row, descInd, formatter);
				if (!StringUtils.isEmpty(domain))
					descriptionText.setDomain(domain);
				if (!StringUtils.isEmpty(description))
					descriptionText.setDescription(description);
			}

			daoMeasureDescription.saveOrUpdate(measureDescription);
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
		final int startIndex = hasLevel(row, formatter) ? 1 : 0, languageCount = (address.getEnd().getCol() - (1 + startIndex)) / 2;
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
	public boolean isWorking() {
		return working;
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public Exception getError() {
		return error;
	}

	@Override
	public void setId(String id) {
		this.id = id;

	}

	@Override
	public void setPoolManager(WorkersPoolManager poolManager) {
		this.poolManager = poolManager;
	}

	@Override
	public String getId() {
		return id;
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

						canceled = true;
					}
				}
			}
		} catch (Exception e) {
			TrickLogManager.Persist(error = e);
		} finally {
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						working = false;
						finished = new Timestamp(System.currentTimeMillis());
					}
				}
			}
			if (importFile != null && importFile.exists()) {
				if (!importFile.delete())
					importFile.deleteOnExit();
			}

		}
	}

	@Override
	public Date getStarted() {
		return started;
	}

	@Override
	public Date getFinished() {
		return finished;
	}

	@Override
	public TaskName getName() {
		return TaskName.IMPORT_MEASURE_COLLECTION;
	}

	@Override
	public Thread getCurrent() {
		return current;
	}

	protected void setCurrent(Thread current) {
		this.current = current;
	}

}
