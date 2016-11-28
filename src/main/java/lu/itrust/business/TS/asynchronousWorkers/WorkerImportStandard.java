package lu.itrust.business.TS.asynchronousWorkers;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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
import lu.itrust.business.TS.messagehandler.MessageHandler;
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

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	private WorkersPoolManager poolManager;

	private DAOStandard daoStandard;

	private DAOMeasureDescription daoMeasureDescription;

	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	private DAOLanguage daoLanguage;

	private File importFile;

	private int sheetNumber;

	private XSSFWorkbook workbook;

	private Standard newstandard;

	private MessageHandler messageHandler;

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
			}

			session = sessionFactory.openSession();

			initialiseDAO(session);

			transaction = session.beginTransaction();

			importNewStandard();

			serviceTaskFeedback.send(id, new MessageHandler("info.commit.transcation", "Commit transaction", 95));

			transaction.commit();

			messageHandler = new MessageHandler("success.import.standard", "Standard was successfully imported", 100);

			messageHandler.setAsyncCallback(new AsyncCallback("reloadSection", "section_standard"));
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
			if (importFile != null && importFile.exists()) {
				if (!importFile.delete())
					importFile.deleteOnExit();
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.asynchronousWorkers.Worker#isMatch(java.lang.String
	 * , java.lang.Object)
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

		FileInputStream fileToOpen = null;

		try {

			serviceTaskFeedback.send(id, new MessageHandler("info.import.norm.from.excel", "Import new Standard from Excel template", 1));

			fileToOpen = new FileInputStream(importFile);

			// Get the workbook instance for XLS file

			workbook = new XSSFWorkbook(fileToOpen);

			sheetNumber = workbook.getNumberOfSheets();

			newstandard = null;

			serviceTaskFeedback.send(id, new MessageHandler("info.import.norm.information", "Import standard information", 5));

			getStandard();

			if (newstandard != null) {
				serviceTaskFeedback.send(id, new MessageHandler("info.import.norm.measure", "Import measures", 10));
				getMeasures();
				serviceTaskFeedback.send(id, new MessageHandler("success.import.norm", "Standard was been successfully imported", 95));
			} else
				throw new TrickException("error.import.norm.malformedExcelFile", "The Excel file containing Standard to import is malformed. Please check its content!");
		} finally {
			try {
				if (fileToOpen != null)
					fileToOpen.close();
			} catch (Exception e1) {
				TrickLogManager.Persist(e1);
			}

			try {
				if (workbook != null)
					workbook.close();
			} catch (Exception e) {
				TrickLogManager.Persist(e);
			}
		}

	}

	/**
	 * getStandard: <br>
	 * This function browse sheet (NormInfo) and table (TableNormInfo) of the
	 * Excel <br/>
	 * workbook and get information of the Standard to import
	 * 
	 */
	public void getStandard() throws Exception {

		XSSFSheet sheet = null;
		XSSFTable table = null;

		short startColSheet, endColSheet;
		int startRowSheet, endRowSheet;

		for (int indexSheet = 0; indexSheet < sheetNumber; indexSheet++) {

			sheet = workbook.getSheetAt(indexSheet);

			if (sheet.getSheetName().equals("NormInfo")) {

				for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

					table = sheet.getTables().get(indexTable);

					if (table.getName().equals("TableNormInfo")) {
						startColSheet = table.getStartCellReference().getCol();
						endColSheet = table.getEndCellReference().getCol();
						startRowSheet = table.getStartCellReference().getRow();
						endRowSheet = table.getEndCellReference().getRow();
						if (startColSheet <= endColSheet && startRowSheet <= endRowSheet)
							for (int indexRow = startRowSheet + 1; indexRow <= endRowSheet; indexRow++) {
								if (daoStandard.existsByNameAndVersion(sheet.getRow(indexRow).getCell(startColSheet).getStringCellValue(),
										(int) sheet.getRow(indexRow).getCell(startColSheet + 1).getNumericCellValue())) {
									newstandard = daoStandard.getStandardByNameAndVersion(sheet.getRow(indexRow).getCell(startColSheet).getStringCellValue(),
											(int) sheet.getRow(indexRow).getCell(startColSheet + 1).getNumericCellValue());
									newstandard.setDescription(sheet.getRow(indexRow).getCell(startColSheet + 2).getStringCellValue());
									newstandard.setComputable(sheet.getRow(indexRow).getCell(startColSheet + 3).getBooleanCellValue());
									serviceTaskFeedback.send(id, new MessageHandler("info.import.norm.safe.update",
											new Object[] { newstandard.getLabel(), newstandard.getVersion() },
											String.format("Updating of standard %s, version %d. No measure shall be waived.", newstandard.getLabel(), newstandard.getVersion()),
											10));
								} else {

									newstandard = new Standard();
									newstandard.setLabel(sheet.getRow(indexRow).getCell(startColSheet).getStringCellValue());
									if (sheet.getRow(indexRow).getCell(startColSheet + 1).getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
										newstandard.setVersion((int) sheet.getRow(indexRow).getCell(startColSheet + 1).getNumericCellValue());
									else
										newstandard.setVersion(Integer.valueOf(sheet.getRow(indexRow).getCell(startColSheet + 1).getStringCellValue()));
									newstandard.setDescription(sheet.getRow(indexRow).getCell(startColSheet + 2).getStringCellValue());
									newstandard.setComputable(getBooleanOrDefault(sheet, indexRow, startColSheet + 3, null));
									if (sheet.getRow(indexRow).getCell(startColSheet).getStringCellValue().equals(Constant.STANDARD_MATURITY))
										newstandard.setType(StandardType.MATURITY);
									else
										newstandard.setType(StandardType.NORMAL);
									daoStandard.save(newstandard);
									serviceTaskFeedback.send(id, new MessageHandler("info.import.norm", new Object[] { newstandard.getLabel(), newstandard.getVersion() },
											String.format("Import standard %s, version %d.", newstandard.getLabel(), newstandard.getVersion()), 10));
								}
							}
					}
				}
			}
		}
	}

	/**
	 * getMeasures: <br>
	 * This function browse sheet (NormData) and table (TableNormData) of the
	 * Excel <br/>
	 * workbook and get information of the measures to import
	 * 
	 */
	public void getMeasures() throws Exception {
		XSSFSheet sheet = null;
		XSSFTable table = null;

		short startColSheet, endColSheet;
		int startRowSheet, endRowSheet;
		String domain = "";
		String description = "";
		Language lang;
		Pattern pattern = Pattern.compile("(Domain|Description)_(\\w{3})");
		Matcher matcher;

		MeasureDescription measureDescription = null;

		ArrayList<MeasureDescriptionText> measureDescriptionTexts = null;
		MeasureDescriptionText measureDescriptionText = null;

		for (int indexSheet = 0; indexSheet < sheetNumber; indexSheet++) {

			sheet = workbook.getSheetAt(indexSheet);

			if (sheet.getSheetName().equals("NormData")) {
				for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {
					table = sheet.getTables().get(indexTable);
					if (table.getName().equals("TableNormData")) {
						startColSheet = table.getStartCellReference().getCol();
						endColSheet = table.getEndCellReference().getCol();
						startRowSheet = table.getStartCellReference().getRow();
						endRowSheet = table.getEndCellReference().getRow();
						if (startColSheet <= endColSheet && startRowSheet <= endRowSheet)
							for (int indexRow = startRowSheet + 1; indexRow <= endRowSheet; indexRow++) {
								measureDescription = daoMeasureDescription.getByReferenceAndStandard(sheet.getRow(indexRow).getCell(1).getStringCellValue(), newstandard);
								if (measureDescription == null) {
									measureDescription = new MeasureDescription();
									measureDescription.setStandard(newstandard);
									daoMeasureDescription.save(measureDescription);
								}

								if (sheet.getRow(indexRow).getCell(0).getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
									measureDescription.setLevel((int) sheet.getRow(indexRow).getCell(0).getNumericCellValue());
								else
									measureDescription.setLevel(Integer.valueOf(sheet.getRow(indexRow).getCell(0).getStringCellValue()));

								measureDescription.setReference(sheet.getRow(indexRow).getCell(1).getStringCellValue());
								measureDescription.setComputable(getBooleanOrDefault(sheet, indexRow, 2, null));

								if (startColSheet + 3 <= endColSheet) {

									measureDescriptionTexts = new ArrayList<>();

									for (int indexCol = startColSheet + 3; indexCol <= endColSheet; indexCol++) {
										XSSFCell cell = sheet.getRow(startRowSheet).getCell(indexCol);
										if (cell == null)
											continue;
										matcher = pattern.matcher(cell.getStringCellValue());
										if (matcher.matches()) {

											if ((indexCol - startColSheet) % 2 == 1) {

												lang = daoLanguage.getByAlpha3(matcher.group(2).trim().toLowerCase());

												if (lang == null) {
													lang = new Language();
													lang.setAlpha3(matcher.group(2));
													if (daoLanguage.existsByAlpha3(matcher.group(2))) {
														lang = daoLanguage.getByAlpha3(matcher.group(2));
													} else {
														lang = new Language();
														lang.setAlpha3(matcher.group(2));
														lang.setName(lang.getAlpha3());
														daoLanguage.save(lang);
													}
												}

												if (daoMeasureDescriptionText.existsForMeasureDescriptionAndLanguage(measureDescription.getId(), lang.getId())) {
													measureDescriptionText = daoMeasureDescriptionText.getForMeasureDescriptionAndLanguage(measureDescription.getId(),
															lang.getId());

													domain = sheet.getRow(indexRow).getCell(indexCol) != null ? sheet.getRow(indexRow).getCell(indexCol).getStringCellValue() : "";
													description = sheet.getRow(indexRow).getCell(indexCol + 1) != null
															? sheet.getRow(indexRow).getCell(indexCol + 1).getStringCellValue() : "";

													if (domain.isEmpty() || measureDescription.isComputable() && description.isEmpty())
														System.out.println("Measuredescriptiontext not valid! Skipping...");
													else {
														measureDescriptionText.setDescription(description);
														measureDescriptionText.setDomain(domain);
													}

												} else {
													measureDescriptionText = new MeasureDescriptionText();
													measureDescriptionText.setMeasureDescription(measureDescription);
													measureDescriptionText.setLanguage(lang);

													domain = sheet.getRow(indexRow).getCell(indexCol) != null ? sheet.getRow(indexRow).getCell(indexCol).getStringCellValue() : "";
													description = sheet.getRow(indexRow).getCell(indexCol + 1) != null
															? sheet.getRow(indexRow).getCell(indexCol + 1).getStringCellValue() : "";

													if (!domain.isEmpty())
														measureDescriptionText.setDomain(domain);

													if (!description.isEmpty())
														measureDescriptionText.setDescription(description);

													if (domain.isEmpty() || measureDescription.isComputable() && description.isEmpty())
														System.out.println("Measuredescription text not valid! Skipping...");
													else
														measureDescription.addMeasureDescriptionText(measureDescriptionText);

												}
											}
										}
									}
								}
								daoMeasureDescription.saveOrUpdate(measureDescription);
							}
					}
				}
				if (measureDescription == null || measureDescriptionText == null || measureDescriptionTexts == null) {
					messageHandler = new MessageHandler("error.import.norm.measure", null, "There was problem during import of measures. Please check measure content!");
					serviceTaskFeedback.send(id, messageHandler);
					return;
				}
			}
		}
	}

	private boolean getBooleanOrDefault(XSSFSheet sheet, int row, int col, Boolean defaultValue) {
		try {
			XSSFCell cell = sheet.getRow(row).getCell(col);
			return cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN ? cell.getBooleanCellValue()
					: cell.getCellType() == XSSFCell.CELL_TYPE_STRING ? Boolean.parseBoolean(cell.getStringCellValue())
							: cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ? cell.getNumericCellValue() != 0 : defaultValue;
		} catch (Exception e) {
			throw new TrickException("error.import.standard.not_boolean",
					String.format("A boolean value was expected at, sheet: %s, row: %d, col: %d", sheet.getSheetName(), row + 1, col + 1), sheet.getSheetName(), (row + 1) + "",
					(col + 1) + "");
		}
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
						Thread.currentThread().interrupt();
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

}
