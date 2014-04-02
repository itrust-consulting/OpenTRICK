package lu.itrust.business.task;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.component.helper.AsyncCallback;
import lu.itrust.business.dao.DAOLanguage;
import lu.itrust.business.dao.DAOMeasureDescription;
import lu.itrust.business.dao.DAOMeasureDescriptionText;
import lu.itrust.business.dao.DAONorm;
import lu.itrust.business.dao.hbm.DAOLanguageHBM;
import lu.itrust.business.dao.hbm.DAOMeasureDescriptionHBM;
import lu.itrust.business.dao.hbm.DAOMeasureDescriptionTextHBM;
import lu.itrust.business.dao.hbm.DAONormHBM;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class WorkerImportNorm implements Worker {

	private long id = System.nanoTime();

	private Exception error;

	private boolean working = false;

	private boolean canceled = false;

	private ServiceTaskFeedback serviceTaskFeedback;

	private SessionFactory sessionFactory;

	private WorkersPoolManager poolManager;

	private DAONorm daoNorm;

	private DAOMeasureDescription daoMeasureDescription;

	private DAOMeasureDescriptionText daoMeasureDescriptionText;
	
	private DAOLanguage daoLanguage;

	private File importFile;

	private int sheetNumber;

	private XSSFWorkbook workbook;

	private Norm newNorm;

	private MessageHandler messageHandler;

	public WorkerImportNorm(ServiceTaskFeedback serviceTaskFeedback, SessionFactory sessionFactory, WorkersPoolManager poolManager, File importFile) {
		super();
		this.serviceTaskFeedback = serviceTaskFeedback;
		this.sessionFactory = sessionFactory;
		this.poolManager = poolManager;
		this.importFile = importFile;
	}

	public void initialiseDAO(Session session) {
		daoLanguage = new DAOLanguageHBM(session);
		daoNorm = new DAONormHBM(session);
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
			}
			session = sessionFactory.openSession();
			initialiseDAO(session);

			transaction = session.beginTransaction();

			importNewNorm();

			transaction.commit();

			messageHandler = new MessageHandler("success.export.save.file", "File was successfully saved", 100);
			messageHandler.setAsyncCallback(new AsyncCallback("reloadSection(\"section_norm\")", null));
			serviceTaskFeedback.send(id, messageHandler);

		} catch (Exception e) {
			this.error = e;
			serviceTaskFeedback.send(id, new MessageHandler("error.import.norm", "Import of norm failed! Error message is: " + e.getMessage(), e));
			e.printStackTrace();
			try {
				if (transaction != null)
					session.getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		} finally {
			try {
				if (session != null)
					session.close();
			} catch (HibernateException e) {
				e.printStackTrace();
			}
			if (importFile != null && importFile.exists())
				importFile.delete();
			synchronized (this) {
				working = false;
			}
			if (poolManager != null)
				poolManager.remove(getId());

		}

	}

	/**
	 * importNewNorm: <br>
	 * Description
	 */
	public void importNewNorm() throws Exception {

		System.out.println("Import new Norm from Excel template...");
		
		FileInputStream fileToOpen = new FileInputStream(importFile);

		// Get the workbook instance for XLS file
		workbook = new XSSFWorkbook(fileToOpen);

		sheetNumber = workbook.getNumberOfSheets();

		newNorm = null;
		
		System.out.println("Retrieve Norm...");
		
		getNorm();

		if (newNorm != null) {
			
			System.out.println("Retrieve Measures of Norm...");
			getMeasures();
			System.out.println("Import Norm Done!");
		}else {
			messageHandler = new MessageHandler("error.import.norm.malformedExcelFile", null, "The Excel file containing Norm to import is malformed. Please check its content!");
			serviceTaskFeedback.send(id, messageHandler);
		}
	}

	/**
	 * getNorm: <br>
	 * This function browse sheet (NormInfo) and table (TableNormInfo) of the
	 * Excel <br/>
	 * workbook and get information of the norm to import
	 * 
	 */
	public void getNorm() throws Exception {

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
								if (daoNorm.exists(sheet.getRow(indexRow).getCell(startColSheet).getStringCellValue(), (int) sheet.getRow(indexRow).getCell(startColSheet + 1).getNumericCellValue())) {
									newNorm = daoNorm.loadSingleNormByNameAndVersion(sheet.getRow(indexRow).getCell(startColSheet).getStringCellValue(), (int) sheet.getRow(indexRow).getCell(startColSheet + 1).getNumericCellValue());
									messageHandler = new MessageHandler("error.import.norm.exists", new Object[] { newNorm.getLabel(), newNorm.getVersion() }, "Norm label (" + newNorm.getLabel() + ") and version (" + newNorm.getVersion() + ") already exist, updating existing norm");
									serviceTaskFeedback.send(id, messageHandler);
									System.out.println("Updating existing Norm (" + newNorm.getLabel() + " - " + newNorm.getVersion() + ")...");
								} else {
									
									newNorm = new Norm();
									newNorm.setLabel(sheet.getRow(indexRow).getCell(startColSheet).getStringCellValue());
									newNorm.setVersion((int) sheet.getRow(indexRow).getCell(startColSheet + 1).getNumericCellValue());
									newNorm.setDescription(sheet.getRow(indexRow).getCell(startColSheet + 2).getStringCellValue());
									newNorm.setComputable(sheet.getRow(indexRow).getCell(startColSheet + 3).getBooleanCellValue());
									
									daoNorm.save(newNorm);
								}
							}
					}

				}

				// System.out.println(newNorm.getLabel() + " " +
				// newNorm.getVersion() + " " + newNorm.getDescription() + " " +
				// newNorm.isComputable());

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
		Pattern pattern;
		Matcher matcher;

		MeasureDescription measureDescription = null;

		ArrayList<MeasureDescriptionText> measureDescriptionTexts = null;
		MeasureDescriptionText measureDescriptionText = null;

		for (int indexSheet = 0; indexSheet < sheetNumber; indexSheet++) {

			sheet = workbook.getSheetAt(indexSheet);

			//System.out.println(sheet.getSheetName());
			
			if (sheet.getSheetName().equals("NormData")) {

				for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

					table = sheet.getTables().get(indexTable);
					//System.out.println(table.getName());
					if (table.getName().equals("TableNormData")) {
						startColSheet = table.getStartCellReference().getCol();
						endColSheet = table.getEndCellReference().getCol();
						startRowSheet = table.getStartCellReference().getRow();
						endRowSheet = table.getEndCellReference().getRow();

						if (startColSheet <= endColSheet && startRowSheet <= endRowSheet)
							for (int indexRow = startRowSheet + 1; indexRow <= endRowSheet; indexRow++) {
								
								measureDescription = daoMeasureDescription.getByReferenceNorm(sheet.getRow(indexRow).getCell(1).getStringCellValue(), newNorm);
								
								if (measureDescription == null){
								
									measureDescription = new MeasureDescription();
									measureDescription.setNorm(newNorm);
									daoMeasureDescription.save(measureDescription);
								}
								
								//System.out.println("Row: " + indexRow);
								
								measureDescription.setLevel((int) sheet.getRow(indexRow).getCell(0).getNumericCellValue());
								measureDescription.setReference(sheet.getRow(indexRow).getCell(1).getStringCellValue());
								if (measureDescription.getLevel()==3)
									measureDescription.setComputable(true);
								else
									measureDescription.setComputable(false);
								
								if (startColSheet + 3 <= endColSheet) {

									measureDescriptionTexts = new ArrayList<>();

									for (int indexCol = startColSheet + 3; indexCol <= endColSheet; indexCol++) {
										pattern = Pattern.compile("(Domain|Description)_(\\w{3})");
										matcher = pattern.matcher(sheet.getRow(startRowSheet).getCell(indexCol).getStringCellValue());
										if (matcher.matches()) {

											if ((indexCol - startColSheet) % 2 == 1) {
												
												lang = daoLanguage.loadFromAlpha3(matcher.group(2).trim().toLowerCase());

												if (lang == null) {
													lang = new Language();
													lang.setAlpha3(matcher.group(2));
													if (daoLanguage.alpha3Exist(matcher.group(2))){
														lang = daoLanguage.loadFromAlpha3(matcher.group(2));
													} else {
														lang = new Language();
														lang.setAlpha3(matcher.group(2));
														lang.setName(lang.getAlpha3());
														daoLanguage.save(lang);
													}
												}
												
												if (daoMeasureDescriptionText.existsForLanguage(measureDescription.getId(), lang.getId())){
													measureDescriptionText = daoMeasureDescriptionText.getByLanguage(measureDescription.getId(), lang.getId());
													
													domain = sheet.getRow(indexRow).getCell(indexCol)!=null?sheet.getRow(indexRow).getCell(indexCol).getStringCellValue():"";
													description = sheet.getRow(indexRow).getCell(indexCol + 1) != null ? sheet.getRow(indexRow).getCell(indexCol + 1).getStringCellValue() : "";
												
													measureDescriptionText.setDomain(domain);
													measureDescriptionText.setDescription(description);
													
												} else {
													measureDescriptionText = new MeasureDescriptionText();
													measureDescriptionText.setMeasureDescription(measureDescription);
													measureDescriptionText.setLanguage(lang);

													domain = sheet.getRow(indexRow).getCell(indexCol)!=null?sheet.getRow(indexRow).getCell(indexCol).getStringCellValue():"";
													description = sheet.getRow(indexRow).getCell(indexCol + 1) != null ? sheet.getRow(indexRow).getCell(indexCol + 1).getStringCellValue() : "";
																	
													measureDescriptionText.setDomain(domain);
													measureDescriptionText.setDescription(description);
													
													measureDescription.addMeasureDescriptionText(measureDescriptionText);
												}
											}
										}
									}
								}

								daoMeasureDescription.saveOrUpdate(measureDescription);
								//System.out.println("Save measure:::"+ measureDescription.getNorm().getLabel() + "->" + measureDescription.getReference());
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

	@Override
	public boolean isWorking() {
		// TODO Auto-generated method stub
		return working;
	}

	@Override
	public boolean isCanceled() {
		// TODO Auto-generated method stub
		return canceled;
	}

	@Override
	public Exception getError() {
		// TODO Auto-generated method stub
		return error;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		this.id = id;

	}

	@Override
	public void setPoolManager(WorkersPoolManager poolManager) {
		// TODO Auto-generated method stub
		this.poolManager = poolManager;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public synchronized void start() {
		run();
	}

	@Override
	public void cancel() {
		try {
			synchronized (this) {
				if (working) {
					Thread.currentThread().interrupt();
					canceled = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = e;
		} finally {
			if (importFile != null && importFile.exists())
				importFile.delete();
			synchronized (this) {
				working = false;
			}
			if (poolManager != null)
				poolManager.remove(getId());
		}
	}

}
