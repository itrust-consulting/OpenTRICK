/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.numToColString;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOItemInformation;
import lu.itrust.business.ts.database.dao.impl.DAOAnalysisImpl;
import lu.itrust.business.ts.database.dao.impl.DAOItemInformationImpl;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.iteminformation.ItemInformation;

/**
 * @author eomar
 *
 */
public class WorkerImportItemInformation extends WorkerImpl {

	static final String SHEET_TABLE_NAME = "Scope";

	private int analysisId;

	private String username;

	private String filename;

	private boolean overwrite;

	private DAOAnalysis daoAnalysis;

	private DAOItemInformation daoItemInformation;

	public WorkerImportItemInformation(int analysisId, String username, String filename, boolean overwrite) {
		setName(TaskName.IMPORT_ITEM_INFORMATION);
		setAnalysisId(analysisId);
		setUsername(username);
		setFilename(filename);
		setOverwrite(overwrite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#start()
	 */
	@Override
	public synchronized void start() {
		run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#cancel()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
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

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.item.information.initialise", "Initialise data", 2));

			session = getSessionFactory().openSession();

			initialiseDAO(session);

			transaction = session.beginTransaction();

			final Analysis analysis = daoAnalysis.get(analysisId);

			importRiskInformation(analysis);

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.commit.transcation", "Commit transaction", 95));

			transaction.commit();

			final MessageHandler messageHandler = new MessageHandler("success.import.item.information",
					"Scope was successfully imported", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("reloadSection", "section_item-information"));

			getServiceTaskFeedback().send(getId(), messageHandler);
			/**
			 * Log
			 */
			TrickLogManager.persist(LogType.ANALYSIS, "log.import.item.information",
					String.format("Scope data has been overwritten, Analysis: %s, version: %s",
							analysis.getIdentifier(), analysis.getVersion()),
					username,
					LogAction.IMPORT, analysis.getIdentifier(), analysis.getVersion());
		} catch (TrickException e) {
			setError(e);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
			TrickLogManager.persist(e);
			if (!(session == null || transaction == null) && transaction.getStatus().canRollback())
				session.getTransaction().rollback();
		} catch (Exception e) {
			setError(e);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("error.import.item.information", "Import of scope failed!", e));
			TrickLogManager.persist(e);
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

	private void importRiskInformation(Analysis analysis) throws Exception {
		final Map<String, ItemInformation> itemInformations = analysis.getItemInformations().stream()
				.collect(Collectors.toMap(e -> e.getDescription().toLowerCase(), Function.identity()));
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(getServiceStorage().loadAsFile(getFilename()));
		final WorkbookPart workbook = mlPackage.getWorkbookPart();
		final DataFormatter formatter = new DataFormatter();

		final int min = 2;
		final int max = 90;
		int multi = (max - min) / 3;
		int progress = min;
		int indexProgress = 1;

		final MessageHandler messageHandler = new MessageHandler(
				"info.import.item.information",
				"Import of scope is in progress",
				progress);

		getServiceTaskFeedback().send(getId(), messageHandler);

		final SheetData sheet = findSheet(workbook, SHEET_TABLE_NAME);

		if (sheet == null)
			throw new TrickException("error.item.information.sheet.not.found",
					String.format("Something wrong with file: Sheet `%s` cannot be found", SHEET_TABLE_NAME),
					SHEET_TABLE_NAME);
		final TablePart tablePart = findTable(sheet, SHEET_TABLE_NAME);
		if (tablePart == null)
			throw new TrickException("error.item.information.table.not.found",
					String.format("Something wrong with sheet `%s` : Table `%s` cannot be found",
							SHEET_TABLE_NAME, SHEET_TABLE_NAME),
					SHEET_TABLE_NAME,
					SHEET_TABLE_NAME);
		final AddressRef address = AddressRef.parse(tablePart.getContents().getRef());
		final int maxProgress = progress + (multi * indexProgress);
		final int minProgress = progress;
		final int size = Math.min(address.getEnd().getRow() + 1, sheet.getRow().size());
		final Map<String, String> mappers = new HashMap<>();
		final MessageSource messageSource = InstanceManager.getMessageSource();
		final Locale[] locales = new Locale[] { Locale.FRENCH, Locale.ENGLISH };
		final Map<String, String> categories = new HashMap<>();

		ItemInformation.defaultItems().forEach(e -> {
			for (var myLocale : locales) {
				mappers.put(messageSource.getMessage(
						"label.item_information." + e.getDescription().toLowerCase(), null,
						e.getDescription(), myLocale).toLowerCase(), e.getDescription().toLowerCase());//
			}
			categories.put(e.getDescription().toLowerCase(), e.getType());
		});

		for (int i = address.getBegin().getRow() + 1; i < size; i++) {

			final Row row = sheet.getRow().get(i);

			final String description = trim(getString(row, 0, formatter));

			if (!StringUtils.hasText(description))
				emptyCellError(SHEET_TABLE_NAME, i, 0);

			var descriptionText = description.toLowerCase();

			ItemInformation itemInformation = itemInformations
					.remove(mappers.getOrDefault(descriptionText, descriptionText));
			if (itemInformation == null)
				analysis.getItemInformations().add(itemInformation = new ItemInformation(mappers.getOrDefault(descriptionText, description),
						categories.getOrDefault(descriptionText, Constant.ITEMINFORMATION_SCOPE), ""));

			itemInformation.setValue(trim(getString(row, 1, formatter)));

			messageHandler.setProgress((int) (minProgress + ((i / (double) size) * maxProgress)));
		}

		getServiceTaskFeedback().send(getId(), new MessageHandler("info.save.analysis", "Saving analysis", max));

		if (isOverwrite()) {
			analysis.getItemInformations().removeAll(itemInformations.values());
			daoItemInformation.delete(itemInformations.values());
		}

		itemInformations.clear();

		daoAnalysis.saveOrUpdate(analysis);
		getServiceTaskFeedback().send(getId(),
				new MessageHandler("info.delete.removed.entry", "Delete removed entries", max + 3));
	}

	private String trim(String value) {
		return value == null ? value : value.trim();
	}

	/**
	 * Throw Error.
	 * 
	 * @param sheet
	 * @param row
	 * @param col
	 * @throws TrickException
	 */
	private void emptyCellError(String sheet, int row, int col) {
		String colValue = numToColString(col - 1);
		String rowValue = (row + 1) + "";
		throw new TrickException("error.import.item.information.cell.empty",
				String.format("Cell cannot be empty. Sheet: %s, row: %s, column: %s", sheet, rowValue, colValue),
				sheet, rowValue, colValue);
	}

	private void initialiseDAO(Session session) {
		setDaoAnalysis(new DAOAnalysisImpl(session));
		setDaoRiskInformation(new DAOItemInformationImpl(session));
	}

	public DAOAnalysis getDaoAnalysis() {
		return daoAnalysis;
	}

	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	public DAOItemInformation getDaoItemInformation() {
		return daoItemInformation;
	}

	public void setDaoRiskInformation(DAOItemInformation daoItemInformation) {
		this.daoItemInformation = daoItemInformation;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(int analysisId) {
		this.analysisId = analysisId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

}
