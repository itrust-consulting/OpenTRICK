/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import static lu.itrust.business.TS.component.NaturalOrderComparator.compareTo;
import static lu.itrust.business.TS.constants.Constant.REGEXP_VALID_RISKINFORMATION_EXPOSED;
import static lu.itrust.business.TS.constants.Constant.RI_SHEET_MAPPERS;
import static lu.itrust.business.TS.constants.Constant.RI_TYPE_RISK;
import static lu.itrust.business.TS.constants.Constant.RI_TYPE_RISK_TBA;
import static lu.itrust.business.TS.constants.Constant.RI_TYPE_RISK_TBS;
import static lu.itrust.business.TS.constants.Constant.RI_TYPE_THREAT;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getSharedStrings;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.numToColString;

import java.io.File;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
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
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAORiskInformation;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAORiskInformationHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;

/**
 * @author eomar
 *
 */
public class WorkerImportRiskInformation extends WorkerImpl {

	private File workFile;

	private DAOAnalysis daoAnalysis;

	private DAORiskInformation daoRiskInformation;

	private ServiceTaskFeedback serviceTaskFeedback;

	private MessageSource messageSource;

	private String username;

	private int analysisId;

	public WorkerImportRiskInformation(int analysisId, String username, File workFile, MessageSource messageSource, WorkersPoolManager poolManager, SessionFactory sessionFactory,
			ServiceTaskFeedback serviceTaskFeedback) {
		super(poolManager, sessionFactory);
		setServiceTaskFeedback(serviceTaskFeedback);
		setName(TaskName.IMPORT_RISK_INFORMATION);
		setMessageSource(messageSource);
		setAnalysisId(analysisId);
		setUsername(username);
		setWorkFile(workFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#start()
	 */
	@Override
	public synchronized void start() {
		run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#cancel()
	 */
	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						Thread.currentThread().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			setError(e);
			TrickLogManager.Persist(e);
		} finally {
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
			if (workFile != null && workFile.exists()) {
				if (!workFile.delete())
					workFile.deleteOnExit();
			}

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
				if (getPoolManager() != null && !getPoolManager().exist(getId()))
					if (!getPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
			}

			serviceTaskFeedback.send(getId(), new MessageHandler("info.risk.information.initialise", "Initialise data", 2));

			session = getSessionFactory().openSession();

			initialiseDAO(session);

			transaction = session.beginTransaction();

			Analysis analysis = daoAnalysis.get(analysisId);

			importRiskInformation(analysis);

			serviceTaskFeedback.send(getId(), new MessageHandler("info.commit.transcation", "Commit transaction", 95));

			transaction.commit();

			MessageHandler messageHandler = new MessageHandler("success.import.risk.information", "Brainstorming was successfully imported", 100);
			messageHandler
					.setAsyncCallback(new AsyncCallback("reloadSection(['section_risk-information_risk','section_risk-information_vul','section_risk-information_threat']);"));

			serviceTaskFeedback.send(getId(), messageHandler);
			/**
			 * Log
			 */
			String username = serviceTaskFeedback.findUsernameById(this.getId());
			TrickLogManager.Persist(LogType.ANALYSIS, "log.import.risk.information", String.format("Brainstorming data has been overwritten, Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()),
					username, LogAction.IMPORT, analysis.getIdentifier(), analysis.getVersion());
		} catch (TrickException e) {
			setError(e);
			serviceTaskFeedback.send(getId(), new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
			TrickLogManager.Persist(e);
			if (transaction != null && transaction.getStatus().canRollback())
				session.getTransaction().rollback();
		} catch (Exception e) {
			setError(e);
			serviceTaskFeedback.send(getId(), new MessageHandler("error.import.risk.information", "Import of risk information failed!", e));
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
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
			if (workFile != null && workFile.exists()) {
				if (!workFile.delete())
					workFile.deleteOnExit();
			}

		}

	}

	private void importRiskInformation(Analysis analysis) throws Exception {
		final Map<String, RiskInformation> riskInformations = analysis.getRiskInformations().stream().collect(Collectors.toMap(RiskInformation::getKey, Function.identity()));
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(workFile);
		final WorkbookPart workbook = mlPackage.getWorkbookPart();
		final Map<String, String> sharedStrings = getSharedStrings(workbook);
		final Locale locale = new Locale(analysis.getLanguage().getAlpha2());
		final Pattern exposurePattern = Pattern.compile(REGEXP_VALID_RISKINFORMATION_EXPOSED);
		final int min = 2, max = 90;
		int multi = (max - min) / 3, progress = min, indexProgress = 1;
		for (String[] mapper : RI_SHEET_MAPPERS) {

			String category = mapper[0].toLowerCase();

			MessageHandler messageHandler = new MessageHandler("info.risk.information.process.sheet." + category, String.format("Processing of %s in progress", category),
					progress);

			serviceTaskFeedback.send(getId(), messageHandler);

			SheetData sheet = findSheet(workbook, mapper[1]);
			if (sheet == null)
				throw new TrickException("error.risk.information.sheet.not.found", String.format("Something wrong with file: Sheet `%s` cannot be found", mapper[1]), mapper[1]);
			TablePart tablePart = findTable(sheet.getWorksheetPart(), mapper[0] + "Table");
			if (tablePart == null)
				throw new TrickException("error.risk.information.table.not.found",
						String.format("Something wrong with sheet `%s` : Table `%s` cannot be found", mapper[1], mapper[0] + "Table"), mapper[1], mapper[0] + "Table");
			AddressRef address = AddressRef.parse(tablePart.getContents().getRef());

			final int maxProgress = progress + (multi * indexProgress), minProgress = progress, size = Math.min(address.getEnd().getRow() + 1, sheet.getRow().size());

			for (int i = address.getBegin().getRow() + 1; i < size; i++) {
				int colIndex = 0;
				Row row = sheet.getRow().get(i);
				String chapter = getString(row, colIndex++, sharedStrings);
				if (StringUtils.isEmpty(chapter))
					emptyCellError(mapper[1], i, colIndex);
				RiskInformation riskInformation = riskInformations.remove(RiskInformation.key(mapper[0], chapter));
				if (riskInformation == null)
					analysis.getRiskInformations().add(riskInformation = new RiskInformation(chapter));

				if (mapper[0].equals(RI_TYPE_RISK)) {
					if (compareTo(chapter, "7") < 0)
						riskInformation.setCategory(RI_TYPE_RISK_TBS);
					else
						riskInformation.setCategory(RI_TYPE_RISK_TBA);
				} else if (riskInformation.getId() < 1)
					riskInformation.setCategory(mapper[0]);

				String name = getString(row, colIndex++, sharedStrings);

				if (StringUtils.isEmpty(name))
					emptyCellError(mapper[1], i, colIndex);

				if (!riskInformation.isCustom() && riskInformation.getId() > 0) {
					String label = getOrignalLabel(locale, riskInformation);
					if (!label.trim().equals(name.trim())) {
						riskInformation.setLabel(name.trim());
						riskInformation.setCustom(true);
					}
				} else {
					riskInformation.setLabel(name);
					riskInformation.setCustom(true);
				}

				if (mapper[0].equals(RI_TYPE_THREAT))
					riskInformation.setAcronym(getString(row.getC().get(colIndex++), sharedStrings));

				String exposed = getString(row, colIndex++, sharedStrings);
				if (StringUtils.isEmpty(exposed))
					riskInformation.setExposed("");
				else if (exposurePattern.matcher(exposed.trim()).matches())
					riskInformation.setExposed(exposed.trim());
				else
					errorInvalidValue(mapper[0], i, colIndex);

				String owner = getString(row, colIndex++, sharedStrings);
				if (StringUtils.isEmpty(owner))
					riskInformation.setOwner("");
				else
					riskInformation.setOwner(owner.trim());

				String comment = getString(row, colIndex++, sharedStrings);

				if (StringUtils.isEmpty(comment))
					riskInformation.setComment("");
				else
					riskInformation.setComment(comment.trim());

				String commentHidden = getString(row, colIndex++, sharedStrings);
				if (StringUtils.isEmpty(commentHidden))
					riskInformation.setHiddenComment("");
				else
					riskInformation.setHiddenComment(commentHidden.trim());

				messageHandler.setProgress(minProgress + ((i / size) * maxProgress));
			}

		}

		serviceTaskFeedback.send(getId(), new MessageHandler("info.save.analysis", "Saving analysis", max));
		riskInformations.values().forEach(riskInformation -> analysis.getRiskInformations().remove(riskInformation));
		daoAnalysis.saveOrUpdate(analysis);
		serviceTaskFeedback.send(getId(), new MessageHandler("info.delete.removed.entry", "Delete removed entries", max + 3));
		daoRiskInformation.delete(riskInformations.values());
	}

	private void errorInvalidValue(String sheet, int i, int colIndex) {
		String colValue = numToColString(colIndex - 1), rowValue = (i + 1) + "";
		throw new TrickException("error.import.risk.information.cell", String.format("Invalid value in sheet: %s, row: %s, column: %s", sheet, rowValue, colValue), sheet, rowValue,
				colValue);
	}

	private String getOrignalLabel(final Locale locale, RiskInformation information) {
		return getOrignalLabel(locale, information.getChapter(), information.getCategory(), information.getLabel());
	}

	private String getOrignalLabel(final Locale locale, String chapter, String category, String label) {
		switch (category) {
		case RI_TYPE_RISK_TBA:
			return messageSource.getMessage(String.format("label.risk_information.risk_tba.", chapter.replace(".", "_")), null, label, locale);
		case RI_TYPE_RISK_TBS:
			return messageSource.getMessage(String.format("label.risk_information.risk_tbs.", chapter.replace(".", "_")), null, label, locale);
		default:
			return messageSource.getMessage(String.format("label.risk_information.%s.", category.toLowerCase(), chapter.replace(".", "_")), null, label, locale);
		}
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
		String colValue = numToColString(col - 1), rowValue = (row + 1) + "";
		throw new TrickException("error.import.risk.information.cell.empty", String.format("Cell cannot be empty. Sheet: %s, row: %s, column: %s", sheet, rowValue, colValue),
				sheet, rowValue, colValue);
	}

	private void initialiseDAO(Session session) {
		setDaoAnalysis(new DAOAnalysisHBM(session));
		setDaoRiskInformation(new DAORiskInformationHBM(session));
	}

	public File getWorkFile() {
		return workFile;
	}

	public void setWorkFile(File workFile) {
		this.workFile = workFile;
	}

	public DAOAnalysis getDaoAnalysis() {
		return daoAnalysis;
	}

	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	public DAORiskInformation getDaoRiskInformation() {
		return daoRiskInformation;
	}

	public void setDaoRiskInformation(DAORiskInformation daoRiskInformation) {
		this.daoRiskInformation = daoRiskInformation;
	}

	public ServiceTaskFeedback getServiceTaskFeedback() {
		return serviceTaskFeedback;
	}

	public void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback) {
		this.serviceTaskFeedback = serviceTaskFeedback;
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

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
